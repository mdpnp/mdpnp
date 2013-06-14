package org.mdpnp.messaging;

import org.mdpnp.messaging.Binding.Role;

public class BindingFactory {
    private BindingFactory() {
        
    }
    
    public enum BindingType {
        RTI_DDS("Domain Id:", "org.mdpnp.messaging.dds.rti.DDSBinding"),
        JGROUPS("Multicast Addr:", "org.mdpnp.messaging.jgroups.JGroupsBinding"),
        NONE(null, "");
        
        private final String settingsDescription;
        private final Class<? extends Binding> bindingClass;
        
        private BindingType(String settingsDescription, String className) {
            this.settingsDescription = settingsDescription;
            Class<? extends Binding> bindingClass = null;
            try {
                bindingClass = (Class<? extends Binding>) Class.forName(className);
            } catch (ClassNotFoundException cnfe) {
                bindingClass = null;
            }
            this.bindingClass = bindingClass;
        }
        public String getSettingsDescription() {
            return settingsDescription;
        }
        public Class<? extends Binding> getBindingClass() {
            return bindingClass;
        }
        public boolean isAvailable() {
            return null != bindingClass;
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
            Class<? extends Binding> cls = type.getBindingClass();
            if(null != cls) {
                return cls.getConstructor(String.class, Role.class, Gateway.class).newInstance(settings, role, gateway);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
