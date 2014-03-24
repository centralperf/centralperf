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

package org.centralperf.helper;

import java.util.HashMap;

/**
 * Allows to store the CSV headers information and retrieve their value by name (based on an index) <br/>
 * For example, if a CSV file is formatted like this <br/>
 * <code>
 * myFirstHeader,mySecondHeader,anotherHeader,lastHeader
 * value1,value2,value3,value4
 * </code>
 * <br/>
 * Then <code>getValue("anotherHeader",["value1","value2","value3","value4"])</code> = value3
 * 
 * @since 1.0
 * 
 */
public class CSVHeaderInfo{
    private HashMap<String, Integer> headersIndex = new HashMap<String, Integer>();
    
    /**
     * Put the headers provided in a Hashmap for indexing
     * @param headers An array of CSV headers. ["myFirstHeader","mySecondHeader","anotherHeader","lastHeader"] for example.
     */
    public CSVHeaderInfo(String[] headers){
        for (int i=0; i<headers.length; i++){
            headersIndex.put(headers[i].trim().toLowerCase(), i);
        }
    }

    /**
     * Get the value for a specific field based on the associated headerName and a CSV line
     * @param headerName Name of the header for the value to find (column name)
     * @param CSVline The current line in the CSV file to analyse
     * @return The value associated to this header for the provided line
     */
    public String getValue(String headerName, String[] CSVline){
    	try{
    		return CSVline[headersIndex.get(headerName.toLowerCase())];
    	}
    	catch(ArrayIndexOutOfBoundsException aioobe){
    		return null;
    	}
    }

}
