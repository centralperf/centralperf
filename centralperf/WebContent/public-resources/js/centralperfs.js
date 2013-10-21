/**
 * On load....
 */
$( document ).ready(function() {
    $('.scroll-collapsed').click(function() {
        $( this ).toggleClass( "scroll-collapsed scroll-expanded" );
    });   
});

/**
* Load modal forms
*/
function displayModal(url){
    $.modal("<div id='modalWindow'></div>");
    $("#modalWindow").load(url);
}

/**
 * Editable fields 
 * @param elementName 	Name of the element to turn into editable field
 * @param attributeName Name of the editable entity attribute
 * @param entityPath 	Server path to submit entity change
 */
function setEditable(elementName, attributeName, entityPath, editorType){
	if(editorType == undefined){
		editorType = "text";
	}
	$(elementName).addClass("editableText");
    $(elementName).editable("", {
        onsubmit: function (settings, self) {
        	var entityId = $(self).attr("entityId");
        	settings.name = attributeName;
            settings.target = entityPath + entityId;
            var value = "";
            if(editorType == "textarea"){
            	value = $(self).find('textarea').val();
            } else {
            	value = $(self).find('input').val();
            }
            // Validate not empty
            return ("" != value.trim());
        },
        tooltip : "Double click to edit " + attributeName,
        event	: 'dblclick',
    	style   : 'inherit',
    	type	: editorType,
        submit  : editorType == "textarea" ? "OK" : "",
        cancel  : editorType == "textarea" ? "Cancel" : "",
        height	: editorType == "textarea" ? "200px" : "auto"
    });	
}


