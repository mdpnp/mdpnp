/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.guis.swing;

import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.Method;
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
	    VentilatorPanel.class
	};
	
	public static final Method[] PANEL_SUPPORTED = new Method[PANELS.length];
	static {
	    for(int i = 0; i < PANELS.length; i++) {
	        try {
                PANEL_SUPPORTED[i] = PANELS[i].getDeclaredMethod("supported", Set.class);
            } catch (Exception e) {
                throw new ExceptionInInitializerError(e);
            }
	    }
	}
	

	private final static Logger log = LoggerFactory.getLogger(DevicePanelFactory.class);
	
	
	public static void resolvePanels(Set<Integer> tags, Container container) {
        log.trace("resolvePanels tags="+tags+" container="+container);
        Map<Class<?>, Component> byClass = new HashMap<Class<?>, Component>();
        
        for(Component c : container.getComponents()) {
            byClass.put(c.getClass(), c);
        }
        
        for(int i = 0; i < PANELS.length; i++) {
            try {
                if((Boolean)PANEL_SUPPORTED[i].invoke(null, tags)) {
                    if(byClass.containsKey(PANELS[i])) {
                        // Reuse a panel
                        byClass.remove(PANELS[i]);
                    } else {
                        // New panel
                        container.add((DevicePanel)PANELS[i].getConstructor().newInstance());
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        for(Component c : byClass.values()) {
            if(c instanceof DevicePanel) {
                ((DevicePanel)c).destroy();
            }
            container.remove(c);
        }
        byClass.clear();
	}
}
