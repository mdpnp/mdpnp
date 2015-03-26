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
package org.mdpnp.apps.testapp.xray;

import ice.Numeric;
import ice.NumericDataReader;
import ice.SampleArray;
import ice.SampleArrayDataReader;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.Duration;

import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.MyNumeric;
import org.mdpnp.apps.testapp.MyNumericItems;
import org.mdpnp.apps.testapp.MyNumericListCell;
import org.mdpnp.apps.testapp.MySampleArray;
import org.mdpnp.apps.testapp.MySampleArrayListCell;
import org.mdpnp.guis.waveform.SampleArrayWaveformSource;
import org.mdpnp.guis.waveform.WaveformCanvas;
import org.mdpnp.guis.waveform.WaveformRenderer;
import org.mdpnp.guis.waveform.javafx.JavaFXWaveformCanvas;
import org.mdpnp.guis.waveform.javafx.JavaFXWaveformPane;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.InstanceModelListener;
import org.mdpnp.rtiapi.data.NumericInstanceModel;
import org.mdpnp.rtiapi.data.NumericInstanceModelImpl;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.mdpnp.rtiapi.data.SampleArrayInstanceModel;
import org.mdpnp.rtiapi.data.SampleArrayInstanceModelImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sarxos.webcam.Webcam;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.Subscriber;

/**
 * @author Jeff Plourde
 *
 */
public class XRayVentPanel {
    @FXML protected FramePanel cameraPanel;
    @FXML protected GridPane main;
    private CameraComboBoxModel cameraModel;

    @FXML protected ComboBox<Webcam> cameraBox;

    @FXML protected JavaFXWaveformPane waveformPanel;
    private final WaveformRenderer renderer = new WaveformRenderer();
    private WaveformCanvas canvas;

    @FXML protected ListView<MyNumeric> deviceList;

    public enum Strategy {
        Manual, Automatic
    }

    public enum TargetTime {
        EndInspiration, EndExpiration
    }
    
    @FXML protected ToggleGroup strategy, targetTime;
    @FXML protected RadioButton manual, automatic, endInspiration, endExpiration;
    @FXML protected Button imageButton, resetButton;
    @FXML protected Label camText;
    @FXML protected ImageView camImage;

    private static final Logger log = LoggerFactory.getLogger(XRayVentPanel.class);

    private NumericInstanceModel startOfBreathModel, deviceNumericModel;
    private SampleArrayInstanceModel sampleArrayModel;
    private SampleArrayWaveformSource source;

    private InstanceModelListener<ice.Numeric, ice.NumericDataReader> numericListener = new InstanceModelListener<ice.Numeric, ice.NumericDataReader>() {
        public void instanceAlive(org.mdpnp.rtiapi.data.InstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric data,
                SampleInfo sampleInfo) {

        };

        public void instanceNotAlive(org.mdpnp.rtiapi.data.InstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader,
                Numeric keyHolder, SampleInfo sampleInfo) {

        };

        public void instanceSample(org.mdpnp.rtiapi.data.InstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric data,
                SampleInfo sampleInfo) {
            if (sampleInfo.valid_data) {
                long previousPeriod = period;
                if (ice.MDC_TIME_PD_INSPIRATORY.VALUE.equals(data.metric_id)) {
                    inspiratoryTime = (long) (1000.0 * data.value);
                } else if (ice.MDC_VENT_TIME_PD_PPV.VALUE.equals(data.metric_id)) {
                    period = (long) (60000.0 / data.value);
                    if (period != previousPeriod) {
                        log.debug("FrequencyIPPV=" + data.value + " period=" + period);
                    }
                } else if (ice.MDC_START_INSPIRATORY_CYCLE.VALUE.equals(data.metric_id)) {
                    log.trace("START_INSPIRATORY_CYCLE");
                    Strategy strategy = (Strategy) XRayVentPanel.this.strategy.getSelectedToggle().getUserData();
                    TargetTime targetTime = (TargetTime) XRayVentPanel.this.targetTime.getSelectedToggle().getUserData();

                    switch (strategy) {
                    case Automatic:
                        autoSync(targetTime);
                        break;
                    case Manual:
                        break;
                    }
                }
            }
        };
    };

