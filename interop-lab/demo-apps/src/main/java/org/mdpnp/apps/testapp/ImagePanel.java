package org.mdpnp.apps.testapp;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ImagePanel extends JPanel {

    private BufferedImage image;
    
    @Override
    public void paintComponent(Graphics g) {
    	if(image != null) {
    		g.drawImage(image, 0, 0, null);
    	}
    }
    
    public void setImage(URL url) {
        try {
            image = null == url ? null : ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
