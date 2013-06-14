/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.guis.swing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.nonin.pulseox.DemoPulseOx;
import org.mdpnp.devices.simulation.pulseox.SimPulseOximeter;

import com.rti.dds.subscription.Subscriber;

public class DevicePanelFactory {
	private DevicePanelFactory() {}
	
//	private static void _findPanel(Class<? extends Device> clazz, Collection<DevicePanel> panels, Set<Class<?>> handled) {
//		if(null != clazz) {
//			try {
//				if(Device.class.isAssignableFrom(clazz)) {
//					// we have a device!
//					// Do we have a gui panel?
//					String pkg = DevicePanel.class.getPackage().getName();
//					Class<?> guiCls;
//					guiCls = Class.forName(pkg+"."+clazz.getSimpleName() + "Panel");
//					if(DevicePanel.class.isAssignableFrom(guiCls) && !handled.contains(guiCls)) {
//						Constructor<?> ctor = guiCls.getConstructor(new Class<?>[0]);
//						panels.add((DevicePanel) ctor.newInstance());
//						handled.add(guiCls);
//					}
//				}
//
//			} catch (ClassNotFoundException e) {
//	//			e.printStackTrace();
//			} catch (NoSuchMethodException e) {
//	//			e.printStackTrace();
//			} catch (SecurityException e) {
//	//			e.printStackTrace();
//			} catch (InstantiationException e) {
//	//			e.printStackTrace();
//			} catch (IllegalAccessException e) {
//	//			e.printStackTrace();
//			} catch (IllegalArgumentException e) {
//	//			e.printStackTrace();
//			} catch (InvocationTargetException e) {
//	//			e.printStackTrace();
//			} finally {
//				
//			}
//			_findPanel((Class<? extends Device>) clazz.getSuperclass(), panels, handled);
//			for(Class<?> cls : clazz.getInterfaces()) {
//				_findPanel((Class<? extends Device>) cls, panels, handled);
//			}
//		}
//	}
	
	public static Collection<DevicePanel> findPanel(AbstractDevice ad) {
	    return findPanel(ad, ad.getSubscriber());
	}
	public static Collection<DevicePanel> findPanel(AbstractDevice ad, Subscriber subscriber) {
	    List<DevicePanel> panels = new ArrayList<DevicePanel>();
	    if(ad instanceof SimPulseOximeter) {
	        panels.add(new PulseOximeterPanel(subscriber, ad.getDeviceIdentity().universal_device_identifier));
	    }
	    if(ad instanceof DemoPulseOx) {
	        panels.add(new PulseOximeterPanel(subscriber, ad.getDeviceIdentity().universal_device_identifier));
	    }
	    
	    return panels;
	}
	
	
//	public static Collection<DevicePanel> findPanel(IdentifierArrayUpdate iau, Gateway gateway, String source) {
//		Collection<DevicePanel> panels = new ArrayList<DevicePanel>();
//		Set<Identifier> identifiers = new HashSet<Identifier>();
//		identifiers.addAll(Arrays.asList(iau.getValue()));
//		
//		if(ElectroCardioGramPanel.supported(identifiers)) {
//			panels.add(new ElectroCardioGramPanel(gateway, source));
//		}
//		if(PulseOximeterPanel.supported(identifiers)) {
////			panels.add(new PulseOximeterPanel(gateway, source));
//		}
//		if(BloodPressurePanel.supported(identifiers)) {
//			panels.add(new BloodPressurePanel(gateway, source));
//		}
//		if(VentilatorPanel.supported(identifiers)) {
//			panels.add(new VentilatorPanel(gateway, source));
//		}
//		if(WebcamPanel.supported(identifiers)) {
//			panels.add(new WebcamPanel(gateway, source));
//		}
//		return panels;
//	}
	
//	public static Collection<DevicePanel> findPanel(Class<? extends Device> clazz) {
//		Collection<DevicePanel> panels = new ArrayList<DevicePanel>();
//		if(null == clazz) {
//			return panels;
//		}
//		_findPanel(clazz, panels, new HashSet<Class<?>>());
//		return panels;
//	}
}
