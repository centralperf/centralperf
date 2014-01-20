package org.centralperf.repository;

import org.centralperf.model.dao.Script;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ScriptRepository extends PagingAndSortingRepository<Script, Long> {

}
