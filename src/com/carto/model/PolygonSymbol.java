package com.carto.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import com.util.JAffine;
import net.sf.json.JSONObject;

public class PolygonSymbol {

    /**
     * @author lb
     */

    @SuppressWarnings("unused")
    private String markerString = new String();
    public String polygonString = new String();
    public JSONObject polygonObject = new JSONObject();
    ////多边形坐标
    public List<Double> x_points_L = new ArrayList<Double>();
    public List<Double> y_points_L = new ArrayList<Double>();
    public int pointNUM;
    ///填充参数
    public int fillColor;
    public double alpha;
    public String fillStyle;
    ///边界参数
    public int outLine_Width;
    public int outLine_Color;
    public double outLine_Alpha;
    public String outLine_Style;
    //多边形
    public Polygon polygon = new Polygon();

    //获取高清程度，确定绘制线的宽度
    public double qualityIndex = 1;


    public PolygonSymbol() {
        // TODO Auto-generated constructor stub

    }

    public void setPolygonAttr(JSONObject polygObject, double qualityIndex) {

        //高清程度，确定边界线宽度
        this.qualityIndex = qualityIndex;

        double x;
        double y;
        //获取填充参数
        JSONObject fillObject = polygObject.getJSONArray("pointArr").getJSONObject(0).getJSONObject("fillSym");
        fillColor = fillObject.getInt("color");
        alpha = fillObject.getLong("alpha");
        fillStyle = fillObject.getString("style");

        //获取边界线参数
        JSONObject outlineObject = polygObject.getJSONArray("pointArr").getJSONObject(0).getJSONObject("fillSym").getJSONObject("outline");
        outLine_Alpha = outlineObject.getLong("alpha");
        outLine_Color = outlineObject.getInt("color");
        outLine_Width = outlineObject.getInt("width");
        outLine_Style = outlineObject.getString("style");


        //获取点坐标
        @SuppressWarnings("unchecked")
        List<JSONObject> pointObjects = polygObject.getJSONArray("pointArr").getJSONObject(0).getJSONArray("pointArr");
        for (int i = 0; i < pointObjects.size(); i++) {
            x = (double) pointObjects.get(i).getDouble("x");
            y = (double) pointObjects.get(i).getDouble("y");
            x_points_L.add(x);
            y_points_L.add(y);
        }
        pointNUM = pointObjects.size();


    }

    public void drawSymbol(Rectangle2D.Double wc, Rectangle2D.Double dc, Graphics2D polygon2D) {

        double scale1 = dc.getWidth() / wc.getWidth();
        double scale2 = dc.getHeight() / wc.getHeight();

        @SuppressWarnings("unused")
        double scale = scale1 > scale2 ? scale1 : scale2;


        JAffine CAT = new JAffine(wc, dc);//获得地理坐标系转换屏幕坐标系矩阵
        AffineTransform AT = CAT.getAffineTransform();
        double m00 = AT.getScaleX();
        double m01 = AT.getShearX();
        double m02 = AT.getTranslateX();
        double m10 = AT.getShearY();
        double m11 = AT.getScaleY();
        double m12 = AT.getTranslateY();

        //设定填充样式
        Color fill = new Color(fillColor);
        int r = fill.getRed();
        int g = fill.getGreen();
        int b = fill.getBlue();
        Color fillColor = new Color(r, g, b, 128);
        //设定画笔样式
        BasicStroke bs = new BasicStroke((float) (outLine_Width * qualityIndex), BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
        Color outline = new Color(outLine_Color);
        int o_r = outline.getRed();
        int o_g = outline.getGreen();
        int o_b = outline.getBlue();
        Color outlineColor = new Color(o_r, o_g, o_b, 255);

        GeneralPath pp = new GeneralPath();

        ///坐标变换
        for (int i = 0; i < pointNUM; i++) {
            Double x = (double) (m00 * x_points_L.get(i) + m01 * y_points_L.get(i) + m02);
            Double y = (double) (m10 * x_points_L.get(i) + m11 * y_points_L.get(i) + m12);
            x_points_L.set(i, x);
            y_points_L.set(i, y);
        }

        for (int i = 0; i < pointNUM; i++) {
            if (0 == i)
                pp.moveTo(x_points_L.get(i), y_points_L.get(i));
            else
                pp.lineTo(x_points_L.get(i), y_points_L.get(i));
        }
        Area fillArea = new Area(pp);


        RenderingHints renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        polygon2D.setRenderingHints(renderHints);

        ///填充图形
        polygon2D.setPaint(fillColor);
        polygon2D.fill(fillArea);
        //绘制图形
        polygon2D.setPaint(outlineColor);
        polygon2D.setStroke(bs);
        polygon2D.draw(fillArea);

    }
}
