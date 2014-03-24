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

package org.centralperf.model;

/**
 * Enumeration for all types of graphs. Each graph have to be associated to a page name
 */
public enum RunDetailGraphTypesEnum {
	SUMMARY("sum"), 
	RESPONSE_TIMES("Rt"), 
	RESPONSE_CODES("Rc"),
	ERRORS("errors");
	
	private String pageName;
	
	/**
	 * Default constructor with the pageName
	 * @param pageName Name of the page to display it in the views
	 */
	RunDetailGraphTypesEnum(String pageName){
		this.setPageName(pageName);
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
}
