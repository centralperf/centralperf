package org.centralperf.model.graphs;

import java.io.Serializable;

public class RespTimeSeries implements Serializable{

	private static final long serialVersionUID = 6913418750734276967L;

	private String label;
	private String latency;
	private String download;

	public RespTimeSeries(String label, String latency, String download) {
		this.label = label;
		this.latency = latency;
		this.download = download;
	}
	
	public String getLabel() {return label;}
	public void setLabel(String label) {this.label = label;}
	public String getLatency() {return latency;}
	public void setLatency(String latency) {this.latency = latency;}
	public String getDownload() {return download;}
	public void setDownload(String download) {this.download = download;}

	@Override
	public String toString() {
		return "[ResponseTimeSeries]\n\tLabels:"+label+"\n\tLatency:"+latency+"\n\tDownload:"+download;
	}
}
