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
package org.mdpnp.apps.testapp.sim;

import java.text.NumberFormat;
import java.util.Arrays;

import ice.GlobalSimulationObjective;
import ice.GlobalSimulationObjectiveDataReader;
import ice.GlobalSimulationObjectiveDataWriter;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import javafx.util.converter.NumberStringConverter;

import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.EventLoop.ConditionHandler;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.mdpnp.rtiapi.data.TopicUtil;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.InstanceHandleSeq;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.OwnershipQosPolicy;
import com.rti.dds.infrastructure.OwnershipStrengthQosPolicy;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.DataWriterQos;
import com.rti.dds.publication.Publisher;
import com.rti.dds.publication.PublisherQos;
import com.rti.dds.publication.builtin.PublicationBuiltinTopicData;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriptionMatchedStatus;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author Jeff Plourde
 *
 */
public class SimControl implements InitializingBean
{
    private static final Logger log = LoggerFactory.getLogger(SimControl.class);

    @FXML protected GridPane main;
    
    static final class NumericValue {
        public final String name, metricId;
        public final float lowerBound, upperBound, initialValue, increment;

        public NumericValue(final String name, final String metricId, final float lowerBound, final float upperBound, final float initialValue,
                final float increment) {
            this.name = name;
            this.metricId = metricId;
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.initialValue = initialValue;
            this.increment = increment;
        }
    }

    private static class UIControl {
        final Slider valueSlider;
        final Label  stepSize;
        final Label  maxJitter;
        final Label  label;
        final Label  currentValue;
        final Label  enableJitter;
        final CheckBox jitter;

        final ice.GlobalSimulationObjective objective;
        final InstanceHandle_t handle;
        final ice.GlobalSimulationObjectiveDataWriter writer;


        static class NumberListCell extends ListCell<Number> {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                super.setText((item==null? "":item.toString() + "%"));
            }
        };


        public UIControl(final NumericValue numericValue, final ice.GlobalSimulationObjectiveDataWriter writer) {

            this.writer = writer;

            final NumberFormat numberFormat = NumberFormat.getNumberInstance();
            numberFormat.setMaximumFractionDigits(2);
            numberFormat.setMinimumFractionDigits(2);
            numberFormat.setMinimumIntegerDigits(1);
            objective = (GlobalSimulationObjective) ice.GlobalSimulationObjective.create();
            objective.metric_id = numericValue.metricId;
            objective.value = numericValue.initialValue;
            objective.jitterStep = 0;
            objective.ceil = numericValue.upperBound;
            objective.floor = numericValue.lowerBound;

            handle = writer.register_instance(objective);

            valueSlider = new Slider(numericValue.lowerBound, numericValue.upperBound, objective.value);
            valueSlider.setMajorTickUnit(numericValue.increment);
            valueSlider.setMinorTickCount(0);
            valueSlider.setShowTickLabels(true);
            valueSlider.setShowTickMarks(true);
            valueSlider.setSnapToTicks(true);

            label = new Label(numericValue.name);
            label.setTextAlignment(TextAlignment.RIGHT);
            currentValue = new Label("" + valueSlider.getValue());

            boolean jitterOn = objective.jitterStep!=0;

            jitter  = new CheckBox();
            jitter.setSelected(false);
            jitter.setSelected(jitterOn);
            enableJitter = new Label("Jitter:");
            enableJitter.setGraphic(jitter);
            enableJitter.setContentDisplay(ContentDisplay.RIGHT);

            stepSize = makeNumberCombo("Step (%):",
                                       new Double[]{0.1, 0.2, 0.5, 0.75, 1.0, 2.0, 5.0, 10.0, 20.0}, 2,
                                       new ChangeListener<Number>() {
                                           @Override
                                           public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                                               float jitterStepPct = newValue.floatValue();
                                               Number jitterMaxPct = (Number) ((ComboBox) maxJitter.getGraphic()).getValue();
                                               recalculate(numericValue, jitterMaxPct.floatValue(), jitterStepPct);
                                               publishObjective();
                                           }
                                       },
                                       !jitterOn);

