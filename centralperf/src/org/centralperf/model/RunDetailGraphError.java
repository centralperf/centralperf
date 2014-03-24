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
import java.util.Iterator;

/**
 * Bean to store all data to be able to display the "Error" graph in views
 */
public class RunDetailGraphError implements Serializable{

	private static final long serialVersionUID = 2412018415877293827L;

	private String label;
	private String nbOk;
	private String nbKo;
	private String koRate;

	/**
	 * Default constructor
	 * @param datas	Data for the graph passed as a an array of objects
	 */
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
