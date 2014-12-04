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
package org.mdpnp.devices.philips.intellivue;

import ice.ConnectionState;
import ice.SampleArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.mdpnp.devices.connected.AbstractConnectedDevice;
import org.mdpnp.devices.net.NetworkLoop;
import org.mdpnp.devices.net.TaskQueue;
import org.mdpnp.devices.philips.intellivue.action.ExtendedPollDataResult;
import org.mdpnp.devices.philips.intellivue.action.ObservationPoll;
import org.mdpnp.devices.philips.intellivue.action.SingleContextPoll;
import org.mdpnp.devices.philips.intellivue.action.SinglePollDataResult;
import org.mdpnp.devices.philips.intellivue.association.AssociationAbort;
import org.mdpnp.devices.philips.intellivue.association.AssociationAccept;
import org.mdpnp.devices.philips.intellivue.association.AssociationDisconnect;
import org.mdpnp.devices.philips.intellivue.association.AssociationFinish;
import org.mdpnp.devices.philips.intellivue.association.AssociationMessage;
import org.mdpnp.devices.philips.intellivue.association.AssociationRefuse;
import org.mdpnp.devices.philips.intellivue.association.impl.AssociationFinishImpl;
import org.mdpnp.devices.philips.intellivue.attribute.Attribute;
import org.mdpnp.devices.philips.intellivue.attribute.AttributeFactory;
import org.mdpnp.devices.philips.intellivue.connectindication.ConnectIndication;
import org.mdpnp.devices.philips.intellivue.data.AbsoluteTime;
import org.mdpnp.devices.philips.intellivue.data.AttributeId;
import org.mdpnp.devices.philips.intellivue.data.AttributeValueList;
import org.mdpnp.devices.philips.intellivue.data.ComponentId;
import org.mdpnp.devices.philips.intellivue.data.CompoundNumericObservedValue;
import org.mdpnp.devices.philips.intellivue.data.DevAlarmEntry;
import org.mdpnp.devices.philips.intellivue.data.DevAlarmList;
import org.mdpnp.devices.philips.intellivue.data.DeviceAlertCondition;
import org.mdpnp.devices.philips.intellivue.data.DisplayResolution;
import org.mdpnp.devices.philips.intellivue.data.EnumValue;
import org.mdpnp.devices.philips.intellivue.data.Handle;
import org.mdpnp.devices.philips.intellivue.data.IPAddressInformation;
import org.mdpnp.devices.philips.intellivue.data.Label;
import org.mdpnp.devices.philips.intellivue.data.MetricSpecification;
import org.mdpnp.devices.philips.intellivue.data.NomPartition;
import org.mdpnp.devices.philips.intellivue.data.NumericObservedValue;
import org.mdpnp.devices.philips.intellivue.data.ObjectClass;
import org.mdpnp.devices.philips.intellivue.data.ObservedValue;
import org.mdpnp.devices.philips.intellivue.data.PollProfileSupport;
import org.mdpnp.devices.philips.intellivue.data.ProductionSpecification;
import org.mdpnp.devices.philips.intellivue.data.ProductionSpecificationType;
import org.mdpnp.devices.philips.intellivue.data.ProtocolSupport.ProtocolSupportEntry;
import org.mdpnp.devices.philips.intellivue.data.RelativeTime;
import org.mdpnp.devices.philips.intellivue.data.SampleArrayCompoundObservedValue;
import org.mdpnp.devices.philips.intellivue.data.SampleArrayObservedValue;
import org.mdpnp.devices.philips.intellivue.data.SampleArraySpecification;
import org.mdpnp.devices.philips.intellivue.data.ScaleAndRangeSpecification;
import org.mdpnp.devices.philips.intellivue.data.SimpleColor;
import org.mdpnp.devices.philips.intellivue.data.StrAlMonInfo;
import org.mdpnp.devices.philips.intellivue.data.SystemModel;
import org.mdpnp.devices.philips.intellivue.data.TextId;
import org.mdpnp.devices.philips.intellivue.data.TextIdList;
import org.mdpnp.devices.philips.intellivue.data.Type;
import org.mdpnp.devices.philips.intellivue.data.UnitCode;
import org.mdpnp.devices.philips.intellivue.data.VariableLabel;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportError;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportMessage;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportResult;
import org.mdpnp.devices.philips.intellivue.dataexport.command.EventReport;
import org.mdpnp.devices.philips.intellivue.dataexport.command.SetResult;
import org.mdpnp.devices.philips.intellivue.dataexport.event.MdsCreateEvent;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.infrastructure.Time_t;

public abstract class AbstractDemoIntellivue extends AbstractConnectedDevice {
    protected Time_t sampleTimeSampleArray = new Time_t(0, 0);

    protected final InstanceHolder<SampleArray> getSampleArrayUpdate(ObservedValue ov, int handle) {
        Map<Integer, InstanceHolder<SampleArray>> forObservedValue = sampleArrayUpdates.get(ov);
        if (null == forObservedValue) {
            return null;
        } else {
            return forObservedValue.get(handle);
        }
    }
    
    protected final void putSampleArrayUpdate(ObservedValue ov, int handle, InstanceHolder<SampleArray> value) {
        Map<Integer, InstanceHolder<SampleArray>> forObservedValue = sampleArrayUpdates.get(ov);
        if (null == forObservedValue) {
            forObservedValue = new HashMap<Integer, InstanceHolder<SampleArray>>();
            sampleArrayUpdates.put(ov, forObservedValue);
        }
        if(null == value) {
            forObservedValue.remove(handle);
        } else {
            forObservedValue.put(handle, value);
        }
    }
    
    @Override
    protected void stateChanged(ConnectionState newState, ConnectionState oldState, String transitionNote) {
        super.stateChanged(newState, oldState, transitionNote);
        if (ice.ConnectionState.Connected.equals(oldState) && !ice.ConnectionState.Connected.equals(newState)) {
            lastDataPoll = 0L;
            lastMessageReceived = 0L;
            lastMessageSentTime = 0L;
            lastKeepAlive = 0L;
            stopEmitFastData();
        }
    }

//    @Override
//    protected boolean sampleArraySpecifySourceTimestamp() {
//        return true;
//    }

    // For a point in time this is currentTime-runTime
    // Or, in other words, the time when the device started
    // according to the device clock
    protected long deviceStartTimeInDeviceTime;

    // For a point in time this is System.currentTimeInMillis()-runTime
    // Or, in other words, the time when the device started according
    // to the local clock
    protected Time_t deviceStartTimeInLocalTime = new Time_t(0, 0);

    protected final static long WATCHDOG_INTERVAL = 200L;
    // Maximum time between message receipt
    protected final static long IN_CONNECTION_TIMEOUT = 5000L;
    // Assert a keepalive if no message received for this long
    protected final static long IN_CONNECTION_ASSERT = 4000L;

    protected static long OUT_CONNECTION_ASSERT = 8000L;

