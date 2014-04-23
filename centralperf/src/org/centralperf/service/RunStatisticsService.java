/*
 * Copyright (C) 2014  The Central Perf authors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.centralperf.service;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.centralperf.model.dao.Run;
import org.centralperf.model.graph.ErrorRateGraph;
import org.centralperf.model.graph.ResponseSizeGraph;
import org.centralperf.model.graph.ResponseTimeGraph;
import org.centralperf.model.graph.RunStats;
import org.centralperf.model.graph.SummaryGraph;
import org.centralperf.repository.RunRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * This class is singleton service to collect run data and graphs.<br>
 * A cache mechanism with time eviction is used to avoid database overload if too many refresh
 * are asked by users.
 * @since 1.0
 */

@Service
public class RunStatisticsService {

	@Resource
	private RunRepository runRepository;
	
	@PersistenceContext
	private EntityManager em;
	
	@Resource
	private ScriptLauncherService scriptLauncherService;
	
    @Value("#{appProperties['report.cache.delay.seconds']}")
    private Long cacheRefreshDelay;
    
    @Value("#{appProperties['report.scaling.level1.seconds']}")
    private Long limitOfFisrtScaling;
    
    @Value("#{appProperties['report.scaling.level2.seconds']}")
    private Long limitOfSecondScaling;
	
    //Cache can't be load on object construction as refresh delay is not yet set
    private LoadingCache<Long, SummaryGraph> summaryGraphCache;
    private LoadingCache<Long, ResponseTimeGraph> responseTimeGraphCache;
    private LoadingCache<Long, ResponseSizeGraph> responseSizeGraphCache;
    private LoadingCache<Long, ErrorRateGraph> errorRateGraphCache;
    private LoadingCache<Long, RunStats> runStatsCache;
    
	private static final Logger log = LoggerFactory.getLogger(RunStatisticsService.class);
	
	/*
	 * Use of cache is necessary to avoid database saturation if more than one user
	 * is looking UI run detail
	 */	
	private CacheLoader<Long, SummaryGraph> summaryGraphLoader = new CacheLoader<Long, SummaryGraph>() {
	    @SuppressWarnings("unchecked")
		@Override
	    public SummaryGraph load(Long runId) throws Exception {
	    	//Load stats from database
	    	log.debug("Loading SummaryGraph datas from database (not in cache)");
	    	Query q = em.createQuery("select to_char(timestamp, 'DD-MM-YYYY HH24:MI:SS') from Sample s where run_fk='"+runId+"' group by to_char(timestamp, 'DD-MM-YYYY HH24:MI:SS')");
	    	int nbSeconds=q.getResultList().size();
	    	log.debug("Check for scaling [seconds:"+nbSeconds+" - Fisrt scaling after: "+limitOfFisrtScaling+" s - second scaling after: "+limitOfSecondScaling+" s");
	    	if(nbSeconds<limitOfFisrtScaling){q = em.createQuery("select to_char(timestamp, 'DD-MM-YYYY HH24:MI:SS'), round(avg(elapsed),0), count(*) from Sample s where run_fk='"+runId+"' group by to_char(timestamp, 'DD-MM-YYYY HH24:MI:SS') order by to_char(timestamp, 'DD-MM-YYYY HH24:MI:SS')");}
	    	else if(nbSeconds < limitOfSecondScaling) {q = em.createQuery("select concat(substring(to_char(timestamp, 'DD-MM-YYYY HH24:MI:SS'),0,19),'0') , round(avg(elapsed),0), count(*), substring(to_char(timestamp, 'DD-MM-YYYY HH24:MI:SS'),0,19) from Sample s where run_fk='"+runId+"'group by substring(to_char(timestamp, 'DD-MM-YYYY HH24:MI:SS'),0,19) order by substring(to_char(timestamp, 'DD-MM-YYYY HH24:MI:SS'),0,19)");}
	    	else {q = em.createQuery("select to_char(timestamp, 'DD-MM-YYYY HH24:MI:00'), round(avg(elapsed),0), count(*) from Sample s where run_fk='"+runId+"' group by to_char(timestamp, 'DD-MM-YYYY HH24:MI:00') order by to_char(timestamp, 'DD-MM-YYYY HH24:MI:00')");}
	    	
	    	Run run = runRepository.findOne(runId);
	    	return new SummaryGraph(q.getResultList().iterator(), run.getStartDate());
	    }
	};
	private CacheLoader<Long, ResponseTimeGraph> responseTimeGraphLoader = new CacheLoader<Long, ResponseTimeGraph>() {
	    @SuppressWarnings("unchecked")
		@Override
	    public ResponseTimeGraph load(Long runId) throws Exception {
	    	//Load stats from database
	    	log.debug("Loading ResponseTimeGraph datas from database (not in cache)");
	    	Query q = em.createQuery("SELECT  sampleName, round(avg(elapsed),0), round(avg(latency),0) from Sample s where run_fk='"+runId+"'   GROUP BY sampleName order by sampleName");
	    	return new ResponseTimeGraph(q.getResultList().iterator());
	    }
	};
	private CacheLoader<Long, ResponseSizeGraph> responseSizeGraphLoader = new CacheLoader<Long, ResponseSizeGraph>() {
	    @SuppressWarnings("unchecked")
		@Override
	    public ResponseSizeGraph load(Long runId) throws Exception {
	    	//Load stats from database
	    	log.debug("Loading ResponseSizeGraph datas from database (not in cache)");
	    	Query q = em.createQuery("SELECT  sampleName, round(avg(sizeInOctet),0)  from Sample s where run_fk='"+runId+"'   GROUP BY sampleName order by sampleName");
	    	return new ResponseSizeGraph(q.getResultList().iterator());
	    }
	};
	private CacheLoader<Long, ErrorRateGraph> errorRateGraphLoader = new CacheLoader<Long, ErrorRateGraph>() {
	    @SuppressWarnings("unchecked")
		@Override
	    public ErrorRateGraph load(Long runId) throws Exception {
	    	//Load stats from database
	    	log.debug("Loading ErrorRateGraph datas from database (not in cache)");
	    	Query q = em.createQuery("SELECT sampleName, count(CASE WHEN assertResult IS true THEN 1 ELSE null END), count(CASE WHEN assertResult IS TRUE THEN null ELSE true END) from Sample s where run_fk='"+runId+"' GROUP BY sampleName  order by sampleName");
	    	return new ErrorRateGraph(q.getResultList().iterator());
	    }
	};
	private CacheLoader<Long, RunStats> runStatsLoader = new CacheLoader<Long, RunStats>() {
	    @Override
	    public RunStats load(Long runId) throws Exception {
	    	//Load stats from database
	    	log.debug("Loading RunStat datas from database (not in cache)");
	    	Query q = em.createQuery("SELECT count(*), sum(sizeInOctet), min(timestamp), max(timestamp), sum(elapsed)/count(*), sum(latency)/count(*), max(allThreads),sum(case when assertResult=true then 0 else 1 end) from Sample s where run_fk='"+runId+"'");
	    	@SuppressWarnings("rawtypes")
			Iterator results =q.getResultList().iterator();
	    	RunStats runStats=null;
	    	String runOutput = null;
	    	boolean running = false;
	    	Run run = runRepository.findOne(runId);
	    	if(run!=null){runOutput=run.getProcessOutput(); running=run.isRunning();}
	    	if(results.hasNext()){runStats = new RunStats((Object[]) results.next(), runOutput,running);}
	    	return runStats;
	    }
	};

