package com.carto.imagemap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Properties;


import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.carto.model.CreateWebService;
import com.carto.model.ImageMap;
import com.carto.model.SaveTiff;
import com.dbconn.ConnOrcl;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

@SuppressWarnings("serial")
public class DrawImageMapServlet extends HttpServlet {
	public DrawImageMapServlet(){
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		String serviceType = request.getParameter("serviceType"); 
		if(serviceType.equals("getFinalImageMap")){
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
				System.out.println("绘制影像地图失败："+e.toString());
			}	
		}  
		else if(serviceType.equals("saveMap")){
			response.setContentType("image/png");
			response.setHeader("Content-type", "image/png;charset=utf-8");
//			PrintWriter sos = null;
			ServletOutputStream sos = null;
			try {
//				sos = response.getWriter();
				sos = response.getOutputStream();
				//画影像地图
				saveMap(request, sos);
				sos.flush();
				sos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("保存影像地图失败："+e.toString());
			}	
		} 
		else if(serviceType.equals("saveHQMapToLocal")){
			saveHQMapToLocal(request, response);
		} 
		else if(serviceType.equals("getHQMap")){
			response.setContentType("image/png");
			response.setHeader("Content-type", "image/png;charset=utf-8");
			ServletOutputStream sos = null;
			try {
				sos = response.getOutputStream();
				getHQMap(request, sos);
				sos.flush();
				sos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("绘制影像地图失败："+e.toString());
			}	
		}else if(serviceType.equals("saveAttr")){
			saveAttr(request, response);
		} 
		else if(serviceType.equals("saveAttrtoSpace"))
		{
			response.setHeader("Content-Type", "text/html;charset=utf-8");
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			saveAttrtoSpace(request,response,out);			
			out.flush();
			out.close();
		}
		///LB   5-6 统计报表在线输出
		else if (serviceType.equals("getDataReport"))
		{
			response.setHeader("Content-Type", "text/html;charset=utf-8");
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			getDataReport(request,out);			
			out.flush();
			out.close();
		}
		//ls6-28
		else if(serviceType.equals("drawBackground")){
			response.setContentType("image/png");
			response.setHeader("Content-type", "image/png;charset=utf-8");
			
			ServletOutputStream sos = null;
				try {
					sos = response.getOutputStream();
					//画影像地图
					drawBackground(request,sos);
					sos.flush();
					sos.close();
					}
				catch (IOException e) {
				// TODO Auto-generated catch block
					System.out.println("绘制影像地图失败："+e.toString());
				}
			}
		
		//lb 9-3
		else if(serviceType.equals("saveHQMapToSpace"))
		{
			response.setContentType("image/png");
			response.setHeader("Content-type", "image/png;charset=utf-8");
			PrintWriter sos = null;
			try
			{
				sos = response.getWriter();
				//画影像地图
				saveHQMapToSpace(request, response, sos);
				sos.flush();
				sos.close();
			}
			catch (Exception e) {
				// TODO: handle exception
				System.out.println("保存高清影像地图失败："+e.toString());
			}
		}
		