    protected final static long CONTINUOUS_POLL_INTERVAL = 10 * 60000L;
    protected final static long CONTINUOUS_POLL_ASSERT = 9 * 60000L;

    protected final static long ASSOCIATION_REQUEST_INTERVAL = 2000L;
    protected final static long FINISH_REQUEST_INTERVAL = 500L;

    private long lastAssociationRequest = 0L;
    private long lastFinishRequest = 0L;
    private long lastDataPoll = 0L;
    private long lastMessageReceived = 0L;
    private long lastKeepAlive = 0L;
    private long lastMessageSentTime = 0L;
    
    private final Map<Integer, ScheduledFuture<?>> emitFastDataByFrequency = new HashMap<Integer, ScheduledFuture<?>>();
    private static final int BUFFER_SAMPLES = 125;
    
    private synchronized void startEmitFastData(long msInterval) {
        int frequency = (int)(1000 / msInterval);
        // for the 62.5Hz case; 
        // TODO client will see an overlapping sample where 62.5Hz is truncated to 62Hz
        long interval = msInterval * BUFFER_SAMPLES;
        if (!emitFastDataByFrequency.containsKey(frequency)) {
            log.info("Start emit fast data at frequency " + frequency);
            emitFastDataByFrequency.put(frequency, executor.scheduleAtFixedRate(new EmitFastData(frequency), 2* interval - System.currentTimeMillis()
                    % interval, interval, TimeUnit.MILLISECONDS));
        }
    }
    
    private synchronized void stopEmitFastData() {
        for (Integer frequency : emitFastDataByFrequency.keySet()) {
            log.info("stop emit fast data at frequency " + frequency);
            emitFastDataByFrequency.get(frequency).cancel(false);
        }
        emitFastDataByFrequency.clear();
    }

    protected void watchdog() {
        long now = System.currentTimeMillis();
        switch (stateMachine.getState().ordinal()) {
        case ice.ConnectionState._Negotiating:
            // In the negotiating state we are emitted association requests
            if (now - lastAssociationRequest >= ASSOCIATION_REQUEST_INTERVAL) {
                try {
                    myIntellivue.requestAssociation();
                    lastAssociationRequest = now;
                } catch (IOException e1) {
                    log.error("requesting association", e1);
                }
            }
            break;

        case ice.ConnectionState._Disconnecting:
            // In the disconnecting state we are emitting association finish
            // requests
            if (now - lastFinishRequest >= FINISH_REQUEST_INTERVAL) {
                try {
                    myIntellivue.send(new AssociationFinishImpl());
                    lastFinishRequest = now;
                } catch (IOException e1) {
                    log.error("sending association finish", e1);
                }
            }
            break;
        case ice.ConnectionState._Connected:
            if (now - lastMessageReceived >= IN_CONNECTION_TIMEOUT) {
                // Been too long since the last message was received, revert to
                // new association requests (Negotiation)
                state(ice.ConnectionState.Negotiating, "timeout receiving  messages");
                return;
            } else if ((now - lastMessageReceived >= IN_CONNECTION_ASSERT || now - lastMessageSentTime >= OUT_CONNECTION_ASSERT)
                    && now - lastKeepAlive >= Math.min(OUT_CONNECTION_ASSERT, IN_CONNECTION_ASSERT)) {
                // Either side (or both) has not asserted themselves in the time
                // required AND we haven't recently sent a keep alive message
                try {
                    myIntellivue.requestSinglePoll(ObjectClass.NOM_MOC_VMO_AL_MON, AttributeId.NOM_ATTR_GRP_VMO_STATIC);
                    lastKeepAlive = now;
                } catch (IOException e) {
                    state(ice.ConnectionState.Negotiating, "failure to send a keepalive");
                    log.error("requesting a keep alive (static attributes of the alarm monitor object)", e);
                }
            } else if (now - lastDataPoll >= CONTINUOUS_POLL_ASSERT) {
                // Time to request a new data poll
                try {
                    myIntellivue.requestExtendedPoll(ObjectClass.NOM_MOC_VMO_METRIC_NU, CONTINUOUS_POLL_INTERVAL);
                    myIntellivue.requestExtendedPoll(ObjectClass.NOM_MOC_VMO_METRIC_SA_RT, CONTINUOUS_POLL_INTERVAL);
                    myIntellivue.requestExtendedPoll(ObjectClass.NOM_MOC_VMO_AL_MON, CONTINUOUS_POLL_INTERVAL, AttributeId.NOM_ATTR_GRP_AL_MON);
                    myIntellivue.requestSinglePoll(ObjectClass.NOM_MOC_PT_DEMOG, AttributeId.NOM_ATTR_GRP_PT_DEMOG);
                    lastDataPoll = now;
                } catch (IOException e) {
                    log.error("requesting data polls", e);
                }
            }
            break;
        }
    }

    private class MyIntellivue extends Intellivue {
        private final Logger log = LoggerFactory.getLogger(MyIntellivue.class);

        public MyIntellivue() {
            super();
        }

        @Override
        protected void handle(DataExportResult message) {
            // if we were checking for confirmation of outgoing confirmed
            // messages this would be the place to find confirmations
            super.handle(message);
        }

        @Override
        public synchronized boolean send(Message message) throws IOException {
            lastMessageSentTime = System.currentTimeMillis();
            return super.send(message);
        }

        @Override
        protected void handle(SocketAddress sockaddr, Message message, SelectionKey sk) throws IOException {
            // This will capture DataExport, Association, and ConnectIndication
            // messages...
            // Opting not to update lastMessageREceived for ConnectIndications
            // .. since they are beacons and not part of the session
            super.handle(sockaddr, message, sk);
        }

        @Override
        protected void handle(SocketAddress sockaddr, AssociationMessage message) throws IOException {
            lastMessageReceived = System.currentTimeMillis();
            super.handle(sockaddr, message);
        }

        @Override
        protected void handle(DataExportError error) throws IOException {
            // Could do something context-sensitive here when a confirmed action
            // fails
            // Such as when setting the priority list returns "access denied"
            // for waveforms because another client is already receiving waves
            super.handle(error);
        }

        @Override
        protected void handle(DataExportMessage message) throws IOException {
            lastMessageReceived = System.currentTimeMillis();
            super.handle(message);
        }

        @Override
        protected void handle(SetResult result, boolean confirmed) {
            super.handle(result, confirmed);
            AttributeValueList attrs = result.getAttributes();
            Attribute<TextIdList> ati = attrs.getAttribute(AttributeId.NOM_ATTR_POLL_NU_PRIO_LIST, TextIdList.class);

            if (null != ati && ati.getValue().containsAll(numericLabels.values().toArray(new Label[0]))) {
            } else {
                log.warn("Numerics priority list does not contain all of our requested labels:" + ati);
            }

            ati = attrs.getAttribute(AttributeId.NOM_ATTR_POLL_RTSA_PRIO_LIST, TextIdList.class);
            if (null != ati && ati.getValue().containsAll(sampleArrayLabels.values().toArray(new Label[0]))) {
            } else {
                log.warn("SampleArray priority list does not contain all requested labels:" + ati);
            }
        }

