package org.centralperf.model;

import java.io.Serializable;
import java.util.Iterator;

public class RunDetailGraphError implements Serializable{

	private static final long serialVersionUID = 2412018415877293827L;

	private String label;
	private String nbOk;
	private String nbKo;
	private String koRate;

	public RunDetailGraphError(Iterator<Object[]> datas) {
		StringBuilder labelSerie =   new StringBuilder("[");
		StringBuilder nbOkSerie = new StringBuilder("[");
		StringBuilder nbKoSerie = new StringBuilder("[");
		StringBuilder koRateSerie = new StringBuilder("[");
		
		Object[] row =null;
		
		while ( datas.hasNext() ) {
			if(row!=null){labelSerie.append(",");nbOkSerie.append(",");nbKoSerie.append(",");koRateSerie.append(",");}
			row = (Object[]) datas.next();
			if(row[0] != null){
				labelSerie.append("\"").append(row[0].toString()).append("\"");
				nbOkSerie.append(Integer.parseInt(row[1].toString()));
				nbKoSerie.append(Integer.parseInt(row[2].toString()));
				koRateSerie.append(Math.round(Double.parseDouble(row[3].toString())*100)/100F);
			}
		}
		labelSerie.append("]");
		nbOkSerie.append("]");
		nbKoSerie.append("]");
		koRateSerie.append("]");
		
		this.label = labelSerie.toString();
		this.nbKo = nbKoSerie.toString();
		this.nbOk = nbOkSerie.toString();
		this.koRate = koRateSerie.toString();
	}
	
	public String getLabel() {return label;}
	public void setLabel(String label) {this.label = label;}
	public String getNbOk() {return nbOk;}
	public void setNbOk(String nbOk) {this.nbOk = nbOk;}
	public String getNbKo() {return nbKo;}
	public void setNbKo(String nbKo) {this.nbKo = nbKo;}
	public String getKoRate() {return koRate;}
	public void setKoRate(String koRate) {this.koRate = koRate;}
	
	@Override
	public String toString() {
		return "[RunDetailGraphError]\n\tLabels:"+label+"\n\tnb Req OK:"+nbOk+"\n\tnb Req KO:"+nbKo+"\n\tKo Req rate:"+koRate;
	}
}
