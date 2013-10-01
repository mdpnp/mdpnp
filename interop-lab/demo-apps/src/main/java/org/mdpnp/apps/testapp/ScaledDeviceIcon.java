package org.mdpnp.apps.testapp;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;

@SuppressWarnings("serial")
public class ScaledDeviceIcon extends DeviceIcon {
    private final DeviceIcon source;
    private Image lastImage;
    private final double scale;

    protected static final Image scaleImage(DeviceIcon source, double scale) {
        return source.getImage().getScaledInstance( (int)(source.getIconWidth() * scale), (int)(source.getIconHeight() * scale), Image.SCALE_SMOOTH);
    }

    public DeviceIcon getSource() {
        return source;
    }

    public ScaledDeviceIcon(DeviceIcon source, double scale) {
        super();
        this.source = source;
        this.scale = scale;
    }

    @Override
    protected boolean isConnected() {
        return source.isConnected();
    }

    @Override
    public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
        if(source.getImage() != lastImage) {
//            System.out.println("RESCALE SOURCE="+source+" GETIMAGE="+source.getImage() + " lastImage="+lastImage);
            lastImage = source.getImage();
            setImage(scaleImage(source, scale));
        }
        super.paintIcon(c, g, x, y);
    }
}
