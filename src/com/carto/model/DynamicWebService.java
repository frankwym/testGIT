package com.carto.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

public class DynamicWebService {

    public String mapUrl;// MapServer路径
    public String bbox;// 输出地理坐系的范围 按,分割
    public int width;
    public int height;
    public String mapScale;

    public String format = "png";
    public Boolean transparent = true;// 背景是否透明
    public String f = "image";// 输出图片


    public DynamicWebService(String mapUrl, String bbox, String mapScale) {
        this.mapUrl = mapUrl;
        this.bbox = bbox;
        this.mapScale = mapScale;
    }

    public BufferedImage export(int width, int height) {
        BufferedImage bimg = null;
        String size = width + "," + height;// 输出的图片的大小像素 按,分割
        this.width = width;
        this.height = height;
        String params = "/export?bbox="
                + bbox
                + "&bboxSR=&layers=&layerDefs=&size="
                + size
                + "&imageSR=&format="
                + format
                + "&transparent="
                + transparent
                + "&dpi=&time=&layerTimeOptions=&dynamicLayers=&gdbVersion=&mapScale="
                + mapScale + "&f=" + f + "";
        mapUrl = mapUrl + params;
        try {
            URL url;
            url = new URL(mapUrl);
            URLConnection con = url.openConnection();
            // 输入流
            InputStream is = null;
            is = con.getInputStream();
            bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            bimg = ImageIO.read(is);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("绘制地图失败：" + e.toString());
        }
        return bimg;
    }

    public static void main(String[] args) throws IOException {
        String mapUrl = "http://115.28.75.54:6080/arcgis/rest/services/TJMap/MapServer/export?bbox=369500.68578333326+4334248.417369045%2C709050.760742857%2C4473283.652821425&bboxSR=&layers=&layerDefs=&size=400%2C400&imageSR=&format=png&transparent=true&dpi=&time=&layerTimeOptions=&dynamicLayers=&gdbVersion=&mapScale=&f=image";
        URL url;
        try {
            url = new URL(mapUrl);
            URLConnection con = url.openConnection();
            // 输入流
            InputStream is = null;
            is = con.getInputStream();
            BufferedImage bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
            bi = ImageIO.read(is);
            ImageIO.write(bi, "png", new File("D:\\testtileMap.png"));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
