package com.carto.imagemap;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esri.arcgis.geoprocessing.tools.analyst3dtools.Int;
import com.carto.model.*;
import net.sf.json.JSONObject;

public class PrintServlet extends HttpServlet {

    public PrintServlet() {
        super();
    }


    public void destroy() {
        super.destroy();
    }


    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("image/png");
        response.setHeader("Content-type", "image/png;charset=utf-8");
        ServletOutputStream sos = null;
        try {
            sos = response.getOutputStream();
            getFinalImageMap(request, sos);
            sos.flush();
            sos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("绘制地图失败：" + e.toString());
        }
    }


    //获取前台显示影像地图
    public void getFinalImageMap(HttpServletRequest request, ServletOutputStream out) {
        try {
            byte[] bytes = null;
            request.setCharacterEncoding("utf-8");
            String mapUrl = request.getParameter("mapUrl");
            String layoutStr = request.getParameter("layout");
            String extent = request.getParameter("extent");
            String mapFrameId = request.getParameter("mapFrameId");
            String title = request.getParameter("title");
            String scale = request.getParameter("scale");
            String compassId = request.getParameter("compassId");
            String fangdaStr = request.getParameter("fangda");
            //放大倍数
            int fangda = Integer.parseInt(fangdaStr);
            if ("".equals(layoutStr)) {
                return;
            }
            JSONObject json = JSONObject.fromObject(layoutStr);
            List<JSONObject> jsons = new ArrayList();
            jsons.add(json);
            MapLayoutModel2 mapLayoutModel = new MapLayoutModel2(jsons);
            //放大模板
            mapLayoutModel.scaleLayoutModel(fangda);
            //重新计算比例尺
            scale = "" + (Double.parseDouble(scale) / fangda * 1.0);
            ImageMap2 image = new ImageMap2();
            image.mapLayoutModel = mapLayoutModel;

            // 地图元素
            scale = "" + (Double.parseDouble(scale) / fangda * 1.0);
            image.dynaicMapUrl = mapUrl;
            image.bbox = extent;
            image.title = title;
            image.mapFrameId = mapFrameId;
            image.scale = scale;
            image.fangda = fangda;
            image.compassId = compassId;
            image.mapFrame = new MapFrame(mapFrameId);
            image.mapScale = new MapScale(scale);
            image.mapCompass = new MapCompass(compassId);

            image.dynamicWebService = new DynamicWebService(image.dynaicMapUrl, image.bbox, "");

//			ImageMap imagemap=new ImageMap(mapID);
            //bytes=imagemap.drawImageMapL();
            bytes = image.drawImageMap(false);
            ;
            out.write(bytes);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("绘制影像地图失败：" + e.toString());
        }
    }


    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    public void init() throws ServletException {
        // Put your code here
    }

}
