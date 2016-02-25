$(function() {
    $( "#slider-range-min" ).slider({
        range: "min",
        value: 5,
        min: 3,
        max: 20,
        slide: function( event, ui ) {
            $( "#amount" ).val(ui.value);
        }
    });
    $( "#amount" ).val( $( "#slider-range-min" ).slider( "value" ) );
});