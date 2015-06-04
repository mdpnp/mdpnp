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
package org.mdpnp.guis.javafx;


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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import org.mdpnp.rtiapi.data.DeviceDataMonitor;
import org.mdpnp.rtiapi.data.ReaderInstanceModel;
import org.mdpnp.rtiapi.data.InstanceModelListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.SampleInfo;

/**
 * @author Jeff Plourde
 *
 */
public class CompositeDevicePanel extends BorderPane {
    protected final Label manufacturer = new Label(" ");
    protected final Label model = new Label(" ");
    protected final Label serial_number = new Label(" ");

    protected final Label connectionState = new Label(" ");
    protected final Label unique_device_identifier = new Label(" ");
    protected final Label icon = new Label(" ");

    private static final Logger log = LoggerFactory.getLogger(CompositeDevicePanel.class);

    private final BorderPane data = new BorderPane();
    private final Label WAITING = new Label("Waiting for data...");
    private final Collection<DevicePanel> dataComponents = new ArrayList<DevicePanel>();

    private final Set<String> knownIdentifiers = new HashSet<String>();
    private final Set<String> knownPumps = new HashSet<String>();


    public CompositeDevicePanel()  {
        GridPane header = new GridPane();

        header.add(new Label("Manufacturer"), 0, 0);
        header.add(manufacturer, 1, 0);

        header.add(new Label("Model"), 0, 1);
        header.add(model, 1, 1);

        header.add(new Label("Serial Number"), 0, 2);
        header.add(serial_number, 1, 2);

        header.add(new Label("Unique Device Identifier"), 0, 3);
        header.add(unique_device_identifier, 1, 3);

        header.add(new Label("Connection State"), 0, 4);
        header.add(connectionState, 1, 4);

        header.add(icon, 2, 0, 1, 5);
        setTop(header);
        data.setCenter(WAITING);
        setCenter(data);
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
            Platform.runLater(new Runnable() {
                public void run() {
                    CompositeDevicePanel.this.manufacturer.setText(data.manufacturer);
                    CompositeDevicePanel.this.model.setText(data.model);
                    serial_number.setText(data.serial_number);
                    unique_device_identifier.setText(data.unique_device_identifier);
                    icon.setText("");
                }
            });
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
            Platform.runLater(new Runnable() {
                public void run() {
                    connectionState.setText(data.state.name() + (!"".equals(data.info) ? (" (" + data.info + ")") : ""));
                }
            });
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
            // Destroys unused panels
            DevicePanelFactory.resolvePanels(knownIdentifiers, dataComponents, knownPumps);
            _dataComponents = dataComponents.toArray(new DevicePanel[0]);
        }
        final DevicePanel[] __dataComponents = _dataComponents;
        
        Runnable r = new Runnable() {
            public void run() {
                data.getChildren().clear();
                if (__dataComponents.length == 0) {
                    data.setCenter(WAITING);
                } else {
                    GridPane gridPane = new GridPane();
                    data.setCenter(gridPane);
                    int i = 0;
                    for (DevicePanel d : __dataComponents) {
                        // TODO this is getting to be a mess
                        if(deviceMonitor != null) {
                            d.set(deviceMonitor);
                        }
                        GridPane.setVgrow(d, Priority.ALWAYS);
                        GridPane.setHgrow(d, Priority.ALWAYS);
                        gridPane.add(d, 0, i++);
                    }
                }
            }
        };
        if (Platform.isFxApplicationThread()) {
            r.run();
        } else {
            Platform.runLater(r);
        }

    }

    public void reset() {
        knownIdentifiers.clear();
        knownPumps.clear();
        replaceDataPanels();
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
