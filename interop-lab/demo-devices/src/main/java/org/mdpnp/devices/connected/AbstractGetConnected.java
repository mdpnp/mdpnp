/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.connected;

import ice.DeviceConnectivity;
import ice.DeviceConnectivityDataReader;
import ice.DeviceConnectivityObjective;
import ice.DeviceConnectivityObjectiveDataWriter;
import ice.DeviceConnectivityObjectiveTypeSupport;
import ice.DeviceConnectivityTypeSupport;

import java.util.Arrays;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderAdapter;
import com.rti.dds.subscription.DataReaderListener;
import com.rti.dds.subscription.LivelinessChangedStatus;
import com.rti.dds.subscription.RequestedDeadlineMissedStatus;
import com.rti.dds.subscription.RequestedIncompatibleQosStatus;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleLostStatus;
import com.rti.dds.subscription.SampleRejectedStatus;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriptionMatchedStatus;
import com.rti.dds.topic.Topic;

public abstract class AbstractGetConnected extends DataReaderAdapter {
	private boolean closing = false;

	private DomainParticipant participant;
	private Subscriber subscriber;
	private Publisher publisher;
	private DeviceConnectivity deviceConnectivity;
	private DeviceConnectivityObjective deviceConnectivityObjective;
	private DeviceConnectivityDataReader deviceConnectivityReader;
	private DeviceConnectivityObjectiveDataWriter deviceConnectivityObjectiveWriter;
	private Topic deviceConnectivityTopic;
	private Topic deviceConnectivityObjectiveTopic;
	
	public AbstractGetConnected(int domainId, String universal_device_identifier) {
	    participant = DomainParticipantFactory.get_instance().create_participant(domainId, DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
	    subscriber = participant.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
	    publisher = participant.create_publisher(DomainParticipant.PUBLISHER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
	    deviceConnectivity = (DeviceConnectivity) DeviceConnectivity.create();
	    deviceConnectivityObjective = (DeviceConnectivityObjective) DeviceConnectivityObjective.create();
	    deviceConnectivityObjective.universal_device_identifier = universal_device_identifier;
	    DeviceConnectivityObjectiveTypeSupport.register_type(participant, DeviceConnectivityObjectiveTypeSupport.get_type_name());
	    deviceConnectivityObjectiveTopic = participant.create_topic(ice.DeviceConnectivityObjectiveTopic.VALUE, ice.DeviceConnectivityObjectiveTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
	    deviceConnectivityObjectiveWriter = (DeviceConnectivityObjectiveDataWriter) publisher.create_datawriter(deviceConnectivityObjectiveTopic, Publisher.DATAWRITER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
	    
	}
	
	public void connect() {
	       DeviceConnectivityTypeSupport.register_type(participant, DeviceConnectivityTypeSupport.get_type_name());
	       deviceConnectivityTopic = participant.create_topic(ice.DeviceConnectivityTopic.VALUE, ice.DeviceConnectivityTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
	       deviceConnectivityReader = (DeviceConnectivityDataReader) subscriber.create_datareader(deviceConnectivityTopic, Subscriber.DATAREADER_QOS_DEFAULT, this, StatusKind.DATA_AVAILABLE_STATUS);
	}
	
	public void disconnect() {
		long start = System.currentTimeMillis();
		
		closing = true;
		deviceConnectivityObjective.connected = false;
		deviceConnectivityObjective.target = "APP IS CLOSING";
		
		boolean disconnected = false;
		
		while(!disconnected) {
		    deviceConnectivityObjectiveWriter.write(deviceConnectivityObjective, InstanceHandle_t.HANDLE_NIL);
			synchronized(this) {
				disconnected = ice.ConnectionState.Disconnected.equals(deviceConnectivity.state);
				if(!disconnected) {
					if( (System.currentTimeMillis()-start) >= 10000L) {
						return;
					}
					try {
						this.wait(1000L);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}

		}
	}
	
	protected abstract void abortConnect();
	protected abstract String addressFromUser();
	protected abstract String addressFromUserList(String[] list);
	protected abstract boolean isFixedAddress();
	
	private boolean issuingConnect;
	
	private void issueConnect() {
		synchronized(this) {
			if(issuingConnect) {
				return;
			} else {
				issuingConnect = true;
				notifyAll();
			}
		}
		try {
			if("".equals(deviceConnectivityObjective.target) && !closing && deviceConnectivity.type != null && (isFixedAddress() || !deviceConnectivity.valid_targets.isEmpty() || !ice.ConnectionType.Serial.equals(deviceConnectivity.type)) && ice.ConnectionState.Disconnected.equals(deviceConnectivity.state)) {
			    if(ice.ConnectionType.Network.equals(deviceConnectivity.type)) {
					deviceConnectivityObjective.target = addressFromUser();
					if(null == deviceConnectivityObjective.target) {
						abortConnect();
						return;
					}
			    } else if(ice.ConnectionType.Serial.equals(deviceConnectivity.type)) {
			        deviceConnectivityObjective.target = addressFromUserList((String[]) deviceConnectivity.valid_targets.toArray(new String[0]));
					if(null == deviceConnectivityObjective.target) {
						abortConnect();
						return;
					}
			    } else {
			        deviceConnectivityObjective.target = "";
				}
			    deviceConnectivityObjective.connected = true;
			    deviceConnectivityObjectiveWriter.write(deviceConnectivityObjective, InstanceHandle_t.HANDLE_NIL);
			}
		} finally {
			synchronized(this) {
				issuingConnect = false;
				notifyAll();
			}
		}
		
	}

    @Override
    public void on_data_available(DataReader arg0) {
        DeviceConnectivityDataReader reader = (DeviceConnectivityDataReader) arg0;
        SampleInfo si = new SampleInfo();
        try {
            reader.read_next_sample(deviceConnectivity, si);
            if(si.valid_data && deviceConnectivityObjective.universal_device_identifier.equals(deviceConnectivity.universal_device_identifier)) {
                issueConnect();
            }
        } catch(RETCODE_NO_DATA noData) {
            
        }
        
    }


}
