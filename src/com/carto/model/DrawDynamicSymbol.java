package com.carto.model;

import com.dbconn.ConnOrcl;
import com.util.JUtil;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.Feature;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class DrawDynamicSymbol {
    /**
     * @author lb
     */
    private String dynamicMarkerSTR = new String();
    public Rectangle2D.Double wc;
    public Rectangle2D.Double dc;
    public int addDynamicSymbol = 1;
    public String DC;
    public String WC;
    private ConnOrcl connection = ConnOrcl.getInstance();

    //控制边界线宽度
    private double qualityIndex = 1;


    public DrawDynamicSymbol() {
        // TODO Auto-generated constructor stub
    }

    public void initParam(String mapID, String mapDC, double qualityIndex) {
        this.qualityIndex = qualityIndex;

        String dynamicSTR = connection.getDyanmicSymbolSTR("IMAGE_MAP", mapID);
        String[] dString = dynamicSTR.split("@");
        addDynamicSymbol = Integer.parseInt(dString[0]);
//		DC = dString[1];

        this.DC = mapDC;
        this.WC = dString[2];

        dynamicMarkerSTR = dString[3];

        String[] wc_str = WC.split(",");
        String[] dc_str = DC.split(",");
        double[] wc_arr = new double[4];
        double[] dc_arr = new double[4];
        for (int i = 0; i < wc_str.length; i++) {
            wc_arr[i] = Double.parseDouble(wc_str[i]);
            dc_arr[i] = Double.parseDouble(dc_str[i]);
        }

        wc = new Rectangle2D.Double(wc_arr[0], wc_arr[1], wc_arr[2] - wc_arr[0], wc_arr[3] - wc_arr[1]);
        dc = new Rectangle2D.Double(dc_arr[0], dc_arr[1], dc_arr[2] - dc_arr[0], dc_arr[3] - dc_arr[1]);
    }

    public BufferedImage drawSymbol() {
        BufferedImage dynamicSymbolBUF = null;

        try {
            String[] mString = dynamicMarkerSTR.split("#_#");

            @SuppressWarnings("unused")
            BufferedImage perSymbolImage = null;

            dynamicSymbolBUF = new BufferedImage((int) dc.width, (int) dc.height, BufferedImage.TYPE_BYTE_INDEXED);
            Graphics2D dynamicSymbol2D = (Graphics2D) dynamicSymbolBUF.createGraphics();
            dynamicSymbolBUF = dynamicSymbol2D.getDeviceConfiguration().createCompatibleImage((int) dc.width, (int) dc.height, Transparency.TRANSLUCENT);
            dynamicSymbol2D.dispose();
            dynamicSymbol2D = dynamicSymbolBUF.createGraphics();

            for (int i = 1; i < mString.length; i++) {
                JSONObject mObject = JSONObject.fromObject(mString[i]);
//				perSymbolImage = getTypeSymbolByte(mObject,wc,dc);
                drawSymbol(mObject, wc, dc, dynamicSymbol2D);
//				dynamicSymbol2D.drawImage(perSymbolImage, null, null);
            }
            dynamicSymbol2D.dispose();

        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
        }
        return dynamicSymbolBUF;
    }

    public void drawSymbol(JSONObject dynamicSymbolObject, Rectangle2D.Double wc, Rectangle2D.Double dc, Graphics2D dynamicSymbol2D) throws IOException {
        String symbolTypeString;
        @SuppressWarnings("unused")
        BufferedImage symbolImage = null;
        SimpleFillSymbol simplefillsymbol = new SimpleFillSymbol();
        simplefillsymbol.setDynamicSymbolAttr(dynamicSymbolObject, qualityIndex);
        simplefillsymbol.drawSymbol(wc, dc, dynamicSymbol2D);

        //注释邱博的代码
//        symbolTypeString = dynamicSymbolObject.getString("fillStyle");

//        if (symbolTypeString.equals("SimpleFillSymbol")) {
//            SimpleFillSymbol simplefillsymbol = new SimpleFillSymbol();
//            simplefillsymbol.setDynamicSymbolAttr(dynamicSymbolObject, qualityIndex);
//            simplefillsymbol.drawSymbol(wc, dc, dynamicSymbol2D);
//        } else if (symbolTypeString.equals("GradientColorFillSymbol")) {
//            GradientColorFillSymbol gradientcolorfillsymbol = new GradientColorFillSymbol();
//            gradientcolorfillsymbol.setDynamicSymbolAttr(dynamicSymbolObject, qualityIndex);
//            gradientcolorfillsymbol.drawSymbol(wc, dc, dynamicSymbol2D);
//        }

    }


    /////////////////////////////
    public void getDynamicSymbolSTR(String imageMapID, String symbolContent) {
        try {
            String mapID = imageMapID;
            dynamicMarkerSTR = symbolContent;

            @SuppressWarnings("unused")
            boolean result = connection.saveDynamicSymbolAsClob("IMAGE_MAP", mapID, dynamicMarkerSTR);
//			if(result)
//				System.out.println("标注绘制失败");
//			else
//				System.out.println("标注绘制失败");
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("保存在线标注失败：" + e.getMessage());
        }
    }


    //输入坐标范围
    //String DC = "0,0," + mapLayoutModel.getMainMapExt().width+ ","+ mapLayoutModel.getMainMapExt().height;
    public void initParam(String mapDC, String mapWC) {
        this.DC = mapDC;
        this.WC = mapWC;
        String[] wc_str = WC.split(",");
        String[] dc_str = DC.split(",");
        double[] wc_arr = new double[4];
        double[] dc_arr = new double[4];
        for (int i = 0; i < wc_str.length; i++) {
            wc_arr[i] = Double.parseDouble(wc_str[i]);
            dc_arr[i] = Double.parseDouble(dc_str[i]);
        }

        wc = new Rectangle2D.Double(wc_arr[0], wc_arr[1], wc_arr[2] - wc_arr[0], wc_arr[3] - wc_arr[1]);
        dc = new Rectangle2D.Double(dc_arr[0], dc_arr[1], dc_arr[2] - dc_arr[0], dc_arr[3] - dc_arr[1]);
    }


    public BufferedImage drawSymbol(String jsonCodeColor, boolean isDistrict,Graphics2D dynamicSymbol2D) throws Exception {
        JSONArray jsonArray = JSONArray.fromObject(jsonCodeColor);

        //测试一下
        String path = JUtil.GetWebInfPath() + "district.json";
        if (!isDistrict) {
            path = JUtil.GetWebInfPath() + "district.json";
        }

        InputStreamReader reader = new InputStreamReader(new FileInputStream(
                path), "utf-8");
        FeatureJSON gjson = new FeatureJSON();
        FeatureCollection featureCollection = gjson.readFeatureCollection(reader);
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);

        String all = "";
        for (int k = 0; k < jsonArray.size(); k++) {
            String code = ((JSONObject) jsonArray.get(k)).getString("code");
            String value = ((JSONObject) jsonArray.get(k)).getString("value");
            System.out.println(value);
            String[] rgb = value.substring(1, value.length() - 1).split(",");
            Color color = new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));//
            Feature feature = null;
            int count = 0;
            FeatureIterator iterator = featureCollection.features();
            while (iterator.hasNext()) {
                feature = iterator.next();
                if (feature.getProperty("code").getValue() == null) {
                    continue;
                }
                if (!code.trim().equals(feature.getProperty("code").getValue().toString().trim())) {
                    continue;
                }
                System.out.println(feature.getProperty("code").getValue());
                System.out.println(code);
                System.out.println(code.trim().equals(feature.getProperty("code").getValue().toString().trim()));
                String pointArrJson = "\"pointArr\":[";
                String fillColor = "\"fillColor\":";
                String fillAlpha = "\"fillAlpha\":";
                String outlineColor = "\"outlineColor\":";
                String outlineWidth = "\"outlineWidth\":";
                count++;

                String pointArr = feature.getValue().toArray()[feature.getValue().size() - 1].toString();
                int index = pointArr.indexOf("POLYGON");
                WKTReader reader1 = new WKTReader(geometryFactory);

                Polygon polygon = (Polygon) reader1.read(pointArr.substring(index, pointArr.length()));
                String arrtem = "";
                for (int i = 0; i < polygon.getNumPoints(); i++) {
                    String temp = "{";
                    temp = temp + "\"x\":" + polygon.getCoordinates()[i].x;
                    temp = temp + ",";
                    temp = temp + "\"y\":" + polygon.getCoordinates()[i].y;
                    temp = temp + "}";
                    if ("".equals(arrtem)) {
                        arrtem = arrtem + temp;
                    } else {
                        arrtem = arrtem + "," + temp;
                    }
                }
                pointArrJson += arrtem;
                pointArrJson += "]";
                fillColor += color.hashCode();
                fillAlpha += "0.6";
                outlineColor += "0";
                outlineWidth += "1";
                System.out.println(pointArrJson);
                System.out.println(fillColor);
                System.out.println(fillAlpha);
                System.out.println(outlineColor);
                System.out.println(outlineWidth);
                if ("".equals(all)) {
                    all = "[{" + pointArrJson + "," + fillColor + "," + fillAlpha + "," + outlineColor + "," + outlineWidth + "}";
                } else {
                    all = all + ",{" + pointArrJson + "," + fillColor + "," + fillAlpha + "," + outlineColor + "," + outlineWidth + "}";
                }
            }
        }
        if (all.equals("")) {
            all += "[";
        }
        all += "]";
        //转换为Json后
        System.out.println(all);

        JSONArray jsonArray1 = JSONArray.fromObject(all);

        BufferedImage dynamicSymbolBUF = null;
        try {
            dynamicSymbolBUF = new BufferedImage((int) dc.width, (int) dc.height, BufferedImage.TYPE_BYTE_INDEXED);
            //Graphics2D dynamicSymbol2D = (Graphics2D) dynamicSymbolBUF.createGraphics();
            dynamicSymbolBUF = dynamicSymbol2D.getDeviceConfiguration().createCompatibleImage((int) dc.width, (int) dc.height, Transparency.TRANSLUCENT);
            dynamicSymbol2D.dispose();
//            dynamicSymbol2D = dynamicSymbolBUF.createGraphics();
            for (int i = 0; i < jsonArray1.size(); i++) {
                JSONObject symbol1 = (JSONObject) jsonArray1.get(i);
                drawSymbol(symbol1, wc, dc, dynamicSymbol2D);
            }
            dynamicSymbol2D.dispose();
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
        }
        ImageIO.write(dynamicSymbolBUF, "png", new File("D:\\symbol.png"));
        return  dynamicSymbolBUF;
    }

    //输入一个行政区划编码和对应的颜色值 就可以进行绘制
    private static void ChangeJson() throws Exception {
        String testjson = "[{\"code\":\"1\",\"value\":[225,0,0]},{\"code\":\"2\",\"value\":[0,255,0]},{\"code\":\"3\",\"value\":[0,0,255]},{\"code\":\"4\",\"value\":[225,15,21]}]";
        JSONArray jsonArray = JSONArray.fromObject(testjson);
        //测试一下
        String path = JUtil.GetWebInfPath() + "district.json";

        InputStreamReader reader = new InputStreamReader(new FileInputStream(
                path), "utf-8");
        FeatureJSON gjson = new FeatureJSON();
        FeatureCollection featureCollection = gjson.readFeatureCollection(reader);
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);

        String all = "";
        for (int k = 0; k < jsonArray.size(); k++) {
            String code = ((JSONObject) jsonArray.get(k)).getString("code");
            String value = ((JSONObject) jsonArray.get(k)).getString("value");
            System.out.println(value);
            String[] rgb = value.substring(1, value.length() - 1).split(",");
            Color color = new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));//
            Feature feature = null;
            int count = 0;
            FeatureIterator iterator = featureCollection.features();
            while (iterator.hasNext()) {
                feature = iterator.next();
                if (feature.getProperty("code").getValue() == null) {
                    continue;
                }
                if (!code.trim().equals(feature.getProperty("code").getValue().toString().trim())) {
                    continue;
                }
                System.out.println(feature.getProperty("code").getValue());
                System.out.println(code);
                System.out.println(code.trim().equals(feature.getProperty("code").getValue().toString().trim()));
                String pointArrJson = "\"pointArr\":[";
                String fillColor = "\"fillColor\":";
                String fillAlpha = "\"fillAlpha\":";
                String outlineColor = "\"outlineColor\":";
                String outlineWidth = "\"outlineWidth\":";
                count++;

                String pointArr = feature.getValue().toArray()[feature.getValue().size() - 1].toString();
                int index = pointArr.indexOf("POLYGON");
                WKTReader reader1 = new WKTReader(geometryFactory);

                Polygon polygon = (Polygon) reader1.read(pointArr.substring(index, pointArr.length()));
                String arrtem = "";
                for (int i = 0; i < polygon.getNumPoints(); i++) {
                    String temp = "{";
                    temp = temp + "\"x\":" + polygon.getCoordinates()[i].x;
                    temp = temp + ",";
                    temp = temp + "\"y\":" + polygon.getCoordinates()[i].y;
                    temp = temp + "}";
                    if ("".equals(arrtem)) {
                        arrtem = arrtem + temp;
                    } else {
                        arrtem = arrtem + "," + temp;
                    }
                }
                pointArrJson += arrtem;
                pointArrJson += "]";
                fillColor += color.hashCode();
                fillAlpha += "0.6";
                outlineColor += "0";
                outlineWidth += "1";
                System.out.println(pointArrJson);
                System.out.println(fillColor);
                System.out.println(fillAlpha);
                System.out.println(outlineColor);
                System.out.println(outlineWidth);
                if ("".equals(all)) {
                    all = "[{" + pointArrJson + "," + fillColor + "," + fillAlpha + "," + outlineColor + "," + outlineWidth + "}";
                } else {
                    all = all + ",{" + pointArrJson + "," + fillColor + "," + fillAlpha + "," + outlineColor + "," + outlineWidth + "}";
                }
            }
        }
        if (all.equals("")) {
            all += "[";
        }
        all += "]";
        //转换为Json后
        System.out.println(all);

        DrawDynamicSymbol drawDynamicSymbol = new DrawDynamicSymbol();
        drawDynamicSymbol.initParam("0,0,778,447", "120.5,31.2, 120.8,31.4");

        JSONArray jsonArray1 = JSONArray.fromObject(all);


        BufferedImage dynamicSymbolBUF = null;
        try {
            dynamicSymbolBUF = new BufferedImage((int) drawDynamicSymbol.dc.width, (int) drawDynamicSymbol.dc.height, BufferedImage.TYPE_BYTE_INDEXED);
            Graphics2D dynamicSymbol2D = (Graphics2D) dynamicSymbolBUF.createGraphics();
            dynamicSymbolBUF = dynamicSymbol2D.getDeviceConfiguration().createCompatibleImage((int) drawDynamicSymbol.dc.width, (int) drawDynamicSymbol.dc.height, Transparency.TRANSLUCENT);
            dynamicSymbol2D.dispose();
            dynamicSymbol2D = dynamicSymbolBUF.createGraphics();
            for (int i = 0; i < jsonArray1.size(); i++) {
                JSONObject symbol1 = (JSONObject) jsonArray1.get(i);
                drawDynamicSymbol.drawSymbol(symbol1, drawDynamicSymbol.wc, drawDynamicSymbol.dc, dynamicSymbol2D);
            }
            dynamicSymbol2D.dispose();

        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
        }

        ImageIO.write(dynamicSymbolBUF, "png", new File("D:\\symbol.png"));
    }

    public static void main(String[] args) throws Exception {
        ChangeJson();
    }


}
