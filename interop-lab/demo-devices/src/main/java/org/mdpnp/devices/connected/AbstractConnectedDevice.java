/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.connected;

import ice.DeviceConnectivity;
import ice.DeviceConnectivityDataWriter;
import ice.DeviceConnectivityObjective;
import ice.DeviceConnectivityObjectiveDataReader;
import ice.DeviceConnectivityObjectiveTopic;
import ice.DeviceConnectivityObjectiveTypeSupport;
import ice.DeviceConnectivityTopic;
import ice.DeviceConnectivityTypeSupport;

import java.util.Arrays;

import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.io.util.StateMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.DataReader;
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

public abstract class AbstractConnectedDevice extends AbstractDevice implements DataReaderListener {
    protected final DeviceConnectivity deviceConnectivity;
    protected final Topic deviceConnectivityTopic;
    protected InstanceHandle_t deviceConnectivityHandle;
    protected final DeviceConnectivityDataWriter deviceConnectivityWriter;
    
	protected final DeviceConnectivityObjective deviceConnectivityObjective;
	protected final DeviceConnectivityObjectiveDataReader deviceConnectivityObjectiveReader;
//	protected final InstanceHandle_t deviceConnectivityObjectiveHandle;
	protected final Topic deviceConnectivityObjectiveTopic;
    
	protected final StateMachine<ice.ConnectionState> stateMachine = new StateMachine<ice.ConnectionState>(legalTransitions, ice.ConnectionState.Disconnected) {
		@Override
		public void emit(ice.ConnectionState newState, ice.ConnectionState oldState) {
			log.debug(oldState + "==>"+newState);
			deviceConnectivity.state = newState;
			deviceConnectivityWriter.write(deviceConnectivity, deviceConnectivityHandle);
		};
	};
	
	private static final Logger log = LoggerFactory.getLogger(AbstractConnectedDevice.class);
	
	public AbstractConnectedDevice(int domainId) {
		super(domainId);
		DeviceConnectivityTypeSupport.register_type(domainParticipant, DeviceConnectivityTypeSupport.get_type_name());
		deviceConnectivityTopic = domainParticipant.create_topic(DeviceConnectivityTopic.VALUE, DeviceConnectivityTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
		deviceConnectivityWriter = (DeviceConnectivityDataWriter) publisher.create_datawriter(deviceConnectivityTopic, Publisher.DATAWRITER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
		
		if(null == deviceConnectivityWriter) {
		    throw new RuntimeException("unable to create writer");
		}
		
		deviceConnectivity = new DeviceConnectivity();
		deviceConnectivity.type = getConnectionType();
		deviceConnectivity.state = ice.ConnectionState.Disconnected;
		
		deviceConnectivityObjective = (DeviceConnectivityObjective) DeviceConnectivityObjective.create();
		DeviceConnectivityObjectiveTypeSupport.register_type(domainParticipant, DeviceConnectivityObjectiveTypeSupport.get_type_name());
		deviceConnectivityObjectiveTopic = domainParticipant.create_topic(DeviceConnectivityObjectiveTopic.VALUE, DeviceConnectivityObjectiveTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
		// TODO Implementing on the inbound DDS thread for convenience
		deviceConnectivityObjectiveReader = (DeviceConnectivityObjectiveDataReader) subscriber.create_datareader(deviceConnectivityObjectiveTopic, Subscriber.DATAREADER_QOS_DEFAULT, this, StatusKind.DATA_AVAILABLE_STATUS);
	}
	
	protected abstract void connect(String str);
	protected abstract void disconnect();
	protected abstract ice.ConnectionType getConnectionType();
	
	public ice.ConnectionState getState() {
		return stateMachine.getState();
	};
	
	private static final ice.ConnectionState[][] legalTransitions = new ice.ConnectionState[][] {
		// Normal "flow"
		{ice.ConnectionState.Disconnected, ice.ConnectionState.Connecting},
		{ice.ConnectionState.Connected, ice.ConnectionState.Disconnecting},
		{ice.ConnectionState.Connecting, ice.ConnectionState.Negotiating},
		{ice.ConnectionState.Negotiating, ice.ConnectionState.Connected},
		{ice.ConnectionState.Disconnecting, ice.ConnectionState.Disconnected},
		// Exception pathways
		{ice.ConnectionState.Negotiating, ice.ConnectionState.Disconnected},
		{ice.ConnectionState.Connecting, ice.ConnectionState.Disconnected},
		{ice.ConnectionState.Connected, ice.ConnectionState.Disconnected}
	};
	
	//Disconnected -> Connecting -> Negotiating -> Connected -> Disconnecting -> Disconnected
	
	
	protected void setConnectionInfo(String connectionInfo) {
	    if(null == connectionInfo) {
	        // TODO work on nullity semantics
	        log.warn("Attempt to set connectionInfo null");
	        connectionInfo = "";
	    }
	    if(!connectionInfo.equals(deviceConnectivity.info)) {
	        deviceConnectivity.info = connectionInfo;
	        deviceConnectivityWriter.write(deviceConnectivity, deviceConnectivityHandle);
	    }
        
	}

	protected long getConnectInterval() {
		return 20000L;
	}
    @Override
    public void on_data_available(DataReader arg0) {
        DeviceConnectivityObjectiveDataReader reader = (DeviceConnectivityObjectiveDataReader) arg0;
        DeviceConnectivityObjective dco = new DeviceConnectivityObjective();
        SampleInfo si = new SampleInfo();
        try {
            reader.read_next_sample(dco, si);
            // TODO reimplement as a content filter or monitor a single instance .. i dunno
            if(deviceIdentity.universal_device_identifier.equals(dco.universal_device_identifier)) {
            
                if(dco.connected) {
                    log.info("Issuing connect for " + deviceIdentity.universal_device_identifier + " to " + dco.target);
                    connect(dco.target);
                    
                } else {
                    log.info("Issuing disconnect for " + deviceIdentity.universal_device_identifier);
                    disconnect();
                }
            }
        } catch (RETCODE_NO_DATA noData) {
            
        }
        
    }
    @Override
    public void on_liveliness_changed(DataReader arg0, LivelinessChangedStatus arg1) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void on_requested_deadline_missed(DataReader arg0, RequestedDeadlineMissedStatus arg1) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void on_requested_incompatible_qos(DataReader arg0, RequestedIncompatibleQosStatus arg1) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void on_sample_lost(DataReader arg0, SampleLostStatus arg1) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void on_sample_rejected(DataReader arg0, SampleRejectedStatus arg1) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void on_subscription_matched(DataReader arg0, SubscriptionMatchedStatus arg1) {
        // TODO Auto-generated method stub
        
    }
}
