package com.carto.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Properties;
import javax.imageio.ImageIO;
import com.dbconn.ConnOrcl;
import net.sf.json.JSONObject;

public class ImageMap {

	//遥感影像数据信息
	public String mapID;
	public String mapName;
	public String imgSrc;
	public String imgAnnoSrc;
	public String vtrSrc;
	public String chartSrc;
	public String imageMetaData;
	public String lengedSrc;

	//判断是否绘制标注
	public String addMarker = "0";
	public String addDynamicSymbol = "0";

	//遥感影像地图元素
	public MapFrame imageMapFrame;	
	public MapScale mapScale;
	public MapCompass mapCompass;
	public String mapExtent;

	//遥感影像图形布局
	public MapLayoutModel mapLayoutModel;
	private double index = 1;

	//记录整个图面的范围，计算文字的位置文字（居中靠边）
	private int mapExtent_x;
	@SuppressWarnings("unused")
	private int mapExtent_y;
	private int mapExtent_w;
	@SuppressWarnings("unused")
	private int mapExtent_h;
	
	
	//
	private static int mapLW;
	private static int mapHW;
	private double wrate;
	

	public  ImageMap(String mapId){
		//由传入的ID创建影像地图对象
		ConnOrcl connection = ConnOrcl.getInstance();
		mapID=mapId;
		String tableName = "IMAGE_MAP";
		String[] columns = {"IMG_SRC", "VTR_SRC","IMGANNO_SRC","MAPLAY_MOD_ID","MAP_NAME","CHART_SRC","MAP_FRAME_ID","SCALE_STR","MAP_EXTENT","META_DATA","LEGEND_SRC"};
		String whereClause = "MAPID='" + mapID + "'";

		List<JSONObject> imagemapdata = connection.selectTable(tableName, columns, whereClause);
		JSONObject mapdata=imagemapdata.get(0);
		vtrSrc = mapdata.getString("VTR_SRC");
		imgSrc=mapdata.getString("IMG_SRC");
		imgAnnoSrc=mapdata.getString("IMGANNO_SRC");
		mapName=mapdata.getString("MAP_NAME");
		mapExtent = mapdata.getString("MAP_EXTENT");
		chartSrc = mapdata.getString("CHART_SRC");
		imageMetaData =  mapdata.getString("META_DATA");
		lengedSrc = mapdata.getString("LEGEND_SRC");
		String frameID=mapdata.getString("MAP_FRAME_ID");
		String scaleStr=mapdata.getString("SCALE_STR");

		//获取内网IP   服务器问题,内外网映射
		String insideIPServer = getCartoInsideIpServer();
		String outsideIPServer = getCartoOutsideIpServer();
		vtrSrc = vtrSrc.replace(outsideIPServer, insideIPServer);
		chartSrc = chartSrc.replace(outsideIPServer, insideIPServer);

		//		vtrSrc = vtrSrc.replaceFirst("http://([a-wA-W0-9.:]+)/", insideIPServer);
		//		chartSrc = chartSrc.replaceFirst("http://([a-wA-W0-9.:]+)/", insideIPServer);
		/////在线标注数据读取
		///lb-4-1修改
		String markerResult = connection.getMarkerString("IMAGE_MAP", mapId);
		String[] mString = markerResult.split("@");
		if(mString.length > 3)
		{
			addMarker = mString[0];
		}

		///态势标注数据读取
		String dynamicSymbolResult = connection.getDyanmicSymbolSTR("IMAGE_MAP", mapId);
		String[] dString = dynamicSymbolResult.split("@");
		if(dString.length > 3)
		{
			addDynamicSymbol = dString[0];
		}

		//初始化图面配置模版
		String mapLayoutModelID=mapdata.getString("MAPLAY_MOD_ID");
		mapLayoutModel=new MapLayoutModel(mapLayoutModelID);

		//放在需要的地方进行初始化
		//初始化图框		
		if(frameID.length() != 3){
			imageMapFrame=new MapFrame("1000");
		}
		else {
			imageMapFrame=new MapFrame(frameID);
		}	
		mapCompass=new MapCompass("/compass.png");
		mapScale=new MapScale(scaleStr);
	}

