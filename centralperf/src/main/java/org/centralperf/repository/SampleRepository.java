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

package org.centralperf.repository;

import org.centralperf.model.dao.Sample;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Samples Spring Data (http://projects.spring.io/spring-data-jpa/) based repository
 * @since 1.0
 */
public interface SampleRepository extends PagingAndSortingRepository<Sample, Long> {

}
