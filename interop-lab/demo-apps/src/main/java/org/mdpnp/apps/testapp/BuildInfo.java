package org.mdpnp.apps.testapp;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class BuildInfo {
    
    private static final String version;
    private static final String date;
    private static final String time;
    private static final String build;
    
    static {
        String _version = null;
        String _date = null, _time = null, _build = null;
        
        Enumeration<URL> resources;
        try {
            resources = BuildInfo.class.getClassLoader()
                    .getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                try {
                  Manifest manifest = new Manifest(resources.nextElement().openStream());
                  manifest.write(System.out);
                  // check that this is your manifest and do what you need or get the next one
                  Attributes mainAttrs = manifest.getMainAttributes();
                  if("demo-apps".equals(mainAttrs.getValue("Implementation-Title"))) {
                      _version = mainAttrs.getValue("Implementation-Version");
                      _date = mainAttrs.getValue("Build-Date");
                      _time = mainAttrs.getValue("Build-Time");
                      _build = mainAttrs.getValue("Build-Number");
                      break;
                  }
                  
                } catch (IOException E) {
                  // handle
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
          version = _version;
          build = _build;
          date = _date;
          time = _time;
    }
    
    
    public static final String getVersion() {
        return version;
    }
    
    public static final String getDate() {
        return date;
    }
    
    public static final String getBuild() {
        return build;
    }
    public static final String getTime() {
        return time;
    }
}
