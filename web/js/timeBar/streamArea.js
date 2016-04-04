var area, areas, color, createLegend, data, display, duration, areaheight, hideLegend, line, showLegend, stack, stackedAreas, start, streamgraph, svg, transitionTo, areawidth, x, xAxis, y;
var monthlimit = 13;
var padding = {left: 50, right: 280, top: 30, bottom: 0};
var leftIndiant = 20;
var rightIndiant = 0;
var totalStreamWidth = 790;

areawidth = totalStreamWidth - padding.right - leftIndiant - rightIndiant;

areaheight = 200 - padding.bottom;

duration = 250;

x = d3.time.scale().range([padding.left + leftIndiant, areawidth]);

y = d3.scale.linear().range([areaheight, 0]);

color = d3.scale.category20c();

area = d3.svg.area().interpolate("basis").x(function (d) {
    return x(d.date);
});

line = d3.svg.line().interpolate("basis").x(function (d) {
    return x(d.date);
});

stack = d3.layout.stack().values(function (d) {
    return d.values;
}).x(function (d) {
    return d.date;
}).y(function (d) {
    return d.count;
}).out(function (d, y0, y) {
    return d.count0 = y0;
}).order("reverse");

xAxis = d3.svg.axis().scale(x).tickSize(areaheight).tickFormat(d3.time.format('%a %d'));

data = null;

svg = d3.select("#seriesTimeBarDown")
    .append("svg")
    .attr("width", totalStreamWidth)
    .attr("height", areaheight + padding.bottom);

transitionTo = function (name) {
    if (name === "stream") {
        streamgraph();
    }
    if (name === "stack") {
        stackedAreas();
    }
    if (name === "area") {
        return areas();
    }
};

start = function () {
    var dates, g, index, maxDate, minDate, requests;
    minDate = d3.min(data, function (d) {
        return d.values[0].date;
    });
    maxDate = d3.max(data, function (d) {
        return d.values[d.values.length - 1].date;
    });
    x.domain([minDate, maxDate]);
    dates = data[0].values.map(function (v) {
        return v.date;
    });
    xAxis.tickValues(dates);
    svg.append("g").attr("class", "x axis").attr("transform", "translate(" + 0 + "," + areaheight + ")").call(xAxis);
    area.y0(areaheight / 2).y1(areaheight / 2);
    g = svg.selectAll(".request").data(data).enter();
    requests = g.append("g").attr("class", "request").attr("transform", "translate(" + 0 + ",0)");
    requests.append("path").attr("class", "area").style("fill", function (d) {
        return color(d.key);
    }).attr("d", function (d) {
        return area(d.values);
    });
    requests.append("path").attr("class", "line").style("stroke-opacity", 1e-6);

    var labHeight = 16;
    var labRadius = 10;
    requests.append("rect")
        .attr("x", function (d) {
            return totalStreamWidth - padding.right * 0.9;
        })
        .attr("y", function (d, i) {
            return padding.top * 0.7 + (labHeight + 8) * i;
        })
        .attr("width", labHeight)
        .attr("height", labHeight)
        .style("fill", function (d) {
            return color(d.key);
        });
    requests.append("text")
        .attr("x", function (d) {
            return totalStreamWidth - padding.right * 0.9 + labHeight * 1.2;
        })
        .attr("y", function (d, i) {
            return padding.top * 0.7 + (labHeight + 8) * i + labHeight / 2;
        })
        .attr("dy", labRadius / 2)
        .attr("fill", "black")
        .text(function (d) {
            return d.key;
        });
    //createLegend();
    return streamgraph();
};

streamgraph = function () {
    var t;
    stack.offset("wiggle");
    stack(data);
    y.domain([
        0, d3.max(data[0].values.map(function (d) {
            return d.count0 + d.count;
        }))
    ]).range([areaheight, 0]);
    line.y(function (d) {
        return y(d.count0);
    });
    area.y0(function (d) {
        return y(d.count0);
    }).y1(function (d) {
        return y(d.count0 + d.count);
    });
    t = svg.selectAll(".request").transition().duration(duration);
    t.select("path.area").style("fill-opacity", 1.0).attr("d", function (d) {
        return area(d.values);
    });
    return t.select("path.line").style("stroke-opacity", 1e-6).attr("d", function (d) {
        return line(d.values);
    });
};

