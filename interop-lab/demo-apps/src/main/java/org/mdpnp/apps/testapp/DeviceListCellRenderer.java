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
package org.mdpnp.apps.testapp;

import ice.DeviceConnectivity;
import ice.DeviceIdentity;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

@SuppressWarnings({ "serial", "rawtypes" })
/**
 * @author Jeff Plourde
 *
 */
public class DeviceListCellRenderer extends JLabel implements ListCellRenderer {
    private final DeviceListModel model;

    public DeviceListCellRenderer() {
        this(null);
    }
    
    public DeviceListCellRenderer(final DeviceListModel model) {
        super();
        this.model = model;
        setVerticalTextPosition(SwingConstants.BOTTOM);
        setHorizontalTextPosition(SwingConstants.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        String udi;
        Device device = null;
        
        if(value instanceof ice.SampleArray) {
            udi = ((ice.SampleArray)value).unique_device_identifier;
        } else if(value instanceof ice.Numeric) {
            udi = ((ice.Numeric)value).unique_device_identifier;
        } else if(value instanceof ice.InfusionStatus) {
            udi = ((ice.InfusionStatus)value).unique_device_identifier;
        } else if(value instanceof Device) {
            device = (Device) value;
            udi = device.getUDI();
        } else {
            udi = null;
        }
        
        if(null != udi && null == device && null != model) {
            device = model.getByUniqueDeviceIdentifier(udi);
        }
        
        if (null != device) {
//            DeviceIcon icon = device.getIcon();
//
//            DeviceConnectivity dc = device.getDeviceConnectivity();
//            if (null != dc) {
//                if (icon != null) {
//                    icon.setConnected(ice.ConnectionState.Connected.equals(dc.state));
//                }
//            } else {
//                if (icon != null) {
//                    icon.setConnected(true);
//                }
//            }

            DeviceIdentity di = device.getDeviceIdentity();
            if (null != di) {
                setText(device.getMakeAndModel());
            } else {
                setText("<unknown>");
            }

//            if (icon != null) {
//                setIcon(icon);
//            } else {
//                setIcon(DeviceIcon.WHITE_SQUARE_ICON);
//            }
            
        } else {
            setText("<unknown>");
//            setIcon(DeviceIcon.WHITE_SQUARE_ICON);
        }
        return this;
    }
}
