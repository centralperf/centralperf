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
			    		cnf.setKeyValue(beanFactory.resolveEmbeddedValue("${"+key+"}"));
			    		log.debug("Value for ["+key+"] found in properties["+cnf.getKeyValue()+"].");
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
