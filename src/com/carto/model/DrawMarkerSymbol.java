package com.carto.model;

import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.dbconn.ConnOrcl;
import net.sf.json.JSONObject;

public class DrawMarkerSymbol {
	
	/**
	 * @author lb
	 */

	public Rectangle2D.Double wc;
	public Rectangle2D.Double dc;
	public int addMarker=1;
	public String DC ;
	public String WC ;
	public String markerString ;
	public ConnOrcl connection = ConnOrcl.getInstance();
	public double qualityIndex = 1;
	
	
	 public DrawMarkerSymbol() {
		// TODO Auto-generated constructor stub
	}
	 
	 /////初始化参数
	 public void iniparam(String mapID,String mapDC,double qualityIndex){
			/////在线标注数据读取
			String markerResult = connection.getMarkerString("IMAGE_MAP", mapID);
			String[] mString = markerResult.split("@");
			addMarker = Integer.parseInt(mString[0]);
			DC = mString[1];
			this.DC = mapDC;
			WC = mString[2];
		    markerString = mString[3];
		    
		    this.qualityIndex = qualityIndex;
			
			String[] wc_str = WC.split(",");
			String[] dc_str = DC.split(",");
			double[] wc_arr = new double[4];
			double[] dc_arr = new double[4];
			for(int i=0;i<wc_str.length;i++)
			{
				wc_arr[i] = Double.parseDouble(wc_str[i]);
				dc_arr[i] = Double.parseDouble(dc_str[i]);
			}
			
			wc = new Rectangle2D.Double(wc_arr[0], wc_arr[1], wc_arr[2]-wc_arr[0], wc_arr[3]-wc_arr[1]);
			dc = new Rectangle2D.Double(dc_arr[0], dc_arr[1], dc_arr[2]-dc_arr[0], dc_arr[3]-dc_arr[1]);
	 }

	 
	 public void drawSymbol(JSONObject markerObject,Rectangle2D.Double wc,Rectangle2D.Double dc,Graphics2D marker2D) throws IOException{
		String symbolTypeString ;
		@SuppressWarnings("unused")
		BufferedImage symbolImage = null;
			symbolTypeString = markerObject.getString("type");
//			System.out.println(markerJsonObjects.toString());
			if(symbolTypeString.equals("GeoPolygonSymbol")){
				PolygonSymbol polygonSymbol = new PolygonSymbol();
				polygonSymbol.setPolygonAttr(markerObject,qualityIndex);
//				symbolImage = polygonSymbol.drawSymbol(wc,dc);
				polygonSymbol.drawSymbol(wc,dc,marker2D);
			}
			if(symbolTypeString.equals("SimpleLineSymbol")){
				LineSymbol lineSymbol = new LineSymbol();
				lineSymbol.setLineAttr(markerObject,qualityIndex);
//				symbolImage = lineSymbol.drawSymbol(wc,dc);
				lineSymbol.drawSymbol(wc,dc,marker2D);
			}
			if(symbolTypeString.equals("GeoTextSymbol")){
				if (markerObject.getJSONArray("pointArr").getJSONObject(0).getString("markSymbol").equals("null") && markerObject.getJSONArray("pointArr").getJSONObject(0).getString("pMarkerSymbol").equals("null")) {
					String text = markerObject.getJSONArray("pointArr").getJSONObject(0).getJSONObject("textSym").getString("text");
					if(!(text.equals("null")||text.equals("")))
					{
						TextSymbol textSymbol = new TextSymbol();
						textSymbol.setTextAttr(markerObject,qualityIndex);
//						symbolImage = textSymbol.drawSymbol(wc,dc);
						textSymbol.drawSymbol(wc,dc,marker2D);
					}
				}
				else {
					PointSymbol pointSymbol = new PointSymbol();
					pointSymbol.setPointAttr(markerObject,qualityIndex);
//					symbolImage = pointSymbol.drawSymbol(wc,dc);
					pointSymbol.drawSymbol(wc,dc,marker2D);
				}

			}
	 }
	 
	 ///////林斌 添加  3.09
	    public BufferedImage drawMarker(){
	    	
	    	BufferedImage allMarkerBufferedImage = null;
	    	try {
	    	
	    		String[] mString = markerString.split("#_#");
	    		
	    		@SuppressWarnings("unused")
				BufferedImage markerImage = null;

	    		allMarkerBufferedImage = new BufferedImage((int)dc.width, (int)dc.height, BufferedImage.TYPE_BYTE_INDEXED);
	    		Graphics2D marker2D = (Graphics2D)allMarkerBufferedImage.createGraphics();
	    		allMarkerBufferedImage = marker2D.getDeviceConfiguration().createCompatibleImage((int)dc.width, (int)dc.height, Transparency.TRANSLUCENT);
	    		marker2D.dispose();
	    		marker2D = allMarkerBufferedImage.createGraphics();

	    		for(int i=1;i<mString.length;i++)
	    		{
	    			JSONObject mObject = JSONObject.fromObject(mString[i]);
//	    			markerImage = getTypeSymbolByte(mObject,wc,dc);
//	    			marker2D.drawImage(markerImage, null, null);
	    			drawSymbol(mObject,wc,dc,marker2D);
	    		}
	    		marker2D.dispose();

	    	} catch (Exception e) {
	    		// TODO: handle exception
	    		System.out.println(e.getMessage());
	    	}

	    	return allMarkerBufferedImage;
	    }
	    
	    /////////////////////////////
		public void saveMarkerSymbol(String imageMapID,String markerContent)
		{
			try {
				String mapID = imageMapID;
				String markerString = markerContent;
				
				@SuppressWarnings("unused")
				boolean result = connection.saveMarkerAsClob("IMAGE_MAP",mapID,markerString);
//				if(result)
//					System.out.println("标注绘制失败");
//				else
//					System.out.println("标注绘制失败");
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("保存在线标注失败："+e.getMessage());
			}
		}
	 
}
