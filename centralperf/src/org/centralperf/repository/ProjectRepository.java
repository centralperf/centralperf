package org.centralperf.repository;

import java.util.List;

import org.centralperf.model.dao.Project;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProjectRepository extends PagingAndSortingRepository<Project, Long> {
	
	@Query(value="SELECT DISTINCT s.project FROM Script s")
	List<Project> findProjectsWithScript();
}
