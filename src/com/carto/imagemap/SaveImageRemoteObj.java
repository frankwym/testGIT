package com.carto.imagemap;

import com.carto.model.ImageMap;

public class SaveImageRemoteObj {
	
	public byte[] saveImg(String mapID){
		byte[] bytes=null;
		ImageMap imagemap=new ImageMap(mapID);
		bytes = imagemap.drawImageMap(true);
		return bytes;
//		BufferedImage bi = null;
//		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
//		try {
//			bi =ImageIO.read(bais);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return bi;
	}
}
