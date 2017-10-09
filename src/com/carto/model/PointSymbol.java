package com.carto.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.sf.json.JSONObject;

public class PointSymbol {

    /**
     * @author lb
     */

    @SuppressWarnings("unused")
    private String markerString = null;
    public double x_point;
    public double y_point;
    boolean isSymbolMarker = false;

    //普通点坐标
    public int size;
    public Color color_p;
    public String style;
    //pictureMarker坐标
    public String pictureSource;

    //	//获取高清程度，选择绘制点坐标的大小
    public double qualityIndex = 1;


    public PointSymbol() {
        // TODO Auto-generated constructor stub
    }

    public void setPointAttr(JSONObject pointObject, double qualityIndex) {

        this.qualityIndex = qualityIndex;

        x_point = pointObject.getJSONArray("pointArr").getJSONObject(0).getJSONObject("mapPoint").getDouble("x");
        y_point = pointObject.getJSONArray("pointArr").getJSONObject(0).getJSONObject("mapPoint").getDouble("y");

        if (pointObject.getJSONArray("pointArr").getJSONObject(0).getString("pMarkerSymbol").equals("null")) {
            size = pointObject.getJSONArray("pointArr").getJSONObject(0).getJSONObject("markSymbol").getInt("size");
            int color = pointObject.getJSONArray("pointArr").getJSONObject(0).getJSONObject("markSymbol").getInt("color");
            color_p = new Color(color);
            style = pointObject.getJSONArray("pointArr").getJSONObject(0).getJSONObject("markSymbol").getString("style");
            isSymbolMarker = true;
        } else {
            String soureString = pointObject.getJSONArray("pointArr").getJSONObject(0).getJSONObject("pMarkerSymbol").getString("source");
            String[] soureArr = soureString.split("/");
            //pictureSource = "/"+soureArr[3];                 //截断后第三位为图片名如： "source":"assets/images/tree.png"
            pictureSource = soureArr[soureArr.length - 2] + "/" + soureArr[soureArr.length - 1];                 //截断后第三位为图片名如： "source":"assets/images/tree.png"

            pictureSource = "/image/industry_" + (int) (qualityIndex) + "/" + pictureSource;

        }
    }

    public void drawSymbol(Rectangle2D.Double wc, Rectangle2D.Double dc, Graphics2D point2d) {

//		Rectangle2D.Double wc = new Rectangle2D.Double(-202.0335806384487,-217.41197858564692,501.0914193615513,286.7286464143531);
//		Rectangle2D.Double dc = new Rectangle2D.Double(0,0,1000,717);

        double scale1 = dc.getWidth() / wc.getWidth();
        double scale2 = dc.getWidth() / wc.getHeight();
        double scale = scale1 < scale2 ? scale1 : scale2;
//		//坐标转换后偏移量校正
        double sx = (dc.getWidth() - (wc.getWidth() * scale)) / 2;
        double sy = (dc.getHeight() + (wc.getHeight() * scale)) / 2;
////		//定位点坐标变换
        x_point = (x_point - wc.getX()) * scale;
        y_point = (-(y_point - wc.getY())) * scale;

        x_point = x_point + sx;
        y_point = y_point + sy;


        RenderingHints renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        point2d.setRenderingHints(renderHints);

        if (isSymbolMarker) {
            //设定点样式
            point2d.setPaint(color_p);
            point2d.fillOval((int) x_point, (int) y_point, (int) (size / scale), (int) (size / scale));   //绘制圆     点标注目前只为圆
        } else {
            //读写图片信息
            BufferedImage pictureImage = getpictureBufferedImage(pictureSource);
            point2d.drawImage(pictureImage, (int) (x_point - pictureImage.getWidth() / 2), (int) (y_point - pictureImage.getHeight() / 2), (int) pictureImage.getWidth(), (int) pictureImage.getHeight(), null);
        }

    }


    public BufferedImage getpictureBufferedImage(String pictureSource) {
        InputStream in = null;
        in = Thread.currentThread().getContextClassLoader().getResourceAsStream(pictureSource);
        BufferedInputStream file;
        BufferedImage bi = null;
        file = new BufferedInputStream(in);
        try {
            bi = ImageIO.read(file);
        } catch (Exception e) {
            System.out.println("绘制影像地图失败：" + e.toString());
        }
        return bi;
    }


}
