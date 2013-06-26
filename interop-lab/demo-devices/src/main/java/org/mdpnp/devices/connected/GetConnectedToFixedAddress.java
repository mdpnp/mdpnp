package org.mdpnp.devices.connected;

import javax.swing.JFrame;

public class GetConnectedToFixedAddress extends GetConnected {
    private final String address;
    
    public GetConnectedToFixedAddress(JFrame frame, int domainId, String universal_device_identifier, String address) {
        super(frame, domainId, universal_device_identifier);
        this.address = address;
    }
    
    @Override
    protected boolean isFixedAddress() {
        return true;
    }
    
    @Override
    protected String addressFromUser() {
        return address;
    }
    
    @Override
    protected String addressFromUserList(String[] list) {
        return address;
    }

}
