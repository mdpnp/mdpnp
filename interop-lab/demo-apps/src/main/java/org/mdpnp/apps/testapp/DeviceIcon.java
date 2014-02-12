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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encodes raw image raster data from ice.Image as an ImageIcon
 * suitable for use in Swing.  Also reflects connectivity state
 * when applicable
 * @author jplourde
 *
 */
@SuppressWarnings("serial")
public class DeviceIcon extends ImageIcon {
    private static final Logger log = LoggerFactory.getLogger(DeviceIcon.class);

    public static final BufferedImage WHITE_SQUARE = new BufferedImage(96, 96, BufferedImage.TYPE_4BYTE_ABGR);
    public static final ImageIcon WHITE_SQUARE_ICON = new ImageIcon(WHITE_SQUARE);
    static {
        for(int y = 0; y < WHITE_SQUARE.getHeight(); y++) {
            for(int x = 0; x < WHITE_SQUARE.getWidth(); x++) {
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

   private static final BufferedImage readOrRaster(ice.Image image) throws IOException {
       BufferedImage bi;
       Exception e_read = null, e_raster = null;
       try {
           bi = ImageIO.read(new ByteArrayInputStream(image.raster.userData.toArrayByte(new byte[image.raster.userData.size()])));
           if(bi.getWidth()>0&&bi.getHeight()>0) {
               return bi;
           }
       } catch(Exception e) {
           e_read = e;
       }
       // The following is for backwards compatibility
       try {
           bi = new BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_ARGB);
           IntBuffer ib = ByteBuffer.wrap(image.raster.userData.toArrayByte(new byte[image.raster.userData.size()])).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
           for(int y = 0; y < image.height; y++) {
               for(int x = 0; x < image.width; x++) {
                   bi.setRGB(x, y, ib.get());
               }
           }
           return bi;
       } catch(Exception e) {
           e_raster = e;
       }
       log.error("Previous non-fatal Exception loading icon as PNG", e_read);
       throw new IOException(e_raster);
   }
   
   public void setImage(ice.Image image, double scale) {
        if(!image.raster.userData.isEmpty()) {
            BufferedImage bi;
            try {
                bi = readOrRaster(image);
                BufferedImage after = new BufferedImage((int)(scale * bi.getWidth()), (int)(scale * bi.getHeight()), BufferedImage.TYPE_INT_ARGB);
                java.awt.geom.AffineTransform at = new java.awt.geom.AffineTransform();
                at.scale(scale, scale);

                AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                after = scaleOp.filter(bi, after);
                setImage(after);
                log.debug("New image is width="+bi.getWidth()+", height="+bi.getHeight());
                return;
            } catch (IOException e) {
                log.error("error loading icon image", e);
            }
        }

        log.warn("width="+image.width+", height="+image.height+(image.raster.userData==null?", raster is null":"")+", using WHITE_SQUARE");
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
        if(!isConnected()) {
            int height = getIconHeight();
            int width = getIconWidth();
            g.setColor(Color.red);
            if(g instanceof Graphics2D) {
                ((Graphics2D)g).setStroke(new BasicStroke(3.0f));
                ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
            }
            g.drawOval(x, y, width, height);

            g.drawLine((int)(x + unitX1 * width / 2 + width / 2), (int)(y + unitY1 * height / 2 + height / 2), (int)(x + unitX2 * width / 2 + width / 2) , (int)(y + unitY2 * height / 2 + height / 2));
        }
    }

    private boolean connected = false;

    public void setConnected(boolean connected) {
        if(connected ^ this.connected) {
            this.connected = connected;
        }
    }

    protected boolean isConnected() {
        return this.connected;
    }

}
