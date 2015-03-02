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
package org.mdpnp.apps.testapp.rrr;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;

import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.SampleArrayInstanceModel;

import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriberQos;

/**
 * @author Jeff Plourde
 *
 */
public class RapidRespiratoryRate implements Runnable {

    @FXML protected ListView<ice.SampleArray> capnoSources;
    @FXML protected Slider thresholdSlider;
    @FXML protected CheckBox device;
    @FXML protected LineChart<Number, Number> wavePanel;
    @FXML protected Label rrLabel;

//    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private final class RespiratoryRateDevice extends AbstractDevice {

        @Override
        protected String iconResourceName() {
            return "rrr.png";
        }

        public RespiratoryRateDevice(int domainId, EventLoop eventLoop) {
            super(domainId, eventLoop);
            deviceIdentity.manufacturer = "";
            deviceIdentity.model = "Respiratory Rate Calc";
            deviceIdentity.serial_number = "1234";
            AbstractSimulatedDevice.randomUDI(deviceIdentity);
            writeDeviceIdentity();
        }

//        private InstanceHolder<Numeric> rate;

//        void updateRate(float rate) {
//            // TODO clearly a synchronization issue here.
//            // enforce a singular calling thread or synchronize accesses
//            this.rate = numericSample(this.rate, (int) Math.round(rate), rosetta.MDC_CO2_RESP_RATE.VALUE, 
//                    "", rosetta.MDC_DIM_DIMLESS.VALUE, null);
//        }
    }

    private RespiratoryRateDevice rrDevice;
    
    public RapidRespiratoryRate set(final int domainId, final EventLoop eventLoop, final Subscriber subscriber) {
        device.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (device.isSelected()) {
                    if (rrDevice == null) {
                        rrDevice = new RespiratoryRateDevice(domainId, eventLoop);
                        // TODO Make this more elegant
                        List<String> strings = new ArrayList<String>();
                        SubscriberQos qos = new SubscriberQos();
                        subscriber.get_qos(qos);
                        
                        for(int i = 0; i < qos.partition.name.size(); i++) {
                            strings.add((String)qos.partition.name.get(i));
                        }
                        rrDevice.setPartition(strings.toArray(new String[0]));
                    }
                } else {
                    if (rrDevice != null) {
                        rrDevice.shutdown();
                        rrDevice = null;
                    }
                }

            }
            
        });
        return this;
    }
    
    public RapidRespiratoryRate() {

//        capnoSources.addListSelectionListener(new ListSelectionListener() {
//
//            @Override
//            public void valueChanged(ListSelectionEvent e) {
//                ice.SampleArray sa = capnoSources.getSelectedValue();
//                if(null == sa) {
//                    waveformSource = null;
//                    wavePanel.setSource(waveformSource);
//                } else {
//                    waveformSource = new SampleArrayWaveformSource(model.getReader(), sa);
//                    wavePanel.setSource(waveformSource);
//                }
//            }
//            
//        });
//
//        wavePanel.start();
//        executor.scheduleAtFixedRate(new Runnable() {
//            public void run() {
//                RapidRespiratoryRate.this.run();
////                eventLoop.doLater(RapidRespiratoryRate.this);
//            }
//        }, 1000L, 200L, TimeUnit.MILLISECONDS);
    }

//    private SampleArrayInstanceModel model;

    public void setModel(SampleArrayInstanceModel model) {
//        String selectedUdi = null;
//        ice.SampleArray selected = capnoSources.getSelectionModel().getSelectedItem();
//        if (null != selected) {
//            selectedUdi = selected.unique_device_identifier;
//        }

//        capnoSources.setItems(null == model ? FXCollections.emptyObservableList() : model);
//        if (null != selectedUdi && model != null) {
//            for (int i = 0; i < model.getSize(); i++) {
//                if (selectedUdi.equals(model.getElementAt(i).unique_device_identifier)) {
//                    capnoSources.setSelectedValue(model.getElementAt(i), true);
//                }
//            }
//        }
//
//        capnoSources.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        if (this.model != null) {
//            this.model.removeListDataListener(this);
//        }
//        this.model = model;
//        if (this.model != null) {
//            this.model.addListDataListener(this);
//        }
    }


//    private double rr;
    
    protected float min, max;
    protected Long lastCrossing, lastInterval;
    protected Float lastValue = null;
    
    @Override
    public void run() {
//        SampleArrayWaveformSource source = this.waveformSource;
//        if(source != null) {
//            source.iterate(new WaveformIterator() {
//
//                @Override
//                public void begin() {
//                    min = Float.MAX_VALUE;
//                    max = Float.MIN_VALUE;
//                }
//
//                @Override
//                public void sample(long time, float value) {
//                    if(value > max) {
//                        max = value;
//                    } else if(value < min) {
//                        min = value;
//                    }
//                }
//
//                @Override
//                public void end() {
//                    
//                }
//                
//            });
//            final float threshold = max - 1f * thresholdSlider.getValue() / 100f * (max - min);
//            source.iterate(new WaveformIterator() {
//
//                @Override
//                public void begin() {
//                    lastCrossing = null;
//                    lastInterval = null;
//                    lastValue = null;
//                }
//
//                @Override
//                public void sample(long time, float value) {
//                    if(null != lastValue) {
//                        if(value >= threshold && lastValue < threshold) {
//                            if(lastCrossing != null) {
//                                lastInterval = time - lastCrossing;
//                            }
//                            lastCrossing = time;
//                        }
//                        
//                    }
//                    lastValue = value;
//                }
//
//                @Override
//                public void end() {
//                    // ms per breath to breaths/ms
//                    if(null != lastInterval) {
//                        rr = Math.round(1.0 / (lastInterval / 60000.0));
//                    }
////                    System.err.println("lastInterval="+lastInterval+ " lastCrossing="+lastCrossing+ " rr="+rr);
//                    
//                }
//                
//            });
//        } else {
//            rr = 0;
//        }
//        this.rrLabel.setText(""+Math.round(rr));
//        if (rrDevice != null) {
//            rrDevice.updateRate((float) Math.round(rr));
//        }
    }
}
