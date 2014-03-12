/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.philips.intellivue;

import ice.Alert;
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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.QosProfiles;
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
import org.mdpnp.devices.philips.intellivue.data.SimpleColor;
import org.mdpnp.devices.philips.intellivue.data.StrAlMonInfo;
import org.mdpnp.devices.philips.intellivue.data.SystemModel;
import org.mdpnp.devices.philips.intellivue.data.TextId;
import org.mdpnp.devices.philips.intellivue.data.TextIdList;
import org.mdpnp.devices.philips.intellivue.data.Type;
import org.mdpnp.devices.philips.intellivue.data.VariableLabel;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportError;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportMessage;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportResult;
import org.mdpnp.devices.philips.intellivue.dataexport.command.EventReport;
import org.mdpnp.devices.philips.intellivue.dataexport.command.SetResult;
import org.mdpnp.devices.philips.intellivue.dataexport.event.MdsCreateEvent;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.Time_t;
import com.rti.dds.topic.Topic;

public abstract class AbstractDemoIntellivue extends AbstractConnectedDevice {
    @Override
    protected void stateChanged(ConnectionState newState, ConnectionState oldState) {
        super.stateChanged(newState, oldState);
        if (ice.ConnectionState.Connected.equals(oldState) && !ice.ConnectionState.Connected.equals(newState)) {
            lastDataPoll = 0L;
            lastMessageReceived = 0L;
            lastMessageSentTime = 0L;
            lastKeepAlive = 0L;
        }
    }

    protected long clockOffset;

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
                Attribute<org.mdpnp.devices.philips.intellivue.data.String> as = attrs.getAttribute(
                        AttributeId.NOM_ATTR_ID_BED_LABEL, org.mdpnp.devices.philips.intellivue.data.String.class);
                Attribute<ProductionSpecification> ps = attrs.getAttribute(AttributeId.NOM_ATTR_ID_PROD_SPECN,
                        ProductionSpecification.class);

                Attribute<AbsoluteTime> clockTime = attrs.getAttribute(AttributeId.NOM_ATTR_TIME_ABS,
                        AbsoluteTime.class);
                Attribute<RelativeTime> offsetTime = attrs.getAttribute(AttributeId.NOM_ATTR_TIME_REL,
                        RelativeTime.class);

                if (null == clockTime) {
                    log.warn("No NOM_ATTR_TIME_ABS in MDS Create");
                } else if (null == offsetTime) {
                    log.warn("No NOM_ATTR_TIME_REL in MDS Create");
                } else {
                    long currentTime = clockTime.getValue().getDate().getTime();
                    long runTime = offsetTime.getValue().toMilliseconds();
                    clockOffset = currentTime - runTime;
                    log.info("Device started at " + new Date(clockOffset));
                }

                if (ps != null) {
                    log.info("ProductionSpecification");
                    log.info("" + ps.getValue());
                    VariableLabel vl = ps.getValue().getByComponentId(ProductionSpecificationType.SERIAL_NUMBER,
                            ComponentId.ID_COMP_PRODUCT);
                    if (null != vl) {
                        deviceIdentity.serial_number = vl.getString();
                        writeDeviceIdentity();
                    }
                }

                if (null != asm) {
                    deviceIdentity.manufacturer = asm.getValue().getManufacturer().getString();
                    deviceIdentity.model = asm.getValue().getModelNumber().getString();
                    writeDeviceIdentity();
                }
                if (null != as) {
                }
                switch (stateMachine.getState().ordinal()) {
                case ice.ConnectionState._Negotiating:
                    state(ice.ConnectionState.Connected, "");
                    break;
                }

                requestSinglePoll(ObjectClass.NOM_MOC_VMS_MDS, AttributeId.NOM_ATTR_GRP_SYS_PROD);

