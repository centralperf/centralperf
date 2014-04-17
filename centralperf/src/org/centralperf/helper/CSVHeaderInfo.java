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
	
	/**
	 * Name of the CSV header to use for sample execution date/time
	 */
	public static final String CSV_HEADER_TIMESTAMP = "timestamp";
	/**
	 * Name of the CSV header to use for elapsed time in ms
	 */	
	public static final String CSV_HEADER_ELAPSED = "elapsed";
	/**
	 * Name of the CSV header to use for sample name
	 */	
	public static final String CSV_HEADER_SAMPLENAME = "label";
	/**
	 * Name of the CSV header to use for sample result status
	 */	
	public static final String CSV_HEADER_STATUS = "responseMessage";
	/**
	 * Name of the CSV header to use for sample response code (HTTP Response code for example)
	 */	
	public static final String CSV_HEADER_RESPONSECODE = "responsecode";	
	/**
	 * Name of the CSV header to use for assert result
	 */
	public static final String CSV_HEADER_ASSERTRESULT = "success";
	/**
	 * Name of the CSV header to use for sample size on bytes
	 */	
	public static final String CSV_HEADER_SIZEINBYTES = "bytes";
	/**
	 * Name of the CSV header to use for number of users in group
	 */	
	public static final String CSV_HEADER_GROUPTHREADS = "grpthreads";
	/**
	 * Name of the CSV header to use for number of all current users
	 */	
	public static final String CSV_HEADER_ALLTHREADS = "allthreads";
	/**
	 * Name of the CSV header to use for latency
	 */	
	public static final String CSV_HEADER_LATENCY = "latency";
	/**
	 * Name of the CSV header to use for CP sample id
	 */	
	public static final String CSV_HEADER_SAMPLEID = "sampleid";
	/**
	 * Name of the CSV header to use for CP run id
	 */	
	public static final String CSV_HEADER_RUNID = "runid";
	
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