	private LoadingCache<Long, SummaryGraph> getSummaryGraphCache() {
		CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
		builder.expireAfterWrite(cacheRefreshDelay, TimeUnit.SECONDS);
		return builder.build(summaryGraphLoader);
    }
	private LoadingCache<Long, ResponseTimeGraph> getResponseTimeGraphCache() {
		CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
		builder.expireAfterWrite(cacheRefreshDelay, TimeUnit.SECONDS);
		return builder.build(responseTimeGraphLoader);
    }
	private LoadingCache<Long, ResponseSizeGraph> getResponseSizeGraphCache() {
		CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
		builder.expireAfterWrite(cacheRefreshDelay, TimeUnit.SECONDS);
		return builder.build(responseSizeGraphLoader);
    }
	private LoadingCache<Long, ErrorRateGraph> getErrorRateGraphCache() {
		CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
		builder.expireAfterWrite(cacheRefreshDelay, TimeUnit.SECONDS);
		return builder.build(errorRateGraphLoader);
    }
	public LoadingCache<Long, RunStats> getRunStatsCache() {
		CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
		builder.expireAfterWrite(cacheRefreshDelay, TimeUnit.SECONDS);
		return builder.build(runStatsLoader);
    }
	
	/**
	 * Get summary graph data as CSV String
	 * @param runId id of the run
	 * @return String with csv of summary graph
	 * @throws ExecutionException On cache read error
	 */
	public String getSummaryGraph(Long runId) throws ExecutionException{
		if(summaryGraphCache==null){summaryGraphCache=this.getSummaryGraphCache();}
		return this.summaryGraphCache.get(runId).toString();
	}
	
	/**
	 * Get responseTime graph data as CSV String
	 * @param runId id of the run
	 * @return String with csv of response time graph
	 * @throws ExecutionException On cache read error
	 */	
	public String getResponseTimeGraph(Long runId) throws ExecutionException{
		if(responseTimeGraphCache==null){responseTimeGraphCache=this.getResponseTimeGraphCache();}
		return this.responseTimeGraphCache.get(runId).toString();
	}
	
	/**
	 * Get response size graph data as CSV String
	 * @param runId id of the run
	 * @return String with csv of response size graph
	 * @throws ExecutionException On cache read error
	 */
	public String getResponseSizeGraph(Long runId) throws ExecutionException{
		if(responseSizeGraphCache==null){responseSizeGraphCache=this.getResponseSizeGraphCache();}
		return this.responseSizeGraphCache.get(runId).toString();
	}
	
	/**
	 * Get error rate graph data as CSV String
	 * @param runId id of the run
	 * @return String with csv of error rate graph
	 * @throws ExecutionException On cache read error
	 */
	public String getErrorRateGraph(Long runId) throws ExecutionException{
		if(errorRateGraphCache==null){errorRateGraphCache=this.getErrorRateGraphCache();}
		return this.errorRateGraphCache.get(runId).toString();
	}
	
	/**
	 * Get run statistics data as RunStats to JSon conversion
	 * @param runId id of the run
	 * @return Runstats object with run statistics datas
	 * @throws ExecutionException On cache read error
	 */
	public RunStats getRunStats(Long runId) throws ExecutionException{
		if(runStatsCache==null){runStatsCache=this.getRunStatsCache();}
		return this.runStatsCache.get(runId);
	}
}
