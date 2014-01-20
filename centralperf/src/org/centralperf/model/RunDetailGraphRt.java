package org.centralperf.model;

import java.io.Serializable;
import java.util.Iterator;

public class RunDetailGraphRt implements Serializable{

	private static final long serialVersionUID = 6913418750734276967L;

	private String label;
	private String latency;
	private String download;
	private String size;

	public RunDetailGraphRt(Iterator<Object[]> datas) {
		StringBuilder labelSerie =   new StringBuilder("[");
		StringBuilder downloadSerie = new StringBuilder("[");
		StringBuilder latencySerie = new StringBuilder("[");
		StringBuilder sizeSerie = new StringBuilder("[");
		
		Object[] row =null;
		Double latency;
		Double download;
		
		while ( datas.hasNext() ) {
			if(row!=null){labelSerie.append(",");downloadSerie.append(",");latencySerie.append(",");sizeSerie.append(",");}
			row = (Object[]) datas.next();
			if(row[0] != null){
				labelSerie.append("\"").append(row[0].toString()).append("\"");
				
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
		
		this.label = labelSerie.toString();
		this.latency = latencySerie.toString();
		this.download = downloadSerie.toString();
		this.size = sizeSerie.toString();
	}
	
	public String getLabel() {return label;}
	public void setLabel(String label) {this.label = label;}
	public String getLatency() {return latency;}
	public void setLatency(String latency) {this.latency = latency;}
	public String getDownload() {return download;}
	public void setDownload(String download) {this.download = download;}
	public String getSize() {return size;}
	public void setSize(String size) {this.size = size;}
	
	@Override
	public String toString() {
		return "[ResponseTimeSeries]\n\tLabels:"+label+"\n\tLatency:"+latency+"\n\tDownload:"+download+"\n\tsize:"+size;
	}
}
