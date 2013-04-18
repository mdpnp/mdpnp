/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.apps.testapp;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.TooManyListenersException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.mdpnp.comms.Gateway;
import org.mdpnp.comms.GatewayListener;
import org.mdpnp.comms.IdentifiableUpdate;
import org.mdpnp.comms.data.text.MutableTextUpdate;
import org.mdpnp.comms.data.text.MutableTextUpdateImpl;
import org.mdpnp.comms.data.text.TextUpdate;
import org.mdpnp.comms.nomenclature.Demographics;
import org.mdpnp.comms.nomenclature.Device;
import org.mdpnp.devices.cpc.bernoulli.DemoBernoulli;
import org.mdpnp.devices.draeger.medibus.DemoApollo;
import org.mdpnp.devices.draeger.medibus.DemoEvitaXL;
import org.mdpnp.devices.masimo.radical.DemoRadical7;
import org.mdpnp.devices.nellcor.pulseox.DemoN595;
import org.mdpnp.devices.nonin.pulseox.DemoPulseOx;
import org.mdpnp.devices.oridion.capnostream.DemoCapnostream20;
import org.mdpnp.devices.philips.intellivue.DemoMP70;
import org.mdpnp.devices.simulation.SimulatedBloodPressureImpl;
import org.mdpnp.devices.simulation.pulseox.SimPulseOximeter;
import org.mdpnp.devices.webcam.WebcamImpl;
import org.mdpnp.transport.GetConnected;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceApp {

	private static final DeviceAppFrame buildGUI(Gateway gateway) {
		DeviceAppFrame frame = new DeviceAppFrame("Device App", gateway);
		frame.setSize(640,480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return frame;
		
	}

	public enum Type {
		PhilipsMP70,
		Nonin,
		NellcorN595,
		MasimoRadical7,
		DragerApollo,
		DragerEvitaXL,
		Bernoulli,
		PO_Simulator,
		NBP_Simulator,
		Webcam,
		Capnostream20
	}
//	private static Device device;
	private static final boolean buildDevice(JFrame frame, Gateway gateway) throws NoSuchFieldException, SecurityException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		Type type = null;
		if(null == (type = (Type) JOptionPane.showInputDialog(frame, "Choose a device", "Device", JOptionPane.QUESTION_MESSAGE, null, Type.values(), null))) {
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			return false;
		}
//		Device device = null;
		switch(type) {
		case Nonin:
			new DemoPulseOx(gateway);
			break;
		case NellcorN595:
			new DemoN595(gateway);
			break;
		case MasimoRadical7:
			new DemoRadical7(gateway);
			break;
		case PO_Simulator:
			new SimPulseOximeter(gateway);
			break;
		case PhilipsMP70:
//			Class.forName("org.mdpnp.devices.impl.intellivue.PhilipsIntellivueMP70Impl").getConstructor(new Class<?>[] {Gateway.class}).newInstance(gateway);
			
			new DemoMP70(gateway);
			break;
		case DragerApollo:
			new DemoApollo(gateway);
			break;
		case DragerEvitaXL: 
			new DemoEvitaXL(gateway);
			break;
		case Bernoulli:
			new DemoBernoulli(gateway);
			break;
		case NBP_Simulator:
			new SimulatedBloodPressureImpl(gateway);
			break;
		case Webcam:
			new WebcamImpl(gateway);
			break;
		case Capnostream20:
			new DemoCapnostream20(gateway);
			break;
		default:
			throw new RuntimeException("Unknown type:"+type);
		}

		return true;
	}
	private static final Logger log = LoggerFactory.getLogger(DeviceApp.class);
	public static void main(String[] args) throws IOException, TooManyListenersException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, InstantiationException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException {
		System.setProperty("java.net.preferIPv4Stack","true"); 
		Gateway gateway = new Gateway();
		final DeviceAppFrame frame = buildGUI(gateway);
		frame.setVisible(true);
		if(!buildDevice(frame, gateway)) {
			return;
		}
		MutableTextUpdate tu = new MutableTextUpdateImpl(Device.REQUEST_AVAILABLE_IDENTIFIERS);
		tu.setValue("");
		gateway.update(tu);
		gateway.addListener(new GatewayListener() {

			@Override
			public void update(IdentifiableUpdate<?> update) {
				if(Device.NAME.equals(update.getIdentifier())) {
					frame.setTitle( ((TextUpdate)update).getValue() );
				} else if(Demographics.FIRST_NAME.equals(update.getIdentifier())) {
					log.info("FIRST NAME:"+((TextUpdate)update).getValue());					
				} else if(Demographics.LAST_NAME.equals(update.getIdentifier())) {
				    log.info("LAST NAME:"+((TextUpdate)update).getValue());
				} else if(Demographics.PATIENT_ID.equals(update.getIdentifier())) {
				    log.info("PATIENT_ID:"+((TextUpdate)update).getValue());
				} else {
//					log.info(update);
				}
			}
			
		});
		final GetConnected getConnected = new GetConnected(frame, gateway);
		getConnected.connect();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				getConnected.disconnect();
			}
		});
//		frame.setModel(device);
//		frame.setModel(device);
	}

}
