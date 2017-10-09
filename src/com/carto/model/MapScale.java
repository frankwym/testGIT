package com.carto.model;
//通过画线画比例尺
//By:sy

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.Math;

import javax.imageio.ImageIO;

public class MapScale {

	public String scale;

	public MapScale(String scale)
	{
		this.scale= scale ;
	}
	
	//将比例尺换算成整数形式
	public double scaleShow(String scale)
	{
		//方法主要是为了实现显示比例尺数字诸如：输入321.123 显示500；输入654.456显示1000的过程。
		//		String scalestr;
		//		scalestr=scale.toString();
		//		String[] scalearry=scalestr.split("\\.",2);//取小数点前的数
		//		String natu=scalearry[0];
		//		int natu1=scalearry[0];

		int b;
		double scaleshowd=0;
		String letter1=scale.substring(0,1);//取string首字
		int let1=Integer.parseInt(letter1);//string第1个字符 to int
		double scaledouble=Double.parseDouble(scale);//scale String to double
		int scaleint=(int)scaledouble;
		String scalestring=scaleint+"";
		int scaleintlength=scalestring.length();
		if(let1>0&&let1<=2){
			b=2;
			scaleshowd=b*Math.pow(10, scaleintlength-1);
		}else if(let1>2&&let1<=4){
			b=4;
			scaleshowd=b*Math.pow(10, scaleintlength-1);
		}else if(let1>4&&let1<=5){
			b=5;
			scaleshowd=b*Math.pow(10, scaleintlength-1);
		}else if(let1>6&&let1<=8){
			b=8;
			scaleshowd=b*Math.pow(10, scaleintlength-1);
		}else if(let1>8){
			b=10;
			scaleshowd=b*Math.pow(10, scaleintlength-1);
		}
		//		String scaleshows=String.valueOf(scaleshowd);//double to String
		return scaleshowd;

	}


	//上面两个都不太好看
	public BufferedImage DrawImageScale1()
	{
		float thick=2.5f;//设置画刷的粗细为0.5
		double scaledouble=Double.parseDouble(scale);//scale String to double
		double linelength=scaleShow(this.scale)/scaledouble;//linelength=比例尺文字显示长度/M
		double linedouble=28.35*linelength;
		int templine=(int)linedouble;//强制double to int  得到动态线长
		BufferedImage scalebuff=new BufferedImage(90,30,BufferedImage.TYPE_INT_RGB);	
		Graphics2D scale2d = (Graphics2D)scalebuff.createGraphics();		
		scalebuff=scale2d.getDeviceConfiguration().createCompatibleImage(30+templine,30, Transparency.TRANSLUCENT);
		scale2d.dispose();
		scale2d=(Graphics2D)scalebuff.createGraphics();
		//绘制比例尺线段
		scale2d.setColor(Color.blue);
		scale2d.drawLine(8, 15, 8, 25);
		scale2d.drawLine(10 + templine, 15, 10 + templine, 25);//由于设置画刷 x坐标手动设置有2个像素的偏移
		Stroke stroke=scale2d.getStroke();//得到当前的画刷
		scale2d.setStroke(new BasicStroke(thick,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND));//设置新的画刷
		scale2d.drawLine(10, 22, 10+templine, 22);
		scale2d.setStroke(stroke);
		//绘制比例尺文字
		scale2d.setColor(Color.CYAN);
		scale2d.setFont(new Font("楷体", Font.BOLD, 11));
		int scalescr = (int)scaleShow(this.scale)/100;
		if(scalescr>5000)
		{
			String scalefinal=scalescr/1000+"公里";//屏幕最终显示的比例尺文字
			scale2d.drawString(scalefinal, templine/2, 10);
		}else if(scalescr<=5000)
		{
			String scalefinal=scalescr+"米";
			scale2d.drawString(scalefinal, templine/2, 10);
		}
		scale2d.dispose();
		return scalebuff;

	}
	public BufferedImage DrawImageScale2()
	{
		float thick=2.5f;//设置画刷的粗细为0.5
		double scaledouble=Double.parseDouble(scale);//scale String to double
		double linelength=scaleShow(this.scale)/scaledouble;//linelength=比例尺文字显示长度/M
		double linedouble=37.7952*linelength;
		int templine=(int)linedouble;//强制double to int  得到动态线长（即组成该线有多少像素）
		BufferedImage scalebuff=new BufferedImage(80,30,BufferedImage.TYPE_INT_RGB);	
		Graphics2D scale2d = (Graphics2D)scalebuff.createGraphics();		
		scalebuff=scale2d.getDeviceConfiguration().createCompatibleImage(30+templine,30, Transparency.TRANSLUCENT);
		scale2d.dispose();
		scale2d=(Graphics2D)scalebuff.createGraphics();
		//绘制比例尺线段
		scale2d.setColor(Color.blue);
		scale2d.drawLine(8, 15, 8, 25);
		scale2d.drawLine(8 + templine, 15, 8 + templine, 25);
		Stroke stroke=scale2d.getStroke();//得到当前的画刷
		scale2d.setStroke(new BasicStroke(thick,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND));//设置新的画刷
		scale2d.drawLine(8, 22, 8+templine, 22);
		scale2d.setStroke(stroke);
		//绘制比例尺文字
		scale2d.setColor(Color.black);
		scale2d.setFont(new Font("楷体", Font.BOLD, 12));
		int scalescr=(int)scaleShow(this.scale)/100;
		if(scalescr>5000)
		{
			String scalefinal=scalescr/1000+"公里";//屏幕最终显示的比例尺文字
			scale2d.drawString(scalefinal, templine/2, 10);
		}else if(scalescr<=5000)
		{
			String scalefinal=scalescr+"米";
			scale2d.drawString(scalefinal, templine/2, 10);
		}
		scale2d.dispose();
		return scalebuff;

	}
	