        @Override
        protected void handle(EventReport eventReport, boolean confirm) throws IOException {
            // The super sends confirmations where appropriate by default
            super.handle(eventReport, confirm);
            switch (ObjectClass.valueOf(eventReport.getEventType().getType())) {
            case NOM_NOTI_MDS_CREAT:
                MdsCreateEvent createEvent = (MdsCreateEvent) eventReport.getEvent();
                AttributeValueList attrs = createEvent.getAttributes();
                Attribute<SystemModel> asm = attrs.getAttribute(AttributeId.NOM_ATTR_ID_MODEL, SystemModel.class);
//                Attribute<org.mdpnp.devices.philips.intellivue.data.String> as = attrs.getAttribute(AttributeId.NOM_ATTR_ID_BED_LABEL,
//                        org.mdpnp.devices.philips.intellivue.data.String.class);

                Attribute<AbsoluteTime> clockTime = attrs.getAttribute(AttributeId.NOM_ATTR_TIME_ABS, AbsoluteTime.class);
                Attribute<RelativeTime> offsetTime = attrs.getAttribute(AttributeId.NOM_ATTR_TIME_REL, RelativeTime.class);

                if (null == clockTime) {
                    log.warn("No NOM_ATTR_TIME_ABS in MDS Create");
                } else if (null == offsetTime) {
                    log.warn("No NOM_ATTR_TIME_REL in MDS Create");
                } else {
                    long currentTime = clockTime.getValue().getDate().getTime();
                    long runTime = offsetTime.getValue().toMilliseconds();
                    deviceStartTimeInDeviceTime = currentTime - runTime;
                    // TODO these all assume near-zero latency
                    long deviceStartTimeInLocalTime = System.currentTimeMillis() - runTime;
                    AbstractDemoIntellivue.this.deviceStartTimeInLocalTime.sec = (int) (deviceStartTimeInLocalTime / 1000L);
                    AbstractDemoIntellivue.this.deviceStartTimeInLocalTime.nanosec = (int) ((deviceStartTimeInLocalTime % 1000L) * 1000000L);
                    log.info("Device started (in device time) at " + new Date(deviceStartTimeInDeviceTime));
                    log.info("Device started (in local time) at " + new Date(deviceStartTimeInLocalTime));
                }

                if (null != asm) {
                    deviceIdentity.manufacturer = asm.getValue().getManufacturer().getString();
                }
                switch (stateMachine.getState().ordinal()) {
                case ice.ConnectionState._Negotiating:
                    state(ice.ConnectionState.Connected, "Received MDS Create Event");
                    break;
                }

                requestSinglePoll(ObjectClass.NOM_MOC_VMS_MDS, AttributeId.NOM_ATTR_GRP_SYS_PROD);

                requestSet(numericLabels.values().toArray(new Label[0]), sampleArrayLabels.values().toArray(new Label[0]));

                break;
            default:
                break;
            }

        }

        @Override
        protected void handle(SocketAddress sockaddr, AssociationRefuse message) {
            switch (stateMachine.getState().ordinal()) {
            case ice.ConnectionState._Connected:
                state(ice.ConnectionState.Negotiating, "reconnecting after active association is later refused");
                break;
            case ice.ConnectionState._Disconnecting:
                state(ice.ConnectionState.Disconnected, "association refused!");
                break;
            case ice.ConnectionState._Negotiating:
                setConnectionInfo("association refused, retrying...");
                break;
            }

            super.handle(sockaddr, message);
        }

        @Override
        protected void handle(SocketAddress sockaddr, AssociationAbort message) {
            switch (stateMachine.getState().ordinal()) {
            case ice.ConnectionState._Connected:
                state(ice.ConnectionState.Negotiating, "reconnecting after active association is later aborted");
                break;
            case ice.ConnectionState._Disconnecting:
                state(ice.ConnectionState.Disconnected, "association aborted!");
                break;
            }
            super.handle(sockaddr, message);
        }

        @Override
        protected void handle(SocketAddress sockaddr, AssociationDisconnect message) {
            switch (stateMachine.getState().ordinal()) {
            case ice.ConnectionState._Connected:
                state(ice.ConnectionState.Negotiating, "unexpected disconnect message");
                break;
            case ice.ConnectionState._Disconnecting:
                state(ice.ConnectionState.Disconnected, "association disconnected");
                break;
            }
            super.handle(sockaddr, message);
        }

        @Override
        protected void handle(SocketAddress sockaddr, AssociationFinish message) throws IOException {
            super.handle(sockaddr, message);
            switch (stateMachine.getState().ordinal()) {
            case ice.ConnectionState._Connected:
                state(ice.ConnectionState.Negotiating, "unexpected disconnect message");
                break;
            case ice.ConnectionState._Disconnecting:
                state(ice.ConnectionState.Disconnected, "association disconnected");
                break;
            }
        }

        @Override
        protected void handle(ConnectIndication connectIndication, SelectionKey sk) {
            log.trace("Received a connectindication:" + connectIndication);
            IPAddressInformation ipinfo = connectIndication.getIpAddressInformation();
            ProtocolSupportEntry pse = acceptable(connectIndication);
            if (null != ipinfo && null != pse) {
                try {
                    InetAddress remote = ipinfo.getInetAddress();
                    int prefixLength = Network.prefixCount(connectIndication.getIpAddressInformation().getSubnetMask());

                    connect(remote, prefixLength, pse.getPortNumber());
                } catch (UnknownHostException e) {
                    log.error("Trying to connect to " + ipinfo + " after receiving a beacon", e);
                } catch (IOException e) {
                    log.error("Trying to connect to " + ipinfo + " after receiving a beacon", e);
                }

            }
        }

        @Override
        protected void handle(SocketAddress sockaddr, AssociationAccept message) {
            PollProfileSupport pps = message.getUserInfo().getPollProfileSupport();
            long timeout = minPollPeriodToTimeout(pps.getMinPollPeriod().toMilliseconds());
            OUT_CONNECTION_ASSERT = Math.max(200L, timeout - 1000L);
            log.debug("Negotiated " + pps.getMinPollPeriod().toMilliseconds() + "ms min poll period, timeout=" + timeout);
            log.debug("Negotiated " + pps.getMaxMtuTx() + " " + pps.getMaxMtuRx() + " " + pps.getMaxBwTx());
            super.handle(sockaddr, message);
        }

