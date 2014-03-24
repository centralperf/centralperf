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

package org.centralperf.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean to store all data to be able to display the "Summary" graph in views
 */
public class RunDetailGraphSum implements Serializable{

	private static final long serialVersionUID = 6176203262861538816L;
	private static final Logger log = LoggerFactory.getLogger(RunDetailGraphSum.class);

	private String rstSerie;
	private String reqSerie;
	private double rstMin=-1;
	private double rstMax=-1;
	private double rstAvg=-1;
	private int    reqMin=-1;
	private int    reqMax=-1;
	private double reqAvg=-1;
	
	public RunDetailGraphSum(Iterator<Object[]> datas, Date runStartDate) {
		SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
    	TimeZone tz = Calendar.getInstance().getTimeZone();
    	int offsetFromUTC = tz.getOffset(runStartDate.getTime());
    	
		StringBuilder rstSerieSB = new StringBuilder("[");
		StringBuilder reqSerieSB = new StringBuilder("[");
		double rstTot=0;
		long reqTot=0;
		
		long count=0;
		Date dt=null;
		Object[] row =null;
		
		while ( datas.hasNext() ) {
			row = (Object[]) datas.next();
			try {
				dt = fmt.parse((String)row[0]);
				
				long timestamp = dt.getTime()+offsetFromUTC;
				double rst = Double.parseDouble(row[2].toString());
				int    req = Integer.parseInt(row[1].toString());
				count++;
				
				if(rstMin<0 || rstMin>rst){rstMin=rst;}
				if(rstMax<rst){rstMax=rst;}
				rstTot+=rst;
				rstSerieSB.append("["+timestamp+","+rst+"]");
				
				if(reqMin<0 || reqMin>req){reqMin=req;}
				if(reqMax<req){reqMax=req;}
				reqTot+=req;
				reqSerieSB.append("["+timestamp+","+req+"]");
				
				if(datas.hasNext()){rstSerieSB.append(','); reqSerieSB.append(',');}
			} 
			catch (ParseException pE) {log.error("Error in date convertion",pE);}
			catch (NullPointerException npE) {log.error("Missing data");}
		}
		rstSerieSB.append("]");
		reqSerieSB.append("]");
		this.rstSerie = rstSerieSB.toString();
		this.reqSerie = reqSerieSB.toString();
		this.rstAvg = count > 0 ? rstTot/count : 0;
		this.reqAvg = count > 0 ? reqTot/count : 0;
	}
	
	public String getRstSerie() {
		return rstSerie;
	}
	public String getReqSerie() {
		return reqSerie;
	}
	public double getRstMin() {
		return rstMin;
	}
	public double getRstMax() {
		return rstMax;
	}
	public double getRstAvg() {
		return rstAvg;
	}
	public int getReqMin() {
		return reqMin;
	}
	public int getReqMax() {
		return reqMax;
	}
	public double getReqAvg() {
		return reqAvg;
	}
	@Override
	public String toString() {
		return "[RunDetailGraphSum]\n\tResponse time: Min("+rstMin+"), Max("+rstMax+"), Avg("+rstAvg+")\tRequest/s: Min("+reqMin+"), Max("+reqMax+"), Avg("+reqAvg+")\n\tResponse Time Serie:"+rstSerie+"\n\tRequest/s Serie:"+reqSerie ;
	}
}
