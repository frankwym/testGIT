/**
 * 计算得到行列号的函数
 * @author luyuexin
 * @param level
 * @param centerGeoX
 * @param centerGeoX
 * @param mapContainerH
 * @param mapContainerw
 * @returns TiledData 将计算结果组成成一个字符串，插入到数据库中
 */
function getTiledMain(level,centerGeoX,centerGeoY,mapContainerH,mapContainerW,index){
	this.Level = level;
	this.Index = index;
//	var scale
	
	originX = -180;
	originY = 90;
	tileSize = 256;
	
	var geoX= 0;
	var geoY=0
	
	//根据地图级别（level）换算得到resolution 注意级别信息
	function getResolution(Level){
		var resolution= 0;
	switch(Level){
	case 0:
		resolution = 0.703125;scale = 295497593.05875;
		break;
	case 1:
		resolution = 0.3515625;scale = 147748796.529375;
		break;
	case 2:
		resolution = 0.17578125;scale = 73874398.264688;
		break;
	case 3:
         resolution = 0.087890625;scale = 36937199.132344;
         break;
	case 4:
         resolution = 0.0439453125;scale =  18468599.566172;
         break;
	case 5:
         resolution = 0.02197265625;scale =  9234299.783086;
         break;
	case 6:
         resolution = 0.010986328125;scale =  4617149.891543;
         break;
	case 7:
         resolution = 0.0054931640625;scale =  2308574.945771;
         break;
	case 8:
         resolution = 0.00274658203125;scale =  1154287.472886;
         break;
	case 9:
         resolution = 0.001373291015625;scale =  577143.736443;
         break;
	case 10:
         resolution = 0.0006866455078125;scale =  288571.86822143558;
         break;
	case 11:
         resolution = 0.00034332275390625;scale =  144285.93411071779;
         break;
	case 12:
         resolution = 0.000171661376953125;scale =  72142.967055358895;
         break;
	case 13:
         resolution = 8.58306884765625e-005;scale =  36071.483527679447;
         break;
	case 14:
         resolution = 4.291534423828125e-005;scale =  18035.741763839724;
         break;
	case 15:
         resolution = 2.1457672119140625e-005;scale =  9017.8708819198619;
         break;
	case 16:
         resolution = 1.0728836059570313e-005;scale =  4508.9354409599309;
         break;
	case 17:
         resolution = 5.3644180297851563e-006;scale =  2254.4677204799655;
         break;
    case 18:
      	 resolution = 0.000002682209014851133319091796875;scale =  1128.4994333441375;
         break;
    case 19:
         resolution = 0.0000013411045074255666595458984375;scale =  564.24971667206875;
         break;
	}
	console.log("通过level为："+Level+"得到对应的resolution值为："+resolution);
	return resolution;
	};
	
	var resolution = getResolution(Level);
	//根据中心点地理坐标和resolution计算得到地理范围
	geoX = resolution* mapContainerW;
	geoY = resolution* mapContainerH;
	var minX =centerGeoX - (resolution* mapContainerW)/2;
//	var maxX =centerGeoX + (resolution* mapContainerW)/2;
//	var minY =centerGeoY - (resolution* mapContainerH)/2;
	var maxY =centerGeoY + (resolution* mapContainerH)/2;
	
	//计算瓦片起始行列号(fixedTileLeftTopNumX、fixedTileLeftTopNumY)
	var fixedTileLeftTopNumX = Math.floor((Math.abs(originX - minX))/(resolution*tileSize));
	var fixedTileLeftTopNumY = Math.floor((Math.abs(originY - maxY))/(resolution*tileSize));
	
	//实际地理范围(realMinX、realMaxY)
	var curLevelClipLength = resolution*tileSize; 
	var realMinX = fixedTileLeftTopNumX * curLevelClipLength + originX;
	var realMaxY= originY - fixedTileLeftTopNumY * curLevelClipLength;
	
	//左上角偏移像素(offSetX、offSetY)
	var offSetX = ((realMinX- minX )/resolution);
	var offSetY = ((maxY - realMaxY )/resolution);
	
	// X、Y轴上的瓦片个数(mapXClipNum、mapYClipNum)
	var mapXClipNum = Math.ceil((mapContainerW + Math.abs(offSetX))/tileSize);
	var mapYClipNum = Math.ceil((mapContainerH + Math.abs(offSetY))/tileSize);
	
	//计算右下角最远的瓦片行列号
	var fixedTileRightButtomNumX = fixedTileLeftTopNumX + mapXClipNum;
	var fixedTileRightButtomNumY = fixedTileLeftTopNumY + mapYClipNum;
	
	//计算其他参数，不确定是否有用！
	var tile = resolution*tileSize;
	var tileX = realMinX;
	var tileY = realMaxY;
	var tileWidth = resolution*mapXClipNum*tileSize;
	var tilwHeight = resolution*mapYClipNum*tileSize;
	
	var extentX = minX;
	var extentY = maxY;
	var extentWidth = resolution*mapContainerW;
	var extentHeight = resolution*mapContainerH;
	
	console.log("最小列号："+ fixedTileLeftTopNumX + "最大列号：" + fixedTileRightButtomNumX +
			"最小行号："+ fixedTileLeftTopNumY + "最大行号：" + fixedTileRightButtomNumY +
			"地图级别：" + Level);
	var TiledData = "tileMinCol="+fixedTileLeftTopNumX +"#tileMaxCol="+ fixedTileRightButtomNumX + "#tileMinRow="+fixedTileLeftTopNumY +
					"#tileMaxRow="+ fixedTileRightButtomNumY +"#imageLevel="+Level+"#tileX="+tileX+"#tileY="+tileY+
					"#tileWidth="+tileWidth+"#tileHeight="+tilwHeight+"#extentX="+extentX+"#extentY="+extentY+
					"#extentWidth="+extentWidth+"#extentHeight="+extentHeight;
	/************************
	 * 获取低两级地图行列号
	 * *****************************************************************/
	
	var resolutionH = getResolution(Level+index);
	var mapContainerWH = geoX/resolutionH;
	var mapContainerHH = geoY/resolutionH;
	
	//计算瓦片起始行列号(fixedTileLeftTopNumX、fixedTileLeftTopNumY)
	var fixedTileLeftTopNumXH = Math.floor((Math.abs(originX - minX))/(resolutionH*tileSize));
	var fixedTileLeftTopNumYH = Math.floor((Math.abs(originY - maxY))/(resolutionH*tileSize));
	
	//实际地理范围(realMinX、realMaxY)
	var curLevelClipLengthH = resolutionH*tileSize; 
	var realMinXH = fixedTileLeftTopNumXH * curLevelClipLengthH + originX;
	var realMaxYH= originY - fixedTileLeftTopNumYH * curLevelClipLengthH;
	
	//左上角偏移像素(offSetX、offSetY)
	var offSetXH = ((realMinXH- minX )/resolutionH);
	var offSetYH = ((maxY - realMaxYH )/resolutionH);
	
	// X、Y轴上的瓦片个数(mapXClipNum、mapYClipNum)
	var mapXClipNumH = Math.ceil((mapContainerWH + Math.abs(offSetXH))/tileSize);
	var mapYClipNumH = Math.ceil((mapContainerHH + Math.abs(offSetYH))/tileSize);
	
	//计算右下角最远的瓦片行列号
	var fixedTileRightButtomNumXH = fixedTileLeftTopNumXH + mapXClipNumH;
	var fixedTileRightButtomNumYH = fixedTileLeftTopNumYH + mapYClipNumH;
	
	//计算其他参数，不确定是否有用！
	var tile = resolutionH*tileSize;
	var tileXH = realMinXH;
	var tileYH = realMaxYH;
	var tileWidthH = resolutionH*mapXClipNumH*tileSize;
	var tilwHeightH = resolutionH*mapYClipNumH*tileSize;
	
	var extentXH = minX;
	var extentYH = maxY;
	var extentWidthH = resolutionH*mapContainerWH;
	var extentHeightH = resolutionH*mapContainerHH;
	
	var TiledDataH = "#tileMinCol="+fixedTileLeftTopNumXH +"#tileMaxCol="+ fixedTileRightButtomNumXH + "#tileMinRow="+fixedTileLeftTopNumYH +
	"#tileMaxRow="+ fixedTileRightButtomNumYH +"#imageLevel="+(Level+2)+"#tileX="+tileXH+"#tileY="+tileYH+
	"#tileWidth="+tileWidthH+"#tileHeight="+tilwHeightH+"#extentX="+extentXH+"#extentY="+extentYH+
	"#extentWidth="+extentWidthH+"#extentHeight="+extentHeightH;
	console.log(TiledDataH);
	
	return TiledData+TiledDataH;
}