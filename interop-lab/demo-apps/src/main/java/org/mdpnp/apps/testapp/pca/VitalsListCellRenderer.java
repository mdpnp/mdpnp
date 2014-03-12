/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.apps.testapp.pca;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.LineBorder;

import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.DeviceIcon;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.vital.Value;
import org.mdpnp.apps.testapp.vital.Vital;

@SuppressWarnings({ "serial", "rawtypes" })
/**
 * @author Jeff Plourde
 *
 */
public class VitalsListCellRenderer extends JPanel implements ListCellRenderer {

    private final JLabel name = new JLabel(" ");
    private final JLabel deviceName = new JLabel(" ");
    private final JLabel value = new JLabel(" ");
    private final JLabel icon = new JLabel(" ");
    private final JLabel udi = new JLabel(" ");

    private final DeviceListModel deviceListModel;

    public VitalsListCellRenderer(DeviceListModel deviceListModel) {
        super(new BorderLayout());
        this.deviceListModel = deviceListModel;
        setBorder(new LineBorder(Color.gray, 1));
        setOpaque(false);
        name.setFont(name.getFont().deriveFont(24f));
        value.setFont(value.getFont().deriveFont(24f));
        deviceName.setFont(deviceName.getFont().deriveFont(14f));
        udi.setFont(Font.decode("fixed-10"));
        // value.setFont(value.getFont().deriveFont(14f));
        JPanel pan = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0,
                0), 0, 0);
        pan.setOpaque(false);

        gbc.weightx = 0.9;
        pan.add(name, gbc);
        gbc.gridy++;
        pan.add(deviceName, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.1;
        gbc.gridy = 0;
        pan.add(value, gbc);
        gbc.gridy++;
        pan.add(udi, gbc);

        add(icon, BorderLayout.WEST);
        add(pan, BorderLayout.CENTER);

    }

    @Override
    public Component getListCellRendererComponent(JList list, Object val, int index, boolean isSelected, boolean cellHasFocus) {
        Vital vital = (Vital) val;
        // strongly thinking about making these identifiers into strings
        String name = vital.getLabel();
        String units = vital.getUnits();

        this.name.setText(name);
        String s = "";
        if (vital.getValues().isEmpty()) {
            s = "<NO SOURCES>";
            icon.setIcon(null);
            deviceName.setText("");
            udi.setText("");
        } else {
            Device device = deviceListModel.getByUniqueDeviceIdentifier(vital.getValues().get(0).getUniqueDeviceIdentifier());
            if (null != device) {
                DeviceIcon di = device.getIcon();
                if (null != di) {
                    icon.setIcon(new ImageIcon(di.getImage()));
                }
                deviceName.setText(device.getMakeAndModel());
                udi.setText(device.getShortUDI());
            } else {
                icon.setIcon(null);
                deviceName.setText("");
                udi.setText(vital.getValues().get(0).getUniqueDeviceIdentifier().substring(0, Device.SHORT_UDI_LENGTH));
            }
            for (Value v : vital.getValues()) {
                s += v.getNumeric().value + " ";
            }
            s += units;
        }
        value.setText(s);

        // DeviceIcon di = v.getDevice().getIcon();
        // if(null != di) {
        // icon.setIcon(new ImageIcon(di.getImage()));
        // }
        // deviceName.setText(v.getDevice().getMakeAndModel());
        // udi.setText(v.getDevice().getShortUDI());
        return this;
    }

}
