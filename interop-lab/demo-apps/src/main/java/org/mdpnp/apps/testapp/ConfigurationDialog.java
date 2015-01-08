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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.*;

import org.mdpnp.apps.testapp.Configuration.Application;
import org.mdpnp.devices.DeviceDriverProvider;
import org.mdpnp.devices.serial.SerialProviderFactory;
import org.mdpnp.devices.serial.TCPSerialProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings({ "serial", "rawtypes", "unchecked" })
/**
 * @author Jeff Plourde
 *
 */
public class ConfigurationDialog extends JDialog {
    private final JTextArea welcomeText = new JTextArea(8, 50);
    private final JScrollPane welcomeScroll = new JScrollPane(welcomeText);
    private final JComboBox applications = new JComboBox(Application.values());
    private final JLabel applicationsLabel = new JLabel("Application:");
    private final JButton start = new JButton("Start");
    private final JButton quit = new JButton("Quit");
    private final JTextField domainId = new JTextField("0", 2);
    private boolean quitPressed = true;
    private final JComboBox<DeviceDriverProvider> deviceType = makeDeviceTypesUIModel();
    private final JLabel deviceTypeLabel = new JLabel("Device Type:");

    private JComboBox serialPorts;
    private final JTextField address = new JTextField("", 10);
    private final JLabel addressLabel = new JLabel("Address:");
    private final CardLayout addressCards = new CardLayout();
    private final JPanel addressPanel = new JPanel(addressCards);
    private final JLabel domainIdLabel = new JLabel("Domain Id:");

    public JComboBox getApplications() {
        return applications;
    }

    public JLabel getApplicationsLabel() {
        return applicationsLabel;
    }

    public JTextField getDomainId() {
        return domainId;
    }

    public JLabel getDomainIdLabel() {
        return domainIdLabel;
    }

    public JButton getQuit() {
        return quit;
    }

    public JScrollPane getWelcomeScroll() {
        return welcomeScroll;
    }

    public JTextArea getWelcomeText() {
        return welcomeText;
    }

