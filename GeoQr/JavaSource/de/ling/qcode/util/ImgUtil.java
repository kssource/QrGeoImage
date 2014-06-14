package de.ling.qcode.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class ImgUtil {
	
	//transpBits 00 - is invisible
//	private static final int WHITE_PARTIAL_TRASPAREN = 0x55FFFFFF;//transparency for white(background) pixel in qrCode
	private static final int QR_BG_COLOR = 0xFFFFFFFF;// default Qr-Code bg is white

	
	private static int getWhitePartialTrasparentColor(int percentOfTransparents){
		int alphaVal = (int) ((double)percentOfTransparents * 255/100);
//		System.out.println("alphaVal: "+ Integer.toHexString(alphaVal));
		if(alphaVal<1)	alphaVal=1;
			
		int alphaBits = alphaVal << 24;
		int out = 0x00FFFFFF | alphaBits;
		
		return out;
	}
	


	public static BufferedImage createQrCodeLayer(BufferedImage origBufImg, int[] codeRect, int imgSize,
			int circleRadius, int qrImgTransparenz) {

		int left = codeRect[0];
		int top = codeRect[1];
		int width = codeRect[2];
		int height = codeRect[3];
		
		// remove edges
		//crop
		BufferedImage subImg = origBufImg.getSubimage(left, top, width, height);
		
		//scale
		BufferedImage resizedImg = resizeToBig(subImg, imgSize, imgSize);
		
		//create transparency
		double radius = circleRadius;
		int circleX = (int) (imgSize/2.0 - radius);
		int circleY = circleX;
		int diameter = (int) (2*radius); 
		
		Graphics2D g2d = resizedImg.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Color whiteColor = Color.white;

		g2d.setColor(whiteColor);
		g2d.fillOval(circleX, circleY, diameter, diameter);
		g2d.dispose();
		
		// make white transparent

		//image center
		double newImgSize = imgSize;
		double centerX = newImgSize/2;
		double centerY = newImgSize/2;
		
		int qrTransparents = getWhitePartialTrasparentColor(qrImgTransparenz);

		for (int y = 0; y < resizedImg.getHeight(); y++) {
		    for (int x = 0; x < resizedImg.getWidth(); x++) {
				boolean isInCircle = isPointInCircle(centerX, centerY, radius, x, y);
      
		    	int  rgb   = resizedImg.getRGB(x, y); 
		    	
	    		if(rgb == QR_BG_COLOR){
			    	if(isInCircle){
				        int newRgb = 0x01FFFFFF & rgb;//full transparent, by 0x00FFFFFF ignored alpha
				        resizedImg.setRGB(x, y, newRgb);
			    	}else{
				        int newRgb = qrTransparents & rgb; //partially transparent
				        resizedImg.setRGB(x, y, newRgb);
			    			
			    	}
	    			
	    		}
	    		
		    }
		}		

		return resizedImg;
	}


	public static BufferedImage mergeImages(BufferedImage img1, BufferedImage img2) {

		int width = Math.max(img1.getWidth(), img2.getWidth());
		int height = Math.max(img1.getHeight(), img2.getHeight());
		
	    int type = BufferedImage.TYPE_INT_ARGB;

	    BufferedImage outImage = new BufferedImage(width, height, type);
	    Graphics2D g = outImage.createGraphics();
	    g.drawImage(img1, 0, 0, width, height, null);
	    g.drawImage(img2, 0, 0, width, height, null);
	    
	    g.dispose();

	    return outImage;
	}
	
	
	private static BufferedImage resizeToBig(Image originalImage, int biggerWidth, int biggerHeight) {
	    int type = BufferedImage.TYPE_INT_ARGB;

	    BufferedImage resizedImage = new BufferedImage(biggerWidth, biggerHeight, type);
	    Graphics2D g = resizedImage.createGraphics();

	    g.setComposite(AlphaComposite.Src);
	    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	    g.drawImage(originalImage, 0, 0, biggerWidth, biggerHeight, null);
	    g.dispose();

	    return resizedImage;
	}
	
	
	
	
	
	// ////////////////////// utils
	private static boolean isInRectangle(double centerX, double centerY, double radius, double x, double y) {
		return x >= centerX - radius && x <= centerX + radius
				&& y >= centerY - radius && y <= centerY + radius;
	}

	// test if coordinate (x, y) is within a radius from coordinate (center_x,
	// center_y)
	private static boolean isPointInCircle(double centerX, double centerY,
													double radius, double x, double y) {
		if (isInRectangle(centerX, centerY, radius, x, y)) {
			double dx = centerX - x;
			double dy = centerY - y;
			dx *= dx;
			dy *= dy;
			double distanceSquared = dx + dy;
			double radiusSquared = radius * radius;
			return distanceSquared <= radiusSquared;
		}
		return false;
	}



	// write errorMessage over inImg
	public static BufferedImage drawAsInvalidImage(BufferedImage inImg) {
		String errString = "DECODING FAILED";
		
		int imgWidth = inImg.getWidth();
		int imgHeight = inImg.getHeight();
		
	    Graphics2D g = inImg.createGraphics();
	    
	    Rectangle2D strBounds = g.getFontMetrics().getStringBounds(errString, g);
	    
	    int xPos = (int) (imgWidth/2 - strBounds.getWidth()/2);
	    int yPos = (int) (imgHeight/2 - strBounds.getHeight()/2);
	    
	    g.drawString(errString, xPos, yPos);
	    
	    g.dispose();

	    return inImg;
	}


	public static BufferedImage createErrorImg() {
		String errString = "QR DECODING FAILED";
		
		int imgWidth = 500;
		int imgHeight = 500;
		
	    int type = BufferedImage.TYPE_INT_ARGB;
	    BufferedImage outImage = new BufferedImage(imgWidth, imgHeight, type);
	    Graphics2D g = outImage.createGraphics();
	    
	    // draw bg
	    g.setColor(Color.LIGHT_GRAY);
	    g.fillRect(0, 0, imgWidth, imgHeight);

	    // draw msg
	    Font yFont = new Font("Arial", Font.BOLD, 20);
	    g.setFont(yFont);
	    g.setColor(Color.red);

	    Rectangle2D strBounds = g.getFontMetrics().getStringBounds(errString, g);
	    int xPos = (int) (imgWidth/2 - strBounds.getWidth()/2);
	    int yPos = (int) (imgHeight/2 - strBounds.getHeight()/2);
	    g.drawString(errString, xPos, yPos);
	    
	    g.dispose();

	    return outImage;
	}

	public static BufferedImage addWhiteBorder(BufferedImage inBufImg, int borderWidth) {

	    int type = BufferedImage.TYPE_INT_ARGB;

	    int inWidth = inBufImg.getWidth();
	    int inHeight = inBufImg.getHeight();
	    
	    int biggerWidth = inWidth+2*borderWidth;
		int biggerHeight = inHeight+2*borderWidth;
		BufferedImage borderedImage = new BufferedImage(biggerWidth, biggerHeight, type);
	    Graphics2D g = borderedImage.createGraphics();

	    // draw bg
	    g.setColor(Color.WHITE);
	    g.fillRect(0, 0, biggerWidth, biggerHeight);

	    g.setComposite(AlphaComposite.Src);
	    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	    g.drawImage(inBufImg, borderWidth, borderWidth, inWidth, inHeight, null);
	    g.dispose();

	    return borderedImage;


	}

}
