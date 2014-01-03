package org.centralperf.helper;

import java.util.HashMap;

/**
 * Allows to store the CSV headers information and retrieve their index by name 
 * @author Charles Le Gallic
 */
public class CSVHeaderInfo{
    private HashMap<String, Integer> headersIndex = new HashMap<String, Integer>();
    public CSVHeaderInfo(String[] headers){
        for (int i=0; i<headers.length; i++){
            headersIndex.put(headers[i].trim().toLowerCase(), i);
        }
    }

    public String getValue(String headerName, String[] CSVline){
    	try{
    		return CSVline[headersIndex.get(headerName.toLowerCase())];
    	}
    	catch(ArrayIndexOutOfBoundsException aioobe){
    		return null;
    	}
    }

}