    private static JComboBox<DeviceDriverProvider> makeDeviceTypesUIModel()
    {
        DeviceDriverProvider[] arr = DeviceFactory.getAvailableDevices();
        DeviceDriverProvider[] l = new DeviceDriverProvider[arr.length+1];
        System.arraycopy(arr, 0, l, 1, arr.length);
        l[0] = null;

        JComboBox jcb = new JComboBox(l);

        jcb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list,
                                                          Object value,
                                                          int index,
                                                          boolean isSelected,
                                                          boolean cellHasFocus) {
                if(value instanceof DeviceDriverProvider) {
                    DeviceDriverProvider ddp = (DeviceDriverProvider) value;
                    DeviceDriverProvider.DeviceType dt = ddp.getDeviceType();
                    return super.getListCellRendererComponent(list, dt, index, isSelected, cellHasFocus);
                }
                else {
                    return super.getListCellRendererComponent(list, "Select One", index, isSelected, cellHasFocus);
                }
            }
        });

        return jcb;
    }

    protected void set(Application app, DeviceDriverProvider dt) {
        switch (app) {
        case ICE_Device_Interface:
            deviceType.setVisible(true);
            deviceTypeLabel.setVisible(true);

            ice.ConnectionType selected = null;
            if(dt != null) {
                start.setText("Start " + dt.getDeviceType().getAlias());
                start.setVisible(true);
                selected = dt.getDeviceType().getConnectionType();
            }
            else {
                start.setVisible(false);
            }
            if (ice.ConnectionType.Serial.equals(selected)) {
                addressLabel.setVisible(true);
                addressLabel.setText("Serial Port:");
                addressPanel.setVisible(true);
                if (null == serialPorts) {
                    serialPorts = new JComboBox(SerialProviderFactory.getDefaultProvider().getPortNames().toArray());
                    addressPanel.add(serialPorts, "serial");
                }
                if (SerialProviderFactory.getDefaultProvider() instanceof TCPSerialProvider) {
                    addressCards.show(addressPanel, "address");
                    addressLabel.setText("IP/Port:");
                } else {
                    addressCards.show(addressPanel, "serial");
                }

            } else if (ice.ConnectionType.Network.equals(selected)) {
                addressLabel.setVisible(true);
                addressLabel.setText("IP Address:");
                addressPanel.setVisible(true);
                addressCards.show(addressPanel, "address");
            } else {
                addressLabel.setVisible(false);
                addressLabel.setText("");
                addressPanel.setVisible(false);
            }
            break;
        case ICE_Supervisor:
        case ICE_ParticipantOnly:
            deviceType.setVisible(false);
            deviceTypeLabel.setVisible(false);
            addressLabel.setVisible(false);
            addressPanel.setVisible(false);
            start.setVisible(true);
            start.setText("Start " + app);
            break;
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                pack();
                //setLocationRelativeTo(null);
            }
        });
    }

    private final static Logger log = LoggerFactory.getLogger(ConfigurationDialog.class);

    private final void addWithAnchor(JComponent c, GridBagConstraints gbc, int anchor, int fill) {
        int previousAnchor = gbc.anchor;
        int previousFill = gbc.fill;
        gbc.anchor = anchor;
        gbc.fill = fill;
        getContentPane().add(c, gbc);
        gbc.anchor = previousAnchor;
        gbc.fill = previousFill;
    }

    private final void addLabel(JComponent c, GridBagConstraints gbc) {
        addWithAnchor(c, gbc, GridBagConstraints.EAST, GridBagConstraints.NONE);
    }

    private final void addOption(JComponent c, GridBagConstraints gbc) {
        addWithAnchor(c, gbc, GridBagConstraints.WEST, GridBagConstraints.NONE);
    }

    public ConfigurationDialog(Window window) {
        this(null, window);
    }

    public ConfigurationDialog(Configuration conf, Window window) {
        super(window, ModalityType.APPLICATION_MODAL);

        if (null != conf) {
            if (null != conf.getApplication()) {
                applications.setSelectedItem(conf.getApplication());
            }
            if (null != conf.getDeviceFactory()) {
                deviceType.setSelectedItem(conf.getDeviceFactory());
            }
            domainId.setText(Integer.toString(conf.getDomainId()));

            if (null != conf.getApplication() && null != conf.getAddress()) {
                switch (conf.getApplication()) {
                case ICE_Device_Interface:
                    if (null != conf.getDeviceFactory()) {
                        ice.ConnectionType connType = conf.getDeviceFactory().getDeviceType().getConnectionType();
                        if (ice.ConnectionType.Network.equals(connType)) {
                            this.address.setText(conf.getAddress());
                        } else if (ice.ConnectionType.Serial.equals(connType)) {
                            if (null == this.serialPorts) {
                                this.serialPorts = new JComboBox(SerialProviderFactory.getDefaultProvider().getPortNames().toArray());
                                addressPanel.add(serialPorts, "serial");
                            }
                            this.serialPorts.setSelectedItem(conf.getAddress());
                            this.address.setText(conf.getAddress());
                        }
                    }
                case ICE_Supervisor:
                    break;
                default:
                    break;
                }
            }
        }

        setTitle("MD PnP Demo Apps");

        setLayout(new GridBagLayout());

        domainId.setHorizontalAlignment(SwingConstants.RIGHT);

        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(2, 0, 2, 0), 2, 2);

        InputStream is = ConfigurationDialog.class.getResourceAsStream("welcome");
        if (null != is) {
            try {

                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuffer sb = new StringBuffer();
                String line = null;
                while (null != (line = br.readLine())) {
                    sb.append(line).append("\n");
                }
                br.close();
                gbc.gridwidth = 3;
                gbc.gridheight = 4;
                welcomeText.setLineWrap(true);
                welcomeText.setWrapStyleWord(true);
                welcomeText.setEditable(false);
                welcomeText.setText(sb.toString());
                addWithAnchor(welcomeScroll, gbc, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
                gbc.gridwidth = 1;
                gbc.gridheight = 1;
                gbc.gridy += 4;
            } catch (IOException ioe) {
                log.warn("No welcome text", ioe);
            }
        }

        gbc.gridx = 0;
        addLabel(applicationsLabel, gbc);
        gbc.gridx++;
        addOption(applications, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        addLabel(deviceTypeLabel, gbc);
        gbc.gridx++;
        addOption(deviceType, gbc);

        addressPanel.add(address, "address");
        gbc.gridy++;
        gbc.gridx = 0;
        addLabel(addressLabel, gbc);
        gbc.gridx++;
        addOption(addressPanel, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        addLabel(domainIdLabel, gbc);
        gbc.gridx++;
        addOption(domainId, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        addOption(quit, gbc);
        gbc.gridx++;
        addLabel(start, gbc);

        getRootPane().setDefaultButton(start);
        start.requestFocus(false);

        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // basic validation of parameters.
                Application app = (Application)applications.getSelectedItem();
                if(Application.ICE_Device_Interface.equals(app)) {
                    DeviceDriverProvider dt = (DeviceDriverProvider) deviceType.getSelectedItem();
                    if(dt == null)
                        return;
                }
                quitPressed = false;
                setVisible(false);
            }
        });

        applications.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    set((Application) e.getItem(), (DeviceDriverProvider) deviceType.getSelectedItem());
                }
            }

        });

        deviceType.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    set((Application) applications.getSelectedItem(), (DeviceDriverProvider) e.getItem());
                }
                else
                    set((Application) applications.getSelectedItem(), null);

            }
        });

        set((Application) applications.getSelectedItem(), (DeviceDriverProvider)deviceType.getSelectedItem());
    }

    private Configuration lastConf;

    public Configuration getLastConfiguration() {
        return lastConf;
    }

    public Configuration showDialog() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        String address = null;
        Application app = (Application) applications.getSelectedItem();
        DeviceDriverProvider ddp = (DeviceDriverProvider)deviceType.getSelectedItem();

        switch (app) {
        case ICE_Device_Interface:
            if(ddp != null) {
                ice.ConnectionType selected = ddp.getDeviceType().getConnectionType();
                if (ice.ConnectionType.Network.equals(selected)) {
                    address = this.address.getText();
                } else if (ice.ConnectionType.Serial.equals(selected)) {
                    if (SerialProviderFactory.getDefaultProvider() instanceof TCPSerialProvider) {
                        address = this.address.getText();
                    } else {
                        address = this.serialPorts.getSelectedItem().toString();
                    }
                }
            }
        case ICE_Supervisor:
            break;
        default:
            break;

        }

        lastConf = new Configuration(app,Integer.parseInt(domainId.getText()), ddp, address);

        dispose();
        return quitPressed ? null : lastConf;
    }
}
