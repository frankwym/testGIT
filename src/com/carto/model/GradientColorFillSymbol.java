package com.carto.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import com.util.JAffine;
import net.sf.json.JSONObject;

public class GradientColorFillSymbol {
	
	/**
	 * @author lb
	 */
	
	@SuppressWarnings("unused")
	private String markerString = new String();
	public String polygonString = new String();
	public JSONObject polygonObject = new JSONObject();
	////多边形坐标
	public List<Double> x_points_L  = new ArrayList<Double>();
	public List<Double> y_points_L = new ArrayList<Double>();
	public int pointNUM;
	///填充参数
	public int fillColor1;
	public int fillColor2;
	public double alpha;
	public String fillStyle;
	///边界参数
	public int outLine_Width;
	public int outLine_Color;
	public double outLine_Alpha;
	public String outLine_Style;
	//多边形
	public Polygon polygon = new Polygon();
	
	public Point2D starPoint  ;
	public Point2D endPoint  ;
	
	//根据成图分辨率设定线宽度
	private double qualityIndex = 1;

	public GradientColorFillSymbol() {
		// TODO Auto-generated constructor stub
	}
	
	
	public void  setDynamicSymbolAttr(JSONObject dynamicSymbolObject,double qualityIndex){
		
		this.qualityIndex = qualityIndex;
		
		double x;
		double y;
//		System.out.println(dynamicSymbolObject.toString());
		//获取填充参数
//		JSONObject fillObject = polygObject.getJSONArray("pointArr").getJSONObject(0).getJSONObject("fillSym");
		JSONObject fillObject = dynamicSymbolObject.getJSONObject("fill");
		fillColor1 = fillObject.getInt("color1");
		fillColor2 = fillObject.getInt("color2");
		alpha = fillObject.getDouble("alpha");
//		fillStyle = fillObject.getString("fillStyle");
//		System.out.println("fillColor："+fillColor+"alpha:"+alpha+"fillStyle:"+fillStyle);
		
		//获取边界线参数
		JSONObject outlineObject = dynamicSymbolObject.getJSONObject("fill").getJSONObject("outline");
		outLine_Alpha = outlineObject.getDouble("alpha");
		outLine_Color = outlineObject.getInt("color");
		outLine_Width = outlineObject.getInt("width");
		outLine_Style = outlineObject.getString("style");
		
//		System.out.println("outLine_Alpha"+outLine_Alpha+"outLine_Color"+outLine_Color+"outLine_Width"+outLine_Width+"outLine_Style"+outLine_Style);
		
		//获取点坐标
		@SuppressWarnings("unchecked")
		List<JSONObject> pointObjects = dynamicSymbolObject.getJSONArray("pointArr");
		
		starPoint = new Point2D.Double((double)pointObjects.get(0).getDouble("x"), (double)pointObjects.get(0).getDouble("y"));
		endPoint = new Point2D.Double((double)pointObjects.get(0).getDouble("x"), (double)pointObjects.get(0).getDouble("y"));
		
		for(int i=0;i<pointObjects.size();i++)
		{
			x = (double)pointObjects.get(i).getDouble("x");
			y = (double)pointObjects.get(i).getDouble("y");
			x_points_L.add(x) ;
			y_points_L.add(y);
		}
		pointNUM = pointObjects.size();

	}
	public void drawSymbol(Rectangle2D.Double wc,Rectangle2D.Double dc,Graphics2D dynamicSymbol2D){
		
		double scale1 = dc.getWidth()/wc.getWidth();
		double scale2 = dc.getHeight()/wc.getHeight();
		
		@SuppressWarnings("unused")
		double scale = scale1>scale2?scale1:scale2;
		
		JAffine CAT = new JAffine(wc,dc);//获得地理坐标系转换屏幕坐标系矩阵
		AffineTransform AT = CAT.getAffineTransform();
		double m00=AT.getScaleX();
		double m01=AT.getShearX();
		double m02=AT.getTranslateX();
		double m10=AT.getShearY();
		double m11=AT.getScaleY();
		double m12=AT.getTranslateY();
		
	
		///坐标变换
		for(int i=0;i<pointNUM;i++)
		{
			Double x=(double)(m00*x_points_L.get(i)+m01*y_points_L.get(i)+m02);
			Double y=(double)(m10*x_points_L.get(i)+m11*y_points_L.get(i)+m12);
			x_points_L.set(i, x);
			y_points_L.set(i, y);
			

		}
		
		starPoint = new Point2D.Double(x_points_L.get(0),y_points_L.get(0));
		endPoint = new Point2D.Double(x_points_L.get(0),y_points_L.get(0));
		
		for(int i=0;i<pointNUM;i++)
		{
			Double x = x_points_L.get(i);
			Double y = y_points_L.get(i);
			//渐变颜色起点
			if(starPoint.getX()>x)
			{
				starPoint.setLocation(x, y);
			}
			//渐变颜色终点
			if(endPoint.getX()<x)
			{
				endPoint.setLocation(x, y);
			}
		}
		//设定填充样式
		//渐变颜色1
		Color fill1 = new Color(fillColor1);
		int r1 = fill1.getRed();
		int g1 = fill1.getGreen();
		int b1 = fill1.getBlue();
		Color fillColor1 = new Color(r1, g1, b1, (int)(255*alpha));
		//渐变颜色2
		Color fill2 = new Color(fillColor2);
		int r2 = fill2.getRed();
		int g2 = fill2.getGreen();
		int b2 = fill2.getBlue();
		Color fillColor2 = new Color(r2, g2, b2, (int)(255*alpha));
		
		//构造渐进色   gradientPaint
		GradientPaint dradientpaint = new GradientPaint(starPoint, fillColor1, endPoint, fillColor2);
		//设定画笔样式
		BasicStroke bs = new BasicStroke((float)(outLine_Width*qualityIndex),BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND);
		Color outline = new Color(outLine_Color);
		int o_r = outline.getRed();
		int o_g = outline.getGreen();
		int o_b = outline.getBlue();
		Color outlineColor = new Color(o_r, o_g, o_b, (int)(outLine_Alpha*255));
		
		GeneralPath pp = new GeneralPath();

		for(int i=0;i<pointNUM;i++)
		{
			if(0==i)
				pp.moveTo(x_points_L.get(i), y_points_L.get(i));
			else
				pp.lineTo(x_points_L.get(i), y_points_L.get(i));
		}
		pp.closePath();
		Area fillArea = new Area(pp);
		 
		 
		 RenderingHints renderHints=new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		 renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		 dynamicSymbol2D.setRenderingHints(renderHints);
		
		///填充图形
		 dynamicSymbol2D.setPaint(dradientpaint);
		 dynamicSymbol2D.fill(fillArea);
		//绘制图形
		 dynamicSymbol2D.setPaint(outlineColor);
		 dynamicSymbol2D.setStroke(bs);
		 
		 dynamicSymbol2D.draw(pp);
		
	}

}
