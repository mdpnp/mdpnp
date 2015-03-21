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
package org.mdpnp.guis.javafx;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javafx.scene.Node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Plourde
 *
 */
public class DevicePanelFactory {
    private DevicePanelFactory() {
    }

    public static final Class<?>[] PANELS = new Class[] { 
        PulseOximeterPanel.class,
        ElectroCardioGramPanel.class,
        BloodPressurePanel.class, 
         VentilatorPanel.class, 
        InfusionPumpPanel.class, 
        InvasiveBloodPressurePanel.class,
//     MultiPulseOximeterPanel.class,
    };
    private final static Logger log = LoggerFactory.getLogger(DevicePanelFactory.class);
    public static final Method[] PANEL_SUPPORTED = new Method[PANELS.length];
    static {
        for (int i = 0; i < PANELS.length; i++) {
            try {
                PANEL_SUPPORTED[i] = PANELS[i].getDeclaredMethod("supported", Set.class);
            } catch (Exception e) {
                log.error("Exception checking supported method", e);
                // throw new ExceptionInInitializerError(e);
            }
        }
    }

    public static <T extends Node> void resolvePanels(Set<String> tags, Collection<T> container, Set<String> pumps) {
        log.trace("resolvePanels tags=" + tags + " container=" + container);
        Map<Class<?>, T> byClass = new HashMap<Class<?>, T>();

        for (T c : container) {
            byClass.put(c.getClass(), c);
        }

        container.clear();
        try {
            if (!pumps.isEmpty()) {
                if (byClass.containsKey(InfusionPumpPanel.class)) {
                    container.add(byClass.remove(InfusionPumpPanel.class));
                } else {
                    container.add((T) InfusionPumpPanel.class.getConstructor().newInstance());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < PANELS.length; i++) {
            try {
                if ((Boolean) PANEL_SUPPORTED[i].invoke(null, tags)) {
                    if (byClass.containsKey(PANELS[i])) {
                        // Reuse a panel
                        container.add(byClass.remove(PANELS[i]));
                        log.trace("Reused a " + PANELS[i]);
                    } else {
                        // New panel
                        container.add((T) PANELS[i].getConstructor().newInstance());
                        log.trace("Created a " + PANELS[i]);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        for (Node c : byClass.values()) {
            if (c instanceof DevicePanel) {

                ((DevicePanel) c).destroy();
                log.trace("Destroyed a " + c.getClass());
            } else {
                log.warn("Not a DevicePanel:" + c.getClass());
            }
        }
        byClass.clear();
    }
}
