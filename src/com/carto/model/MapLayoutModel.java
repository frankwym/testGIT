package com.carto.model;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import com.dbconn.ConnOrcl;

import net.sf.json.JSONObject;


public class MapLayoutModel {
	private String id= "";
	private String modelName = "";
	
	
	private Point titleLocation=new Point();
	private Rectangle titleExtent=new Rectangle();
	private Point mainMapLocation=new Point();
	private Rectangle mainMapExtent=new Rectangle();
	private Point legendLocation=new Point();
	private Rectangle legendExtent=new Rectangle();
	private Point staChartLocation=new Point();
	private Rectangle staChartExtent=new Rectangle();
	private Point mapLocation=new Point();
	private Rectangle mapExtent=new Rectangle();
	private Point scaleLocation=new Point();
	private Rectangle scaleExtent=new Rectangle();
	private Point compassLocation=new Point();
	private Rectangle compassExtent=new Rectangle();
	private Point mapFrameLocation = new Point();
	private Rectangle mapFrameExtent = new Rectangle();

	
	private Point metaLocation = new Point();
	private Rectangle metaExtent = new Rectangle();
	
	
	//没有用到
	public static int a3w = 4961;
	public static int a3h = 3508;
	
	public MapLayoutModel(String id){
		super();
		this.id=id;		
		init(id);
	}
	
	
	//前台传入布局的数据，直接构建模板对象
	public MapLayoutModel(List<JSONObject> imagemapdata){
		JSONObject mapdata=imagemapdata.get(0);
    	modelName = mapdata.getString("MODEL_NAME");//模板名
    	int titlex=Integer.parseInt(mapdata.getString("TITLELOCATION_X"));
    	int titley=Integer.parseInt(mapdata.getString("TITLELOCATION_Y"));
    	int titleh=Integer.parseInt(mapdata.getString("TITLEEXTENT_H"));
    	int titlew=Integer.parseInt(mapdata.getString("TITLEEXTENT_W"));
    	int mainmapx=Integer.parseInt(mapdata.getString("MAINMAPLOCATION_X"));
    	int mainmapy=Integer.parseInt(mapdata.getString("MAINMAPLOCATION_Y"));
    	int mainmaph=Integer.parseInt(mapdata.getString("MAINMAPEXTENT_H"));
    	int mainmapw=Integer.parseInt(mapdata.getString("MAINMAPEXTENT_W"));
    	int legendx=Integer.parseInt(mapdata.getString("LEGENDLOCATION_X"));
    	int legendy=Integer.parseInt(mapdata.getString("LEGENDLOCATION_Y"));
    	int legendh=Integer.parseInt(mapdata.getString("LEGENDEXTENT_H"));
    	int legendw=Integer.parseInt(mapdata.getString("LEGENDEXTENT_W"));
    	int chartx=Integer.parseInt(mapdata.getString("STACHARTLOCATION_X"));
    	int charty=Integer.parseInt(mapdata.getString("STACHARTLOCATION_Y"));
    	int charth=Integer.parseInt(mapdata.getString("STACHARTEXTENT_H"));
    	int chartw=Integer.parseInt(mapdata.getString("STACHARTEXTENT_W"));
    	
    	int mapx=Integer.parseInt(mapdata.getString("MAP_X"));
    	int mapy=Integer.parseInt(mapdata.getString("MAP_Y"));
    	int mapw=Integer.parseInt(mapdata.getString("MAP_WIDTH"));
    	int maph=Integer.parseInt(mapdata.getString("MAP_HEIGHT"));
    	int scalex=Integer.parseInt(mapdata.getString("SCALELOCATION_X"));	
    	int scaley=Integer.parseInt(mapdata.getString("SCALELOCATION_Y"));	
    	int scalew=Integer.parseInt(mapdata.getString("SCALE_WIDTH"));	
    	int scaleh=Integer.parseInt(mapdata.getString("SCALE_HEIGHT"));	
    	int compassx=Integer.parseInt(mapdata.getString("COMPASSLOCATION_X"));	
    	int compassy=Integer.parseInt(mapdata.getString("COMPASSLOCATION_Y"));	
    	int compassw=Integer.parseInt(mapdata.getString("COMPASS_WIDTH"));	
    	int compassh=Integer.parseInt(mapdata.getString("COMPASS_HEIGHT"));	
    	int mapFramex=Integer.parseInt(mapdata.getString("MAPFRAME_X"));	
    	int mapFramey=Integer.parseInt(mapdata.getString("MAPFRAME_Y"));
    	int mapFramew=Integer.parseInt(mapdata.getString("MAPFRAME_WIDTH"));
    	int mapFrameh=Integer.parseInt(mapdata.getString("MAPFRAME_HEIGHT"));
    	
    	int metax = Integer.parseInt(mapdata.getString("METALOCATION_X"));
    	int metay = Integer.parseInt(mapdata.getString("METALOCATION_Y"));
    	int metaw = Integer.parseInt(mapdata.getString("META_WIDTH"));
    	int metah = Integer.parseInt(mapdata.getString("META_HEIGHT"));
    	
		titleLocation.setLocation(titlex,titley);
		mainMapLocation.setLocation(mainmapx, mainmapy);
		legendLocation.setLocation(legendx, legendy);
		staChartLocation.setLocation(chartx, charty);
		mapLocation.setLocation(mapx,mapy);
		scaleLocation.setLocation(scalex, scaley);
		compassLocation.setLocation(compassx, compassy);
		this.mapFrameLocation.setLocation(mapFramex, mapFramey);
		metaLocation.setLocation(metax, metay);
		
		titleExtent.setRect(titleLocation.getX(), titleLocation.getY(), titlew, titleh);
		mainMapExtent.setRect(mainMapLocation.getX(), mainMapLocation.getY(), mainmapw, mainmaph);
		legendExtent.setRect(legendLocation.getX(), legendLocation.getY(), legendw, legendh);
		staChartExtent.setRect(staChartLocation.getX(), staChartLocation.getY(), chartw, charth);
		mapExtent.setRect(mapLocation.getX(), mapLocation.getY(), mapw, maph);
		scaleExtent.setRect(scaleLocation.getX(), scaleLocation.getY(), scalew, scaleh);
		compassExtent.setRect(compassLocation.getX(), compassLocation.getY(), compassw, compassh);
		this.mapFrameExtent.setRect(mapFrameLocation.getX(), mapFrameLocation.getY(), mapFramew, mapFrameh);
		metaExtent.setRect(metaLocation.getX(), metaLocation.getY(), metaw, metah);
	}
	
