package org.centralperf.service;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.centralperf.model.RunStatistics;
import org.centralperf.repository.RunRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * This class is singleton service to collect run data and graphs.<br>
 * A cache mechanism with time eviction is used to avoid database overload if too many refresh
 * are asked by users.
 * 
 * @author dclairac
 *
 */

@Service
public class RunStatisticsService {

	@Resource
	private RunRepository runRepository;
	
	@PersistenceContext
	private EntityManager em;
	
	private static final Logger log = LoggerFactory.getLogger(RunStatisticsService.class);
	
	/*
	 * Use of cache is necessary to avoid database saturation if more than one user
	 * is looking UI run detail
	 */
	private CacheLoader<Long, RunStatistics> runStatLoader = new CacheLoader<Long, RunStatistics>() {
	    @Override
	    public RunStatistics load(Long runId) throws Exception {
	    	//Load stats from database
	    	Query q = em.createQuery("SELECT count(*), sum(sizeInOctet), min(timestamp), max(timestamp), sum(elapsed)/count(*), sum(latency)/count(*), max(allThreads),sum(case when assertResult=true then 0 else 1 end) from Sample s where run_fk='"+runId+"'");
	    	
	    	@SuppressWarnings("rawtypes")
			Iterator results =q.getResultList().iterator();

	    	RunStatistics runStat=null;
	    	if(results.hasNext()){
	    		runStat = new RunStatistics((Object[]) results.next());
	    	}
	    	return runStat;
	    }
	};
	
	//TODO: Nb of second should be a parameter
	private LoadingCache<Long, RunStatistics> runStatCache = CacheBuilder
			.newBuilder()
			.expireAfterWrite(1, TimeUnit.SECONDS) //Data expire 1 second after Bdd access
			.build(runStatLoader);
	
	
	/**
	 * Return statistics of the run from the cache. If cache data are older than XXX seconds,
	 * they are reload from database.
	 * 
	 * @param runId Id of the Run to monitor
	 * @return RunStatistics object
	 */
	public RunStatistics getRunStatistics(Long runId){
		RunStatistics runStat=null;
		try{runStat = runStatCache.get(runId);}
		catch (ExecutionException eE) {log.warn("Someone ask for run statistics for a not existing run ["+runId+"]:"+eE.getMessage(),eE);}
		return runStat;
	}
}
