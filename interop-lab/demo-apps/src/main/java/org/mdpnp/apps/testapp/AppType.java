package org.mdpnp.apps.testapp;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.mdpnp.apps.testapp.rrr.RapidRespiratoryRate;

public enum AppType {
    Main("main", "Main Menu", null, false),
    Device("device", "Device Info", null, false),
    PCA("pca", "Infusion Safety", "NOPCA", true, "infusion-safety.png"),
    PCAViz("pcaviz", "Data Visualization", null, true, "data-viz.png"),
    XRay("xray", "X-Ray Ventilator Sync", "NOXRAYVENT", true, "xray-vent.png"),
    RRR("rrr", "Rapid Respiratory Rate", "NORRR", true, RapidRespiratoryRate.class.getResource("rrr.png"))
    ;
    
    private final String id;
    private final String name;
    private final String disableProperty;
    private final boolean listed;
    private final Icon icon;
    
    private AppType(final String id, final String name, final String disableProperty, final boolean listed, final Icon icon) {
        this.id = id;
        this.name = name;
        this.disableProperty = disableProperty;
        this.listed = listed;
        this.icon = icon;
    }
    
    private AppType(final String id, final String name, final String disableProperty, final boolean listed, final URL icon) {
        this(id, name, disableProperty, listed, new ImageIcon(icon));
    }
    
    private AppType(final String id, final String name, final String disableProperty, final boolean listed, final String icon) {
        this(id, name, disableProperty, listed, new ImageIcon(DemoApp.class.getResource(icon)));
    }
    private AppType(final String id, final String name, final String disableProperty, final boolean listed) {
        this(id, name, disableProperty, listed, (Icon) null);
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
        if(null != s && "true".equals(s)) {
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
        for(AppType at : values()) {
            if(!at.isDisabled() && at.isListed()) {
                ats.add(at);
            }
        }
        return ats.toArray(new AppType[0]);   
    }
    
    public static String[] getListedNames() {
        List<String> names = new ArrayList<String>();
        for(AppType at : getListedTypes()) {
            names.add(at.name);
        }
        return names.toArray(new String[0]);
    }
    public static AppType fromName(String name) {
        if(name == null) {
            return null;
        }
        for(AppType at : values()) {
            if(name.equals(at.getName())) {
                return at;
            }
        }
        return null;
    }
}