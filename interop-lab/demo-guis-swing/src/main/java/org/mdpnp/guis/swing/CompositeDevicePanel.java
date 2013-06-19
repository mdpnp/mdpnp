package org.mdpnp.guis.swing;

import ice.DeviceConnectivity;
import ice.DeviceIdentity;
import ice.Numeric;
import ice.SampleArray;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
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
   private final JLabel description = new JLabel("FUNK");
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
        header.setLayout(new GridLayout(2,2));
        header.add(description);
        header.add(connectionState);
        header.add(universal_device_identifier);
        header.add(icon);
        add(header, BorderLayout.NORTH);
        add(data, BorderLayout.CENTER);
    }

    @Override
    public void deviceIdentity(DeviceIdentity di, SampleInfo sampleInfo) {
        description.setText(di.manufacturer + " " + di.model);
        universal_device_identifier.setText(di.universal_device_identifier);
        icon.setText("");
        icon.setIcon(new ImageIcon(IconUtil.image(di.icon)));
    }

    @Override
    public void deviceConnectivity(DeviceConnectivity dc, SampleInfo sampleInfo) {
        connectionState.setText(dc.state.name());
    }

    // TODO this should matter eventually for updating new panels with extant information
    // should really be pushed down to DDS somehow
    private Numeric lastNumeric;
    private SampleArray lastSampleArray;
    
    
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
