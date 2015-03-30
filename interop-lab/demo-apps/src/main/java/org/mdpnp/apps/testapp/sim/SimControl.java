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

/**
 * @author Jeff Plourde
 *
 */
public class SimControl {

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

    private DomainParticipant participant;
    private Publisher publisher;
    private Topic topic;
    private ice.GlobalSimulationObjectiveDataWriter writer;
    private final ice.GlobalSimulationObjective[] objectives = new ice.GlobalSimulationObjective[numericValues.length];
    private final InstanceHandle_t[] handles = new InstanceHandle_t[numericValues.length];
    
    public SimControl setup(final DomainParticipant participant) {
        this.participant = participant;
        publisher = participant.create_publisher(DomainParticipant.PUBLISHER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        ice.GlobalSimulationObjectiveTypeSupport.register_type(participant, ice.GlobalSimulationObjectiveTypeSupport.get_type_name());
        topic = participant.create_topic(ice.GlobalSimulationObjectiveTopic.VALUE, ice.GlobalSimulationObjectiveTypeSupport.get_type_name(),
                DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        writer = (GlobalSimulationObjectiveDataWriter) participant.create_datawriter_with_profile(topic, QosProfiles.ice_library, QosProfiles.state,
                null, StatusKind.STATUS_MASK_NONE);

        final Slider[] sliders = new Slider[numericValues.length];
        final Label[] labels = new Label[numericValues.length];
        final Label[] currentValues = new Label[numericValues.length];

        final NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMinimumIntegerDigits(1);
        
        for (int i = 0; i < objectives.length; i++) {
            objectives[i] = (GlobalSimulationObjective) ice.GlobalSimulationObjective.create();
            objectives[i].metric_id = numericValues[i].metricId;
            objectives[i].value = numericValues[i].initialValue;
            handles[i] = writer.register_instance(objectives[i]);
            sliders[i] = new Slider(numericValues[i].lowerBound, numericValues[i].upperBound, objectives[i].value);
            sliders[i].setMajorTickUnit(numericValues[i].increment);
            sliders[i].setMinorTickCount(0);
            sliders[i].setShowTickLabels(true);
            sliders[i].setShowTickMarks(true);
            sliders[i].setSnapToTicks(true);
            labels[i] = new Label(numericValues[i].name);
            labels[i].setTextAlignment(TextAlignment.RIGHT);
            currentValues[i] = new Label("" + sliders[i].getValue());
            main.add(labels[i], 0, i);
            main.add(sliders[i], 1, i);
            main.add(currentValues[i], 2, i);

            writer.write(objectives[i], handles[i]);

            final ice.GlobalSimulationObjective obj = objectives[i];
            final Slider slider = sliders[i];
            // final JLabel label = labels[i];
            final Label currentValue = currentValues[i];
            final InstanceHandle_t handle = handles[i];

            
            currentValue.textProperty().bindBidirectional(slider.valueProperty(), new NumberStringConverter(numberFormat));
            sliders[i].valueProperty().addListener(new ChangeListener<Number>() {

                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    obj.value = newValue.floatValue();
                    writer.write(obj, handle);
                }
                
            });
        }
        return this;
    }
    
    
    public SimControl() {
    }

    public void tearDown() {
        for (int i = 0; i < numericValues.length; i++) {
            writer.unregister_instance(objectives[i], handles[i]);
        }

        participant.delete_datawriter(writer);
        participant.delete_topic(topic);
        participant.delete_publisher(publisher);
        ice.GlobalSimulationObjectiveTypeSupport.unregister_type(participant, ice.GlobalSimulationObjectiveTypeSupport.get_type_name());
    }

    public void start() {

    }

    public void stop() {

    }
}