	public byte[] drawImageMap(boolean isHighQuality){
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


//		int mapFrameW = (int) mapLayoutModel.getMapFrame().getWidth();
//		int mapFrameH = (int) mapLayoutModel.getMapFrame().getHeight();
		if(!imgSrc.equals("")){
			BufferedImage imageMap2;
			if(isHighQuality){
				imageMap2 = this.getImageH(imgSrc, 1.0,mapLayoutModel.getId());
				index = Math.pow(2, index);
			}
			else {
				imageMap2 = this.getImageL(imgSrc, 1.0);
				index = Math.pow(2, index);
			}

			double scale = (double)mapLayoutModel.getMainMapExt().width / (double)imageMap2.getWidth();
			if((mapLayoutModel.getMainMapExt().height / scale) >= imageMap2.getHeight() ){
				mapLayoutModel.scaleLayoutModel(1 / scale);
			}
			else {
				scale = (double)mapLayoutModel.getMainMapExt().height / (double)imageMap2.getHeight();
				mapLayoutModel.scaleLayoutModel(1 / scale);
			}
			try{
				//1、创建地图
								BufferedImage theImageMap=new BufferedImage((int)mapLayoutModel.getMapExt().getWidth(),(int)mapLayoutModel.getMapExt().getHeight(),BufferedImage.TYPE_INT_RGB);
//				BufferedImage theImageMap=new BufferedImage(mapFrameW,mapFrameH,BufferedImage.TYPE_INT_RGB);
				Graphics2D finalg2d = (Graphics2D)theImageMap.createGraphics();			
				theImageMap=finalg2d.getDeviceConfiguration().createCompatibleImage((int)mapLayoutModel.getMapExt().getWidth(),(int)mapLayoutModel.getMapExt().getHeight(), Transparency.TRANSLUCENT);
				finalg2d.dispose();
				finalg2d=(Graphics2D)theImageMap.createGraphics();

				//2、绘制图框
				if(mapLayoutModel.getMapFrame().getWidth() > 0.0){							
					BufferedImage frameBuffer=this.imageMapFrame.getFrameBufferedImage();
					finalg2d.drawImage(frameBuffer, 0,0, (int)mapLayoutModel.getMapExt().getWidth(),(int)mapLayoutModel.getMapExt().getHeight(), null);
					mapExtent_x=0;mapExtent_y=0;mapExtent_w=(int)mapLayoutModel.getMapExt().getWidth();mapExtent_h=(int)mapLayoutModel.getMapExt().getHeight();
					//					ImageIO.write(frameBuffer, "png", new File("D:\\testImage\\mapFrame.png"));
				}else{
					//绘制黑边框, 边框尺寸的控制  //不同尺度的控制stroke变化的
					double left = mapLayoutModel.getMainMapExt().getX() - mapLayoutModel.getMapExt().getX();
					double top = mapLayoutModel.getMainMapExt().getY() - mapLayoutModel.getMapExt().getY();
					double right = mapLayoutModel.getMapExt().width - mapLayoutModel.getMainMapExt().width - left;
					double bottom = mapLayoutModel.getMapExt().height - mapLayoutModel.getMainMapExt().height - top;
					double min1 = Math.min(left, right);
					double min2 = Math.min(top, bottom);
					double min = Math.min(min1, min2);
					float gap = (float)min;
					float strokeWidth = (float)(min / 5);
					finalg2d.setStroke(new BasicStroke(strokeWidth));
					finalg2d.setPaint(Color.black);
					finalg2d.drawRect((int)(mapLayoutModel.getMainMapExt().getX() - gap), (int)(mapLayoutModel.getMainMapExt().getY() - gap), 
							(int)(mapLayoutModel.getMainMapExt().getWidth() + gap*2), (int)(mapLayoutModel.getMainMapExt().getHeight() + gap*2));

					//
					mapExtent_x=(int)(mapLayoutModel.getMainMapExt().getX() - gap);mapExtent_y=(int)(mapLayoutModel.getMainMapExt().getY() - gap);mapExtent_w=(int)(mapLayoutModel.getMainMapExt().getWidth() + gap*2);mapExtent_h=(int)(mapLayoutModel.getMainMapExt().getHeight() + gap*2);
				}

				Graphics2D imageg2d = (Graphics2D)imageMap2.createGraphics();
				//3、绘制矢量图片
				String [] vectstr = this.vtrSrc.split("@");
				for(int i=0; i < vectstr.length; i++){
					if(!vectstr[i].equals("")){
						try {
							String vectorUrl = vectstr[i] + "&DC=0,0," + imageMap2.getWidth() + "," + imageMap2.getHeight();
							//尺寸缩放
							String URL1 = vectorUrl.split("width=")[0];
							int widthLoc = vectorUrl.split("width=")[1].indexOf("&");
							String URL2 = vectorUrl.split("width=")[1].substring(widthLoc);
							String originWidth =vectorUrl.split("width=")[1].substring(0, widthLoc);
							String newWidth = originWidth+"_"+index;
							//中文名转换为utf-8编码， 中文用户名编码转换
							String txtNm = URL1.split("txtName=")[1].substring(0, URL1.split("txtName=")[1].indexOf("&"));
							txtNm = URLEncoder.encode(txtNm, "utf-8");
							URL1 = URL1.split("txtName=")[0] + "txtName=" + txtNm + URL1.split("txtName=")[1].substring(URL1.split("txtName=")[1].indexOf("&"));

							String userName = URL2.split("userName=")[1].substring(0, URL2.split("userName=")[1].indexOf("&"));
							userName = URLEncoder.encode(userName, "utf-8");
							URL2 = URL2.split("userName=")[0] + "userName=" + userName + URL2.split("userName=")[1].substring(URL2.split("userName=")[1].indexOf("&"));

							vectorUrl = URL1 + "width=" + newWidth + URL2;
							URL url=new URL(vectorUrl); 
							//									System.out.println(vectorUrl);
							URLConnection con=url.openConnection();
							InputStream iStream=con.getInputStream();
							BufferedImage vecBi = ImageIO.read(iStream);
							imageg2d.drawImage(vecBi, 0, 0, imageMap2.getWidth(), imageMap2.getHeight(), null);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("绘制矢量要素失败："+e.toString());
						}
					}
				}
				//3.2绘制专题符号
				String [] symbolStr = this.chartSrc.split("@");
				for(int j=0; j < symbolStr.length; j++){	
					if(!symbolStr[j].equals("")){
						try {
							wrate = mapHW/mapLW;
							String symUrl = "";
							if(isHighQuality){
								String urlString  = symbolStr[j] + "&DC=0,0," + imageMap2.getWidth() + "," + imageMap2.getHeight() + "&RATE="+wrate;
//								symUrl = URLEncoder.encode(urlString,"utf-8");
								symUrl = urlString;
							}else{
								String urlString  =symbolStr[j] + "&DC=0,0," + imageMap2.getWidth() + "," + imageMap2.getHeight() + "&RATE="+1;
//								symUrl =  URLEncoder.encode(urlString,"utf-8");
								symUrl = urlString;
								System.out.println(symUrl);
							}


//							System.out.println(symUrl);
							URL url=new URL(symUrl); 

							URLConnection con=url.openConnection();
							InputStream iStream=con.getInputStream();
							BufferedImage chartBi = ImageIO.read(iStream);
							imageg2d.drawImage(chartBi, 0, 0, imageMap2.getWidth(), imageMap2.getHeight(), null);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("绘制专题符号失败："+e.toString());
						}
					}
				}
//				ImageIO.write(imageMap2, "png", new File("D:\\testImage\\testChartLyr.png"));
				//3.3绘制标注
				if(addMarker.equals("1"))
				{
					DrawMarkerSymbol drawMarkerSymbol = new DrawMarkerSymbol();
					String DC = "0,0," + imageMap2.getWidth() + ","+ imageMap2.getHeight();
					drawMarkerSymbol.iniparam(this.mapID, DC, index);
					BufferedImage markerBufferedImage = drawMarkerSymbol.drawMarker();
					/////////////////////////////////////////////////////
					imageg2d.drawImage(markerBufferedImage,0, 0, imageMap2.getWidth(), imageMap2.getHeight(), null);
				}

				if(addDynamicSymbol.equals("1"))
				{
					DrawDynamicSymbol drawDynamicSymbol = new DrawDynamicSymbol();
					String DC = "0,0," + imageMap2.getWidth() + ","+ imageMap2.getHeight();
					drawDynamicSymbol.initParam(this.mapID, DC, index);
					BufferedImage dynamicSymbolBufferedImage = drawDynamicSymbol.drawSymbol();
					/////////////////////////////////////////////////////
					imageg2d.drawImage(dynamicSymbolBufferedImage,0, 0, imageMap2.getWidth(), imageMap2.getHeight(), null);
				}

				///绘制 影像注记6
				if(!imgAnnoSrc.equals(""))
				{
					BufferedImage annoImage;
					if(isHighQuality){
						annoImage = this.getImageH(imgAnnoSrc, 1.0,mapLayoutModel.getId());
						//						index = Math.pow(2, index);
					}
					else {
						annoImage = this.getImageL(imgAnnoSrc, 1.0);
						//						index = Math.pow(2, index);
					}
					imageg2d.drawImage(annoImage,0, 0, imageMap2.getWidth(), imageMap2.getHeight(), null);
				}

				//填充影像、矢量、专题、标注到主图区
				Rectangle mainMapRect = this.modifyToMiddle(new Rectangle(0,0, imageMap2.getWidth(), imageMap2.getHeight()), 
						new Rectangle((int)mapLayoutModel.getMainMapExt().getX(), (int)mapLayoutModel.getMainMapExt().getY(), 
								(int)mapLayoutModel.getMainMapExt().getWidth(), (int)mapLayoutModel.getMainMapExt().getHeight()));
//				Rectangle mainMapRect = this.modifyToMiddle(new Rectangle(0,0, imageMap2.getWidth(), imageMap2.getHeight()), 
//						new Rectangle((int)mapLayoutModel.getMainMapExt().getX(), (int)mapLayoutModel.getMainMapExt().getY(), 
//								(int)mapLayoutModel.getMapFrame().getWidth(), (int)mapLayoutModel.getMapFrame().getHeight()));
				finalg2d.drawImage(imageMap2, mainMapRect.x, mainMapRect.y, mainMapRect.width, mainMapRect.height, null);
//				finalg2d.drawImage(imageMap2, 0,0, 4961 ,3508, null);
//				ImageIO.write(imageMap2, "png", new File("D:\\testImage\\testA3.png"));
				//绘制专题符号图例
				if(mapLayoutModel.getLegendExt().getWidth() > 0.0){					
					//					for(int k=0; k < symbolStr.length; k++){	
					if(!lengedSrc.equals("")){
						try {
							//								String symUrl = symbolStr[k] + "&DC=0,0," + imageMap2.getWidth() + "," + imageMap2.getHeight();
							String chartUrl = lengedSrc;
							URL url=new URL(chartUrl); 

							//								System.out.println(chartUrl);
							URLConnection con=url.openConnection();
							InputStream iStream=con.getInputStream();
							BufferedImage legendBi = ImageIO.read(iStream);
//							ImageIO.write(legendBi, "png", new File("D:\\testImage\\testLegendLyr.png"));
							Rectangle legendRect = getDrawRect(new Rectangle(0,0,legendBi.getWidth(),legendBi.getHeight()), 
									new Rectangle((int)mapLayoutModel.getLegendExt().getX(), (int)mapLayoutModel.getLegendExt().getY(), 
											(int)mapLayoutModel.getLegendExt().getWidth(), (int)mapLayoutModel.getLegendExt().getHeight()));
							RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING , RenderingHints.VALUE_ANTIALIAS_ON); 
							qualityHints.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
							finalg2d.setStroke(new BasicStroke(1.0f));
							finalg2d.setRenderingHints(qualityHints);							
							finalg2d.drawImage(legendBi, legendRect.x, legendRect.y, legendRect.width, legendRect.height, null);
							finalg2d.drawRect(legendRect.x, legendRect.y, legendRect.width, legendRect.height);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("绘制专题符号图例失败："+e.toString());
						}
					}
					//					}
				}
				//7、绘制指北针
				if(mapLayoutModel.getCompassExt().getWidth() > 0.0){
					BufferedImage compassBi=mapCompass.getCompassBuffer();
					Rectangle compassRect = getDrawRect(new Rectangle(0, 0, compassBi.getWidth(), compassBi.getHeight()), 
							new Rectangle((int)mapLayoutModel.getCompassExt().getX(), (int)mapLayoutModel.getCompassExt().getY(), 
									(int)mapLayoutModel.getCompassExt().width, (int)mapLayoutModel.getCompassExt().height));
					//finalg2d.fillRect(compassRect.x, compassRect.y, compassRect.width, compassRect.height);
					finalg2d.drawImage(compassBi, compassRect.x, compassRect.y, compassRect.width, compassRect.height,null);
					//finalg2d.drawRect(compassRect.x, compassRect.y, compassRect.width, compassRect.height);
				}

				//8、绘制比例尺
				if(mapLayoutModel.getScaleExt().getWidth() > 0.0){
					finalg2d.setPaint(Color.white);			
					BufferedImage scaleBi = mapScale.DrawImageScale2();
					Rectangle scaleRect = getDrawRect(new Rectangle(0,0,scaleBi.getWidth(),scaleBi.getHeight()), 
							new Rectangle((int)mapLayoutModel.getScaleExt().getX(), (int)mapLayoutModel.getScaleExt().getY(), 
									(int)mapLayoutModel.getScaleExt().getWidth(), (int)mapLayoutModel.getScaleExt().getHeight()));
					finalg2d.fillRect(scaleRect.x, scaleRect.y, scaleRect.width, scaleRect.height);
					finalg2d.drawImage(scaleBi, scaleRect.x, scaleRect.y, scaleRect.width, scaleRect.height,null);
					finalg2d.drawRect(scaleRect.x, scaleRect.y, scaleRect.width, scaleRect.height);				
				}

				//9、绘制图名:根据宽高判断类型，再绘制
				if(mapLayoutModel.getTitleExt().getWidth() > 0.0){

					if((imageMetaData.isEmpty()||imageMetaData.equals(null)))                //如果元数据为空，则非通用模板，图名无需居中计算
					{
						if(mapLayoutModel.getTitleExt().width > mapLayoutModel.getTitleExt().height){
							//绘制
							finalg2d.setFont(new Font("宋体", Font.BOLD, (int)(30/scale)));	
							finalg2d.setColor(Color.red);
							finalg2d.drawString(this.mapName, (int)mapLayoutModel.getTitleExt().getX(), (int)mapLayoutModel.getTitleExt().getY() + (int)(30 / scale));
						}
						else { 
							finalg2d.setColor(Color.red);
							finalg2d.setFont(new Font("宋体", Font.BOLD, (int)(30/scale)));	
							for(int i=0; i < mapName.length(); i++){
								finalg2d.drawString(mapName.substring(i, i + 1), (int)mapLayoutModel.getTitleExt().getX(), (int)mapLayoutModel.getTitleExt().getY() + (int)(30 / scale)*(i+1));
							}
						} 
					}
					else    
					{
						//模板为通用，设置图名居中
						//设定文字样式
						Font nameTextFont = new Font("黑体", Font.PLAIN, (int)(30 / scale));
						finalg2d.setFont(nameTextFont);
						//获取字符长宽度
						FontMetrics metrics = finalg2d.getFontMetrics();
						Rectangle2D textRectangle = metrics.getStringBounds(mapName, finalg2d);
						@SuppressWarnings("unused")
						double nameTextHeight = textRectangle.getHeight();
						double nameTextWidth = textRectangle.getWidth();

						int mapName_x = (int)((mapExtent_x+mapExtent_w/2)-nameTextWidth/2);
						int mapName_y = (int)mapLayoutModel.getTitleExt().getY();
						finalg2d.setColor(Color.black);
						finalg2d.drawString(this.mapName, mapName_x, mapName_y + (int)(30 / scale));
					}
				}

				//10、绘制元数据信息 ，单位
				if(!(imageMetaData.isEmpty()||imageMetaData.equals(null)))
				{
					//设置文字右靠边
					//设定文字样式
					String metas[]=imageMetaData.split("&&");
					String time = metas[0];
					String reslution = metas[1];
					String mapArea = metas[2];
					String mapOrg = metas[3];

					//区分高清低清分辨率
					String qualityRes[] = reslution.split("_");

					//时间
					Font tTextFont = new Font("宋体", Font.PLAIN, (int)(13/scale));
					finalg2d.setFont(tTextFont);
					FontMetrics tMetrics = finalg2d.getFontMetrics();
					finalg2d.setColor(Color.black);
					Rectangle2D tTextRectangle = tMetrics.getStringBounds(time, finalg2d);
					double tTextHeight = tTextRectangle.getHeight();
					@SuppressWarnings("unused")
					double tTextWidth = tTextRectangle.getWidth();
					int tmeta_x = (int)mapExtent_x;
					int tmeta_y = (int)mapLayoutModel.getMetaExt().getY();
					finalg2d.drawString(time, tmeta_x, tmeta_y+(int)(18/scale));

					//分辨率
					if(isHighQuality)
					{
						reslution = qualityRes[1];
					}
					else
					{
						reslution = qualityRes[0];
					}

					if(reslution.contains("."))
					{
						int pointIndex = reslution.indexOf(".");
						reslution = reslution.substring(0, pointIndex+2)+"米";     //保留小数点后一位
					}

					Font rTextFont = new Font("宋体", Font.PLAIN, (int)(13/scale));
					finalg2d.setFont(rTextFont);
					FontMetrics rMetrics = finalg2d.getFontMetrics();
					finalg2d.setColor(Color.black);
					@SuppressWarnings("unused")
					Rectangle2D rTextRectangle = rMetrics.getStringBounds(reslution, finalg2d);
					@SuppressWarnings("unused")
					double rTextHeight = tTextRectangle.getHeight();
					@SuppressWarnings("unused")
					double rTextWidth = tTextRectangle.getWidth();
					int rmeta_x = (int)mapExtent_x;
					int rmeta_y = (int)(mapLayoutModel.getMetaExt().getY()+tTextHeight) ;
					finalg2d.drawString(reslution, rmeta_x,rmeta_y+(int)(20/scale));

					//单位信息
					Font metaTextFont = new Font("宋体", Font.PLAIN, (int)(18 / scale));
					finalg2d.setFont(metaTextFont);
					//获取字符长宽度
					FontMetrics metaMetrics = finalg2d.getFontMetrics();
					finalg2d.setColor(Color.black);
					Rectangle2D metaTextRectangle = metaMetrics.getStringBounds(mapOrg, finalg2d);
					@SuppressWarnings("unused")
					double metaTextHeight = metaTextRectangle.getHeight();
					double metaTextWidth = metaTextRectangle.getWidth();
					int meta_x = (int)(mapExtent_x+mapExtent_w-metaTextWidth);
					int meta_y = (int)mapLayoutModel.getMetaExt().getY() ;
					finalg2d.drawString(mapOrg, meta_x, meta_y+(int)(25/scale));

					//制图区域
					Rectangle2D aTextRectangle = metaMetrics.getStringBounds(mapArea, finalg2d);
					@SuppressWarnings("unused")
					double aTextHeight = aTextRectangle.getHeight();
					double aTextWidth = aTextRectangle.getWidth();
					int ameta_x = (int)((mapExtent_x+mapExtent_w/2)-aTextWidth/2);
					int ameta_y = (int)mapLayoutModel.getMetaExt().getY();
					finalg2d.drawString(mapArea, ameta_x, ameta_y+(int)(25/scale));
				}

				finalg2d.dispose();		
				ImageIO.write(theImageMap, "png", outputStream);
//				ImageIO.write(theImageMap, "png", new File("D://testImage//final.png"));
			}	
			catch(IOException e) {
				System.out.println("绘制影像地图失败："+e.toString());
			}
			return outputStream.toByteArray();	
		}


