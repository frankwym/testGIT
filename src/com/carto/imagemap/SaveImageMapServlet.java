package com.carto.imagemap;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.imageio.stream.FileImageOutputStream;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.carto.model.ImageMap;

@SuppressWarnings("serial")
public class SaveImageMapServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public SaveImageMapServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("image/png");
		response.setHeader("Content-type", "image/png;charset=utf-8");
//		PrintWriter sos = null;
		ServletOutputStream sos = null;
		try {
//			sos = response.getWriter();
			sos = response.getOutputStream();
			//画影像地图
//			saveMap(request, sos);
			request.setCharacterEncoding("utf-8");
			byte[] bytes=null;
			String mapID=URLDecoder.decode(request.getParameter("mapID"), "utf-8");
			ImageMap imagemap=new ImageMap(mapID);
			bytes = imagemap.drawImageMap(true);
			sos.write(bytes);	
			String path = "D:\\final.png";
			FileImageOutputStream imageOutput = new FileImageOutputStream(new File(path));
			imageOutput.write(bytes, 0, bytes.length);
			imageOutput.close();
			sos.flush();
			sos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("保存影像地图失败："+e.toString());
		}	
	}

	public void saveMap(HttpServletRequest request, ServletOutputStream out){
		try {
			request.setCharacterEncoding("utf-8");
			byte[] bytes=null;
			String mapID=URLDecoder.decode(request.getParameter("mapID"), "utf-8");
			ImageMap imagemap=new ImageMap(mapID);
			bytes = imagemap.drawImageMap(true);
			out.write(bytes);	
			String path = "D:\\final.png";
			FileImageOutputStream imageOutput = new FileImageOutputStream(new File(path));
			imageOutput.write(bytes, 0, bytes.length);
			imageOutput.close();
//			ImageIO.write(bytes, "png", new File("D:\\final.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("保存影像地图到用户空间失败："+e.toString());
		}
	}
	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the POST method");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
