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

import ice.GlobalSimulationObjective;
import ice.GlobalSimulationObjectiveDataWriter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.util.converter.NumberStringConverter;

import org.mdpnp.rtiapi.data.QosProfiles;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.topic.Topic;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author Jeff Plourde
 *
 */
public class SimControl implements InitializingBean
{

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
        final Slider slider;
        final Label label;
        final Label currentValue;
        final ice.GlobalSimulationObjective objective;
        final InstanceHandle_t handle;
        final ice.GlobalSimulationObjectiveDataWriter writer;

        public UIControl(final NumericValue numericValue, final ice.GlobalSimulationObjectiveDataWriter writer) {

            this.writer = writer;

            final NumberFormat numberFormat = NumberFormat.getNumberInstance();
            numberFormat.setMaximumFractionDigits(2);
            numberFormat.setMinimumFractionDigits(2);
            numberFormat.setMinimumIntegerDigits(1);

            objective = (GlobalSimulationObjective) ice.GlobalSimulationObjective.create();
            objective.metric_id = numericValue.metricId;
            objective.value = numericValue.initialValue;
            handle = writer.register_instance(objective);
            slider = new Slider(numericValue.lowerBound, numericValue.upperBound, objective.value);
            slider.setMajorTickUnit(numericValue.increment);
            slider.setMinorTickCount(0);
            slider.setShowTickLabels(true);
            slider.setShowTickMarks(true);
            slider.setSnapToTicks(true);
            label = new Label(numericValue.name);
            label.setTextAlignment(TextAlignment.RIGHT);
            currentValue = new Label("" + slider.getValue());

            writer.write(objective, handle);

            currentValue.textProperty().bindBidirectional(slider.valueProperty(), new NumberStringConverter(numberFormat));
            slider.valueProperty().addListener(new ChangeListener<Number>() {

                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    objective.value = newValue.floatValue();
                    writer.write(objective, handle);
                }

            });
        }

        void close() {
            writer.unregister_instance(objective, handle);
        }

        void addTo(GridPane main, int i)
        {
            main.add(label, 0, i);
            main.add(slider, 1, i);
            main.add(currentValue, 2, i);
        }
    }

    private static final NumericValue[] numericValues = new NumericValue[] {
            new NumericValue("SpO2", rosetta.MDC_PULS_OXIM_SAT_O2.VALUE, 0, 100, 98, 10),
            new NumericValue("Pulse Rate (SpO2)", rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, 10, 360, 60, 50),
            new NumericValue("Heart Rate (ECG)", rosetta.MDC_ECG_HEART_RATE.VALUE, 10, 360, 60, 50),
            new NumericValue("etCO2", rosetta.MDC_AWAY_CO2_ET.VALUE, 0, 140, 30, 20),
            // The Fluke ProSim does not support respiratory rates less than 10
            // but we'll allow settings down to zero for other simulators
            new NumericValue("RespRate", rosetta.MDC_CO2_RESP_RATE.VALUE, 0, 60, 15, 10),
            new NumericValue("ABP Systolic", rosetta.MDC_PRESS_BLD_ART_ABP_SYS.VALUE, 0, 300, 120, 40),
            new NumericValue("ABP Diastolic", rosetta.MDC_PRESS_BLD_ART_ABP_DIA.VALUE, 0, 300, 80, 40),
            new NumericValue("NIBP Systolic", rosetta.MDC_PRESS_BLD_NONINV_SYS.VALUE, 0, 400, 120, 40),
            new NumericValue("NIBP Diastolic", rosetta.MDC_PRESS_BLD_NONINV_DIA.VALUE, 0, 400, 80, 40) };


    private final DomainParticipant participant;
    private Publisher publisher;
    private Topic topic;
    private ice.GlobalSimulationObjectiveDataWriter writer;

    private final UIControl[] controls = new UIControl[numericValues.length];

    public SimControl(DomainParticipant participant) {
        this.participant = participant;
    }

    /**
     * initialize the UI. we do not wrap it in the generic init()-like API, but use the spring's interface to
     * emphasize that this is a creation-time call that should be invoked on the same thread as the constructor
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        publisher = participant.create_publisher(DomainParticipant.PUBLISHER_QOS_DEFAULT,
                                                 null,
                                                 StatusKind.STATUS_MASK_NONE);

        ice.GlobalSimulationObjectiveTypeSupport.register_type(participant,
                                                               ice.GlobalSimulationObjectiveTypeSupport.get_type_name());

        topic = participant.create_topic(ice.GlobalSimulationObjectiveTopic.VALUE,
                                         ice.GlobalSimulationObjectiveTypeSupport.get_type_name(),
                                         DomainParticipant.TOPIC_QOS_DEFAULT,
                                         null,
                                         StatusKind.STATUS_MASK_NONE);

        writer = (GlobalSimulationObjectiveDataWriter) participant.create_datawriter_with_profile(topic,
                                                                                                  QosProfiles.ice_library,
                                                                                                  QosProfiles.state,
                                                                                                  null,
                                                                                                  StatusKind.STATUS_MASK_NONE);


        for (int i = 0; i < numericValues.length; i++) {
            controls[i] = new UIControl(numericValues[i], writer);
            controls[i].addTo(main, i);
        }
    }
    

    public void shutDown() {
        for (int i = 0; i < controls.length; i++) {
            controls[i].close();
        }

        participant.delete_datawriter(writer);
        participant.delete_topic(topic);
        participant.delete_publisher(publisher);
        ice.GlobalSimulationObjectiveTypeSupport.unregister_type(participant, ice.GlobalSimulationObjectiveTypeSupport.get_type_name());
    }
}
