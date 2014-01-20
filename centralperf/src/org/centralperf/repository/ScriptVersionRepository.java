package org.centralperf.repository;

import org.centralperf.model.dao.ScriptVersion;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ScriptVersionRepository extends PagingAndSortingRepository<ScriptVersion, Long> {

}
