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