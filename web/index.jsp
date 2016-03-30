<%--
  Created by IntelliJ IDEA.
  User: llei
  Date: 16-3-9
  Time: 下午7:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html class="no-js">
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <!--[if lt IE 7]>
    <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]--><!--[if IE 7]>
    <html class="no-js lt-ie9 lt-ie8"> <![endif]--><!--[if IE 8]>
    <html class="no-js lt-ie9"> <![endif]-->
    <!--[if gt IE 8]><!--><!--<![endif]-->
    <meta name="robots" content="noindex">
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title></title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width">
    <!-- Place favicon.ico and apple-touch-icon.png in the root directory -->
    <link rel="stylesheet" href="css/normalize.css">
    <link rel="stylesheet" href="css/main.css">
    <link rel="stylesheet" href="css/RadSet.css">
    <link rel="stylesheet" href="css/layout-default-latest.css">
    <link rel="stylesheet" href="css/complex.css">
    <!--<link rel="sytlesheet" type="text/css" href="css/jquery-ui-1.10.2.custom.min.css"/>-->
    <script src="js/vendor/modernizr-2.6.2.min.js"></script>
    <style>
        body {
            font: 15px sans-serif;
        }

        .chord path {
            fill-opacity: .67;
            stroke: #000;
            stroke-width: .5px;
        }

        path.arc {
            pointer-events: none;
            fill: none;
            stroke: #000;
            display: none;
        }

        path.cell {
            fill: none;
            pointer-events: all;
        }

        circle {
            fill: red;
            fill-opacity: .8;
            stroke: #fff;
        }

        #cells.voronoi path.cell {
            stroke: brown;
        }

        #cells g:hover path.arc {
            display: inherit;
        }

    </style>
</head>
<body>
<!--[if lt IE 7]>

<p class="chromeframe">
<![endif]-->
<!-- Add your site or application content here --><!-- <p>Hello world! This is HTML5 Boilerplate.</p> -->
<script src="js/jquery.js"></script>
<!--<script>window.jQuery || document.write('<script src="js/vendor/jquery-1.9.1.min.js"><\/script>')</script>-->
<script src="js/jquery-ui-1.10.2.custom.min.js"></script>
<script src="js/plugins.js"></script>
<script src="js/main.js"></script>
<script src="js/jquery.layout-latest.js"></script>
<script src="js/d3.v3.js"></script>
<script type="text/javascript" src="js/queue.v1.min.js"></script>
<script src="js/jquery.arrayUtilities.min.js"></script>
<!--<script src="js/bit-array.js"></script>-->
<script src="js/vendor/jquery.tablesorter.min.js"></script>
<div class="ui-layout-west">
    <div class="ui-layout-center">
        <div id="mapHeader" class="header">地域分布图</div>
        <div id="chinaMap"></div>
        <div id="tooltip" class="hidden box">
            <div>
                省份:<span class="dataHolder" name="id"></span>
                <br/>
                人数:<span class="dataHolder" name="total"></span>
            </div>
        </div>
        <h2>
            <span></span>
        </h2>
    </div>
    <div class="ui-layout-south">
        <div id="styleHeader" class="header">具体车型</div>
        <div align="right" style="margin-top: 5px;margin-right: 5px;">
            对比：
            <select id="chooseAspect">
                <option value="costPerform">性价比</option>
                <option value="control">操控</option>
                <option value="space">空间</option>
                <option value="comfort">舒适度</option>
                <option value="interior">内饰</option>
                <option value="oil">油耗</option>
                <option value="appearance">外观</option>
                <option value="power">动力</option>
            </select>
        </div>
        <div id="pieChart"></div>
    </div>
</div>
<div class="ui-layout-east">
    <div class="ui-layout-center">
        <div id="radarHeader" class="header">评分对比</div>
        <div class="radarChart"></div>
    </div>
    <div class="ui-layout-south">
        <div class="header">具体参数对比</div>
        <div class="content">
            <div><label id="lblSelection"></label></div>
            <div id="divSelEntities">
                <table id="seriesComparison" class="Entities" border="1">
                </table>
            </div>
        </div>
    </div>
</div>
<div class="ui-layout-south">

</div>
<div id="mainContent">
    <div class="content"><a id="reload" href="#">Reload</a>&nbsp;&nbsp; <a
            id="clearSel" href="#"><b><font color="#d64741">Clear Selection</font></b></a>
        <span class="float-right"> <label>Search:</label>
                    <input id="txtSearch" value="" type="text"> </span>

        <div id="radialset">
            <svg></svg>
        </div>
    </div>
</div>
<div id="initDialog">

