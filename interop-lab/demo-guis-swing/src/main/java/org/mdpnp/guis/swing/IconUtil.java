package org.mdpnp.guis.swing;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class IconUtil {
    public static Image image(ice.Image image) {
      if(image.raster.userData.size()==0 || image.width == 0 || image.height == 0) {
          return null;
      }
      BufferedImage bi = new BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_ARGB);
      byte[] raster = new byte[image.raster.userData.size()];
      raster = image.raster.userData.toArrayByte(raster);
      IntBuffer ib = ByteBuffer.wrap(raster).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
      for(int y = 0; y < image.height; y++) {
          for(int x = 0; x < image.width; x++) {
              bi.setRGB(x, y, ib.get());
          }
      }
      return bi;
    }
}
