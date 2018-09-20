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
package org.mdpnp.apps.device;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.FlowPaneBuilder;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import org.mdpnp.apps.fxbeans.InfusionStatusFx;
import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.SampleArrayFx;
import org.mdpnp.apps.testapp.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Plourde
 *
 */
public class CompositeDevicePanel extends BorderPane {
    protected final Label manufacturer = new Label(" ");
    protected final Label model = new Label(" ");
    protected final Label serial_number = new Label(" ");
    protected final Label build = new Label(" ");
    protected final Label operating_system = new Label(" ");
    protected final Label host_name = new Label(" ");

    protected final Label connectionState = new Label(" ");
    protected final Label unique_device_identifier = new Label(" ");
    protected final Label icon = new Label(" ");

    private static final Logger log = LoggerFactory.getLogger(CompositeDevicePanel.class);

    private final BorderPane data = new BorderPane();
    private final Label WAITING = new Label("Waiting for data...");
    private final Collection<DevicePanel> dataComponents = new ArrayList<DevicePanel>();

    private final Set<String> knownIdentifiers = new HashSet<String>();
    private final Set<String> knownPumps = new HashSet<String>();
    
    /**
     * A FlowPane that occupies the top of the BorderPane.  This will be created in the constructor
     * for CompositeDevicePanel, after which its only child will be the device information.  Subclasses
     * can therefore use it to add another child that will appear to the right of the device information.
     */
    private FlowPane topFlowPane;


    public CompositeDevicePanel()  {
        getStylesheets().add(getClass().getResource("demo-guis-javafx.css").toExternalForm());
        getStyleClass().add("composite");
        
        GridPane header = new GridPane();
        header.setHgap(10.0);

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
        
        header.add(new Label("Version"), 0, 5);
        header.add(build, 1, 5);
        
        header.add(new Label("Operating System"), 0, 6);
        header.add(operating_system, 1, 6);
        
        header.add(new Label("Host Name"), 0, 7);
        header.add(host_name, 1, 7);        

        header.add(icon, 2, 0, 1, 8);
        
        /*
         * In order to keep "header" as it was in this code, but to make use of the space
         * on the right hand side, put "header" in a FlowPane, and then add the FlowPane
         * as the top of the BorderPane.  Then we can add another GridPane or any other
         * component to the FlowPane to use the rest of the space in BorderPane.top
         */
        
        topFlowPane=new FlowPane(Orientation.HORIZONTAL);
        topFlowPane.getChildren().add(header);
        
        setTop(topFlowPane);
        data.setCenter(WAITING);
        setCenter(data);
    }
    
    protected void numeric(NumericFx numeric) {
        if(!knownIdentifiers.contains(numeric.getMetric_id())) {
            knownIdentifiers.add(numeric.getMetric_id());
            log.trace("New numeric, new set:" + knownIdentifiers);
            replaceDataPanels();
        }
    }
    private final ListChangeListener<NumericFx> numericListener = new ListChangeListener<NumericFx>() {
        @Override
        public void onChanged(javafx.collections.ListChangeListener.Change<? extends NumericFx> c) {
            while(c.next()) {
                if(c.wasAdded()) {
                    c.getAddedSubList().forEach((fx) -> numeric(fx));
                }
                if(c.wasUpdated()) {
                    c.getList().subList(c.getFrom(), c.getTo()).forEach((fx)->numeric(fx));
                }
            }
        }
        
    };

    protected void sampleArray(SampleArrayFx sampleArray) {
        if (!knownIdentifiers.contains(sampleArray.getMetric_id())) {
            knownIdentifiers.add(sampleArray.getMetric_id());
            log.trace("New SampleArray, new set:" + knownIdentifiers);
            replaceDataPanels();
        }
    }
    
    private final ListChangeListener<SampleArrayFx> sampleArrayListener = new ListChangeListener<SampleArrayFx>() {

        @Override
        public void onChanged(javafx.collections.ListChangeListener.Change<? extends SampleArrayFx> c) {
            while(c.next()) {
                if(c.wasAdded()) {
                    c.getAddedSubList().forEach((fx) -> sampleArray(fx));
                }
                if(c.wasUpdated()) {
                    c.getList().subList(c.getFrom(), c.getTo()).forEach((fx)->sampleArray(fx));
                }
            }
        }
        
    };
    
    protected void infusionStatus(InfusionStatusFx data) {
        if (!knownPumps.contains(data.getUnique_device_identifier())) {
            knownPumps.add(data.getUnique_device_identifier());
            replaceDataPanels();
        }
    }
    
    private final ListChangeListener<InfusionStatusFx> infusionStatusListener = new ListChangeListener<InfusionStatusFx>() {
        @Override
        public void onChanged(javafx.collections.ListChangeListener.Change<? extends InfusionStatusFx> c) {
            while(c.next()) {
                if(c.wasAdded()) {
                    c.getAddedSubList().forEach((fx) -> infusionStatus(fx));
                }
                if(c.wasUpdated()) {
                    c.getList().subList(c.getFrom(), c.getTo()).forEach((fx)->infusionStatus(fx));
                }
            }
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
                    	d.setFlowPane(topFlowPane);
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
        icon.setText("");
        if (null != this.deviceMonitor) {
            manufacturer.textProperty().unbind();
            model.textProperty().unbind();
            serial_number.textProperty().unbind();
            unique_device_identifier.textProperty().set("");
            connectionState.textProperty().unbind();
            operating_system.textProperty().unbind();
            build.textProperty().unbind();
            host_name.textProperty().unbind();
            
            this.deviceMonitor.getNumericModel().removeListener(numericListener);
            this.deviceMonitor.getSampleArrayModel().removeListener(sampleArrayListener);
            this.deviceMonitor.getInfusionStatusModel().removeListener(infusionStatusListener);
        }
        this.deviceMonitor = deviceMonitor;
        reset();
        if (null != this.deviceMonitor) {

            Device d = deviceMonitor.getDevice();

            manufacturer.textProperty().bind(d.manufacturerProperty());
            model.textProperty().bind(d.modelProperty());
            serial_number.textProperty().bind(d.serial_numberProperty());
            unique_device_identifier.textProperty().set(d.getUDI());
            connectionState.textProperty().bind(Bindings.concat(d.connectivityStateProperty()).concat(" (").concat(d.connectivityInfoProperty()).concat(")"));
            operating_system.textProperty().bind(d.operating_systemProperty());
            build.textProperty().bind(d.buildProperty());
            host_name.textProperty().bind(d.hostnameProperty());

            deviceMonitor.getNumericModel().addListener(numericListener);
            deviceMonitor.getNumericModel().forEach((fx)->numeric(fx));
            
            deviceMonitor.getSampleArrayModel().addListener(sampleArrayListener);
            deviceMonitor.getSampleArrayModel().forEach((fx)->sampleArray(fx));
            
            deviceMonitor.getInfusionStatusModel().addListener(infusionStatusListener);
            deviceMonitor.getInfusionStatusModel().forEach((fx)->infusionStatus(fx));
        }
    }

    public DeviceDataMonitor getModel() {
        return deviceMonitor;
    }
    
    public FlowPane getTopFlowPane() {
    	return topFlowPane;
    }
}
