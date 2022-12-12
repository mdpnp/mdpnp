package org.mdpnp.apps.util;

import com.sun.javafx.tk.FontMetrics;

/**
 * A class that holds utility methods for FontMetrics that we found we needed for
 * migration to Java 17.
 * @author simon
 *
 */
public class FontMetricsWrapper {
	
	/**
	 * In Java 8, the FontMetrics class had a computeStringWidth method.  That was
	 * removed in Java 9, but getCharWidth is available in that, so we simply take
	 * the string, and sum up the width of each char.  Not sure why that was ever
	 * removed, as it seems quite a basic thing to need.
	 * 
	 * @param fm The FontMetrics class to be used to calculate the width
	 * @param str The string to calculate the width of
	 * @return the computed string width.
	 */
	public static float computeStringWidth(FontMetrics fm, String str) {
		float f=0;
		char[] chars=str.toCharArray();
		for(char achar : chars) {
			f+=fm.getCharWidth(achar);
		}
		return f;
	}

}
