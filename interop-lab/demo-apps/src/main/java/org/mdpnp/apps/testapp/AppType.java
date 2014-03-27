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

import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.mdpnp.apps.testapp.rrr.RapidRespiratoryRate;

/**
 * @author Jeff Plourde
 *
 */
public enum AppType {
    Main("main", "Main Menu", null, false), Device("device", "Device Info", null, false), PCA("pca", "Infusion Safety", "NOPCA", true,
            "infusion-safety.png", 0.75), PCAViz("pcaviz", "Data Visualization", "NOPCAVIZ", true, "data-viz.png", 0.75), XRay("xray",
            "X-Ray Ventilator Sync", "NOXRAYVENT", true, "xray-vent.png", 0.75), RRR("rrr", "Rapid Respiratory Rate", "NORRR", true,
            RapidRespiratoryRate.class.getResource("rrr.png"), 0.75), SimControl("sim", "Simulation Control", "NOSIM", true, "sim.png", 0.75);

    private final String id;
    private final String name;
    private final String disableProperty;
    private final boolean listed;
    private final Icon icon;

    private static final BufferedImage read(URL url) {
        try {
            return ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static final BufferedImage read(String name) {
        return read(DemoApp.class.getResource(name));
    }

    private static final BufferedImage scale(BufferedImage before, double scale) {
        if (null == before) {
            return null;
        }
        if (0 == Double.compare(scale, 0.0)) {
            return before;
        }
        int width = before.getWidth();
        int height = before.getHeight();

        BufferedImage after = new BufferedImage((int) (scale * width), (int) (scale * height), BufferedImage.TYPE_INT_ARGB);
        java.awt.geom.AffineTransform at = new java.awt.geom.AffineTransform();
        at.scale(scale, scale);

        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        after = scaleOp.filter(before, after);
        return after;
    }

    private AppType(final String id, final String name, final String disableProperty, final boolean listed, final BufferedImage icon, double scale) {
        this.id = id;
        this.name = name;
        this.disableProperty = disableProperty;
        this.listed = listed;
        this.icon = null == icon ? null : new ImageIcon(scale(icon, scale));
    }

    private AppType(final String id, final String name, final String disableProperty, final boolean listed, final URL icon, double scale) {
        this(id, name, disableProperty, listed, read(icon), scale);
    }

    private AppType(final String id, final String name, final String disableProperty, final boolean listed, final String icon, double scale) {
        this(id, name, disableProperty, listed, read((icon)), scale);
    }

    private AppType(final String id, final String name, final String disableProperty, final boolean listed) {
        this(id, name, disableProperty, listed, (BufferedImage) null, 1.0);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isListed() {
        return listed;
    }

    public Icon getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return name;
    }

    private static final boolean isTrue(String property) {
        String s = System.getProperty(property);
        if (null != s && "true".equals(s)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isDisabled() {
        return null != disableProperty && isTrue(disableProperty);
    }

    public static AppType[] getListedTypes() {
        List<AppType> ats = new ArrayList<AppType>();
        for (AppType at : values()) {
            if (!at.isDisabled() && at.isListed()) {
                ats.add(at);
            }
        }
        return ats.toArray(new AppType[0]);
    }

    public static String[] getListedNames() {
        List<String> names = new ArrayList<String>();
        for (AppType at : getListedTypes()) {
            names.add(at.name);
        }
        return names.toArray(new String[0]);
    }

    public static AppType fromName(String name) {
        if (name == null) {
            return null;
        }
        for (AppType at : values()) {
            if (name.equals(at.getName())) {
                return at;
            }
        }
        return null;
    }
}
