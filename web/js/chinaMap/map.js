var container,
    width = 750,
    height = 550,
    colors;
var mouseDuration = 250,        //鼠标移动动画时长
    duration = 1000;

var chinaG;                      //包含中国地图的group
var setting = {
    countryColor: d3.scale.linear()
        .domain([1, 34])
        .range([d3.rgb(255, 255, 180), d3.rgb(130, 140, 20)]),
    strokeColor: "#6a6a6a"
}

// Project from latlng to pixel coords
//使用墨卡托投影
var projection = d3.geo.mercator()
        .scale(width / 1.1)                                   //对地图进行缩放
        .translate([width / 2, height / 2])                 //将地图平移到屏幕中间
        .rotate([-110, 0])
        .center([-5, 40.5])                                  //设置中心点，调整到屏幕中心
    ;

// Draw geojson to svg path using the projection
var path = d3.geo.path().projection(projection);

//画出中国地图
function drawChina(ds) {
    if (!chinaG)
        chinaG = container.append("g").attr("id", "province");
    chinaG.selectAll("path")
        .data(ds.features)
        .enter()
        .insert("path")
        .attr("id", function (d) {
            return d.id;
        })
        .attr("fill", "#000000")
        .attr("d", path)
        .attr('stroke', setting.strokeColor)
        .attr('stroke-width', '0.7px')
    ;
}

/**
 * 获取0到max之间的随机整数
 */
function random(max) {
    return (Math.random() * max).toFixed(0);
}
function px(v) {
    if (typeof v === 'string')
        return v.replace("px", '');
    return v + "px";
}

/**
 * 创建svg
 */
function createSVG(id, w, h) {
    if ($("#chinaMap").children("svg").length > 0) {
        return;
    }
    d3.select("#chinaMap").append("svg:svg")
        .attr("id", id)
        .attr("width", w)
        .attr("height", h)
        .style("position", "absolute")
    ;
}

//显示标题
function showTitle(gkData) {
    container.append("text")
        .attr("x", width / 2)
        .attr("y", 100)
        .attr("text-anchor", "middle")
        .attr("font-family", "sans-serif")
        .attr("font-size", "20px")
        .attr("font-weight", "bold")
        .attr("fill", "black")
        .text(gkData._title)
    ;
}

/**
 * 根据参加高考人数排序
 */
function sortByTotal(gkData) {
    //首先我们需要对数据进行录取率从大到小的排序
    //因为rate 是 xx.xx% 的格式，所以在对比前需要进行parseFloat 的操作
    var data = gkData.datas.sort(function (d1, d2) {
        return d2.total - d1.total;
    });

    //创建过度颜色,注意上一步的排序是从大到小，那么颜色应该是从深到浅
    var rateColors = d3.scale.linear()
        .domain([1, 340])
        .range([d3.rgb(75, 108, 156), d3.rgb(211, 229, 255)]);
    //.range([d3.rgb(20, 120, 140), d3.rgb(180, 230, 255)]);
    /*
     遍历上一步得到是数组
     forEach 参数中的 d 就是遍历到的某个数据， i 就是该对象的下标序号，从0开始
     */
    data.forEach(function (d, i) {
        d.sort = i + 1;
        //通过d.id 来获取中国地图上对应的省份，因为地图中的省份块是根据省份拼音命名的
        d3.select("#" + d.id)
            .transition()
            .duration(duration)
            .delay(10 * i)
            .attr("fill", rateColors((i + 1) * 10))
        ;
    });

    buildTip(data);
}

function fillColorOfProvince(gkData) {
    var maxTotal = 0;
    for (var i = 0; i < gkData.datas.length; i++) {
        maxTotal = gkData.datas[i].total > maxTotal ? gkData.datas[i].total : maxTotal;
    }
    var rangeCount = 10;
    var rateColors = d3.scale.linear()
        .domain([1, rangeCount])
        .range([d3.rgb(205, 223, 251), d3.rgb(75, 108, 156)]);
    //.range([d3.rgb(211, 229, 255), d3.rgb(75, 108, 156)]);
    /*
     遍历上一步得到是数组
     forEach 参数中的 d 就是遍历到的某个数据， i 就是该对象的下标序号，从0开始
     */
    gkData.datas.forEach(function (d, i) {
        //d.sort = i + 1;
        //通过d.id 来获取中国地图上对应的省份，因为地图中的省份块是根据省份拼音命名的
        d3.select("#" + d.id)
            .transition()
            .duration(duration)
            .delay(10 * i)
            .attr("fill", rateColors(Math.ceil(d.total / 100)))
        ;
    });

    buildTip(gkData.datas);
}

function buildTip(data) {
    var t = "#tooltip";
    chinaG.selectAll("path")
        .data(data, function (d) {
            return d.id;
        })
        .on("mouseover", function (d) {
            d3.select(t)
                .style("left", d3.event.x + "px")
                .style("top", d3.event.y + "px")
                .classed("hidden", false)
                .selectAll(".dataHolder")[0]
                .forEach(function (h) {
                    h = d3.select(h);
                    h.html(d[h.attr('name')]);
                })
            ;
            d3.select(this)
                .attr("opacity", 0.8);
        })
        .on("mouseout", function () {
            d3.select(t).classed("hidden", true);
            d3.select(this)
                .attr("opacity", 1);
        })
    ;
}

