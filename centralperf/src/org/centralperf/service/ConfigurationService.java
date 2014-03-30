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

package org.centralperf.service;

import javax.annotation.Resource;

import org.centralperf.model.Configuration;
import org.centralperf.model.dao.KeyValue;
import org.centralperf.repository.KeyValueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Configuration for CP integration (database, logs...) or features
 * @since 1.0
 */
@Service
public class ConfigurationService {
	
	@Resource
	private KeyValueRepository keyValueRepository;
	
	@Autowired
	private AbstractBeanFactory beanFactory;
	
	private static final Logger log = LoggerFactory.getLogger(ConfigurationService.class);
	
	LoadingCache<String,Configuration> cache = CacheBuilder.newBuilder()
		    .maximumSize(100)
		    .build(new CacheLoader<String, Configuration>() {
			    @Override
			    public Configuration load(String key) throws Exception {
			    	log.debug("Value for ["+key+"] not in cache. Loading from DB and properties file");
			    	Configuration cnf = new Configuration(key);
			    	
			    	//First looking in DB
			    	KeyValue keyValue = keyValueRepository.findByKey(key);
			    	if(keyValue!=null){
			    		cnf.setFromDb(true);
			    		cnf.setKeyValue(keyValue.getValue());
			    		log.debug("Value for ["+key+"] found in database["+cnf.getKeyValue()+"].");
				    }
			    	else{
			    		cnf.setFromDb(false);
			    		try{
			    			cnf.setKeyValue(beanFactory.resolveEmbeddedValue("${"+key+"}"));
				    		log.debug("Value for ["+key+"] found in properties["+cnf.getKeyValue()+"].");
			    		}
			    		catch(IllegalArgumentException e){
			    			// The key is not necessaraly in a property file. It may be possible to check properties that will exist at last only in the DB
			    			// Then set configuration to null
			    			log.debug("Value for ["+key+"] not found in properties. Set it to null.");
			    			cnf.setKeyValue(null);			    			
			    		}
			    	}
			    	return cnf;
			    }
		    });
	
	
	public Configuration getConfigurationData(String key){
		return cache.getUnchecked(key);
	}
	
	public String getConfigurationValue(String key){
		return cache.getUnchecked(key).getKeyValue();
	}
	
	public void updateConfigurationValue(String key, String value){	
		KeyValue keyValue = keyValueRepository.findByKey(key);
		if(keyValue == null){
			keyValue = new KeyValue();
			keyValue.setKey(key);
		}
		keyValue.setValue(value);
		log.warn("Updating ["+keyValue.getKey()+"] with data ["+keyValue.getValue()+"] in db");
		keyValueRepository.save(keyValue);
		log.warn("Invalidation of ["+keyValue.getKey()+"] from cache");
		cache.invalidate(key);
	}
	
	public void deleteConfigurationValue(String key){
		KeyValue keyValue = keyValueRepository.findByKey(key);
		if(keyValue!=null){
			keyValueRepository.delete(keyValue.getId());
			cache.invalidate(key);
		}
		else{log.warn("Revert of ["+key+"] could not be done (Was not found in DB");}
	}
}
