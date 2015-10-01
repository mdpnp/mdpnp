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

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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

import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFx;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.NumericFxListCell;
import org.mdpnp.guis.waveform.SampleArrayWaveformSource;
import org.mdpnp.guis.waveform.javafx.JavaFXWaveformPane;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sarxos.webcam.Webcam;
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

    @FXML protected ListView<NumericFx> deviceList;

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
    
    private NumericFxList numericList;
    private SampleArrayFxList sampleArrayList;
    private ObservableList<NumericFx> startOfBreathModel, deviceNumericModel;
    private ObservableList<SampleArrayFx> deviceFlowModel;
    
    private SampleArrayWaveformSource source;
    
    protected void add(SampleArrayFx data) {
        XRayVentPanel.this.source = new SampleArrayWaveformSource(sampleArrayList.getReader(), data.getHandle());
        waveformPanel.setSource(source);
        waveformPanel.start();
    }
    
    protected void remove(SampleArrayFx data) {
        XRayVentPanel.this.source = new SampleArrayWaveformSource(sampleArrayList.getReader(), data.getHandle());
        waveformPanel.setSource(null);
        waveformPanel.stop();
    }
    
    private ListChangeListener<SampleArrayFx> sampleArrayListener = new ListChangeListener<SampleArrayFx>() {
        @Override
        public void onChanged(javafx.collections.ListChangeListener.Change<? extends SampleArrayFx> c) {
            while(c.next()) {
                if(c.wasAdded()) c.getAddedSubList().forEach( (fx) -> add(fx));
                if(c.wasRemoved()) c.getRemoved().forEach( (fx) -> remove(fx));
            }
        }
    };
    
    private final ChangeListener<Number> inspiratoryTimeListener = new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            inspiratoryTime = (long) (1000.0 * newValue.doubleValue());
        }
    };
    
    private final ChangeListener<Number> periodListener = new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            period = (long) (60000.0 / newValue.doubleValue());
            log.debug("FrequencyIPPV=" + newValue + " period=" + period);
        }
    };
    
    private final ChangeListener<Date> startOfBreathListener = new ChangeListener<Date>() {

        @Override
        public void changed(ObservableValue<? extends Date> observable, Date oldValue, Date newValue) {
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
        
    };
    
    private void add(NumericFx fx) {
        if(ice.MDC_TIME_PD_INSPIRATORY.VALUE.equals(fx.getMetric_id())) {
            fx.valueProperty().addListener(inspiratoryTimeListener);
        } else if(ice.MDC_VENT_TIME_PD_PPV.VALUE.equals(fx.getMetric_id())) {
            fx.valueProperty().addListener(periodListener);
        } else if(ice.MDC_START_INSPIRATORY_CYCLE.VALUE.equals(fx.getMetric_id())) {
            fx.source_timestampProperty().addListener(startOfBreathListener);
        }
    }
    
    private void remove(NumericFx fx) {
        if(ice.MDC_TIME_PD_INSPIRATORY.VALUE.equals(fx.getMetric_id())) {
            fx.valueProperty().removeListener(inspiratoryTimeListener);
        } else if(ice.MDC_VENT_TIME_PD_PPV.VALUE.equals(fx.getMetric_id())) {
            fx.valueProperty().removeListener(periodListener);
        } else if(ice.MDC_START_INSPIRATORY_CYCLE.VALUE.equals(fx.getMetric_id())) {
            fx.source_timestampProperty().removeListener(startOfBreathListener);
        }
    }
    
    private ListChangeListener<NumericFx> numericListener = new ListChangeListener<NumericFx>() {
        @Override
        public void onChanged(javafx.collections.ListChangeListener.Change<? extends NumericFx> c) {
            while(c.next()) {
                if(c.wasAdded()) c.getAddedSubList().forEach((fx) -> add(fx));
                if(c.wasRemoved()) c.getRemoved().forEach((fx) -> remove(fx));
            }
        };
    };
    
    public void changeSource(final String source) {
        this.source = null;
        
        if(null != deviceNumericModel) {
            deviceNumericModel.removeListener(numericListener);
        }
        deviceNumericModel = new FilteredList<>(numericList, new Predicate<NumericFx>() {
            @Override
            public boolean test(NumericFx t) {
                return source.equals(t.getUnique_device_identifier());
            }
        });
        deviceNumericModel.addListener(numericListener);
        deviceNumericModel.forEach((fx)->add(fx));

        if(null != deviceFlowModel) {
            deviceFlowModel.removeListener(sampleArrayListener);
        }

        deviceFlowModel = new FilteredList<>(sampleArrayList, new Predicate<SampleArrayFx>() {
            @Override
            public boolean test(SampleArrayFx t) {
                return rosetta.MDC_FLOW_AWAY.VALUE.equals(t.getMetric_id()) && source.equals(t.getUnique_device_identifier());
            }
        });

        
        deviceFlowModel.addListener(sampleArrayListener);
        deviceFlowModel.forEach( (fx) -> add(fx));
        

        log.trace("new source is " + source);
        
    }

    private boolean imageButtonDown = false;

    public XRayVentPanel set(final DeviceListModel deviceListModel, final NumericFxList numericList, final SampleArrayFxList sampleArrayList) {
        this.numericList = numericList;
        startOfBreathModel = new FilteredList<>(numericList, new Predicate<NumericFx>() {
            @Override
            public boolean test(NumericFx t) {
                return ice.MDC_START_INSPIRATORY_CYCLE.VALUE.equals(t.getMetric_id());
            }
        });
        cameraPanel.set(executorNonCritical);
        manual.setUserData(Strategy.Manual);
        automatic.setUserData(Strategy.Automatic);
        endInspiration.setUserData(TargetTime.EndInspiration);
        endExpiration.setUserData(TargetTime.EndExpiration);
        
        
        cameraModel = new CameraComboBoxModel();
        cameraBox.setItems(cameraModel.getItems());
        
        
        this.sampleArrayList = sampleArrayList;

        deviceList.setCellFactory(new Callback<ListView<NumericFx>,ListCell<NumericFx>>() {

            @Override
            public ListCell<NumericFx> call(ListView<NumericFx> param) {
                return new NumericFxListCell(deviceListModel);
            }
            
        });
        
        deviceList.setItems(startOfBreathModel);
        
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
        deviceList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<NumericFx>() {

            @Override
            public void changed(ObservableValue<? extends NumericFx> observable, NumericFx oldValue, NumericFx newValue) {
                if(newValue != null) {
                    changeSource(newValue.getUnique_device_identifier());
                }
            }
        });
        return this;
    }

    public XRayVentPanel() {

    }

    @FXML public void clickReset(ActionEvent evt) {
        cameraPanel.unfreeze();
    }
    
    public void stop() {
        Scene scene = main.getScene();
        waveformPanel.stop();
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
        
    }
    
    public void destroy() {
        executorNonCritical.shutdown();
        executorCritical.shutdown();
    }
    
    private EventHandler<KeyEvent> keyEventHandler;

    public void start(Subscriber subscriber, EventLoop eventLoop) {

        deviceList.getSelectionModel().clearSelection();


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
        waveformPanel.start();
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
