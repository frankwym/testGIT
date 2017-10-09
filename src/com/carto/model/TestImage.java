package com.carto.model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TestImage {
	
	private static String pathString="C:\\Users\\msi\\Desktop\\photo.jpg";
	
	public void createImage(int width, int height) throws IOException{
		File file = new File(pathString);
		BufferedImage bi = ImageIO.read(file);
		BufferedImage newbi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2d = newbi.createGraphics();
		
		g2d.drawImage(bi, 0,0,width, height, null);
		
		ImageIO.write(newbi, "PNG", new File("D:\\test1.png"));
		
    }
	
	public static void main(String[] args) throws IOException{
		TestImage ts = new TestImage();
		ts.createImage(1500 , 2000);
	}
	
}
