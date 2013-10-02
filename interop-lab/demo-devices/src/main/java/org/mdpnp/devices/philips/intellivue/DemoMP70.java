/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.philips.intellivue;

import ice.ConnectionState;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import org.mdpnp.devices.philips.intellivue.association.AssociationRefuse;
import org.mdpnp.devices.philips.intellivue.association.impl.AssociationFinishImpl;
import org.mdpnp.devices.philips.intellivue.attribute.Attribute;
import org.mdpnp.devices.philips.intellivue.attribute.AttributeFactory;
import org.mdpnp.devices.philips.intellivue.connectindication.ConnectIndication;
import org.mdpnp.devices.philips.intellivue.data.AttributeId;
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
import org.mdpnp.devices.philips.intellivue.dataexport.command.EventReport;
import org.mdpnp.devices.philips.intellivue.dataexport.command.SetResult;
import org.mdpnp.devices.philips.intellivue.dataexport.event.MdsCreateEvent;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoMP70 extends AbstractConnectedDevice {
    @Override
    protected void stateChanged(ConnectionState newState, ConnectionState oldState) {
        super.stateChanged(newState, oldState);
        if (ice.ConnectionState.Connected.equals(oldState) && !ice.ConnectionState.Connected.equals(newState)) {

        }

    }

    private class MyIntellivue extends Intellivue {
        private final Logger log = LoggerFactory.getLogger(MyIntellivue.class);

        public MyIntellivue() {
            super();
        }

        @Override
        protected void handle(SetResult result, boolean confirmed) {
            super.handle(result, confirmed);
            Attribute<TextIdList> ati = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_POLL_NU_PRIO_LIST,
                    TextIdList.class);

            if (result.getAttributes().get(ati)
                    && ati.getValue().containsAll(numericLabels.values().toArray(new Label[0]))) {

                TaskQueue.Task<Object> nuTask = new TaskQueue.TaskImpl<Object>() {
                    @Override
                    public Object doExecute(TaskQueue queue) {
                        try {
                            requestExtendedPoll(ObjectClass.NOM_MOC_VMO_METRIC_NU, CONTINUOUS_POLL_INTERVAL);
                        } catch (IOException e) {
                            log.error("requesting extended poll of numeric data", e);
                        }
                        return null;
                    };
                };
                nuTask.setInterval(CONTINUOUS_POLL_INTERVAL);
                networkLoop.add(nuTask);
            } else {
                log.warn("Numerics priority list does not contain all of our requested labels:" + ati);
            }

            ati = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_POLL_RTSA_PRIO_LIST, TextIdList.class);
            if (result.getAttributes().get(ati)
                    && ati.getValue().containsAll(waveformLabels.values().toArray(new Label[0]))) {
                TaskQueue.Task<Object> saTask = new TaskQueue.TaskImpl<Object>() {
                    @Override
                    public Object doExecute(TaskQueue queue) {
                        try {
                            requestExtendedPoll(ObjectClass.NOM_MOC_VMO_METRIC_SA_RT, CONTINUOUS_POLL_INTERVAL);
                        } catch (IOException e) {
                            log.error("requesting extended poll of samplearray data", e);
                        }
                        return null;
                    }

                };
                saTask.setInterval(CONTINUOUS_POLL_INTERVAL);
                networkLoop.add(saTask);
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
                Attribute<SystemModel> asm = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_ID_MODEL,
                        SystemModel.class);
                Attribute<org.mdpnp.devices.philips.intellivue.data.String> as = AttributeFactory.getAttribute(
                        AttributeId.NOM_ATTR_ID_BED_LABEL, org.mdpnp.devices.philips.intellivue.data.String.class);
                Attribute<ProductionSpecification> ps = AttributeFactory.getAttribute(
                        AttributeId.NOM_ATTR_ID_PROD_SPECN, ProductionSpecification.class);

                if (createEvent.getAttributes().get(ps)) {
                    log.info("ProductionSpecification");
                    log.info("" + ps.getValue());
                    VariableLabel vl = ps.getValue().getByComponentId(ProductionSpecificationType.SERIAL_NUMBER,
                            ComponentId.ID_COMP_PRODUCT);
                    if (null != vl) {
                        deviceIdentity.serial_number = vl.getString();
                        writeDeviceIdentity();
                    }
                }

                if (createEvent.getAttributes().get(asm)) {
                    deviceIdentity.manufacturer = asm.getValue().getManufacturer().getString();
                    writeDeviceIdentity();
                }
                if (createEvent.getAttributes().get(as)) {
                    deviceIdentity.model = asm.getValue().getModelNumber().getString();
                    writeDeviceIdentity();
                }

                state(ice.ConnectionState.Connected, null);
                requestSinglePoll(ObjectClass.NOM_MOC_VMS_MDS, AttributeId.NOM_ATTR_GRP_SYS_PROD);
                TaskQueue.Task<Object> nuTask = new TaskQueue.TaskImpl<Object>() {
                    @Override
                    public Object doExecute(TaskQueue queue) {
                        try {
                            requestSinglePoll(ObjectClass.NOM_MOC_PT_DEMOG, AttributeId.NOM_ATTR_GRP_PT_DEMOG);
                        } catch (IOException e) {
                            log.error("single poll for patient demographic data");
                        }
                        return null;
                    };
                };
                nuTask.setInterval(CONTINUOUS_POLL_INTERVAL);
                networkLoop.add(nuTask);
                requestSet(numericLabels.values().toArray(new Label[0]), waveformLabels.values().toArray(new Label[0]));

                break;
            default:
                break;
            }

        }

        @Override
        protected void handle(SocketAddress sockaddr, AssociationRefuse message) {
            state(ice.ConnectionState.Disconnected, "refused");
            super.handle(sockaddr, message);
        }

        @Override
        protected void handle(SocketAddress sockaddr, AssociationAbort message) {
            state(ice.ConnectionState.Disconnected, "aborted");
            super.handle(sockaddr, message);
        }

        @Override
        protected void handle(SocketAddress sockaddr, AssociationDisconnect message) {
            state(ice.ConnectionState.Disconnected, "disconnected");
            super.handle(sockaddr, message);
        }

        private int lastKeepAliveSentInvokeId = -1;
        private int lastKeepAliveRecvInvokeId = -1;
        private TaskQueue.Task<Object> keepAliveTask;

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
            state(ice.ConnectionState.Negotiating, "accepted");
            PollProfileSupport pps = message.getUserInfo().getPollProfileSupport();
            log.debug("Negotiated " + pps.getMinPollPeriod().toMilliseconds() + "ms min poll period, timeout="
                    + minPollPeriodToTimeout(pps.getMinPollPeriod().toMilliseconds()));
            log.debug("Negotiated " + pps.getMaxMtuTx() + " " + pps.getMaxMtuRx() + " " + pps.getMaxBwTx());
            keepAliveTask = new TaskQueue.TaskImpl<Object>() {
                public Object doExecute(TaskQueue queue) {
                    if (lastKeepAliveSentInvokeId >= 0) {
                        if (lastKeepAliveSentInvokeId >= 0) {
                            if (lastKeepAliveSentInvokeId != lastKeepAliveRecvInvokeId) {
                                log.error("lastKeepAliveSentInvokeId=" + lastKeepAliveSentInvokeId
                                        + " != lastKeepAliveRecvInvokeId=" + lastKeepAliveSentInvokeId);
                                return null;
                            }
                        }
                    }
                    try {
                        lastKeepAliveSentInvokeId = requestSinglePoll(ObjectClass.NOM_MOC_VMO_AL_MON,
                                AttributeId.NOM_ATTR_GRP_VMO_STATIC);
                    } catch (IOException e) {
                        log.error("requesting a keep alive (static attributes of the alarm monitor object)", e);
                    }
                    return null;
                }
            };

            keepAliveTask.setInterval(minPollPeriodToTimeout(pps.getMinPollPeriod().toMilliseconds()) - 100L);
            keepAliveTask.setScheduledTime(System.currentTimeMillis() + keepAliveTask.getInterval() - 100L);
            networkLoop.add(keepAliveTask);
            super.handle(sockaddr, message);
        }

        @Override
        protected void handle(SinglePollDataResult result) {
            switch (ObjectClass.valueOf(result.getPolledObjType().getOidType().getType())) {
            case NOM_MOC_VMO_AL_MON:
                switch (AttributeId.valueOf(result.getPolledAttributeGroup().getType())) {
                case NOM_ATTR_GRP_VMO_STATIC:
                    lastKeepAliveRecvInvokeId = result.getAction().getMessage().getInvoke();
                    break;
                }
            }

            for (SingleContextPoll sop : result.getPollInfoList()) {
                for (ObservationPoll op : sop.getPollInfo()) {
                    if (op.getAttributes().get(prodSpec)) {
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
                    if (op.getAttributes().get(firstName)) {
                    }
                    if (op.getAttributes().get(lastName)) {
                    }
                    if (op.getAttributes().get(patientId)) {
                    }
                }
            }
            super.handle(result);
        }

        @Override
        protected void handle(ExtendedPollDataResult result) {
            log.debug("ExtendedPollDataResult");
            // log.debug(lineWrap(result.toString()));
            for (SingleContextPoll sop : result.getPollInfoList()) {
                for (ObservationPoll op : sop.getPollInfo()) {
                    int handle = op.getHandle().getHandle();

                    if (op.getAttributes().get(observed)) {
                        log.debug(observed.toString());
                        handle(handle, observed.getValue());
                    }

                    if (op.getAttributes().get(compoundObserved)) {
                        for (NumericObservedValue nov : compoundObserved.getValue().getList()) {
                            handle(handle, nov);
                        }
                    }

                    if (op.getAttributes().get(type)) {
                        log.debug(type.toString());
                    }
                    if (op.getAttributes().get(metricSpecification)) {
                        log.debug(metricSpecification.toString());
                    }

                    if (op.getAttributes().get(idLabel)) {
                        log.debug(idLabel.toString());
                    }

                    if (op.getAttributes().get(idLabelString)) {
                        log.debug(idLabelString.toString());
                    }

                    if (op.getAttributes().get(displayResolution)) {
                        log.debug(displayResolution.toString());
                    }

                    if (op.getAttributes().get(color)) {
                        log.debug(color.toString());
                    }

                    if (op.getAttributes().get(period)) {
                        log.debug(period.toString());
                        handle(handle, period.getValue());
                    }

                    if (op.getAttributes().get(DemoMP70.this.handle)) {
                        log.debug(DemoMP70.this.handle.toString());
                        handle(handle, DemoMP70.this.handle.getValue());
                    }

                    if (op.getAttributes().get(spec)) {
                        log.debug(spec.toString());
                        handle(handle, spec.getValue());

                    }
                    if (op.getAttributes().get(cov)) {
                        for (SampleArrayObservedValue v : cov.getValue().getList()) {
                            handle(handle, v);
                        }
                    }
                    if (op.getAttributes().get(v)) {
                        log.debug(v.toString());
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
                InstanceHolder<ice.Numeric> mnu = numericUpdates.get(ov);
                if (null != mnu) {
                    numericSample(mnu, observed.getValue().floatValue());
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
                MyWaveform w = waveformUpdates.get(ov);
                if (null == w) {
                    log.warn("No waveform for " + ov);
                } else {
                    getByHandle(handle).add(w);
                    int cnt = w.holder.data.values.size();
                    for (int i = 0; i < cnt; i++) {
                        w.applyValue(i, bytes);
                    }
                    sampleArrayDataWriter.write(w.holder.data, w.holder.handle);
                }
            }
        }

        protected void handle(int handle, SampleArraySpecification spec) {
            int cnt = spec.getArraySize();
            short sampleSize = spec.getSampleSize();
            short significantBits = spec.getSignificantBits();

            for (MyWaveform w : getByHandle(handle)) {

                w.setSampleSize(sampleSize);
                w.setSignificantBits(significantBits);
                w.holder.data.values.setSize(cnt);

            }
        }

        protected void handle(int handle, RelativeTime period) {
            for (MyWaveform w : getByHandle(handle)) {
                w.holder.data.millisecondsPerSample = (int) period.toMilliseconds();
            }
        }
    }

    private void addSampleArray(ObservedValue ov, String tag, Label l) {
        waveformUpdates.put(ov, new MyWaveform(createSampleArrayInstance(tag)));
        waveformLabels.put(ov, l);
    }

    private void addNumeric(ObservedValue ov, String tag, Label l) {
        numericUpdates.put(ov, createNumericInstance(tag));
        numericLabels.put(ov, l);
    }

    private void configureData() {
        addNumeric(ObservedValue.NOM_PULS_OXIM_SAT_O2, rosetta.MDC_PULS_OXIM_SAT_O2.VALUE,
                Label.NLS_NOM_PULS_OXIM_SAT_O2);
        addNumeric(ObservedValue.NOM_PLETH_PULS_RATE, rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE,
                Label.NLS_NOM_PULS_OXIM_PULS_RATE);
        addNumeric(ObservedValue.NOM_PRESS_BLD_NONINV_DIA, rosetta.MDC_PRESS_CUFF_DIA.VALUE,
                Label.NLS_NOM_PRESS_BLD_NONINV);
        addNumeric(ObservedValue.NOM_PRESS_BLD_NONINV_SYS, rosetta.MDC_PRESS_CUFF_SYS.VALUE,
                Label.NLS_NOM_PRESS_BLD_NONINV);
        addNumeric(ObservedValue.NOM_PRESS_BLD_NONINV_PULS_RATE, rosetta.MDC_PULS_RATE_NON_INV.VALUE,
                Label.NLS_NOM_PRESS_BLD_NONINV_PULS_RATE);

        addSampleArray(ObservedValue.NOM_PLETH, rosetta.MDC_PULS_OXIM_PLETH.VALUE, Label.NLS_NOM_PULS_OXIM_PLETH);
        addSampleArray(ObservedValue.NOM_ECG_ELEC_POTL_I, rosetta.MDC_ECG_AMPL_ST_I.VALUE,
                Label.NLS_NOM_ECG_ELEC_POTL_I);
        addSampleArray(ObservedValue.NOM_ECG_ELEC_POTL_II, rosetta.MDC_ECG_AMPL_ST_II.VALUE,
                Label.NLS_NOM_ECG_ELEC_POTL_II);
        addSampleArray(ObservedValue.NOM_ECG_ELEC_POTL_III, rosetta.MDC_ECG_AMPL_ST_III.VALUE,
                Label.NLS_NOM_ECG_ELEC_POTL_III);
        addSampleArray(ObservedValue.NOM_ECG_ELEC_POTL_AVF, rosetta.MDC_ECG_AMPL_ST_AVF.VALUE,
                Label.NLS_NOM_ECG_ELEC_POTL_AVF);
        addSampleArray(ObservedValue.NOM_ECG_ELEC_POTL_AVL, rosetta.MDC_ECG_AMPL_ST_AVL.VALUE,
                Label.NLS_NOM_ECG_ELEC_POTL_AVL);
        addSampleArray(ObservedValue.NOM_ECG_ELEC_POTL_AVR, rosetta.MDC_ECG_AMPL_ST_AVR.VALUE,
                Label.NLS_NOM_ECG_ELEC_POTL_AVR);
        addSampleArray(ObservedValue.NOM_ECG_ELEC_POTL_V2, rosetta.MDC_ECG_AMPL_ST_V2.VALUE,
                Label.NLS_NOM_ECG_ELEC_POTL_V2);
        addSampleArray(ObservedValue.NOM_ECG_ELEC_POTL_V5, rosetta.MDC_ECG_AMPL_ST_V5.VALUE,
                Label.NLS_NOM_ECG_ELEC_POTL_V5);

    }

    private final MyIntellivue myIntellivue;

    private static final Logger log = LoggerFactory.getLogger(DemoMP70.class);

    private final NetworkLoop networkLoop;
    private final Thread networkLoopThread;

    public DemoMP70(int domainId, EventLoop eventLoop) throws IOException {
        this(domainId, eventLoop, null);
    }

    public DemoMP70(int domainId, EventLoop eventLoop, NetworkLoop loop) throws IOException {
        super(domainId, eventLoop);

        deviceIdentity.manufacturer = "Philips";
        deviceIdentity.model = "MP70";
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
        configureData();
    }

    protected static class MyWaveform {
        private short sampleSize, significantBits;
        private final InstanceHolder<ice.SampleArray> holder;

        public MyWaveform(InstanceHolder<ice.SampleArray> holder) {
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
                state(ice.ConnectionState.Connecting, "trying " + address + ":" + port);
            } catch (UnknownHostException e) {
                log.error("Trying to connect to address", e);
            } catch (IOException e) {
                log.error("Trying to connect to address", e);
            }
        }

    }

    protected final void state(ice.ConnectionState state, String connectionInfo) {
        if (!stateMachine.transitionWhenLegal(state, 5000L)) {
            throw new RuntimeException("timed out changing state");
        }
        setConnectionInfo(connectionInfo);
    }

    protected final Map<Integer, Set<MyWaveform>> waveHandle = new HashMap<Integer, Set<MyWaveform>>();

    protected Collection<MyWaveform> getByHandle(int h) {
        Set<MyWaveform> set = waveHandle.get(h);
        if (null == set) {
            set = new HashSet<MyWaveform>();
            waveHandle.put(h, set);
        }
        return set;
    }

    protected final static long CONTINUOUS_POLL_INTERVAL = 60000L;

    protected final Attribute<CompoundNumericObservedValue> compoundObserved = AttributeFactory.getAttribute(
            AttributeId.NOM_ATTR_NU_CMPD_VAL_OBS, CompoundNumericObservedValue.class);
    protected final Attribute<NumericObservedValue> observed = AttributeFactory.getAttribute(
            AttributeId.NOM_ATTR_NU_VAL_OBS, NumericObservedValue.class);
    protected final Attribute<SampleArrayObservedValue> v = AttributeFactory.getAttribute(
            AttributeId.NOM_ATTR_SA_VAL_OBS, SampleArrayObservedValue.class);
    protected final Attribute<SampleArrayCompoundObservedValue> cov = AttributeFactory.getAttribute(
            AttributeId.NOM_ATTR_SA_CMPD_VAL_OBS, SampleArrayCompoundObservedValue.class);
    protected final Attribute<ProductionSpecification> prodSpec = AttributeFactory.getAttribute(
            AttributeId.NOM_ATTR_ID_PROD_SPECN, ProductionSpecification.class);

    protected final Attribute<org.mdpnp.devices.philips.intellivue.data.String> firstName = AttributeFactory
            .getAttribute(AttributeId.NOM_ATTR_PT_NAME_GIVEN, org.mdpnp.devices.philips.intellivue.data.String.class);
    protected final Attribute<org.mdpnp.devices.philips.intellivue.data.String> lastName = AttributeFactory
            .getAttribute(AttributeId.NOM_ATTR_PT_NAME_FAMILY, org.mdpnp.devices.philips.intellivue.data.String.class);
    protected final Attribute<org.mdpnp.devices.philips.intellivue.data.String> patientId = AttributeFactory
            .getAttribute(AttributeId.NOM_ATTR_PT_ID, org.mdpnp.devices.philips.intellivue.data.String.class);

    protected final Map<ObservedValue, Label> waveformLabels = new HashMap<ObservedValue, Label>();
    protected final Map<ObservedValue, Label> numericLabels = new HashMap<ObservedValue, Label>();
    protected final Map<ObservedValue, InstanceHolder<ice.Numeric>> numericUpdates = new HashMap<ObservedValue, InstanceHolder<ice.Numeric>>();
    protected final Map<ObservedValue, MyWaveform> waveformUpdates = new HashMap<ObservedValue, MyWaveform>();

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

    private InetAddress lastRemote;
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

        myIntellivue.requestAssociation();
    }

    @Override
    public void disconnect() {
        state(ice.ConnectionState.Disconnecting, "disassociating");

        long start = System.currentTimeMillis();

        networkLoop.clearTasks();
        while (!ice.ConnectionState.Disconnected.equals(stateMachine.getState())
                && (System.currentTimeMillis() - start) <= 5000L) {
            try {
                myIntellivue.send(new AssociationFinishImpl());
            } catch (IOException e1) {
                log.error("sending association finish", e1);
            }
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                log.error("Interrupted", e);
            }
        }
    }

    @Override
    public void shutdown() {
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
