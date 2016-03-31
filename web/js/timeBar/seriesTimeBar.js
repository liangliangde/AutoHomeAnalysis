function showSeriesTimeBar(category) {
    $("#seriesTimeBar").empty();
    var width = 800;	//SVG绘制区域的宽度
    var height = 300;	//SVG绘制区域的高度
    var monthlimit = 13;
    //外边框
    var padding = {left: 50, right: 280, top: 30, bottom: 50};

    var svg = d3.select("#seriesTimeBar")			//选择<body>
        .append("svg")			//在<body>中添加<svg>
        .attr("width", width)	//设定<svg>的宽度属性
        .attr("height", height);//设定<svg>的高度属性

    //1. 确定初始数据
    //var dataset = [
    //    {
    //        name: "Software",
    //        sales: [{year: 2005, profit: 1100},
    //            {year: 2006, profit: 1700},
    //            {year: 2007, profit: 1680},
    //            {year: 2008, profit: 4000},
    //            {year: 2009, profit: 4900}]
    //    }
    //];

    var dataset = [];
    var styleLimit = Math.min(category.StyleList.length, 6);
    for (var i = 0; i < styleLimit; i++) {
        var data = {};
        data["name"] = category.StyleList[i].Name;
        data["sales"] = [];
        var Sale = category.StyleList[i].Sale;
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
            for (var j = 0; j < Sale.length; j++) {
                if (Sale[j].boughtTime === curTimeNum) {

                    data["sales"].push({"year": year + "," + month, "profit": Sale[j].SaleNum});
                    break;
                }
                if (j == Sale.length - 1) {
                    data["sales"].push({"year": year + "," + month, "profit": 0});
                }
            }
            curTimeNum++;
        }
        dataset.push(data);
    }

    //2. 转换数据
    var stack = d3.layout.stack()
        .values(function (d) {
            return d.sales;
        })
        .x(function (d) {
            return d.year;
        })
        .y(function (d) {
            return d.profit;
        });

    var data = stack(dataset);

    console.log(data);


    //3. 绘制


    //创建x轴比例尺
    var xRangeWidth = width - padding.left - padding.right;

    var xScale = d3.scale.ordinal()
        .domain(data[0].sales.map(function (d) {
            return d.year;
        }))
        .rangeBands([0, xRangeWidth], 0.3);

    //创建y轴比例尺

    //最大利润（定义域的最大值）
    var maxProfit = d3.max(data[data.length - 1].sales, function (d) {
        return d.y0 + d.y;
    });

    //最大高度（值域的最大值）
    var yRangeWidth = height - padding.top - padding.bottom;

    var yScale = d3.scale.linear()
        .domain([0, maxProfit])		//定义域
        .range([0, yRangeWidth]);	//值域


    //颜色比例尺
    var color = d3.scale.linear()
        .domain([0, styleLimit - 1])
        .range(["#aad", "#556"]);

    //添加分组元素
    var groups = svg.selectAll("g")
        .data(data)
        .enter()
        .append("g")
        .style("fill", function (d, i) {
            return color(i);
        });

    //添加矩形
    var rects = groups.selectAll("rect")
        .data(function (d) {
            return d.sales;
        })
        .enter()
        .append("rect")
        .attr("x", function (d) {
            return xScale(d.year);
        })
        .attr("y", function (d) {
            return yRangeWidth - yScale(d.y0 + d.y);
        })
        .attr("width", function (d) {
            return xScale.rangeBand();
        })
        .attr("height", function (d) {
            return yScale(d.y);
        })
        .attr("transform", "translate(" + padding.left + "," + padding.top + ")");

    //添加坐标轴
    var xAxis = d3.svg.axis()
        .scale(xScale)
        .orient("bottom");

    yScale.range([yRangeWidth, 0]);

    var yAxis = d3.svg.axis()
        .scale(yScale)
        .orient("left");

    svg.append("g")
        .attr("class", "axis")
        .attr("transform", "translate(" + padding.left + "," + (height - padding.bottom) + ")")
        .call(xAxis)
        .selectAll("text")
        .attr("dy", ".35em")
        .attr("transform", "rotate(45)")
        .style("text-anchor", "start");

    svg.append("g")
        .attr("class", "axis")
        .attr("transform", "translate(" + padding.left + "," + (height - padding.bottom - yRangeWidth) + ")")
        .call(yAxis);

    ////添加分组标签
    var labHeight = 16;
    var labRadius = 10;

    var labelCircle = groups.append("rect")
        .attr("x", function (d) {
            return width - padding.right * 0.9;
        })
        .attr("y", function (d, i) {
            return padding.top * 2 + (labHeight + 8) * i;
        })
        .attr("width", labHeight)
        .attr("height", labHeight);
    //.attr("cx",function(d){ return width - padding.right*0.98; })
    //.attr("cy",function(d,i){ return padding.top * 2 + labHeight * i; })
    //.attr("r",labRadius);

    var labelText = groups.append("text")
        .attr("x", function (d) {
            return width - padding.right * 0.9 + labHeight * 1.2;
        })
        .attr("y", function (d, i) {
            return padding.top * 2 + (labHeight + 8) * i + labHeight / 2;
        })
        .attr("dy", labRadius / 2)
        .attr("fill", "black")
        .text(function (d) {
            return d.name;
        });

}
