package de.ling.qcode.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import de.ling.qcode.util.EncodeResult.RESULT_CODE;

public class QrGeoImage {

	private static final ErrorCorrectionLevel CORR_LEVEL = ErrorCorrectionLevel.H;
	private static final String charset = "UTF-8";

//	private static final String testFilePath = "E:/dev/webEntw/test4/tmp/QRCode.png";
//	private static final String testFilePath2 = "E:/dev/webEntw/test4/tmp/QRCode2.png";


	public static EncodeResult createGeoQr(String lat, String lon,
			int zoom, int imgSize, int circleRadius, int qrImgTransparenz) throws WriterException {

		BufferedImage outImg = null;
		Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, CORR_LEVEL);

		// create data string
		StringBuilder builder = new StringBuilder("geo:");
		builder.append(lat).append(",").append(lon).append("?z=").append(zoom);
		String dataStr = builder.toString();

		BufferedImage qrCodeLayer = createQRCodeLayer(dataStr, hintMap, imgSize, circleRadius, qrImgTransparenz);
		BufferedImage mapLayer = createMapLayer(lat, lon, zoom, imgSize);
		
		outImg = ImgUtil.mergeImages(mapLayer, qrCodeLayer);
//		writeToTestFile(outImg);

		// check legibility of qrCode
		boolean readable = checkLegibility(outImg);
		EncodeResult result = new EncodeResult();
		if(readable){
			result.resultCode = RESULT_CODE.CREATED;
		}else{
			result.resultCode = RESULT_CODE.DECODE_ERR;
//			outImg = ImgUtil.drawAsInvalidImage(outImg);
		}

		result.image = outImg;

		return result;
	}



	private static BufferedImage createMapLayer(String lat, String lon,
			int zoom, int imgSize) {
			
		BufferedImage inMapImg = NetUtil.getStaticMapImage(zoom, lat, lon, imgSize);
		return inMapImg;
	}



	private static BufferedImage createQRCodeLayer(String qrCodeData,
			Map<EncodeHintType, ErrorCorrectionLevel> hintMap, int imgSize,
			int circleRadius, int qrImgTransparenz) throws WriterException {


		String data = qrCodeData;
		try {
			data = new String(qrCodeData.getBytes(charset), charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		BitMatrix matrix = new MultiFormatWriter().encode(data,
				BarcodeFormat.QR_CODE, imgSize, imgSize, hintMap);
//		writeToTestFile(matrix);

		BufferedImage origBufImg = MatrixToImageWriter.toBufferedImage(matrix);
		int[] codeRect = matrix.getEnclosingRectangle();

		BufferedImage scaledBufImg = ImgUtil.createQrCodeLayer(origBufImg,
				codeRect, imgSize, circleRadius, qrImgTransparenz);
		
//		writeToTestFile(scaledBufImg);

		return scaledBufImg;
	}

	private static boolean checkLegibility(BufferedImage scaledBufImg) {
		boolean readable = false;
		try {
			//add quiet zone for better reading
			int borderWidth = 50;//px
			BufferedImage imgWithQuiet = ImgUtil.addWhiteBorder(scaledBufImg, borderWidth);

//			writeToTestFile(imgWithQuiet);

			BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
															new BufferedImageLuminanceSource(imgWithQuiet)));
			Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap, null);
			String txt = qrCodeResult.getText();
			System.out.println("decoded qr text: "+txt);
			readable = true;
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		
		
		return readable;
	}

//	private static void writeToTestFile(BitMatrix matrix) {
//		File file = new File(testFilePath);
//		try {
//			MatrixToImageWriter.writeToFile(matrix, "png", file);
//		} catch (IOException e) {
//			System.out.println("Cannot wreite " + testFilePath);
//			e.printStackTrace();
//		}
//	}

//	private static void writeToTestFile(BufferedImage image) {
//        try {
//			ImageIO.write(image, "png", new File(testFilePath2));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//	}
//

}
