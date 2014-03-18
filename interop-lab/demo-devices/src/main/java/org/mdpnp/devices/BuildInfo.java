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
package org.mdpnp.devices;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * @author Jeff Plourde
 *
 */
public class BuildInfo {

    private static final String version;
    private static final String date;
    private static final String time;
    private static final String build;
    private static final String descriptor;

    static {
        String _version = null;
        String _date = null, _time = null, _build = null, _descriptor = null;

        Enumeration<URL> resources;
        try {
            resources = BuildInfo.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                try {
                    Manifest manifest = new Manifest(resources.nextElement().openStream());
                    // manifest.write(System.out);
                    // check that this is your manifest and do what you need or
                    // get the next one
                    Attributes mainAttrs = manifest.getMainAttributes();
                    if ("demo-apps".equals(mainAttrs.getValue("Implementation-Title"))) {
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
        if (null == _version) {
            _descriptor = "Development Version on " + System.getProperty("java.version");
        } else {
            _descriptor = 
                    "v" + _version + " built:" + _date + " " + _time + " on " + System.getProperty("java.version");
        }
        version = _version;
        build = _build;
        date = _date;
        time = _time;
        descriptor = _descriptor;
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
    
    public static String getDescriptor() {
        return descriptor;
    }
}
