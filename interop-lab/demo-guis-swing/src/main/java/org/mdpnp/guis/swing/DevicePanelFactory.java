/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.guis.swing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DevicePanelFactory {
	private DevicePanelFactory() {}
	
	public static final Class[] PANELS = new Class[] {
	    PulseOximeterPanel.class,
	    BloodPressurePanel.class,
	    ElectroCardioGramPanel.class
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
	

	public static void resolvePanels(Set<Integer> tags, Collection<DevicePanel> panels) {
	    Map<Class, DevicePanel> byClass = new HashMap<Class, DevicePanel>();
	    Collection<DevicePanel> newPanels = new ArrayList<DevicePanel>();
	    
	    for(DevicePanel p : panels) {
	        byClass.put(p.getClass(), p);
	    }
	    
	    for(int i = 0; i < PANELS.length; i++) {
	        try {
                if((Boolean)PANEL_SUPPORTED[i].invoke(null, tags)) {
                    if(byClass.containsKey(PANELS[i])) {
                        newPanels.add(byClass.get(PANELS[i]));
                        byClass.remove(PANELS[i]);
                    } else {
                        newPanels.add((DevicePanel)PANELS[i].getConstructor().newInstance());
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
	    }
	    
	    panels.clear();
	    panels.addAll(newPanels);
	}
}
