package org.centralperf.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.centralperf.model.graphs.HttpCodeRepartition;
import org.centralperf.model.graphs.RespTimeSeries;
import org.centralperf.model.graphs.SumSeries;
import org.centralperf.model.Run;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Manage operations on scripts
 * @author Charles Le Gallic
 *
 */
@Service
public class GraphService {

	@PersistenceContext
	private EntityManager em;
	
	private static final Logger log = LoggerFactory.getLogger(GraphService.class);
	
	@SuppressWarnings("rawtypes")
	public SumSeries getSumSeries(Run run){
		Query q = em.createQuery("select to_char(timestamp, 'DD-MM-YYYY HH24:MI:SS'), count(*), avg(elapsed) from Sample s where run_fk='"+run.getId()+"' group by to_char(timestamp, 'DD-MM-YYYY HH24:MI:SS') order by to_char(timestamp, 'DD-MM-YYYY HH24:MI:SS')");
		
		SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
    	TimeZone tz = Calendar.getInstance().getTimeZone();
    	int offsetFromUTC = tz.getOffset(run.getStartDate().getTime());
    	
		StringBuilder rstSerie = new StringBuilder();
		StringBuilder reqSerie = new StringBuilder();
		double rstMin=-1;
		double rstMax=-1;
		double rstTot=0;
		int reqMin=-1;
		int reqMax=-1;
		long reqTot=0;
		
		Iterator results =q.getResultList().iterator();
		long count=0;
		Date dt=null;
		Object[] row =null;
		
		while ( results.hasNext() ) {
			row = (Object[]) results.next();
			try {
				dt = fmt.parse((String)row[0]);
				
				long timestamp = dt.getTime()+offsetFromUTC;
				double rst = Double.parseDouble(row[2].toString());
				int    req = Integer.parseInt(row[1].toString());
				count++;
				
				if(rstMin<0 || rstMin>rst){rstMin=rst;}
				if(rstMax<rst){rstMax=rst;}
				rstTot+=rst;
				rstSerie.append("["+timestamp+","+rst+"]");
				
				if(reqMin<0 || reqMin>req){reqMin=req;}
				if(reqMax<req){reqMax=req;}
				reqTot+=req;
				reqSerie.append("["+timestamp+","+req+"]");
				
				if(results.hasNext()){rstSerie.append(','); reqSerie.append(',');}
			} 
			catch (ParseException pE) {log.error("Error in date convertion",pE);}
			catch (NullPointerException npE) {log.error("Missing data");}
		}
		
		return new SumSeries(rstSerie.toString(),reqSerie.toString(),rstMin,rstMax,count > 0 ? rstTot/count : 0,reqMin,reqMax,count > 0 ? reqTot/count : 0);
	}
	
	@SuppressWarnings("rawtypes")
	public HttpCodeRepartition getCodeRepartition(Run run){
		
		Query q = em.createQuery("SELECT  substring(status,1,1), count(*) from Sample s where run_fk='"+run.getId()+"'   GROUP BY substring(status,1,1)");

		
		Iterator results =q.getResultList().iterator();
		Object[] row =null;
		
		HttpCodeRepartition hcr = new HttpCodeRepartition();
		while ( results.hasNext() ) {
			row = (Object[]) results.next();
			String codeValue = row[0] != null ? row[0].toString() : null;
			String codeValueCount = row[1] != null ? row[1].toString() : null;
			if(codeValue != null && codeValueCount != null)
				hcr.setNbHttpXXX(codeValue, Long.parseLong(codeValueCount));
		}

		return hcr;
	}
    
	@SuppressWarnings("rawtypes")
	public RespTimeSeries getRespTimeSeries(Run run){
		//SELECT AVG(ELAPSED), AVG(LATENCY) FROM SAMPLE WHERE RUN_FK=1 GROUP BY SAMPLENAME
		Query q = em.createQuery("SELECT  sampleName, avg(elapsed), avg(latency), avg(sizeInOctet)  from Sample s where run_fk='"+run.getId()+"'   GROUP BY sampleName");
		
		Iterator results =q.getResultList().iterator();
		StringBuilder labelSerie =   new StringBuilder("[");
		StringBuilder downloadSerie = new StringBuilder("[");
		StringBuilder latencySerie = new StringBuilder("[");
		StringBuilder sizeSerie = new StringBuilder("[");
		
		Object[] row =null;
		Double latency;
		Double download;
		
		while ( results.hasNext() ) {
			if(row!=null){labelSerie.append(",");downloadSerie.append(",");latencySerie.append(",");sizeSerie.append(",");}
			row = (Object[]) results.next();
			if(row[0] != null){
				labelSerie.append("'").append(row[0].toString()).append("'");
				
				latency=Double.parseDouble(row[2].toString());
				download=Double.parseDouble(row[1].toString())-latency;			
				latencySerie.append(latency.longValue());
				downloadSerie.append(download.longValue());
				sizeSerie.append(new Double(Double.parseDouble(row[3].toString())).longValue());
			}
		}
		labelSerie.append("]");
		downloadSerie.append("]");
		latencySerie.append("]");
		sizeSerie.append("]");
		return new RespTimeSeries(labelSerie.toString(), latencySerie.toString(), downloadSerie.toString(),sizeSerie.toString());
	}
	
	
}
