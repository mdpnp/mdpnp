package org.mdpnp.guis.swing;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IconUtil {
    public static BufferedImage image(ice.Image image) throws IOException {
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
    private static final Logger log = LoggerFactory.getLogger(IconUtil.class);
}
