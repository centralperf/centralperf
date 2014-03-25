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

/**
 * On load....
 */
$( document ).ready(function() {
    $('.scroll-collapsed').click(function() {
        $( this ).toggleClass( "scroll-collapsed scroll-expanded" );
    });   
});

/**
 * Editable fields 
 * @param elementName 	Name of the element to turn into editable field
 */
function setEditable(elementName){
	 $(elementName).editable({
	    	send: 'always',
	    	placement : 'bottom',
	    	emptyclass : 'cp-editable-empty',
	    	params: function(params) {
		    	// change submit format : [entity attribute]=[new value]
		    	var attributeName = params.name;
		    	delete params.name;
		    	params[attributeName] = params.value;
				delete params.value;
				delete params.pk;
		    	return params;
			}
	    });
}