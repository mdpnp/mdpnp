/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.serial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerialProviderFactory {

    private static SerialProvider defaultProvider;
    private static final String[] DEFAULT_PROVIDERS = new String[] {"org.mdpnp.data.serial.PureJavaCommSerialProvider", "org.mdpnp.devices.serial.TCPSerialProvider"};
    private static final String SYSTEM_PROPERTY = "org.mdpnp.data.serial.SerialProviderFactory.defaultProvider";

    public static final void setDefaultProvider(SerialProvider serialProvider) {
            SerialProviderFactory.defaultProvider = serialProvider;
    }

    private static final Logger log = LoggerFactory.getLogger(SerialProviderFactory.class);

    private static final void addCandidates(List<String> candidates, InputStream in) {
        if(null != in) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String line = null;
                while(null != (line = br.readLine())) {
                    candidates.add(line);
                }
                br.close();
            } catch (IOException ioe) {
                log.error("error reading candidates for defaultSerialProvider", ioe);
            }
        }
    }

    public static final SerialProvider getDefaultProvider() {
        if(null == defaultProvider) {
            List<String> candidates = new ArrayList<String>();

            String sysProp = System.getProperty(SYSTEM_PROPERTY);

            if(sysProp != null) {
                candidates.add(sysProp);
            }

            addCandidates(candidates, SerialProviderFactory.class.getResourceAsStream("serial-providers"));
            addCandidates(candidates, SerialProviderFactory.class.getClassLoader().getResourceAsStream("serial-providers"));

            candidates.addAll(Arrays.asList(DEFAULT_PROVIDERS));

            while(null == defaultProvider) {
                if(candidates.isEmpty()) {
                    throw new IllegalStateException("No valid defaultProvider available");
                }
                String candidate = null;
                try {
                    candidate = candidates.remove(0);
                    defaultProvider = (SerialProvider) Class.forName(candidate).getConstructor(new Class<?>[0]).newInstance(new Object[0]);
                } catch (Exception e) {
                    if(candidates.isEmpty()) {
                        throw new RuntimeException(e);
                    } else {
                        log.warn("cannot load candidate SerialProvider " + candidate + "; trying another", e);
                    }
                }
            }
        }
        return defaultProvider;
    }
}
