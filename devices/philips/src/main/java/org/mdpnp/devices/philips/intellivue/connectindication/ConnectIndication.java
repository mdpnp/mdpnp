package org.mdpnp.devices.philips.intellivue.connectindication;

import org.mdpnp.devices.philips.intellivue.Message;
import org.mdpnp.devices.philips.intellivue.data.IPAddressInformation;
import org.mdpnp.devices.philips.intellivue.data.ProtocolSupport;
import org.mdpnp.devices.philips.intellivue.data.SystemLocalization;
import org.mdpnp.devices.philips.intellivue.data.Type;
import org.mdpnp.devices.philips.intellivue.dataexport.Nomenclature;

public interface ConnectIndication extends Message {
	public Nomenclature getNomenclature();
	
	public IPAddressInformation getIpAddressInformation();

	public SystemLocalization getSystemLocalization();
	
	public Type getSystemType();

	public ProtocolSupport getProtocolSupport();

}
