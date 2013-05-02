package org.mdpnp.apps.testapp;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.mdpnp.apps.testapp.Configuration.Application;
import org.mdpnp.apps.testapp.Configuration.DeviceType;
import org.mdpnp.comms.serial.SerialProviderFactory;
import org.mdpnp.messaging.BindingFactory;
import org.mdpnp.messaging.BindingFactory.BindingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationDialog extends JDialog {
    private final JTextArea welcomeText = new JTextArea(8, 50);
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
    private final JLabel bindingSettingsLabel = new JLabel("Settings:");
    
    protected void setTransport(BindingFactory.BindingType transport) {
        switch(transport) {
        case RTI_DDS:
            bindingSettings.setVisible(true);
            bindingSettings.setColumns(4);
            bindingSettingsLabel.setText(transport.getSettingsDescription());
            break;
        case JGROUPS:
            bindingSettings.setVisible(true);
            bindingSettings.setColumns(15);
            bindingSettingsLabel.setText(transport.getSettingsDescription());
            break;
        default:
            bindingSettings.setVisible(false);
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                pack();
                setLocationRelativeTo(null);
            }
        });
        
    }
    
    protected void set(Application app, DeviceType deviceType) {
        switch(app) {
        case ICE_Device_Interface:
            this.deviceType.setVisible(true);
            deviceTypeLabel.setVisible(true);
            start.setText("Start " +deviceType);
            switch(deviceType.getConnectionType()) {
            case Serial:
                addressLabel.setVisible(true);
                addressLabel.setText("Serial Port:");
                addressPanel.setVisible(true);
                if(null == serialPorts) {
                    serialPorts = new JComboBox(SerialProviderFactory.getDefaultProvider().getPortNames().toArray());
                    addressPanel.add(serialPorts, "serial");
                }
                addressCards.show(addressPanel, "serial");
                break;
            case Network:
                addressLabel.setVisible(true);
                addressLabel.setText("IP Address:");
                addressPanel.setVisible(true);
                addressCards.show(addressPanel, "address");
                break;
            default:
                addressLabel.setVisible(false);
                addressLabel.setText("");
                addressPanel.setVisible(false);
            }
            break;
        case ICE_Supervisor:
            this.deviceType.setVisible(false);
            deviceTypeLabel.setVisible(false);
            addressLabel.setVisible(false);
            addressPanel.setVisible(false);
            start.setText("Start " + app);
            break;
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                pack();
                setLocationRelativeTo(null);
            }
        });  
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
                case ICE_Device_Interface:
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
            }
        }
        
        
        setTitle("MD PnP Demo Apps");
        
        setLayout(new GridBagLayout());
        
        bindingSettings.setHorizontalAlignment(SwingConstants.RIGHT);
        
        GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,1.0,1.0,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2,0,2,0), 2, 2);
        
        InputStream is = ConfigurationDialog.class.getResourceAsStream("welcome");
        if(null != is) {
            try {
            
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuffer sb = new StringBuffer();
                String line = null;
                while(null != (line = br.readLine())) {
                    sb.append(line).append("\n");
                }
                br.close();
                gbc.gridwidth = 3;
                gbc.gridheight = 4;
                welcomeText.setLineWrap(true);
                welcomeText.setWrapStyleWord(true);
                welcomeText.setEditable(false);
                welcomeText.setText(sb.toString());
                addWithAnchor(new JScrollPane(welcomeText),  gbc, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
                gbc.gridwidth = 1;
                gbc.gridheight = 1;
                gbc.gridy+=4;
            } catch (IOException ioe) {
                log.warn("No welcome text", ioe);
            }
        }
        
        
        gbc.gridx = 0;
        addLabel(new JLabel("Application:"), gbc);
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
        addLabel(new JLabel("Binding:"), gbc);
        gbc.gridx++;
        addOption(bindings, gbc);
        
        gbc.gridy++;
        gbc.gridx = 0;
        addLabel(bindingSettingsLabel, gbc);
        gbc.gridx++;
        addOption(bindingSettings, gbc);
        
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
    
    private Configuration lastConf;
    
    public Configuration getLastConfiguration() {
        return lastConf;
    }
    
    public Configuration showDialog() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        
        String address = null;
        switch((Application)applications.getSelectedItem()) {
        case ICE_Device_Interface:
            switch(((DeviceType)deviceType.getSelectedItem()).getConnectionType()) {
            case Network:
                address = this.address.getText();
                break;
            case Serial:
                address = this.serialPorts.getSelectedItem().toString();
                break;
            }
            
        }
        lastConf = new Configuration((Application)applications.getSelectedItem(), (BindingFactory.BindingType)bindings.getSelectedItem(), bindingSettings.getText(), (DeviceType) deviceType.getSelectedItem(), address);

        dispose();
        return quitPressed ? null : lastConf;
    }
}