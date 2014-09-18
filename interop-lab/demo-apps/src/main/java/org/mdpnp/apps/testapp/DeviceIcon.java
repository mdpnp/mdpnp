/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.apps.testapp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ImageIcon;

import org.mdpnp.guis.swing.IconUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encodes raw image raster data from ice.Image as an ImageIcon suitable for use
 * in Swing. Also reflects connectivity state when applicable
 * 
 * @author Jeff Plourde
 * 
 */
@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class DeviceIcon extends ImageIcon {
    private static final Logger log = LoggerFactory.getLogger(DeviceIcon.class);

    public static final BufferedImage WHITE_SQUARE = new BufferedImage(96, 96, BufferedImage.TYPE_4BYTE_ABGR);
    public static final ImageIcon WHITE_SQUARE_ICON = new ImageIcon(WHITE_SQUARE);
    static {
        for (int y = 0; y < WHITE_SQUARE.getHeight(); y++) {
            for (int x = 0; x < WHITE_SQUARE.getWidth(); x++) {
                WHITE_SQUARE.setRGB(x, y, 0xFFFFFFFF);
            }
        }
    }

    public DeviceIcon() {
        super(WHITE_SQUARE);
    }

    public boolean isBlank() {
        return WHITE_SQUARE.equals(getImage());
    }

    public void setImage(ice.Image image, double scale) {
        if (!image.image.userData.isEmpty()) {
            BufferedImage bi;
            try {
                bi = IconUtil.image(image);
                BufferedImage after = new BufferedImage((int) (scale * bi.getWidth()), (int) (scale * bi.getHeight()), BufferedImage.TYPE_INT_ARGB);
                java.awt.geom.AffineTransform at = new java.awt.geom.AffineTransform();
                at.scale(scale, scale);

                AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                after = scaleOp.filter(bi, after);
                setImage(after);
                log.debug("New image is width=" + bi.getWidth() + ", height=" + bi.getHeight());
                return;
            } catch (IOException e) {
                log.error("error loading icon image", e);
            }
        }

//        log.warn("width=" + image.width + ", height=" + image.height + (image.raster.userData == null ? ", raster is null" : "")
//                + ", using WHITE_SQUARE");
        setImage(WHITE_SQUARE);
    }

    public DeviceIcon(ice.Image image) {
        this(image, 0.75);
    }

    public DeviceIcon(ice.Image image, double scale) {
        super();
        setImage(image, scale);
    }

    public DeviceIcon(Image image) {
        super(image);
    }

    private static final double PI_OVER_4 = Math.PI / 4.0;
    private static final double FIVEPI_OVER_4 = 5.0 * Math.PI / 4.0;

    private static final double unitX1 = Math.cos(PI_OVER_4);
    private static final double unitY1 = Math.sin(PI_OVER_4);

    private static final double unitX2 = Math.cos(FIVEPI_OVER_4);
    private static final double unitY2 = Math.sin(FIVEPI_OVER_4);

    @Override
    public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
        super.paintIcon(c, g, x, y);
        if (!isConnected()) {
            int height = getIconHeight();
            int width = getIconWidth();
            g.setColor(Color.red);
            if (g instanceof Graphics2D) {
                ((Graphics2D) g).setStroke(new BasicStroke(3.0f));
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            }
            g.drawOval(x, y, width, height);

            g.drawLine((int) (x + unitX1 * width / 2 + width / 2), (int) (y + unitY1 * height / 2 + height / 2),
                    (int) (x + unitX2 * width / 2 + width / 2), (int) (y + unitY2 * height / 2 + height / 2));
        }
    }

    private boolean connected = false;

    public void setConnected(boolean connected) {
        if (connected ^ this.connected) {
            this.connected = connected;
        }
    }

    protected boolean isConnected() {
        return this.connected;
    }

}
