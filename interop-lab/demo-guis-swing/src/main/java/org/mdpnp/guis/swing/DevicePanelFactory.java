/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.guis.swing;

import java.awt.Component;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevicePanelFactory {
    private DevicePanelFactory() {}

    public static final Class<?>[] PANELS = new Class[] {
        PulseOximeterPanel.class,
        BloodPressurePanel.class,
        ElectroCardioGramPanel.class,
        VentilatorPanel.class,
        InfusionPumpPanel.class,
        InvasiveBloodPressurePanel.class
//        MultiPulseOximeterPanel.class,
    };
    private final static Logger log = LoggerFactory.getLogger(DevicePanelFactory.class);
    public static final Method[] PANEL_SUPPORTED = new Method[PANELS.length];
    static {
        for(int i = 0; i < PANELS.length; i++) {
            try {
                PANEL_SUPPORTED[i] = PANELS[i].getDeclaredMethod("supported", Set.class);
            } catch (Exception e) {
                log.error("Exception checking supported method", e);
//                throw new ExceptionInInitializerError(e);
            }
        }
    }





    public static <T extends Component> void resolvePanels(Set<String> tags, Collection<T> container, Set<String> pumps) {
        log.trace("resolvePanels tags="+tags+" container="+container);
        Map<Class<?>, T> byClass = new HashMap<Class<?>, T>();

        for(T c : container) {
            byClass.put(c.getClass(), c);
        }

        container.clear();
        try {
            if(!pumps.isEmpty()) {
                if(byClass.containsKey(InfusionPumpPanel.class)) {
                    container.add(byClass.remove(InfusionPumpPanel.class));
                } else {
                    container.add((T)InfusionPumpPanel.class.getConstructor().newInstance());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for(int i = 0; i < PANELS.length; i++) {
            try {
                if((Boolean)PANEL_SUPPORTED[i].invoke(null, tags)) {
                    if(byClass.containsKey(PANELS[i])) {
                        // Reuse a panel
                        container.add(byClass.remove(PANELS[i]));
                        log.trace("Reused a " + PANELS[i]);
                    } else {
                        // New panel
                        container.add((T)PANELS[i].getConstructor().newInstance());
                        log.trace("Created a " + PANELS[i]);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        for(Component c : byClass.values()) {
            if(c instanceof DevicePanel) {

                ((DevicePanel)c).destroy();
                log.trace("Destroyed a " + c.getClass());
            } else {
                log.warn("Not a DevicePanel:"+c.getClass());
            }
        }
        byClass.clear();
    }
}