                requestSet(numericLabels.values().toArray(new Label[0]),
                        sampleArrayLabels.values().toArray(new Label[0]));

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
            log.debug("Negotiated " + pps.getMinPollPeriod().toMilliseconds() + "ms min poll period, timeout="
                    + timeout);
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
                    // Currently we track any incoming message as proof of life ... so nothing special to do here
                    break;
                case NOM_ATTR_GRP_AL_MON:
                    log.debug("Alert Monitor Information has arrived:"+result);
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
                    Attribute<ProductionSpecification> prodSpec = attrs.getAttribute(
                            AttributeId.NOM_ATTR_ID_PROD_SPECN, ProductionSpecification.class);
                    if (null != prodSpec) {
                        log.info("ProductionSpecification");
                        log.info("" + prodSpec.getValue());
                        VariableLabel vlabel = prodSpec.getValue().getByComponentId(
                                ProductionSpecificationType.SERIAL_NUMBER, ComponentId.ID_COMP_PRODUCT);
                        if (vlabel != null) {
                            deviceIdentity.serial_number = vlabel.getString();
                            writeDeviceIdentity();
                        }
                        vlabel = prodSpec.getValue().getByComponentId(ProductionSpecificationType.PART_NUMBER,
                                ComponentId.ID_COMP_PRODUCT);
                    }

