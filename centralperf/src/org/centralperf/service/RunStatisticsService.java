package org.centralperf.service;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.centralperf.model.RunDetail;
import org.centralperf.model.RunDetailGraphError;
import org.centralperf.model.RunDetailGraphRc;
import org.centralperf.model.RunDetailGraphRt;
import org.centralperf.model.RunDetailGraphSum;
import org.centralperf.model.RunDetailStatistics;
import org.centralperf.model.dao.Run;
import org.centralperf.repository.RunRepository;
import org.centralperf.sampler.api.SamplerRunJob;
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
	
    @Value("#{appProperties['report.cache.delay.seconds']}")
    private Long cacheRefreshDelay;
	
    //Cache can't be load on object construction as refresh delay is not yet set
    private LoadingCache<Long, RunDetailStatistics> runDetailStatisticsCache;
    private LoadingCache<Long, RunDetailGraphRt> runDetailGraphRtCache;
    private LoadingCache<Run, RunDetailGraphSum> runDetailGraphSumCache;
    private LoadingCache<Long, RunDetailGraphRc> runDetailGraphRcCache;
    private LoadingCache<Long, RunDetailGraphError> runDetailGraphErrorCache;
    
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
	private CacheLoader<Long, RunDetailGraphRt> runDetailGraphRtLoader = new CacheLoader<Long, RunDetailGraphRt>() {
	    @SuppressWarnings("unchecked")
		@Override
	    public RunDetailGraphRt load(Long runId) throws Exception {
	    	//Load stats from database
	    	Query q = em.createQuery("SELECT  sampleName, avg(elapsed), avg(latency), avg(sizeInOctet)  from Sample s where run_fk='"+runId+"'   GROUP BY sampleName order by sampleName");
	    	return new RunDetailGraphRt(q.getResultList().iterator());
	    }
	};	
	private CacheLoader<Run, RunDetailGraphSum> runDetailGraphSumLoader = new CacheLoader<Run, RunDetailGraphSum>() {
	    @SuppressWarnings("unchecked")
		@Override
	    public RunDetailGraphSum load(Run run) throws Exception {
	    	//Load stats from database
	    	Query q = em.createQuery("select to_char(timestamp, 'DD-MM-YYYY HH24:MI:SS'), count(*), avg(elapsed) from Sample s where run_fk='"+run.getId()+"' group by to_char(timestamp, 'DD-MM-YYYY HH24:MI:SS') order by to_char(timestamp, 'DD-MM-YYYY HH24:MI:SS')");
			return new RunDetailGraphSum(q.getResultList().iterator(), run.getStartDate());
	    }
	};
	private CacheLoader<Long, RunDetailGraphRc> runDetailGraphRcLoader = new CacheLoader<Long, RunDetailGraphRc>() {
	    @SuppressWarnings("unchecked")
		@Override
	    public RunDetailGraphRc load(Long runId) throws Exception {
	    	//Load stats from database
	    	Query q = em.createQuery("SELECT  substring(status,1,1), count(*) from Sample s where run_fk='"+runId+"' GROUP BY substring(status,1,1)");
	    	return new RunDetailGraphRc(q.getResultList().iterator());
	    }
	};
	private CacheLoader<Long, RunDetailGraphError> runDetailGraphErrorLoader = new CacheLoader<Long, RunDetailGraphError>() {
	    @SuppressWarnings("unchecked")
		@Override
	    public RunDetailGraphError load(Long runId) throws Exception {
	    	//Load stats from database
	    	
	    	/*SELECT sampleName, count(CASE WHEN assertResult THEN 1 ELSE null END), count(CASE WHEN assertResult THEN null ELSE true END), (count(CASE WHEN assertResult THEN null ELSE true END)/(count(*)*1.00))*100 from Sample s where run_fk='"+run.getId()+"' GROUP BY sampleName;  */
	    	
	    	Query q = em.createQuery("SELECT sampleName, count(CASE WHEN assertResult IS true THEN 1 ELSE null END), count(CASE WHEN assertResult IS TRUE THEN null ELSE true END), (count(CASE WHEN assertResult IS TRUE THEN null ELSE true END)/(count(*)*1.00))*100 from Sample s where run_fk='"+runId+"' GROUP BY sampleName  order by sampleName");
	    	return new RunDetailGraphError(q.getResultList().iterator());
	    }
	};
	
	
	public LoadingCache<Long, RunDetailStatistics> getRunDetailStatisticsCache() {
		CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
		builder.expireAfterWrite(cacheRefreshDelay, TimeUnit.SECONDS);
		return builder.build(runDetailStatisticsLoader);
    }
	public LoadingCache<Long, RunDetailGraphRt> getRunDetailGraphRtCache() {
		CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
		builder.expireAfterWrite(cacheRefreshDelay, TimeUnit.SECONDS);
		return builder.build(runDetailGraphRtLoader);
    }
	public LoadingCache<Run, RunDetailGraphSum> getRunDetailGraphSumCache() {
		CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
		builder.expireAfterWrite(cacheRefreshDelay, TimeUnit.SECONDS);
		return builder.build(runDetailGraphSumLoader);
    }
	public LoadingCache<Long, RunDetailGraphRc> getRunDetailGraphRcCache() {
		CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
		builder.expireAfterWrite(cacheRefreshDelay, TimeUnit.SECONDS);
		return builder.build(runDetailGraphRcLoader);
    }
	public LoadingCache<Long, RunDetailGraphError> getRunDetailGraphErrorCache() {
		CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
		builder.expireAfterWrite(cacheRefreshDelay, TimeUnit.SECONDS);
		return builder.build(runDetailGraphErrorLoader);
    }

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
				if(runDetailStatisticsCache==null){runDetailStatisticsCache=this.getRunDetailStatisticsCache();}
				if(runDetailGraphRtCache==null){runDetailGraphRtCache=this.getRunDetailGraphRtCache();}
				if(runDetailGraphSumCache==null){runDetailGraphSumCache=this.getRunDetailGraphSumCache();}
				if(runDetailGraphRcCache==null){runDetailGraphRcCache=this.getRunDetailGraphRcCache();}
				if(runDetailGraphErrorCache==null){runDetailGraphErrorCache=this.getRunDetailGraphErrorCache();}
				
				runDetail.setRunDetailStatistics(runDetailStatisticsCache.get(runId));
				runDetail.setRunDetailGraphRt(runDetailGraphRtCache.get(runId));
				runDetail.setRunDetailGraphSum(runDetailGraphSumCache.get(run));
				runDetail.setRunDetailGraphRc(runDetailGraphRcCache.get(runId));
				runDetail.setRunDetailGraphError(runDetailGraphErrorCache.get(runId));
			}
			catch (ExecutionException eE) {log.warn("Someone ask for run statistics for a not existing run ["+runId+"]:"+eE.getMessage(),eE);}
		}
		return runDetail;
	}
}
