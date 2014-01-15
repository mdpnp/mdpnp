package org.mdpnp.apps.testapp.sim;

import ice.GlobalSimulationObjective;
import ice.GlobalSimulationObjectiveDataWriter;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.topic.Topic;

@SuppressWarnings("serial")
public class SimControl extends JPanel {

    private static final class NumericValue {
        public final String name, metricId;
        public final float lowerBound, upperBound, initialValue, increment;

        public NumericValue(final String name, final String metricId, final float lowerBound, final float upperBound, final float initialValue, final float increment) {
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
      new NumericValue("Heart Rate", rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, 10, 360, 60, 50),
      new NumericValue("etCO2", rosetta.MDC_AWAY_CO2_EXP.VALUE, 0, 140, 30, 20),
      new NumericValue("RespRate", rosetta.MDC_RESP_RATE.VALUE, 0, 60, 15, 10),
    };

    private final DomainParticipant participant;
    private final Publisher publisher;
    private final Topic topic;
    private final ice.GlobalSimulationObjectiveDataWriter writer;
    private final ice.GlobalSimulationObjective[] objectives = new ice.GlobalSimulationObjective[numericValues.length];
    private final InstanceHandle_t[] handles = new InstanceHandle_t[numericValues.length];


    public SimControl(final DomainParticipant participant) {
        super(new GridLayout(numericValues.length, 1));
        this.participant = participant;
        publisher = participant.create_publisher(DomainParticipant.PUBLISHER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        ice.GlobalSimulationObjectiveTypeSupport.register_type(participant, ice.GlobalSimulationObjectiveTypeSupport.get_type_name());
        topic = participant.create_topic(ice.GlobalSimulationObjectiveTopic.VALUE, ice.GlobalSimulationObjectiveTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        writer = (GlobalSimulationObjectiveDataWriter) participant.create_datawriter(topic, Publisher.DATAWRITER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);

        final JSlider[] sliders = new JSlider[numericValues.length];
        final JLabel[] labels = new JLabel[numericValues.length];
        final JLabel[] currentValues = new JLabel[numericValues.length];
        final JPanel[] panels = new JPanel[numericValues.length];

        for(int i = 0; i < objectives.length; i++) {
            objectives[i] = (GlobalSimulationObjective) ice.GlobalSimulationObjective.create();
            objectives[i].metric_id.userData = numericValues[i].metricId;
            objectives[i].value = numericValues[i].initialValue;
            handles[i] = writer.register_instance(objectives[i]);
            sliders[i] = new JSlider((int)numericValues[i].lowerBound, (int)numericValues[i].upperBound);
            sliders[i].setValue((int)objectives[i].value);
            sliders[i].setLabelTable(sliders[i].createStandardLabels((int)numericValues[i].increment, (int)numericValues[i].lowerBound));
            sliders[i].setPaintLabels(true);
            sliders[i].setPaintTicks(true);
            labels[i] = new JLabel(numericValues[i].name);
            currentValues[i] = new JLabel(""+sliders[i].getValue());

            panels[i] = new JPanel(new BorderLayout());
            panels[i].add(labels[i], BorderLayout.WEST);
            panels[i].add(sliders[i], BorderLayout.CENTER);
            panels[i].add(currentValues[i], BorderLayout.EAST);

            add(panels[i]);

            writer.write(objectives[i], handles[i]);

            final ice.GlobalSimulationObjective obj = objectives[i];
            final JSlider slider = sliders[i];
//            final JLabel label = labels[i];
            final JLabel currentValue = currentValues[i];
            final InstanceHandle_t handle = handles[i];

            sliders[i].getModel().addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    obj.value = slider.getValue();
                    currentValue.setText(""+obj.value);
                    writer.write(obj, handle);
                }
            });
        }

    }

    public void tearDown() {
        for(int i = 0; i < numericValues.length; i++) {
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