</div>
<script>

    var outerLayout, innerLayout;

    var layoutSettings_Outer = {
        name: "outerLayout"
        , defaults: {
            size: "auto"
            , minSize: 50
            , paneClass: "pane"    // default = 'ui-layout-pane'
            , resizerClass: "resizer" // default = 'ui-layout-resizer'
            , togglerClass: "toggler" // default = 'ui-layout-toggler'
            , buttonClass: "button"  // default = 'ui-layout-button'
            , contentSelector: ".content"  // inner div to auto-size so only it scrolls, not the entire pane!
            , contentIgnoreSelector: "span"    // 'paneSelector' for content to 'ignore' when measuring room for content
            , togglerLength_open: 35      // WIDTH of toggler on north/south edges - HEIGHT on east/west edges
            , togglerLength_closed: 35      // "100%" OR -1 = full height
            , hideTogglerOnSlide: true    // hide the toggler when pane is 'slid open'
            , togglerTip_open: "Close This Pane"
            , togglerTip_closed: "Open This Pane"
            , resizerTip: "Resize This Pane"
            //  effect defaults - overridden on some panes
            , fxName: "slide"   // none, slide, drop, scale
            , fxSpeed_open: 700
            , fxSpeed_close: 1500
            , fxSettings_open: {easing: "easeInQuint"}
            , fxSettings_close: {easing: "easeOutQuint"}
        }
        , west: {
            size: 800
            , spacing_closed: 21      // wider space when closed
            , togglerLength_closed: 21      // make toggler 'square' - 21x21
            , togglerAlign_closed: "top"   // align to top of resizer
            , togglerLength_open: 0     // NONE - using custom togglers INSIDE west-pane
            , togglerTip_open: "Close West Pane"
            , togglerTip_closed: "Open West Pane"
            , resizerTip_open: "Resize West Pane"
            , slideTrigger_open: "click"   // default
            , initClosed: false
            , slidable: true
            //  add 'bounce' option to default 'slide' effect
            , fxSettings_open: {easing: ""}
            , childOptions: {
                south: {
                    size: 400
                }
            }
        }
        , east: {
            size: 450
            , spacing_closed: 21      // wider space when closed
            , togglerLength_closed: 21      // make toggler 'square' - 21x21
            , togglerAlign_closed: "top"   // align to top of resizer
            , togglerLength_open: 0       // NONE - using custom togglers INSIDE east-pane
            , togglerTip_open: "Close East Pane"
            , togglerTip_closed: "Open East Pane"
            , resizerTip_open: "Resize East Pane"
            , slideTrigger_open: "click"
            , initClosed: false
            , slidable: true
            //  override default effect, speed, and settings
            , fxName: "drop"
            , fxSpeed: "normal"
            , fxSettings: {easing: ""} // nullify default easing
            , childOptions: {
                south: {
                    size: 650
                }
            }
        }
        , south: {
            maxSize: 400
            , minSize: 400
            , spacing_closed: 0     // HIDE resizer & toggler when 'closed'
            , slidable: true   // REFERENCE - cannot slide if spacing_closed = 0
            , initClosed: true
        }
        , center: {
            paneSelector: "#mainContent"      // sample: use an ID to select pane instead of a class
            , minWidth: 200
            , minHeight: 200
        }
    };

    outerLayout = $("body").layout(layoutSettings_Outer);

    var westSelector = "body > .ui-layout-west"; // outer-west pane
    var eastSelector = "body > .ui-layout-east"; // outer-east pane

    $("<span></span>").addClass("pin-button").prependTo(westSelector);
    $("<span></span>").addClass("pin-button").prependTo(eastSelector);

    outerLayout.addPinBtn(westSelector + " .pin-button", "west");
    outerLayout.addPinBtn(eastSelector + " .pin-button", "east");


    $("<span></span>").attr("id", "west-closer").prependTo(westSelector);
    $("<span></span>").attr("id", "east-closer").prependTo(eastSelector);

    outerLayout.addCloseBtn("#west-closer", "west");
    outerLayout.addCloseBtn("#east-closer", "east");

    //westLayout = $(divWestCont).layout();

    // DEMO HELPER: prevent hyperlinks from reloading page when a 'base.href' is set
    $("a").each(function () {
        var path = document.location.href;
        if (path.substr(path.length - 1) == "#")
            path = path.substr(0, path.length - 1);
        if (this.href.substr(this.href.length - 1) == "#")
            this.href = path + "#";
    });

</script>
<script src="js/RadSets/RadSets.js"></script>
<script src="js/RadSets/RadSetsConverter.js"></script>
<script src="js/RadSets/RadSetsSelector.js"></script>
<script src="js/RadSets/RadSetsDraw.js"></script>
<script src="js/RadSet.js"></script>
<script src="js/radarChart/radarChart.js"></script>
<script src="js/pie/d3pie.js"></script>
<script src="js/chinaMap/map.js"></script>
</body>
</html>
