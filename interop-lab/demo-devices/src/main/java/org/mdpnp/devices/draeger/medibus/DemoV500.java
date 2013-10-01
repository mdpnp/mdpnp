package org.mdpnp.devices.draeger.medibus;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket;
import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;

public class DemoV500 extends AbstractDraegerVent {
    public DemoV500(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);
    }
    public DemoV500(int domainId, EventLoop eventLoop, SerialSocket socket) {
        super(domainId, eventLoop, socket);
    }
    
    @Override
    public SerialProvider getSerialProvider() {
        SerialProvider serialProvider =  super.getSerialProvider();
        serialProvider.setDefaultSerialSettings(19200, DataBits.Eight, Parity.None, StopBits.One);
        return serialProvider;
    }
    
    @Override
    protected String iconResourceName() {
        return "v500.png";
    }
}
