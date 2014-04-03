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

package org.centralperf.model.graph;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Bean to store all data to be able to display the "Response time" graph in views
 */
public class ResponseTimeGraph implements Serializable{

	private static final long serialVersionUID = 6913418750734276967L;

	private String csvDatas;

	 
	public ResponseTimeGraph(Iterator<Object[]> datas) {
		StringBuilder buffer = new StringBuilder("x,elapsed,latency\n");
		Object[] row =null;
		while ( datas.hasNext() ) {
			row = (Object[]) datas.next();
			buffer.append(row[0].toString()).append(',');
			buffer.append(row[1].toString()).append(',');
			buffer.append(row[2].toString()).append('\n');
		}
		this.csvDatas=buffer.toString();
	}
	
	@Override
	public String toString() {
		return this.csvDatas;
	}
}