    private InstanceModelListener<ice.SampleArray, ice.SampleArrayDataReader> sampleArrayListener = new InstanceModelListener<ice.SampleArray, ice.SampleArrayDataReader>() {
        public void instanceAlive(org.mdpnp.rtiapi.data.InstanceModel<SampleArray, SampleArrayDataReader> model, SampleArrayDataReader reader,
                SampleArray data, SampleInfo sampleInfo) {
            if (sampleInfo.valid_data) {
                if (rosetta.MDC_FLOW_AWAY.VALUE.equals(data.metric_id)) {
                    XRayVentPanel.this.source = new SampleArrayWaveformSource(reader, data);
                }
            }
        };

        public void instanceNotAlive(org.mdpnp.rtiapi.data.InstanceModel<SampleArray, SampleArrayDataReader> model, SampleArrayDataReader reader,
                SampleArray keyHolder, SampleInfo sampleInfo) {

        };

        public void instanceSample(org.mdpnp.rtiapi.data.InstanceModel<SampleArray, SampleArrayDataReader> model, SampleArrayDataReader reader,
                SampleArray data, SampleInfo sampleInfo) {

        };
    };

    public void changeSource(String source, Subscriber subscriber, EventLoop eventLoop) {
        this.source = null;
        deviceNumericModel.stop();
        sampleArrayModel.stop();

        StringSeq params = new StringSeq();
        params.add("'" + rosetta.MDC_FLOW_AWAY.VALUE + "'");
        sampleArrayModel.addListener(sampleArrayListener);
        sampleArrayModel.start(subscriber, eventLoop, "metric_id = %0", params, QosProfiles.ice_library, QosProfiles.waveform_data);
        params = new StringSeq();
        params.add("'" + source + "'");
        deviceNumericModel.addListener(numericListener);
        deviceNumericModel.start(subscriber, eventLoop, "ice_id = %0", params, QosProfiles.ice_library, QosProfiles.numeric_data);
        log.trace("new source is " + source);
    }

    private boolean imageButtonDown = false;

    public XRayVentPanel set(final Subscriber subscriber, final EventLoop eventLoop, final DeviceListModel deviceListModel) {
        canvas = new JavaFXWaveformCanvas(waveformPanel);
        
        cameraPanel.set(executorNonCritical);
        manual.setUserData(Strategy.Manual);
        automatic.setUserData(Strategy.Automatic);
        endInspiration.setUserData(TargetTime.EndInspiration);
        endExpiration.setUserData(TargetTime.EndExpiration);
        
        
        cameraModel = new CameraComboBoxModel();
        cameraBox.setItems(cameraModel.getItems());
        
        startOfBreathModel = new NumericInstanceModelImpl(ice.NumericTopic.VALUE);
        sampleArrayModel = new SampleArrayInstanceModelImpl(ice.SampleArrayTopic.VALUE);
        deviceNumericModel = new NumericInstanceModelImpl(ice.NumericTopic.VALUE);

        deviceList.setCellFactory(new Callback<ListView<MyNumeric>,ListCell<MyNumeric>>() {

            @Override
            public ListCell<MyNumeric> call(ListView<MyNumeric> param) {
                return new MyNumericListCell(deviceListModel);
            }
            
        });
        
        deviceList.setItems(new MyNumericItems().setModel(startOfBreathModel).getItems());
        
        cameraBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Webcam>() {

            @Override
            public void changed(ObservableValue<? extends Webcam> observable, Webcam oldValue, Webcam newValue) {
                executorNonCritical.schedule(new Runnable() {
                    public void run() {
                      cameraPanel.setWebcam((Webcam) cameraBox.getSelectionModel().getSelectedItem());                        
                    }
                }, 0L, TimeUnit.MILLISECONDS);
            }
            
        });
        

