package org.mdpnp.guis.swing;

import ice.DeviceConnectivity;
import ice.DeviceIdentity;
import ice.InfusionStatus;
import ice.InfusionStatusDataReader;
import ice.Numeric;
import ice.SampleArray;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;

@SuppressWarnings("serial")
public class CompositeDevicePanel extends JPanel implements DeviceMonitorListener {
   private final JLabel manufacturer = new JLabel("MANUFACTURER");
   private final JLabel model = new JLabel("MODEL");
   private final JLabel serial_number = new JLabel("SERIAL#");
   
   private final JLabel connectionState = new JLabel("CONN");
   private final JLabel universal_device_identifier = new JLabel("UDI");
   private final JLabel icon = new JLabel("ICON");
   
   private static final Logger log = LoggerFactory.getLogger(CompositeDevicePanel.class);
   
   private final JPanel data = new JPanel(new BorderLayout());
   private final JLabel WAITING = new JLabel("Waiting for data...");
   private final Collection<DevicePanel> dataComponents = new ArrayList<DevicePanel>();
   
   private final Set<Integer> knownIdentifiers = new HashSet<Integer>();
   private final Set<String> knownPumps = new HashSet<String>();

    
    public CompositeDevicePanel() {
        super(new BorderLayout());
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
        data.add(WAITING, BorderLayout.CENTER);
        add(data, BorderLayout.CENTER);
    }

    
    private final Set<InstanceHandle_t> seenInstances = new HashSet<InstanceHandle_t>();
    @Override
    public void deviceIdentity(ice.DeviceIdentityDataReader reader, ice.DeviceIdentitySeq di_seq, SampleInfoSeq info_seq) {
        // TODO really a History QoS with keep last of 1 is better but trying to be compatible with various QoS settings for now
        seenInstances.clear();
        for(int i = info_seq.size() - 1; i >= 0; i--) {
            SampleInfo si = (SampleInfo) info_seq.get(i);
            if(si.valid_data && !seenInstances.contains(si.instance_handle)) {
                seenInstances.add(si.instance_handle);
                DeviceIdentity di = (DeviceIdentity) di_seq.get(i);
                manufacturer.setText(di.manufacturer);
                model.setText(di.model);
                serial_number.setText(di.serial_number);
                universal_device_identifier.setText(di.universal_device_identifier);
                icon.setText("");
                Image img = IconUtil.image(di.icon);

                if(null != img) {
                    icon.setIcon(new ImageIcon(img));
                }
            }
        }

    }

    
    
    @Override
    public void deviceConnectivity(ice.DeviceConnectivityDataReader reader, ice.DeviceConnectivitySeq dc_seq, SampleInfoSeq info_seq) {
        seenInstances.clear();
        for(int i = info_seq.size() - 1; i >= 0; i--) {
            SampleInfo si = (SampleInfo) info_seq.get(i);
            if(si.valid_data && !seenInstances.contains(si.instance_handle)) {
                seenInstances.add(si.instance_handle);
                DeviceConnectivity dc = (DeviceConnectivity) dc_seq.get(i);
                connectionState.setText(dc.state.name() + (!"".equals(dc.info)?(" ("+dc.info+")"):""));
            }
        }
    }
    
    private void replaceDataPanels() {
        DevicePanel[] _dataComponents;
        synchronized(dataComponents) {
            DevicePanelFactory.resolvePanels(knownIdentifiers, dataComponents, knownPumps);
            _dataComponents = dataComponents.toArray(new DevicePanel[0]);
        }
        final DevicePanel[] __dataComponents = _dataComponents;
        
        Runnable r = new Runnable() {
            public void run() {
                data.removeAll();
                if(__dataComponents.length == 0) {
                    data.setLayout(new BorderLayout());
                    data.add(WAITING, BorderLayout.CENTER);
                } else {
                    data.setLayout(new GridLayout(__dataComponents.length, 1));
                    for(DevicePanel d : __dataComponents) {
                        data.add(d);
                    }
                    CompositeDevicePanel.this.validate();
                    CompositeDevicePanel.this.repaint();
                }
            }
        };
        if(SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(r);
            } catch (InvocationTargetException e) {
                log.error("error adding panels", e);
            } catch (InterruptedException e) {
                log.error("error adding panels", e);
            }
        }
    }
    
    @Override
    public void numeric(ice.NumericDataReader reader, ice.NumericSeq nu_seq, SampleInfoSeq info_seq) {
        seenInstances.clear();
        for(int i = info_seq.size() - 1; i >= 0; i--) {
            SampleInfo si = (SampleInfo) info_seq.get(i);
            if(si.valid_data && !seenInstances.contains(si.instance_handle)) {
                seenInstances.add(si.instance_handle);
                ice.Numeric n = (Numeric) nu_seq.get(i);
                if(!knownIdentifiers.contains(n.name)) {
                    // avoid reboxing ... also tells us if something is new
                    knownIdentifiers.add(n.name);
                    log.trace("New numeric, new set:"+knownIdentifiers);
                    replaceDataPanels();
                }
                // TODO this should probably be handled by replaying the contents of the reader for any new gui panels
                synchronized(dataComponents) {
                    for(DevicePanel d : dataComponents) {
                        d.numeric(n, si);
                    }
                }
            }
        }

//        log.trace(n.toString());
    }

    @Override
    public void sampleArray(ice.SampleArrayDataReader reader, ice.SampleArraySeq sa_seq, SampleInfoSeq info_seq) {
        seenInstances.clear();
        for(int i = info_seq.size() - 1; i >= 0; i--) {
            SampleInfo si = (SampleInfo) info_seq.get(i);
            if(si.valid_data && !seenInstances.contains(si.instance_handle)) {
                seenInstances.add(si.instance_handle);
                ice.SampleArray sampleArray = (SampleArray) sa_seq.get(i);
                if(!knownIdentifiers.contains(sampleArray.name)) {
                    knownIdentifiers.add(sampleArray.name);
                    log.trace("New SampleArray, new set:"+knownIdentifiers);
                    replaceDataPanels();
                }
                synchronized(dataComponents) {
                    for(DevicePanel d : dataComponents) {
                        d.sampleArray(sampleArray, si);
                    }
                }
            }
        }
    }
    
    public void reset() {
        knownIdentifiers.clear();
        knownPumps.clear();
        replaceDataPanels();
    }
    private DeviceMonitor deviceMonitor;
    
    public void setModel(DeviceMonitor deviceMonitor) {
        if(null != this.deviceMonitor) {
            this.deviceMonitor.removeListener(this);
        }
        this.deviceMonitor = deviceMonitor;
        reset();
        if(null != this.deviceMonitor) { 
            this.deviceMonitor.addListener(this);
        }
    }
    public DeviceMonitor getModel() {
        return deviceMonitor;
    }



    @Override
    public void infusionPump(InfusionStatusDataReader reader, ice.InfusionStatusSeq status, SampleInfoSeq sampleInfo) {
        for(int i = 0; i < sampleInfo.size(); i++) {
            InfusionStatus infusionStatus = (InfusionStatus) status.get(i);
            SampleInfo si = (SampleInfo) sampleInfo.get(i);
            if(si.valid_data) {
//                log.info("Pump Status:"+infusionStatus);
                if(!knownPumps.contains(infusionStatus.universal_device_identifier)) {
                    knownPumps.add(infusionStatus.universal_device_identifier);
                    replaceDataPanels();
                }
                synchronized(dataComponents) {
                    for(DevicePanel d : dataComponents) {
                        d.infusionStatus(infusionStatus, si);
                    }
                }
            }
        }
        
    }
}
