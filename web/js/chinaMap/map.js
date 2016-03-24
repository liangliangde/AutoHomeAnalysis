var container,
    width = 800,
    height = 600,
    colors;
var mouseDuration = 250,        //鼠标移动动画时长
    duration = 1000;

var chinaG                      //包含中国地图的group
    ;
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
        chinaG = container.append("g");
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
    d3.select("#chinaMap").append("svg")
        .attr("id", id)
        .attr("width", w)
        .attr("height", h)
        .style("position", "absolute")
    ;
}

//显示标题
function showTitle(gkData){
    container.append("text")
        .attr("x", width/2)
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
        .range([d3.rgb(75, 108, 156),d3.rgb(211, 229, 255)]);
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
    //showOnTable(data);
}

/**
 * 创建提示条
 * 提示的创建大致有3种方式
 * 1： 给svg元素里面增加一个title元素，
 *     var t = d3.select(id).append("title").text("我是提示条");
 *      这种方法效果不大理想，而且提示单调
 *
 * 2： 给需要提示的元素添加mouseover， mouseout 事件，当鼠标在该元素上移动时，就显示提示条（动态创建的svg元素），如：
 *      var t = d3.select(id);
 *      t.on('mouseover',function(){
     *          //创建提示条
                svg.append("text")
                  .attr("id", "tooltip")
                  .attr("x", d3.event.x)
                  .attr("y", d3.event.y)
                  .attr("text-anchor", "middle")
                  .attr("font-family", "sans-serif")
                  .attr("font-size", "11px")
                  .attr("font-weight", "bold")
                  .attr("ﬁll", "black")
                  .text("我是svg的提示条");
                })
 *      });
 *
 * 3： 类似方法2，但是提示条不是svg元素，而是普通的html元素（如div），动态修改提示框里面的内容跟提示框的x，y坐标
 *      达到提示的效果，总体来说这个方法较好，较为灵活，而且可以使用css3，同时不用担心提示框超出svg范围的问题
 *
 *      所以，在教程中，都是使用这个方法
 */
function buildTip(data){
    var t = "#tooltip";
    chinaG.selectAll("path")
        .data(data, function(d){
            return d.id;
        })
        .on("mouseover",function(d){
            d3.select(t)
                .style("left", d3.event.x + "px")
                .style("top", d3.event.y + "px")
                .classed("hidden", false)
                .selectAll(".dataHolder")[0]
                .forEach(function(h){
                    h = d3.select(h);
                    h.html(d[h.attr('name')]);
                })
            ;
            d3.select(this)
                .attr("opacity", 0.8);
        })
        .on("mouseout",function(){
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
        if (location ==="其它" || _x.Entries[i].Cats.indexOf(category.Name) == -1) {
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
        //.defer(d3.json, "data/2013GKLQL.json")
        .await(function (error, china) {
            if (error) {
                alert("加载数据出错！" + error);
                return;
            }
            drawChina(china);

            gkData = getMapData(category, _x);
            if (gkData) {
                //showTitle(gkData);
                sortByTotal(gkData);
            }
        });
}
/**
 * Created by llei on 16-3-24.
 */
