package org.centralperf.service;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.centralperf.model.RunDetail;
import org.centralperf.model.RunDetailGraphRc;
import org.centralperf.model.RunDetailGraphRt;
import org.centralperf.model.RunDetailGraphSum;
import org.centralperf.model.RunDetailStatistics;
import org.centralperf.model.dao.Run;
import org.centralperf.repository.RunRepository;
import org.centralperf.sampler.api.SamplerRunJob;
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
	
	@Resource
	private ScriptLauncherService scriptLauncherService;
	
	private static final Logger log = LoggerFactory.getLogger(RunStatisticsService.class);
	
	/*
	 * Use of cache is necessary to avoid database saturation if more than one user
	 * is looking UI run detail
	 */
	private CacheLoader<Long, RunDetailStatistics> runDetailStatisticsLoader = new CacheLoader<Long, RunDetailStatistics>() {
	    @Override
	    public RunDetailStatistics load(Long runId) throws Exception {
	    	//Load stats from database
	    	Query q = em.createQuery("SELECT count(*), sum(sizeInOctet), min(timestamp), max(timestamp), sum(elapsed)/count(*), sum(latency)/count(*), max(allThreads),sum(case when assertResult=true then 0 else 1 end) from Sample s where run_fk='"+runId+"'");
	    	
	    	@SuppressWarnings("rawtypes")
			Iterator results =q.getResultList().iterator();

	    	RunDetailStatistics runDetailStatistics=null;
	    	if(results.hasNext()){
	    		runDetailStatistics = new RunDetailStatistics((Object[]) results.next());
	    	}
	    	return runDetailStatistics;
	    }
	};
	//TODO: Nb of second should be a parameter
	private LoadingCache<Long, RunDetailStatistics> runDetailStatisticsCache = CacheBuilder
			.newBuilder()
			.expireAfterWrite(1, TimeUnit.SECONDS) //Data expire 1 second after Bdd access
			.build(runDetailStatisticsLoader);
	
	
	private CacheLoader<Long, RunDetailGraphRt> runDetailGraphRtLoader = new CacheLoader<Long, RunDetailGraphRt>() {
	    @SuppressWarnings("unchecked")
		@Override
	    public RunDetailGraphRt load(Long runId) throws Exception {
	    	//Load stats from database
	    	Query q = em.createQuery("SELECT  sampleName, avg(elapsed), avg(latency), avg(sizeInOctet)  from Sample s where run_fk='"+runId+"'   GROUP BY sampleName");
	    	return new RunDetailGraphRt(q.getResultList().iterator());
	    }
	};
	//TODO: Nb of second should be a parameter
	private LoadingCache<Long, RunDetailGraphRt> runDetailGraphRtCache = CacheBuilder
			.newBuilder()
			.expireAfterWrite(1, TimeUnit.SECONDS) //Data expire 1 second after Bdd access
			.build(runDetailGraphRtLoader);	

	
	private CacheLoader<Run, RunDetailGraphSum> runDetailGraphSumLoader = new CacheLoader<Run, RunDetailGraphSum>() {
	    @SuppressWarnings("unchecked")
		@Override
	    public RunDetailGraphSum load(Run run) throws Exception {
	    	//Load stats from database
	    	Query q = em.createQuery("select to_char(timestamp, 'DD-MM-YYYY HH24:MI:SS'), count(*), avg(elapsed) from Sample s where run_fk='"+run.getId()+"' group by to_char(timestamp, 'DD-MM-YYYY HH24:MI:SS') order by to_char(timestamp, 'DD-MM-YYYY HH24:MI:SS')");
			return new RunDetailGraphSum(q.getResultList().iterator(), run.getStartDate());
	    }
	};
	//TODO: Nb of second should be a parameter
	private LoadingCache<Run, RunDetailGraphSum> runDetailGraphSumCache = CacheBuilder
			.newBuilder()
			.expireAfterWrite(1, TimeUnit.SECONDS) //Data expire 1 second after Bdd access
			.build(runDetailGraphSumLoader);	

	
	private CacheLoader<Long, RunDetailGraphRc> runDetailGraphRcLoader = new CacheLoader<Long, RunDetailGraphRc>() {
	    @SuppressWarnings("unchecked")
		@Override
	    public RunDetailGraphRc load(Long runId) throws Exception {
	    	//Load stats from database
	    	Query q = em.createQuery("SELECT  substring(status,1,1), count(*) from Sample s where run_fk='"+runId+"'   GROUP BY substring(status,1,1)");
	    	return new RunDetailGraphRc(q.getResultList().iterator());
	    }
	};
	//TODO: Nb of second should be a parameter
	private LoadingCache<Long, RunDetailGraphRc> runDetailGraphRcCache = CacheBuilder
			.newBuilder()
			.expireAfterWrite(1, TimeUnit.SECONDS) //Data expire 1 second after Bdd access
			.build(runDetailGraphRcLoader);	
	
	/**
	 * Return statistics of the run from the cache. If cache data are older than XXX seconds,
	 * they are reload from database.
	 * 
	 * @param runId Id of the Run to monitor
	 * @return RunStatistics object
	 */
	public RunDetail getRunDetail(Long runId){
		Run run = runRepository.findOne(runId);
		RunDetail runDetail=null;
		
		if(run!=null){
			runDetail=new RunDetail(run);
			if(run.isRunning()){
				SamplerRunJob runJob = scriptLauncherService.getJob(run.getId());
				if(runJob != null){
					runDetail.setJobOutput(runJob.getProcessOutput());
				}
			}
			try{
				runDetail.setRunDetailStatistics(runDetailStatisticsCache.get(runId));
				runDetail.setRunDetailGraphRt(runDetailGraphRtCache.get(runId));
				runDetail.setRunDetailGraphSum(runDetailGraphSumCache.get(run));
				runDetail.setRunDetailGraphRc(runDetailGraphRcCache.get(runId));
			}
			catch (ExecutionException eE) {log.warn("Someone ask for run statistics for a not existing run ["+runId+"]:"+eE.getMessage(),eE);}
		}
		return runDetail;
	}
}
