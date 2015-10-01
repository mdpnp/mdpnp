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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.util.Callback;
import javafx.util.Duration;

import org.mdpnp.apps.fxbeans.SampleArrayFx;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.*;
import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.DeviceDriverProvider;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.guis.waveform.SampleArrayWaveformSource;
import org.mdpnp.guis.waveform.WaveformCanvas;
import org.mdpnp.guis.waveform.WaveformRenderer;
import org.mdpnp.guis.waveform.WaveformSource.WaveformIterator;
import org.mdpnp.guis.waveform.javafx.JavaFXWaveformCanvas;
import org.mdpnp.guis.waveform.javafx.JavaFXWaveformPane;
import org.mdpnp.rtiapi.data.EventLoop;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriberQos;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * @author Jeff Plourde
 *
 */
public class RapidRespiratoryRate implements Runnable {

    @FXML protected ListView<SampleArrayFx> capnoSources;
    @FXML protected Slider thresholdSlider;
    @FXML protected CheckBox device;
    @FXML protected JavaFXWaveformPane wavePanel;
    @FXML protected Label rrLabel;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private final class RespiratoryRateDevice extends AbstractDevice {

        @Override
        protected String iconResourceName() {
            return "rrr.png";
        }

        public RespiratoryRateDevice(Subscriber subscriber, Publisher publisher, EventLoop eventLoop) {
            super(subscriber, publisher, eventLoop);
            deviceIdentity.manufacturer = "";
            deviceIdentity.model = "Respiratory Rate Calc";
            deviceIdentity.serial_number = "1234";
            AbstractSimulatedDevice.randomUDI(deviceIdentity);
            writeDeviceIdentity();
        }

        private InstanceHolder<ice.Numeric> rate;

        void updateRate(float rate) {
            DeviceClock.Reading sampleTime = clock.instant();
            // TODO clearly a synchronization issue here.
            // enforce a singular calling thread or synchronize accesses
            this.rate = numericSample(this.rate, (int) Math.round(rate), rosetta.MDC_CO2_RESP_RATE.VALUE, 
                    rosetta.MDC_CO2_RESP_RATE.VALUE, 0, rosetta.MDC_DIM_DIMLESS.VALUE, sampleTime);
        }
    }
    protected final DeviceClock clock = new DeviceClock.WallClock();
    private DeviceDriverProvider.DeviceAdapter rrDevice;

