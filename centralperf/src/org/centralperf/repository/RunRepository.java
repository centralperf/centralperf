package org.centralperf.repository;

import java.util.List;

import org.centralperf.model.dao.Run;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RunRepository extends PagingAndSortingRepository<Run, Long> {
	List<Run> findByScriptVersionScriptId(Long id);
	List<Run> findByScriptVersionId(Long id);
    List<Run> findByProjectIdOrderByStartDateDesc(Long id);
    List<Run> findByRunning(boolean running);
}
