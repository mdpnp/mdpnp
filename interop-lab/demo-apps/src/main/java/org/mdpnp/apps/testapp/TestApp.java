package org.mdpnp.apps.testapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.mdpnp.comms.Gateway;
import org.mdpnp.comms.GatewayListener;
import org.mdpnp.comms.IdentifiableUpdate;
import org.mdpnp.comms.connected.AbstractGetConnected;
import org.mdpnp.devices.nonin.pulseox.DemoPulseOx;

public class TestApp {
	public static void main(final String[] args) throws IOException {
		if(args.length < 1) {
			System.err.println("Please specify serial port");
			return;
		}
		Gateway gateway = new Gateway();
		new DemoPulseOx(gateway);
		AbstractGetConnected getConnected = new AbstractGetConnected(gateway) {

			@Override
			protected void abortConnect() {
				System.exit(0);
			}

			@Override
			protected String addressFromUser() {
				return args[0];
			}

			@Override
			protected String addressFromUserList(String[] list) {
				return args[0];
			}

			@Override
			protected boolean isFixedAddress() {
				return true;
			}
			
		};
		
		gateway.addListener(new GatewayListener() {

			@Override
			public void update(IdentifiableUpdate<?> update) {
				System.out.println(update);
			}
			
		});
		
		getConnected.connect();
		
		System.err.println("Press <Enter> to quit");
		
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		br.readLine();
		
		getConnected.disconnect();
		
	}
}
