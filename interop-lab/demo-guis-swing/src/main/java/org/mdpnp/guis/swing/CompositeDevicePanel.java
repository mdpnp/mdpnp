package org.mdpnp.guis.swing;

import ice.DeviceConnectivity;
import ice.DeviceIdentity;
import ice.Numeric;
import ice.SampleArray;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.subscription.SampleInfo;

@SuppressWarnings("serial")
public class CompositeDevicePanel extends JComponent implements DeviceMonitorListener {
   private final JLabel manufacturer = new JLabel("MANUFACTURER");
   private final JLabel model = new JLabel("MODEL");
   private final JLabel serial_number = new JLabel("SERIAL#");
   
   private final JLabel connectionState = new JLabel("CONN");
   private final JLabel universal_device_identifier = new JLabel("UDI");
   private final JLabel icon = new JLabel("ICON");
   
   private static final Logger log = LoggerFactory.getLogger(CompositeDevicePanel.class);
   
   private final JPanel data = new JPanel();
   private final Collection<DevicePanel> dataComponents = new ArrayList<DevicePanel>();
   
   private final Set<Integer> knownIdentifiers = new HashSet<Integer>();

    
    public CompositeDevicePanel() {
        super();
        setLayout(new BorderLayout());
        JComponent header = new JPanel();
        header.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,1.0,1.0,GridBagConstraints.BASELINE,GridBagConstraints.BOTH, new Insets(1,1,1,1), 1,1);
        
        header.add(new JLabel("Manufacturer"), gbc);
        gbc.gridx++;
        header.add(manufacturer, gbc);
        
        gbc.gridy++;
        gbc.gridx--;
        header.add(new JLabel("Model"), gbc);
        gbc.gridx++;
        header.add(model, gbc);
        
        gbc.gridy++;
        gbc.gridx--;
        header.add(new JLabel("Serial Number"), gbc);
        gbc.gridx++;
        header.add(serial_number, gbc);
        
        gbc.gridy++;
        gbc.gridx--;
        header.add(new JLabel("Universal Device Id"), gbc);
        gbc.gridx++;
        header.add(universal_device_identifier, gbc);
        
        gbc.gridy++;
        gbc.gridx--;
        header.add(new JLabel("Connection State"), gbc);
        gbc.gridx++;
        header.add(connectionState, gbc);
        
        gbc.gridy++;
        gbc.gridheight = gbc.gridy;
        gbc.gridy = 0;
        gbc.gridx = 2;
        header.add(icon, gbc);
        add(header, BorderLayout.NORTH);
        add(data, BorderLayout.CENTER);
    }

    @Override
    public void deviceIdentity(DeviceIdentity di, SampleInfo sampleInfo) {
        manufacturer.setText(di.manufacturer);
        model.setText(di.model);
        serial_number.setText(di.serial_number);
        universal_device_identifier.setText(di.universal_device_identifier);
        icon.setText("");
        icon.setIcon(new ImageIcon(IconUtil.image(di.icon)));
    }

    @Override
    public void deviceConnectivity(DeviceConnectivity dc, SampleInfo sampleInfo) {
        connectionState.setText(dc.state.name() + (!"".equals(dc.info)?(" ("+dc.info+")"):""));
    }

    private void replaceDataPanels() {
        
        synchronized(dataComponents) {
            DevicePanelFactory.resolvePanels(knownIdentifiers, dataComponents);
            log.debug("dataComponents:"+dataComponents);
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                data.removeAll();
                synchronized(dataComponents) {
                    data.setLayout(new GridLayout(dataComponents.size(), 1));
                    for(DevicePanel p : dataComponents) {
                        data.add(p);
                    }
                }
                data.revalidate();
            }
        }); 
    }
    
    @Override
    public void numeric(Numeric n, SampleInfo sampleInfo) {
        if(!knownIdentifiers.contains(n.name)) {
            // avoid reboxing ... also tells us if something is new
            knownIdentifiers.add(n.name);
            log.trace("New numeric, new set:"+knownIdentifiers);
            replaceDataPanels();
        }
        synchronized(dataComponents) {
            for(DevicePanel d : dataComponents) {
                d.numeric(n, sampleInfo);
            }
        }
//        log.trace(n.toString());
    }

    @Override
    public void sampleArray(SampleArray sampleArray, SampleInfo sampleInfo) {
        if(!knownIdentifiers.contains(sampleArray.name)) {
            knownIdentifiers.add(sampleArray.name);
            log.trace("New SampleArray, new set:"+knownIdentifiers);
            replaceDataPanels();
        }
        synchronized(dataComponents) {
            for(DevicePanel d : dataComponents) {
                d.sampleArray(sampleArray, sampleInfo);
            }
        }
//        log.trace(sampleArray.toString());
    }
    
    public void reset() {
        knownIdentifiers.clear();
        dataComponents.clear();
        replaceDataPanels();
    }

    @Override
    public void addNumeric(int name) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeNumeric(int name) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addSampleArray(int name) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeSampleArray(int name) {
        // TODO Auto-generated method stub
        
    }
}