    public RapidRespiratoryRate set(final ApplicationContext parentContext, final int domainId, final EventLoop eventLoop, final Subscriber subscriber, final DeviceListModel deviceListModel) {
//        ((NumberAxis)wavePanel.getXAxis()).forceZeroInRangeProperty().set(false);
//        ((NumberAxis)wavePanel.getYAxis()).forceZeroInRangeProperty().set(false);
//        ((NumberAxis)wavePanel.getYAxis()).autoRangingProperty().set(true);
//        ((NumberAxis)wavePanel.getXAxis()).autoRangingProperty().set(true);
        capnoSources.setCellFactory(new Callback<ListView<SampleArrayFx>,ListCell<SampleArrayFx>>() {

            @Override
            public ListCell<SampleArrayFx> call(ListView<SampleArrayFx> param) {
                return new SampleArrayFxListCell(deviceListModel);
            }
            
        });
        canvas = new JavaFXWaveformCanvas(wavePanel);
        waveformRender = new Timeline(new KeyFrame(Duration.millis(100), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
//                wavePanel.getXAxis().setAutoRanging(false);
                long tm = System.currentTimeMillis();
                
                renderer.render(source, canvas, tm-12000L, tm-2000L);
                
//                ((NumberAxis)wavePanel.getXAxis()).setLowerBound(tm-12000L);
//                ((NumberAxis)wavePanel.getXAxis()).setUpperBound(tm-2000L);
            }
            
        }));
        waveformRender.setCycleCount(Timeline.INDEFINITE);
        waveformRender.play();
        
        
        device.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (device.isSelected()) {
                    if (rrDevice == null) {

                        DeviceDriverProvider.SpringLoadedDriver df = new DeviceDriverProvider.SpringLoadedDriver() {
                            @Override
                            public DeviceType getDeviceType() {
                                return new DeviceType(ice.ConnectionType.Simulated, "Simulated", "RespiratoryRate", "RespiratoryRate", 1);
                            }

                            @Override
                            public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
                                EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
                                Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
                                Publisher publisher = context.getBean("publisher", Publisher.class);
                                return new RespiratoryRateDevice(subscriber, publisher, eventLoop);
                            }
                        };

                        try {
                            rrDevice = df.create((AbstractApplicationContext) parentContext);

                            // TODO Make this more elegant
                            List<String> strings = new ArrayList<String>();
                            SubscriberQos qos = new SubscriberQos();
                            subscriber.get_qos(qos);

                            for (int i = 0; i < qos.partition.name.size(); i++) {
                                strings.add((String) qos.partition.name.get(i));
                            }

                            rrDevice.setPartition(strings.toArray(new String[0]));
                        }
                        catch(Exception ex) {
                            throw new RuntimeException("Failed to create a driver", ex);
                        }

                    }
                } else {
                    if (rrDevice != null) {
                        rrDevice.stop();
                        rrDevice = null;
                    }
                }

            }
            
        });
        capnoSources.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SampleArrayFx>() {

            @Override
            public void changed(ObservableValue<? extends SampleArrayFx> observable, SampleArrayFx oldValue, SampleArrayFx newValue) {

                SampleArrayFxList model = RapidRespiratoryRate.this.model;
                if (model != null && newValue != null) {
                    ice.SampleArray keyHolder = new ice.SampleArray();
                    model.getReader().get_key_value(keyHolder, newValue.getHandle());
                    source = new SampleArrayWaveformSource(model.getReader(), keyHolder);
                }
//                wavePanel.getData().clear();
//                Series<Number,Number> series = data.getSeries(newValue.getHandle());
//                wavePanel.getData().add(series);

            }

        });
   
        return this;
    }

    public RapidRespiratoryRate() {
//        wavePanel.start();
        executor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                RapidRespiratoryRate.this.run();
//                eventLoop.doLater(RapidRespiratoryRate.this);
            }
        }, 1000L, 200L, TimeUnit.MILLISECONDS);
    }
    private SampleArrayFxList model;
    private ObservableList<SampleArrayFx> filteredModel;
    
    private SampleArrayWaveformSource source;
    private final WaveformRenderer renderer = new WaveformRenderer();
    private WaveformCanvas canvas;
    private Timeline waveformRender;
    
    public void start(SampleArrayFxList sampleArrayList) {
        this.model = sampleArrayList;
        filteredModel = new FilteredList<>(sampleArrayList, new Predicate<SampleArrayFx>() {
            @Override
            public boolean test(SampleArrayFx t) {
                return rosetta.MDC_AWAY_CO2.VALUE.equals(t.getMetric_id()) || rosetta.MDC_IMPED_TTHOR.VALUE.equals(t.getMetric_id());
            }
            
        });
        capnoSources.setItems(filteredModel);
    }
    


    private double rr;
    
    protected float min, max;
    protected Long lastCrossing, lastInterval;
    protected Float lastValue = null;
    
    @Override
    public void run() {
        SampleArrayWaveformSource source = this.source;
        if(source != null) {
            source.iterate(new WaveformIterator() {

                @Override
                public void begin() {
                    min = Float.MAX_VALUE;
                    max = Float.MIN_VALUE;
                }

                @Override
                public void sample(long time, float value) {
                    if(value > max) {
                        max = value;
                    } else if(value < min) {
                        min = value;
                    }
                }

                @Override
                public void end() {
                    
                }
                
            });
            final double threshold = max - 1f * thresholdSlider.getValue() / 100f * (max - min);
            source.iterate(new WaveformIterator() {

                @Override
                public void begin() {
                    lastCrossing = null;
                    lastInterval = null;
                    lastValue = null;
                }

                @Override
                public void sample(long time, float value) {
                    if(null != lastValue) {
                        if(value >= threshold && lastValue < threshold) {
                            if(lastCrossing != null) {
                                lastInterval = time - lastCrossing;
                            }
                            lastCrossing = time;
                        }
                        
                    }
                    lastValue = value;
                }

                @Override
                public void end() {
                    // ms per breath to breaths/ms
                    if(null != lastInterval) {
                        rr = Math.round(1.0 / (lastInterval / 60000.0));
                    }
//                    System.err.println("lastInterval="+lastInterval+ " lastCrossing="+lastCrossing+ " rr="+rr);
                    
                }
                
            });
        } else {
            rr = 0;
        }
        Platform.runLater(updateLabel);
        //.rrLabel.setText(""+Math.round(rr));
        if (rrDevice != null) {
            RespiratoryRateDevice impl = (RespiratoryRateDevice)rrDevice.getDevice();
            impl.updateRate((float) Math.round(rr));
        }
    }
    protected Runnable updateLabel = new Runnable() {
        public void run() {
            rrLabel.setText(""+Math.round(rr));
        }
    };
    public void stop() {
        model.stop();
        waveformRender.stop();
        executor.shutdownNow();
    }
}
