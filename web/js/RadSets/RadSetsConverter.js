/**
 Module for web implementation of Radial Sets
 @module RadSets
 @submodule Converter
 @main RadSets
 **/

/**
 @class RadSet
 @constructor
 **/
var RadSet = (function (window, document, $, undefined) {
    var _x = window.RadSet || {};

    /**
     * List of Entries
     * @attribute Entries
     * @type [Entry]
     */
    _x.Entries = [];
    /**
     * List of Categories
     * @attribute CatList
     * @type [Category]
     */
    _x.CatList = [];
    /**
     * List of elements by Cardinality
     * @attribute ElementsByCardinality
     * @type [{Category,Count}]
     */
    _x.ElementsByCardinality = [];

    /**
     * List of elements by degree
     * @attribute ElementsByDegree
     * @type [{Degree,Count}]
     */
    _x.ElementsByDegree = [];


    //_x.ActiveSchema = "avgRating";
    //_x.Schemas = {
    //    "avgRating": { file: "AvgRating.csv", order: "desc" },
    //    "releaseDate": { file: "releaseDate.csv", order: "desc" },
    //    "watches": { file: "watches.csv", order: "desc" },
    //};
    //_x.Legend = {
    //    "avgRating": { file: "AvgRating_TF.csv"},
    //    "releaseDate": { file: "releaseDate_TF.csv"},
    //    "watches": { file: "watches_TF.csv"},
    //};
    //_x.LegendData = null;


    /* Constructors */
    /**
     Entry class
     @class Entry
     @constructor
     **/
    function Entry(id, name) {
        /**
         Autonumber of Entry
         @property ID
         @type Integer
         **/
        this.ID = id;
        /**
         Name of the Entry
         @property Name
         @type String
         **/
        this.Name = name;
        /**
         List of Categories Names
         @property Cats
         @type [String]
         **/
        this.Cats = [];
        /**
         Degree
         @property Degree
         @type Int
         @default -1
         **/
        this.Degree = -1;
        /**
         ReleaseDate
         @property Degree
         @type Date
         @default null
         **/
        this.ReleaseDate = null;
        /**
         AvgRating
         @property AvgRating
         @type Float
         @default 0
         **/
        this.AvgRating = 0;
        /**
         Watches
         @property Watches
         @type int
         @default 0
         **/
        this.Watches = 0;
        /**
         Returns String of connected Categories
         @method Sets
         @return String
         **/
        this.Sets = function () {
            return this.Cats.join(",");
        };
    }

    /**
     Category class
     @class Category
     @constructor
     **/
    function Category(name) {
        /**
         @property Name
         @type String
         **/
        this.Name = name;
        /**
         @property Count
         @type Integer
         @default 0
         **/
        this.Count = 0;
        /**
         @property StartAngle
         @type Integer
         @default 0
         **/
        this.StartAngle = 0;
        /**
         @property EndAngle
         @type Integer
         @default 0
         **/
        this.EndAngle = 0;
        /**
         @property MiddleAngle
         @type Integer
         @default 0
         **/
        this.MiddleAngle = 0;
        /**
         @property SortOrder
         @type Integer
         @default 0
         **/
        this.SortOrder = 0;
        /**
         @property Histograms
         @type [Histogram]
         @default []
         **/
        this.Histograms = [];
        /**
         Object that contains information about connected ConnectedCategory, Key for the Category is its Name
         @property ConnectedCats
         @type {ConnectedCategory}
         @default {}
         **/
        this.ConnectedCats = {};
        /**
         Gets the maximum count in the histogram list
         @method MaxCountInHistogram
         @for Category
         @return Integer
         **/
        this.MaxCountInHistogram = function () {
            var max = 0;
            for (var i = 0; i < this.Histograms.length; i += 1) {
                var x = this.Histograms[i];
                if (max < x.Count) {
                    max = x.Count;
                }
            }
            return max;
        };
        this.StyleList = [];
        this.AttrList = [];
        this.SpecialAttrList = [];
        this.Score = {};
        this.AimList = [];
        this.avgAge = 0;
        this.ageVariance = 0;
        this.BoughtLineList = [];
        this.AVGPriceOfProvince = [];
        this.Focus = {};
    }

    /**
     ConnectedCategory class
     @class ConnectedCategory
     @constructor
     **/
    function ConnectedCategory(name, count, firstEntryId) {
        /**
         @property Name
         @type String
         **/
        this.Name = name;
        /**
         @property Count
         @type Int
         **/
        this.Count = count;
        /**
         List of the connected entry IDs
         @property Entries
         @type [Int]
         **/
        this.Entries = [firstEntryId]; //list of the connected entry ids
        /**
         @method GetNumberOfConnected
         @return Int
         **/
        this.GetNumberOfConnected = function (ids) {
            var count = 0;
            for (var i = 0, len = ids.length; i < len; i++) {
                if (this.Entries.indexOf(ids[i]) !== -1) {
                    count += 1;
                }
            }
            return count;
        };
    }

    /**
     Histogram class
     @class Histogram
     @constructor
     **/
    function Histogram(degree) {
        /**
         @property Degree
         @type Int
         **/
        this.Degree = degree;
        /**
         @property InnerRadius
         @type Float
         **/
        this.InnerRadius = 0;
        /**
         @property OuterRadius
         @type Float
         **/
        this.OuterRadius = 0;
        /**
         @property Count
         @type Int
         **/
        this.Count = 0;
    }

    function Style(Id, Name, Num) {
        this.Id = Id;
        this.Name = Name;
        this.Num = parseInt(Num);//total sale num
        this.attr = [];
        this.Score = {};
        this.Sale = [];
    }

    function SaleNumAndTime(num, boughtTime, avgPrice){
        this.SaleNum = num;
        this.boughtTime = parseInt(boughtTime);
        this.avgPrice = avgPrice;
    }

    function Attribution(attrName, attrValue) {
        this.attrName = attrName;
        this.attrValue = attrValue;
    }

    function Aim(aimName, aimProp) {
        this.aimName = aimName;
        this.aimProp = aimProp;
    }

    function BoughtLine(userLoc, boughtSite, num) {
        this.userLoc = userLoc;
        this.boughtSite = boughtSite;
        this.num = parseFloat(num);
    }

    function BoughtPriceOfProvince(province, avgPrice, boughtNum) {
        this.province = province;
        this.avgPrice = avgPrice;
        this.boughtNum = boughtNum;
    }

    /**
     Reads a list of strings an transforms it into Entries and Categories
     @method ConvertCSVtoObjects
     @for RadSet
     @param {String} allTextLines
     @param {String} sep
     **/
    function ConvertCSVtoObjects(allTextLines, sep) {
        _x.CatList = [];
        _x.Entries = [];
        var maxLines = allTextLines.length;
        var headerRow = [];
        var headerIdxAndCatIdx = [];

        if (maxLines > _x.options.EntryLimit && _x.options.EntryLimit > 0) {
            maxLines = _x.options.EntryLimit;
        }

        for (var i = 0; i < maxLines; i++) {
            var line = allTextLines[i];
            var lineParts = line.split(sep);
            var e = null;

            if (line === "") {
                continue;
            }

            for (var x = 0; x < lineParts.length; x++) {
                var t = lineParts[x];
                if (i === 0) {
                    headerRow.push(t);
                }
                if (i === 0 && x !== 0) {
                    if ($.inArray(t, _x.options.ListOfNonCategories) === -1) { //when not in list den count as category
                        var c = new Category(t);
                        c.SortOrder = _x.options.CategoryOrder.indexOf(t) === -1 ? null : _x.options.CategoryOrder.indexOf(t);
                        _x.CatList.push(c);

                        headerIdxAndCatIdx[x] = _x.CatList.length - 1;
                    }
                } else if (i !== 0 && x === 0) {
                    var id = _x.Entries.length;
                    e = new Entry(id, t);
                } else if (i !== 0 && x !== 0) {
                    var head = headerRow[x];
                    if ($.inArray(head, _x.options.ListOfNonCategories) === -1) { //when not in list den count as category
                        if (t === "1") {
                            var catIdx = headerIdxAndCatIdx[x];
                            if (catIdx !== undefined) {
                                var cat = _x.CatList[catIdx];
                                e.Cats.push(cat.Name);
                                _x.CatList[catIdx].Count += 1;
                            }
                        }
                    } else {
                        e[head] = t;
                    }
                }
            }
            if (e !== null) {
                e.Degree = e.Cats.length;
                if ((e.Degree < 1 && !_x.options.IgnoreEntriesWithoutCategories) || e.Degree > 0) {
                    _x.Entries.push(e);
                }
            }
        }
        _x.log("{0} categories and {1} objects saved.".format(_x.CatList.length, _x.Entries.length));

        //if no sortorder was found then take category-entry-count
        for (var cidx = 0; cidx < _x.CatList.length; cidx++) {
            var cat2 = _x.CatList[cidx];
            if (cat2.SortOrder === null) {
                cat2.SortOrder = cat2.Count;
            }
        }

        _x.CatList.sort(function (a, b) {
            return (a.SortOrder - b.SortOrder);
        });

        CreateHistogram();
    }

    /**
     Create Histogram Objects for the Entries
     @method CreateHistogram
     @for RadSet
     **/
    function CreateHistogram() {
        var tmpCatHistoList = {};
        var tmpConnectedCats = {};

        for (var idx = _x.Entries.length - 1; idx >= 0; idx--) {
            var e = _x.Entries[idx];
            var degree = e.Degree + '';
            for (var i = e.Cats.length - 1; i >= 0; i--) {
                var cname = e.Cats[i];

                if (tmpCatHistoList[cname] === undefined) {
                    tmpCatHistoList[cname] = {};
                }
                if (tmpConnectedCats[cname] === undefined) {
                    tmpConnectedCats[cname] = {};
                }
                if (tmpCatHistoList[cname][degree] === undefined) {
                    tmpCatHistoList[cname][degree] = new Histogram(parseInt(degree, 10));
                    tmpCatHistoList[cname][degree].Count = 1;
                } else {
                    tmpCatHistoList[cname][degree].Count += 1;
                }
                //create connected to
                var otherCats = e.Cats.slice();
                otherCats.splice(i, 1);
                for (var oc = otherCats.length - 1; oc >= 0; oc--) {
                    var ocName = otherCats[oc];
                    if (tmpConnectedCats[cname][ocName] === undefined) {
                        tmpConnectedCats[cname][ocName] = new ConnectedCategory(ocName, 1, e.ID);
                    } else {
                        tmpConnectedCats[cname][ocName].Count += 1;
                        tmpConnectedCats[cname][ocName].Entries.push(e.ID);
                    }
                }
            }
        }


        for (var c = 0; c < _x.CatList.length; c++) {
            var cat = _x.CatList[c];
            var histos = [];
            if (tmpCatHistoList[cat.Name] !== undefined) {
                var k;
                for (k in tmpCatHistoList[cat.Name]) {
                    var hi = tmpCatHistoList[cat.Name][k];
                    histos.push(hi);
                }
                _x.CatList[c].Histograms = histos;
            }

            var connected = [];
            if (tmpConnectedCats[cat.Name] !== undefined) {
                var kcc;
                for (kcc in tmpConnectedCats[cat.Name]) {
                    var con = tmpConnectedCats[cat.Name][kcc];
                    connected.push(con);
                }
                //_x.CatList[c].ConnectedCats = connected;
            }
            var connectedSortByPos = [];
            for (var i = c + 1; i < _x.CatList.length; i++) {
                for (var j = 0; j < connected.length; j++) {
                    if (connected[j].Name == _x.CatList[i].Name) {
                        connectedSortByPos.push(connected[j]);
                    }
                }
            }
            for (var i = 0; i < c; i++) {
                for (var j = 0; j < connected.length; j++) {
                    if (connected[j].Name == _x.CatList[i].Name) {
                        connectedSortByPos.push(connected[j]);
                    }
                }
            }
            _x.CatList[c].ConnectedCats = connectedSortByPos;
        }

        _x.log("created historgrams data.");
    }


    /**
     Method that reads the CSV file and transforms the data in the file
     @method ReadCSV
     @for RadSet
     @param {String} filename
     **/
    _x.ReadCSV = function ReadCSV(filename) {
        $.ajax({
            url: filename,
            async: false,
            success: function (data) {
                _x.log("file {0} found.".format(filename));
                var allTextLines = [];
                //$("#divPlainText").text(data);
                allTextLines = data.split(/\r\n|\n/);
                ConvertCSVtoObjects(allTextLines, ",");
            },
            error: function (xhr) {
                _x.log("error while reading file {0}.".format(filename));
            }
        });
    };

    /**
     Method that query style list of series and series' general attribution
     @param {Category} catList
     **/
    _x.QuerySeriesDetail = function QuerySeriesDetail(catList) {
        var queryStr = "seriesNames=";
        for (var i = 0; i < catList.length; i++) {
            queryStr += catList[i].Name;
            if (i < catList.length - 1) {
                queryStr += ",";
            }
        }
        var url = "detailofseries?" + queryStr;
        if (window.XMLHttpRequest) {
            req = new XMLHttpRequest();
        } else if (window.ActiveXObject) {
            req = new ActiveXObject("Microsoft.XMLHttp");
        }
        req.open("POST", url, false);
        req.onreadystatechange = ProcessSeriesDetail;
        req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        req.send(null);
    };

    function ProcessSeriesDetail() {
        if (req.readyState == 4) {
            if (req.status == 200) {
                var result = req.responseText.split("###");
                var styleList = result[0];
                var generalAttrListOfSeries = result[1];
                var attrListOfStyleOfSeries = result[2];
                var seriesScoreList = result[3];
                var seriesAimList = result[4];
                var seriesBoughtLine = result[5];
                var seriesBoughtPriceOfProvince = result[6];
                var seriesFocus = result[7];
                var styleScoreList = result[8];
                var styleBoughtTimePriceList = result[9];
                FillStyleOfCategory(styleList);
                FillGeneralAttrOfCategory(generalAttrListOfSeries);
                FillAttrOfStyle(attrListOfStyleOfSeries);
                FillSpecialAttrOfCategory();
                FillSeriesScore(seriesScoreList);
                FillSeriesAim(seriesAimList);
                FillSeriesBoughtLine(seriesBoughtLine);
                FillSeriesBoughtPriceOfProvince(seriesBoughtPriceOfProvince);
                FillSeriesFocus(seriesFocus);
                FillStyleScore(styleScoreList);
                FillStyleBoughtTimePriceList(styleBoughtTimePriceList);
            }
        }
    }

    function FillStyleBoughtTimePriceList(styleBoughtTimePriceList) {
        var allTextLines = styleBoughtTimePriceList.split(/\r\n|\n/);
        var len = allTextLines.length;

        for (var i = 0; i < len; i++) {
            var lineTxt = allTextLines[i];
            if (lineTxt == "") {
                continue;
            }
            var lineParts = lineTxt.split(",");
            var seriesName = lineParts[0];
            var styleId = lineParts[1];
            var boughtTime = lineParts[2];
            var avgPrice = parseFloat(lineParts[3]);
            var saleNum = parseInt(lineParts[4]);
            var catNum = _x.CatList.length;
            for (var j = 0; j < catNum; j++) {
                if (_x.CatList[j].Name == seriesName) {
                    for(var k=0;k<_x.CatList[j].StyleList.length;k++){
                        if(_x.CatList[j].StyleList[k].Id === styleId){
                            _x.CatList[j].StyleList[k].Sale.push(new SaleNumAndTime(saleNum, boughtTime, avgPrice));
                        }
                    }
                }
            }
        }
    }

    function FillStyleScore(styleScoreList) {
        var allTextLines = styleScoreList.split(/\r\n|\n/);
        var len = allTextLines.length;
        for (var i = 0; i < len; i++) {
            var lineTxt = allTextLines[i];
            if (lineTxt == "") {
                continue;
            }
            var lineParts = lineTxt.split(",");
            var seriesName = lineParts[0];
            var styleId = lineParts[1];
            var catNum = _x.CatList.length;
            for (var j = 0; j < catNum; j++) {
                if (_x.CatList[j].Name == seriesName) {
                    for (var k = 0; k < _x.CatList[j].StyleList.length; k++) {
                        if (_x.CatList[j].StyleList[k].Id === styleId) {
                            _x.CatList[j].StyleList[k].Score["costPerform"] = parseFloat(lineParts[2]);
                            _x.CatList[j].StyleList[k].Score["control"] = parseFloat(lineParts[3]);
                            _x.CatList[j].StyleList[k].Score["space"] = parseFloat(lineParts[4]);
                            _x.CatList[j].StyleList[k].Score["comfort"] = parseFloat(lineParts[5]);
                            _x.CatList[j].StyleList[k].Score["interior"] = parseFloat(lineParts[6]);
                            _x.CatList[j].StyleList[k].Score["oil"] = parseFloat(lineParts[7]);
                            _x.CatList[j].StyleList[k].Score["appearance"] = parseFloat(lineParts[8]);
                            _x.CatList[j].StyleList[k].Score["power"] = parseFloat(lineParts[9]);
                        }
                    }
                }
            }
        }
    }

    function FillSeriesFocus(seriesFocus) {
        var allTextLines = seriesFocus.split(/\r\n|\n/);
        var len = allTextLines.length;

        for (var i = 0; i < len; i++) {
            var lineTxt = allTextLines[i];
            if (lineTxt == "") {
                continue;
            }
            var lineParts = lineTxt.split(",");
            var seriesName = lineParts[0];
            var aspect = lineParts[1];
            var degree = lineParts[2];
            var catNum = _x.CatList.length;
            for (var j = 0; j < catNum; j++) {
                if (_x.CatList[j].Name == seriesName) {
                    _x.CatList[j].Focus[aspect] = parseFloat(degree);
                }
            }
        }
    }

    function FillSeriesBoughtPriceOfProvince(seriesBoughtPriceOfProvince) {
        var allTextLines = seriesBoughtPriceOfProvince.split(/\r\n|\n/);
        var len = allTextLines.length;

        for (var i = 0; i < len; i++) {
            var lineTxt = allTextLines[i];
            if (lineTxt == "") {
                continue;
            }
            var lineParts = lineTxt.split(",");
            var seriesName = lineParts[0];
            var boughtPriceOfProvince = new BoughtPriceOfProvince(lineParts[1], lineParts[2], lineParts[3]);
            var catNum = _x.CatList.length;
            for (var j = 0; j < catNum; j++) {
                if (_x.CatList[j].Name == seriesName) {
                    _x.CatList[j].AVGPriceOfProvince.push(boughtPriceOfProvince);
                }
            }
        }
    }

    function FillSeriesBoughtLine(seriesBoughtLine) {
        var allTextLines = seriesBoughtLine.split(/\r\n|\n/);
        var len = allTextLines.length;


        for (var i = 0; i < len; i++) {
            var lineTxt = allTextLines[i];
            if (lineTxt == "") {
                continue;
            }
            var lineParts = lineTxt.split(",");
            var seriesName = lineParts[0];
            var boughtline = new BoughtLine(lineParts[1], lineParts[2], lineParts[3]);
            var catNum = _x.CatList.length;
            for (var j = 0; j < catNum; j++) {
                if (_x.CatList[j].Name == seriesName) {
                    _x.CatList[j].BoughtLineList.push(boughtline);
                }
            }
        }
    }


    function FillSpecialAttrOfCategory() {
        for (var i = 0; i < _x.CatList.length; i++) {
            _x.CatList[i].SpecialAttrList = getSpecialAttr(_x.CatList[i]);
        }
    }

    function getSpecialAttr(catgory) {
        var attrList = [];
        var len = catgory.AttrList.length;
        for (var i = 0; i < len; i++) {
            var attr = catgory.AttrList[i];
            for (var j = 0; j < _x.CatList.length; j++) {
                if (_x.CatList[j] != catgory && checkAttrExist(_x.CatList[j].StyleList, attr)) {
                    break;
                }
                if (j == _x.CatList.length - 1) {
                    attrList.push(attr);
                }
            }
        }
        return attrList;
    }

    function checkAttrExist(styleList, attr) {
        for (var i = 0; i < styleList.length; i++) {
            var style = styleList[i];
            for (var j = 0; j < style.attr.length; j++) {
                var attr2 = style.attr[j];
                if (attr2.attrName === attr.attrName && attr2.attrValue === attr.attrValue) {
                    return true;
                }
            }
        }
        return false;
    }

    function FillSeriesAim(seriesAimList) {
        var allTextLines = seriesAimList.split(/\r\n|\n/);
        var len = allTextLines.length;
        for (var i = 0; i < len; i++) {
            var lineTxt = allTextLines[i];
            if (lineTxt == "") {
                continue;
            }
            var lineParts = lineTxt.split(",");
            var seriesName = lineParts[0];
            var aim = new Aim(lineParts[1], lineParts[2]);
            var catNum = _x.CatList.length;
            for (var j = 0; j < catNum; j++) {
                if (_x.CatList[j].Name == seriesName) {
                    _x.CatList[j].AimList.push(aim);
                }
            }
        }
    }

    function FillSeriesScore(seriesScoreList) {
        var allTextLines = seriesScoreList.split(/\r\n|\n/);
        var len = allTextLines.length;
        for (var i = 0; i < len; i++) {
            var lineTxt = allTextLines[i];
            if (lineTxt == "") {
                continue;
            }
            var lineParts = lineTxt.split(",");
            var seriesName = lineParts[0];
            var catNum = _x.CatList.length;
            for (var j = 0; j < catNum; j++) {
                if (_x.CatList[j].Name == seriesName) {
                    _x.CatList[j].Score["costPerform"] = parseFloat(lineParts[1]);
                    _x.CatList[j].Score["control"] = parseFloat(lineParts[2]);
                    _x.CatList[j].Score["space"] = parseFloat(lineParts[3]);
                    _x.CatList[j].Score["comfort"] = parseFloat(lineParts[4]);
                    _x.CatList[j].Score["interior"] = parseFloat(lineParts[5]);
                    _x.CatList[j].Score["oil"] = parseFloat(lineParts[6]);
                    _x.CatList[j].Score["appearance"] = parseFloat(lineParts[7]);
                    _x.CatList[j].Score["power"] = parseFloat(lineParts[8]);
                }
            }
        }
    }

    function FillAttrOfStyle(attrListOfStyleOfSeries) {
        var allTextLines = attrListOfStyleOfSeries.split(/\r\n|\n/);
        var len = allTextLines.length;
        for (var i = 0; i < len; i++) {
            var lineTxt = allTextLines[i];
            if (lineTxt == "") {
                continue;
            }
            var lineParts = lineTxt.split(",");
            var seriesName = lineParts[0];
            var styleId = lineParts[1];
            var styleAttr = lineParts[2];
            var attr = styleAttr.split("]");
            var attrName = attr[0].substring(1);
            var attrValue = attr[1];
            var attribution = new Attribution(attrName, attrValue);
            var catNum = _x.CatList.length;
            for (var j = 0; j < catNum; j++) {
                if (_x.CatList[j].Name == seriesName) {
                    var Category = _x.CatList[j];
                    var lenStyleList = Category.StyleList.length;
                    for (var k = 0; k < lenStyleList; k++) {
                        if (Category.StyleList[k].Id == styleId) {
                            Category.StyleList[k].attr.push(attribution);
                        }
                    }
                }
            }
        }
    }

    function FillStyleOfCategory(styleList) {
        var allTextLines = styleList.split(/\r\n|\n/);
        var len = allTextLines.length;
        for (var i = 0; i < len; i++) {
            var lineTxt = allTextLines[i];
            if (lineTxt == "") {
                continue;
            }
            var lineParts = lineTxt.split(",");
            var seriesName = lineParts[0];
            if (seriesName == "宝马3系") {
                seriesName = "宝马3系";
            }
            var styleId = lineParts[1]
            var styleName = lineParts[2];
            var styleNum = lineParts[3];
            var style = new Style(styleId, styleName, styleNum);
            var catNum = _x.CatList.length;
            for (var j = 0; j < catNum; j++) {
                if (_x.CatList[j].Name == seriesName) {
                    _x.CatList[j].StyleList.push(style);
                }
            }
        }
    }

    function FillGeneralAttrOfCategory(attrList) {
        var allTextLines = attrList.split(/\r\n|\n/);
        var len = allTextLines.length;
        for (var i = 0; i < len; i++) {
            var lineTxt = allTextLines[i];
            if (lineTxt == "") {
                continue;
            }
            var lineParts = lineTxt.split(",");
            var seriesName = lineParts[0];
            var attr = lineParts[1].split("]");
            var attrName = attr[0].substring(1);
            var attrValue = attr[1];
            var attribution = new Attribution(attrName, attrValue);
            var catNum = _x.CatList.length;
            for (var j = 0; j < catNum; j++) {
                if (_x.CatList[j].Name == seriesName) {
                    _x.CatList[j].AttrList.push(attribution);
                }
            }
        }
    }

    _x.CreateColorSchemaFromCSV = function (name, filename, order) {
        $.ajax({
            url: filename,
            async: false,
            success: function (data) {
                var allTextLines = [];
                allTextLines = data.split(/\r\n|\n/);
                //CreateColorSchema(name, allTextLines, ";");
                CreateColorSchema2(name, allTextLines, "\t", order);
            },
            error: function (xhr) {
                _x.log("error while reading file {0}.".format(filename));
            }
        });
    };

    _x.ReadLegendCSV = function (name, filename) {
        $.ajax({
            url: filename,
            async: false,
            success: function (data) {
                var allTextLines = [];
                allTextLines = data.split(/\r\n|\n/);
                CreateLegend(name, allTextLines, "\t");
            },
            error: function (xhr) {
                _x.log("error while reading file {0}.".format(filename));
            }
        });
    }

    _x.ActivateSchema = function (name) {
        $("body").removeClass(_x.ActiveSchema);
        _x.ActiveSchema = null;
        if (name !== undefined && name !== null && name !== "") {
            $("body").addClass(name);
            _x.ActiveSchema = name;
            UpdateLegendDialog(name, "tabLegend");
        }
    }

    _x.DeactivateSchema = function (name) {
        $("body").removeClass(_x.ActiveSchema);
    };
    _x.ReactivateSchema = function (name) {
        $("body").addClass(_x.ActiveSchema);
    };

    _x.ShowLegend = function () {
        $("#diaLegend").dialog("open");
    };

    function UpdateLegendDialog(name, id) {
        var tab = $(document.getElementById(id));
        tab.empty();
        var str = "";
        var data = _x.LegendData[name];
        var c = 0;
        for (var v in data) {
            var d = data[v];
            str += "<tr>";
            if (c === 0) {
                str += "<td rowspan='9'><div id='bar'></div></td>";
            }
            str += "<td class='barline'>" + v + "</td></tr>";
            c++;
        }
        tab.html(str);
    }

    function CreateColorSchema(name, allTextLines, sep) {
        var maxLines = allTextLines.length;
        var headerRow = [];
        var headerIdxAndCatIdx = [];
        var schema = {};

        for (var i = 0; i < maxLines; i++) {
            var line = allTextLines[i];
            var lineParts = line.split(sep);
            var e = null;
            var degree = 0;

            if (line === "") {
                continue;
            }

            for (var x = 0; x < lineParts.length; x++) {
                var t = lineParts[x];
                if (i === 0 && t !== "") {
                    headerRow.push(t);
                    schema[t] = {};
                }
                if (i !== 0 && x === 0) {
                    degree = parseInt(t, 10);
                } else if (i !== 0 && x !== 0) {
                    var head = headerRow[x - 1];
                    schema[head][degree] = t;
                }
            }
        }
        AddSchemaStyle(name, schema);
    }

    function CreateColorSchema2(name, allTextLines, sep, order) {
        var maxLines = allTextLines.length;
        var headerRow = [];
        var headerIdxAndCatIdx = [];
        var schema = {};

        for (var i = 0; i < maxLines; i++) {
            var line = allTextLines[i];
            var lineParts = line.split(sep);
            var e = null;
            var degree = 0;

            if (line === "") {
                continue;
            }

            var cat = null;
            for (var x = 0; x < lineParts.length; x++) {
                var t = lineParts[x];
                if (x === 0) {
                    cat = t;
                    schema[cat] = {};
                } else if (x !== 0) {
                    if (order === "asc") {
                        schema[cat][x] = t;
                    } else if (order === "desc") {
                        schema[cat][lineParts.length - x] = t;
                    }
                }
            }
        }
        AddSchemaStyle(name, schema);
    }

    function AddSchemaStyle(name, schema) {
        var style = "<style>";

        for (var cat in schema) {
            var c = schema[cat];
            for (var d in c) {
                var color = c[d];
                style += GetSingleStyle(name, cat, d, color);
            }
        }

        style += "</style>";

        $("head").append(style);
        _x.log("create schema " + name);
    }

    function GetSingleStyle(name, cat, degree, color) {
        var hexColor = color.indexOf("#") === 0 ? color : "#" + color;
        return "body." + name + " .sector." + cat + " .Histogram.Degree-" + degree + "{ fill: " + hexColor + "; }";
    }

    function CreateLegend(name, allTextLines, sep) {
        //fill Legend Data
        var maxLines = allTextLines.length;
        var headerRow = [];
        var headerIdxAndCatIdx = [];
        var data = {};

        for (var i = 0; i < maxLines; i++) {
            var line = allTextLines[i];
            var lineParts = line.split(sep);
            var e = null;

            if (line === "") {
                continue;
            }

            var val = null;
            for (var x = 0; x < lineParts.length; x++) {
                var t = lineParts[x];
                if (x === 0) {
                    val = t;
                    data[val] = null;
                } else if (x !== 0) {
                    data[val] = t;
                }
            }
        }
        if (_x.LegendData === null) {
            _x.LegendData = {};
        }
        _x.LegendData[name] = data;

        //create style
        AddStyleForLegend(name, data);

    }

    function AddStyleForLegend(name, data) {
        //background: -moz-linear-gradient(top,  #1e5799 0%, #2989d8 50%, #207cca 51%, #7db9e8 100%);
        //background: -ms-linear-gradient(top,  #1e5799 0%,#2989d8 50%,#207cca 51%,#7db9e8 100%); /* IE10+ */
        //background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#1e5799), color-stop(50%,#2989d8), color-stop(51%,#207cca), color-stop(100%,#7db9e8)); /* Chrome,Safari4+ */
        //background: -webkit-linear-gradient(top,  #1e5799 0%,#2989d8 50%,#207cca 51%,#7db9e8 100%); /* Chrome10+,Safari5.1+ */
        //background: -o-linear-gradient(top,  #1e5799 0%,#2989d8 50%,#207cca 51%,#7db9e8 100%); /* Opera 11.10+ */

        var style = "<style>";

        style += "body." + name + " #bar{ ";


        //convert values to percent and use for legend-background-gradient
        var max = null;
        for (var d in data) {
            if (max === null || max < d) {
                max = d;
            }
        }
        //var arr = { "0%": "#1e5799", "50%": "#2989d8", "100%": "#7db9e8" };
        var arr = {};
        var c = 0;
        for (var x in data) {
            var color = data[x];
            //var per = (x / max * 100);
            per = (c * 11.11).toFixed(0);
            arr[per + "%"] = color;
            c++;
        }

        style += CreateBackground(arr, "moz");
        style += CreateBackground(arr, "ms");
        style += CreateBackground(arr, "o");
        style += CreateBackground(arr, "webkit");

        style += "}</style>";

        $("head").append(style);
        _x.log("create legend " + name);
    }

    function CreateBackground(data, browser) {
        var str = "";

        str += " background: -" + browser + "-linear-gradient(top";

        for (var v in data) {
            var c = data[v];
            c = c.indexOf("#") === 0 ? c : "#" + c;
            str += ", " + c + " " + v;
        }
        str += ");";

        if (browser === "webkit") {
            ////background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#1e5799), color-stop(50%,#2989d8), color-stop(51%,#207cca), color-stop(100%,#7db9e8)); /* Chrome,Safari4+ */
            str += " background: -" + browser + "-gradient(linear, left top, left bottom";
            for (var x in data) {
                var c = data[x];
                c = c.indexOf("#") === 0 ? c : "#" + c;
                str += ", color-stop(" + x + "," + c + ")";
            }
            str += ");";
        }

        return str;
    }


    return _x;
}(window, document, jQuery));

