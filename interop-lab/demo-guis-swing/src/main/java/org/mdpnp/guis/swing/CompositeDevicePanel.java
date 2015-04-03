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
package org.mdpnp.guis.swing;


import ice.DeviceConnectivity;
import ice.DeviceConnectivityDataReader;
import ice.DeviceIdentity;
import ice.DeviceIdentityDataReader;
import ice.InfusionStatus;
import ice.InfusionStatusDataReader;
import ice.Numeric;
import ice.NumericDataReader;
import ice.SampleArray;
import ice.SampleArrayDataReader;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;
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

import org.mdpnp.rtiapi.data.DeviceDataMonitor;
import org.mdpnp.rtiapi.data.ReaderInstanceModel;
import org.mdpnp.rtiapi.data.InstanceModelListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.SampleInfo;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class CompositeDevicePanel extends JPanel {
    protected final JLabel manufacturer = new JLabel(" ");
    protected final JLabel model = new JLabel(" ");
    protected final JLabel serial_number = new JLabel(" ");

    protected final JLabel connectionState = new JLabel(" ");
    protected final JLabel unique_device_identifier = new JLabel(" ");
    protected final JLabel icon = new JLabel(" ");

    private static final Logger log = LoggerFactory.getLogger(CompositeDevicePanel.class);

    private final JPanel data = new JPanel(new BorderLayout());
    private final JLabel WAITING = new JLabel("Waiting for data...");
    private final Collection<DevicePanel> dataComponents = new ArrayList<DevicePanel>();

    private final Set<String> knownIdentifiers = new HashSet<String>();
    private final Set<String> knownPumps = new HashSet<String>();


    public CompositeDevicePanel()  {
        super(new BorderLayout());
        
        JComponent header = new JPanel();
        header.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.BASELINE, GridBagConstraints.BOTH, new Insets(1, 1,
                1, 1), 1, 1);

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
        header.add(new JLabel("Unique Device Identifier"), gbc);
        gbc.gridx++;
        header.add(unique_device_identifier, gbc);

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

    private final InstanceModelListener<ice.DeviceIdentity, ice.DeviceIdentityDataReader> deviceIdentityListener = new InstanceModelListener<ice.DeviceIdentity, ice.DeviceIdentityDataReader>() {

        @Override
        public void instanceAlive(ReaderInstanceModel<DeviceIdentity, DeviceIdentityDataReader> model, DeviceIdentityDataReader reader,
                DeviceIdentity data, SampleInfo sampleInfo) {
            
        }

        @Override
        public void instanceNotAlive(ReaderInstanceModel<DeviceIdentity, DeviceIdentityDataReader> model, DeviceIdentityDataReader reader,
                DeviceIdentity keyHolder, SampleInfo sampleInfo) {
            
        }

        @Override
        public void instanceSample(ReaderInstanceModel<DeviceIdentity, DeviceIdentityDataReader> model, DeviceIdentityDataReader reader,
                DeviceIdentity data, SampleInfo sampleInfo) {
            CompositeDevicePanel.this.manufacturer.setText(data.manufacturer);
            CompositeDevicePanel.this.model.setText(data.model);
            serial_number.setText(data.serial_number);
            unique_device_identifier.setText(data.unique_device_identifier);
            icon.setText("");
            BufferedImage img;
            try {
                img = IconUtil.image(data.icon);
            } catch (IOException e) {
                log.error("Error loading device image", e);
                img = null;
            }

            if (null != img) {
                icon.setIcon(new ImageIcon(img));
            }
        }
        
    };
    
    private final InstanceModelListener<ice.DeviceConnectivity, ice.DeviceConnectivityDataReader> deviceConnectivityListener = new InstanceModelListener<ice.DeviceConnectivity, ice.DeviceConnectivityDataReader>() {

        @Override
        public void instanceAlive(ReaderInstanceModel<DeviceConnectivity, DeviceConnectivityDataReader> model, DeviceConnectivityDataReader reader,
                DeviceConnectivity data, SampleInfo sampleInfo) {
            
        }

        @Override
        public void instanceNotAlive(ReaderInstanceModel<DeviceConnectivity, DeviceConnectivityDataReader> model, DeviceConnectivityDataReader reader,
                DeviceConnectivity keyHolder, SampleInfo sampleInfo) {
        }

        @Override
        public void instanceSample(ReaderInstanceModel<DeviceConnectivity, DeviceConnectivityDataReader> model, DeviceConnectivityDataReader reader,
                DeviceConnectivity data, SampleInfo sampleInfo) {
            connectionState.setText(data.state.name() + (!"".equals(data.info) ? (" (" + data.info + ")") : ""));
        }
        
    };
    
    private final InstanceModelListener<ice.Numeric, ice.NumericDataReader> numericListener = new InstanceModelListener<ice.Numeric, ice.NumericDataReader>() {

        @Override
        public void instanceAlive(ReaderInstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric data, SampleInfo sampleInfo) {
            if (0 != (InstanceStateKind.ALIVE_INSTANCE_STATE & sampleInfo.instance_state) && !knownIdentifiers.contains(data.metric_id)) {
                // avoid reboxing ... also tells us if something is new
                knownIdentifiers.add(data.metric_id);
                log.trace("New numeric, new set:" + knownIdentifiers);
                replaceDataPanels();
            }
            // TODO this should probably be handled by replaying the contents of
            // the reader for any new gui panels

        }

        @Override
        public void instanceNotAlive(ReaderInstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric keyHolder,
                SampleInfo sampleInfo) {
            
        }

        @Override
        public void instanceSample(ReaderInstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric data, SampleInfo sampleInfo) {
            
        }
        
    };
    
    private final InstanceModelListener<ice.SampleArray, ice.SampleArrayDataReader> sampleArrayListener = new InstanceModelListener<ice.SampleArray, ice.SampleArrayDataReader>() {

        @Override
        public void instanceAlive(ReaderInstanceModel<SampleArray, SampleArrayDataReader> model, SampleArrayDataReader reader, SampleArray data,
                SampleInfo sampleInfo) {
            if (0 != (InstanceStateKind.ALIVE_INSTANCE_STATE & sampleInfo.instance_state) && !knownIdentifiers.contains(data.metric_id)) {
                knownIdentifiers.add(data.metric_id);
                log.trace("New SampleArray, new set:" + knownIdentifiers);
                replaceDataPanels();
            }
        }

        @Override
        public void instanceNotAlive(ReaderInstanceModel<SampleArray, SampleArrayDataReader> model, SampleArrayDataReader reader, SampleArray keyHolder,
                SampleInfo sampleInfo) {
            
        }

        @Override
        public void instanceSample(ReaderInstanceModel<SampleArray, SampleArrayDataReader> model, SampleArrayDataReader reader, SampleArray data,
                SampleInfo sampleInfo) {
            
        }
        
    };
    
    private final InstanceModelListener<ice.InfusionStatus, ice.InfusionStatusDataReader> infusionStatusListener = new InstanceModelListener<ice.InfusionStatus, ice.InfusionStatusDataReader>() {

        @Override
        public void instanceAlive(ReaderInstanceModel<InfusionStatus, InfusionStatusDataReader> model, InfusionStatusDataReader reader,
                InfusionStatus data, SampleInfo sampleInfo) {
            // log.info("Pump Status:"+infusionStatus);
            // TODO Jeff, what are you doing?
            if (!knownPumps.contains(data.unique_device_identifier)) {
                knownPumps.add(data.unique_device_identifier);
                replaceDataPanels();
            }
        }

        @Override
        public void instanceNotAlive(ReaderInstanceModel<InfusionStatus, InfusionStatusDataReader> model, InfusionStatusDataReader reader,
                InfusionStatus keyHolder, SampleInfo sampleInfo) {
            
        }

        @Override
        public void instanceSample(ReaderInstanceModel<InfusionStatus, InfusionStatusDataReader> model, InfusionStatusDataReader reader,
                InfusionStatus data, SampleInfo sampleInfo) {
            
        }
        
    };
    

    private void replaceDataPanels() {
        DevicePanel[] _dataComponents;
        synchronized (dataComponents) {
            DevicePanelFactory.resolvePanels(knownIdentifiers, dataComponents, knownPumps);
            _dataComponents = dataComponents.toArray(new DevicePanel[0]);
        }
        final DevicePanel[] __dataComponents = _dataComponents;
        
        Runnable r = new Runnable() {
            public void run() {
                data.removeAll();
                if (__dataComponents.length == 0) {
                    data.setLayout(new BorderLayout());
                    data.add(WAITING, BorderLayout.CENTER);
                } else {
                    data.setLayout(new GridLayout(__dataComponents.length, 1));
                    for (DevicePanel d : __dataComponents) {
                        // TODO this is getting to be a mess
                        if(deviceMonitor != null) {
                            d.set(deviceMonitor);
                        }
                        data.add(d);
                    }
                    CompositeDevicePanel.this.validate();
                    CompositeDevicePanel.this.repaint();
                }
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
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

    public void reset() {
        knownIdentifiers.clear();
        knownPumps.clear();
        replaceDataPanels();
    }

    public void stop() {
        
    }
    
    private DeviceDataMonitor deviceMonitor;

    public void setModel(DeviceDataMonitor deviceMonitor) {
        if (null != this.deviceMonitor) {
            this.deviceMonitor.getDeviceIdentityModel().removeListener(deviceIdentityListener);
            this.deviceMonitor.getDeviceConnectivityModel().removeListener(deviceConnectivityListener);
            this.deviceMonitor.getNumericModel().removeListener(numericListener);
            this.deviceMonitor.getSampleArrayModel().removeListener(sampleArrayListener);
            this.deviceMonitor.getInfusionStatusModel().removeListener(infusionStatusListener);
        }
        this.deviceMonitor = deviceMonitor;
        reset();
        if (null != this.deviceMonitor) {
            deviceMonitor.getDeviceIdentityModel().iterateAndAddListener(deviceIdentityListener);
            deviceMonitor.getDeviceConnectivityModel().iterateAndAddListener(deviceConnectivityListener);
            deviceMonitor.getNumericModel().iterateAndAddListener(numericListener);
            deviceMonitor.getSampleArrayModel().iterateAndAddListener(sampleArrayListener);
            deviceMonitor.getInfusionStatusModel().iterateAndAddListener(infusionStatusListener);
        }
    }

    public DeviceDataMonitor getModel() {
        return deviceMonitor;
    }

}
