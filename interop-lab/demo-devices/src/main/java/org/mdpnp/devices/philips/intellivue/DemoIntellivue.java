/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.connected.AbstractConnectedDevice;
import org.mdpnp.devices.philips.intellivue.action.ExtendedPollDataResult;
import org.mdpnp.devices.philips.intellivue.action.ObservationPoll;
import org.mdpnp.devices.philips.intellivue.action.SingleContextPoll;
import org.mdpnp.devices.philips.intellivue.action.SinglePollDataResult;
import org.mdpnp.devices.philips.intellivue.association.AssociationAbort;
import org.mdpnp.devices.philips.intellivue.association.AssociationAccept;
import org.mdpnp.devices.philips.intellivue.association.AssociationDisconnect;
import org.mdpnp.devices.philips.intellivue.association.AssociationFinish;
import org.mdpnp.devices.philips.intellivue.association.AssociationRefuse;
import org.mdpnp.devices.philips.intellivue.association.impl.AssociationFinishImpl;
import org.mdpnp.devices.philips.intellivue.attribute.Attribute;
import org.mdpnp.devices.philips.intellivue.attribute.AttributeFactory;
import org.mdpnp.devices.philips.intellivue.connectindication.ConnectIndication;
import org.mdpnp.devices.philips.intellivue.data.AttributeId;
import org.mdpnp.devices.philips.intellivue.data.AttributeValueList;
import org.mdpnp.devices.philips.intellivue.data.ComponentId;
import org.mdpnp.devices.philips.intellivue.data.CompoundNumericObservedValue;
import org.mdpnp.devices.philips.intellivue.data.DisplayResolution;
import org.mdpnp.devices.philips.intellivue.data.EnumValue;
import org.mdpnp.devices.philips.intellivue.data.Handle;
import org.mdpnp.devices.philips.intellivue.data.IPAddressInformation;
import org.mdpnp.devices.philips.intellivue.data.Label;
import org.mdpnp.devices.philips.intellivue.data.MetricSpecification;
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
import org.mdpnp.devices.philips.intellivue.data.SystemModel;
import org.mdpnp.devices.philips.intellivue.data.TextId;
import org.mdpnp.devices.philips.intellivue.data.TextIdList;
import org.mdpnp.devices.philips.intellivue.data.Type;
import org.mdpnp.devices.philips.intellivue.data.VariableLabel;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportMessage;
import org.mdpnp.devices.philips.intellivue.dataexport.command.EventReport;
import org.mdpnp.devices.philips.intellivue.dataexport.command.SetResult;
import org.mdpnp.devices.philips.intellivue.dataexport.event.MdsCreateEvent;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoIntellivue extends AbstractConnectedDevice {
    @Override
    protected void stateChanged(ConnectionState newState, ConnectionState oldState) {
        super.stateChanged(newState, oldState);
        if (ice.ConnectionState.Connected.equals(oldState) && !ice.ConnectionState.Connected.equals(newState)) {
            lastDataPoll = 0L;
            lastMessageReceived = 0L;
            lastMessageSent = 0L;
            lastKeepAlive = 0L;
        }
    }
    protected final static long WATCHDOG_INTERVAL = 200L;
    // Maximum time between message receipt
    protected final static long IN_CONNECTION_TIMEOUT = 5000L;
    // Assert a keepalive if no message received for this long
    protected final static long IN_CONNECTION_ASSERT = 4000L;

    protected static long OUT_CONNECTION_ASSERT = 8000L;


    protected final static long CONTINUOUS_POLL_INTERVAL = 60000L;
    protected final static long CONTINUOUS_POLL_ASSERT = 50000L;

    protected final static long ASSOCIATION_REQUEST_INTERVAL = 2000L;
    protected final static long FINISH_REQUEST_INTERVAL = 500L;


    private long lastAssociationRequest = 0L;
    private long lastFinishRequest = 0L;
    private long lastDataPoll = 0L;
    private long lastMessageReceived = 0L;
    private long lastKeepAlive = 0L;
    private long lastMessageSent = 0L;

    protected void watchdog() {
        long now = System.currentTimeMillis();
        switch(stateMachine.getState().ordinal()) {
        case ice.ConnectionState._Negotiating:
            if(now - lastAssociationRequest >= ASSOCIATION_REQUEST_INTERVAL) {
                try {
                    myIntellivue.requestAssociation();
                    lastAssociationRequest = now;
                } catch (IOException e1) {
                    log.error("requesting association", e1);
                }
            }
            break;
        case ice.ConnectionState._Disconnecting:
            if(now - lastFinishRequest >= FINISH_REQUEST_INTERVAL) {
                try {
                    myIntellivue.send(new AssociationFinishImpl());
                    lastFinishRequest = now;
                } catch (IOException e1) {
                    log.error("sending association finish", e1);
                }
            }
            break;
        case ice.ConnectionState._Connected:
            if(now - lastMessageReceived >= IN_CONNECTION_TIMEOUT) {
                // Check that the last was acknowledged
                state(ice.ConnectionState.Negotiating, "timeout receiving  messages");
                return;
            } else if( (now - lastMessageReceived >= IN_CONNECTION_ASSERT || now - lastMessageSent >= OUT_CONNECTION_ASSERT) && now - lastKeepAlive >= Math.min(OUT_CONNECTION_ASSERT, IN_CONNECTION_ASSERT)) {
                try {
                    myIntellivue.requestSinglePoll(ObjectClass.NOM_MOC_VMO_AL_MON,
                            AttributeId.NOM_ATTR_GRP_VMO_STATIC);
                    lastKeepAlive = now;
                } catch (IOException e) {
                    state(ice.ConnectionState.Negotiating, "failure to send a keepalive");
                    log.error("requesting a keep alive (static attributes of the alarm monitor object)", e);
                }
            } else if(now - lastDataPoll >= CONTINUOUS_POLL_ASSERT) {
                try {
                    myIntellivue.requestExtendedPoll(ObjectClass.NOM_MOC_VMO_METRIC_NU, CONTINUOUS_POLL_INTERVAL);
                    myIntellivue.requestExtendedPoll(ObjectClass.NOM_MOC_VMO_METRIC_SA_RT, CONTINUOUS_POLL_INTERVAL);
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
        public synchronized boolean send(Message message) throws IOException {
            lastMessageSent = System.currentTimeMillis();
            return super.send(message);
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
            Attribute<TextIdList> ati = attrs.getAttribute(AttributeId.NOM_ATTR_POLL_NU_PRIO_LIST,
                    TextIdList.class);

            if (null != ati
                    && ati.getValue().containsAll(numericLabels.values().toArray(new Label[0]))) {
            } else {
                log.warn("Numerics priority list does not contain all of our requested labels:" + ati);
            }

            ati = attrs.getAttribute(AttributeId.NOM_ATTR_POLL_RTSA_PRIO_LIST, TextIdList.class);
            if (null != ati
                    && ati.getValue().containsAll(sampleArrayLabels.values().toArray(new Label[0]))) {
            } else {
                log.warn("SampleArray priority list does not contain all requested labels:" + ati);
            }
        }

        @Override
        protected void handle(EventReport eventReport, boolean confirm) throws IOException {
            super.handle(eventReport, confirm);
            switch (ObjectClass.valueOf(eventReport.getEventType().getType())) {
            case NOM_NOTI_MDS_CREAT:
                MdsCreateEvent createEvent = (MdsCreateEvent) eventReport.getEvent();
                AttributeValueList attrs = createEvent.getAttributes();
                Attribute<SystemModel> asm = attrs.getAttribute(AttributeId.NOM_ATTR_ID_MODEL,
                        SystemModel.class);
                Attribute<org.mdpnp.devices.philips.intellivue.data.String> as = attrs.getAttribute(
                        AttributeId.NOM_ATTR_ID_BED_LABEL, org.mdpnp.devices.philips.intellivue.data.String.class);
                Attribute<ProductionSpecification> ps = attrs.getAttribute(
                        AttributeId.NOM_ATTR_ID_PROD_SPECN, ProductionSpecification.class);

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

                requestSinglePoll(ObjectClass.NOM_MOC_VMS_MDS, AttributeId.NOM_ATTR_GRP_SYS_PROD);

                requestSet(numericLabels.values().toArray(new Label[0]), sampleArrayLabels.values().toArray(new Label[0]));

                break;
            default:
                break;
            }

        }

        @Override
        protected void handle(SocketAddress sockaddr, AssociationRefuse message) {
            switch(stateMachine.getState().ordinal()) {
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
            switch(stateMachine.getState().ordinal()) {
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
            switch(stateMachine.getState().ordinal()) {
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
            switch(stateMachine.getState().ordinal()) {
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
            switch(stateMachine.getState().ordinal()) {
            case ice.ConnectionState._Negotiating:
                state(ice.ConnectionState.Connected, "");
                break;
            }
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
                    // This is what we're using as a keepalive
                    // but currently only tracking the receipt of any DataExport message
//                    lastKeepAliveRecvInvokeId = result.getAction().getMessage().getInvoke();
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

                    Attribute<org.mdpnp.devices.philips.intellivue.data.String> firstName = attrs
                            .getAttribute(AttributeId.NOM_ATTR_PT_NAME_GIVEN, org.mdpnp.devices.philips.intellivue.data.String.class);
                    Attribute<org.mdpnp.devices.philips.intellivue.data.String> lastName = attrs
                            .getAttribute(AttributeId.NOM_ATTR_PT_NAME_FAMILY, org.mdpnp.devices.philips.intellivue.data.String.class);
                    Attribute<org.mdpnp.devices.philips.intellivue.data.String> patientId = attrs
                            .getAttribute(AttributeId.NOM_ATTR_PT_ID, org.mdpnp.devices.philips.intellivue.data.String.class);

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
//            log.debug("ExtendedPollDataResult");
            // log.debug(lineWrap(result.toString()));
            for (SingleContextPoll sop : result.getPollInfoList()) {
                for (ObservationPoll op : sop.getPollInfo()) {
                    int handle = op.getHandle().getHandle();
                    AttributeValueList attrs = op.getAttributes();

                    Attribute<NumericObservedValue> observed = attrs.getAttribute(DemoIntellivue.this.observed);
                    Attribute<CompoundNumericObservedValue> compoundObserved = attrs.getAttribute(DemoIntellivue.this.compoundObserved);
//                    Attribute<Type> type = attrs.getAttribute(DemoIntellivue.this.type);
//                    Attribute<MetricSpecification> metricSpecification = attrs.getAttribute(DemoIntellivue.this.metricSpecification);
//                    Attribute<TextId> idLabel  = attrs.getAttribute(DemoIntellivue.this.idLabel);
//                    Attribute<org.mdpnp.devices.philips.intellivue.data.String> idLabelString = attrs.getAttribute(DemoIntellivue.this.idLabelString);
//                    Attribute<DisplayResolution> displayResolution = attrs.getAttribute(DemoIntellivue.this.displayResolution);
//                    Attribute<EnumValue<SimpleColor>> color = attrs.getAttribute(DemoIntellivue.this.color);
                    Attribute<Handle> objectHandle = attrs.getAttribute(DemoIntellivue.this.handle);
                    Attribute<RelativeTime> period = attrs.getAttribute(DemoIntellivue.this.period);
                    Attribute<SampleArraySpecification> spec = attrs.getAttribute(DemoIntellivue.this.spec);
                    Attribute<SampleArrayCompoundObservedValue> cov = attrs.getAttribute(DemoIntellivue.this.cov);
                    Attribute<SampleArrayObservedValue> v = attrs.getAttribute(DemoIntellivue.this.v);

                    if (null != observed) {
//                        log.debug(observed.toString());
                        handle(handle, observed.getValue());
                    }

                    if (null != compoundObserved) {
                        for (NumericObservedValue nov : compoundObserved.getValue().getList()) {
                            handle(handle, nov);
                        }
                    }

//                    if (null != type) {
//                        log.debug(type.toString());
//                    }


//                    if (null != metricSpecification) {
//                        log.debug(metricSpecification.toString());
//                    }

//                    if (null != idLabel) {
//                        log.debug(idLabel.toString());
//                    }

//                    if (null != idLabelString) {
//                        log.debug(idLabelString.toString());
//                    }

//                    if (null != displayResolution) {
//                        log.debug(displayResolution.toString());
//                    }

//                    if (null != color) {
//                        log.debug(color.toString());
//                    }

                    if (null != period) {
//                        log.debug(period.toString());
                        handle(handle, period.getValue());
                    }

                    if (null != objectHandle) {
//                        log.debug(objectHandle.toString());
                        handle(handle, objectHandle.getValue());
                    }

                    if (null != spec) {
//                        log.debug(spec.toString());
                        handle(handle, spec.getValue());

                    }
                    if (null != cov) {
                        for (SampleArrayObservedValue saov : cov.getValue().getList()) {
                            handle(handle, saov);
                        }
                    }
                    if (null != v) {
//                        log.debug(v.toString());
                        handle(handle, v.getValue());
                    }
                }
            }
            // lastPoint.setTime(System.currentTimeMillis());

            super.handle(result);
        }

        private void handle(int handle, Handle value) {
            // log.debug(value.toString());
        }

        private final void handle(int handle, NumericObservedValue observed) {
            // log.debug(observed.toString());
            ObservedValue ov = ObservedValue.valueOf(observed.getPhysioId().getType());
            if (null != ov) {
                String metricId = numericMetricIds.get(ov);
                if(null != metricId) {
                    if(observed.getMsmtState().isUnavailable()) {
                        numericUpdates.put(ov, numericSample(numericUpdates.get(ov), (Float) null, metricId));
                    } else {
                        numericUpdates.put(ov, numericSample(numericUpdates.get(ov), observed.getValue().floatValue(), metricId));
                    }
                } else {
                    log.debug("Unknown numeric:" + observed);
                }
            }

        }

        protected void handle(int handle, SampleArrayObservedValue v) {
            short[] bytes = v.getValue();
            ObservedValue ov = ObservedValue.valueOf(v.getPhysioId().getType());
            if (null == ov) {
                log.warn("No ObservedValue for " + v.getPhysioId().getType());
            } else {
                String metricId = sampleArrayMetricIds.get(ov);
                if(null == metricId) {
                    log.warn("No metricId for " + ov);
                } else {
                    MySampleArray w = sampleArrayUpdates.get(ov);
                    if(null == w) {
                        SampleArraySpecification sas = handleToSampleArraySpecification.get(handle);
                        RelativeTime rt = handleToRelativeTime.get(handle);
                        if(null != sas && null != rt) {
                            w = new MySampleArray(createSampleArrayInstance(metricId));
                            w.setSampleSize(sas.getSampleSize());
                            w.setSignificantBits(sas.getSignificantBits());
                            w.holder.data.values.setSize(sas.getArraySize());
                            w.holder.data.millisecondsPerSample = (int) rt.toMilliseconds();
                            sampleArrayUpdates.put(ov, w);
                        } else {
                            log.warn("No SampleArraySpecification or RelativeTime for handle="+handle);
                            return;
                        }
                    }

                    int cnt = w.holder.data.values.size();
                    for (int i = 0; i < cnt; i++) {
                        w.applyValue(i, bytes);
                    }
                    sampleArrayDataWriter.write(w.holder.data, w.holder.handle);
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

    protected final Map<ObservedValue, InstanceHolder<ice.Numeric>> numericUpdates = new HashMap<ObservedValue, InstanceHolder<ice.Numeric>>();
    protected final Map<ObservedValue, MySampleArray> sampleArrayUpdates = new HashMap<ObservedValue, MySampleArray>();

    protected static void loadMap(Map<ObservedValue, String> numericMetricIds, Map<ObservedValue, Label> numericLabels, Map<ObservedValue, String> sampleArrayMetricIds, Map<ObservedValue, Label> sampleArrayLabels) {

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    DemoIntellivue.class.getResourceAsStream("intellivue.map")));
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

    private final MyIntellivue myIntellivue;

    private static final Logger log = LoggerFactory.getLogger(DemoIntellivue.class);

    private final NetworkLoop networkLoop;
    private final Thread networkLoopThread;
    private final TaskQueue.Task<?> watchdogTask;

    public DemoIntellivue(int domainId, EventLoop eventLoop) throws IOException {
        this(domainId, eventLoop, null);
    }

    public DemoIntellivue(int domainId, EventLoop eventLoop, NetworkLoop loop) throws IOException {
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

    protected static class MySampleArray {
        private short sampleSize, significantBits;
        private final InstanceHolder<ice.SampleArray> holder;

        public MySampleArray(InstanceHolder<ice.SampleArray> holder) {
            this.holder = holder;
        }

        private int[] mask = new int[0];
        private int[] shift = new int[0];

        public void applyValue(int sampleNumber, short[] values) {
            int value = 0;
            for (int i = 0; i < sampleSize; i++) {
                value |= (mask[i] & values[sampleNumber * sampleSize + i]) << shift[i];
            }
            holder.data.values.setFloat(sampleNumber, value);
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
            this.sampleSize = (short) (s / Byte.SIZE);
            buildMaskAndShift();
        }

        public void setSignificantBits(short s) {
            this.significantBits = s;
            buildMaskAndShift();
        }
    }

    @Override
    public void connect(String address) {
        if (null == address || "".equals(address)) {
            try {
                String[] hosts = listenForConnectIndication();

                if (null == hosts) {
                    state(ice.ConnectionState.Disconnected, "no broadcast addresses");
                } else {
                    state(ice.ConnectionState.Connecting, "listening  on " + Arrays.toString(hosts));
                }
            } catch (IOException e) {
                log.error("Awaiting beacon", e);
            }

        } else {
            try {
                int port = Intellivue.DEFAULT_UNICAST_PORT;

                int colon = address.lastIndexOf(':');
                if (colon >= 0) {
                    port = Integer.parseInt(address.substring(colon + 1, address.length()));
                    address = address.substring(0, colon);
                }

                InetAddress addr = InetAddress.getByName(address);

                connect(addr, -1, port);

            } catch (UnknownHostException e) {
                log.error("Trying to connect to address", e);
            } catch (IOException e) {
                log.error("Trying to connect to address", e);
            }
        }

    }

    protected final void state(ice.ConnectionState state, String connectionInfo) {
        // So actually the state transition will emit the connection info
        deviceConnectivity.info = connectionInfo;

        if (!stateMachine.transitionWhenLegal(state, 5000L)) {
            throw new RuntimeException("timed out changing state");
        }

        // If we didn't actually transition state then this will fire the info change
        // If we already did fire it this will be a no op
        setConnectionInfo(connectionInfo);
    }

    protected final Map<Integer, RelativeTime> handleToRelativeTime = new HashMap<Integer, RelativeTime>();
    protected final Map<Integer, SampleArraySpecification> handleToSampleArraySpecification = new HashMap<Integer, SampleArraySpecification>();



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

    public void connect(InetAddress remote) throws IOException {
        connect(remote, -1, Intellivue.DEFAULT_UNICAST_PORT);
    }

    public String[] listenForConnectIndication() throws IOException {
        unregisterAll();

        List<Network.AddressSubnet> broadcastAddresses = Network.getBroadcastAddresses();
        if (broadcastAddresses.isEmpty()) {
            return null;
        } else {
            List<String> hosts = new ArrayList<String>();
            for (Network.AddressSubnet address : broadcastAddresses) {
                final DatagramChannel channel = DatagramChannel.open();
                channel.configureBlocking(false);
                channel.socket().setReuseAddress(true);
                channel.socket().bind(new InetSocketAddress(address.getInetAddress(), Intellivue.BROADCAST_PORT));
                registrationKeys.add(networkLoop.register(myIntellivue, channel));

                hosts.add(address.getInetAddress().getHostAddress());
            }
            return hosts.toArray(new String[0]);
        }
    }

    @SuppressWarnings("unused")
    private InetAddress lastRemote;
    @SuppressWarnings("unused")
    private int lastPrefixLength;
    private Set<SelectionKey> registrationKeys = new HashSet<SelectionKey>();

    private void unregisterAll() {
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

    public void connect(InetAddress remote, int prefixLength, int port) throws IOException {
        switch(stateMachine.getState().ordinal()) {
        case ice.ConnectionState._Disconnected:
            state(ice.ConnectionState.Connecting, "trying " + remote.getHostAddress() + ":" + port);
            break;
        case ice.ConnectionState._Connecting:
            setConnectionInfo("trying " + remote.getHostAddress() + ":" + port);
            break;
        default:
            return;
        }
        InetAddress local = null;

        lastRemote = remote;
        lastPrefixLength = prefixLength;

        unregisterAll();

        final DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.socket().setReuseAddress(true);

        if (prefixLength >= 0) {
            local = Network.getLocalAddresses(remote, (short) prefixLength, true).get(0);
            channel.socket().bind(new InetSocketAddress(local, 0));
        }

        channel.connect(new InetSocketAddress(remote, port));

        registrationKeys.add(networkLoop.register(myIntellivue, channel));

        state(ice.ConnectionState.Negotiating, "");
    }

    @Override
    public void disconnect() {
        state(ice.ConnectionState.Disconnecting, "disassociating");
        if(!stateMachine.wait(ice.ConnectionState.Disconnected, 5000L)) {
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
    protected ice.ConnectionType getConnectionType() {
        return ice.ConnectionType.Network;
    }

    @Override
    protected String iconResourceName() {
        return "mp70.png";
    }
}
