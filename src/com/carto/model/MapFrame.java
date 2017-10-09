package com.carto.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import com.util.JUtil;

public class MapFrame {

	public String mapFrameName;
	public String mapFrameID = "100";

	public MapFrame(String mapFrameID) {
		// TODO Auto-generated constructor stub
		this.mapFrameName = "/MapFrame"+mapFrameID+".png";
	}
	//读取图片
	public BufferedImage getFrameBufferedImage(){
		String path = JUtil.GetWebInfPath()+"images/"+mapFrameName;
		File file = new File(path);
		BufferedImage bi = null;
		try{
			bi = ImageIO.read(file);
		}catch(Exception e){
			System.out.println("绘制图框失败："+e.toString());
		} 
		return bi;    	 
	}
	
	public static void main(String[] args){
		MapFrame mp = new MapFrame("100");
		BufferedImage bi = mp.getFrameBufferedImage();
		try {
			ImageIO.write(bi, "png", new File("D:\\MapFrame100.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
