package utils;

import java.awt.Color;

/**
 *
 * @author zeroos
 */
public class Utils{
	public static String getHTMLColor(Color c){
		///////everything toHexString?
		return "#" +
			Integer.toHexString(c.getRed()+0x100).substring(1) + 
			Integer.toHexString(c.getGreen()+0x100).substring(1) + 
			Integer.toHexString(c.getBlue()+0x100).substring(1);
	}
}
