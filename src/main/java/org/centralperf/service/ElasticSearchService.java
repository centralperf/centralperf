package org.centralperf.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.centralperf.model.SampleDataBackendTypeEnum;
import org.centralperf.model.dao.Run;
import org.centralperf.model.dao.Sample;
import org.centralperf.model.dto.ESSample;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.*;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Elastic Search interaction service Creates index and Kibana objects if they
 * do not exists
 * 
 * @author Charles Le Gallic (charles@amoae.com)
 * @since 1.1
 */
@Service
public class ElasticSearchService {

	@Value("${centralperf.backend}")
	private SampleDataBackendTypeEnum sampleDataBackendType;

	@Value("${centralperf.elastic.centralperf-index-name}")
	private String esCentralPerfIndexName;

	@Value("${spring.elasticsearch.rest.uris}")
	private String[] esClusterUris;

	@Value("${centralperf.elastic.kibana.internal.url}")
	private String kibanaUrl;

	@Value("${centralperf.elastic.kibana.internal.base-path}")
	private String kibanaBasePath;

	@Value("classpath:kibana/dashboards/*")
	private org.springframework.core.io.Resource[] kibanaDashboardsFiles;

	@Value("classpath:kibana/visualizations/*")
	private org.springframework.core.io.Resource[] kibanaVisualizationsFiles;

	@Value("classpath:kibana/index-patterns/*")
	private org.springframework.core.io.Resource[] kibanaIndexPatternsFiles;

	private RestHighLevelClient esClient;
	private RestClient kibanaClient;
	private RequestOptions kibanaClientRequestOptions;
	private ObjectMapper mapper;

	private static final Logger logger = LoggerFactory.getLogger(BootstrapService.class);

	/**
	 * Init index and Kibana configuration if necessary. Open connections to ES
	 * 
	 * @throws IOException
	 */
	@PostConstruct
	public void init() throws IOException {

		// Only init if storage is set to ES
		if (SampleDataBackendTypeEnum.ES.equals(sampleDataBackendType)) {

			// Init ES Client
			List<HttpHost> esHosts = new ArrayList<>();
			for(String uri:esClusterUris) {
				esHosts.add(HttpHost.create(uri));
			}
			esClient = new RestHighLevelClient(RestClient.builder(esHosts.toArray(new HttpHost[0])));

			// Init Kibana client
			kibanaClient = RestClient.builder(HttpHost.create(kibanaUrl)).setPathPrefix(kibanaBasePath).build();
			RequestOptions.Builder optionsBuilder = RequestOptions.DEFAULT.toBuilder();
			optionsBuilder.addHeader("kbn-xsrf", "true");
			kibanaClientRequestOptions = optionsBuilder.build();

			// Check if index already exists
			GetIndexRequest existsRequest = new GetIndexRequest(esCentralPerfIndexName);
			boolean indexExists = esClient.indices().exists(existsRequest, RequestOptions.DEFAULT);

			// Create it otherwise, and identify timestamp field
			if (!indexExists) {

				// Create index & Map timestamp
				CreateIndexRequest createRequest = new CreateIndexRequest(esCentralPerfIndexName);
				XContentBuilder builder = XContentFactory.jsonBuilder();
				builder.startObject();
				{
					builder.startObject("properties");
					{
						builder.startObject("timestamp");
						{
							builder.field("type", "date");
							builder.field("format", "epoch_millis");
						}
						builder.endObject();
					}
					builder.endObject();
				}
				builder.endObject();
				createRequest.mapping(builder);
				esClient.indices().create(createRequest, RequestOptions.DEFAULT);
			}

			// Create Kibana objects
			boostrapKibanaObjects();

			// For further json serialization
			mapper = new ObjectMapper();
		}
	}

	@PreDestroy
	private void onDestroy() throws IOException {
		esClient.close();
		kibanaClient.close();
	}

	/**
	 * Boostrap Kibana visualization and dashboards
	 */
	private void boostrapKibanaObjects() {
		try {

			// Central Perf Index Pattern
			for (org.springframework.core.io.Resource f : kibanaIndexPatternsFiles) {
				this.bootstrapKibanaObject("index-pattern", f);
			}

			// Vizualisations
			for (org.springframework.core.io.Resource f : kibanaVisualizationsFiles) {
				this.bootstrapKibanaObject("visualization", f);
			}

			// Dashboards
			for (org.springframework.core.io.Resource f : kibanaDashboardsFiles) {
				this.bootstrapKibanaObject("dashboard", f);
			}

		} catch (IOException e) {
			logger.error("Error on Kibana objects bootstrap import: " + e.getMessage(), e);
		}
	}

	private void bootstrapKibanaObject(String objectType, org.springframework.core.io.Resource savedObjet) throws IOException {
		String objectId = "centralperf_" + FilenameUtils.removeExtension(savedObjet.getFilename());

		// Check if object already exists
		Request existsRequest = new Request("GET", String.format("/api/saved_objects/%s/%s", objectType, objectId));
		existsRequest.setOptions(kibanaClientRequestOptions);
		try {
			kibanaClient.performRequest(existsRequest); // throws 404 if not found
			logger.debug("The '{}' saved object already exists, skip it", objectId);
		} catch (ResponseException responseException) {
			String content = getResourceAsString(savedObjet);
			Request request = new Request("POST", String.format("/api/saved_objects/%s/%s", objectType, objectId));
			request.setJsonEntity(content);
			request.setOptions(kibanaClientRequestOptions);
			kibanaClient.performRequest(request);
			logger.debug("Saved object of type '{}' imported in Kibana with id '{}'", objectType, objectId);
		}
	}

	private String getResourceAsString(org.springframework.core.io.Resource resource) {
		try {
			return IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8.name());
		} catch (IOException e) {
			logger.error(String.format("Unable to load bootstrap file %s", resource.getFilename()), e);
			return null;
		}
	}

	/**
	 * Remove all documents linked to the run
	 *
	 * @param run Run to reset
	 * @return Number of sample deleted or -1 if unable to delete
	 */
	public long deleteRun(Run run) {
		MatchQueryBuilder matchRunIdQuery = QueryBuilders.matchQuery("runId", run.getId());
		DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(esCentralPerfIndexName);
		deleteByQueryRequest.setQuery(matchRunIdQuery);
		BulkByScrollResponse response = null;
		try {
			response = esClient.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
			return response.getTotal();
		} catch (IOException e) {
			logger.warn("Communication error with ElasticSearch, unable to delete Run samples", e);
			return -1;
		}
	}

	/**
	 * Send sample to the Central Perf index
	 * 
	 * @param sample
	 * @return
	 */
	public Sample insertSample(Sample sample) {
		ESSample essample = new ESSample(sample);
		try {
			IndexRequest request = new IndexRequest(esCentralPerfIndexName)
					.type("_doc")
					.source(mapper.writeValueAsBytes(essample), XContentType.JSON);
			esClient.index(request, RequestOptions.DEFAULT);
		} catch (Exception e) {
			logger.error("Error while trying to insert sample data to ES", e);
			// FIXME : store samples in memory until it's possible to query ES ?
		}
		return sample;
	}
}
