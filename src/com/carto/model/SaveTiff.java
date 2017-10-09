/**
 * 
 */
package com.carto.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import com.sun.media.imageio.plugins.tiff.TIFFImageWriteParam;

/**
 * @author shenyang 2013-8-9
 *
 */
public class SaveTiff {
	/**
	 * 
	 */
	public SaveTiff() {
		// TODO Auto-generated constructor stub
	}
	
	public boolean saveTiff(String filename, BufferedImage image) {

		File tiffFile = null;
		TIFFImageWriteParam writeParam = null;
		ImageWriter writer = null;
		ImageOutputStream ios = null;
		IIOImage iioImage =null;
		try {
			//set outfile
			tiffFile = new File(filename);
			// find an appropriate writer
			Iterator<ImageWriter> itwriter = ImageIO.getImageWritersByFormatName("TIF");
			if (itwriter.hasNext()) {
				writer = (ImageWriter)itwriter.next();
			} else {
				return false;
			}
			// setup writer
			ios = ImageIO.createImageOutputStream(tiffFile);
			writer.setOutput(ios);
			writeParam = new TIFFImageWriteParam(Locale.ENGLISH);
			writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			// see writeParam.getCompressionTypes() for available compression type strings
			writeParam.setCompressionType("PackBits");
			// convert to an IIOImage
			iioImage = new IIOImage(image, null, null);
			// write it!
			writer.write(null, iioImage, writeParam);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
