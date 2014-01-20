package org.centralperf.repository;

import org.centralperf.model.dao.Sample;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SampleRepository extends PagingAndSortingRepository<Sample, Long> {

}
