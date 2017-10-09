package com.carto.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

import net.sf.json.JSONObject;

public class ImageMap2 {
    // 资源数据
    public String dynaicMapUrl;// 动态服务地图资源
    public String title;// 图名
    public String bbox;// 地理范围

    public String mapFrameId;// 图框的Id 100 101 102 103
    public String scale;// 比例尺，数值
    public String compassId;// 指北针Id; 100

    public String chartUrl;// 统计图表
    public String legendUrl;// 统计图表图例
    public String legend;// 地图服务的图例

    //放大，是按照纸张比例打出
    public int fangda;

    // 地图元素
    public MapFrame mapFrame;
    public MapScale mapScale;
    public MapCompass mapCompass;
    public DynamicWebService dynamicWebService;

    // 影像图形布局 可以根据前台传入的值
    public MapLayoutModel2 mapLayoutModel;


    //记录整个图面的范围，计算文字的位置文字（居中靠边）
    private int mapExtent_x;
    @SuppressWarnings("unused")
    private int mapExtent_y;
    private int mapExtent_w;
    @SuppressWarnings("unused")
    private int mapExtent_h;

    public byte[] drawImageMap(boolean isHighQuality) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {

            // 1、创建地图构建主图位置
            BufferedImage theImageMap = new BufferedImage((int) mapLayoutModel.getMapExtent().getWidth(), (int) mapLayoutModel.getMapExtent().getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D finalg2d = (Graphics2D) theImageMap.createGraphics();
            theImageMap = finalg2d.getDeviceConfiguration().createCompatibleImage((int) mapLayoutModel.getMapExtent().getWidth(), (int) mapLayoutModel.getMapExtent().getHeight(), Transparency.TRANSLUCENT);
            finalg2d.dispose();
            finalg2d = (Graphics2D) theImageMap.createGraphics();

            //2、绘制图框
            if (mapLayoutModel.getMapFrameExtent().getWidth() > 0.0) {
                BufferedImage frameBuffer = mapFrame.getFrameBufferedImage();
                finalg2d.drawImage(frameBuffer, mapLayoutModel.getMapFrameLocation().x, mapLayoutModel.getMapFrameLocation().y, (int) mapLayoutModel.getMapFrameExtent().getWidth(), (int) mapLayoutModel.getMapFrameExtent().getHeight(), null);
                //开始确定主图的大小
                mapExtent_x = mapLayoutModel.getMainMapExtent().x;
                mapExtent_y = mapLayoutModel.getMainMapExtent().y;
                mapExtent_w = (int) mapLayoutModel.getMainMapExtent().getWidth();
                mapExtent_h = (int) mapLayoutModel.getMainMapExtent().getHeight();
            } else {
                //目前是有bug的
                //绘制黑边框, 边框尺寸的控制  //不同尺度的控制stroke变化的
                double left = mapLayoutModel.getMainMapExtent().getX() - mapLayoutModel.getMapExtent().getX();
                double top = mapLayoutModel.getMainMapExtent().getY() - mapLayoutModel.getMapExtent().getY();
                double right = mapLayoutModel.getMapExtent().width - mapLayoutModel.getMainMapExtent().width - left;
                double bottom = mapLayoutModel.getMapExtent().height - mapLayoutModel.getMainMapExtent().height - top;
                double min1 = Math.min(left, right);
                double min2 = Math.min(top, bottom);
                double min = Math.min(min1, min2);
                float gap = (float) min;
                float strokeWidth = (float) (min / 5);
                finalg2d.setStroke(new BasicStroke(strokeWidth));
                finalg2d.setPaint(Color.black);
                finalg2d.drawRect((int) (mapLayoutModel.getMainMapExtent().getX() - gap), (int) (mapLayoutModel.getMainMapExtent().getY() - gap),
                        (int) (mapLayoutModel.getMainMapExtent().getWidth() + gap * 2), (int) (mapLayoutModel.getMainMapExtent().getHeight() + gap * 2));
                mapExtent_x = (int) (mapLayoutModel.getMainMapExtent().getX() - gap);
                mapExtent_y = (int) (mapLayoutModel.getMainMapExtent().getY() - gap);
                mapExtent_w = (int) (mapLayoutModel.getMainMapExtent().getWidth() + gap * 2);
                mapExtent_h = (int) (mapLayoutModel.getMainMapExtent().getHeight() + gap * 2);
            }
            //ImageIO.write(theImageMap, "png", new File("D:\\mapFrame.png"));

            //绘制动态服务
            int width = mapLayoutModel.getMainMapExtent().width;
            int height = mapLayoutModel.getMainMapExtent().height;
            BufferedImage frameBuffer = dynamicWebService.export(mapLayoutModel.getMainMapExtent().width, mapLayoutModel.getMainMapExtent().height);
            finalg2d.drawImage(frameBuffer, mapLayoutModel.getMainMapExtent().x, mapLayoutModel.getMainMapExtent().y, mapLayoutModel.getMainMapExtent().width, mapLayoutModel.getMainMapExtent().height, null);
            ImageIO.write(frameBuffer, "png", new File("D:\\map" + fangda + ".png"));

            //绘制分级图层
           /* DrawDynamicSymbol drawDynamicSymbol = new DrawDynamicSymbol();
            drawDynamicSymbol.initParam("0,0," + (int) mapLayoutModel.getMainMapExtent().width + "," + (int) mapLayoutModel.getMainMapExtent().height, this.bbox);
            String testjson = "[{\"code\":\"1\",\"value\":[225,0,0]},{\"code\":\"2\",\"value\":[0,255,0]},{\"code\":\"3\",\"value\":[0,0,255]},{\"code\":\"4\",\"value\":[225,15,21]}]";
            drawDynamicSymbol.drawSymbol(testjson, true,finalg2d);*/


            //7、绘制指北针
            if (mapLayoutModel.getCompassExtent().getWidth() > 0.0) {
                BufferedImage compassBi = mapCompass.getCompassBuffer();
                Rectangle compassRect = getDrawRect(new Rectangle(0, 0, compassBi.getWidth(), compassBi.getHeight()),
                        new Rectangle((int) mapLayoutModel.getCompassExtent().getX(), (int) mapLayoutModel.getCompassExtent().getY(),
                                (int) mapLayoutModel.getCompassExtent().width, (int) mapLayoutModel.getCompassExtent().height));
                finalg2d.drawImage(compassBi, compassRect.x, compassRect.y, compassRect.width, compassRect.height, null);
            }

            //8、绘制比例尺
            if (mapLayoutModel.getScaleExtent().getWidth() > 0.0) {
                finalg2d.setPaint(Color.white);
                BufferedImage scaleBi = mapScale.DrawImageScale();
                Rectangle scaleRect = getDrawRect(new Rectangle(0, 0, scaleBi.getWidth(), scaleBi.getHeight()),
                        new Rectangle((int) mapLayoutModel.getScaleExtent().getX(), (int) mapLayoutModel.getScaleExtent().getY(),
                                (int) mapLayoutModel.getScaleExtent().getWidth(), (int) mapLayoutModel.getScaleExtent().getHeight()));
                finalg2d.fillRect(scaleRect.x, scaleRect.y, scaleRect.width, scaleRect.height);
                finalg2d.drawImage(scaleBi, scaleRect.x, scaleRect.y, scaleRect.width, scaleRect.height, null);
                finalg2d.drawRect(scaleRect.x, scaleRect.y, scaleRect.width, scaleRect.height);
            }

            //9、绘制图名:根据宽高判断类型，再绘制  字体的大小要随着他变化
            if (mapLayoutModel.getTitleExtent().getWidth() > 0.0) {
                if (mapLayoutModel.getTitleExtent().width > mapLayoutModel.getTitleExtent().height) {
                    //绘制图名只是要
                    int defaultFontSize = 30 * fangda;
                    Font font = new Font("宋体", Font.BOLD, defaultFontSize);
                    finalg2d.setFont(font);
                    finalg2d.setColor(Color.BLACK);
                    FontMetrics fm = finalg2d.getFontMetrics();
                    Rectangle2D rec = fm.getStringBounds(this.title, finalg2d);
                    Double scale = mapLayoutModel.getTitleExtent().getWidth() / rec.getWidth();

                    font = new Font("宋体", Font.BOLD, (int) (defaultFontSize * scale));
                    finalg2d.setFont(font);
                    finalg2d.setColor(Color.BLACK);
                    Rectangle2D rec2 = fm.getStringBounds(this.title, finalg2d);
                    Double titleWidth = rec2.getWidth();
                    if (mapLayoutModel.getMainMapExtent().getWidth() < rec2.getWidth() + mapLayoutModel.getTitleExtent().getX()) {
                        titleWidth = mapLayoutModel.getMainMapExtent().getWidth();
                    }
                    finalg2d.drawString(this.title, (int) mapLayoutModel.getTitleExtent().getX(), (int) (mapLayoutModel.getTitleExtent().getY() - mapLayoutModel.getTitleExtent().getHeight() / 2 + rec2.getHeight() / 2));

                } else {
                    finalg2d.setColor(Color.BLACK);
                    finalg2d.setFont(new Font("宋体", Font.BOLD, (int) (30 * fangda)));
                    for (int i = 0; i < title.length(); i++) {
                        finalg2d.drawString(title.substring(i, i + 1), (int) mapLayoutModel.getTitleExtent().getX(), (int) mapLayoutModel.getTitleExtent().getY() + (int) (30) * (i + 1));
                    }
                }
            }
            ImageIO.write(theImageMap, "png", new File("D:\\ss.png"));
        } catch (Exception e) {

        }
        return outputStream.toByteArray();
    }