	//通过数据库进行查询
	public void init(String modleid){
		
		ConnOrcl connection = ConnOrcl.getInstance();
		String tableName = "MAPLAYOUT_MODLE";
    	String[] columns = {"TITLELOCATION_X","TITLELOCATION_Y","TITLEEXTENT_H","TITLEEXTENT_W","MAINMAPLOCATION_X","MAINMAPLOCATION_Y","MAINMAPEXTENT_H","MAINMAPEXTENT_W"
    			,"LEGENDLOCATION_X","LEGENDLOCATION_Y","LEGENDEXTENT_H","LEGENDEXTENT_W","STACHARTLOCATION_X","STACHARTLOCATION_Y","STACHARTEXTENT_H","STACHARTEXTENT_W",
    			"MAP_X","MAP_Y","MAP_WIDTH","MAP_HEIGHT","SCALELOCATION_X","SCALELOCATION_Y","SCALE_WIDTH","SCALE_HEIGHT","COMPASSLOCATION_X","COMPASSLOCATION_Y","COMPASS_WIDTH",
    			"COMPASS_HEIGHT","MAPFRAME_X","MAPFRAME_Y","MAPFRAME_WIDTH","MAPFRAME_HEIGHT","MODEL_NAME","METALOCATION_X","METALOCATION_Y","META_WIDTH","META_HEIGHT"};
    	String whereClause = "MODLEID='" + modleid + "'";
    	
    	List<JSONObject> imagemapdata = connection.selectTable(tableName, columns, whereClause);
    	connection.destroyConn();
    	JSONObject mapdata=imagemapdata.get(0);
    	modelName = mapdata.getString("MODEL_NAME");
    	int titlex=Integer.parseInt(mapdata.getString("TITLELOCATION_X"));
    	int titley=Integer.parseInt(mapdata.getString("TITLELOCATION_Y"));
    	int titleh=Integer.parseInt(mapdata.getString("TITLEEXTENT_H"));
    	int titlew=Integer.parseInt(mapdata.getString("TITLEEXTENT_W"));
    	int mainmapx=Integer.parseInt(mapdata.getString("MAINMAPLOCATION_X"));
    	int mainmapy=Integer.parseInt(mapdata.getString("MAINMAPLOCATION_Y"));
    	int mainmaph=Integer.parseInt(mapdata.getString("MAINMAPEXTENT_H"));
    	int mainmapw=Integer.parseInt(mapdata.getString("MAINMAPEXTENT_W"));
    	int legendx=Integer.parseInt(mapdata.getString("LEGENDLOCATION_X"));
    	int legendy=Integer.parseInt(mapdata.getString("LEGENDLOCATION_Y"));
    	int legendh=Integer.parseInt(mapdata.getString("LEGENDEXTENT_H"));
    	int legendw=Integer.parseInt(mapdata.getString("LEGENDEXTENT_W"));
    	int chartx=Integer.parseInt(mapdata.getString("STACHARTLOCATION_X"));
    	int charty=Integer.parseInt(mapdata.getString("STACHARTLOCATION_Y"));
    	int charth=Integer.parseInt(mapdata.getString("STACHARTEXTENT_H"));
    	int chartw=Integer.parseInt(mapdata.getString("STACHARTEXTENT_W"));
    	
    	int mapx=Integer.parseInt(mapdata.getString("MAP_X"));
    	int mapy=Integer.parseInt(mapdata.getString("MAP_Y"));
    	int mapw=Integer.parseInt(mapdata.getString("MAP_WIDTH"));
    	int maph=Integer.parseInt(mapdata.getString("MAP_HEIGHT"));
    	int scalex=Integer.parseInt(mapdata.getString("SCALELOCATION_X"));	
    	int scaley=Integer.parseInt(mapdata.getString("SCALELOCATION_Y"));	
    	int scalew=Integer.parseInt(mapdata.getString("SCALE_WIDTH"));	
    	int scaleh=Integer.parseInt(mapdata.getString("SCALE_HEIGHT"));	
    	int compassx=Integer.parseInt(mapdata.getString("COMPASSLOCATION_X"));	
    	int compassy=Integer.parseInt(mapdata.getString("COMPASSLOCATION_Y"));	
    	int compassw=Integer.parseInt(mapdata.getString("COMPASS_WIDTH"));	
    	int compassh=Integer.parseInt(mapdata.getString("COMPASS_HEIGHT"));	
    	int mapFramex=Integer.parseInt(mapdata.getString("MAPFRAME_X"));	
    	int mapFramey=Integer.parseInt(mapdata.getString("MAPFRAME_Y"));
    	int mapFramew=Integer.parseInt(mapdata.getString("MAPFRAME_WIDTH"));
    	int mapFrameh=Integer.parseInt(mapdata.getString("MAPFRAME_HEIGHT"));
    	
    	int metax = Integer.parseInt(mapdata.getString("METALOCATION_X"));
    	int metay = Integer.parseInt(mapdata.getString("METALOCATION_Y"));
    	int metaw = Integer.parseInt(mapdata.getString("META_WIDTH"));
    	int metah = Integer.parseInt(mapdata.getString("META_HEIGHT"));
    	
//    	mapFramew = a3w;
//    	mapFrameh = a3h;
//    	
    	
		titleLocation.setLocation(titlex,titley);
		mainMapLocation.setLocation(mainmapx, mainmapy);
		legendLocation.setLocation(legendx, legendy);
		staChartLocation.setLocation(chartx, charty);
		mapLocation.setLocation(mapx,mapy);
		scaleLocation.setLocation(scalex, scaley);
		compassLocation.setLocation(compassx, compassy);
		this.mapFrameLocation.setLocation(mapFramex, mapFramey);
		metaLocation.setLocation(metax, metay);
		
		
		titleExtent.setRect(titleLocation.getX(), titleLocation.getY(), titlew, titleh);
		mainMapExtent.setRect(mainMapLocation.getX(), mainMapLocation.getY(), mainmapw, mainmaph);
		legendExtent.setRect(legendLocation.getX(), legendLocation.getY(), legendw, legendh);
		staChartExtent.setRect(staChartLocation.getX(), staChartLocation.getY(), chartw, charth);
		mapExtent.setRect(mapLocation.getX(), mapLocation.getY(), mapw, maph);
		scaleExtent.setRect(scaleLocation.getX(), scaleLocation.getY(), scalew, scaleh);
		compassExtent.setRect(compassLocation.getX(), compassLocation.getY(), compassw, compassh);
		this.mapFrameExtent.setRect(mapFrameLocation.getX(), mapFrameLocation.getY(), mapFramew, mapFrameh);
		metaExtent.setRect(metaLocation.getX(), metaLocation.getY(), metaw, metah);
//		System.out.println("\n titleExtent:"+titleExtent);
//		System.out.println("\n mainMapExtent:"+mainMapExtent);
//		System.out.println("\n legendExtent:"+legendExtent);
//		System.out.println("\n staChartExtent:"+staChartExtent);
	}
	

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Point getTitleLocation(){
		return titleLocation;		
	}
	
