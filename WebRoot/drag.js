var param = {
    mapUrl: null,
    layout: null,
    extent: null,
    title: null,
    mapFrameId: null,
    XXrr:null
};

var param3 = {
	    sds:null,
	    sfdsf:23,
	    layout: null
	   
	};
$(function () {
    loadMap();
    var $box = $('.box').mousedown(function (e) {
        $(this).parent().children().css({
            "zIndex": "0"
        });
        $(this).css({
            "zIndex": "1"
        });
        var position = $(this).position();
        this.posix = {
            'x': e.pageX - position.left,
            'y': e.pageY - position.top,
            'w': $(this).width() + 2 * parseInt($(this).css('border-width')),
            'h': $(this).height() + 2 * parseInt($(this).css('border-width'))
        };
        //如果鼠标按下的是box，document.move_target为this.box，否则为null
        $.extend(document, {
            'move': true,
            'move_target': this
        });
        //console.log(document.move_target)
    }).on('mousedown', '.coor', function (e) {
        var father = $(this).parent();
        father.parent().children().css({
            "zIndex": "0"
        });
        father.css({
            "zIndex": "1"
        });
        //resizePos是鼠标按下时的位置坐标和box的高宽
        var resizePos = {
            'w': father.width() + 2 * parseInt(father.css('border-width')),
            'h': father.height() + 2 * parseInt(father.css('border-width')),
            'x': e.pageX,
            'y': e.pageY,
            'l': father.position().left,
            't': father.position().top
        };
        $.extend(document, {
            'move': true,
            'resize': function (e) {
                //最大宽（高）度
                var maxWidth = 800 - resizePos.l - 12,
                    maxHeight = 500 - resizePos.t - 11;
                //根据屏幕坐标算出来的宽（高）度和最终设置的新宽（高）度
                var width, newWidth, height, newHeight;
                width = Math.min(maxWidth, e.pageX - resizePos.x + resizePos.w);
                height = Math.min(maxHeight, e.pageY - resizePos.y + resizePos.h);
                //假设屏幕坐标算出来的宽度小于最大宽度
                newWidth = width;
                height = newWidth / resizePos.w * resizePos.h;
                newHeight = height;
                //如果height达到了最大值，就按照height来计算width
                if (height > maxHeight && width <= maxWidth) {
                    newHeight = maxHeight;
                    width = newHeight / resizePos.h * resizePos.w;
                    newWidth = width;
                }
                //如果width达到了最大值，就按照width来计算height
                else if (width > maxWidth && height <= maxHeight) {
                    newWidth = maxWidth;
                    height = newWidth / resizePos.w * resizePos.h;
                    newHeight = height;
                }
                father.css({
                    'width': newWidth,
                    'height': newHeight
                });
            }
        });
        return false;
    });

    $(document).mousemove(function (e) {
        if (!!this.move) {
            var posix = !document.move_target ? {
                'x': 0,
                'y': 0
            } : document.move_target.posix;
            //callback是决定mousemove是改变位置还是改变大小
            var callback = document.resize || function () {
                    var top = e.pageY - posix.y,
                        left = e.pageX - posix.x
                    //top和left的最大最小值
                    var minTop = 11,
                        minLeft = 12,
                        maxLeft = 800 - posix.w - minLeft,
                        maxTop = 500 - posix.h - minTop;
                    //x不能超过主图区范围
                    if (left < maxLeft) {
                        $(this.move_target).css({
                            'left': Math.max(minLeft, left)
                        });
                    } else if (left >= maxLeft) {
                        $(this.move_target).css({
                            'left': maxLeft
                        });
                    }
                    //y不能超过主图区范围
                    if (top < maxTop) {
                        $(this.move_target).css({
                            'top': Math.max(minTop, top)
                        });
                    } else if (top >= maxTop) {
                        $(this.move_target).css({
                            'top': maxTop
                        });
                    }
                };
            callback.call(this, e, posix);
        }
    }).mouseup(function (e) {
        if (!!this.move) {
            var callback = document.call_up || function () {
                };
            callback.call(this, e);
            $.extend(this, {
                'move': false,
                'move_target': null,
                'resize': false,
                'call_up': false
            });
        }
        refresh()
    });

    //将各个div的坐标存入layout

    function refresh() {
        var layout = {
            "MODEL_NAME": "测试",
            //MAP最外层的纸张图框
            "MAP_X": 0,
            "MAP_Y": 0,
            "MAP_WIDTH": parseInt($("#paper").width()),
            "MAP_HEIGHT": parseInt($("#paper").height()),
            //图廓"ID": "mainMap"
            "MAPFRAME_X": $("#mapFrame").position().left,
            "MAPFRAME_Y": $("#mapFrame").position().top,
            "MAPFRAME_WIDTH": parseInt($("#mapFrame").width()),
            "MAPFRAME_HEIGHT": parseInt($("#mapFrame").height()),
            "METALOCATION_X": 0,
            "METALOCATION_Y": 0,
            "META_WIDTH": 0,
            "META_HEIGHT": 0,
            //地图
            "MAINMAPLOCATION_X": parseInt($("#mainMap").position().left),
            "MAINMAPLOCATION_Y": parseInt($("#mainMap").position().top),
            "MAINMAPEXTENT_W": parseInt($("#mainMap").width()),
            "MAINMAPEXTENT_H": parseInt($("#mainMap").height()),
            //"ID": "mapName",
            "TITLELOCATION_X": parseInt($("#mapName").position().left),
            "TITLELOCATION_Y": parseInt($("#mapName").position().top + $("#mapName").height()),
            "TITLEEXTENT_W": parseInt($("#mapName").width()),
            "TITLEEXTENT_H": parseInt($("#mapName").height()),
            //"ID": "legend",
            "LEGENDLOCATION_X": parseInt($("#legend").position().left),
            "LEGENDLOCATION_Y": parseInt($("#legend").position().top),
            "LEGENDEXTENT_W": parseInt($("#legend").width()),
            "LEGENDEXTENT_H": parseInt($("#legend").height()),
            //"ID": "chart",
            "STACHARTLOCATION_X": parseInt($("#chart").position().left),
            "STACHARTLOCATION_Y": parseInt($("#chart").position().top),
            "STACHARTEXTENT_W": parseInt($("#chart").width()),
            "STACHARTEXTENT_H": parseInt($("#chart").height()),
            //"ID": "scale",
            "SCALELOCATION_X": parseInt($("#scale").position().left),
            "SCALELOCATION_Y": parseInt($("#scale").position().top),
            "SCALE_WIDTH": parseInt($("#scale").width()),
            "SCALE_HEIGHT": parseInt($("#scale").height()),
            //"ID": "compass",
            "COMPASSLOCATION_X": parseInt($("#compass").position().left),
            "COMPASSLOCATION_Y": parseInt($("#compass").position().top),
            "COMPASS_WIDTH": parseInt($("#compass").width()),
            "COMPASS_HEIGHT": parseInt($("#compass").height()),
        };
        param.layout = JSON.stringify(layout)
        console.log(JSON.stringify(layout))
    }
});

//添加底图
function loadMap() {
    require(["esri/map", "dojo/domReady!"], function (Map) {
        var map = new Map("mainMap", {
            slider: false,
            nav: false,
            logo: false
        });
        var basemap = new esri.layers.ArcGISTiledMapServiceLayer("http://server.arcgisonline.com/arcgis/rest/services/ESRI_Imagery_World_2D/MapServer")
        map.addLayer(basemap);
        param.mapUrl = basemap.url;
        param.title = "天津市测绘院";
        param.mapFrameId = 103;
        param.compassId = 100;
        param.fangda = 8;
        map.on("extent-change", function () {
            param.extent = map.extent.xmin + "," + map.extent.ymin + "," + map.extent.xmax + "," + map.extent.ymax
            param.scale = map.getScale();
            // console.log(JSON.stringify(map.extent));
            // param.extent=JSON.stringify(map.extent);
            console.log(param)
           // printMap();
        });
    });
}


function printMap() {
    $.ajax({
        type: "post",
        url: "http://localhost:8080/Print/servlet/PrintServlet",
        data: param,
        dataType: "json",
        success: function (data) {
            console.log(data);
        }
    });
}

