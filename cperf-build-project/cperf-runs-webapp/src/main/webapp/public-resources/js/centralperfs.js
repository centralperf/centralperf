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
