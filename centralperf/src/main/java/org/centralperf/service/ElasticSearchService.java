package org.centralperf.service;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.centralperf.model.SampleDataBackendTypeEnum;
import org.centralperf.model.dao.Run;
import org.centralperf.model.dao.Sample;
import org.centralperf.model.dto.ESSample;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.bulk.byscroll.BulkByScrollResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Elastic Search interaction service Creates index and Kibana objects if they
 * do not exists
 * 
 * @author Charles Le Gallic (charles@amoae.com)
 * @since 1.1
 */
@Service
public class ElasticSearchService {

	@Value("${sampledata.backend}")
	private SampleDataBackendTypeEnum sampleDataBackendType;

	@Value("#{appProperties['es.kibana.index.name']}")
	private String esKibanaIndexName;

	@Value("#{appProperties['es.centralperf.index.name']}")
	private String esCentralPerfIndexName;

	@Value("#{appProperties['es.cluster.name']}")
	private String esClusterName;

	@Value("#{appProperties['es.cluster.transport.host']}")
	private String esClusterTransportHost;

	@Value("#{appProperties['es.cluster.transport.port']}")
	private Integer esClusterTransportPort;

	@Resource
	private BootstrapServiceFiles bootstrapServiceFiles;

	private TransportClient client;
	private ObjectMapper mapper;

	private static final Logger logger = LoggerFactory.getLogger(BootstrapService.class);

	/**
	 * Init index and Kibana configuration if necessary. Open connections to ES
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings({ "resource", "unchecked" })
	@PostConstruct
	public void init() throws IOException {

		// Only init if storage is set to ES
		if (SampleDataBackendTypeEnum.ES.equals(sampleDataBackendType)) {

			// Set ES Cluster name
			Settings settings = Settings.builder().put("cluster.name", esClusterName).build();

			client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(
					new InetSocketAddress(esClusterTransportHost, esClusterTransportPort)));

			// Check if index already exists
			boolean indexExists = client.admin().indices().prepareExists(esCentralPerfIndexName).execute().actionGet()
					.isExists();

			// Create it otherwise, and identify timestamp field
			if (!indexExists) {

				// Create index
				String mapping = XContentFactory.jsonBuilder().startObject().startObject("sample")
						.startObject("properties").startObject("timestamp").field("type", "date")
						.field("format", "epoch_millis").endObject().endObject().endObject().endObject().string();

				// Map timestamp
				client.admin().indices().prepareCreate(esCentralPerfIndexName)
						.addMapping("sample", mapping, XContentType.JSON).get();

				// Create Kibana objects
				boostrapKibanaObjects();
			}

			// For further json serialization
			mapper = new ObjectMapper();
		}
	}

	/**
	 * Boostrap Kibana visualization and dashboards
	 */
	private void boostrapKibanaObjects() {
		try {
			
			// Central Perf Index Pattern
			client.prepareIndex(esKibanaIndexName, "index-pattern", "centralperf*")
			.setSource(
					FileUtils.readFileToByteArray(
							bootstrapServiceFiles.getKibanaCentralPerfIndexPattern().getFile()),
					XContentType.JSON)
			.get();
			
			// Visualizations
			client.prepareIndex(esKibanaIndexName, "visualization", "centralperf_global_metrics")
					.setSource(
							FileUtils.readFileToByteArray(
									bootstrapServiceFiles.getKibanaVisualizationGlobalMetrics().getFile()),
							XContentType.JSON)
					.get();
			client.prepareIndex(esKibanaIndexName, "visualization", "centralperf_response_time_per_time")
					.setSource(
							FileUtils.readFileToByteArray(
									bootstrapServiceFiles.getKibanaVisualizationResponseTimePerTime().getFile()),
							XContentType.JSON)
					.get();
			client.prepareIndex(esKibanaIndexName, "visualization", "centralperf_response_time_per_sample")
					.setSource(
							FileUtils.readFileToByteArray(
									bootstrapServiceFiles.getKibanaVisualizationResponseTimePerSample().getFile()),
							XContentType.JSON)
					.get();

			// Dashboards
			client.prepareIndex(esKibanaIndexName, "dashboard", "centralperf_overview_dashboard")
					.setSource(
							FileUtils.readFileToByteArray(bootstrapServiceFiles.getKibanaDashboardOverview().getFile()),
							XContentType.JSON)
					.get();
		} catch (IOException e) {
			logger.error("Error on Kibana objects bootstrap import: " + e.getMessage(), e);
		}
	}

	/**
	 * Remove all documents linked to the run
	 * 
	 * @param runId
	 * @return
	 */
	public long deleteRun(Run run) {
		BulkByScrollResponse response = DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
				.filter(QueryBuilders.matchQuery("runId", run.getId())).source(esCentralPerfIndexName).get();

		return response.getDeleted();
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
			client.prepareIndex(esCentralPerfIndexName, "sample")
					.setSource(mapper.writeValueAsBytes(essample), XContentType.JSON).get();
		} catch (Exception e) {
			logger.error("Error while trying to insert sample data to ES", e);
			// FIXME : store samples in memory until it's possible to query ES ?
		}
		return sample;
	}
}
