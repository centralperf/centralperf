package org.centralperf.service;

import javax.annotation.Resource;

import org.centralperf.model.KeyValue;
import org.centralperf.repository.KeyValueRepository;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationService {
	
	@Resource
	private KeyValueRepository keyValueRepository;
	
	public String getConfigurationValue(String configName){
		KeyValue keyValue = keyValueRepository.findByKey(configName);
		return keyValue != null ? keyValue.getValue() : null;
	}

	public Boolean getConfigurationValueAsBoolean(String configName){
		String value = getConfigurationValue(configName); 
		return value != null ? new Boolean(value) : null;
	}	
	
	public void updateConfigurationValue(String configName, Boolean value) {
		updateConfigurationValue(configName, value.toString());
	}

	public void updateConfigurationValue(String configName, String value){	
		// Update (or create) the configuration in the repository
		KeyValue keyValue = keyValueRepository.findByKey(configName);
		if(keyValue == null){
			keyValue = new KeyValue();
			keyValue.setKey(configName);
		}
		keyValue.setValue(value);
		keyValueRepository.save(keyValue);
	}
	
}