    //判定布局要素在地图幅面的方位，1,2,3,4个象限；然后确定绘制要素图片的位置
    private Rectangle getDrawRect(Rectangle originSize, Rectangle resultSize) {
        Rectangle resultRect = new Rectangle();
        double scale1 = originSize.getWidth() / resultSize.getWidth();
        double scale2 = originSize.getHeight() / resultSize.getHeight();
        double scale = scale1 > scale2 ? scale1 : scale2;

        double sx, sy, width, height;
        if ((this.mapLayoutModel.getMapExtent().getCenterX() > resultSize.getCenterX()) //左上角
                && (this.mapLayoutModel.getMapExtent().getCenterY() > resultSize.getCenterY())) {
            sx = resultSize.x;
            sy = resultSize.y;
            width = originSize.getWidth() * 1 / scale;
            height = originSize.getHeight() * 1 / scale;
        } else if ((this.mapLayoutModel.getMapExtent().getCenterX() < resultSize.getCenterX()) //右上角
                && (this.mapLayoutModel.getMapExtent().getCenterY() > resultSize.getCenterY())) {
            sx = (resultSize.getWidth() - (originSize.getWidth() * 1 / scale)) + resultSize.x;
            sy = resultSize.y;
            width = originSize.getWidth() * 1 / scale;
            height = originSize.getHeight() * 1 / scale;
        } else if ((this.mapLayoutModel.getMapExtent().getCenterX() > resultSize.getCenterX()) //左下角
                && (this.mapLayoutModel.getMapExtent().getCenterY() < resultSize.getCenterY())) {
            sx = resultSize.x;
            sy = (resultSize.getHeight() - (originSize.getHeight() * 1 / scale)) + resultSize.y;
            width = originSize.getWidth() * 1 / scale;
            height = originSize.getHeight() * 1 / scale;
        } else {
            sx = (resultSize.getWidth() - (originSize.getWidth() * 1 / scale)) + resultSize.x;
            sy = (resultSize.getHeight() - (originSize.getHeight() * 1 / scale)) + resultSize.y;
            width = originSize.getWidth() * 1 / scale;
            height = originSize.getHeight() * 1 / scale;
        }
        resultRect.x = (int) sx;
        resultRect.y = (int) sy;
        resultRect.width = (int) width;
        resultRect.height = (int) height;
        return resultRect;
    }