		else {
			try{
				if(isHighQuality){
					index = 4.0;
				}
				else {
					index = 1.0;
				}
				mapLayoutModel.scaleLayoutModel(index);
				//1、创建地图
				BufferedImage theImageMap=new BufferedImage((int)mapLayoutModel.getMapExt().getWidth(),(int)mapLayoutModel.getMapExt().getHeight(),BufferedImage.TYPE_INT_RGB);	
				Graphics2D finalg2d = (Graphics2D)theImageMap.createGraphics();			
				theImageMap=finalg2d.getDeviceConfiguration().createCompatibleImage((int)mapLayoutModel.getMapExt().getWidth(),(int)mapLayoutModel.getMapExt().getHeight(), Transparency.TRANSLUCENT);
				finalg2d.dispose();
				finalg2d=(Graphics2D)theImageMap.createGraphics();
				finalg2d.setPaint(Color.black);

				//				2、绘制图框
				if(mapLayoutModel.getMapFrame().getWidth() > 0.0){
					BufferedImage frameBuffer=this.imageMapFrame.getFrameBufferedImage();
					finalg2d.drawImage(frameBuffer, 0,0, (int)mapLayoutModel.getMapExt().getWidth(),(int)mapLayoutModel.getMapExt().getHeight() , null);
					mapExtent_x=0;mapExtent_y=0;mapExtent_w=(int)mapLayoutModel.getMapExt().getWidth();mapExtent_h=(int)mapLayoutModel.getMapExt().getHeight();
				}else {
					//绘制黑边框, 边框尺寸的控制  //不同尺度的控制stroke变化的
					double left = mapLayoutModel.getMainMapExt().getX() - mapLayoutModel.getMapExt().getX();
					double top = mapLayoutModel.getMainMapExt().getY() - mapLayoutModel.getMapExt().getY();
					double right = mapLayoutModel.getMapExt().width - mapLayoutModel.getMainMapExt().width - left;
					double bottom = mapLayoutModel.getMapExt().height - mapLayoutModel.getMainMapExt().height - top;
					double min1 = Math.min(left, right);
					double min2 = Math.min(top, bottom);
					double min = Math.min(min1, min2);
					float gap = (float)min;
					float strokeWidth = (float)(min / 5);
					finalg2d.setStroke(new BasicStroke(strokeWidth));
					finalg2d.setPaint(Color.black);
					finalg2d.drawRect((int)(mapLayoutModel.getMainMapExt().getX() - gap), (int)(mapLayoutModel.getMainMapExt().getY() - gap), 
							(int)(mapLayoutModel.getMainMapExt().getWidth() + gap*2), (int)(mapLayoutModel.getMainMapExt().getHeight() + gap*2));

					mapExtent_x=(int)(mapLayoutModel.getMainMapExt().getX() - gap);mapExtent_y=(int)(mapLayoutModel.getMainMapExt().getY() - gap);mapExtent_w=(int)(mapLayoutModel.getMainMapExt().getWidth() + gap*2);mapExtent_h=(int)(mapLayoutModel.getMainMapExt().getHeight() + gap*2);
				}

				////////绘制背景
				Color lineColor=new Color(255,255,224);//设置线的颜色
				finalg2d.setPaint(lineColor);
				long XMin=(long)mapLayoutModel.getMainMapExt().getX();
				long YMin=(long)mapLayoutModel.getMainMapExt().getY();
				long XMax=(long)(mapLayoutModel.getMainMapExt().getX()+mapLayoutModel.getMainMapExt().getWidth());
				long YMax=(long)(mapLayoutModel.getMainMapExt().getY()+mapLayoutModel.getMainMapExt().getHeight());
				GeneralPath pp = new GeneralPath();
				pp.moveTo(XMin, YMin);
				pp.lineTo(XMin, YMax);
				pp.lineTo(XMax, YMax);
				pp.lineTo(XMax, YMin);
				Area fillArea = new Area(pp);
				finalg2d.fill(fillArea);
				/////////////


				//3、绘制矢量要素
				String [] vectstr = this.vtrSrc.split("@");
				for(int i=0; i < vectstr.length; i++){
					if(!vectstr[i].equals("")){
						try {
							String vecUrl = vectstr[i] + "&DC=0,0," + (int)mapLayoutModel.getMainMapExt().width + "," + (int)mapLayoutModel.getMainMapExt().height;
							//尺寸缩放
							String URL1 = vecUrl.split("width=")[0];
							int widthLoc = vecUrl.split("width=")[1].indexOf("&");
							String URL2 = vecUrl.split("width=")[1].substring(widthLoc);
							String originWidth =vecUrl.split("width=")[1].substring(0, widthLoc);
							String newWidth = originWidth+"_"+index;

							//中文名转换为utf-8编码， 中文用户名编码转换
							String txtNm = URL1.split("txtName=")[1].substring(0, URL1.split("txtName=")[1].indexOf("&"));
							txtNm = URLEncoder.encode(txtNm, "utf-8");
							URL1 = URL1.split("txtName=")[0] + "txtName=" + txtNm + URL1.split("txtName=")[1].substring(URL1.split("txtName=")[1].indexOf("&"));

							String userName = URL2.split("userName=")[1].substring(0, URL2.split("userName=")[1].indexOf("&"));
							userName = URLEncoder.encode(userName, "utf-8");
							URL2 = URL2.split("userName=")[0] + "userName=" + userName + URL2.split("userName=")[1].substring(URL2.split("userName=")[1].indexOf("&"));

							vecUrl = URL1 + "width=" + newWidth + URL2;

							URL url=new URL(vecUrl); 
							//							System.out.println(vecUrl);
							URLConnection con=url.openConnection();
							InputStream iStream=con.getInputStream();
							BufferedImage vecBi = ImageIO.read(iStream);
							finalg2d.drawImage(vecBi, (int)mapLayoutModel.getMainMapExt().getX(), (int)mapLayoutModel.getMainMapExt().getY(), (int)mapLayoutModel.getMainMapExt().width, (int)mapLayoutModel.getMainMapExt().height, null);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("绘制矢量要素失败："+e.toString());
						}
					}
				}
				System.out.println("绘制完矢量符号");
				//ImageIO.write(imageMap2, "png", new File("C:\\whu\\imageaavector.png"));
				//4、绘制专题符号
				String [] symbolStr = this.chartSrc.split("@");
				for(int j=0; j < symbolStr.length; j++){	
					if(!symbolStr[j].equals("")){
						try {
							String symUrl = symbolStr[j] + "&DC=0,0," + (int)mapLayoutModel.getMainMapExt().width + "," + (int)mapLayoutModel.getMainMapExt().height;
							URL url=new URL(symUrl); 

							System.out.println(symUrl);
							URLConnection con=url.openConnection();
							InputStream iStream=con.getInputStream();
							BufferedImage chartBi = ImageIO.read(iStream);
							finalg2d.drawImage(chartBi, (int)mapLayoutModel.getMainMapExt().getX(), (int)mapLayoutModel.getMainMapExt().getY(), (int)mapLayoutModel.getMainMapExt().width, (int)mapLayoutModel.getMainMapExt().height, null);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("绘制专题符号失败："+e.toString());
						}
					}
				}
				System.out.println("绘制完专题符号");
				//ImageIO.write(imageMap2, "png", new File("C:\\whu\\imageaavectoraasymbol.png"));
				//5、绘制标注
				if(addMarker.equals("1"))
				{
					DrawMarkerSymbol drawMarkerSymbol = new DrawMarkerSymbol();
					String DC = "0,0," + mapLayoutModel.getMainMapExt().width+ ","+ mapLayoutModel.getMainMapExt().height;
					drawMarkerSymbol.iniparam(this.mapID, DC, index);
					BufferedImage markerBufferedImage = drawMarkerSymbol.drawMarker();
					/////////////////////////////////////////////////////
					finalg2d.drawImage(markerBufferedImage, (int)mapLayoutModel.getMainMapExt().getX(), (int)mapLayoutModel.getMainMapExt().getY(), (int)mapLayoutModel.getMainMapExt().width, (int)mapLayoutModel.getMainMapExt().height, null);
				}

				if(addDynamicSymbol.equals("1"))
				{
					DrawDynamicSymbol drawDynamicSymbol = new DrawDynamicSymbol();
					String DC = "0,0," + mapLayoutModel.getMainMapExt().width+ ","+ mapLayoutModel.getMainMapExt().height;
					drawDynamicSymbol.initParam(this.mapID, DC, index);
					BufferedImage dynamicSymbolBufferedImage = drawDynamicSymbol.drawSymbol();
					/////////////////////////////////////////////////////
					finalg2d.drawImage(dynamicSymbolBufferedImage, (int)mapLayoutModel.getMainMapExt().getX(), (int)mapLayoutModel.getMainMapExt().getY(), (int)mapLayoutModel.getMainMapExt().width, (int)mapLayoutModel.getMainMapExt().height, null);
				}

				//finalg2d.drawRect((int)mapLayoutModel.getMainMapExt().getX(), (int)mapLayoutModel.getMainMapExt().getY(), (int)mapLayoutModel.getMainMapExt().width, (int)mapLayoutModel.getMainMapExt().height);

				///绘制影像注记
				///绘制 影像注记
				if(!imgAnnoSrc.equals(""))
				{
					BufferedImage annoImage;
					if(isHighQuality){
						annoImage = this.getImageH(imgAnnoSrc, 1.0,mapLayoutModel.getId());
					}
					else {
						annoImage = this.getImageL(imgAnnoSrc, 1.0);
					}
					finalg2d.drawImage(annoImage,(int)mapLayoutModel.getMainMapExt().getX(), (int)mapLayoutModel.getMainMapExt().getY(), (int)mapLayoutModel.getMainMapExt().width, (int)mapLayoutModel.getMainMapExt().height, null);
				}

				//6、绘制专题符号图例
				for(int k=0; k < symbolStr.length; k++){	
					if(!symbolStr[k].equals("")){
						try {
							String symUrl = symbolStr[k] + "&DC=0,0," + (int)mapLayoutModel.getMainMapExt().width + "," + (int)mapLayoutModel.getMainMapExt().height;
							String chartUrl = symUrl.split("\\?")[0].replace("WCSServlet", "WLSServlet") + "?LegendID=ChartLegend&" + symUrl.split("\\?")[1];
							URL url=new URL(chartUrl); 

							System.out.println(chartUrl);
							URLConnection con=url.openConnection();
							InputStream iStream=con.getInputStream();
							BufferedImage legendBi = ImageIO.read(iStream);
							//ImageIO.write(legendBi, "png", new File("C:\\whu\\legend.png"));
							Rectangle legendRect = getDrawRect(new Rectangle(0,0,legendBi.getWidth(),legendBi.getHeight()), 
									new Rectangle((int)mapLayoutModel.getLegendExt().getX(), (int)mapLayoutModel.getLegendExt().getY(), 
											(int)mapLayoutModel.getLegendExt().getWidth(), (int)mapLayoutModel.getLegendExt().getHeight()));
							RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING , RenderingHints.VALUE_ANTIALIAS_ON); 
							qualityHints.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
							finalg2d.setRenderingHints(qualityHints);

							//finalg2d.setColor(Color.green);
							finalg2d.drawImage(legendBi, legendRect.x, legendRect.y, legendRect.width, legendRect.height, null);
							finalg2d.drawRect(legendRect.x, legendRect.y, legendRect.width , legendRect.height);
							//finalg2d.drawRect((int)mapLayoutModel.getLegendExt().getX(), (int)mapLayoutModel.getLegendExt().getY(), (int)mapLayoutModel.getLegendExt().getWidth(), (int)mapLayoutModel.getLegendExt().getHeight());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("绘制专题符号图例失败："+e.toString());
						}
					}
				}
				//7、绘制指北针
				BufferedImage compassBi=mapCompass.getCompassBuffer();
				Rectangle compassRect = getDrawRect(new Rectangle(0, 0, compassBi.getWidth(), compassBi.getHeight()), 
						new Rectangle((int)mapLayoutModel.getCompassExt().getX(), (int)mapLayoutModel.getCompassExt().getY(), 
								(int)mapLayoutModel.getCompassExt().width, (int)mapLayoutModel.getCompassExt().height));
				//finalg2d.fillRect(compassRect.x, compassRect.y, compassRect.width, compassRect.height);
				finalg2d.drawImage(compassBi, compassRect.x, compassRect.y, compassRect.width, compassRect.height,null);
				//finalg2d.drawRect(compassRect.x, compassRect.y, compassRect.width, compassRect.height);

				//8、绘制比例尺
				finalg2d.setPaint(Color.white);			
				BufferedImage scaleBi = mapScale.DrawImageScale2();
				Rectangle scaleRect = getDrawRect(new Rectangle(0,0,scaleBi.getWidth(),scaleBi.getHeight()), 
						new Rectangle((int)mapLayoutModel.getScaleExt().getX(), (int)mapLayoutModel.getScaleExt().getY(), 
								(int)mapLayoutModel.getScaleExt().getWidth(), (int)mapLayoutModel.getScaleExt().getHeight()));
				finalg2d.fillRect(scaleRect.x, scaleRect.y, scaleRect.width, scaleRect.height);
				finalg2d.drawImage(scaleBi, scaleRect.x, scaleRect.y, scaleRect.width, scaleRect.height,null);
				finalg2d.drawRect(scaleRect.x, scaleRect.y, scaleRect.width, scaleRect.height);				

				//9、绘制图名:根据宽高判断类型，再绘制
				if((imageMetaData.isEmpty()||imageMetaData.equals(null)))                //如果元数据为空，则非通用模板，图名无需居中计算
				{
					if(mapLayoutModel.getTitleExt().width > mapLayoutModel.getTitleExt().height){
						finalg2d.setColor(Color.red);
						finalg2d.setFont(new Font("宋体", Font.BOLD, (int)(30*index)));			
						finalg2d.drawString(this.mapName, (int)mapLayoutModel.getTitleExt().getX(), (int)mapLayoutModel.getTitleExt().getY() + (int)(30*index));
					}
					else { 
						finalg2d.setColor(Color.red);
						finalg2d.setFont(new Font("宋体", Font.BOLD, (int)(30*index)));	
						for(int i=0; i < mapName.length(); i++){
							finalg2d.drawString(mapName.substring(i, i + 1), (int)mapLayoutModel.getTitleExt().getX(), (int)mapLayoutModel.getTitleExt().getY() + ((int)(30*index))*(i+1));
						}
					} 
				}
				else
				{
					//模板为通用，设置图名居中
					//设定文字样式
					Font nameTextFont = new Font("宋体", Font.BOLD, (int)(30*index));
					finalg2d.setFont(nameTextFont);
					//获取字符长宽度
					FontMetrics metrics = finalg2d.getFontMetrics();
					Rectangle2D textRectangle = metrics.getStringBounds(mapName, finalg2d);
					@SuppressWarnings("unused")
					double nameTextHeight = textRectangle.getHeight();
					double nameTextWidth = textRectangle.getWidth();

					int mapName_x = (int)((mapExtent_x+mapExtent_w/2)-nameTextWidth/2);
					int mapName_y = (int)mapLayoutModel.getTitleExt().getY();
					finalg2d.setColor(Color.black);
					finalg2d.drawString(this.mapName, mapName_x, mapName_y + (int)(30*index));
				}

				//10、绘制元数据信息 ，单位
				if(!(imageMetaData.isEmpty()||imageMetaData.equals(null)))
				{
					//设置文字右靠边
					//设定文字样式
					String metas[]=imageMetaData.split("&&");
					String time = metas[0];
					@SuppressWarnings("unused")
					String reslution = metas[1];
					String mapArea = metas[2];
					String mapOrg = metas[3];


					//时间
					Font tTextFont = new Font("宋体", Font.PLAIN, (int)(13*index));
					finalg2d.setFont(tTextFont);
					FontMetrics tMetrics = finalg2d.getFontMetrics();
					finalg2d.setColor(Color.black);
					Rectangle2D tTextRectangle = tMetrics.getStringBounds(time, finalg2d);
					@SuppressWarnings("unused")
					double tTextHeight = tTextRectangle.getHeight();
					@SuppressWarnings("unused")
					double tTextWidth = tTextRectangle.getWidth();
					int tmeta_x = (int)mapExtent_x;
					int tmeta_y = (int)mapLayoutModel.getMetaExt().getY();
					finalg2d.drawString(time, tmeta_x, tmeta_y+(int)(18*index));

					//单位信息
					Font metaTextFont = new Font("宋体", Font.PLAIN, (int)(18*index));
					finalg2d.setFont(metaTextFont);
					//获取字符长宽度
					FontMetrics metaMetrics = finalg2d.getFontMetrics();
					finalg2d.setColor(Color.black);
					Rectangle2D metaTextRectangle = metaMetrics.getStringBounds(mapOrg, finalg2d);
					@SuppressWarnings("unused")
					double metaTextHeight = metaTextRectangle.getHeight();
					double metaTextWidth = metaTextRectangle.getWidth();
					int meta_x = (int)(mapExtent_x+mapExtent_w-metaTextWidth);
					int meta_y = (int)mapLayoutModel.getMetaExt().getY() ;
					finalg2d.drawString(mapOrg, meta_x, meta_y+(int)(25*index));

					//制图区域
					Rectangle2D aTextRectangle = metaMetrics.getStringBounds(mapArea, finalg2d);
					@SuppressWarnings("unused")
					double aTextHeight = aTextRectangle.getHeight();
					double aTextWidth = aTextRectangle.getWidth();
					int ameta_x = (int)((mapExtent_x+mapExtent_w/2)-aTextWidth/2);
					int ameta_y = (int)mapLayoutModel.getMetaExt().getY();
					finalg2d.drawString(mapArea, ameta_x, ameta_y+(int)(25*index));
				}

				finalg2d.dispose();		
				ImageIO.write(theImageMap, "png", outputStream);	

			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("绘制影像地图失败："+e.toString());
			}
			return outputStream.toByteArray();
		}
	}

	//根据数据源字符串获取切片
	public BufferedImage getImageH(String srcStr, double qualityIndex,String modelId){
		BufferedImage imageMap2 = null;

		String imageURL = srcStr.split("&_&")[0];

		
		imageMap2 = getSelfWMTSImageData(imageURL, qualityIndex , mapLayoutModel.getId());
	
		return imageMap2;
	}

	public BufferedImage getImageL(String srcStr,double qualityIndex){
		BufferedImage imageMap2 = null;

		String imageURL = srcStr.split("&_&")[0];

		imageMap2 = getSelfWMTSImageDataL(imageURL, qualityIndex);

		return imageMap2;
	}

	private BufferedImage getSelfWMTSImageData(String imgURL,double qualityIndex,String modelId ){
		BufferedImage bimg = null; 
		try{
			//内外网映射
			String insideIP = getImageInsideIpServer("ZYIMAGEINSIDEIP");
			String outsideIP = getImageOutsideIpServer("ZYIMAGEOUTSIDEIP");
			imgURL = imgURL.replace(outsideIP, insideIP);

			int imageWidth = 0;
			int imageHeight = 0;
			
			
			if(modelId.equals("100")||modelId.equals("101")||modelId.equals("102")){
				PaperSize ps = new PaperSize("A3", 300, 0);
				imageWidth = ps.getWidth();
				imageHeight = ps.getHeight();
			}else{
				PaperSize ps = new PaperSize("A3", 300, 1);
				imageWidth = ps.getWidth();
				imageHeight = ps.getHeight();
			}
			
			
			String tmpURL=new String();
			tmpURL=imgURL;
			String[] tmpstrs=tmpURL.split(";");
			String imageUrl=tmpstrs[0];
			String[] tmpstrs2=tmpstrs[1].split("#");
			int tileMinCol=Integer.parseInt((tmpstrs2[0].split("="))[1]);
			int tileMaxCol=Integer.parseInt((tmpstrs2[1].split("="))[1]);
			int tileMinRow=Integer.parseInt((tmpstrs2[2].split("="))[1]);
			int tileMaxRow=Integer.parseInt((tmpstrs2[3].split("="))[1]);
			int imageLevel=Integer.parseInt((tmpstrs2[4].split("="))[1]);
			Double tileX=Double.parseDouble((tmpstrs2[5].split("="))[1]);
			Double tileY=Double.parseDouble((tmpstrs2[6].split("="))[1]);
			Double tileWidth=Double.parseDouble((tmpstrs2[7].split("="))[1]);
			Double tileHeight=Double.parseDouble((tmpstrs2[8].split("="))[1]);
			Double extentX=Double.parseDouble((tmpstrs2[9].split("="))[1]);
			Double extentY=Double.parseDouble((tmpstrs2[10].split("="))[1]);
			Double extentWidth=Double.parseDouble((tmpstrs2[11].split("="))[1]);
			Double extentHeight=Double.parseDouble((tmpstrs2[12].split("="))[1]);
			int LevelL=Integer.parseInt((tmpstrs2[17].split("="))[1]);
			String mapUrl=new String();
			index=imageLevel-LevelL;

			//拼接切片成影像图
			int mapWidth=0;
			int mapHeight=0;			
			int colnum=tileMaxCol-tileMinCol+1;
			int rownum=tileMaxRow-tileMinRow+1;	
			BufferedImage[][] bi=new BufferedImage[colnum][rownum];
			InputStream is=null;
			for(int i = tileMinCol;i <= tileMaxCol;i++){
				for(int j=tileMinRow;j<=tileMaxRow;j++){
					//					mapUrl=	imageUrl+"&tileMatrix="+imageLevel+"&tileRow="+j+"&tileCol="+i;
					mapUrl = imageUrl+"/tile/"+imageLevel+"/"+j+"/"+i;
					URL url = new URL(mapUrl);
					try {
						// 打开连接
						URLConnection con = url.openConnection();
						// 输入流
						is = con.getInputStream();
						bi[i-tileMinCol][j-tileMinRow] = ImageIO.read(is);				    
					} catch (IOException e) {
						// TODO Auto-generated catch block
						BufferedImage biT= new BufferedImage(bi[0][0].getWidth(),bi[0][0].getHeight(),BufferedImage.TYPE_INT_RGB);
						Graphics2D g2dT=(Graphics2D)biT.createGraphics();
						biT=g2dT.getDeviceConfiguration().createCompatibleImage(bi[0][0].getWidth(),bi[0][0].getHeight(),Transparency.TRANSLUCENT);
						bi[i-tileMinCol][j-tileMinRow]=biT;	
					}
				}
			}
			for(int i=0;i<colnum;i++){
				mapWidth+= bi[i][0].getWidth();
			}
			for(int j=0;j<rownum;j++){
				mapHeight+= bi[0][j].getHeight();
			}
			BufferedImage imageMap = new BufferedImage(mapWidth,mapHeight,BufferedImage.TYPE_INT_RGB);			
			Graphics2D g2d = (Graphics2D) imageMap.createGraphics();
			//设置透明底色
			imageMap=g2d.getDeviceConfiguration().createCompatibleImage(mapWidth,mapHeight, Transparency.TRANSLUCENT);
			g2d.dispose();
			g2d=(Graphics2D)imageMap.createGraphics();			

			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);	
			int widthgap=bi[0][0].getWidth();
			int heightgap=bi[0][0].getHeight();
			for(int i=0;i<colnum;i++){
				for(int j=0;j<rownum;j++){				
					int x=widthgap*i;
					int y=heightgap*j;
					g2d.drawImage(bi[i][j],null,x,y);
				}
			}
			g2d.dispose();

			//截取范围内的影像
			Double wT=mapWidth/tileWidth;
			Double hT=mapHeight/tileHeight;
			int imgTW=(int)(extentWidth*wT);
			int imgTH=(int)(extentHeight*hT);
			int cutX=(int)((extentX-tileX)*wT);
			int cutY=(int)((tileY-extentY)*hT);
			//			ImageIO.write(imageMap,"png", new File("C:\\img\\getfinalimg\\self\\selfImg.png"));
			bimg=imageMap.getSubimage(cutX,cutY,imgTW,imgTH);
			mapHW = imgTW;
//			ImageIO.write(bimg,"png", new File("C:\\img\\getfinalimg\\self\\selfSubImg.png"));
			
			//A3
			BufferedImage a3Bimg = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d1 = a3Bimg.createGraphics();
			
			g2d1.drawImage(bimg, 0,0,imageWidth, imageHeight, null);
			//			ImageIO.write(bimg,"png", new File("C:\\img\\getfinalimg\\selfL\\selfSubImg.png"));
//			ImageIO.write(a3Bimg, "png", new File("D:\\testImage\\testTile1.png"));
			//关闭连接
			is.close();
			return a3Bimg;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("绘制影像地图失败："+e.toString());
		}
		return bimg; 
	}


	@SuppressWarnings("unused")
	private BufferedImage getBaiduImageL(String imgURL,double qualityIndex){
		BufferedImage bimg = null;
		return bimg;
	}

	private BufferedImage getSelfWMTSImageDataL(String imgURL,double qualityIndex){
		BufferedImage bimg = null; 
		try{
			//内外网映射
			String insideIP = getImageInsideIpServer("ZYIMAGEINSIDEIP");
			String outsideIP = getImageOutsideIpServer("ZYIMAGEOUTSIDEIP");
			imgURL = imgURL.replace(outsideIP, insideIP);

			String tmpURL=new String();
			tmpURL=imgURL;
			String[] tmpstrs=tmpURL.split(";");
			String imageUrl=tmpstrs[0];
			String[] tmpstrs2=tmpstrs[1].split("#");
			System.out.println("imgURL= " +imgURL);
			System.out.println("tmpstrs = "+ imageUrl+"      "+"tmpstrs2 =" + tmpstrs[1]);
			int tileMinCol=Integer.parseInt((tmpstrs2[13].split("="))[1]);
			int tileMaxCol=Integer.parseInt((tmpstrs2[14].split("="))[1]);
			int tileMinRow=Integer.parseInt((tmpstrs2[15].split("="))[1]);
			int tileMaxRow=Integer.parseInt((tmpstrs2[16].split("="))[1]);
			int imageLevel=Integer.parseInt((tmpstrs2[17].split("="))[1]);
			Double tileX=Double.parseDouble((tmpstrs2[18].split("="))[1]);
			Double tileY=Double.parseDouble((tmpstrs2[19].split("="))[1]);
			Double tileWidth=Double.parseDouble((tmpstrs2[20].split("="))[1]);
			Double tileHeight=Double.parseDouble((tmpstrs2[21].split("="))[1]);
			Double extentX=Double.parseDouble((tmpstrs2[22].split("="))[1]);
			Double extentY=Double.parseDouble((tmpstrs2[23].split("="))[1]);
			Double extentWidth=Double.parseDouble((tmpstrs2[24].split("="))[1]);
			Double extentHeight=Double.parseDouble((tmpstrs2[25].split("="))[1]);
			Integer.parseInt((tmpstrs2[4].split("="))[1]);
			String mapUrl=new String();
			index=0.0;

			//拼接切片成影像图
			int mapWidth=0;
			int mapHeight=0;			
			int colnum=tileMaxCol-tileMinCol+1;
			int rownum=tileMaxRow-tileMinRow+1;	
			BufferedImage[][] bi=new BufferedImage[colnum][rownum];
			InputStream is=null;
			for(int i = tileMinCol;i <= tileMaxCol;i++){
				for(int j=tileMinRow;j<=tileMaxRow;j++){
					//					mapUrl=	imageUrl+"&tileMatrix="+imageLevel+"&tileRow="+j+"&tileCol="+i;
					mapUrl = imageUrl+"/tile/"+imageLevel+"/"+j+"/"+i;
					URL url = new URL(mapUrl);
					try {
						// 打开连接
						URLConnection con = url.openConnection();
						// 输入流
						is = con.getInputStream();
						bi[i-tileMinCol][j-tileMinRow] = ImageIO.read(is);				    
					} catch (IOException e) {
						// TODO Auto-generated catch block
						BufferedImage biT= new BufferedImage(bi[0][0].getWidth(),bi[0][0].getHeight(),BufferedImage.TYPE_INT_RGB);
						Graphics2D g2dT=(Graphics2D)biT.createGraphics();
						biT=g2dT.getDeviceConfiguration().createCompatibleImage(bi[0][0].getWidth(),bi[0][0].getHeight(),Transparency.TRANSLUCENT);
						bi[i-tileMinCol][j-tileMinRow]=biT;	
					}
				}
			}
			for(int i=0;i<colnum;i++){
				mapWidth+= bi[i][0].getWidth();
			}
			for(int j=0;j<rownum;j++){
				mapHeight+= bi[0][j].getHeight();
			}
			BufferedImage imageMap = new BufferedImage(mapWidth,mapHeight,BufferedImage.TYPE_INT_RGB);			
			Graphics2D g2d = (Graphics2D) imageMap.createGraphics();
			//设置透明底色
			imageMap=g2d.getDeviceConfiguration().createCompatibleImage(mapWidth,mapHeight, Transparency.TRANSLUCENT);
			g2d.dispose();
			g2d=(Graphics2D)imageMap.createGraphics();			

			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);	
			int widthgap=bi[0][0].getWidth();
			int heightgap=bi[0][0].getHeight();
			for(int i=0;i<colnum;i++){
				for(int j=0;j<rownum;j++){				
					int x=widthgap*i;
					int y=heightgap*j;
					g2d.drawImage(bi[i][j],null,x,y);
				}
			}
			g2d.dispose();

			bimg=imageMap;
			//			ImageIO.write(imageMap, "png", new File("D:\\testImage\\testTile1.png"));
			//截取范围内的影像
			Double wT=mapWidth/tileWidth;
			Double hT=mapHeight/tileHeight;
			int imgTW=(int)(extentWidth*wT);
			int imgTH=(int)(extentHeight*hT);
			int cutX=(int)((extentX-tileX)*wT);
			int cutY=(int)((tileY-extentY)*hT);
			bimg=imageMap.getSubimage(cutX,cutY,imgTW,imgTH);
			mapLW = imgTW;
			
			//			bimg=imageMap.getSubimage(cutX,cutY,imgTW,imgTH);///
			//			//ImageIO.write(bimg, "png", new File("C:\\img\\sres\\subImage.png"));
			//关闭连接
			is.close();
			return bimg;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("绘制影像地图失败："+e.toString());
		}
		return bimg; 
	}



	//读取图片
	public BufferedImage readImage(String imagestr){

		InputStream   in   =   null;   
		in   =   Thread.currentThread().getContextClassLoader().getResourceAsStream(imagestr); 
		BufferedInputStream file;
		BufferedImage bi=null;
		file = new BufferedInputStream(in);
		try{
			bi = ImageIO.read(file);
		}catch(Exception e){
			System.out.println("绘制影像地图失败："+e.toString());
		} 
		return bi;    	 
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

	//	private Rectangle modifySize(Rectangle WC, Rectangle DC){
	//		Rectangle resultRect = new Rectangle();
	//		double scale1 = WC.getWidth() / DC.getWidth();
	//		double scale2 = WC.getHeight() / DC.getHeight();
	//		double scale = scale1 > scale2 ? scale1 : scale2;
	//
	//		//计算出按照缩放倍率缩放后，dc跟缩放后的wc的宽、高差值，然后进行平移，保证缩放后的wc在dc中居中绘制图形
	//		double sx = (DC.getWidth() - (WC.getWidth() * 1 / scale))  + DC.x;
	//		double sy = (DC.getHeight() - (WC.getHeight() * 1 / scale))  + DC.y;
	//		double width = WC.getWidth() * 1 / scale;
	//		double height = WC.getHeight() * 1 / scale;
	//
	//		resultRect.x = (int)sx;
	//		resultRect.y = (int)sy;
	//		resultRect.width = (int)width;
	//		resultRect.height = (int)height;		
	//		return resultRect;
	//	}

	private Rectangle modifyToMiddle(Rectangle WC, Rectangle DC){
		Rectangle resultRect = new Rectangle();
		double scale1 = WC.getWidth() / DC.getWidth();
		double scale2 = WC.getHeight() / DC.getHeight();
		double scale = scale1 > scale2 ? scale1 : scale2;

		//计算出按照缩放倍率缩放后，dc跟缩放后的wc的宽、高差值，然后进行平移，保证缩放后的wc在dc中居中绘制图形
		double sx = (DC.getWidth() - (WC.getWidth() * 1 / scale)) / 2 + DC.x;
		double sy = (DC.getHeight() - (WC.getHeight() * 1 / scale)) / 2 + DC.y;
		double width = WC.getWidth() * 1 / scale;
		double height = WC.getHeight() * 1 / scale;

		resultRect.x = (int)sx;
		resultRect.y = (int)sy;
		resultRect.width = (int)width;
		resultRect.height = (int)height;		
		return resultRect;
	}

	//判定布局要素在地图幅面的方位，1,2,3,4个象限；然后确定绘制要素图片的位置
	private Rectangle getDrawRect(Rectangle originSize, Rectangle resultSize){
		Rectangle resultRect = new Rectangle();
		double scale1 = originSize.getWidth() / resultSize.getWidth();
		double scale2 = originSize.getHeight() / resultSize.getHeight();
		double scale = scale1 > scale2 ? scale1 : scale2;

		double sx,sy,width,height;
		if((this.mapLayoutModel.getMapExt().getCenterX() > resultSize.getCenterX()) //左上角
				&& (this.mapLayoutModel.getMapExt().getCenterY() > resultSize.getCenterY())){
			sx = resultSize.x;
			sy = resultSize.y;
			width = originSize.getWidth() * 1 / scale;
			height = originSize.getHeight() * 1 / scale;
		}else if((this.mapLayoutModel.getMapExt().getCenterX() < resultSize.getCenterX()) //右上角
				&& (this.mapLayoutModel.getMapExt().getCenterY() > resultSize.getCenterY())){
			sx = (resultSize.getWidth() - (originSize.getWidth() * 1 / scale)) + resultSize.x;
			sy = resultSize.y;
			width = originSize.getWidth() * 1 / scale;
			height = originSize.getHeight() * 1 / scale;
		}else if((this.mapLayoutModel.getMapExt().getCenterX() > resultSize.getCenterX()) //左下角
				&& (this.mapLayoutModel.getMapExt().getCenterY() < resultSize.getCenterY())){
			sx = resultSize.x;
			sy = (resultSize.getHeight() - (originSize.getHeight() * 1 / scale)) + resultSize.y;
			width = originSize.getWidth() * 1 / scale;
			height = originSize.getHeight() * 1 / scale;
		}else {
			sx = (resultSize.getWidth() - (originSize.getWidth() * 1 / scale)) + resultSize.x;
			sy = (resultSize.getHeight() - (originSize.getHeight() * 1 / scale)) + resultSize.y;
			width = originSize.getWidth() * 1 / scale;
			height = originSize.getHeight() * 1 / scale;
		}
		resultRect.x = (int)sx;
		resultRect.y = (int)sy;
		resultRect.width = (int)width;
		resultRect.height = (int)height;		
		return resultRect;
	}


	public String getCartoInsideIpServer()
	{
		//获取配置文件当中用户记录数的最大值
		CreateWebService cws = new CreateWebService();
		Properties ps = cws.ps;
		String insideIP = ps.getProperty("CARTOINSIDEIP");
		return insideIP;
	}

	public String getCartoOutsideIpServer()
	{
		//获取配置文件当中用户记录数的最大值
		CreateWebService cws = new CreateWebService();
		Properties ps = cws.ps;
		String outsideIP = ps.getProperty("CARTOOUTSIDEIP");
		return outsideIP;
	}

	public String getImageInsideIpServer(String insideImageIP)
	{
		//获取配置文件当中用户记录数的最大值
		CreateWebService cws = new CreateWebService();
		Properties ps = cws.ps;
		String insideIP = ps.getProperty(insideImageIP);
		return insideIP;
	}

	public String getImageOutsideIpServer(String outsideImageIP)
	{
		//获取配置文件当中用户记录数的最大值
		CreateWebService cws = new CreateWebService();
		Properties ps = cws.ps;
		String outsideIP = ps.getProperty(outsideImageIP);
		return outsideIP;
	}

	public static void main(String[] args) throws IOException{
//		String mapUrl = "http://localhost:6080/arcgis/rest/services/LHKBgTileMap/MapServer/tile/4/18853/26254";
//		URL url;
//		try {
//			url = new URL(mapUrl);
//			URLConnection con = url.openConnection();
//			// 输入流
//			InputStream is=null;
//			is = con.getInputStream();
//			BufferedImage bi = new BufferedImage(100, 100,BufferedImage.TYPE_INT_RGB);
//			bi= ImageIO.read(is);
//			ImageIO.write(bi, "png", new File("D:\\testtileMap.png"));
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
	}
}
