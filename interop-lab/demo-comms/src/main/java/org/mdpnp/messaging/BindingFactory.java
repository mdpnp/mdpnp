package org.mdpnp.messaging;

import org.mdpnp.comms.Gateway;
import org.mdpnp.messaging.dds.rti.DDSBinding;
import org.mdpnp.messaging.jgroups.JGroupsBinding;


public class BindingFactory {
    private BindingFactory() {
        
    }
    
    public enum BindingType {
        RTI_DDS("Domain Id:"),
        JGROUPS("Multicast Addr:");
        
        private final String settingsDescription;
        
        private BindingType(String settingsDescription) {
            this.settingsDescription = settingsDescription;
        }
        public String getSettingsDescription() {
            return settingsDescription;
        }
    }
    
    private static BindingType type = BindingType.RTI_DDS;
    
    static {
        String s = System.getProperty("org.mdpnp.transport.WrapperFactory.type");
        if(s != null) {
            type = BindingType.valueOf(s);
        }
    }
    
    
    public static BindingType getType() {
        return type;
    }
    public static void setType(BindingType type) {
        BindingFactory.type = type;
    }
    
    public static final Binding createBinding(Gateway gateway, Binding.Role role) {
        return createBinding(BindingFactory.type, gateway, role, null);
    }
    
    public static final Binding createBinding(BindingType type, Gateway gateway, Binding.Role role, String settings) {
        try {
            switch(type) {
            case RTI_DDS:
                return new DDSBinding(null==settings?0:Integer.parseInt(settings), role, gateway);
            case JGROUPS:
                return new JGroupsBinding(role, gateway);
            default:
                throw new IllegalArgumentException("Binding.Role " + role + " not recognized");
            }
            
//            
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