    public static void main(String[] args) throws IOException {

        // 输入地图参数
        String jstr = "{\"MODEL_NAME\":\"测试\",\"MAP_X\":0,\"MAP_Y\":0,\"MAP_WIDTH\":800,\"MAP_HEIGHT\":500,\"MAPFRAME_X\":0,\"MAPFRAME_Y\":0,\"MAPFRAME_WIDTH\":800,\"MAPFRAME_HEIGHT\":500,\"METALOCATION_X\":0,\"METALOCATION_Y\":0,\"META_WIDTH\":0,\"META_HEIGHT\":0,\"MAINMAPLOCATION_X\":12,\"MAINMAPLOCATION_Y\":11,\"MAINMAPEXTENT_W\":778,\"MAINMAPEXTENT_H\":476,\"TITLELOCATION_X\":83,\"TITLELOCATION_Y\":49,\"TITLEEXTENT_W\":198,\"TITLEEXTENT_H\":38,\"LEGENDLOCATION_X\":588,\"LEGENDLOCATION_Y\":289,\"LEGENDEXTENT_W\":198,\"LEGENDEXTENT_H\":198,\"STACHARTLOCATION_X\":12,\"STACHARTLOCATION_Y\":280,\"STACHARTEXTENT_W\":198,\"STACHARTEXTENT_H\":118,\"SCALELOCATION_X\":12,\"SCALELOCATION_Y\":467,\"SCALE_WIDTH\":198,\"SCALE_HEIGHT\":20,\"COMPASSLOCATION_X\":738,\"COMPASSLOCATION_Y\":11,\"COMPASS_WIDTH\":48,\"COMPASS_HEIGHT\":38}";
        // 资源数据
        String dynaicMapUrl = "http://115.28.75.54:6080/arcgis/rest/services/TJMap/MapServer";// 动态服务地图资源
        String title = "天津市土地利用现状分布图";// 图名
        String bbox = "369535.68578333326,4334248.417369045,709050.760742857,4473283.652821425";
        String mapFrameId = "100";// 图框的Id 100 101 102 103
        String scale = "3208010.040277547";// 比例尺，数值
        String compassId = "100";// 指北针Id; 100

        int fangda = 20;
        String chartUrl;// 统计图表
        String legendUrl;// 统计图表图例
        String legend;// 地图服务的图例

        // 影像图形布局 可以根据前台传入的值
        MapLayoutModel2 mapLayoutModel;
        // 输入其他参数
        JSONObject json = JSONObject.fromObject(jstr);
        List<JSONObject> jsons = new ArrayList();
        jsons.add(json);
        mapLayoutModel = new MapLayoutModel2(jsons);
        mapLayoutModel.scaleLayoutModel(fangda);
        scale = "" + (Double.parseDouble(scale) / fangda * 1.0);

        // 地图元素
        MapFrame mapFrame = new MapFrame(mapFrameId);
        MapScale mapScale = new MapScale(scale);
        MapCompass mapCompass = new MapCompass(compassId);


        ImageMap2 image = new ImageMap2();
        image.dynaicMapUrl = dynaicMapUrl;
        image.bbox = bbox;
        image.title = title;
        image.mapFrameId = mapFrameId;
        image.scale = scale;
        image.fangda = fangda;
        image.compassId = compassId;
        image.mapFrame = new MapFrame(mapFrameId);
        image.mapScale = new MapScale(scale);
        image.mapCompass = new MapCompass(compassId);
        image.mapLayoutModel = mapLayoutModel;
        image.dynamicWebService = new DynamicWebService(dynaicMapUrl, bbox, scale);
        image.drawImageMap(false);
        System.out.print(mapLayoutModel.getLegendExtent().width);
    }
}
