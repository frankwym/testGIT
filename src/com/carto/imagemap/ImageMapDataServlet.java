package com.carto.imagemap;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.dbconn.ConnOrcl;

@SuppressWarnings("serial")
public class ImageMapDataServlet extends HttpServlet {

	public ImageMapDataServlet(){
		super();
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		String serviceType = request.getParameter("serviceType"); 
		response.setHeader("Content-Type", "text/html;charset=utf-8");
		response.setContentType("text/html");
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
	     System.out.println("保存影像地图数据失败："+e.toString());
		}
		ConnOrcl connection = ConnOrcl.getInstance();
		
		if (serviceType.equals("saveImageMapData")) {		
			//saveImageMapData(connection, request, out);			
		}else if (serviceType.equals("saveSeleModel")) {		
			saveSeleModel(connection, request, out);
		}else if (serviceType.equals("saveCartoHistory")) {		
			//saveCartoHistory(connection, request, out);
		}
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
            doGet(request, response);
    }

	//保存图名、模板数据
	private void saveSeleModel(ConnOrcl connection, HttpServletRequest request, PrintWriter out){
		try {
			String imageMapID= URLDecoder.decode(request.getParameter("imageMapID"), "utf-8");	
			String seleModel= URLDecoder.decode(request.getParameter("seleModel"), "utf-8");
			String imageFramework = URLDecoder.decode(request.getParameter("mapFrameworkID"), "utf-8");
			String imageMapName = URLDecoder.decode(request.getParameter("imageMapName"), "utf-8");	
			String imageMetaData = URLDecoder.decode(request.getParameter("imageMetaData"),"utf-8");
			String modelName = URLDecoder.decode(request.getParameter("modelName"), "utf-8");	
			if(seleModel.length() != 3){
				String userLayout = request.getParameter("userLayoutStr");
				String columns = "MAINMAPLOCATION_X,MAINMAPLOCATION_Y,MAINMAPEXTENT_W,MAINMAPEXTENT_H," +
				"TITLELOCATION_X,TITLELOCATION_Y,TITLEEXTENT_W,TITLEEXTENT_H," + 
				"LEGENDLOCATION_X,LEGENDLOCATION_Y,LEGENDEXTENT_W,LEGENDEXTENT_H," +
				"STACHARTLOCATION_X,STACHARTLOCATION_Y,STACHARTEXTENT_W,STACHARTEXTENT_H," +
				"SCALELOCATION_X,SCALELOCATION_Y,SCALE_WIDTH,SCALE_HEIGHT," +
				"COMPASSLOCATION_X,COMPASSLOCATION_Y,COMPASS_WIDTH,COMPASS_HEIGHT," + 
				"MAPFRAME_X,MAPFRAME_Y,MAPFRAME_WIDTH,MAPFRAME_HEIGHT," +
				"MAP_X,MAP_Y,MAP_WIDTH,MAP_HEIGHT,"+
				"METALOCATION_X,METALOCATION_Y,META_WIDTH,META_HEIGHT,"+
				"MODLEID,MODEL_NAME";
				
				String [] elements = userLayout.split(";");
				String values = "";
				for(int i=0; i < elements.length; i++){
					String [] value = elements[i].split(",");
					for(int j=0; j < value.length; j++){
						values += value[j] + ",";
					}
				}
				values += seleModel+","+modelName;
				
				String [] val = values.split(",");				
				boolean result1 = false;	
				String [] col = {"MODLEID"};
				if(connection.selectTable("MAPLAYOUT_MODLE", col, "MODLEID=\'" + seleModel + "\'").isEmpty() ){
					result1 = connection.insert2Table("MAPLAYOUT_MODLE", columns, val);
					if (result1) {
					    System.out.println("插入 MAPLAYOUT_MODLE 成功");
					} else {
					    System.out.println("插入 MAPLAYOUT_MODLE 失败");
					    return ;
				    }

					String setClause = "MAP_FRAME_ID='"+imageFramework+"',MAPLAY_MOD_ID='"+seleModel+"',MAP_NAME='"+imageMapName+"',META_DATA='"+imageMetaData+"'";
					String whereClause = "MAPID='" + imageMapID + "'";
					boolean result = connection.updateTable("IMAGE_MAP", setClause, whereClause);
					if (result) {
					    out.write("Update IMAGE_MAP success");
					} else {
					    out.write("Update IMAGE_MAP failed");
				    }
				}
				else {
					String deleWhere = "MODLEID='" + seleModel +"'";
					if(connection.deleteFromTable("MAPLAYOUT_MODLE", deleWhere)){
						boolean result2 = connection.insert2Table("MAPLAYOUT_MODLE", columns, val);
						if (result2) {
						    System.out.println("插入 MAPLAYOUT_MODLE 成功");
						} else {
						    System.out.println("插入 MAPLAYOUT_MODLE 失败");
						    return ;
					    }
						String setClause = "MAP_FRAME_ID='"+imageFramework+"',MAPLAY_MOD_ID='"+seleModel+"',MAP_NAME='"+imageMapName+"',META_DATA='"+imageMetaData+"'";
						String whereClause = "MAPID='" + imageMapID + "'";
						boolean result = connection.updateTable("IMAGE_MAP", setClause, whereClause);
						if (result) {
						    out.write("Update IMAGE_MAP success");
						} else {
						    out.write("Update IMAGE_MAP failed");
					    }
					}else {
						System.out.println("删除MAPLAYOUT_MODLE记录失败");
					}
				}
			}
			else {
				String setClause = "MAP_FRAME_ID='"+imageFramework+"',MAPLAY_MOD_ID='"+seleModel+"',MAP_NAME='"+imageMapName+"',META_DATA='"+imageMetaData+"'";
				String whereClause = "MAPID='" + imageMapID + "'";
				boolean result = connection.updateTable("IMAGE_MAP", setClause, whereClause);
				if (result) {
					 out.write("Update IMAGE_MAP success");
				} else {
					out.write("Update IMAGE_MAP failed");
			    }
			}
			connection.destroyConn();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("保存用户模板失败："+e.toString());
		}
	}
}