		///lb 9-4
		else if(serviceType.equals("saveGeoTiff")){
			response.setContentType("image/png");
			response.setHeader("Content-type", "image/png;charset=utf-8");
			PrintWriter sos = null;
			try {
				sos = response.getWriter();
				//画影像地图
				saveGeoTiff(request, sos);
				sos.flush();
				sos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("生成geotiff："+e.toString());
			}	
		}
	}
	
	///获取统计报表数据、
	public void getDataReport(HttpServletRequest request,PrintWriter out)
	{
		String mapID = "";
		try {
			request.setCharacterEncoding("utf-8");
			mapID = URLDecoder.decode(request.getParameter("mapID"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ImageMap imagemap = new ImageMap(mapID);
		
		String chartSTR = imagemap.chartSrc;
		String[] chartArr = chartSTR.split("@");
		String data = new String();
		
		for(int i=0;i<chartArr.length;i++)
		{
			System.out.println(chartArr[i].toString());
			String tableName = chartArr[i].split("\\?")[1].split("&")[0].split("=")[1];
			String fieldENG = chartArr[i].split("\\?")[1].split("&")[6].split("=")[1];
			fieldENG = "REGN_CN;Y_EAR;" + fieldENG; 
			String[] fieldENG_Arr = fieldENG.split(";");
			String fieldCN = chartArr[i].split("\\?")[1].split("&")[7].split("=")[1];
			fieldCN = "地区名称;年 份;" + fieldCN;
			String[] fieldCN_Arr = fieldCN.split(";");
			String tableNameCN = chartArr[i].split("\\?")[1].split("&")[8].split("=")[1];
 
			
			ConnOrcl conn = ConnOrcl.getInstance();
			String  dataPer = conn.getDataReport(tableName, fieldENG_Arr, fieldCN_Arr);
			
			dataPer = tableNameCN + "&" + fieldCN + "&" + dataPer;

			data = data + "#" + dataPer;
			
		}
		data = data.substring(1, data.length());		
		out.write(data);
	}

	//获取前台显示影像地图
	public void getFinalImageMap(HttpServletRequest request, ServletOutputStream out){
		try {
			byte[] bytes=null;
			request.setCharacterEncoding("utf-8");
			String mapID= URLDecoder.decode(request.getParameter("mapID"), "utf-8");
			ImageMap imagemap=new ImageMap(mapID);
			//bytes=imagemap.drawImageMapL();
			bytes=imagemap.drawImageMap(false);
			out.write(bytes);		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("绘制影像地图失败："+e.toString());
		}
	}

	//保存较高质量影像图到客户端，ZIP包
	public void saveHQMapToLocal(HttpServletRequest request, HttpServletResponse response){
		try {
			byte[] bytes=null;
			String mapID=request.getParameter("mapID");
			ImageMap imagemap=new ImageMap(mapID);
			String spatialReference = request.getParameter("spatialRef");
			String imagemapServiceurl = request.getParameter("imagemapServiceurl");

			String fileName = URLDecoder.decode(imagemap.mapName,"utf-8");			
			

			String mapName = "图名: " + URLDecoder.decode(imagemap.mapName,"utf-8");		
			String author = "制作者: " + mapID.substring(0, mapID.lastIndexOf("_"));
			String time = "成图时间: " + mapID.substring(mapID.lastIndexOf("_")+1, mapID.length());
			String extent = "成图范围: " + imagemap.mapExtent;
			String layoutID = "布局模板: " + String.valueOf(imagemap.mapLayoutModel.getLayoutId());
			String imageMeta = "影像数据: " + imagemap.imgSrc;
			String vectorMeta = "矢量数据: " + imagemap.vtrSrc;
			String symbolMeta = "专题数据: " + imagemap.chartSrc;
			String mapAttr = mapName + "@_@" + author + "@_@" + time + "@_@" + spatialReference + "@_@" + extent + "@_@" + layoutID + "@_@"+ imagemapServiceurl + "@_@"  + imageMeta + "@_@" + vectorMeta + "@_@" + symbolMeta;

			//从配置文件读取路径 根据路径及用户名建文件夹
			CreateWebService cws = new CreateWebService();
			Properties ps = cws.ps;
			String userName = mapID.substring(0, mapID.lastIndexOf("_"));
			String hqMapPath = ps.getProperty("USERSPACE")+"//"+userName;
			File prehqfile= new File(hqMapPath);
			if (!prehqfile.exists()) {  
				prehqfile.mkdir();
			} 
			
			String outZipFileName =   hqMapPath + "\\"+ mapID + ".zip";
			File file = new File(outZipFileName);
			//判断文件是否存在
			if (file.exists()){
				System.out.println("高清文件存在");
				// 以流的形式下载文件。
				InputStream fis = new BufferedInputStream(new FileInputStream(file));
				byte[] buffer = new byte[fis.available()];
				fis.read(buffer);
				fis.close();
				// 清空response
				response.reset();
				// 设置response的Header
				response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
				response.addHeader("Content-Length", "" + file.length());
				OutputStream sos = new BufferedOutputStream(response.getOutputStream());
				//response.setContentType("application/octet-stream");
				response.setHeader("Content-type", "application/octet-stream;charset=utf-8");
				sos.write(buffer);
				sos.flush();
				sos.close();
			}else
			{
				System.out.println("高清文件不存在，重新获取");
				//获取图像byte
				bytes=imagemap.drawImageMap(true);
				
				//outpngFileName路径下生成png图片
				String outpngFileName =   hqMapPath + "\\"+ fileName + ".png";
				File hqMapfile = new File(outpngFileName);
				FileOutputStream imgfile = new FileOutputStream(hqMapfile);
				imgfile.write(bytes);
				imgfile.flush();
				imgfile.close();
				
				
				//mapAttrFileName路径下生成txt文件
				String mapAttrFileName =   hqMapPath + "\\"+ fileName + "_" + System.currentTimeMillis() + ".txt";
				FileWriter fw = new FileWriter(new File(mapAttrFileName),true);//追加
				//				BufferedWriter = new BufferWriter(fw);
				PrintWriter pw = new PrintWriter(fw);
				String [] attrArray = mapAttr.split("@_@");
				for(int i=0; i < attrArray.length; i++){
					pw.printf("%s"+"%n",attrArray[i]);				
				}			
				pw.close();
				// 通过生成的txt文件读取buffer
				File txtfile = new File(mapAttrFileName);
				FileInputStream fis = new FileInputStream(txtfile);
				byte[] buffer = new byte[fis.available()];
				// 以流的形式下载文件。
				fis.read(buffer);
				fis.close();
				
//				String outZipFileName =   hqMapPath + "\\"+ fileName + ".zip";
				//ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(new File(outZipFileName)));
				ZipOutputStream zipOut = new ZipOutputStream(new File(outZipFileName));			
				zipOut.setEncoding("gb2312");
				zipOut.putNextEntry(new ZipEntry(fileName + "_" + System.currentTimeMillis() + ".txt"));
				zipOut.write(buffer);
				zipOut.putNextEntry(new ZipEntry(fileName + ".png"));
				zipOut.write(bytes);
				zipOut.closeEntry();
				zipOut.close();
				
				txtfile.delete();
				hqMapfile.delete();
				// path是指欲下载的文件的路径。
				File zipfile = new File(outZipFileName);

				// 以流的形式下载文件。
				InputStream zipis = new BufferedInputStream(new FileInputStream(zipfile));
				byte[] buffer1 = new byte[zipis.available()];
				zipis.read(buffer1);
				zipis.close();
				// 清空response
				response.reset();
				// 设置response的Header
				response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
				response.addHeader("Content-Length", "" + zipfile.length());
				OutputStream sos = new BufferedOutputStream(response.getOutputStream());
				response.setHeader("Content-type", "application/octet-stream;charset=utf-8");
				sos.write(buffer1);
				sos.flush();
				sos.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("下载影像图到本地失败："+e.toString());
		}
	}
//=========================================================================================================
	//生成预览较高质量影像图
	public void getHQMap(HttpServletRequest request, ServletOutputStream out){

		try {
			byte[] bytes=null;
			request.setCharacterEncoding("utf-8");
			String mapID=request.getParameter("mapID");
			String spatialReference = request.getParameter("spatialRef");
			String imagemapServiceurl = request.getParameter("imagemapServiceurl");
			
			ImageMap imagemap=new ImageMap(mapID);
			String fileName = URLDecoder.decode(imagemap.mapName,"utf-8");
		
			String mapName = "图名: " + URLDecoder.decode(imagemap.mapName,"utf-8");		
			String author = "制作者: " + mapID.substring(0, mapID.lastIndexOf("_"));
			String time = "成图时间: " + mapID.substring(mapID.lastIndexOf("_")+1, mapID.length());
			String extent = "成图范围: " + imagemap.mapExtent;
			String layoutID = "布局模板: " + String.valueOf(imagemap.mapLayoutModel.getLayoutId());
			String imageMeta = "影像数据: " + imagemap.imgSrc;
			String vectorMeta = "矢量数据: " + imagemap.vtrSrc;
			String symbolMeta = "专题数据: " + imagemap.chartSrc;
			String mapAttr = mapName + "@_@" + author + "@_@" + time + "@_@" + spatialReference + "@_@" + extent + "@_@" + layoutID + "@_@"+ imagemapServiceurl + "@_@"  + imageMeta + "@_@" + vectorMeta + "@_@" + symbolMeta;
			//bytes = imagemap.drawImageMapH();
			
			//从配置文件读取路径 根据路径及用户名建文件夹
			CreateWebService cws = new CreateWebService();
			Properties ps = cws.ps;
			String userName = mapID.substring(0, mapID.lastIndexOf("_"));
			String hqMapPath = ps.getProperty("USERSPACE")+"//"+userName;
			File prehqfile= new File(hqMapPath);
			if (!prehqfile.exists()) {  
				prehqfile.mkdir();
			}  

			//获取图像byte
			bytes=imagemap.drawImageMap(true);
			
			//outpngFileName路径下生成png图片
			String outpngFileName =  hqMapPath + "\\"+ fileName + ".png";
			File fi = new File(outpngFileName);
			FileOutputStream imgfile = new FileOutputStream(fi);
			imgfile.write(bytes);
			imgfile.flush();
			imgfile.close();
			
			
			//mapAttrFileName路径下生成txt文件
			String mapAttrFileName = hqMapPath + "\\" + fileName + "_" + System.currentTimeMillis() + ".txt";
			FileWriter fw = new FileWriter(new File(mapAttrFileName),true);//追加
			//				BufferedWriter = new BufferWriter(fw);
			PrintWriter pw = new PrintWriter(fw);
			String [] attrArray = mapAttr.split("@_@");
			for(int i=0; i < attrArray.length; i++){
				pw.printf("%s"+"%n",attrArray[i]);				
			}			
			pw.close();
			// 通过生成的txt文件读取buffer
			File file = new File(mapAttrFileName);
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[fis.available()];
			// 以流的形式下载文件。
			fis.read(buffer);
			fis.close();
			
			
			String outZipFileName =  hqMapPath  + "\\"+ mapID + ".zip";
			//ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(new File(outZipFileName)));
			ZipOutputStream zipOut = new ZipOutputStream(new File(outZipFileName));			
			zipOut.setEncoding("gb2312");
			zipOut.putNextEntry(new ZipEntry(fileName + "_" + System.currentTimeMillis() + ".txt"));
			zipOut.write(buffer);
			zipOut.putNextEntry(new ZipEntry(fileName + ".png"));
			zipOut.write(bytes);
			zipOut.closeEntry();
			zipOut.close();
//			ZipOrNot=1;
			
			file.delete();
			fi.delete();
			
			out.write(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("绘制高清影像地图失败："+e.toString());
		}
	}
//__________________________________________________________________________________________________________
	//保存较高质量的影像图到用户空间
	public void saveMap(HttpServletRequest request, ServletOutputStream out){
		try {
			request.setCharacterEncoding("utf-8");
			byte[] bytes=null;
			String mapID=URLDecoder.decode(request.getParameter("mapID"), "utf-8");
			ImageMap imagemap=new ImageMap(mapID);
			bytes = imagemap.drawImageMap(true);
			out.write(bytes);	
			//String filename = request.getParameter("filename");
			
			//从配置文件读取路径 根据路径及用户名建文件夹
//			CreateWebService cws = new CreateWebService();
//			Properties ps = cws.ps;
//			String userName = mapID.substring(0, mapID.lastIndexOf("_"));
//			String userName = mapID;
//			String hqMapPath = ps.getProperty("USERSPACE")+"//"+userName;
//			File prehqfile= new File(hqMapPath);
//			if (!prehqfile.exists()) {  
//				prehqfile.mkdir();
//			}  
			
			//创建影像地图类对象
//			ImageMap imagemap=new ImageMap(mapID);
//			String filename = userName+"_"+imagemap.mapName+"_"+System.currentTimeMillis();
//			String realFilePath = hqMapPath + "//"  + filename + ".png";
			//获取影像地图
			//bytes = imagemap.drawImageMap(true);
//			bytes=imagemap.drawImageMap(false);
//			File saveFile = new File(realFilePath);
//			FileOutputStream fos = new FileOutputStream(saveFile);
//			fos.write(bytes);
//			fos.flush();
//			fos.close();

//			out.write(realFilePath);
//			System.out.println(realFilePath);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("保存影像地图到用户空间失败："+e.toString());
		}
	}

	//保存高清图到用户空间
	public void saveHQMapToSpace(HttpServletRequest request, HttpServletResponse response, PrintWriter out){
		try {
			byte[] bytes=null;
			String mapID=request.getParameter("mapID");
			ImageMap imagemap=new ImageMap(mapID);
			String fileName = URLDecoder.decode(imagemap.mapName,"utf-8");	
			
			//从配置文件读取路径 根据路径及用户名建文件夹
			CreateWebService cws = new CreateWebService();
			Properties ps = cws.ps;
			String userName = mapID.substring(0, mapID.lastIndexOf("_"));
			String hqMapPath = ps.getProperty("USERSPACE")+"//"+userName;
			File prehqfile= new File(hqMapPath);
			if (!prehqfile.exists()) {  
				prehqfile.mkdir();
			} 
			
				bytes=imagemap.drawImageMap(true);
				
				//outpngFileName路径下生成png图片
				String outpngFileName =   hqMapPath + "\\"+ fileName + mapID + ".png";
				File hqMapfile = new File(outpngFileName);
				FileOutputStream imgfile = new FileOutputStream(hqMapfile);
				imgfile.write(bytes);
				imgfile.flush();
				imgfile.close();
				
				System.out.println(outpngFileName);
				out.write(outpngFileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("下载影像图到本地失败："+e.toString());
		}
	}
	
	private void saveAttr(HttpServletRequest request, HttpServletResponse response){

		try {
			request.setCharacterEncoding("utf-8");
			String mapID=request.getParameter("mapID");
			String spatialReference = request.getParameter("spatialRef");
//			String imagemapServiceurl = request.getParameter("imagemapServiceurl");    //lb 6-21 去除服务连接
			ImageMap imagemap=new ImageMap(mapID);
			String fileName = URLDecoder.decode(imagemap.mapName,"utf-8");	
			String mapName = "图名: " + URLDecoder.decode(imagemap.mapName,"utf-8");		
			String author = "制作者: " + mapID.substring(0, mapID.lastIndexOf("_"));
			String time = "成图时间: " + mapID.substring(mapID.lastIndexOf("_")+1, mapID.length());
			String extent = "成图范围: " + imagemap.mapExtent;
			String layoutID = "布局模板: " + String.valueOf(imagemap.mapLayoutModel.getLayoutId());
			String imageMeta = "影像数据: " + imagemap.imgSrc;
			String vectorMeta = "矢量数据: " + imagemap.vtrSrc;
			String symbolMeta = "专题数据: " + imagemap.chartSrc;
			
			//lb 6-21 去除服务连接
//			String mapAttr = mapName + "@_@" + author + "@_@" + time + "@_@" + spatialReference + "@_@" + extent + "@_@" + layoutID + "@_@" + imagemapServiceurl + "@_@"  + imageMeta + "@_@" + vectorMeta + "@_@" + symbolMeta;
			String mapAttr = mapName + "@_@" + author + "@_@" + time + "@_@" + spatialReference + "@_@" + extent + "@_@" + layoutID + "@_@"  + imageMeta + "@_@" + vectorMeta + "@_@" + symbolMeta;
			//bytes = imagemap.drawImageMapH();
			
			//从配置文件读取路径 根据路径及用户名建文件夹
			CreateWebService cws = new CreateWebService();
			Properties ps = cws.ps;
			String userName = mapID.substring(0, mapID.lastIndexOf("_"));
			String hqMapPath = ps.getProperty("USERSPACE")+"//"+userName;
			File prehqfile= new File(hqMapPath);
			if (!prehqfile.exists()) {  
				prehqfile.mkdir();
			}  
			
			String mapAttrFileName =  hqMapPath  + "\\" + fileName + "_" + System.currentTimeMillis() + ".txt";

			FileWriter fw = new FileWriter(new File(mapAttrFileName),true);//追加
//			BufferedWriter = new BufferWriter(fw);
			PrintWriter pw = new PrintWriter(fw);
			String [] attrArray = mapAttr.split("@_@");
			for(int i=0; i < attrArray.length; i++){
				pw.printf("%s"+"%n",attrArray[i]);				
			}			
	        pw.close();

	        
			// path是指欲下载的文件的路径。
			File file = new File(mapAttrFileName);
	        
	        String fileLength = String.valueOf(file.length());	 
	        response.reset();
	        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".txt");
	        response.setHeader("Content-Type", "text/plain");
			response.setHeader("Content-Length", fileLength);
	        
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[fis.available()];
			// 以流的形式下载文件。
			fis.read(buffer);
			fis.close();
			
//			file.delete(); 
			// 清空response
			OutputStream os = response.getOutputStream();
			//response.setContentType("application/octet-stream");
			os.write(buffer);
			os.flush();
			os.close();		

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("保存影像图属性失败："+e.toString());
		}
	}	
	
	public void saveAttrtoSpace(HttpServletRequest request,HttpServletResponse response,PrintWriter out)
	{
		try {
			request.setCharacterEncoding("utf-8");
			String mapID=request.getParameter("mapID");
			String spatialReference =URLDecoder.decode(request.getParameter("spatialRef"),"utf-8");
//			String imagemapServiceurl = request.getParameter("imagemapServiceurl");    //lb 6-21 去除服务连接
			ImageMap imagemap=new ImageMap(mapID);
			String fileName = URLDecoder.decode(imagemap.mapName,"utf-8");	
			String mapName = "图名: " + URLDecoder.decode(imagemap.mapName,"utf-8");		
			String author = "制作者: " + mapID.substring(0, mapID.lastIndexOf("_"));
			
			String timeTemp = mapID.substring(mapID.lastIndexOf("_")+1, mapID.length());
			String year = timeTemp.substring(0,4);
			String month = timeTemp.substring(4, 6);
			String date = timeTemp.substring(6, 8);
			String hour = timeTemp.substring(8, 10);
			String min = timeTemp.substring(10, 12);
			
			String time = "成图时间: " + hour+":"+min+"  "+year+"/"+month+"/"+date;
			
//			String time = "成图时间: " + mapID.substring(mapID.lastIndexOf("_")+1, mapID.length());
			String extent = "成图范围: " + imagemap.mapExtent;
			String layoutID = "布局模板: " + String.valueOf(imagemap.mapLayoutModel.getLayoutName());;
			String imageMeta = "影像数据: " + imagemap.imgSrc;
			String vectorMeta = "矢量数据: " + imagemap.vtrSrc;
			String symbolMeta = "专题数据: " + imagemap.chartSrc;
			
			String mapAttr = mapName + "@_@" + author + "@_@" + time + "@_@" + spatialReference + "@_@" + extent + "@_@" + layoutID + "@_@"  + imageMeta + "@_@" + vectorMeta + "@_@" + symbolMeta;
			
			//从配置文件读取路径 根据路径及用户名建文件夹
			CreateWebService cws = new CreateWebService();
			Properties ps = cws.ps;
			String userName = mapID.substring(0, mapID.lastIndexOf("_"));
			String hqMapPath = ps.getProperty("USERSPACE")+"//"+userName;
			File prehqfile= new File(hqMapPath);
			if (!prehqfile.exists()) {  
				prehqfile.mkdir();
			}  
			
			String mapAttrFileName =  hqMapPath  + "\\" + fileName + "_" + System.currentTimeMillis() + ".txt";

			FileWriter fw = new FileWriter(new File(mapAttrFileName),true);//追加
//			BufferedWriter = new BufferWriter(fw);
			PrintWriter pw = new PrintWriter(fw);
			String [] attrArray = mapAttr.split("@_@");
			for(int i=0; i < attrArray.length; i++){
				pw.printf("%s"+"%n",attrArray[i]);				
			}			
	        pw.close();

	        out.write(mapAttrFileName);
//			// path是指欲下载的文件的路径。
//			File file = new File(mapAttrFileName);
//	        
//	        String fileLength = String.valueOf(file.length());	 
//	        response.reset();
//	        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".txt");
//	        response.setHeader("Content-Type", "text/plain");
//			response.setHeader("Content-Length", fileLength);
//	        
//			FileInputStream fis = new FileInputStream(file);
//			byte[] buffer = new byte[fis.available()];
//			// 以流的形式下载文件。
//			fis.read(buffer);
//			fis.close();
//			
////			file.delete(); 
//			// 清空response
//			OutputStream os = response.getOutputStream();
//			//response.setContentType("application/octet-stream");
//			os.write(buffer);
//			os.flush();
//			os.close();		

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("保存影像图属性失败："+e.toString());
		}
	}

	//替换字符
	public String stringReplaceAll( String source, String find, String replacement){ 
		String[] tmp =  source.split("["+find+"]");
		String reValue = "";
		for(int i=0;i<tmp.length;i++)
		{
			reValue += tmp[i]+"&";
		}
		return reValue;
	}
	
	//ls 6-28
//////////////6-27 LS 绘制背景
	public void drawBackground(HttpServletRequest request,ServletOutputStream sos) throws UnsupportedEncodingException
	{
		request.setCharacterEncoding("utf-8");
		String dc=request.getParameter("DC");
		String wc=request.getParameter("WC");
		
		String[] wc_str = wc.split(",");
		String[] dc_str = dc.split(",");
		double[] wc_arr = new double[4];
		double[] dc_arr = new double[4];

		for(int i=0;i<wc_str.length;i++)
		{
			wc_arr[i] = Double.parseDouble(wc_str[i]);
			dc_arr[i] = Double.parseDouble(dc_str[i]);
		}
		@SuppressWarnings("unused")
		Rectangle2D.Double WC=new Rectangle2D.Double(wc_arr[0],wc_arr[1],wc_arr[2]-wc_arr[0],wc_arr[3]-wc_arr[1]);
		Rectangle2D.Double DC=new Rectangle2D.Double(dc_arr[0],dc_arr[1],dc_arr[2],dc_arr[3]);
		try {	
			byte[] bytes=null;  
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			//BufferedImage image=null;
			BufferedImage bufferedImage=new BufferedImage((int)DC.width, (int)DC.height, BufferedImage.TYPE_BYTE_INDEXED);//构造一种预定义图像类型的BufferedImage
																													//imageType type of the created image			
			Graphics2D graphics2D = (Graphics2D)bufferedImage.createGraphics();//得到一个Graphics2D对象，由于 Graphics2D 是一个抽象类，而且它必须由不同输出设备的子类定制，所以无法直接创建 Graphics2D 对象。Graphics2D 对象必须从另一个 Graphics2D 对象获得、由某个 Component 创建，或者从 BufferedImage 之类的图像对象获得。
			bufferedImage = graphics2D.getDeviceConfiguration().createCompatibleImage((int)DC.width, (int)DC.height, Transparency.TRANSLUCENT);//定义map大小的透明画布
			graphics2D.dispose();//释放Graphics2D对象
			graphics2D = bufferedImage.createGraphics();//重新得到新的Graphics2D对象
			//float Width=Float.parseFloat(width);//设置线宽

		    /*Rendering Hints是	Graphics2D	对象描绘基本类型时使用的各种描绘方法。如使用的抗锯齿hint。这些hints被封装在java.awt.RenderingHints类中。由于使用
			Java2D API会比旧的AWT涉及更多的计算，设计师默认禁止了一部分特性以提高性能。两个最常用的设置是抗锯齿（混合锯齿柔滑锯齿线）和高质量渲染。*/
			RenderingHints renderHints=new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			graphics2D.setRenderingHints(renderHints); 

			Color lineColor=new Color(255,255,224);//设置线的颜色
			graphics2D.setPaint(lineColor);//设置颜色
			GeneralPath pp = new GeneralPath();
		
			long XMin=(long)dc_arr[0];
			long YMin=(long)dc_arr[1];
			long XMax=(long)(dc_arr[0]+dc_arr[2]);
			long YMax=(long)(dc_arr[1]+dc_arr[3]);

			pp.moveTo(XMin, YMin);
			pp.lineTo(XMin, YMax);
			pp.lineTo(XMax, YMax);
			pp.lineTo(XMax, YMin);
					
			Area fillArea = new Area(pp);
			graphics2D.fill(fillArea);
			
			ImageIO.write(bufferedImage, "png", outputStream);//将bufferedImage中的图像按照png格式写成输出流	
			bytes=outputStream.toByteArray();//将输出流转换成字节流
			//System.out.println(bytes.toString());
			sos.write(bytes);//将字节流输出
		}
		catch(Exception e)
		{
			System.out.println("绘制背景失败："+e.toString());
		}
		
	}
	
	////////生成GeoTiff/////////////////////
	public void saveGeoTiff(HttpServletRequest request,PrintWriter out)
	{
		try{
		request.setCharacterEncoding("utf-8");
		byte[] bytes=null;
		String mapID=URLDecoder.decode(request.getParameter("mapID"), "utf-8");
		String geoTiffType = URLDecoder.decode(request.getParameter("geoTiffType"), "utf-8");
		String tiffPath = "";
		//从配置文件读取路径 根据路径及用户名建文件夹
		CreateWebService cws = new CreateWebService();
		Properties ps = cws.ps;
		String userName = mapID.substring(0, mapID.lastIndexOf("_"));
		String hqMapPath = ps.getProperty("USERSPACE")+"//"+userName;
		File prehqfile= new File(hqMapPath);
		if (!prehqfile.exists()) {  
			prehqfile.mkdir();
		}  
		//创建影像地图类对象
		ImageMap imagemap=new ImageMap(mapID);
		String filename = userName+"_"+imagemap.mapName+"_"+System.currentTimeMillis();
		
		if(geoTiffType.equals("L_Res"))
		{
			tiffPath = hqMapPath + "//"  + filename + ".tiff";
			//获取影像地图
			bytes = imagemap.drawImageMap(false);
		}
		if(geoTiffType.equals("H_Res"))
		{
			tiffPath = hqMapPath + "//"  + filename +"_H" + ".tiff";
			//获取影像地图
			bytes = imagemap.drawImageMap(true);
		}
		ByteArrayInputStream inImage = new ByteArrayInputStream(bytes);
		BufferedImage image = ImageIO.read(inImage);
		SaveTiff st = new SaveTiff();
		st.saveTiff(tiffPath,image);
		
		out.write(tiffPath);
		/////将tiff转成geoTiff所需的参数
//		String xmin = URLDecoder.decode(request.getParameter("xmin"),"utf-8");
//		String xmax = URLDecoder.decode(request.getParameter("xmax"),"utf-8");
//		String ymin = URLDecoder.decode(request.getParameter("ymin"),"utf-8");
//		String ymax = URLDecoder.decode(request.getParameter("ymax"),"utf-8");
//		String wkt = URLDecoder.decode(request.getParameter("wkt"), "utf-8");
//		String wkid = URLDecoder.decode(request.getParameter("wkid"), "utf-8");
//		String geoServerUrl = URLDecoder.decode(request.getParameter("geoServerUrl"), "utf-8");
//		String serverType = URLDecoder.decode(request.getParameter("geoServerType"), "utf-8");
//		String geoTiffPath = hqMapPath + "//"  + filename + "_geotiff" +".tif";
//		
//		HashMap<String, String>param = new HashMap<String, String>();
//		param.put("xmin", xmin);
//		param.put("xmax", xmax);
//		param.put("ymin", ymin);
//		param.put("ymax", ymax);
//		param.put("wkt", wkt);
//		param.put("wkid", wkid);
//		param.put("serverType", serverType);
//		param.put("geoTiffPath", geoTiffPath);
//		param.put("tiffPath", tiffPath);
//		
//		sendGeoTiffRequest(geoServerUrl,param);
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.println("生成geotiff失败"+e.getMessage());
		}
	}
	
	
	///后台请求servlet  lb9-5
	@SuppressWarnings("unused")
	private void sendGeoTiffRequest(String url,HashMap<String, String>HashMapParam)
	{
		try {
		StringBuffer param = new StringBuffer();
		HttpURLConnection urlConnection = null;
		int i = 0;  
        for (String key : HashMapParam.keySet()) {  
            if (i == 0)  
                param.append("?");  
            else  
                param.append("&");  
            param.append(key).append("=").append(HashMapParam.get(key));  
            i++;  
        }  
        url+=param;
        URL serverURL = new URL(url);
        urlConnection = (HttpURLConnection)serverURL.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.setUseCaches(false);
        
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("请求geoTiff服务失败"+e.getMessage());
		}
	}
	
	
	///后台servlet响应
//	private  makeContent(String urlString,HttpURLConnection urlConnection) throws IOException {  
//			        HttpRespons httpResponser = new HttpRespons();  
//			        try {  
//			            InputStream in = urlConnection.getInputStream();  
//			            BufferedReader bufferedReader = new BufferedReader(  
//			                    new InputStreamReader(in));  
//			            httpResponser.contentCollection = new Vector<String>();  
//			            StringBuffer temp = new StringBuffer();  
//			            String line = bufferedReader.readLine();  
//			            while (line != null) {  
//			                httpResponser.contentCollection.add(line);  
//			                temp.append(line).append("\r\n");  
//			                line = bufferedReader.readLine();  
//			            }  
//			            bufferedReader.close();  
//			    
//			            String ecod = urlConnection.getContentEncoding();  
//			            if (ecod == null)  
//			                ecod = this.defaultContentEncoding;  
//			    
//			            httpResponser.urlString = urlString;  
//			    
//			            httpResponser.defaultPort = urlConnection.getURL().getDefaultPort();  
//			            httpResponser.file = urlConnection.getURL().getFile();  
//			            httpResponser.host = urlConnection.getURL().getHost();  
//			            httpResponser.path = urlConnection.getURL().getPath();  
//			            httpResponser.port = urlConnection.getURL().getPort();  
//			            httpResponser.protocol = urlConnection.getURL().getProtocol();  
//			            httpResponser.query = urlConnection.getURL().getQuery();  
//			            httpResponser.ref = urlConnection.getURL().getRef();  
//			            httpResponser.userInfo = urlConnection.getURL().getUserInfo();  
//			    
//			            httpResponser.content = new String(temp.toString().getBytes(), ecod);  
//			            httpResponser.contentEncoding = ecod;  
//			            httpResponser.code = urlConnection.getResponseCode();  
//			            httpResponser.message = urlConnection.getResponseMessage();  
//			            httpResponser.contentType = urlConnection.getContentType();  
//			            httpResponser.method = urlConnection.getRequestMethod();  
//			            httpResponser.connectTimeout = urlConnection.getConnectTimeout();  
//			            httpResponser.readTimeout = urlConnection.getReadTimeout();  
//			    
//			            return httpResponser;  
//			        } catch (IOException e) {  
//			            throw e;  
//			        } finally {  
//			            if (urlConnection != null)  
//			                urlConnection.disconnect();  
//			        }  
//			    }  

	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		doGet(request, response);
	}
}