	//这个比较好看，按照96dpi设置
	public BufferedImage DrawImageScale()
	{
		float thick=2.5f;//设置画刷的粗细为0.5
		double scaledouble=Double.parseDouble(scale);
		double linelength=scaleShow(this.scale)/scaledouble;//linelength=比例尺文字显示长度/M
		double linedouble=37.7952*linelength;//图上距离d*38 代表DPI96情况下 1厘米的长度
		int templine=(int)linedouble;//强制double to int  得到动态线长
		BufferedImage scalebuff=new BufferedImage(80,30,BufferedImage.TYPE_INT_RGB);	
		Graphics2D scale2d = (Graphics2D)scalebuff.createGraphics();		
		scalebuff=scale2d.getDeviceConfiguration().createCompatibleImage(30+2*templine,40, Transparency.TRANSLUCENT);
		scale2d.dispose();
		scale2d=(Graphics2D)scalebuff.createGraphics();
		//绘制比例尺线段
		scale2d.setColor(Color.black);
		scale2d.drawLine(7, 15, 7, 25);
		scale2d.drawLine(8 + templine, 19, 8 + templine, 28);
		scale2d.drawLine(8 + 2*templine, 15, 8 + 2*templine, 24);

		Stroke stroke=scale2d.getStroke();//得到当前的画刷
		scale2d.setStroke(new BasicStroke(thick,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND));//设置新的画刷
		scale2d.drawLine(8, 20, 8+templine, 20);
		scale2d.drawLine(8+templine, 23, 8+2*templine, 23);

		scale2d.setColor(Color.white);
		scale2d.drawLine(8, 23, 8+templine, 23);
		scale2d.drawLine(8+templine, 20, 8+2*templine, 20);

		scale2d.setStroke(stroke);
		//绘制比例尺文字
		scale2d.setColor(Color.black);
		scale2d.setFont(new Font("楷体", Font.ITALIC, 12));
		int scalescr=(int)scaleShow(this.scale)/100;
		if(scalescr>5000)
		{
			String scalefinal=scalescr/1000+"公里";//屏幕最终显示的比例尺文字
			String scalefinal2=scalescr/500+"公里";
			scale2d.drawString(scalefinal, templine-20, 38);
			scale2d.drawString(scalefinal2, 2*templine-40, 12);

		}else if(scalescr<=5000)
		{
			String scalefinal=scalescr+"米";
			String scalefinal2=2*scalescr+"米";

			scale2d.drawString(scalefinal, templine-15, 38);
			scale2d.drawString(scalefinal2, 2*templine-15, 12);

		}
		scale2d.dispose();
		return scalebuff;
	}

	
	public static void main(String[] args){
		MapScale mp = new MapScale("1000000");
		BufferedImage bi = mp.DrawImageScale();
		try {
			ImageIO.write(bi, "png", new File("D:\\MapFrame100.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
