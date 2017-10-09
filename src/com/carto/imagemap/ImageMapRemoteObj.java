package com.carto.imagemap;
import com.dbconn.ConnOrcl;


public class ImageMapRemoteObj {

	public void saveToImageMap(String mapID, String content){
		String[] contentArray = content.split("@_#"); 
		try {			
			String imageStr=contentArray[0].substring(contentArray[0].indexOf("=") + 1);
			String imageAnooStr=contentArray[1].substring(contentArray[1].indexOf("=") + 1);		
//			String imageAnooStr = "";
			String vectorStr = contentArray[2].substring(contentArray[2].indexOf("=") + 1);
			String chartStr = contentArray[3].substring(contentArray[3].indexOf("=") + 1);
			String imageMapID = contentArray[4].substring(contentArray[4].indexOf("=") + 1);
			String mapScale=contentArray[5].split("=")[1];	
			String mapExtent=contentArray[6].split("=")[1];
			String markerContent = contentArray[7].substring(contentArray[7].indexOf("=") + 1);
			String dynamicContent = contentArray[8].substring(contentArray[8].indexOf("=") + 1);
			String legendStr = contentArray[9].substring(contentArray[9].indexOf("=") + 1);
			ConnOrcl jdCon  = ConnOrcl.getInstance();
			//mapID在初始化时生成，直接插入可能有重复记录
			String[] col = {"MAPID"};
			if(jdCon.selectTable("IMAGE_MAP", col, "MAPID=\'" + imageMapID + "\'").isEmpty()){
				String columns = "MAPID,IMG_SRC,IMGANNO_SRC,VTR_SRC,CHART_SRC,SCALE_STR,MAP_EXTENT,LEGEND_SRC";
				String [] values = {imageMapID,imageStr,imageAnooStr,vectorStr,chartStr,mapScale,mapExtent,legendStr};
				if(jdCon.insert2Table("IMAGE_MAP", columns, values)){
					if(saveOnlineMarker(jdCon, "IMAGE_MAP", imageMapID, markerContent, dynamicContent))
						System.out.println("IMAGE_MAP中保存制图信息成功");						
					else 
						System.out.println("IMAGE_MAP中保存标注信息失败");											
				}  
				else
					System.out.println("IMAGE_MAP中插入记录失败");
			} else {
				String setClause = "IMG_SRC='" + imageStr + "', IMGANNO_SRC='" + imageAnooStr+ "', VTR_SRC='" + vectorStr + "', MAP_EXTENT='" + mapExtent+"', CHART_SRC='" + chartStr+"', SCALE_STR='" + mapScale+"', LEGEND_SRC='"+legendStr+"'";
				String whereClause = "MAPID='" + imageMapID + "'";
				if(jdCon.updateTable("IMAGE_MAP", setClause, whereClause)){
					if(saveOnlineMarker(jdCon, "IMAGE_MAP", imageMapID, markerContent, dynamicContent))
						System.out.println("IMAGE_MAP中保存制图信息成功");						
					else 
						System.out.println("IMAGE_MAP中保存标注信息失败");	
				}
				else
					System.out.println("IMAGE_MAP中更新记录失败");
			}
			jdCon.destroyConn();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("保存影像地图数据失败："+e.toString());
		}
	}

