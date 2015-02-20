package org.mdpnp.devices;

import org.springframework.context.ApplicationContext;

/**
 *
 */
public interface DeviceDriverProvider {
    DeviceType     getDeviceType();
    AbstractDevice create(ApplicationContext context) throws Exception;


    public static class DeviceType {

        private final ice.ConnectionType connectionType;
        private final String manufacturer, model, aliases[];

        public DeviceType(ice.ConnectionType connectionType, String manufacturer, String model, String alias) {
            this(connectionType, manufacturer, model, new String[] { alias });
        }

        public DeviceType(ice.ConnectionType connectionType, String manufacturer, String model, String[] alias) {
            this.connectionType = connectionType;
            this.manufacturer = manufacturer;
            this.model = model;
            this.aliases = alias;
        }

        public ice.ConnectionType getConnectionType() {
            return connectionType;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public String getModel() {
            return model;
        }

        public String[] getAliases() {
            return aliases;
        }

        public String getAlias() {
            return aliases[0];
        }

        @Override
        public String toString() {
            return manufacturer + " " + model;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DeviceType that = (DeviceType) o;

            if (!connectionType.equals(that.connectionType)) return false;
            if (!manufacturer.equals(that.manufacturer)) return false;
            if (!model.equals(that.model)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = connectionType.hashCode();
            result = 31 * result + manufacturer.hashCode();
            result = 31 * result + model.hashCode();
            return result;
        }
    }
}
