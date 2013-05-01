package org.mdpnp.apps.testapp;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.mdpnp.apps.testapp.Configuration.DeviceType;
import org.mdpnp.apps.testapp.Configuration.Application;
import org.mdpnp.comms.serial.SerialProviderFactory;
import org.mdpnp.messaging.BindingFactory;
import org.mdpnp.messaging.BindingFactory.BindingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationDialog extends JDialog {
    private final JComboBox applications = new JComboBox(Application.values());
    private final JComboBox bindings = new JComboBox(BindingFactory.BindingType.values());
    private final JButton start = new JButton("Start");
    private final JButton quit = new JButton("Quit");
    private final JTextField bindingSettings = new JTextField("0", 2);
    private boolean quitPressed = true;
    private final JComboBox deviceType = new JComboBox(DeviceType.values());
    private final JLabel deviceTypeLabel = new JLabel("Device Type:");
    
    private JComboBox serialPorts;
    private final JTextField address = new JTextField("", 10);
    private final JLabel addressLabel = new JLabel("Address:");
    private final CardLayout addressCards = new CardLayout();
    private final JPanel addressPanel = new JPanel(addressCards);
    
    protected void setTransport(BindingFactory.BindingType transport) {
        switch(transport) {
        case RTI_DDS:
            bindingSettings.setVisible(true);
            break;
        default:
            bindingSettings.setVisible(false);
        }
        pack();
    }
    
    protected void set(Application app, DeviceType deviceType) {
        switch(app) {
        case DeviceAdapter:
            this.deviceType.setVisible(true);
            deviceTypeLabel.setVisible(true);
            start.setText("Start " +deviceType);
            switch(deviceType.getConnectionType()) {
            case Serial:
                addressLabel.setVisible(true);
                addressPanel.setVisible(true);
                if(null == serialPorts) {
                    serialPorts = new JComboBox(SerialProviderFactory.getDefaultProvider().getPortNames().toArray());
                    addressPanel.add(serialPorts, "serial");
                }
                addressCards.show(addressPanel, "serial");
                break;
            case Network:
                addressLabel.setVisible(true);
                addressPanel.setVisible(true);
                addressCards.show(addressPanel, "address");
                break;
            default:
                addressLabel.setVisible(false);
                addressPanel.setVisible(false);
            }
            break;
        case DemoApp:
            this.deviceType.setVisible(false);
            deviceTypeLabel.setVisible(false);
            addressLabel.setVisible(false);
            addressPanel.setVisible(false);
            start.setText("Start " + app);
            break;
        }
        pack();        
    }
    
    private final static Logger log = LoggerFactory.getLogger(ConfigurationDialog.class);
    
    private static boolean ddsInit() {
        try {
            if((Boolean)Class.forName("org.mdpnp.rti.dds.DDS").getMethod("init").invoke(null)) {
                return true;                
            } else {
                throw new Exception("Unable to init");
            }
        } catch (Throwable t) {
            log.warn("Unable to initialize RTI DDS, removing the option", t);
        }
        return false;
    }
    
    public ConfigurationDialog(Configuration conf) {
        super( (JDialog)null, true);
        
        if(!ddsInit()) {
            bindings.removeItem(BindingType.RTI_DDS);
        }
        
        if(null != conf) {
            if(null != conf.getApplication()) {
                applications.setSelectedItem(conf.getApplication());
            }
            if(null != conf.getDeviceType()) {
                deviceType.setSelectedItem(conf.getDeviceType());
            } 
            if(null != conf.getBinding()) {
                bindings.setSelectedItem(conf.getBinding());
            }
            if(null != conf.getBindingSettings()) {
                bindingSettings.setText(conf.getBindingSettings());
            }
            if(null != conf.getApplication() && null != conf.getAddress()) {
                switch(conf.getApplication()) {
                case DeviceAdapter:
                    if(null != conf.getDeviceType()) {
                        switch(conf.getDeviceType().getConnectionType()) {
                        case Network:
                            this.address.setText(conf.getAddress());
                            break;
                        case Serial:
                            if(null == this.serialPorts) {
                                this.serialPorts = new JComboBox(SerialProviderFactory.getDefaultProvider().getPortNames().toArray());
                                addressPanel.add(serialPorts, "serial");
                            }
                            this.serialPorts.setSelectedItem(conf.getAddress());
                            break;
                        }
                    }                    
                }
                this.address.setText(conf.getAddress());
                this.serialPorts.setSelectedItem(conf.getAddress());
            }
        }
        
        
        setTitle("MD PnP Demo Apps");
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        
        bindingSettings.setHorizontalAlignment(SwingConstants.RIGHT);
        
        GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,1.0,1.0,GridBagConstraints.BASELINE, GridBagConstraints.BOTH, new Insets(2,0,2,0), 2, 2);
        
        getContentPane().add(new JLabel("Application:"), gbc);
        gbc.gridx++;
        getContentPane().add(applications, gbc);
        
        
        gbc.gridy = 1;
        gbc.gridx = 0;
        getContentPane().add(deviceTypeLabel, gbc);
        gbc.gridx++;
        getContentPane().add(deviceType, gbc);
        
        gbc.gridy = 2;
        gbc.gridx = 0;
        getContentPane().add(new JLabel("Binding:"), gbc);
        gbc.gridx++;
        getContentPane().add(bindings, gbc);
        gbc.gridx++;
        getContentPane().add(bindingSettings, gbc);

        addressPanel.add(address, "address");
        
        gbc.gridy = 3;
        gbc.gridx = 0;
        getContentPane().add(addressLabel, gbc);
        gbc.gridx++;
        gbc.gridwidth = 2;
        getContentPane().add(addressPanel, gbc);
        
        gbc.gridwidth = 1;
        
        gbc.gridy = 4;
        gbc.gridx = 0;
        getContentPane().add(quit, gbc);
        gbc.gridx++;
        gbc.gridwidth = 2;
        getContentPane().add(start, gbc);

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
                quitPressed = false;
                setVisible(false);
            }
        });
        
        applications.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()==ItemEvent.SELECTED) {
                    set((Application)e.getItem(), (DeviceType)deviceType.getSelectedItem());
                }
            }
            
        });
        
        bindings.addItemListener(new ItemListener() {
           @Override
            public void itemStateChanged(ItemEvent e) {
               if(e.getStateChange()==ItemEvent.SELECTED) {
                   setTransport((BindingFactory.BindingType)e.getItem());
               }
            } 
        });
        
        deviceType.addItemListener(new ItemListener() {
           @Override
            public void itemStateChanged(ItemEvent e) {
               if(e.getStateChange()==ItemEvent.SELECTED) {
                   set((Application)applications.getSelectedItem(),(DeviceType)e.getItem());
               }
            } 
        });

        setTransport((BindingFactory.BindingType)bindings.getSelectedItem());
        set((Application)applications.getSelectedItem(), (DeviceType)deviceType.getSelectedItem());
    }
    public Configuration showDialog() {
        pack();
        setVisible(true);
        dispose();
        String address = null;
        switch((Application)applications.getSelectedItem()) {
        case DeviceAdapter:
            switch(((DeviceType)deviceType.getSelectedItem()).getConnectionType()) {
            case Network:
                address = this.address.getText();
                break;
            case Serial:
                address = this.serialPorts.getSelectedItem().toString();
                break;
            }
            
        }
        return quitPressed ? null : new Configuration((Application)applications.getSelectedItem(), (BindingFactory.BindingType)bindings.getSelectedItem(), bindingSettings.getText(), (DeviceType) deviceType.getSelectedItem(), address);
    }
}