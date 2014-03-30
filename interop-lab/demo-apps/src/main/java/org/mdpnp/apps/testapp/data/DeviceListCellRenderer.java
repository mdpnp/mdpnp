package org.mdpnp.apps.testapp.data;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.DeviceListModel;

@SuppressWarnings("serial")
public class DeviceListCellRenderer extends DefaultListCellRenderer {
    private final DeviceListModel model;
    
    public DeviceListCellRenderer(final DeviceListModel model) {
        this.model = model;
    }
    
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        String udi = null;
        Device device = null;
        
        if (value != null) {
            if(value instanceof ice.SampleArray) {
                udi = ((ice.SampleArray)value).unique_device_identifier;
            } else if(value instanceof ice.Numeric) {
                udi = ((ice.Numeric)value).unique_device_identifier;
            } else if(value instanceof ice.InfusionStatus) {
                udi = ((ice.InfusionStatus)value).unique_device_identifier;
            }
            if(udi != null && model != null) {
                device = model.getByUniqueDeviceIdentifier(udi);
            }
        }
        Component c = super.getListCellRendererComponent(list, device == null ? value : device.getMakeAndModel(), index, isSelected, cellHasFocus);
        if (c instanceof JLabel && device != null) {
            ((JLabel) c).setIcon(device.getIcon());
            ((JLabel) c).setOpaque(false);
        }
        return c;
    }
}
