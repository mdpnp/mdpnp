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

/**
 * @author Jeff Plourde
 *
 */
public class IconUtil {
    public static BufferedImage image(ice.Image image) throws IOException {
        if(image.width == 0 || image.height == 0 || image.raster.userData.isEmpty()) {
            return null;
        }
        
        BufferedImage bi;
        Exception e_read = null, e_raster = null;
        try {
            bi = ImageIO.read(new ByteArrayInputStream(image.raster.userData.toArrayByte(new byte[image.raster.userData.size()])));
            if (bi.getWidth() > 0 && bi.getHeight() > 0) {
                return bi;
            }
        } catch (Exception e) {
            e_read = e;
        }
        // The following is for backwards compatibility
        try {
            bi = new BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_ARGB);
            IntBuffer ib = ByteBuffer.wrap(image.raster.userData.toArrayByte(new byte[image.raster.userData.size()])).order(ByteOrder.BIG_ENDIAN)
                    .asIntBuffer();
            for (int y = 0; y < image.height; y++) {
                for (int x = 0; x < image.width; x++) {
                    bi.setRGB(x, y, ib.get());
                }
            }
            return bi;
        } catch (Exception e) {
            e_raster = e;
        }
        log.error("Previous non-fatal Exception loading icon as PNG", e_read);
        throw new IOException(e_raster);
    }

    private static final Logger log = LoggerFactory.getLogger(IconUtil.class);
}