            maxJitter = makeNumberCombo("Max (%):",
                                        new Integer[]{1, 3, 5, 10, 15, 20, 50}, 10,
                                        new ChangeListener<Number>() {
                                            @Override
                                            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                                                float jitterMaxPct = newValue.floatValue();
                                                Number jitterStepPct = (Number) ((ComboBox) stepSize.getGraphic()).getValue();
                                                recalculate(numericValue, jitterMaxPct, jitterStepPct.floatValue());
                                                publishObjective();
                                            }
                                        },
                                        !jitterOn);



            currentValue.textProperty().bindBidirectional(valueSlider.valueProperty(), new NumberStringConverter(numberFormat));
            valueSlider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    objective.value = newValue.floatValue();
                    boolean jitterOn = jitter.isSelected();
                    recalculate(numericValue, jitterOn);
                    publishObjective();
                }
            });

            jitter.selectedProperty().addListener(new ChangeListener<Boolean>() {
                public void changed(ObservableValue<? extends Boolean> ov,
                                    Boolean oldVal, Boolean newVal) {

                    stepSize.setDisable(!newVal);
                    maxJitter.setDisable(!newVal);

                    recalculate(numericValue, newVal);
                    publishObjective();
                }
            });

            // publish the current state out.
            //

            if(jitterOn) {
                Number jitterStepPct = (Number) ((ComboBox) stepSize.getGraphic()).getValue();
                Number jitterMaxPct  = (Number) ((ComboBox) maxJitter.getGraphic()).getValue();
                recalculate(numericValue, jitterMaxPct.floatValue(), jitterStepPct.floatValue());
            }

            publishObjective();
        }

        private void publishObjective() {
            log.debug("Publish objective changes:" + objective);
            writer.write(objective, handle);
        }

        private boolean recalculate(final NumericValue numericValue, boolean jitterOn) {
            if(!jitterOn) {
                objective.jitterStep = 0;
                objective.ceil = numericValue.upperBound;
                objective.floor = numericValue.lowerBound;
                return true;
            }
            else {
                Number jitterStepPct = (Number)((ComboBox) stepSize.getGraphic()).getValue();
                Number jitterMaxPct  = (Number)((ComboBox) maxJitter.getGraphic()).getValue();
                return recalculate(numericValue, jitterMaxPct.floatValue(), jitterStepPct.floatValue());
            }
        }

        private boolean recalculate(final NumericValue numericValue, float jitterMaxPct, float jitterStepPct) {

            if(jitterMaxPct<=jitterStepPct)
                return false;

            double floor = objective.value - (objective.value*jitterMaxPct)/100.0;
            floor = floor<numericValue.lowerBound ? numericValue.lowerBound : floor;
            double ceil = objective.value + (objective.value*jitterMaxPct)/100.0;
            ceil = ceil>numericValue.upperBound ? numericValue.upperBound : ceil;

            objective.ceil=(float)ceil;
            objective.floor=(float)floor;
            objective.jitterStep = (float)((objective.value*jitterStepPct)/100.0);

            return true;
        }

        void close() {
            writer.unregister_instance(objective, handle);
        }

        void addTo(GridPane main, int i)
        {
            main.add(label,                  0, i);
            main.add(valueSlider,            1, i);
            main.add(currentValue,           2, i);
            main.add(enableJitter,           3, i);
            main.add(stepSize,               4, i);
            main.add(maxJitter,              5, i);
        }


        Label makeNumberCombo(String lbl, Number[] values, int sel, ChangeListener<Number> callback, boolean initState) {

            Callback<ListView<Number>,ListCell<Number>> fac = new Callback<ListView<Number>,ListCell<Number>>() {
                @Override
                public ListCell<Number> call(ListView<Number> param) {
                    return new NumberListCell();
                }
            };

            ObservableList<Number> v = FXCollections.observableArrayList();
            v.addAll(Arrays.asList(values));
            ComboBox<Number> comboBox  = new ComboBox<>();
            comboBox.setItems(v);
            comboBox.setButtonCell(new NumberListCell());
            comboBox.setCellFactory(fac);
            comboBox.setValue(sel);

            //comboBox.setPromptText("Max");

            comboBox.valueProperty().addListener(callback);

            Label label = new Label(lbl);
            label.setDisable(initState);
            label.setGraphic(comboBox);
            label.setContentDisplay(ContentDisplay.RIGHT);
            return label;
        }
    }

    private static final NumericValue[] numericValues = new NumericValue[] {
            new NumericValue("SpO\u2082", rosetta.MDC_PULS_OXIM_SAT_O2.VALUE, 0, 100, 98, 1),
            new NumericValue("Any Pulse", rosetta.MDC_PULS_RATE.VALUE, 10, 360, 60, 1),
//            new NumericValue("Pulse Rate (SpO\u2082)", rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, 10, 360, 60, 50),
//            new NumericValue("Heart Rate (ECG)", rosetta.MDC_ECG_HEART_RATE.VALUE, 10, 360, 60, 50),
            new NumericValue("etCO\u2082", rosetta.MDC_AWAY_CO2_ET.VALUE, 0, 140, 30, 1),
            // The Fluke ProSim does not support respiratory rates less than 10
            // but we'll allow settings down to zero for other simulators
            new NumericValue("Any RespRate", rosetta.MDC_RESP_RATE.VALUE, 0, 60, 15, 1),
//            new NumericValue("CO2 RespRate", rosetta.MDC_CO2_RESP_RATE.VALUE, 0, 60, 15, 10),
//            new NumericValue("T.Thor RespRate", rosetta.MDC_TTHOR_RESP_RATE.VALUE, 0, 60, 15, 10),
            new NumericValue("Any Systolic", rosetta.MDC_PRESS_BLD_SYS.VALUE, 0, 300, 120, 5),
            new NumericValue("Any Diastolic", rosetta.MDC_PRESS_BLD_DIA.VALUE, 0, 300, 80, 5),
            new NumericValue("Any Mean", rosetta.MDC_PRESS_BLD_MEAN.VALUE, 0, 300, 90, 5),
            new NumericValue("Cuff Systolic", rosetta.MDC_PRESS_CUFF_SYS.VALUE, 0, 300, 120, 5),
            new NumericValue("Cuff Diastolic", rosetta.MDC_PRESS_CUFF_DIA.VALUE, 0, 300, 80, 5),
            new NumericValue("Cuff Mean", rosetta.MDC_PRESS_CUFF_MEAN.VALUE, 0, 300, 90, 5)
    };

    private final Subscriber subscriber;
    private final Publisher publisher;
    private final EventLoop eventLoop;
    private Topic topic;
    private ice.GlobalSimulationObjectiveDataWriter writer;
    private IntegerProperty maxObservedOwnershipStrength = new SimpleIntegerProperty(this, "maxObservedOwnershipStrength", -1);
    private IntegerProperty currentOwnershipStrength = new SimpleIntegerProperty(this, "currentOwnershipStrength", 0);

    private final UIControl[] controls = new UIControl[numericValues.length];
    
    public SimControl(EventLoop eventLoop, Subscriber subscriber, Publisher publisher) {
        this.eventLoop = eventLoop;
        this.subscriber = subscriber;
        this.publisher = publisher;
    }

    /**
     * initialize the UI. we do not wrap it in the generic init()-like API, but use the spring's interface to
     * emphasize that this is a creation-time call that should be invoked on the same thread as the constructor
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        ice.GlobalSimulationObjectiveTypeSupport.register_type(publisher.get_participant(),
                                                               ice.GlobalSimulationObjectiveTypeSupport.get_type_name());

        topic = TopicUtil.findOrCreateTopic(publisher.get_participant(), ice.GlobalSimulationObjectiveTopic.VALUE,
                                         ice.GlobalSimulationObjectiveTypeSupport.class);
        
        writer = (GlobalSimulationObjectiveDataWriter) publisher.create_datawriter_with_profile(topic,
                                                                                                  QosProfiles.ice_library,
                                                                                                  QosProfiles.state,
                                                                                                  null,
                                                                                                  StatusKind.STATUS_MASK_NONE);

        Button button = new Button("Assert Control");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setOwnershipStrength(maxObservedOwnershipStrength.get()+1);
            }
        });
        button.disableProperty().bind(currentOwnershipStrength.greaterThan(maxObservedOwnershipStrength));
        main.add(button, 0, 0, 3, 1);
        GridPane.setHalignment(button, HPos.CENTER);
        CheckBox jitter = new CheckBox();
        jitter.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Arrays.asList(controls).forEach((c)->c.jitter.setSelected(jitter.isSelected()));
            }
        });
        Label jitterLbl = new Label("Jitter:", jitter);
        jitterLbl.setContentDisplay(ContentDisplay.RIGHT);
        main.add(new Label("Jitter:", jitter), 3, 0, 1, 1);
        Label maxObserved = new Label();
        maxObserved.textProperty().bind(Bindings.concat("Max Strength: ", maxObservedOwnershipStrength.asString()));
        Label current = new Label();
        current.textProperty().bind(Bindings.concat("Strength: ", currentOwnershipStrength.asString()));
        main.add(current, 4, 0);
        main.add(maxObserved, 5, 0);
        
        for (int i = 0; i < numericValues.length; i++) {
            controls[i] = new UIControl(numericValues[i], writer);
            controls[i].addTo(main, i + 1);
        }
    }
    
    public void setOwnershipStrength(final int ownershipStrength) {
        DataWriterQos dwQos = new DataWriterQos();
        writer.get_qos(dwQos);
        dwQos.ownership_strength.value = ownershipStrength;
        writer.set_qos(dwQos);
        eventLoop.doLater(()->Arrays.asList(controls).forEach((c)->c.publishObjective()));
        currentOwnershipStrength.set(ownershipStrength);
    }
    
    public void shutDown() {
        for (int i = 0; i < controls.length; i++) {
            controls[i].close();
        }

        publisher.delete_datawriter(writer);
        publisher.get_participant().delete_topic(topic);

//        ice.GlobalSimulationObjectiveTypeSupport.unregister_type(publisher.get_participant(), ice.GlobalSimulationObjectiveTypeSupport.get_type_name());
    }
}
