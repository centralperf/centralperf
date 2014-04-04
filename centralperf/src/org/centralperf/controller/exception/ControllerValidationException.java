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

package org.centralperf.controller.exception;

/**
 * Thrown when the controller does not validate data
 * @since 1.0
 */
@SuppressWarnings("serial")
public class ControllerValidationException extends Exception {
	
	/**
	 * Default controller. Message is requested
	 * @param message Message that will be displayed in the UI
	 */
	public ControllerValidationException(String message) { 
		super(message);
	}
	
	/**
	 * Return the message as a JSON string
	 * @return the message as a JSON string
	 */
	public String getMessageAsJSON(){
		return "{\"validationErrorMessage\":\"" + this.getMessage() + "\"}";
	}
}
