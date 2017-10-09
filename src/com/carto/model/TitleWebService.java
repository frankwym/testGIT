package com.carto.model;

import java.io.IOException;

public class TitleWebService {
	public String mapUrl;// MapServer路径
	public String bbox;// 输出地理坐系的范围 按,分割
	public String width;
	public String height;
	
	
	//所选的框的大小
	public double  extent_selected_xmin;
	public double  extent_selected_ymin;
	public double  extent_selected_xmax;
	public double  extent_selected_ymax;
	
	//1°等于多少米
	public double m_degree=111194.872221777;
	
	//结果
	public int tileMinCol ;
	public int  tileMaxCol ;
	public int  tileMinRow;
	public int  tileMaxRow;
	public int  mapLevelH ;
	

	
	public static void main(String[] args) throws IOException {
		String mapUrl = "http://115.28.75.54:6080/arcgis/rest/services/T05/TCP050100C/MapServer";
		TitleWebService title=new TitleWebService();
	}
}
