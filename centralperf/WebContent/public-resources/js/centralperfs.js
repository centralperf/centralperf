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
			},
		    error: function(errors) {
		    	 var msg = '';
		    	 console.log(errors);
		    	 if(errors && errors.responseText) { //ajax error, errors = xhr object
		    		 msg = JSON.parse(errors.responseText).validationErrorMessage;
		    	 } else { //validation error (client-side)
		    		 $.each(errors, function(k, v) { msg += k+": "+v+"<br>"; });
		    	 }
		    	 return msg;
		    }			
	    });
}

/**
 * Humanize file sizes (SI or Not)
 * @param bytes
 * @param si
 * @returns {String}
 */
function humanFileSize(bytes, si) {
    var thresh = si ? 1000 : 1024;
    if(bytes < thresh) return bytes + ' B';
    var units = si ? ['kB','MB','GB','TB','PB','EB','ZB','YB'] : ['KiB','MiB','GiB','TiB','PiB','EiB','ZiB','YiB'];
    var u = -1;
    do {
        bytes /= thresh;
        ++u;
    } while(bytes >= thresh);
    return bytes.toFixed(1)+' '+units[u];
};