	public void saveToImageCartoHis(String historyID, String content){
		String[] contentArray = content.split("@_#");
		String cartoHistoryID = contentArray[0].substring(contentArray[0].indexOf("=") + 1);
		String state = contentArray[1].substring(contentArray[1].indexOf("=") + 1);
		String industry = contentArray[2].substring(contentArray[2].indexOf("=") + 1);
		String spaceName = contentArray[3].substring(contentArray[3].indexOf("=") + 1);
		String mapID = contentArray[4].substring(contentArray[4].indexOf("=") + 1);
		String mapName = contentArray[5].substring(contentArray[5].indexOf("=") + 1);
		String layout = contentArray[6].substring(contentArray[6].indexOf("=") + 1);
		String extent = contentArray[7].substring(contentArray[7].indexOf("=") + 1);
		String mapExtent = contentArray[8].substring(contentArray[8].indexOf("=") + 1);
		String dataProvider = contentArray[9].substring(contentArray[9].indexOf("=") + 1);
		dataProvider = dataProvider.replace("\"", "\'"); //" 替换为'
		//System.out.println(dataProvider);
		String marker_Str = contentArray[10].substring(contentArray[10].indexOf("=") + 1);
		String dynamic_Sym = contentArray[11].substring(contentArray[11].indexOf("=") + 1);
		
		String mapAttribute = contentArray[12].substring(contentArray[12].indexOf("=") + 1);
		String layoutName = contentArray[13].substring(contentArray[13].indexOf("=") + 1);
		String metaName = contentArray[14].substring(contentArray[14].indexOf("=") + 1);
		ConnOrcl jdConn = new ConnOrcl();
		String [] col = {"CARTOHISTORYID"};
		if(jdConn.selectTable("IMAGECARTO_HISTORY", col, "CARTOHISTORYID=\'" + cartoHistoryID + "\'").isEmpty()){
			//String columns = "CARTOHISTORYID, STATE, INDUSTRY, MAPID, MAPNAME, LAYOUT, EXTENT, MAPEXTENT, DATAPROVIDER, MARKER_STR, DYNAMIC_SYMBOL";
			String columns = "CARTOHISTORYID, STATE, INDUSTRY, MAPID, SPACENAME, MAPNAME, LAYOUT, EXTENT, MAPEXTENT,DATAPROVIDER,MAPATTR,LAYOUT_NAME,META_NAME";
			//String[] values = {cartoHistoryID, state, industry, mapID, mapName, layout, extent, mapExtent, dataProvider, marker_Str, dynamic_Sym}; 
			String[] values = {cartoHistoryID, state, industry, mapID, spaceName, mapName, layout, extent, mapExtent, dataProvider,mapAttribute,layoutName,metaName};
			if(jdConn.insert2Table("IMAGECARTO_HISTORY", columns, values))
				if(saveOnlineMarker(jdConn, "IMAGECARTO_HISTORY", mapID, marker_Str, dynamic_Sym))
					System.out.println("IMAGECARTO_HISTORY中保存制图信息成功");						
				else 
					System.out.println("IMAGECARTO_HISTORY中保存标注信息失败");	
			else
				System.out.println("IMAGECARTO_HISTORY中插入记录失败");
		} else {
			String whereClause = "CARTOHISTORYID='" + cartoHistoryID + "'";		
			if(jdConn.deleteFromTable("IMAGECARTO_HISTORY", whereClause)){
				//String columns = "CARTOHISTORYID, STATE, INDUSTRY, MAPID, MAPNAME, LAYOUT, EXTENT, MAPEXTENT, DATAPROVIDER, MARKER_STR, DYNAMIC_SYMBOL";
				String columns = "CARTOHISTORYID, STATE, INDUSTRY, MAPID, SPACENAME, MAPNAME, LAYOUT, EXTENT, MAPEXTENT, DATAPROVIDER,MAPATTR,LAYOUT_NAME,META_NAME";
				//String[] values = {cartoHistoryID, state, industry, mapID, mapName, layout, extent, mapExtent, dataProvider, marker_Str, dynamic_Sym}; 
				String[] values = {cartoHistoryID, state, industry, mapID, spaceName, mapName, layout, extent, mapExtent, dataProvider,mapAttribute,layoutName,metaName};
				if(jdConn.insert2Table("IMAGECARTO_HISTORY", columns, values))
					if(saveOnlineMarker(jdConn, "IMAGECARTO_HISTORY", mapID, marker_Str, dynamic_Sym))
						System.out.println("IMAGECARTO_HISTORY中保存制图信息成功");						
					else 
						System.out.println("IMAGECARTO_HISTORY中保存标注信息失败");	
				else
					System.out.println("IMAGECARTO_HISTORY中插入记录失败");
			} else
				System.out.println("IMAGECARTO_HISTORY中删除记录失败");
		}
	}

	private boolean saveOnlineMarker(ConnOrcl conn, String tableName, String mapID, String markerCon, String dynamicCon){
		if(conn.saveMarkerAsClob(tableName, mapID, markerCon)){
			if(conn.saveDynamicSymbolAsClob(tableName, mapID, dynamicCon)){
				return true;
			}else {
				System.out.println("保存态势符号失败");
				return false;
			} 				
		} else {
			System.out.println("保存标注失败");
			return false;
		}
	}
}
