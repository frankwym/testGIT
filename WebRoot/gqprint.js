//下面是国情数据打印的模块
/*$(function(){
	$("#modle").append(" <li> <img id='Landscape_01' onclick='choosecss(this.id)' src='images/layout/Landscape_01_s.png'></li>"+
			"<li><a href='#'> <img id='Landscape_02' onclick='choosecss(this.id)' src='images/layout/Landscape_02_s.png'></a></li>"+
			"<li><a href='#'> <img id='Landscape_03' onclick='choosecss(this.id)' src='images/layout/Landscape_03_s.png'></a></li>" +
			"<li><a href='#'> <img id='Portrait_01' onclick='choosecss(this.id)' src='images/layout/Portrait_01_s.png'></a></li>"+
			"<li><a href='#'> <img id='Portrait_02' onclick='choosecss(this.id)' src='images/layout/Portrait_02_s.png'></a></li>")
})*/
var imageMapID="";//地图编号
var mapScale = "";
var mapLevel =0;
var urlHeader ='';
var centerX;
var centerY;
var divW=0;
var divH=0;

var isHightMap = false;
var index = 0;

$(function(){
//	var urlHeader = getParam('allFilters');//url前半段
	urlHeader = "http://10.2.35.3:9070/WebServiceForH5/SuperEngine/WMTSServer/gqpc201607?renderType=1&type=3&service=WMTS&request=GetTile&version=1.0.0&layer=resp_310101%40file_ds_osGQPC%2Cresp_310102%40file_ds_osGQPC%2Cresp_310103%40file_ds_osGQPC%2Cresp_310104%40file_ds_osGQPC%2CV_LCRA15%40file_ds_osGQPC&style=&tilematrixSet=WGS84&format=image%2Fpng&transparent=true&height=256&width=256&filters=&wsver=2015&srs=EPSG%3A4326&tilematrix=";
	mapLevel = parseInt(getParam('lipLevel'));//地图级别
	centerX = parseFloat(getParam('pointX'));//地图中心点坐标
	centerY = parseFloat(getParam('pointY'));//地图中心点坐标
	mapScale = parseFloat(getParam('lipScale'));//比例尺
	divW = parseInt(getParam('mapWidth'));//地图容器宽
	divH = parseInt(getParam('mapHeight'));//地图容器高
	
	console.log("开始计算参数");
	var tData = getTiledMain(mapLevel,centerX,centerY,divH,divW,index);
	
	var tiledpara = {tData:tData,urlHeader:urlHeader};
	$.ajax({
		url : 'http://10.2.35.3:8080/gqAtlas/servlet/SaveImageObjServlet',
		type : 'post',
		dataType : 'json',
		data : tiledpara,
	}).done(function(data) {
		console.log("save url succcess");
		imageMapID = data;
		console.log(imageMapID);
	}).fail(function() {
		console.log("error");
	}).always(function() {
		console.log("complete");
	});
	
});
/**
 * 获取参数函数
 * @param paramName
 * @returns {String}
 */
function getParam(paramName){
	paramValue = "";
	isFound =false;
	if(this.location.search.indexOf("?") == 0 && this.location.search.indexOf("=")>1){
		arrSource = unescape(this.location.search).substring(1, this.location.search.length).split("&");
		i=0;
		while(i<arrSource.length&&!isFound){
			if(arrSource[i].indexOf("=") > 0){
				if(arrSource[i].split("=")[0].toLowerCase() == paramName.toLowerCase()){
					paramValue = arrSource[i].split("=")[1];
					isFound = true;
				}
			}
			i++;
		}
	}
	return paramValue;
}

/**
 * 改变选中样式
 * @param id
 */
function choosecss(id){
	$("#modelimg_div").html('');
//	$("#modelimg_div").append("<img id='datu' style='margin: 100px 150px;' src='images/layout/"+id+".jpg' >")
	
	if(id=="103"||id=="104"){
		$("#modle img").removeClass("modleimg_choosev");
		$("#modle img").removeClass("modleimg_chooseh");
		modleid = id;
		$("#"+modleid).addClass("modleimg_choosev");
		$("#modelimg_div").append("<img id='"+id+"' style='margin: 70px 400px; width: 594px;height: 860px;' src='images/layout/modelPic"+id+".png' >")
	}else{
		$("#modle img").removeClass("modleimg_choosev");
		$("#modle img").removeClass("modleimg_chooseh");
		modleid = id;
		$("#"+modleid).addClass("modleimg_chooseh");
		$("#modelimg_div").append("<img id='"+id+"' style='margin: 100px 200px;width: 955px;height: 675px;' src='images/layout/modelPic"+id+".png' >")
	}
	
}

var mapframeid = 100;
var mapName = '';
$("#comfire_result").click(function(){ 
	if($("#usermapname").val() == ""){
		alert("请填写图名！");
		return;
	}else if(($("#maplevel").val() == "")||(parseInt($("#maplevel").val())>17)||(parseInt($("#maplevel").val())<1)){
		console.log("级别不变")
		mapName = $("#usermapname").val();
		mapframeid = $("#modelimg_div img").attr("id");
		isHightMap = false;
		getFinalMap(mapframeid,imageMapID,isHightMap);
	}else{
		console.log("级别："+$("#maplevel").val());
		index = parseInt($("#maplevel").val()) - mapLevel;
		var tData = getTiledMain(mapLevel,centerX,centerY,divH,divW,index);
		
		var tiledpara = {tData:tData,urlHeader:urlHeader};
		$.ajax({
			url : 'http://10.2.35.3:8080/gqAtlas/servlet/SaveImageObjServlet',
			type : 'post',
			dataType : 'json',
			async: false,
			data : tiledpara,
		}).done(function(data) {
			console.log("save url succcess");
			imageMapID = data;
			console.log(imageMapID);
			
			mapName = $("#usermapname").val();
			mapframeid = $("#modelimg_div img").attr("id");
			isHightMap = true;
			
			getFinalMap(mapframeid,imageMapID,isHightMap);
		}).fail(function() {
			console.log("error");
		}).always(function() {
			console.log("complete");
		});
	}
});

function getFinalMap(mapframeid,imageMapID,isHightMap){
//	imageMapID = 201610;
//	imageMapID = data;
//	var mapName = "用户输入的地图名111";
//	mapScale = mapScale
	var para1 = {mapFrameID:mapframeid,imageMapID:imageMapID,scale:mapScale,mapName:mapName};
	//更新模板编号，图名等信息到表中
	$.ajax({
		url : 'http://10.2.35.3:8080/gqAtlas/servlet/SaveMapDataServlet',
		type : 'post',
		dataType : 'json',
		async: false,
		data : para1,
	}).done(function(data) {
		console.log("updata mapname success");
		$("#printprogressbar1").show();
		$("#modelimg_div img").attr('src','http://10.2.35.3:8080/gqAtlas/servlet/DrawImageMapServlet?mapID=' + imageMapID+"&isHightMap="+isHightMap);
		$("#printprogressbar1").hide();
	}).fail(function() {
		console.log("error");
	}).always(function() {
		console.log("complete");
	});
}