        imageButton.pressedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean imageButtonDown) {
                XRayVentPanel.this.imageButtonDown = imageButtonDown;
                if (imageButtonDown && Strategy.Manual.equals(XRayVentPanel.this.strategy.getSelectedToggle().getUserData())) {
                    noSync();
                }
            }
            
        });
        deviceList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MyNumeric>() {

            @Override
            public void changed(ObservableValue<? extends MyNumeric> observable, MyNumeric oldValue, MyNumeric newValue) {
                changeSource(newValue.getIce_id(), subscriber, eventLoop);
            }
        });
        Timeline waveformRender = new Timeline(new KeyFrame(Duration.millis(100), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                long tm = System.currentTimeMillis();
                SampleArrayWaveformSource source = XRayVentPanel.this.source;
                if(null != source) {
                    renderer.render(source, canvas, tm-12000L, tm-2000L);
                }
            }
            
        }));
        waveformRender.setCycleCount(Timeline.INDEFINITE);
        waveformRender.play();
        return this;
    }

    public XRayVentPanel() {

    }

    @FXML public void clickReset(ActionEvent evt) {
        cameraPanel.unfreeze();
    }
    
    public void stop() {
        Scene scene = main.getScene();
        if(null != scene) {
            scene.removeEventFilter(KeyEvent.KEY_PRESSED, keyEventHandler);
            scene.removeEventFilter(KeyEvent.KEY_RELEASED, keyEventHandler);
        }
        executorNonCritical.schedule(new Runnable() {
            public void run() {
                cameraModel.stop();
                cameraPanel.stop();

            }
        }, 0L, TimeUnit.MILLISECONDS);
        startOfBreathModel.stop();
        sampleArrayModel.stop();
        deviceNumericModel.stop();
        executorNonCritical.shutdownNow();
        executorCritical.shutdownNow();
    }
    
    private EventHandler<KeyEvent> keyEventHandler;

    public void start(Subscriber subscriber, EventLoop eventLoop) {

        deviceList.getSelectionModel().clearSelection();

        StringSeq params = new StringSeq();
        params.add("'" + ice.MDC_START_INSPIRATORY_CYCLE.VALUE + "'");
//        params.add("'" + rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE + "'");
        startOfBreathModel.start(subscriber, eventLoop, "metric_id = %0", params, QosProfiles.ice_library, QosProfiles.numeric_data);

        executorNonCritical.schedule(new Runnable() {
            public void run() {
                cameraModel.start();
//                waveformPanel.start();
                cameraPanel.start();
            }
        }, 0L, TimeUnit.MILLISECONDS);
        
        keyEventHandler = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch(event.getCode()) {
                case SPACE:
                    if(KeyEvent.KEY_PRESSED.equals(event.getEventType())) {
                        System.err.println("SPACE PRESSED");
                        imageButton.arm();
                    } else if(KeyEvent.KEY_RELEASED.equals(event.getEventType())) {
                        System.err.println("SPACE RELEASED");
                        imageButton.fire();
                    }
                    event.consume();
                    break;
                case ESCAPE:
                    if(KeyEvent.KEY_RELEASED.equals(event.getEventType())) {
                        resetButton.fire();
                        event.consume();
                    }
                    break;
                default:
                    break;
                }
            }
        };
        main.getScene().addEventFilter(KeyEvent.KEY_PRESSED, keyEventHandler);
        main.getScene().addEventFilter(KeyEvent.KEY_RELEASED, keyEventHandler);        
    }

    protected long inspiratoryTime;
    protected long period;
    protected ScheduledExecutorService executorCritical = Executors.newSingleThreadScheduledExecutor();
    protected ScheduledExecutorService executorNonCritical = Executors.newSingleThreadScheduledExecutor();

    private final Callable<Void> freezeCallable = new Callable<Void>() {
        @Override
        public Void call() throws Exception {
            if (imageButtonDown) {
                cameraPanel.freeze((long)(1000.0*exposureTime.getValue()));
            }
            return null;
        }
    };

    @FXML protected Slider exposureTime;

    private final void noSync() {
        cameraPanel.freeze((long)(1000.0*exposureTime.getValue()));

    }

    private final void autoSync(TargetTime targetTime) {

        switch (targetTime) {
        case EndExpiration:
            // JP Apr 29, 2013
            // Luckily this works quickly enough to respond to *this* start
            // breath
            // event. A more robust implementation would probably trigger just
            // before
            // the *next* start breath ... or a timeout of (this.period-50L) on
            // the following line
            executorCritical.schedule(freezeCallable, 0L, TimeUnit.MILLISECONDS);
            break;
        case EndInspiration:
            executorCritical.schedule(freezeCallable, inspiratoryTime - 100L, TimeUnit.MILLISECONDS);
            break;
        }
    }
}
