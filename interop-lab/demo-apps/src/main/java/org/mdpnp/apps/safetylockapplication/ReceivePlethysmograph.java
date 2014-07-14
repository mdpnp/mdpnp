
package org.mdpnp.apps.safetylockapplication;

import ice.SampleArray;
import ice.SampleArrayDataReader;
import ice.Values;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderListener;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.LivelinessChangedStatus;
import com.rti.dds.subscription.RequestedDeadlineMissedStatus;
import com.rti.dds.subscription.RequestedIncompatibleQosStatus;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleLostStatus;
import com.rti.dds.subscription.SampleRejectedStatus;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriptionMatchedStatus;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.Topic;

/**
 * @author Jeff Plourde
 *
 */
public class ReceivePlethysmograph implements DataReaderListener {

    private final ice.SampleArraySeq array_seq = new ice.SampleArraySeq();
    private final SampleInfoSeq sampleinfo_seq = new SampleInfoSeq();
    private final Date source_time = new Date(), receipt_time = new Date();
    private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
    private boolean plethAvailable = false;
    private ArrayList<Number> pleth;
    
    public synchronized void update(DeviceEvent event)
    {
    	if (pleth != null)
    		event.plethysmographSet = new ArrayList<Number>(pleth);
    }

    @Override
    public synchronized void on_data_available(DataReader arg0) {
        ice.SampleArrayDataReader reader = (SampleArrayDataReader) arg0;
        for (;;) {
            try {
                reader.read(array_seq, sampleinfo_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.NOT_READ_SAMPLE_STATE,
                        ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
                for (int i = 0; i < sampleinfo_seq.size(); i++) {
                    SampleInfo si = (SampleInfo) sampleinfo_seq.get(i);
                    ice.SampleArray numeric = (SampleArray) array_seq.get(i);
                    if (si.valid_data) {
                        Values arraySample = numeric.values;
                        
                        ArrayList<Number> newPleth = new ArrayList<Number>();
                        Matcher m = Pattern.compile("(?!=\\d\\.\\d\\.)([\\d.]+)").matcher(arraySample.toString());
                        while (m.find())
                        {
                            double d = Double.parseDouble(m.group(1));
                            newPleth.add((Number) d);
                        }
                        pleth = new ArrayList<Number>(newPleth);
                    }                        
                }
            } catch (RETCODE_NO_DATA noData) {
                break;
            } finally {
                reader.return_loan(array_seq, sampleinfo_seq);
            }
        }
    }

    @Override
    public void on_liveliness_changed(DataReader arg0, LivelinessChangedStatus arg1) {

    }

    @Override
    public void on_requested_deadline_missed(DataReader arg0, RequestedDeadlineMissedStatus arg1) {

    }

    @Override
    public void on_requested_incompatible_qos(DataReader arg0, RequestedIncompatibleQosStatus arg1) {

    }

    @Override
    public void on_sample_lost(DataReader arg0, SampleLostStatus arg1) {

    }

    @Override
    public void on_sample_rejected(DataReader arg0, SampleRejectedStatus arg1) {

    }

    @Override
    public void on_subscription_matched(DataReader arg0, SubscriptionMatchedStatus arg1) {

    }
}
