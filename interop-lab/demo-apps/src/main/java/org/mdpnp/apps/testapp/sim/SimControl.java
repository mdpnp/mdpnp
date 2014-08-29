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

import ice.GlobalSimulationObjective;
import ice.GlobalSimulationObjectiveDataWriter;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.mdpnp.rtiapi.data.QosProfiles;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.topic.Topic;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class SimControl extends JPanel {

    private static final class NumericValue {
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

    private final DomainParticipant participant;
    private final Publisher publisher;
    private final Topic topic;
    private final ice.GlobalSimulationObjectiveDataWriter writer;
    private final ice.GlobalSimulationObjective[] objectives = new ice.GlobalSimulationObjective[numericValues.length];
    private final InstanceHandle_t[] handles = new InstanceHandle_t[numericValues.length];

    public SimControl(final DomainParticipant participant) {
        super(new GridBagLayout());
        this.participant = participant;
        publisher = participant.create_publisher(DomainParticipant.PUBLISHER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        ice.GlobalSimulationObjectiveTypeSupport.register_type(participant, ice.GlobalSimulationObjectiveTypeSupport.get_type_name());
        topic = participant.create_topic(ice.GlobalSimulationObjectiveTopic.VALUE, ice.GlobalSimulationObjectiveTypeSupport.get_type_name(),
                DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        writer = (GlobalSimulationObjectiveDataWriter) participant.create_datawriter_with_profile(topic, QosProfiles.ice_library, QosProfiles.state,
                null, StatusKind.STATUS_MASK_NONE);

        final JSlider[] sliders = new JSlider[numericValues.length];
        final JLabel[] labels = new JLabel[numericValues.length];
        final JLabel[] currentValues = new JLabel[numericValues.length];
        // final JPanel[] panels = new JPanel[numericValues.length];

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;
        gbc.ipady = 10;

        for (int i = 0; i < objectives.length; i++) {
            objectives[i] = (GlobalSimulationObjective) ice.GlobalSimulationObjective.create();
            objectives[i].metric_id = numericValues[i].metricId;
            objectives[i].value = numericValues[i].initialValue;
            handles[i] = writer.register_instance(objectives[i]);
            sliders[i] = new JSlider((int) numericValues[i].lowerBound, (int) numericValues[i].upperBound);
            sliders[i].setValue((int) objectives[i].value);
            sliders[i].setLabelTable(sliders[i].createStandardLabels((int) numericValues[i].increment, (int) numericValues[i].lowerBound));
            sliders[i].setPaintLabels(true);
            sliders[i].setPaintTicks(true);
            labels[i] = new JLabel(numericValues[i].name);
            currentValues[i] = new JLabel("" + sliders[i].getValue());
            gbc.gridx = 0;
            gbc.weightx = 0.1;
            add(labels[i], gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            add(sliders[i], gbc);
            gbc.gridx = 2;
            gbc.weightx = 0.1;
            add(currentValues[i], gbc);

            // panels[i] = new JPanel(new BorderLayout());
            // panels[i].add(labels[i], BorderLayout.WEST);
            // panels[i].add(sliders[i], BorderLayout.CENTER);
            // panels[i].add(currentValues[i], BorderLayout.EAST);

            // add(panels[i]);

            writer.write(objectives[i], handles[i]);

            final ice.GlobalSimulationObjective obj = objectives[i];
            final JSlider slider = sliders[i];
            // final JLabel label = labels[i];
            final JLabel currentValue = currentValues[i];
            final InstanceHandle_t handle = handles[i];

            sliders[i].addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    // For now I would rather publish changes as the slider is
                    // dragged
                    // if(! ((JSlider)e.getSource()).getValueIsAdjusting()) {
                    obj.value = slider.getValue();
                    currentValue.setText("" + obj.value);
                    writer.write(obj, handle);
                    // }
                }
            });

            gbc.gridy++;
        }

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
