package com.carto.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.util.*;

import com.util.JAffine;
import net.sf.json.JSONObject;
import java.util.ArrayList;

public class LineSymbol {
	/**
	 * @author lb
	 */
	
	@SuppressWarnings("unused")
	private String markerString = new String();
	public String lineString = new String();
	public List<Double> x_points_L = new ArrayList<Double>();
	public List<Double> y_points_L = new ArrayList<Double>();
	public int pointsNUM;
	@SuppressWarnings("rawtypes")
	Vector  points = null;
	
	//标注线样式
	public Color lineColor ;
	public double alpha;
	public int lineWidth;
	public String lineStyle;
	
	//获取高清程度，确定绘制线的宽度
	public double qualityIndex = 1;
	
	public LineSymbol() {
		// TODO Auto-generated constructor stub
	}
	
	public void setLineAttr(JSONObject lineObject, double qualityIndex){
		
		//获取高清程度，确定绘制线的宽度
		this.qualityIndex = qualityIndex;
		
		//临时存放点坐标
		double x ;
		double y ;
		JSONObject lineSymbolObject = lineObject.getJSONArray("pointArr").getJSONObject(0).getJSONObject("lineSymbol");
//        设定点样式
		int intColor = lineSymbolObject.getInt("color");
		Color color = new Color(intColor);
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();

		lineColor = new Color(r, g, b, 255);
		lineStyle = lineSymbolObject.getString("style");
		alpha = lineSymbolObject.getDouble("alpha");
		lineWidth = lineSymbolObject.getInt("width");
		
		//获取点坐标
		
		@SuppressWarnings("unchecked")
		List<JSONObject> pointObjects = lineObject.getJSONArray("pointArr").getJSONObject(0).getJSONArray("pointArr");
		
		for(int i=0;i<pointObjects.size();i++)
		{
			x = (double)pointObjects.get(i).getDouble("x");
			y = (double)pointObjects.get(i).getDouble("y");
			new Point((int)x, (int)y);
			
			x_points_L.add(i,x);
			y_points_L.add(i,y);
		}
		pointsNUM = pointObjects.size();
	}
	
	public void drawSymbol(Rectangle2D.Double wc,Rectangle2D.Double dc,Graphics2D line2D){
		//线转换参数
		@SuppressWarnings("unused")
		double scale1 = dc.getWidth()/wc.getWidth();
		@SuppressWarnings("unused")
		double scale2 = dc.getHeight()/wc.getHeight();
		
		new ByteArrayOutputStream();

		JAffine CAT = new JAffine(wc,dc);//获得地理坐标系转换屏幕坐标系矩阵
		AffineTransform AT = CAT.getAffineTransform();
		double m00=AT.getScaleX();
		double m01=AT.getShearX();
		double m02=AT.getTranslateX();
		double m10=AT.getShearY();
		double m11=AT.getScaleY();
		double m12=AT.getTranslateY();
		
		//设定画笔样式
		BasicStroke bs = new BasicStroke((float)(lineWidth*qualityIndex),BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
		line2D.setPaint(lineColor);
		line2D.setStroke(bs);
		
		RenderingHints renderHints=new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		line2D.setRenderingHints(renderHints);
//		
		//坐标变换
		for(int i=0;i<pointsNUM;i++)
		{
			Double x=(double)(m00*x_points_L.get(i)+m01*y_points_L.get(i)+m02);
			Double y=(double)(m10*x_points_L.get(i)+m11*y_points_L.get(i)+m12);
			x_points_L.set(i, x);
			y_points_L.set(i, y);
		}
		///绘制线路径
		GeneralPath lp = new GeneralPath();
		for(int i=0;i<pointsNUM;i++)
		{
			if(0==i)
				lp.moveTo(x_points_L.get(i),y_points_L.get(i));
			else
				lp.lineTo(x_points_L.get(i), y_points_L.get(i));
		}
		
		line2D.draw(lp);
		
		///
		try {
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage().toString());
		}
	}

}
