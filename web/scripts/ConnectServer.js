function sendSeries2Server(kValue) {
    var queryStr = "seriesIds=65,66,3207,692,588,639,364,526,633,442&k=" + kValue;
    var url = "/kmeansresult?" + queryStr;
    if (window.XMLHttpRequest) {
        req = new XMLHttpRequest();
    } else if (window.ActiveXObject) {
        req = new ActiveXObject("Microsoft.XMLHTTP");
    }
    req.open("POST", url, false);
    req.onreadystatechange = freshMainGraph;
    req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    req.send(null);
}

function getSliderValue(){
    return $( "#slider-range-min" ).slider( "value" );
}

function freshMainGraph() {
    if (req.readyState == 4) {
        if (req.status == 200) {
            $("#svg").remove();
            loadMainGraph();
            //var request = req.responseText.split("###");
            //var seriesInfo = request[0];
            //var clusterInfo = request[1];
            //var collectDetailInfo = request[2];
        }
    }
}
