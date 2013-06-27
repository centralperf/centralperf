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
	public void setRstSerie(String rstSerie) {
		this.rstSerie = rstSerie;
	}
	public String getReqSerie() {
		return reqSerie;
	}
	public void setReqSerie(String reqSerie) {
		this.reqSerie = reqSerie;
	}
	public double getRstMin() {
		return rstMin;
	}
	public void setRstMin(double rstMin) {
		this.rstMin = rstMin;
	}
	public double getRstMax() {
		return rstMax;
	}
	public void setRstMax(double rstMax) {
		this.rstMax = rstMax;
	}
	public double getRstAvg() {
		return rstAvg;
	}
	public void setRstAvg(double rstAvg) {
		this.rstAvg = rstAvg;
	}
	public int getReqMin() {
		return reqMin;
	}
	public void setReqMin(int reqMin) {
		this.reqMin = reqMin;
	}
	public int getReqMax() {
		return reqMax;
	}
	public void setReqMax(int reqMax) {
		this.reqMax = reqMax;
	}
	public double getReqAvg() {
		return reqAvg;
	}
	public void setReqAvg(double reqAvg) {
		this.reqAvg = reqAvg;
	}
	
	@Override
	public String toString() {
		return "[SumSeries]\n\tResponse time: Min("+rstMin+"), Max("+rstMax+"), Avg("+rstAvg+")\tRequest/s: Min("+reqMin+"), Max("+reqMax+"), Avg("+reqAvg+")\n\tResponse Time Serie:"+rstSerie+"\n\tRequest/s Serie:"+reqSerie ;
	}
}
