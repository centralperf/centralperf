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

package org.centralperf.controller;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.centralperf.controller.exception.ControllerValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Basic controller with all common methods for all controllers (Exception handling for example) and common utils
 */
public abstract class BaseController {

	/**
	 * Return an error 400 Bad Request when validation fails
	 * @param e	Validation Exception thrown 
	 * @return A message as JSON
	 * @see ControllerValidationException
	 */
    @ExceptionHandler(ControllerValidationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody String handleValidationException(ControllerValidationException e) {
        return e.getMessageAsJSON();
    }
    
    /**
     * Validate a bean based on javax.validation API
     * @param bean Bean to validate
     * @throws ControllerValidationException If the bean is not validated
     */
    protected static void validateBean(Object bean) throws ControllerValidationException{
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(bean);
        if (constraintViolations.size() > 0 ) {
        	StringBuilder errorMessage = new StringBuilder();
        	for (ConstraintViolation<Object> constraintViolation : constraintViolations) {
				errorMessage.append(constraintViolation.getMessage());
			}
        	throw new ControllerValidationException(errorMessage.toString());
        }
    }
	
}
