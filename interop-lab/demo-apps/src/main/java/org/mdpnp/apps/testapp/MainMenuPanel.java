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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import org.mdpnp.apps.testapp.IceApplicationProvider.AppType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class MainMenuPanel extends JPanel {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(MainMenuPanel.class);
    private final JList<AppType> appList;
    private final JList<Device> deviceList;

    public MainMenuPanel(IceApplicationProvider.AppType[] appTypes) {
        super(new GridBagLayout());

        setOpaque(false);

        Font bigFont = Font.decode("verdana-30");

        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.BASELINE, GridBagConstraints.BOTH, new Insets(2, 10,
                2, 10), 0, 0);
        appList = new JList<AppType>(appTypes);
        appList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        appList.setVisibleRowCount(0);
        appList.setFixedCellHeight(150);
        appList.setFixedCellWidth(150);
        
        deviceList = new JList<Device>();
        deviceList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        deviceList.setVisibleRowCount(0);
        deviceList.setFixedCellHeight(150);
        deviceList.setFixedCellWidth(150);
        
        AppListCellRenderer lcr = new AppListCellRenderer();
        DeviceListCellRenderer dlcr = new DeviceListCellRenderer();
        
        lcr.setHorizontalAlignment(SwingConstants.CENTER);
        lcr.setVerticalAlignment(SwingConstants.CENTER);
        
        dlcr.setHorizontalAlignment(SwingConstants.CENTER);
        dlcr.setVerticalAlignment(SwingConstants.CENTER);

        appList.setCellRenderer(lcr);
        deviceList.setCellRenderer(dlcr);

        JLabel lbl;
        add(lbl = new JLabel("Available Applications"), gbc);
        lbl.setFont(bigFont);
        gbc.gridy++;
        gbc.weighty = 100.0;
        JScrollPane scrollAppList = new JScrollPane(appList);
        
        scrollAppList.setBorder(null);
        scrollAppList.getViewport().setOpaque(false);
        add(scrollAppList, gbc);
        scrollAppList.setOpaque(false);
        appList.setOpaque(false);

        gbc.gridy++;
        gbc.weighty = 1.0;
        add(new JLabel(), gbc);

        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.gridx++;
        gbc.weighty = 1.0;
        add(lbl = new JLabel("Connected Devices"), gbc);
        lbl.setFont(bigFont);
        gbc.gridy++;
        gbc.weighty = 100.0;

        JScrollPane scrollDeviceList = new JScrollPane(deviceList);
        
        scrollDeviceList.setBorder(null);
        add(scrollDeviceList, gbc);
        scrollDeviceList.setOpaque(false);
        scrollDeviceList.getViewport().setOpaque(false);
        deviceList.setOpaque(false);

    }

    public JList<AppType> getAppList() {
        return appList;
    }

    public JList<Device> getDeviceList() {
        return deviceList;
    }

}