        @Override
        protected void handle(SinglePollDataResult result) {
            switch (ObjectClass.valueOf(result.getPolledObjType().getOidType().getType())) {
            case NOM_MOC_VMO_AL_MON:
                switch (AttributeId.valueOf(result.getPolledAttributeGroup().getType())) {
                case NOM_ATTR_GRP_VMO_STATIC:
                    // Responses to our "keep alive" messages occur here
                    // Currently we track any incoming message as proof of life
                    // ... so nothing special to do here
                    break;
                case NOM_ATTR_GRP_AL_MON:
                    log.debug("Alert Monitor Information has arrived:" + result);
                    break;
                default:
                    break;
                }
            default:
                break;
            }

            for (SingleContextPoll sop : result.getPollInfoList()) {
                for (ObservationPoll op : sop.getPollInfo()) {
                    AttributeValueList attrs = op.getAttributes();
                    Attribute<ProductionSpecification> prodSpec = attrs.getAttribute(AttributeId.NOM_ATTR_ID_PROD_SPECN,
                            ProductionSpecification.class);
                    if (null != prodSpec) {
                        log.info("ProductionSpecification");
                        log.info("" + prodSpec.getValue());
                        VariableLabel serial = prodSpec.getValue().getByComponentId(ProductionSpecificationType.SERIAL_NUMBER, ComponentId.ID_COMP_PRODUCT);
                        VariableLabel part = prodSpec.getValue().getByComponentId(ProductionSpecificationType.PART_NUMBER, ComponentId.ID_COMP_PRODUCT);
                        if (null != serial) {
                            deviceIdentity.serial_number = serial.getString();
                        } else {
                            log.warn("No serial number found in the ProductionSpecification");
                        }
                        if(null != part) {
                            if("865240".equals(part.getString())) {
                                iconOrBlank("MX800", "mx800.png");
                            } else {
                                iconOrBlank("MP70", "mp70.png");
                            } 
                        } else {
                            log.warn("No PART NUMBER for ID COMP PRODUCT");
                            writeDeviceIdentity();
                        }                        
                        
                    } else {
                        writeDeviceIdentity();
                    }

                    Attribute<org.mdpnp.devices.philips.intellivue.data.String> firstName = attrs.getAttribute(AttributeId.NOM_ATTR_PT_NAME_GIVEN,
                            org.mdpnp.devices.philips.intellivue.data.String.class);
                    Attribute<org.mdpnp.devices.philips.intellivue.data.String> lastName = attrs.getAttribute(AttributeId.NOM_ATTR_PT_NAME_FAMILY,
                            org.mdpnp.devices.philips.intellivue.data.String.class);
                    Attribute<org.mdpnp.devices.philips.intellivue.data.String> patientId = attrs.getAttribute(AttributeId.NOM_ATTR_PT_ID,
                            org.mdpnp.devices.philips.intellivue.data.String.class);

                    if (null != firstName) {

                    }
                    if (null != lastName) {

                    }
                    if (null != patientId) {

                    }
                }
            }
            super.handle(result);
        }

