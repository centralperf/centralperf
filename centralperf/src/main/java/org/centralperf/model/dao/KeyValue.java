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

package org.centralperf.model.dao;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * KeyValue entity allows to store CP parameters in the database 
 */
@Entity
public class KeyValue {
	
    @Id
    @GeneratedValue
    private Long id;
    
    private String key;
    private String value;
    
	public Long getId() {
		return id;
	}
	
	/**
	 * Unique key used by JPA for persistence (automatically generated)
	 * @param id	Unique key
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getKey() {
		return key;
	}
	
	/**
	 * The key to find this parameter
	 * @param key	Any key
	 */
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getValue() {
		return value;
	}

	/**
	 * The value associated to a key. Only String allowed
	 * @return	the value of this entry
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
