package org.mdpnp.transport;

import org.mdpnp.comms.Gateway;
import org.mdpnp.transport.dds.rti.DDSWrapper;
import org.mdpnp.transport.jgroups.JGroupsWrapper;
import org.mdpnp.transport.mcast.MulticastWrapper;

public class WrapperFactory {
    private WrapperFactory() {
        
    }
    
    public enum WrapperType {
        RTI_DDS,
        JGROUPS,
        MULTICAST
    }
    
    private static WrapperType type = WrapperType.RTI_DDS;
    
    static {
        String s = System.getProperty("org.mdpnp.transport.WrapperFactory.type");
        if(s != null) {
            type = WrapperType.valueOf(s);
        }
    }
    
    
    public static WrapperType getType() {
        return type;
    }
    public static void setType(WrapperType type) {
        WrapperFactory.type = type;
    }
    
    public static final Wrapper createWrapper(Gateway gateway, Wrapper.Role role) {
        try {
            switch(type) {
            case RTI_DDS:
                return new DDSWrapper(0, role, gateway);
            case JGROUPS:
                return new JGroupsWrapper(role, gateway);
            case MULTICAST:
                return new MulticastWrapper(null, gateway);
            default:
                throw new IllegalArgumentException("Wrapper.Role " + role + " not recognized");
            }
            
//            
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
