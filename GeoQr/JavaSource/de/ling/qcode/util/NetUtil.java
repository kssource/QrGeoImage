package de.ling.qcode.util;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

public class NetUtil {

    private static final int BUFFER_SIZE = 4096;

    public static BufferedImage getStaticMapImage(int zoom, String lat, String lng, int size ){
    	BufferedImage bufImg = null;
    	
        try {
        	StringBuilder builder = new StringBuilder("http://maps.googleapis.com/maps/api/staticmap");
        	builder.append("?center=").append(lat).append(",").append(lng);
        	builder.append("&zoom=").append(zoom);
        	builder.append("&size=").append(size).append("x").append(size);
        	builder.append("&markers=").append(lat).append(",").append(lng);
        	builder.append("&sensor=false");
        	
        	String imgMapUrl = builder.toString();
        	
        	
        	URL url = new URL(imgMapUrl);
            URLConnection conn = url.openConnection();

            InputStream inputStream = conn.getInputStream();
            bufImg = writeToBuffBuff(inputStream);
            
//            saveToFile(inputStream, savePath);

        } catch (IOException ex) {
            ex.printStackTrace();
        }    	
    	
        return bufImg;
    }

    public static BufferedImage writeToBuffBuff(InputStream inputStream){
		BufferedImage buffImg = null;

		try {

			BufferedImage img=ImageIO.read(inputStream);

			inputStream.close();

			buffImg = img;
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return buffImg;
    	
    }

    
    
    public static boolean saveToFile(InputStream inputStream, String outFilePath){
		boolean saved = false;

		String savePath = outFilePath;

		try {

			FileOutputStream outputStream = new FileOutputStream(savePath);

			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			outputStream.close();
			inputStream.close();

			System.out.println("File Saved");
			saved = true;
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return saved;
    	
    }
    
	
	
	
}
