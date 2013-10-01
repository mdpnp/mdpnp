package org.mdpnp.devices.connected;

import javax.swing.JFrame;

import org.mdpnp.devices.EventLoop;

public class GetConnectedToFixedAddress extends GetConnected {
    private final String address;
    
    public GetConnectedToFixedAddress(JFrame frame, int domainId, String unique_device_identifier, String address, EventLoop eventLoop) {
        super(frame, domainId, unique_device_identifier, eventLoop);
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
