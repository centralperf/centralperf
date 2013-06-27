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

import org.centralperf.graph.model.SumSeries;
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
		Query q = em.createQuery("select to_char(timestamp, 'DD-MM-YYYY HH24:MI:SS'), count(*), avg(elapsed) from Sample s where run_fk='"+run.getId()+"' group by to_char(timestamp, 'DD-MM-YYYY HH24:MI:SS')");
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
			} catch (ParseException pE) {log.error("Error in date convertion",pE);}
		}
		log.debug("TOT: "+reqTot+" - COUNT: "+count+"  => "+(reqTot/count) );
		return new SumSeries(rstSerie.toString(),reqSerie.toString(),rstMin,rstMax,rstTot/count,reqMin,reqMax,reqTot/count);
	}
	
}
