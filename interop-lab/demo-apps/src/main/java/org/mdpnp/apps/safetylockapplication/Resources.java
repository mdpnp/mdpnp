package org.mdpnp.apps.safetylockapplication;


import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

public class Resources {
	
	public static enum Hand {
	LEFT,
	RIGHT 
	}
	
	public static enum OperatingMode {
	MANUAL,
	HEART_RATE_VS_PULSE_RATE,
	PLETHYSMOGRAPH,
	CAPNOGRAPH,
	UNSPECIFIED 
	}
	
	public static enum DisplayMode {
	QUANTITATIVE,
	QUALITATIVE 
	}
	
	public static enum Command {
		START, STOP
	}
	
	public static enum Algorithm {
		ALPHA, BETA, GAMMA, DELTA, EPSILON, UNSPECIFIED
	}
	
	public static enum AlarmOption {
		DEFAULT,
		CLEAR,
		NEW,
		OK,
		CANCEL,
		UNDEFINED
	}
	
	public static enum YesNoResponse {
		YES,
		NO
	}
	
	public static final Image loadImage(String path) {
	    try {
            return ImageIO.read(Resources.class.getResource("images/"+path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
	}
	
	public static final String pathToImages = "src/main/java/org/mdpnp/apps/safetylockapplication/images/";
	
	public static final Color physiologicalDisplayPanelBackgroundColor = new Color(41,34,37); 	//dirty gray
	public static final Color valueMissingTextColor = new Color(231,13,40); 					//soft red
	public static final Color valueAvailableTextColor = new Color(11,160,88);					//soft green
	public static final Color standardTextColor = new Color(72, 116, 143);						//soft blue
	public static Color valueMediocreTextColor = new Color(219, 145, 16);						//soft orange
}