function getMapData(category, _x) {
    mapData = {};
    mapData["_title"] = category.Name;
    mapData["datas"] = [];
    for (var i = 0; i < _x.Entries.length; i++) {
        var location = _x.Entries[i].location;
        if (location === "其它" || _x.Entries[i].Cats.indexOf(category.Name) == -1) {
            continue;
        }
        var findLocation = false;
        for (var j = 0; j < mapData["datas"].length; j++) {
            if (mapData["datas"][j].id === location) {
                mapData["datas"][j].total += 1;
                findLocation = true;
                break;
            }
        }
        if (findLocation == false) {
            mapData["datas"].push({"id": location, "total": 1});
        }
    }
    return mapData;
}

function extractCity(china) {
    var cityData = [];
    for (var i = 0; i < china.features.length; i++) {
        var province = {};
        province["g"] = parseFloat(china.features[i].X);
        province["l"] = parseFloat(china.features[i].Y);
        province["name"] = china.features[i].id;
        cityData.push(province);
    }
    return cityData;
}

function drawCircle(cityData, boughtLineList, AVGPriceofProvince) {

    var countByCity = {},
        locationByCity = {},
        positions = [],
        linksByOrigin = {};

    $("#container").children("#cells").remove();
    $("#container").children("#circles").remove();

    var circles = d3.select("#container").append("svg:g")
        .attr("id", "circles");

    var cells = d3.select("#container").append("svg:g")
        .attr("id", "cells");

    var arc = d3.geo.greatArc()
        .source(function (d) {
            return locationByCity[d.source];
        })
        .target(function (d) {
            return locationByCity[d.target];
        });

    boughtLineList.forEach(function (boughtLine) {
        var boughtSite = boughtLine.boughtSite,
            userLoc = boughtLine.userLoc,
            links = linksByOrigin[boughtSite] || (linksByOrigin[boughtSite] = []);
        links.push({source: boughtSite, target: userLoc, num: boughtLine.num});
        countByCity[boughtSite] = (countByCity[boughtSite] || 0) + boughtLine.num;
        countByCity[userLoc] = (countByCity[userLoc] || 1);
    });

    cityData = cityData.filter(function (city) {
        if (countByCity[city.name]) {
            var location = [+city.g, +city.l];
            locationByCity[city.name] = location;
            positions.push(projection(location));
            for (var i = 0; i < AVGPriceofProvince.length; i++) {
                if (AVGPriceofProvince[i].province === city.name) {
                    city.avgPrice = AVGPriceofProvince[i].avgPrice;
                    city.boughtNum = AVGPriceofProvince[i].boughtNum;
                }
            }
            return true;
        }
    });


    var polygons = d3.geom.voronoi(positions);

    var g = cells.selectAll("g")
        .data(cityData)
        .enter().append("svg:g");

    g.append("svg:path")
        .attr("class", "cell")
        .attr("d", function (d, i) {
            return "M" + polygons[i].join("L") + "Z";
        })
        //.on("mouseover", function (d, i) {
        //    d3.select("h2 span").text(d.name);
        //});

    g.selectAll("path.arc")
        .data(function (d) {
            return linksByOrigin[d.name] || [];
        })
        .enter().append("svg:path")
        .attr("class", "arc")
        .attr("stroke-width", function (d) {
            if (d.num > 3) {
                return d.num;
            }
            return 0;
        })
        .attr("d", function (d) {
            //console.log(d)
            if(!(d.target == "澳门" || d.source=="澳门"))
                return path(arc(d));
        });

    circles.selectAll("circle")
        .data(cityData)
        .enter();

    var g2 = circles.selectAll("g")
        .data(cityData)
        .enter().append("svg:g");

    g2.append("svg:circle")
        .attr("cx", function (d, i) {
            return positions[i][0];
        })
        .attr("cy", function (d, i) {
            return positions[i][1];
        })
        .attr("r", function (d, i) {
            return Math.sqrt(countByCity[d.name])/2;
        });

    g2.append("svg:text")
        //.data(cityData)
        .attr("transform", function (d, i) {
            return "translate(" + (positions[i][0]) + ',' + (positions[i][1] - 8) + ")";
        })
        .attr("dy", ".35em")
        .attr("text-anchor", function (d) {
            return "middle";
        })
        .text(function (d, i) {
            if (d.boughtNum > 20) {
                return d.avgPrice + "(" + d.boughtNum + ")";
            }
        });
}

function showChinaMap(category, _x) {
    var gkData;
    //同时生成随机的颜色值
    colors = d3.scale.category20b();

    createSVG("container", width, height);
    /*
     赋值给container
     同时调用style()方法给container设置相应的style属性
     */
    container = d3.select("#container");

    queue()
        .defer(d3.json, "data/china.json")
        .await(function (error, china) {
            if (error) {
                alert("加载数据出错！" + error);
                return;
            }
            drawChina(china);

            gkData = getMapData(category, _x);
            if (gkData) {
                //showTitle(gkData);
                $("#mapHeader").text(gkData._title + "地域分布图");
                //sortByTotal(gkData);
                fillColorOfProvince(gkData);
            }

            var citydata = extractCity(china);

            drawCircle(citydata, category.BoughtLineList, category.AVGPriceOfProvince);
        });
}
/**
 * Created by llei on 16-3-24.
 */
