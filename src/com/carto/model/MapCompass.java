package com.carto.model;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.*;

import com.util.JUtil;

public class MapCompass {
	public String compassName;
	public MapCompass(String compassName){
	this.compassName= "compass"+compassName+".png";
	}
	
	//读取图片
	public BufferedImage getCompassBuffer()
	{
		String path = JUtil.GetWebInfPath()+"images/"+compassName;
		File file = new File(path);
		BufferedImage bi = null;
		try{
			bi=ImageIO.read(file);
		}catch(Exception e){
			System.out.println("绘制指北针失败："+e.toString());
		}
		return bi;
	}
}
	


