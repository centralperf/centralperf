package org.centralperf.repository;

import java.util.List;

import org.centralperf.model.dao.ScriptVersion;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ScriptVersionRepository extends PagingAndSortingRepository<ScriptVersion, Long> {
	List<ScriptVersion> findByScriptIdOrderByNumberDesc(Long id);
}
