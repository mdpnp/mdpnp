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
package org.mdpnp.devices.serial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Plourde
 *
 */
public class SerialProviderFactory {

    private static SerialProvider defaultProvider;

    private static final String[] DEFAULT_PROVIDERS = new String[] {
            "org.mdpnp.data.serial.PureJavaCommSerialProvider",
            "org.mdpnp.devices.serial.TCPSerialProvider"
    };
    private static final String SYSTEM_PROPERTY = "org.mdpnp.data.serial.SerialProviderFactory.defaultProvider";

    public static final void setDefaultProvider(SerialProvider serialProvider) {
        SerialProviderFactory.defaultProvider = serialProvider;
    }

    private static final Logger log = LoggerFactory.getLogger(SerialProviderFactory.class);

    private static final void addCandidates(List<String> candidates, InputStream in) {
        if (null != in) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String line = null;
                while (null != (line = br.readLine())) {
                    candidates.add(line);
                }
                br.close();
            } catch (IOException ioe) {
                log.error("error reading candidates for defaultSerialProvider", ioe);
            }
        }
    }

    public static SerialProvider getDefaultProvider() {
        if (null == defaultProvider) {
            defaultProvider = locateDefaultProvider();
        }
        return defaultProvider;
    }

    static SerialProvider locateDefaultProvider() {

      List<String> candidates = new ArrayList<>();

      String sysProp = System.getProperty(SYSTEM_PROPERTY);

      if (sysProp != null) {
        candidates.add(sysProp);
      }

      addCandidates(candidates, SerialProviderFactory.class.getResourceAsStream("serial-providers"));
      addCandidates(candidates, SerialProviderFactory.class.getClassLoader().getResourceAsStream("serial-providers"));

      candidates.addAll(Arrays.asList(DEFAULT_PROVIDERS));

      return locateDefaultProvider(candidates);
    }

  static SerialProvider locateDefaultProvider(List<String> orig) {

        SerialProvider sp=null;
        ArrayList<String> candidates = new ArrayList<>(orig);
        while (null == sp) {
            if (candidates.isEmpty()) {
                throw new IllegalStateException("No valid defaultProvider available");
            }
            String candidate = null;
            try {
                candidate = candidates.remove(0);
                log.warn("Attempt to load " + candidate);
                Class clazz = Class.forName(candidate);
                Constructor<?> constructor = clazz.getConstructor(new Class<?>[0]);
                sp = (SerialProvider) constructor.newInstance(new Object[0]);
            } catch (Exception e) {
                if (candidates.isEmpty()) {
                    throw new RuntimeException(e);
                } else {
                    log.warn("cannot load candidate SerialProvider " + candidate + "; trying another", e);
                }
            }
        }

        log.warn("Setting defaultProvider to " + sp==null?"null":sp.getClass().getName());
        return sp;
    }
}
