package org.centralperf.repository;

import org.centralperf.model.dao.KeyValue;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface KeyValueRepository extends PagingAndSortingRepository<KeyValue, Long> {
	
	KeyValue findByKey(String key);
	
}