        @Override
        protected void handle(ExtendedPollDataResult result) {
            long now = System.currentTimeMillis();
            // we could track gaps in poll sequence numbers but instead we're
            // relying on consumers of the data
            // to observe a gap in the data timestamps
            if (result.getPolledObjType().getNomPartition().equals(NomPartition.Object)
                    && result.getPolledObjType().getOidType().getType() == ObjectClass.NOM_MOC_VMO_AL_MON.asInt()) {
                for (SingleContextPoll sop : result.getPollInfoList()) {
                    for (ObservationPoll op : sop.getPollInfo()) {
//                        int handle = op.getHandle().getHandle();
                        AttributeValueList attrs = op.getAttributes();
                        Attribute<DeviceAlertCondition> deviceAlertCondition = attrs.getAttribute(AbstractDemoIntellivue.this.deviceAlertCondition);
                        Attribute<DevAlarmList> patientAlertList = attrs.getAttribute(AbstractDemoIntellivue.this.patientAlertList);
                        Attribute<DevAlarmList> technicalAlertList = attrs.getAttribute(AbstractDemoIntellivue.this.technicalAlertList);

                        if (null != deviceAlertCondition) {
                            writeDeviceAlert(deviceAlertCondition.getValue().getDeviceAlertState().toString());
                        }

                        if (null != patientAlertList) {
                            markOldPatientAlertInstances();
                            for (DevAlarmEntry dae : patientAlertList.getValue().getValue()) {
                                if (dae.getAlMonInfo() instanceof StrAlMonInfo) {
                                    StrAlMonInfo info = (StrAlMonInfo) dae.getAlMonInfo();
                                    String key = dae.getAlCode().getType() + "-" + dae.getAlSource().getType() + "-"
                                            + dae.getObject().getGlobalHandle().getMdsContext() + "-" + dae.getObject().getGlobalHandle().getHandle()
                                            + "-" + dae.getObject().getOidType().getType();
                                    writePatientAlert(key, Normalizer.normalize(info.getString().getString(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", ""));
                                }
                            }
                            clearOldPatientAlertInstances();
                        }
                        if (null != technicalAlertList) {
                            markOldTechnicalAlertInstances();
                            for (DevAlarmEntry dae : technicalAlertList.getValue().getValue()) {
                                if (dae.getAlMonInfo() instanceof StrAlMonInfo) {
                                    StrAlMonInfo info = (StrAlMonInfo) dae.getAlMonInfo();
                                    String key = dae.getAlCode().getType() + "-" + dae.getAlSource().getType() + "-"
                                            + dae.getObject().getGlobalHandle().getMdsContext() + "-" + dae.getObject().getGlobalHandle().getHandle()
                                            + "-" + dae.getObject().getOidType().getType();
                                    writeTechnicalAlert(key, Normalizer.normalize(info.getString().getString(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", ""));
                                }
                            }
                            clearOldTechnicalAlertInstances();
                        }
                    }
                }
            } else {
//                log.info("Here begins a pollInfoList at time " + result.getRelativeTime() + " " + result.getAbsoluteTime());
                for (SingleContextPoll sop : result.getPollInfoList()) {
                    for (ObservationPoll op : sop.getPollInfo()) {
                        int handle = op.getHandle().getHandle();
                        AttributeValueList attrs = op.getAttributes();

                        Attribute<NumericObservedValue> observed = attrs.getAttribute(AbstractDemoIntellivue.this.observed);
                        Attribute<CompoundNumericObservedValue> compoundObserved = attrs.getAttribute(AbstractDemoIntellivue.this.compoundObserved);
                        Attribute<RelativeTime> period = attrs.getAttribute(AbstractDemoIntellivue.this.period);
                        Attribute<SampleArraySpecification> spec = attrs.getAttribute(AbstractDemoIntellivue.this.spec);
                        Attribute<SampleArrayCompoundObservedValue> cov = attrs.getAttribute(AbstractDemoIntellivue.this.cov);
                        Attribute<SampleArrayObservedValue> v = attrs.getAttribute(AbstractDemoIntellivue.this.v);
                        Attribute<ScaleAndRangeSpecification> sar = attrs.getAttribute(AbstractDemoIntellivue.this.sar);
                        Attribute<EnumValue<UnitCode>> unitCode = attrs.getAttribute(AbstractDemoIntellivue.this.unitCode);
                        
                        if(null != unitCode) {
                            handle(handle, unitCode.getValue().getEnum());
                        }

                        if (null != observed) {
                            handle(handle, result.getRelativeTime(), observed.getValue());
                        }

                        if (null != compoundObserved) {
                            for (NumericObservedValue nov : compoundObserved.getValue().getList()) {
                                handle(handle, result.getRelativeTime(), nov);
                            }
                        }
                        
                        if (null != period) {
                            handle(handle, period.getValue());
                        }
                        if (null != sar) {
                            handle(handle, sar.getValue());
                        }
                        if (null != spec) {
                            handle(handle, spec.getValue());
                        }
                        if (null != cov) {
//                            log.info("Time for SampleArrayCompoundObservedValue " + result.getRelativeTime().toMicroseconds());
                            for (SampleArrayObservedValue saov : cov.getValue().getList()) {
                                handle(handle, result.getRelativeTime(), saov, now);
                            }
                        }
                        if (null != v) {
//                            log.info("Time for SampleArrayObservedValue " + result.getRelativeTime().toMicroseconds());
                            handle(handle, result.getRelativeTime(), v.getValue(), now);
                        }
                    }
                }
            }
            super.handle(result);
        }

        private final void populateTime(RelativeTime time, Time_t t) {
            t.copy_from(deviceStartTimeInLocalTime);
            long microseconds = time.toMicroseconds();
            t.sec += microseconds / 1000000L;
            microseconds %= 1000000L;
            // Check for overflow ... will we create an entire second?
            if ((microseconds + t.nanosec / 1000L) >= 1000000L) {
                t.sec++;
                microseconds -= 1000000L;
            }
            t.nanosec += microseconds * 1000L;
        }

        private Time_t sampleTimeNumeric = new Time_t(0,0);
        private final void handle(int handle, RelativeTime time, NumericObservedValue observed) {
            // log.debug(observed.toString());
            ObservedValue ov = ObservedValue.valueOf(observed.getPhysioId().getType());
            if (null != ov) {
                String metricId = numericMetricIds.get(ov);
                if (null != metricId) {
                    // TODO using the local clock instead of device clock in
                    // case device clock is set incorrectly
                    populateTime(time, sampleTimeNumeric);
                    
                    UnitCode unit = UnitCode.valueOf(observed.getUnitCode().getType());

                    if (observed.getMsmtState().isUnavailable()) {
                        putNumericUpdate(ov, handle, numericSample(getNumericUpdate(ov, handle), (Float) null, metricId, handle, 
                                RosettaUnits.units(unit), sampleTimeNumeric));
                    } else {
                        putNumericUpdate(ov, handle,
                                numericSample(getNumericUpdate(ov, handle), observed.getValue().floatValue(), metricId, handle,
                                    RosettaUnits.units(unit), sampleTimeNumeric));
                    }
                } else {
                    log.debug("Unknown numeric:" + observed);
                }
            }

        }



        private final InstanceHolder<ice.Numeric> getNumericUpdate(ObservedValue ov, int handle) {
            Map<Integer, InstanceHolder<ice.Numeric>> forObservedValue = numericUpdates.get(ov);
            if (null == forObservedValue) {
                return null;
            } else {
                return forObservedValue.get(handle);
            }
        }



        private final void putNumericUpdate(ObservedValue ov, int handle, InstanceHolder<ice.Numeric> value) {
            Map<Integer, InstanceHolder<ice.Numeric>> forObservedValue = numericUpdates.get(ov);
            if (null == forObservedValue) {
                forObservedValue = new HashMap<Integer, InstanceHolder<ice.Numeric>>();
                numericUpdates.put(ov, forObservedValue);
            }
            forObservedValue.put(handle, value);
        }
        


        protected void handle(int handle, RelativeTime time, SampleArrayObservedValue v, long now) {
            short[] bytes = v.getValue();
            ObservedValue ov = ObservedValue.valueOf(v.getPhysioId().getType());
            if (null == ov) {
                log.warn("No ObservedValue for " + v.getPhysioId().getType());
            } else {
                String metricId = sampleArrayMetricIds.get(ov);
                if (null == metricId) {
                    log.warn("No metricId for " + ov);
                } else {
                    SampleArraySpecification sas = handleToSampleArraySpecification.get(handle);
                    ScaleAndRangeSpecification sar = handleToScaleAndRangeSpecification.get(handle);
                    UnitCode unitCode = handleToUnitCode.get(handle);
                    RelativeTime rt = handleToUpdatePeriod.get(handle);
                    if (null == sas || null == rt || null == sar || null == unitCode) {
                        log.warn("No SampleArraySpecification or RelativeTime for handle=" + handle + " rt=" + rt + " sas=" + sas + " sar="+sar+ " unitCode="+unitCode);
                    } else {
                        int cnt = sas.getArraySize();
                        // TODO these were once cached, no?
                        MySampleArray w = new MySampleArray();
                        
                        w.setSampleArraySpecification(sas);
                        w.setScaleAndRangeSpecification(sar);
                        
                        int cnt_sa = v.getLength() / (sas.getSampleSize() / Byte.SIZE);

                        if (cnt_sa < cnt) {
                            log.warn("Ignoring insufficient data (" + cnt_sa + ") in the samplearray observation when " + cnt + " expected for " + ov
                                    + " " + handle + " v.getLength()=" + v.getLength() + " sampleSize=" + sas.getSampleSize());
                            return;
                        } else {
                            if (cnt < cnt_sa) {
//                                log.info("Expanding to accomodate " + cnt_sa + " samples where only " + cnt + " were expected");
                                w.setArraySize(cnt_sa);
                                cnt = cnt_sa;
                            }
                            if (w.getArraySize() < cnt) {
//                                log.info("Expanding to accomodate " + cnt + " samples where " + w.getArraySize() + " were expected");
                                w.setArraySize(cnt);
                            }
                            
                            // 
                            for (int i = 0; i < cnt; i++) {
                                w.applyValue(i, bytes);
                            }
                            
                            Map<Integer, SampleCache> handleToSampleCache = sampleArrayCache.get(ov);
                            if(null == handleToSampleCache) {
                                handleToSampleCache = Collections.synchronizedMap( new HashMap<Integer, SampleCache>() );
                                sampleArrayCache.put(ov, handleToSampleCache);
                            }
                            SampleCache sampleCache = handleToSampleCache.get(handle);
                            if(null == sampleCache) {
                                sampleCache = new SampleCache();
                                handleToSampleCache.put(handle, sampleCache);
                            }
                            
                            sampleCache.addNewSamples(w.getNumbers());
                            startEmitFastData(rt.toMilliseconds());
                        }
                    }
                }
            }
        }


        protected void handle(int handle, ScaleAndRangeSpecification sar) {
            handleToScaleAndRangeSpecification.put(handle, sar.clone());
            if(log.isTraceEnabled()) {
                log.trace("Received a ScaleAndRangeSpecification for " + handle + " " + sar);
            }
        }

        protected void handle(int handle, UnitCode unitCode) {
            handleToUnitCode.put(handle, unitCode);
            if(log.isTraceEnabled()) {
                log.trace("Received a unitCode for " + handle + " " + unitCode);
            }
        }
        
        protected void handle(int handle, SampleArraySpecification spec) {
            handleToSampleArraySpecification.put(handle, spec.clone());
            if(log.isTraceEnabled()) {
                log.trace("Received a SampleArraySpecification for " + handle + " " + spec);
            }
        }

        protected void handle(int handle, RelativeTime period) {
            RelativeTime newPeriod = handleToUpdatePeriod.get(handle);
            if (null == newPeriod) {
                newPeriod = new RelativeTime();
                handleToUpdatePeriod.put(handle, newPeriod);
            }
            newPeriod.fromMicroseconds(period.toMicroseconds());
        }
    }
    private class EmitFastData implements Runnable {

        private final int frequency;

        public EmitFastData(final int frequency) {
            this.frequency = frequency;
        }

        private ObservedValue[] observedValues = new ObservedValue[10];
        private Integer[] handles = new Integer[10];
        
        @Override
        public void run() {
            try {
                observedValues = sampleArrayCache.keySet().toArray(observedValues);
                for(ObservedValue ov : observedValues) {
                    if(null == ov) {
                        break;
                    }
                    Map<Integer, SampleCache> sampleCacheByHandle = sampleArrayCache.get(ov);
                    handles = sampleCacheByHandle.keySet().toArray(handles);
                    for(Integer handle : handles) {
                        if(null == handle) {
                            break;
                        }
                        SampleCache sampleCache = sampleCacheByHandle.get(handle);
                        InstanceHolder<ice.SampleArray> sa = getSampleArrayUpdate(ov, handle);
                        RelativeTime rt = handleToUpdatePeriod.get(handle);
                        if (null == rt || null == sampleCache || null == unitCode) {
                            log.warn("No RelativeTime for handle=" + handle + " rt=" + rt + " sampleCache=" + sampleCache + " unitCode="+unitCode);
                            continue;
                        }
                        int frequency = (int)(1000 / rt.toMilliseconds());
                        if(this.frequency == frequency) {
                            if(null != sa) {
                                synchronized(sampleCache) {
                                    Collection<Number> c = sampleCache.emitSamples(BUFFER_SAMPLES, sa.data.metric_id+" "+sa.data.instance_id);
                                    if(null == c) {
                                        putSampleArrayUpdate(ov, handle, null);
                                    } else {
                                        sampleArraySample(sa, c, null);
                                    }
                                }
                            } else {
                                String metric_id = sampleArrayMetricIds.get(ov);
                                UnitCode unitCode = handleToUnitCode.get(handle);
                                synchronized(sampleCache) {
                                    putSampleArrayUpdate(
                                            ov, handle,
                                            sampleArraySample(getSampleArrayUpdate(ov, handle), sampleCache.emitSamples(BUFFER_SAMPLES, metric_id+" "+handle),
                                            metric_id, handle, 
                                            RosettaUnits.units(unitCode),
                                            frequency, null));
                                }
                            }
                        }
                    }
                }

            } catch (Throwable t) {
                log.error("error emitting fast data", t);
            }
        }

    }

    @Override
    protected void unregisterAllNumericInstances() {
        numericUpdates.clear();
        super.unregisterAllNumericInstances();
    }

    @Override
    protected void unregisterAllSampleArrayInstances() {
        sampleArrayUpdates.clear();
        super.unregisterAllSampleArrayInstances();
    }
    
    @Override
    protected void unregisterSampleArrayInstance(InstanceHolder<SampleArray> holder, Time_t timestamp) {
        
        super.unregisterSampleArrayInstance(holder, timestamp);
    }

    protected final Map<ObservedValue, String> numericMetricIds = new HashMap<ObservedValue, String>();
    protected final Map<ObservedValue, String> sampleArrayMetricIds = new HashMap<ObservedValue, String>();

    protected final Map<ObservedValue, Label> numericLabels = new HashMap<ObservedValue, Label>();
    protected final Map<ObservedValue, Label> sampleArrayLabels = new HashMap<ObservedValue, Label>();

    
    private static final class SampleCache {
        private final List<Number> newSamples = Collections.synchronizedList(new ArrayList<Number>());
        private final List<Number> oldSamples = Collections.synchronizedList(new ArrayList<Number>());
        
        public void addNewSamples(Collection<Number> coll) {
            newSamples.addAll(coll);
        }
        
        public Collection<Number> emitSamples(int n, String s) {
            synchronized(newSamples) {
                if(newSamples.isEmpty()) {
                    log.warn(s+" no new samples to emit");
                    return null;
                }
                
                if(newSamples.size() < n) {
                    log.warn(s+" will repeat " + (n - newSamples.size()) + " old samples to make up a shortfall");
                }
                // Move up to n samples from the old list to the new
                List<Number> oldestNewSamples = newSamples.subList(0, n > newSamples.size() ? newSamples.size() : n);
                
                oldSamples.addAll(oldestNewSamples);
                oldestNewSamples.clear();
            }
            synchronized(oldSamples) {
                // If we have insufficient oldSamples (shouldn't happen except maybe at initialization) fill in values
                if(oldSamples.size() < n) {
                    log.warn(s+" filling in " + (n - oldSamples.size()) + " zeros; this should not continue happening");
                    while(oldSamples.size() < n) {
                        oldSamples.add(0, 0);
                    }
                }
                // If we have extra oldSamples then remove them
                if(oldSamples.size() > n) {
                    oldSamples.subList(0, oldSamples.size()-n).clear();
                }
            }
            return oldSamples;
        }
    }
    
    protected final Map<ObservedValue, Map<Integer, InstanceHolder<ice.Numeric>>> numericUpdates = new HashMap<ObservedValue, Map<Integer, InstanceHolder<ice.Numeric>>>();
    protected final Map<ObservedValue, Map<Integer, InstanceHolder<ice.SampleArray>>> sampleArrayUpdates = new HashMap<ObservedValue, Map<Integer, InstanceHolder<ice.SampleArray>>>();
    protected final Map<ObservedValue, Map<Integer, SampleCache>> sampleArrayCache = Collections.synchronizedMap(new HashMap<ObservedValue, Map<Integer, SampleCache>>());
    
    protected static void loadMap(Map<ObservedValue, String> numericMetricIds, Map<ObservedValue, Label> numericLabels,
            Map<ObservedValue, String> sampleArrayMetricIds, Map<ObservedValue, Label> sampleArrayLabels) {

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(DemoEthernetIntellivue.class.getResourceAsStream("intellivue.map")));
            String line = null;

            while (null != (line = br.readLine())) {
                line = line.trim();
                if (line.length() > 0 && '#' != line.charAt(0)) {
                    String v[] = line.split("\t");

                    if (v.length < 4) {
                        log.debug("Bad line:" + line);
                    } else {
                        ObservedValue ov = ObservedValue.valueOf(v[0]);
                        String metric_id = v[1];
                        Label label = Label.valueOf(v[2]);

                        log.trace("Adding " + ov + " mapped to " + metric_id + " with label " + label);
                        v[3] = v[3].trim();
                        if ("W".equals(v[3])) {
                            sampleArrayLabels.put(ov, label);
                            sampleArrayMetricIds.put(ov, metric_id);
                        } else if ("N".equals(v[3])) {
                            numericLabels.put(ov, label);
                            numericMetricIds.put(ov, metric_id);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected final MyIntellivue myIntellivue;

    protected static final Logger log = LoggerFactory.getLogger(DemoEthernetIntellivue.class);

    protected final NetworkLoop networkLoop;
    private final Thread networkLoopThread;
    private final TaskQueue.Task<?> watchdogTask; // ,    serviceSampleArrays;

    public AbstractDemoIntellivue(int domainId, EventLoop eventLoop) throws IOException {
        this(domainId, eventLoop, null);
    }

    public AbstractDemoIntellivue(int domainId, EventLoop eventLoop, NetworkLoop loop) throws IOException {
        super(domainId, eventLoop);
        loadMap(numericMetricIds, numericLabels, sampleArrayMetricIds, sampleArrayLabels);

        deviceIdentity.manufacturer = "Philips";
        deviceIdentity.model = "Intellivue Device";
        AbstractSimulatedDevice.randomUDI(deviceIdentity);
        writeDeviceIdentity();

        if (null == loop) {
            networkLoop = new NetworkLoop();
            networkLoopThread = new Thread(threadGroup, new Runnable() {
                @Override
                public void run() {
                    try {
                        networkLoop.runLoop();
                    } finally {
                        log.info("NetworkLoop.runLoop ended");
                    }
                }
            }, "Network Loop");
            networkLoopThread.setDaemon(true);
            networkLoopThread.start();
        } else {
            networkLoop = loop;
            networkLoopThread = null;
        }

        myIntellivue = new MyIntellivue();

        watchdogTask = new TaskQueue.TaskImpl<Object>() {
            @Override
            public Object doExecute(TaskQueue queue) {
                watchdog();
                return null;
            };
        };
        watchdogTask.setInterval(WATCHDOG_INTERVAL);
        networkLoop.add(watchdogTask);

    }

    protected static final void mustProgress(Time_t time, int sec, int nanosec) {
        if(sec > time.sec || (sec == time.sec && nanosec >= time.nanosec)) {
            // ok because time progressed or stayed the same
            time.sec = sec;
            time.nanosec = nanosec;
        } else {
            log.warn("Not updating Time_t from sec="+time.sec+" nanosec="+time.nanosec+" to sec="+sec+" nanosec="+nanosec+" because that would move backward in time");
        }
    }
    
    protected static class MySampleArray {
        private short sampleSize, significantBits;
        private double lowerAbsoluteValue, upperAbsoluteValue;
        private int lowerScaledValue, upperScaledValue;
        private final List<Number> numbers = new ArrayList<Number>();

        public MySampleArray() {

        }

        public List<Number> getNumbers() {
            return numbers;
        }

        private int[] mask = new int[0];
        private int[] shift = new int[0];

        public void applyValue(int sampleNumber, short[] values) {
            int value = 0;
            for (int i = 0; i < sampleSize; i++) {
                int idx = sampleNumber * sampleSize + i;
                if (idx >= values.length) {
                    log.warn("Cannot locate index " + idx + " where values.length=" + values.length + " sampleSize=" + sampleSize + " sampleNumber="
                            + sampleNumber + " i=" + i);
                } else {
                    if (i >= mask.length) {
                        log.warn("Cannot access i=" + i + " where mask.length=" + mask.length);
                    } else {
                        if (i >= shift.length) {
                            log.warn("Cannot access i=" + i + " where shift.length=" + shift.length);
                        } else {
                            value |= (mask[i] & values[idx]) << shift[i];
                        }
                    }
                }
            }
            if (sampleNumber >= numbers.size()) {
                log.warn("Received sampleNumber=" + sampleNumber + " where expected size was " + numbers.size());
            } else {
                // Scale and range the value
                
                if(!Double.isNaN(lowerAbsoluteValue) && !Double.isNaN(upperAbsoluteValue)) {
                    if(upperScaledValue == lowerScaledValue) {
                        log.error("Not scaling " + value + " between scaled " + lowerScaledValue + " and " + upperScaledValue);
                    } else {
                        double prop = 1.0 * (value - lowerScaledValue) / (upperScaledValue - lowerScaledValue);
                        if(lowerAbsoluteValue == upperAbsoluteValue) {
                            log.error("Not scaling " + value + " (proportionally " + prop+ ") between " + lowerAbsoluteValue + " and " + upperScaledValue);
                        } else {
                            prop = lowerAbsoluteValue + prop * (upperAbsoluteValue - lowerAbsoluteValue);
                            numbers.set(sampleNumber, prop);
                        }
                    } 
                } else {
                    numbers.set(sampleNumber, value);
                }
            }
        }

        public static final int createMask(int prefix) {
            int mask = 0;

            for (int i = 0; i < prefix; i++) {
                mask |= (1 << i);
            }
            return mask;
        }

        private void buildMaskAndShift() {
            if (this.shift.length < sampleSize) {
                this.shift = new int[sampleSize];
            }
            if (this.mask.length < sampleSize) {
                this.mask = new int[sampleSize];
            }
            int significantBits = this.significantBits;
            for (int i = sampleSize - 1; i >= 0; i--) {
                shift[i] = (sampleSize - i - 1) * Byte.SIZE;
                mask[i] = significantBits >= Byte.SIZE ? 0xFF : createMask(significantBits);
                significantBits -= Byte.SIZE;
            }
            log.debug("Mask:" + Arrays.toString(mask) + " Shift:" + Arrays.toString(shift) + " sampleSize=" + sampleSize + " sigBits="
                    + this.significantBits);
        }

        public short getSampleSize() {
            return sampleSize;
        }

        public short getSignificantBits() {
            return significantBits;
        }

        public void setSampleSize(short s) {
            s /= Byte.SIZE;
            if (sampleSize != s) {
                this.sampleSize = s;
                buildMaskAndShift();
            }
        }

        public void setSignificantBits(short s) {
            if (significantBits != s) {
                this.significantBits = s;
                buildMaskAndShift();
            }
        }

        public int getArraySize() {
            return numbers.size();
        }

        public void setArraySize(int size) {
            if (size != numbers.size()) {
                while (numbers.size() < size) {
                    numbers.add(0);
                }
                while (numbers.size() > size) {
                    numbers.remove(0);
                }
            }
        }

        public void setSampleArraySpecification(SampleArraySpecification sas) {
            setSampleSize(sas.getSampleSize());
            setSignificantBits(sas.getSignificantBits());
            setArraySize(sas.getArraySize());
            buildMaskAndShift();
        }
        
        public void setScaleAndRangeSpecification(ScaleAndRangeSpecification sar) {
            this.lowerAbsoluteValue = sar.getLowerAbsoluteValue().doubleValue();
            this.upperAbsoluteValue = sar.getUpperAbsoluteValue().doubleValue();
            this.lowerScaledValue = sar.getLowerScaledValue();
            this.upperScaledValue = sar.getUpperScaledValue();
        }
    }

    protected final void state(ice.ConnectionState state, String connectionInfo) {
        // So actually the state transition will emit the connection info
        if (!stateMachine.transitionWhenLegal(state, 5000L, connectionInfo)) {
            throw new RuntimeException("timed out changing state");
        }

        // If we didn't actually transition state then this will fire the info
        // change
        // If we already did fire it this will be a no op
        setConnectionInfo(connectionInfo);
    }

    protected final Map<Integer, RelativeTime> handleToUpdatePeriod = new HashMap<Integer, RelativeTime>();
    protected final Map<Integer, SampleArraySpecification> handleToSampleArraySpecification = new HashMap<Integer, SampleArraySpecification>();
    protected final Map<Integer, ScaleAndRangeSpecification> handleToScaleAndRangeSpecification = new HashMap<Integer, ScaleAndRangeSpecification>();
    protected final Map<Integer, UnitCode> handleToUnitCode = new HashMap<Integer, UnitCode>();

    

    protected final Attribute<DeviceAlertCondition> deviceAlertCondition = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_DEV_AL_COND,
            DeviceAlertCondition.class);

    protected final Attribute<DevAlarmList> patientAlertList = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_AL_MON_P_AL_LIST,
            DevAlarmList.class);

    protected final Attribute<DevAlarmList> technicalAlertList = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_AL_MON_T_AL_LIST,
            DevAlarmList.class);

    protected final Attribute<CompoundNumericObservedValue> compoundObserved = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_NU_CMPD_VAL_OBS,
            CompoundNumericObservedValue.class);
    protected final Attribute<NumericObservedValue> observed = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_NU_VAL_OBS,
            NumericObservedValue.class);
    protected final Attribute<SampleArrayObservedValue> v = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_SA_VAL_OBS,
            SampleArrayObservedValue.class);
    protected final Attribute<SampleArrayCompoundObservedValue> cov = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_SA_CMPD_VAL_OBS,
            SampleArrayCompoundObservedValue.class);

    protected final Attribute<Type> type = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_ID_TYPE, Type.class);
    protected final Attribute<MetricSpecification> metricSpecification = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_METRIC_SPECN,
            MetricSpecification.class);
    protected final Attribute<TextId> idLabel = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_ID_LABEL, TextId.class);
    protected final Attribute<org.mdpnp.devices.philips.intellivue.data.String> idLabelString = AttributeFactory.getAttribute(
            AttributeId.NOM_ATTR_ID_LABEL_STRING, org.mdpnp.devices.philips.intellivue.data.String.class);
    protected final Attribute<DisplayResolution> displayResolution = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_DISP_RES,
            DisplayResolution.class);
    protected final Attribute<EnumValue<SimpleColor>> color = AttributeFactory
            .getEnumAttribute(AttributeId.NOM_ATTR_COLOR.asOid(), SimpleColor.class);

    protected final Attribute<Handle> handle = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_ID_HANDLE, Handle.class);
    protected final Attribute<RelativeTime> period = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_TIME_PD_SAMP, RelativeTime.class);
    protected final Attribute<SampleArraySpecification> spec = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_SA_SPECN,
            SampleArraySpecification.class);
    protected final Attribute<ScaleAndRangeSpecification> sar = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_SCALE_SPECN_I16, ScaleAndRangeSpecification.class);
    protected final Attribute<EnumValue<UnitCode>> unitCode = AttributeFactory.getEnumAttribute(AttributeId.NOM_ATTR_UNIT_CODE.asOid(), UnitCode.class);


    protected Set<SelectionKey> registrationKeys = new HashSet<SelectionKey>();

    protected void unregisterAll() {
        for (SelectionKey key : registrationKeys) {
            networkLoop.unregister(key, myIntellivue);
        }
        registrationKeys.clear();
    }

    protected static long minPollPeriodToTimeout(long minPollPeriod) {
        if (minPollPeriod <= 3300L) {
            return 10000L;
        } else if (minPollPeriod <= 43000L) {
            return 3 * minPollPeriod;
        } else {
            return 130000L;
        }
    }

    public void connect(InetAddress remote) throws IOException {
        connect(remote, -1, Intellivue.DEFAULT_UNICAST_PORT);
    }

    public void connect(InetAddress r, int prefixLength, int port) throws IOException {
        InetSocketAddress local = null;

        if (prefixLength >= 0) {
            InetAddress l = Network.getLocalAddresses(r, (short) prefixLength, true).get(0);
            local = new InetSocketAddress(l, 0);
        } else {
            local = new InetSocketAddress(0);
        }

        InetSocketAddress remote = new InetSocketAddress(r, port);
        connect(remote, local);

    }

    public void connect(InetSocketAddress remote, InetSocketAddress local) throws IOException {
        switch (stateMachine.getState().ordinal()) {
        case ice.ConnectionState._Disconnected:
            state(ice.ConnectionState.Connecting, "trying " + remote.getAddress().getHostAddress() + ":" + remote.getPort());
            break;
        case ice.ConnectionState._Connecting:
            setConnectionInfo("trying " + remote.getAddress().getHostAddress() + ":" + remote.getPort());
            break;
        default:
            return;
        }

        unregisterAll();

        final DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.socket().setReuseAddress(true);
        channel.bind(local);
        channel.connect(remote);

        registrationKeys.add(networkLoop.register(myIntellivue, channel));

        state(ice.ConnectionState.Negotiating, "");
    }

    @Override
    public void disconnect() {
        synchronized (stateMachine) {
            if (ice.ConnectionState.Disconnected.equals(stateMachine.getState())) {
                return;
            }
            if (!ice.ConnectionState.Connected.equals(stateMachine.getState())) {
                state(ice.ConnectionState.Disconnected, "");
                return;
            } else {
                state(ice.ConnectionState.Disconnecting, "disassociating");
            }
        }
        if (!stateMachine.wait(ice.ConnectionState.Disconnected, 5000L)) {
            log.trace("No disconnect received in response to finish");
        }

    }

    @Override
    public void shutdown() {
        networkLoop.clearTasks();
        networkLoop.cancelThread();
        if (null != networkLoopThread) {
            try {
                networkLoopThread.join();
            } catch (InterruptedException e) {
                log.error("Interrupted", e);
            }
        }


        super.shutdown();
    }

    @Override
    protected String iconResourceName() {
        return "mp70.png";
    }
}