	public Point getMainMapLocation(){
		return mainMapLocation;		
	}
	
	public Point getLegendLocation(){
		return legendLocation;		
	}
	
	public Point getStaChartLocation(){
		return staChartLocation;		
	}
	
	public Point getMetaLocation(){
		return metaLocation;
	}
	
	public Rectangle getMetaExt(){
		return metaExtent;
	}
	
	public Rectangle getTitleExt(){
		return titleExtent;
	}
	
	public Rectangle getMainMapExt(){
		return mainMapExtent;
	}
	
	public Rectangle getLegendExt(){
		return legendExtent;
	}
	
	public Rectangle getStaChartExt(){
		return staChartExtent;
	}
	public Rectangle getMapExt(){
		return mapExtent;
	}
	public Rectangle getScaleExt(){
		return scaleExtent;
	}
	public Rectangle getCompassExt(){
		return compassExtent;
	}
	public Rectangle getMapFrame(){
		return mapFrameExtent;
	}
	public String getLayoutId(){
		return id;
	}
	public String getLayoutName()
	{
		return this.modelName;
	}
	
	public void scaleLayoutModel(double scale){
		titleExtent.setRect(titleExtent.getX() * scale, titleExtent.getY() * scale, titleExtent.width * scale, titleExtent.height * scale);
		mainMapExtent.setRect(mainMapExtent. getX() * scale, mainMapExtent. getY() * scale, mainMapExtent.width * scale, mainMapExtent.height * scale);
		legendExtent.setRect(legendExtent. getX() * scale, legendExtent. getY() * scale, legendExtent.width * scale, legendExtent.height * scale);
		staChartExtent.setRect(staChartExtent. getX() * scale, staChartExtent. getY() * scale, staChartExtent.width * scale, staChartExtent.height * scale);
		mapExtent.setRect(mapExtent.getX() * scale, mapExtent.getY() * scale, mapExtent.width * scale, mapExtent.height * scale);
		scaleExtent.setRect(scaleExtent. getX() * scale, scaleExtent. getY() * scale, scaleExtent.width * scale, scaleExtent.height * scale);
		compassExtent.setRect(compassExtent. getX() * scale, compassExtent. getY() * scale, compassExtent.width * scale, compassExtent.height * scale);
		mapFrameExtent.setRect(mapFrameExtent. getX() * scale, mapFrameExtent. getY() * scale, mapFrameExtent.width * scale, mapFrameExtent.height * scale);
		metaExtent.setRect(metaExtent.getX()*scale, metaExtent.getY()*scale, metaExtent.width*scale, metaExtent.height*scale);
	}
}

