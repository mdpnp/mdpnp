package org.mdpnp.apps.testapp;

import ice.SampleArray;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.mdpnp.guis.waveform.SampleArrayWaveformSource;
import org.mdpnp.guis.waveform.swing.SwingWaveformPanel;
import org.mdpnp.rti.dds.DDS;
import org.mdpnp.rtiapi.data.QosProfiles;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.Time_t;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.Topic;

public class WaveformRenderer extends JComponent {
    
    private final ice.SampleArrayDataReader reader;
    
    public WaveformRenderer(final ice.SampleArrayDataReader reader) {
        this.reader = reader;
    }
    
    private final SampleInfoSeq sample_info_seq = new SampleInfoSeq();
    private final ice.SampleArraySeq sample_array_seq = new ice.SampleArraySeq();
    private final Dimension dimension = new Dimension();
    
    private float maximum = Float.MIN_VALUE, minimum = Float.MAX_VALUE;
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            long now = System.currentTimeMillis();
            long start = now - 11000L;
            long end = now - 1000L;
            getSize(dimension);
            
            int last_x = 0, last_y = 0;
            
            ice.SampleArray keyHolder = (SampleArray) ice.SampleArray.create();
            keyHolder.unique_device_identifier = "sjgTVdaNtNtKXGp3Zoy1leVqhr9bAythBB7e";
            keyHolder.metric_id = "MDC_CAPNOGRAPH";
            keyHolder.instance_id = 0;
            
            InstanceHandle_t instanceHandle = reader.lookup_instance(keyHolder);
            if(instanceHandle.is_nil()) {
                return;
            }
            
//            for(;;) {
                try {
                    
                    reader.read_instance(sample_array_seq, sample_info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, instanceHandle, SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
                    int count = 0;
                    for(int i = 0; i < sample_info_seq.size(); i++) {
                        Time_t t = ((SampleInfo)sample_info_seq.get(i)).source_timestamp;
                        long baseTime = t.sec * 1000L + t.nanosec / 1000000L;
                        ice.SampleArray sampleArray = (SampleArray) sample_array_seq.get(i);
                        
//                        System.out.println(new Date(baseTime) + " " + sampleArray);
                        
                        for(int j = 0; j < sampleArray.values.userData.size(); j++) {
                            long tm = baseTime + sampleArray.millisecondsPerSample * j;
                            float value = sampleArray.values.userData.getFloat(j);
                            float x_prop = 1f * (tm - start) / (end-start);
                            
                            minimum = Math.min(value, minimum);
                            maximum = Math.max(value, maximum);
                            float y_prop = 1f * (value - minimum) / (maximum-minimum);
                            
                            int x = (int) (x_prop * dimension.width);
                            int y = dimension.height - (int) (y_prop * dimension.height);
                            
                            if(x>=0&&x<dimension.width&&y>=0&&y<dimension.height) {
                            
                                g.drawLine(last_x, last_y, x, y);
                                count++;
                                last_x = x;
                                last_y = y;
                            }
                            
                        }
                        
                        
                    }
//                    System.out.println("Drew " + count + " segments in " + (System.currentTimeMillis() -now) + "ms");
                } finally {
                    reader.return_loan(sample_array_seq, sample_info_seq);
                }
//            }
        } catch (RETCODE_NO_DATA noData) {
            
        }
    }
    
    public static void main(String[] args) {
        DDS.init();
        DomainParticipant participant = DomainParticipantFactory.get_instance().create_participant(15, DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        ice.SampleArrayTypeSupport.register_type(participant, ice.SampleArrayTypeSupport.get_type_name());
        Topic topic = participant.create_topic(ice.SampleArrayTopic.VALUE, ice.SampleArrayTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        ice.SampleArrayDataReader reader = (ice.SampleArrayDataReader) participant.get_implicit_subscriber().create_datareader_with_profile(topic, QosProfiles.ice_library, QosProfiles.waveform_data, null, StatusKind.STATUS_MASK_NONE);
//        WaveformRenderer renderer = new WaveformRenderer(reader);
        JFrame frame = new JFrame("waveform happiness");
        
        SwingWaveformPanel panel = new SwingWaveformPanel();
        ice.SampleArray keyHolder = (SampleArray) ice.SampleArray.create();
        keyHolder.unique_device_identifier = "sjgTVdaNtNtKXGp3Zoy1leVqhr9bAythBB7e";
        keyHolder.metric_id = "MDC_CAPNOGRAPH";
        keyHolder.instance_id = 0;
        panel.setSource(new SampleArrayWaveformSource(reader, keyHolder));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.setSize(800, 600);
        frame.setVisible(true);
        
        for(;;) {
            
            panel.repaint();
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