                    Attribute<org.mdpnp.devices.philips.intellivue.data.String> firstName = attrs.getAttribute(
                            AttributeId.NOM_ATTR_PT_NAME_GIVEN, org.mdpnp.devices.philips.intellivue.data.String.class);
                    Attribute<org.mdpnp.devices.philips.intellivue.data.String> lastName = attrs
                            .getAttribute(AttributeId.NOM_ATTR_PT_NAME_FAMILY,
                                    org.mdpnp.devices.philips.intellivue.data.String.class);
                    Attribute<org.mdpnp.devices.philips.intellivue.data.String> patientId = attrs.getAttribute(
                            AttributeId.NOM_ATTR_PT_ID, org.mdpnp.devices.philips.intellivue.data.String.class);

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
            // we could track gaps in poll sequence numbers but instead we're relying on consumers of the data
            // to observe a gap in the data timestamps
            if(result.getPolledObjType().getNomPartition().equals(NomPartition.Object) &&
               result.getPolledObjType().getOidType().getType() == ObjectClass.NOM_MOC_VMO_AL_MON.asInt()) {
                for (SingleContextPoll sop : result.getPollInfoList()) {
                    for (ObservationPoll op : sop.getPollInfo()) {
                        int handle = op.getHandle().getHandle();
                        AttributeValueList attrs = op.getAttributes();
                        Attribute<DeviceAlertCondition> deviceAlertCondition = attrs.getAttribute(AbstractDemoIntellivue.this.deviceAlertCondition);
                        Attribute<DevAlarmList> patientAlertList = attrs.getAttribute(AbstractDemoIntellivue.this.patientAlertList);
                        Attribute<DevAlarmList> technicalAlertList = attrs.getAttribute(AbstractDemoIntellivue.this.technicalAlertList);

                        if(null != deviceAlertCondition) {
                            deviceAlertConditionInstance.data.alert_state = deviceAlertCondition.getValue().getDeviceAlertState().toString();
                            deviceAlertConditionWriter.write(deviceAlertConditionInstance.data, deviceAlertConditionInstance.handle);
                        }

                        if(null != patientAlertList) {

                            Set<String> oldPatientAlerts = new HashSet<String>(AbstractDemoIntellivue.this.patientAlertInstances.keySet());


                            for(DevAlarmEntry dae : patientAlertList.getValue().getValue()) {
                                if(dae.getAlMonInfo() instanceof StrAlMonInfo) {
                                    StrAlMonInfo info = (StrAlMonInfo)dae.getAlMonInfo();
                                    String key = dae.getAlCode().getType() + "-"+dae.getAlSource().getType()+"-"+dae.getObject().getGlobalHandle().getMdsContext()+"-"+dae.getObject().getGlobalHandle().getHandle()+"-"+dae.getObject().getOidType().getType();
                                    oldPatientAlerts.remove(key);
                                    InstanceHolder<ice.Alert> alert = patientAlertInstances.get(key);
                                    if(null == alert) {
                                        alert = new InstanceHolder<ice.Alert>();
                                        alert.data = (Alert) ice.Alert.create();
                                        alert.data.unique_device_identifier = deviceIdentity.unique_device_identifier;
                                        alert.data.identifier = key;
                                        alert.handle = patientAlertWriter.register_instance(alert.data);
                                        patientAlertInstances.put(key, alert);
                                    }
                                    alert.data.text = info.getString().getString();
                                    alert.data.text =
                                            Normalizer
                                                .normalize(alert.data.text, Normalizer.Form.NFD)
                                                .replaceAll("[^\\p{ASCII}]", "");
                                    patientAlertWriter.write(alert.data, alert.handle);
                                }
                            }
                            for(String key : oldPatientAlerts) {
                                InstanceHolder<ice.Alert> alert = patientAlertInstances.remove(key);
                                if(null != alert) {
                                    patientAlertWriter.dispose(alert.data, alert.handle);
                                }
                            }
                        }
                        if(null != technicalAlertList) {
                            Set<String> oldTechnicalAlerts = new HashSet<String>(AbstractDemoIntellivue.this.technicalAlertInstances.keySet());
                            for(DevAlarmEntry dae : technicalAlertList.getValue().getValue()) {
                                if(dae.getAlMonInfo() instanceof StrAlMonInfo) {
                                    StrAlMonInfo info = (StrAlMonInfo)dae.getAlMonInfo();
                                    String key = dae.getAlCode().getType() + "-"+dae.getAlSource().getType()+"-"+dae.getObject().getGlobalHandle().getMdsContext()+"-"+dae.getObject().getGlobalHandle().getHandle()+"-"+dae.getObject().getOidType().getType();
                                    oldTechnicalAlerts.remove(key);
                                    InstanceHolder<ice.Alert> alert = technicalAlertInstances.get(key);
                                    if(null == alert) {
                                        alert = new InstanceHolder<ice.Alert>();
                                        alert.data = (Alert) ice.Alert.create();
                                        alert.data.unique_device_identifier = deviceIdentity.unique_device_identifier;
                                        alert.data.identifier = key;
                                        alert.handle = technicalAlertWriter.register_instance(alert.data);
                                        technicalAlertInstances.put(key, alert);
                                    }
                                    alert.data.text = info.getString().getString();
                                    alert.data.text =
                                            Normalizer
                                                .normalize(alert.data.text, Normalizer.Form.NFD)
                                                .replaceAll("[^\\p{ASCII}]", "");
                                    technicalAlertWriter.write(alert.data, alert.handle);
                                }
                            }



                            for(String key : oldTechnicalAlerts) {
                                InstanceHolder<ice.Alert> alert = technicalAlertInstances.remove(key);
                                if(null != alert) {
                                    technicalAlertWriter.dispose(alert.data, alert.handle);
                                }
                            }
                        }
                    }
                }
            } else {

                for (SingleContextPoll sop : result.getPollInfoList()) {
                    for (ObservationPoll op : sop.getPollInfo()) {
                        int handle = op.getHandle().getHandle();
                        AttributeValueList attrs = op.getAttributes();

                        Attribute<NumericObservedValue> observed = attrs.getAttribute(AbstractDemoIntellivue.this.observed);
                        Attribute<CompoundNumericObservedValue> compoundObserved = attrs
                                .getAttribute(AbstractDemoIntellivue.this.compoundObserved);
                        Attribute<RelativeTime> period = attrs.getAttribute(AbstractDemoIntellivue.this.period);
                        Attribute<SampleArraySpecification> spec = attrs.getAttribute(AbstractDemoIntellivue.this.spec);
                        Attribute<SampleArrayCompoundObservedValue> cov = attrs
                                .getAttribute(AbstractDemoIntellivue.this.cov);
                        Attribute<SampleArrayObservedValue> v = attrs.getAttribute(AbstractDemoIntellivue.this.v);

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

                        if (null != spec) {
                            handle(handle, spec.getValue());

                        }
                        if (null != cov) {
                            for (SampleArrayObservedValue saov : cov.getValue().getList()) {
                                handle(handle, result.getRelativeTime(), saov);
                            }
                        }
                        if (null != v) {
                            handle(handle, result.getRelativeTime(), v.getValue());
                        }
                    }
                }
            }
            super.handle(result);
        }

        private final void handle(int handle, RelativeTime time, NumericObservedValue observed) {
            // log.debug(observed.toString());
            ObservedValue ov = ObservedValue.valueOf(observed.getPhysioId().getType());
            if (null != ov) {
                String metricId = numericMetricIds.get(ov);
                if (null != metricId) {
                    long tm = clockOffset + time.toMilliseconds();
                    sampleTime.sec = (int) (tm / 1000L);
                    sampleTime.nanosec = (int) ((tm % 1000L) * 1000000L);
                    if (observed.getMsmtState().isUnavailable()) {
                        putNumericUpdate(ov, handle, 
                                numericSample(getNumericUpdate(ov, handle), (Float) null, metricId, handle, sampleTime));
                    } else {
                        putNumericUpdate(ov, handle, 
                                numericSample(getNumericUpdate(ov, handle), observed.getValue().floatValue(), metricId, handle,
                                        sampleTime));
                    }
                } else {
                    log.debug("Unknown numeric:" + observed);
                }
            }

        }

        protected Time_t sampleTime = new Time_t(0, 0);

        private final MySampleArray getSampleArray(ObservedValue ov, int handle) {
            Map<Integer, MySampleArray> forObservedValue = mySampleArrays.get(ov);
            if(null == forObservedValue) {
                return null;
            } else {
                return forObservedValue.get(handle);
            }

        }
        private final void putSampleArray(ObservedValue ov, int handle, MySampleArray value) {
            Map<Integer, MySampleArray> forObservedValue = mySampleArrays.get(ov);
            if(null == forObservedValue) {
                forObservedValue = new HashMap<Integer, MySampleArray>();
                mySampleArrays.put(ov, forObservedValue);
            }
            forObservedValue.put(handle, value);
        }

        private final InstanceHolder<SampleArray> getSampleArrayUpdate(ObservedValue ov, int handle) {
            Map<Integer, InstanceHolder<SampleArray>> forObservedValue = sampleArrayUpdates.get(ov);
            if(null == forObservedValue) {
                return null;
            } else {
                return forObservedValue.get(handle);
            }
        }
        private final InstanceHolder<ice.Numeric> getNumericUpdate(ObservedValue ov, int handle) {
            Map<Integer, InstanceHolder<ice.Numeric>> forObservedValue = numericUpdates.get(ov);
            if(null == forObservedValue) {
                return null;
            } else {
                return forObservedValue.get(handle);
            }            
        }
        
        private final void putSampleArrayUpdate(ObservedValue ov, int handle, InstanceHolder<SampleArray> value) {
            Map<Integer, InstanceHolder<SampleArray>> forObservedValue = sampleArrayUpdates.get(ov);
            if(null == forObservedValue) {
                forObservedValue = new HashMap<Integer, InstanceHolder<SampleArray>>();
                sampleArrayUpdates.put(ov, forObservedValue);
            }
            forObservedValue.put(handle, value);
        }

        private final void putNumericUpdate(ObservedValue ov, int handle, InstanceHolder<ice.Numeric> value) {
            Map<Integer, InstanceHolder<ice.Numeric>> forObservedValue = numericUpdates.get(ov);
            if(null == forObservedValue) {
                forObservedValue = new HashMap<Integer, InstanceHolder<ice.Numeric>>();
                numericUpdates.put(ov, forObservedValue);
            }
            forObservedValue.put(handle, value);
        }

        protected void handle(int handle, RelativeTime time, SampleArrayObservedValue v) {
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
                    RelativeTime rt = handleToRelativeTime.get(handle);
                    if (null == sas || null == rt) {
                        log.warn("No SampleArraySpecification or RelativeTime for handle=" + handle + " rt=" + rt
                                + " sas=" + sas);
                    } else {

                        MySampleArray w = getSampleArray(ov, handle);
                        if (null == w) {
                            w = new MySampleArray();
                            putSampleArray(ov, handle, w);
                            w.setSampleArraySpecification(sas);
                        }

                        int cnt = sas.getArraySize();
                        int cnt_sa = v.getLength()/(sas.getSampleSize()/Byte.SIZE);
                        
                        if(cnt_sa < cnt) {
                        	log.info("Ignoring insufficient data ("+cnt_sa+") in the samplearray observation when " + cnt + " expected for " + ov + " " + handle + " v.getLength()="+v.getLength() + " sampleSize="+sas.getSampleSize());
                        	return;
                        } else {
                        	if(cnt < cnt_sa) { 
                        		log.info("Expanding to accomodate " + cnt_sa + " samples where only " + cnt + " were expected");
                        		w.setArraySize(cnt_sa);
                        		cnt = cnt_sa;
                        	}
                        	if(w.getArraySize()<cnt) {
                        		log.info("Expanding to accomodate " + cnt + " samples where " + w.getArraySize() + " were expected");
                        		w.setArraySize(cnt);
                        	}
                        	
                        for (int i = 0; i < cnt; i++) {
                            w.applyValue(i, bytes);
                        }
                        }
                        
                        
                        long tm = clockOffset + time.toMilliseconds();
                        sampleTime.sec = (int) (tm / 1000L);
                        sampleTime.nanosec = (int) ((tm % 1000L) * 1000000L);
                        putSampleArrayUpdate(ov, handle,
                                sampleArraySample(getSampleArrayUpdate(ov, handle), w.getNumbers(),
                                        (int) rt.toMilliseconds(), metricId, handle, sampleTime));
                    }
                }
            }
        }

        protected void handle(int handle, SampleArraySpecification spec) {
            SampleArraySpecification sas = new SampleArraySpecification();
            sas.setArraySize(spec.getArraySize());
            sas.setSampleSize(spec.getSampleSize());
            sas.setSignificantBits(spec.getSignificantBits());

            handleToSampleArraySpecification.put(handle, sas);
        }

        protected void handle(int handle, RelativeTime period) {
            RelativeTime newPeriod = new RelativeTime();
            newPeriod.fromMicroseconds(period.toMicroseconds());
            handleToRelativeTime.put(handle, newPeriod);
        }
    }

    @Override
    protected void unregisterAllNumericInstances() {
        numericUpdates.clear();
        super.unregisterAllNumericInstances();
    }

    @Override
    protected void unregisterSampleArrayInstance(InstanceHolder<SampleArray> holder) {
        sampleArrayUpdates.clear();
        super.unregisterSampleArrayInstance(holder);
    }

    protected final Map<ObservedValue, String> numericMetricIds = new HashMap<ObservedValue, String>();
    protected final Map<ObservedValue, String> sampleArrayMetricIds = new HashMap<ObservedValue, String>();

    protected final Map<ObservedValue, Label> numericLabels = new HashMap<ObservedValue, Label>();
    protected final Map<ObservedValue, Label> sampleArrayLabels = new HashMap<ObservedValue, Label>();

    protected final Map<ObservedValue, Map<Integer, InstanceHolder<ice.Numeric>>> numericUpdates = new HashMap<ObservedValue, Map<Integer, InstanceHolder<ice.Numeric>>>();
    protected final Map<ObservedValue, Map<Integer, InstanceHolder<ice.SampleArray>>> sampleArrayUpdates = new HashMap<ObservedValue, Map<Integer, InstanceHolder<ice.SampleArray>>>();
    protected final Map<ObservedValue, Map<Integer, MySampleArray>> mySampleArrays = new HashMap<ObservedValue, Map<Integer, MySampleArray>>();

    protected static void loadMap(Map<ObservedValue, String> numericMetricIds, Map<ObservedValue, Label> numericLabels,
            Map<ObservedValue, String> sampleArrayMetricIds, Map<ObservedValue, Label> sampleArrayLabels) {

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    DemoEthernetIntellivue.class.getResourceAsStream("intellivue.map")));
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

    private static final Logger log = LoggerFactory.getLogger(DemoEthernetIntellivue.class);

    protected final NetworkLoop networkLoop;
    private final Thread networkLoopThread;
    private final TaskQueue.Task<?> watchdogTask;

    protected Topic deviceAlertConditionTopic, patientAlertTopic, technicalAlertTopic;
    protected ice.DeviceAlertConditionDataWriter deviceAlertConditionWriter;
    protected ice.AlertDataWriter patientAlertWriter, technicalAlertWriter;

    protected final InstanceHolder<ice.DeviceAlertCondition> deviceAlertConditionInstance;
    protected final Map<String, InstanceHolder<ice.Alert>> patientAlertInstances = new HashMap<String, InstanceHolder<ice.Alert>>(), technicalAlertInstances = new HashMap<String, InstanceHolder<ice.Alert>>();


    public AbstractDemoIntellivue(int domainId, EventLoop eventLoop) throws IOException {
        this(domainId, eventLoop, null);
    }

    public AbstractDemoIntellivue(int domainId, EventLoop eventLoop, NetworkLoop loop) throws IOException {
        super(domainId, eventLoop);
        loadMap(numericMetricIds, numericLabels, sampleArrayMetricIds, sampleArrayLabels);

        ice.DeviceAlertConditionTypeSupport.register_type(domainParticipant, ice.DeviceAlertConditionTypeSupport.get_type_name());
        deviceAlertConditionTopic = domainParticipant.create_topic(ice.DeviceAlertConditionTopic.VALUE, ice.DeviceAlertConditionTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);

        deviceAlertConditionWriter = (ice.DeviceAlertConditionDataWriter) publisher.create_datawriter_with_profile(deviceAlertConditionTopic, QosProfiles.ice_library, QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);

        ice.AlertTypeSupport.register_type(domainParticipant, ice.AlertTypeSupport.get_type_name());
        patientAlertTopic = domainParticipant.create_topic(ice.PatientAlertTopic.VALUE, ice.AlertTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        technicalAlertTopic = domainParticipant.create_topic(ice.TechnicalAlertTopic.VALUE, ice.AlertTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);

        patientAlertWriter = (ice.AlertDataWriter) publisher.create_datawriter_with_profile(patientAlertTopic, QosProfiles.ice_library, QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);
        technicalAlertWriter = (ice.AlertDataWriter) publisher.create_datawriter_with_profile(technicalAlertTopic, QosProfiles.ice_library, QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);

        deviceIdentity.manufacturer = "Philips";
        deviceIdentity.model = "Intellivue Device";
        AbstractSimulatedDevice.randomUDI(deviceIdentity);
        writeDeviceIdentity();
        {
            ice.DeviceAlertCondition alertCondition = (ice.DeviceAlertCondition) ice.DeviceAlertCondition.create();
            alertCondition.unique_device_identifier = deviceIdentity.unique_device_identifier;
            alertCondition.alert_state = "";
            InstanceHandle_t deviceAlertHandle = deviceAlertConditionWriter.register_instance(alertCondition);
            deviceAlertConditionInstance = new InstanceHolder<ice.DeviceAlertCondition>(alertCondition, deviceAlertHandle);
        }

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

    protected static class MySampleArray {
        private short sampleSize, significantBits;
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
            	if(idx >= values.length) {
            		log.warn("Cannot locate index " + idx + " where values.length="+values.length+" sampleSize="+sampleSize+" sampleNumber="+sampleNumber+" i="+i);
            	} else {
            		if(i >= mask.length) {
            			log.warn("Cannot access i="+i+" where mask.length="+mask.length);
            		} else {
            			if(i>=shift.length) {
            				log.warn("Cannot access i="+i+" where shift.length="+shift.length);
            			} else {
            				value |= (mask[i] & values[idx]) << shift[i];
            			}
            }
            	}
            }
            if(sampleNumber>=numbers.size()) {
            	log.warn("Received sampleNumber="+sampleNumber+" where expected size was "+numbers.size());
            } else {
            numbers.set(sampleNumber, value);
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
            log.debug("Mask:" + Arrays.toString(mask) + " Shift:" + Arrays.toString(shift) + " sampleSize="
                    + sampleSize + " sigBits=" + this.significantBits);
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
                while(numbers.size() > size) {
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
    }

    protected final void state(ice.ConnectionState state, String connectionInfo) {
        // So actually the state transition will emit the connection info
        deviceConnectivity.info = connectionInfo;

        if (!stateMachine.transitionWhenLegal(state, 5000L)) {
            throw new RuntimeException("timed out changing state");
        }

        // If we didn't actually transition state then this will fire the info
        // change
        // If we already did fire it this will be a no op
        setConnectionInfo(connectionInfo);
    }

    protected final Map<Integer, RelativeTime> handleToRelativeTime = new HashMap<Integer, RelativeTime>();
    protected final Map<Integer, SampleArraySpecification> handleToSampleArraySpecification = new HashMap<Integer, SampleArraySpecification>();

    protected final Attribute<DeviceAlertCondition> deviceAlertCondition = AttributeFactory.getAttribute(
            AttributeId.NOM_ATTR_DEV_AL_COND, DeviceAlertCondition.class);

    protected final Attribute<DevAlarmList> patientAlertList = AttributeFactory.getAttribute(
            AttributeId.NOM_ATTR_AL_MON_P_AL_LIST, DevAlarmList.class);

    protected final Attribute<DevAlarmList> technicalAlertList = AttributeFactory.getAttribute(
            AttributeId.NOM_ATTR_AL_MON_T_AL_LIST, DevAlarmList.class);

    protected final Attribute<CompoundNumericObservedValue> compoundObserved = AttributeFactory.getAttribute(
            AttributeId.NOM_ATTR_NU_CMPD_VAL_OBS, CompoundNumericObservedValue.class);
    protected final Attribute<NumericObservedValue> observed = AttributeFactory.getAttribute(
            AttributeId.NOM_ATTR_NU_VAL_OBS, NumericObservedValue.class);
    protected final Attribute<SampleArrayObservedValue> v = AttributeFactory.getAttribute(
            AttributeId.NOM_ATTR_SA_VAL_OBS, SampleArrayObservedValue.class);
    protected final Attribute<SampleArrayCompoundObservedValue> cov = AttributeFactory.getAttribute(
            AttributeId.NOM_ATTR_SA_CMPD_VAL_OBS, SampleArrayCompoundObservedValue.class);

    protected final Attribute<Type> type = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_ID_TYPE, Type.class);
    protected final Attribute<MetricSpecification> metricSpecification = AttributeFactory.getAttribute(
            AttributeId.NOM_ATTR_METRIC_SPECN, MetricSpecification.class);
    protected final Attribute<TextId> idLabel = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_ID_LABEL,
            TextId.class);
    protected final Attribute<org.mdpnp.devices.philips.intellivue.data.String> idLabelString = AttributeFactory
            .getAttribute(AttributeId.NOM_ATTR_ID_LABEL_STRING, org.mdpnp.devices.philips.intellivue.data.String.class);
    protected final Attribute<DisplayResolution> displayResolution = AttributeFactory.getAttribute(
            AttributeId.NOM_ATTR_DISP_RES, DisplayResolution.class);
    protected final Attribute<EnumValue<SimpleColor>> color = AttributeFactory.getEnumAttribute(
            AttributeId.NOM_ATTR_COLOR.asOid(), SimpleColor.class);

    protected final Attribute<Handle> handle = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_ID_HANDLE,
            Handle.class);
    protected final Attribute<RelativeTime> period = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_TIME_PD_SAMP,
            RelativeTime.class);
    protected final Attribute<SampleArraySpecification> spec = AttributeFactory.getAttribute(
            AttributeId.NOM_ATTR_SA_SPECN, SampleArraySpecification.class);

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
            state(ice.ConnectionState.Connecting,
                    "trying " + remote.getAddress().getHostAddress() + ":" + remote.getPort());
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
        publisher.delete_datawriter(deviceAlertConditionWriter);
        deviceAlertConditionWriter = null;
        domainParticipant.delete_topic(deviceAlertConditionTopic);
        deviceAlertConditionTopic = null;
        ice.DeviceAlertConditionTypeSupport.unregister_type(domainParticipant, ice.DeviceAlertConditionTypeSupport.get_type_name());

        publisher.delete_datawriter(patientAlertWriter);
        publisher.delete_datawriter(technicalAlertWriter);
        patientAlertWriter = technicalAlertWriter = null;

        domainParticipant.delete_topic(patientAlertTopic);
        domainParticipant.delete_topic(technicalAlertTopic);

        patientAlertTopic = technicalAlertTopic = null;

        ice.AlertTypeSupport.unregister_type(domainParticipant, ice.AlertTypeSupport.get_type_name());

        super.shutdown();
    }

    @Override
    protected String iconResourceName() {
        return "mp70.png";
    }
}
