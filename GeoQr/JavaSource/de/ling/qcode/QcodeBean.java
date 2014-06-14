package de.ling.qcode;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.google.zxing.WriterException;

import de.ling.qcode.util.EncodeResult;
import de.ling.qcode.util.EncodeResult.RESULT_CODE;
import de.ling.qcode.util.ImgUtil;
import de.ling.qcode.util.QrGeoImage;

@ManagedBean
@SessionScoped
public class QcodeBean implements Serializable {

	private static final long serialVersionUID = 1L;


	private int zoom = 14;
	private String latit = "52.52193";
	private String longit = "13.41321";

	//initialValues
	private int imageSize = 500;
	private int circleRadius = 100;
	private int qrImgTransparenz = 40;


	private int dummyData = 0;
	// prevent image caching
	public int getDummyData() {
		return dummyData++;
	}

	public void testAction() {
		System.out.println("testAction(), ");
	}

	public void downloadAction() {
		try {
			HttpServletResponse response = (HttpServletResponse) FacesContext
							.getCurrentInstance().getExternalContext().getResponse();
			response.setContentType("image/png");
			response.setHeader("Content-Disposition", "attachment;filename=qr.png");

			BufferedImage img = createImage();
			ServletOutputStream responseStream = response.getOutputStream();
			ImageIO.write(img, "png", responseStream);

			FacesContext.getCurrentInstance().responseComplete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void paintImage(OutputStream out, Object data) throws IOException{
		BufferedImage outBufImg = createImage();
		if(outBufImg != null){
			ImageIO.write(outBufImg, "png", out);
		}
		
	}
	
	private BufferedImage createImage(){
		BufferedImage outBufImg = null;
	
		try {
			String lat = latit;
			if(lat.length() > 8)	lat = lat.substring(0, 8);
			String lon = longit;
			if(lon.length() > 8)	lon = lon.substring(0, 8);
			
			EncodeResult result = QrGeoImage.createGeoQr(lat, lon, zoom, imageSize, circleRadius, qrImgTransparenz);
			if(result.resultCode == RESULT_CODE.CREATED){
				outBufImg = result.image;
			}else{
				outBufImg = getErrImg();
			}
		} catch (WriterException e) {
			e.printStackTrace();
		} 

		return outBufImg;
	}

	
	


	public int getZoom() {
		return zoom;
	}

	public void setZoom(int zoom) {
		this.zoom = zoom;
	}

	public String getLatit() {
		return latit;
	}

	public void setLatit(String latit) {
		this.latit = latit;
	}

	public String getLongit() {
		return longit;
	}

	public void setLongit(String longit) {
		this.longit = longit;
	}

	public int getImageSize() {
		if(imageSize<10)	imageSize = 10;
		return imageSize;
	}

	public void setImageSize(int imageSize) {
		this.imageSize = imageSize;
	}

	public int getCircleRadius() {
		return circleRadius;
	}

	public void setCircleRadius(int circleRadius) {
		this.circleRadius = circleRadius;
	}


	public int getQrImgTransparenz() {
		return qrImgTransparenz;
	}

	public void setQrImgTransparenz(int qrImgTransparenz) {
		this.qrImgTransparenz = qrImgTransparenz;
	}

	//////////////////////////////


	public BufferedImage getErrImg() {
		BufferedImage errImg = ImgUtil.createErrorImg();
		return errImg;
	}
	
}
