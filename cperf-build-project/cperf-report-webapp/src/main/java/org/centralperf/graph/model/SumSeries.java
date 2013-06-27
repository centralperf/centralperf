package org.centralperf.graph.model;

import java.io.Serializable;

public class SumSeries implements Serializable{

	private static final long serialVersionUID = 6176203262861538816L;

	private String rstSerie;
	private String reqSerie;
	private double rstMin;
	private double rstMax;
	private double rstAvg;
	private int    reqMin;
	private int    reqMax;
	private double    reqAvg;
	
	public SumSeries(String rstSerie, String reqSerie, double rstMin, double rstMax, double rstAvg, int reqMin, int reqMax, double reqAvg) {
		this.rstSerie = rstSerie;
		this.reqSerie = reqSerie;
		this.rstMin = rstMin;
		this.rstMax = rstMax;
		this.rstAvg = rstAvg;
		this.reqMin = reqMin;
		this.reqMax = reqMax;
		this.reqAvg = reqAvg;
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
		return "[SumSeries]\n\tResponse time: Min("+rstMin+"), Max("+rstMax+"), Avg("+rstAvg+")\tRequest/s: Min("+reqMin+"), Max("+reqMax+"), Avg("+reqAvg+")\n\tResponse Time Serie:"+rstSerie+"\n\tRequest/s Serie:"+reqSerie ;
	}
}
