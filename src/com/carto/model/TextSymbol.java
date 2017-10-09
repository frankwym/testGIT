package com.carto.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.sf.json.JSONObject;

public class TextSymbol {
	
	/**
	 * @author lb
	 */

	@SuppressWarnings("unused")
	private String markerString = null;
	public String font = new String();
	public Color  textColor;
	public int size;
	public String text;
	public double x_point;
	public double y_point;
	
	
	//获取高清程度，确定绘制线的宽度
	public double qualityIndex = 1;
	
	public TextSymbol() {
		// TODO Auto-generated constructor stub
	}
	
	public void setTextAttr(JSONObject textObject,double qualityIndex){
		
		//获取高清程度，确定绘制线的宽度
		this.qualityIndex = qualityIndex;
		
//		System.out.println(textObject.toString());
		JSONObject textSymObject = textObject.getJSONArray("pointArr").getJSONObject(0).getJSONObject("textSym");
		JSONObject textFormatObject = textObject.getJSONArray("pointArr").getJSONObject(0).getJSONObject("textSym").getJSONObject("textFormat");
		text = textSymObject.getString("text");
		font = textFormatObject.getString("font");
		size = textFormatObject.getInt("size");
		int color = textFormatObject.getInt("color");
		textColor = new Color(color);
		
		JSONObject mapPointObject = textObject.getJSONArray("pointArr").getJSONObject(0).getJSONObject("mapPoint");
		x_point = mapPointObject.getDouble("x");
		y_point = mapPointObject.getDouble("y");
 		
//		System.out.println("text="+text+"font="+font+"size="+size+"color="+textColor+"x="+x_point+"y="+y_point);
	}
	
	public void drawSymbol(Rectangle2D.Double wc,Rectangle2D.Double dc,Graphics2D text2D) throws IOException{
		//坐标系转换系数
		
		//文字大小变换
		double scale1 = dc.getWidth()/wc.getWidth();
		double scale2 = dc.getWidth()/wc.getHeight();
		double scale = scale1<scale2?scale1:scale2;
		//坐标转换后偏移量校正
		double sx = (dc.getWidth() - (wc.getWidth() * scale)) / 2;
		double sy = (dc.getHeight() + (wc.getHeight() * scale)) / 2;
////		//定位点坐标变换
		x_point = (x_point - wc.getX())*scale;
		y_point = (-(y_point - wc.getY()))*scale;

		x_point = x_point + sx;
		y_point = y_point + sy;
		
		
		@SuppressWarnings("unused")
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		
		//反锯齿
		RenderingHints renderHints=new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		text2D.setRenderingHints(renderHints);
		
		//设定文字样式
		Font textFont = new Font("宋体", Font.PLAIN, (int)(size*qualityIndex));
		text2D.setFont(textFont);
		//获取字符长宽度
		FontMetrics metrics = text2D.getFontMetrics();
		Rectangle2D textRectangle = metrics.getStringBounds(text, text2D);
		double textHeight = textRectangle.getHeight();
		double textWidth = textRectangle.getWidth();
//		int textWidth = metrics.stringWidth(text);
		
		//描绘白边
		Color borderColor = new Color(255, 255, 255);
		text2D.setColor(borderColor);
		text2D.drawString(text, (float)(x_point-textWidth/2)+1, (float)(y_point+textHeight/2)+1);
		text2D.drawString(text, (float)(x_point-textWidth/2)-1, (float)(y_point+textHeight/2)-1);
		text2D.drawString(text, (float)(x_point-textWidth/2)-1, (float)(y_point+textHeight/2)+1);
		text2D.drawString(text, (float)(x_point-textWidth/2)+1, (float)(y_point+textHeight/2)-1);
		
		//绘制文字
		text2D.setColor(textColor);
		text2D.drawString(text, (float)(x_point-textWidth/2), (float)(y_point+textHeight/2));
		
		try {
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
