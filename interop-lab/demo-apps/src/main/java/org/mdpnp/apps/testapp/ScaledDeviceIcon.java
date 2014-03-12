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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class ScaledDeviceIcon extends DeviceIcon {
    private final DeviceIcon source;
    private Image lastImage;
    private final double scale;

    protected static final Image scaleImage(DeviceIcon source, double scale) {
        return source.getImage().getScaledInstance((int) (source.getIconWidth() * scale), (int) (source.getIconHeight() * scale), Image.SCALE_SMOOTH);
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
        if (source.getImage() != lastImage) {
            // System.out.println("RESCALE SOURCE="+source+" GETIMAGE="+source.getImage()
            // + " lastImage="+lastImage);
            lastImage = source.getImage();
            setImage(scaleImage(source, scale));
        }
        super.paintIcon(c, g, x, y);
    }
}
