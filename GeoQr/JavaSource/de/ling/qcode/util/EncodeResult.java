package de.ling.qcode.util;

import java.awt.image.BufferedImage;

public class EncodeResult {


	public enum RESULT_CODE {CREATED, DECODE_ERR}; 

	
	public RESULT_CODE resultCode = null;
	public BufferedImage image = null;
}
