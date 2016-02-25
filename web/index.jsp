<%--
  Created by IntelliJ IDEA.
  User: llei
  Date: 16-2-23
  Time: 下午3:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <meta HTTP-EQUIV="X-UA-COMPATIBLE" CONTENT="IE=EmulateIE9">
    <meta http-equiv="Content-type" content="text/html; charset=utf-8">
    <meta property="og:image" content="http://www.brightpointinc.com/interactive/images/PoliticalInfluence_202px.png">
    <title>AutoHome Analysis</title>
    <link type="text/css" rel="stylesheet" href="style/style.css">
    <link rel="stylesheet" href="lib/jquery-ui-1.11.4/jquery-ui.min.css">
    <script src="lib/jquery/dist/jquery.min.js"></script>
    <script src="lib/jquery-ui-1.11.4/jquery-ui.min.js"></script>
    <script type="text/javascript" src="lib/d3/d3.js"></script>

</head>
<body>
<div id="bpg">
    <!--[if IE 6]>
    <div id="bpg-error">
        <p>1 This interactive graphic requires a browser with SVG support, such as <a
                href="http://www.google.com/chrome">Chrome</a>, <a
                href="http://www.mozilla.org/en-US/firefox/">Firefox</a>, <a
                href="http://www.apple.com/safari/download/">Safari</a> or the latest <a
                href="http://windows.microsoft.com/en-US/internet-explorer/products/ie/home">Internet Explorer 9</a>.
        </p>
        <img src="images/browserCheck.jpg" alt="Error">

        <div id="bpg-chartFrame" style="display:none;">
        </div>
    <![endif]-->

    <!--[if IE 7]>
    <div id="bpg-error">
        <p>2 This interactive graphic requires a browser with SVG support, such as <a
                href="http://www.google.com/chrome">Chrome</a>, <a
                href="http://www.mozilla.org/en-US/firefox/">Firefox</a>, <a
                href="http://www.apple.com/safari/download/">Safari</a> or the latest <a
                href="http://windows.microsoft.com/en-US/internet-explorer/products/ie/home">Internet Explorer 9</a>.
        </p>
        <img src="images/browserCheck.jpg" alt="Error">

        <div id="bpg-chartFrame" style="display:none;">

    <![endif]-->

    <!--[if IE 8]>
    <div id="bpg-error">
        <p>3 This interactive graphic requires a browser with SVG support, such as <a
                href="http://www.google.com/chrome">Chrome</a>, <a
                href="http://www.mozilla.org/en-US/firefox/">Firefox</a>, <a
                href="http://www.apple.com/safari/download/">Safari</a> or the latest <a
                href="http://windows.microsoft.com/en-US/internet-explorer/products/ie/home">Internet Explorer 9</a>.
        </p>
        <img src="images/browserCheck.jpg" alt="Error">

        <div id="bpg-chartFrame" style="display:none;">

    <![endif]-->

    <!--[if IE 9]>
    <div id="bpg-chartFrame">
    <![endif]-->

    <!--[if !IE]>
    <div id="bpg-chartFrame">
    <![endif]-->

    <div id="slider">
        <label for="amount" style="font-size: 20px">Current K value: </label>
        <input type="text" id="amount" style="border:0; color:#f6931f; font-weight:bold; font-size: 20px;">
    </div>
    <div id="slider-range-min" style="width: 200px;" align="center"></div>
    <button onclick="sendSeries2Server()">ok</button>

    <div id="mainDiv">
        <div id="svgDiv"></div>

        <div id="headerRight" style="width:350px; right:20px;">
            <div class="hint">
                <div id="totalDiv"
                     style="color: #000; font-size:22px; font-weight:bold; margin-top:5px; font-family: Georgia; font-style:italic">
                    $0
                </div>
                <!--div style="color:#777; font-style:normal;">
                    Design and Engineering: <a href="http://www.brightpointinc.com/project-list/" title="BrightPoint Consulting, Inc.">www.brightpointinc.com</a>
                </div-->
            </div>
        </div>
    </div>
    <div id="toolTip" class="tooltip" style="width:250px; height:120px; position:absolute;">
        <div id="header1" class="header2"></div>
        <div class="header-rule"></div>
        <div id="head" class="header"></div>
        <div class="header-rule"></div>
        <div id="header2" class="header3"></div>
    </div>
</div>

<script type="text/javascript" src="scripts/MainGraph.js"></script>
<script type="text/javascript" src="scripts/KValueSlider.js"></script>
<script type="text/javascript" src="scripts/ConnectServer.js"></script>

</body>
</html>