stackedAreas = function () {
    var t;
    stack.offset("zero");
    stack(data);
    y.domain([
        0, d3.max(data[0].values.map(function (d) {
            return d.count0 + d.count;
        }))
    ]).range([areaheight, 0]);
    line.y(function (d) {
        return y(d.count0);
    });
    area.y0(function (d) {
        return y(d.count0);
    }).y1(function (d) {
        return y(d.count0 + d.count);
    });
    t = svg.selectAll(".request").transition().duration(duration);
    t.select("path.area").style("fill-opacity", 1.0).attr("d", function (d) {
        return area(d.values);
    });
    return t.select("path.line").style("stroke-opacity", 1e-6).attr("d", function (d) {
        return line(d.values);
    });
};

areas = function () {
    var g, t;
    g = svg.selectAll(".request");
    line.y(function (d) {
        return y(d.count0 + d.count);
    });
    g.select("path.line").attr("d", function (d) {
        return line(d.values);
    }).style("stroke-opacity", 1e-6);
    y.domain([
        0, d3.max(data.map(function (d) {
            return d.maxCount;
        }))
    ]).range([areaheight, 0]);
    area.y0(areaheight).y1(function (d) {
        return y(d.count);
    });
    line.y(function (d) {
        return y(d.count);
    });
    t = g.transition().duration(duration);
    t.select("path.area").style("fill-opacity", 0.5).attr("d", function (d) {
        return area(d.values);
    });
    return t.select("path.line").style("stroke-opacity", 1).attr("d", function (d) {
        return line(d.values);
    });
};

showLegend = function (d, i) {
    return d3.select("#legend svg g.panel").transition().duration(500).attr("transform", "translate(0,0)");
};

hideLegend = function (d, i) {
    return d3.select("#legend svg g.panel").transition().duration(500).attr("transform", "translate(165,0)");
};

createLegend = function () {
    var keys, legend, legendG, legendHeight, legendWidth;
    legendWidth = 200;
    legendHeight = 245;
    legend = d3.select("#legend").append("svg").attr("width", legendWidth).attr("height", legendHeight);
    legendG = legend.append("g").attr("transform", "translate(165,0)").attr("class", "panel");
    legendG.append("rect").attr("width", legendWidth).attr("height", legendHeight).attr("rx", 4).attr("ry", 4).attr("fill-opacity", 0.5).attr("fill", "white");
    legendG.on("mouseover", showLegend).on("mouseout", hideLegend);
    keys = legendG.selectAll("g").data(data).enter().append("g").attr("transform", function (d, i) {
        return "translate(" + 5. + "," + (10 + 40 * (i + 0)) + ")";
    });
    keys.append("rect").attr("width", 30).attr("height", 30).attr("rx", 4).attr("ry", 4).attr("fill", function (d) {
        return color(d.key);
    });
    return keys.append("text").text(function (d) {
        return d.key;
    }).attr("text-anchor", "left").attr("dx", "2.3em").attr("dy", "1.3em");

};

display = function (data) {
    var parseTime;
    parseTime = d3.time.format.utc("%x").parse;
    data.forEach(function (s) {
        s.values.forEach(function (d) {
            d.date = parseTime(d.date);
            return d.count = parseFloat(d.count);
        });
        return s.maxCount = d3.max(s.values, function (d) {
            return d.count;
        });
    });
    data.sort(function (a, b) {
        return b.total - a.total;
    });
    return start();
};

function extractData(CatList) {
    var dataArr = [];
    for (var i = 0; i < CatList.length; i++) {
        var _data = {};
        _data["key"] = CatList[i].Name;
        _data["total"] = CatList[i].Count;
        _data["values"] = [];
        var curTimeNum = 1;
        while (curTimeNum <= monthlimit) {
            var year, month;
            if (curTimeNum > 12) {
                year = 2016;
                month = curTimeNum - 12;
            }
            else {
                year = 2015;
                month = curTimeNum;
            }
            var count = 0;
            var value = {"date": month + "/01" + "/" + year};
            for (var j = 0; j < CatList[i].StyleList.length; j++) {
                var Sale = CatList[i].StyleList[j].Sale;
                for (var k = 0; k < Sale.length; k++) {
                    if (Sale[k].boughtTime === curTimeNum) {
                        count += Sale[k].SaleNum;
                    }
                }
            }
            value["count"] = count;
            _data["values"].push(value);
            curTimeNum++;
        }
        dataArr.push(_data);
    }
    return dataArr;
}

function showStreamArea(CatList) {
    d3.selectAll(".switch").on("click", function (d) {
        var id;
        d3.event.preventDefault();
        id = d3.select(this).attr("id");
        return transitionTo(id);
    });
    //return d3.json("data/requests.json", display);
    data = extractData(CatList);
    display(data);
}

// ---
// generated by coffee-script 1.9.2