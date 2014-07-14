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
package org.mdpnp.apps.safetylockapplication;

import ice.Numeric;
import ice.NumericDataReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


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

public class ReceivePulseOximeterNumerics implements DataReaderListener {

    private final ice.NumericSeq numeric_seq = new ice.NumericSeq();
    private final SampleInfoSeq sampleinfo_seq = new SampleInfoSeq();
    private ice.Numeric numeric;
    private int lastO2Sat = -1;
    private int lastPulseRate = -1;
    
    public synchronized void update(DeviceEvent event)
    {
    	if (numeric != null)
    	{
    		event.o2Saturation = lastO2Sat;
    		event.pulseRate = lastPulseRate;
    	}
    }

    @Override
    public synchronized void on_data_available(DataReader arg0) {
        ice.NumericDataReader reader = (NumericDataReader) arg0;
        for (;;) {
            try {
                reader.read(numeric_seq, sampleinfo_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.NOT_READ_SAMPLE_STATE,
                        ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
                for (int i = 0; i < sampleinfo_seq.size(); i++) {
                    SampleInfo si = (SampleInfo) sampleinfo_seq.get(i);
                    numeric = (Numeric) numeric_seq.get(i);
                    if (si.valid_data) {
                    
                    if (numeric.metric_id.equals("MDC_PULS_OXIM_SAT_O2"))
            			lastO2Sat = (int) numeric.value;
            		else if (numeric.metric_id.equals("MDC_PULS_OXIM_PULS_RATE"))
            			lastPulseRate = (int) numeric.value;
                    }	
                }
            } catch (RETCODE_NO_DATA noData) {
                break;
            } finally {
                reader.return_loan(numeric_seq, sampleinfo_seq);
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
