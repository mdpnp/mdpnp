package org.mdpnp.apps.testapp;

import java.util.Arrays;

import org.mdpnp.comms.Gateway;
import org.mdpnp.comms.GatewayListener;
import org.mdpnp.comms.IdentifiableUpdate;
import org.mdpnp.comms.Identifier;
import org.mdpnp.comms.data.enumeration.EnumerationUpdate;
import org.mdpnp.comms.data.identifierarray.MutableIdentifierArrayUpdate;
import org.mdpnp.comms.data.identifierarray.MutableIdentifierArrayUpdateImpl;
import org.mdpnp.comms.data.text.TextUpdate;
import org.mdpnp.comms.nomenclature.Association;
import org.mdpnp.comms.nomenclature.ConnectedDevice;
import org.mdpnp.comms.nomenclature.Device;
import org.mdpnp.comms.serial.SerialProviderFactory;
import org.mdpnp.comms.serial.TCPSerialProvider;
import org.mdpnp.transport.Adapter;
import org.mdpnp.transport.GetConnected;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionedDeviceAdapter {

	protected static String address;
	protected static Gateway deviceGateway = new Gateway(), externalGateway = new Gateway();
	
	protected static GetConnected getConnected = new GetConnected(null, deviceGateway) {
		@Override
		protected void abortConnect() {
		}
		@Override
		protected String addressFromUser() {
			return address;
		}
		@Override
		protected String addressFromUserList(String[] list) {
			return address;
		}
		@Override
		protected boolean isFixedAddress() {
			return true;
		}
	};
	protected static Adapter adapter;
	
	protected static final Logger log = LoggerFactory.getLogger(VersionedDeviceAdapter.class);
	
	public static void main(final String[] args) throws Exception {
		
		
		log.trace("startProgram("+Arrays.toString(args)+")");
		if(args.length < 1) {
			System.err.println("Please specify DeviceAdapter type");
			return;
		}
		
		address = args.length > 1 ? args[1] : null;
		
		if(address.contains(":")) {
			SerialProviderFactory.setDefaultProvider(new TCPSerialProvider());
			log.info("Using the TCPSerialProvider, be sure you provided a host:port target");
		}
		
		final int domainId = 0;
		
		deviceGateway.addListener(new GatewayListener() {

			@Override
			public void update(IdentifiableUpdate<?> update) {
				if(Device.GUID.equals(update.getIdentifier())) {
					log.debug("GUID:"+((TextUpdate)update).getValue());
				} else if(Device.NAME.equals(update.getIdentifier())) {
					log.debug("NAME:"+((TextUpdate)update).getValue());
				} else if(ConnectedDevice.STATE.equals(update.getIdentifier())) {
					log.debug("(Connection) STATE:"+((EnumerationUpdate)update).getValue());
				} else if(ConnectedDevice.CONNECTION_INFO.equals(update.getIdentifier())) {
					log.debug("CONNECTION_INFO:"+((TextUpdate)update).getValue());
				} else if(Association.ANNOUNCE_ARRIVE.equals(update.getIdentifier())) {
					log.debug("Announcing arrival...");
				} else if(Association.ACKNOWLEDGE_ARRIVE.equals(update.getIdentifier())) {
					log.debug("Arrival acknowledged");
				} else if(Association.ANNOUNCE_DEPART.equals(update.getIdentifier())) {
					log.debug("Announcing departure...");
				} else if(Association.ACKNOWLEDGE_DEPART.equals(update.getIdentifier())) {
					log.debug("Departure acknowledged");
				}
			}
			
		});

		
		DeviceAdapter.buildDevice(DeviceAdapter.Type.valueOf(args[0]), deviceGateway);

		adapter = new Adapter(domainId, deviceGateway, externalGateway);
			     
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				log.trace("getConnected.disconnect");
				getConnected.disconnect();
				log.trace("adapter.depart");
				
				adapter.depart();
				log.trace("adapter.tearDown");
				adapter.tearDown();
			}
		}));
   
		getConnected.connect();
		MutableIdentifierArrayUpdate miau = new MutableIdentifierArrayUpdateImpl(Device.REQUEST_IDENTIFIED_UPDATES);
		miau.setValue(new Identifier[] {Device.NAME, Device.GUID});
		deviceGateway.update(miau);
		     
	}

}
