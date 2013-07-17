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
import ice.DeviceConnectivityObjectiveSeq;
import ice.DeviceConnectivityObjectiveTopic;
import ice.DeviceConnectivityObjectiveTypeSupport;
import ice.DeviceConnectivityTopic;
import ice.DeviceConnectivityTypeSupport;

import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.io.util.StateMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusCondition;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.ReadCondition;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.Topic;

public abstract class AbstractConnectedDevice extends AbstractDevice {
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
		deviceConnectivityObjectiveReader = (DeviceConnectivityObjectiveDataReader) subscriber.create_datareader(deviceConnectivityObjectiveTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);

		final ReadCondition rc = deviceConnectivityObjectiveReader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
		
        final DeviceConnectivityObjectiveSeq data_seq = new DeviceConnectivityObjectiveSeq();
        final SampleInfoSeq info_seq = new SampleInfoSeq();
        
		eventLoop.addHandler(rc, new EventLoop.ConditionHandler() {

            @Override
            public void conditionChanged(Condition condition) {
                try {
                    deviceConnectivityObjectiveReader.read_w_condition(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, rc);
                    for(int i = 0; i < info_seq.size(); i++) {
                        SampleInfo si = (SampleInfo) info_seq.get(i);
                        if(si.valid_data) {
                            DeviceConnectivityObjective dco = (DeviceConnectivityObjective) data_seq.get(i);
                            if(deviceIdentity.universal_device_identifier.equals(dco.universal_device_identifier)) {
                            
                                if(dco.connected) {
                                    log.info("Issuing connect for " + deviceIdentity.universal_device_identifier + " to " + dco.target);
                                    connect(dco.target);
                                    
                                } else {
                                    log.info("Issuing disconnect for " + deviceIdentity.universal_device_identifier);
                                    disconnect();
                                }
                            }
                        }
                    }
                    
                } catch (RETCODE_NO_DATA noData) {
                    
                } finally {
                    deviceConnectivityObjectiveReader.return_loan(data_seq, info_seq);
                }
            }
		    
		});
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
}
