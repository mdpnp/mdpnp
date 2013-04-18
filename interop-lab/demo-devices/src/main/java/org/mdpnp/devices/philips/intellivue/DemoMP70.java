/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.philips.intellivue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mdpnp.comms.Gateway;
import org.mdpnp.comms.IdentifiableUpdate;
import org.mdpnp.comms.Identifier;
import org.mdpnp.comms.connected.AbstractConnectedDevice;
import org.mdpnp.comms.data.identifierarray.IdentifierArrayUpdate;
import org.mdpnp.comms.data.identifierarray.MutableIdentifierArrayUpdate;
import org.mdpnp.comms.data.identifierarray.MutableIdentifierArrayUpdateImpl;
import org.mdpnp.comms.data.numeric.MutableNumericUpdate;
import org.mdpnp.comms.data.numeric.MutableNumericUpdateImpl;
import org.mdpnp.comms.data.numeric.Numeric;
import org.mdpnp.comms.data.text.MutableTextUpdate;
import org.mdpnp.comms.data.text.MutableTextUpdateImpl;
import org.mdpnp.comms.data.text.TextUpdate;
import org.mdpnp.comms.data.waveform.MutableWaveformUpdate;
import org.mdpnp.comms.data.waveform.MutableWaveformUpdateImpl;
import org.mdpnp.comms.data.waveform.Waveform;
import org.mdpnp.comms.nomenclature.BloodPressure;
import org.mdpnp.comms.nomenclature.ConnectedDevice;
import org.mdpnp.comms.nomenclature.Demographics;
import org.mdpnp.comms.nomenclature.Device;
import org.mdpnp.comms.nomenclature.ElectroCardioGram;
import org.mdpnp.comms.nomenclature.PulseOximeter;
import org.mdpnp.devices.philips.intellivue.action.ExtendedPollDataResult;
import org.mdpnp.devices.philips.intellivue.action.ObservationPoll;
import org.mdpnp.devices.philips.intellivue.action.SingleContextPoll;
import org.mdpnp.devices.philips.intellivue.action.SinglePollDataResult;
import org.mdpnp.devices.philips.intellivue.association.AssociationAbort;
import org.mdpnp.devices.philips.intellivue.association.AssociationAccept;
import org.mdpnp.devices.philips.intellivue.association.AssociationDisconnect;
import org.mdpnp.devices.philips.intellivue.association.AssociationRefuse;
import org.mdpnp.devices.philips.intellivue.attribute.Attribute;
import org.mdpnp.devices.philips.intellivue.attribute.AttributeFactory;
import org.mdpnp.devices.philips.intellivue.data.AttributeId;
import org.mdpnp.devices.philips.intellivue.data.ComponentId;
import org.mdpnp.devices.philips.intellivue.data.CompoundNumericObservedValue;
import org.mdpnp.devices.philips.intellivue.data.DisplayResolution;
import org.mdpnp.devices.philips.intellivue.data.EnumValue;
import org.mdpnp.devices.philips.intellivue.data.Handle;
import org.mdpnp.devices.philips.intellivue.data.Label;
import org.mdpnp.devices.philips.intellivue.data.MetricSpecification;
import org.mdpnp.devices.philips.intellivue.data.NumericObservedValue;
import org.mdpnp.devices.philips.intellivue.data.ObjectClass;
import org.mdpnp.devices.philips.intellivue.data.ObservedValue;
import org.mdpnp.devices.philips.intellivue.data.ProductionSpecification;
import org.mdpnp.devices.philips.intellivue.data.ProductionSpecificationType;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoMP70 extends AbstractConnectedDevice {
	private class MyIntellivue extends Intellivue {
		public MyIntellivue() throws IOException {
			super();
		}
		public MyIntellivue(NetworkLoop loop) {
			super(loop);
		}
		@Override
		protected void transientDisassociation(InetAddress lastRemote,
				int prefixLength) {
			super.transientDisassociation(lastRemote, prefixLength);
			state(State.Connecting, "Re-establishing");
		}
		@Override
		protected void handle(SetResult result, boolean confirmed) {
			super.handle(result, confirmed);
			Attribute<TextIdList> ati = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_POLL_NU_PRIO_LIST, TextIdList.class);
			if(result.getAttributes().get(ati) && ati.getValue().containsAll(numericLabels.values().toArray(new Label[0]))) {
				
				TaskQueue.Task<Object> nuTask = new TaskQueue.TaskImpl<Object>() {
					@Override
					public Object doExecute(TaskQueue queue) {
						requestExtendedPoll(ObjectClass.NOM_MOC_VMO_METRIC_NU, CONTINUOUS_POLL_INTERVAL);
						return null;
					};
				};
				nuTask.setInterval(CONTINUOUS_POLL_INTERVAL);
				networkLoop.add(nuTask);
			} else {
				log.warn("Numerics priority list does not contain all of our requested labels:"+ati);
			}
			
			ati = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_POLL_RTSA_PRIO_LIST, TextIdList.class);
			if(result.getAttributes().get(ati) && ati.getValue().containsAll(waveformLabels.values().toArray(new Label[0]))) {
				TaskQueue.Task<Object> saTask = new TaskQueue.TaskImpl<Object>() {
					@Override
					public Object doExecute(TaskQueue queue) {
						requestExtendedPoll(ObjectClass.NOM_MOC_VMO_METRIC_SA_RT, CONTINUOUS_POLL_INTERVAL);
						return null;
					}
					
				};
				saTask.setInterval(CONTINUOUS_POLL_INTERVAL);
				networkLoop.add(saTask);
			} else {
				log.warn("SampleArray priority list does not contain all requested labels:"+ati);
			}
		}
		@SuppressWarnings("unused")
        private String manufacturer, modelNumber;
		@Override
		protected void handle(EventReport eventReport, boolean confirm) {
			super.handle(eventReport, confirm);
			switch(ObjectClass.valueOf(eventReport.getEventType().getType())) {
			case NOM_NOTI_MDS_CREAT:
				MdsCreateEvent createEvent = (MdsCreateEvent) eventReport.getEvent();
				Attribute<SystemModel> asm = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_ID_MODEL, SystemModel.class);
				Attribute<org.mdpnp.devices.philips.intellivue.data.String> as = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_ID_BED_LABEL, org.mdpnp.devices.philips.intellivue.data.String.class);
				Attribute<ProductionSpecification> ps = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_ID_PROD_SPECN, ProductionSpecification.class);
				
				
				
				if(createEvent.getAttributes().get(ps)) {
					log.info("ProductionSpecification");
					log.info(""+ps.getValue());
					VariableLabel vl = ps.getValue().getByComponentId(ProductionSpecificationType.SERIAL_NUMBER, ComponentId.ID_COMP_PRODUCT);
					if(null != vl) {
						guidUpdate.setValue(vl.getString());
						gateway.update(DemoMP70.this, guidUpdate);
					}
				}
				
				
//				String name = "";
				if(createEvent.getAttributes().get(asm)) {
					manufacturer = asm.getValue().getManufacturer().getString();
//					name = name + asm.getValue().getManufacturer().getString() + " " + asm.getValue().getModelNumber() + " ";
				}
				if(createEvent.getAttributes().get(as)) {
					modelNumber = asm.getValue().getModelNumber().getString();
//					name = name + as.getValue().getString() + " ";
				}

				// TODO attention: ignoring name as specified by device
//				name = "Philips MP70";

				
//				if(!"".equals(name)) {
//					nameUpdate.setValue(name);
//					gateway.update(PhilipsIntellivueMP70Impl.this, nameUpdate);
//				}
				
				state(State.Connected, null);
				requestSinglePoll(ObjectClass.NOM_MOC_VMS_MDS, AttributeId.NOM_ATTR_GRP_SYS_PROD);
				TaskQueue.Task<Object> nuTask = new TaskQueue.TaskImpl<Object>() {
					@Override
					public Object doExecute(TaskQueue queue) {
						requestSinglePoll(ObjectClass.NOM_MOC_PT_DEMOG, AttributeId.NOM_ATTR_GRP_PT_DEMOG);
//						requestExtendedPoll(ObjectClass.NOM_MOC_PT_DEMOG, CONTINUOUS_POLL_INTERVAL, AttributeId.NOM_ATTR_GRP_PT_DEMOG);
						return null;
					};
				};
				nuTask.setInterval(CONTINUOUS_POLL_INTERVAL);
				networkLoop.add(nuTask);
//				requestExtendedPoll(ObjectClass.NOM_MOC_PT_DEMOG, 60000L);
//				requestSinglePoll(ObjectClass.NOM_MOC_PT_DEMOG, AttributeId.NOM_ATTR_GRP_PT_DEMOG);
				requestSet(numericLabels.values().toArray(new Label[0]), waveformLabels.values().toArray(new Label[0]));
				



				break;
			default:
				break;
			}
			
		}
		@Override
		protected synchronized void handle(SocketAddress sockaddr, AssociationRefuse message) {
			state(State.Disconnected, "refused");
			super.handle(sockaddr, message);
		}
		
		@Override
		protected synchronized void handle(SocketAddress sockaddr, AssociationAbort message) {
			state(State.Disconnected, "aborted");
			super.handle(sockaddr, message);
		}
		
		@Override
		protected synchronized void handle(SocketAddress sockaddr, AssociationDisconnect message) {
			state(State.Disconnected, "disconnected");
			super.handle(sockaddr, message);
		}
		
		@Override
		protected void handle(SocketAddress sockaddr, AssociationAccept message) {
			state(State.Negotiating, "accepted");
			super.handle(sockaddr, message);
		}
		@Override
		protected void handle(SinglePollDataResult result) {
			
			for(SingleContextPoll sop : result.getPollInfoList()) {
				for(ObservationPoll op : sop.getPollInfo()) {
					if(op.getAttributes().get(prodSpec)) {
						log.info("ProductionSpecification");
						log.info(""+prodSpec.getValue());
						VariableLabel vlabel = prodSpec.getValue().getByComponentId(ProductionSpecificationType.SERIAL_NUMBER, ComponentId.ID_COMP_PRODUCT);
						if(vlabel != null) {
							guidUpdate.setValue(vlabel.getString());
							gateway.update(DemoMP70.this, guidUpdate);
						}
						vlabel = prodSpec.getValue().getByComponentId(ProductionSpecificationType.PART_NUMBER, ComponentId.ID_COMP_PRODUCT);
						if(vlabel != null) {
							nameUpdate.setValue(manufacturer + " " + vlabel.getString());
							gateway.update(DemoMP70.this, nameUpdate);
						}
					}
					if(op.getAttributes().get(firstName)) {
						if(firstNameUpdate.setValue(firstName.getValue().getString())) {
							gateway.update(DemoMP70.this, firstNameUpdate);
						}
					}
					if(op.getAttributes().get(lastName)) {
						if(lastNameUpdate.setValue(lastName.getValue().getString())) {
							gateway.update(DemoMP70.this, lastNameUpdate);
						}
					}
					if(op.getAttributes().get(patientId)) {
						if(patientIdUpdate.setValue(patientId.getValue().getString())) {
							gateway.update(DemoMP70.this, patientIdUpdate);
						}
					}
				}
			}
			super.handle(result);
		}
		
		@Override
		protected void handle(ExtendedPollDataResult result) {
			log.debug("ExtendedPollDataResult");
			log.debug(lineWrap(result.toString()));
			for(SingleContextPoll sop : result.getPollInfoList()) {
				for(ObservationPoll op : sop.getPollInfo()) {
					int handle = op.getHandle().getHandle();
							
					if(op.getAttributes().get(observed)) {
						log.debug(observed.toString());
						handle(handle, observed.getValue());
					}

					if(op.getAttributes().get(compoundObserved)) {
						for(NumericObservedValue nov : compoundObserved.getValue().getList()) {
							handle(handle, nov);
						}
					}
					
					if(op.getAttributes().get(type)) {
						log.debug(type.toString());
					}
					if(op.getAttributes().get(metricSpecification)) {
						log.debug(metricSpecification.toString());
					}
					
					if(op.getAttributes().get(idLabel)) {
						log.debug(idLabel.toString());
					}
					
					if(op.getAttributes().get(idLabelString)) {
						log.debug(idLabelString.toString());
					}
					
					if(op.getAttributes().get(displayResolution)) {
						log.debug(displayResolution.toString());
					}
					
					if(op.getAttributes().get(color)) {
						log.debug(color.toString());
					}
				
					
					if(op.getAttributes().get(period)) {
						log.debug(period.toString());
						handle(handle, period.getValue());
					}
					
					if(op.getAttributes().get(DemoMP70.this.handle)) {
						log.debug(DemoMP70.this.handle.toString());
						handle(handle, DemoMP70.this.handle.getValue());
					}

					
					if(op.getAttributes().get(spec)) {
						log.debug(spec.toString());
						handle(handle, spec.getValue());
						
					}
					if(op.getAttributes().get(cov)) {
						for(SampleArrayObservedValue v : cov.getValue().getList()) {
							handle(handle, v);
						}
					}
					if(op.getAttributes().get(v)) {
						log.debug(v.toString());
						handle(handle, v.getValue());
					}
				}
			}
//			lastPoint.setTime(System.currentTimeMillis());

			super.handle(result);
		}
		
		private void handle(int handle, Handle value) {
//			log.debug(value.toString());
		}
		private final void handle(int handle, NumericObservedValue observed) {
//			log.debug(observed.toString());
			ObservedValue ov = ObservedValue.valueOf(observed.getPhysioId().getType());
			if(null != ov) {
				MutableNumericUpdate mnu = numericUpdates.get(ov);
				if(null != mnu) {
					mnu.setValue(observed.getValue());
					gateway.update(DemoMP70.this, mnu);
				}
			}
			
		}
		protected void handle(int handle, SampleArrayObservedValue v) {
			short[] bytes = v.getValue();
			ObservedValue ov = ObservedValue.valueOf(v.getPhysioId().getType());
			if(null == ov) {
				log.warn("No ObservedValue for " + v.getPhysioId().getType());
			} else {
				MyWaveform w = waveformUpdates.get(ov);
				if(null == w) {
					log.warn("No waveform for " + ov);
				} else {
					getByHandle(handle).add(w);
//					Integer cnt = w.getCount();
					Number[] values = w.getValues();
					if(values != null) {
						Integer cnt = values.length;
						if(null == cnt) {
							log.warn("null count for " + ov + " " + w);
						} else {
							for(int i = 0; i < cnt; i++) {	
								w.applyValue(i, bytes);
							}
							log.debug(Arrays.toString(bytes));
							log.debug(Arrays.toString(values));
							gateway.update(DemoMP70.this, w);
						}
					}
				}
			}
		}
		protected void handle(int handle, SampleArraySpecification spec) {
			int cnt = spec.getArraySize();
			short sampleSize = spec.getSampleSize();
			short significantBits = spec.getSignificantBits();
			
			for(MyWaveform w : getByHandle(handle)) {
			
				w.setSampleSize(sampleSize);
				w.setSignificantBits(significantBits);
				w.setValues(new Number[cnt]);
//				w.setCount(cnt);
//				if(w.getValues().length < cnt) {
//					int[] newpleth = new int[cnt];
//					System.arraycopy(w.getValues(), 0, newpleth, 0, w.getValues().length);
//					w.setValues(newpleth);
//				}
			}
		}
		protected void handle(int handle, RelativeTime period) {
			for(MyWaveform w : getByHandle(handle)) {
				w.setMillisecondsPerSample((double)period.toMilliseconds());
			}
		}
	}
	
	
//	private final Map<Identifier, IdentifiableUpdate> updates = new HashMap<Identifier, IdentifiableUpdate>();
	
	private void add(ObservedValue ov, Waveform w, Label l) {
		MyWaveform myw = new MyWaveformImpl(w);
//		updates.put(w, myw);
		add(myw);
		waveformUpdates.put(ov, myw);
		waveformLabels.put(ov, l);
	}
	
	private void add(ObservedValue ov, Numeric n, Label l) {
		MutableNumericUpdate mnu = new MutableNumericUpdateImpl(n);
//		updates.put(n, mnu);
		add(mnu);
		numericUpdates.put(ov, mnu);
		numericLabels.put(ov, l);
	}
	
//	private void add(IdentifiableUpdate<?> iu) {
//		updates.put(iu.getIdentifier(), iu);
//	}
	
	private void configureData() {
		add(ObservedValue.NOM_PULS_OXIM_SAT_O2, PulseOximeter.SPO2, Label.NLS_NOM_PULS_OXIM_SAT_O2);
		add(ObservedValue.NOM_PLETH_PULS_RATE, PulseOximeter.PULSE, Label.NLS_NOM_PULS_OXIM_PULS_RATE);
		add(ObservedValue.NOM_PRESS_BLD_NONINV_DIA, BloodPressure.DIASTOLIC, Label.NLS_NOM_PRESS_BLD_NONINV);
		add(ObservedValue.NOM_PRESS_BLD_NONINV_SYS, BloodPressure.SYSTOLIC, Label.NLS_NOM_PRESS_BLD_NONINV);
		add(ObservedValue.NOM_PRESS_BLD_NONINV_PULS_RATE, BloodPressure.PULSE, Label.NLS_NOM_PRESS_BLD_NONINV_PULS_RATE);
		

		add(ObservedValue.NOM_PLETH, PulseOximeter.PLETH, Label.NLS_NOM_PULS_OXIM_PLETH);
		add(ObservedValue.NOM_ECG_ELEC_POTL_I, ElectroCardioGram.I, Label.NLS_NOM_ECG_ELEC_POTL_I);
		add(ObservedValue.NOM_ECG_ELEC_POTL_II, ElectroCardioGram.II, Label.NLS_NOM_ECG_ELEC_POTL_II);
		add(ObservedValue.NOM_ECG_ELEC_POTL_III, ElectroCardioGram.III, Label.NLS_NOM_ECG_ELEC_POTL_III);
		add(ObservedValue.NOM_ECG_ELEC_POTL_AVF, ElectroCardioGram.A_VF, Label.NLS_NOM_ECG_ELEC_POTL_AVF);
		add(ObservedValue.NOM_ECG_ELEC_POTL_AVL, ElectroCardioGram.A_VL, Label.NLS_NOM_ECG_ELEC_POTL_AVL);
		add(ObservedValue.NOM_ECG_ELEC_POTL_AVR, ElectroCardioGram.A_VR, Label.NLS_NOM_ECG_ELEC_POTL_AVR);
		add(ObservedValue.NOM_ECG_ELEC_POTL_V2, ElectroCardioGram.V2, Label.NLS_NOM_ECG_ELEC_POTL_V2);
		add(ObservedValue.NOM_ECG_ELEC_POTL_V5, ElectroCardioGram.V5, Label.NLS_NOM_ECG_ELEC_POTL_V5);
		
		stateUpdate.setValue(ConnectedDevice.State.Disconnected);
		connectionTypeUpdate.setValue(ConnectionType.Network);
		
		add(stateUpdate);
		add(connectionInfoUpdate);
		add(connectionTypeUpdate);
		add(guidUpdate);
		add(nameUpdate);
		add(firstNameUpdate);
		add(lastNameUpdate);
		add(patientIdUpdate);
		
		gateway.addListener(this);
		
	}
	
//	private final Gateway gateway;
	private final MyIntellivue myIntellivue;
	
	private static final Logger log = LoggerFactory.getLogger(DemoMP70.class);
	
	public DemoMP70(Gateway gateway) throws IOException {
		super(gateway);
		myIntellivue = new MyIntellivue();
		configureData();
	}
	
	public DemoMP70(Gateway gateway, NetworkLoop loop) {
		super(gateway);
		myIntellivue = new MyIntellivue(loop);
		configureData();
	}
	
//	private final MutableEnumerationUpdate stateUpdate = new MutableEnumerationUpdateImpl(ConnectedDevice.STATE);
//	private final MutableTextUpdate connectionInfoUpdate = new MutableTextUpdateImpl(ConnectedDevice.CONNECTION_INFO);
//	private final MutableEnumerationUpdate connectionTypeUpdate = new MutableEnumerationUpdateImpl(ConnectedDevice.CONNECTION_TYPE);

	protected final MutableTextUpdate firstNameUpdate = new MutableTextUpdateImpl(Demographics.FIRST_NAME);
	protected final MutableTextUpdate lastNameUpdate = new MutableTextUpdateImpl(Demographics.LAST_NAME);
	protected final MutableTextUpdate patientIdUpdate = new MutableTextUpdateImpl(Demographics.PATIENT_ID);
	
	protected interface MyWaveform extends MutableWaveformUpdate {
		void applyValue(int sampleNumber, short[] values);
		short getSampleSize();
		void setSampleSize(short s);
		short getSignificantBits();
		void setSignificantBits(short s);
	}
	
	@SuppressWarnings("serial")
    protected static class MyWaveformImpl extends MutableWaveformUpdateImpl implements MyWaveform {
//		private int[] values = new int[0];
//		private int count = 0, maxCount = 0;
//		private double msPerSample = 1.0;
		private short sampleSize, significantBits;

		public MyWaveformImpl(Waveform waveform) {
			super(waveform);
		}
		
				
		private int[] mask = new int[0];
		private int[] shift = new int[0];
		@Override
		public void applyValue(int sampleNumber, short[] values) {
			int value = 0;
			for(int i = 0; i < sampleSize; i++) {
				value |= (mask[i] & values[sampleNumber*sampleSize + i]) << shift[i];
			}
			getValues()[sampleNumber] = value;
//			setValue(sampleNumber, value);
//			applyValue(value);
//			applyValue((0x0F00 & (values[2*sampleNumber] << 8)) | (0xFF & values[2*sampleNumber+1]));
		}
		public static final int createMask(int prefix) {
			int mask = 0;
			
			for(int i = 0; i < prefix; i++) {
				mask |= (1 << i);
			}
			return mask;
		}
		private void buildMaskAndShift() {
			if(this.shift.length < sampleSize) {
				this.shift = new int[sampleSize];
			}
			if(this.mask.length < sampleSize) {
				this.mask = new int[sampleSize];
			}
			int significantBits = this.significantBits;
			for(int i = sampleSize - 1; i >= 0; i--) {
				shift[i] = (sampleSize-i-1) * Byte.SIZE;
				mask[i] = significantBits >= Byte.SIZE ? 0xFF : createMask(significantBits);
				significantBits-=Byte.SIZE;
			}
			log.debug("Mask:"+Arrays.toString(mask) + " Shift:"+Arrays.toString(shift) + " sampleSize="+sampleSize + " sigBits="+this.significantBits);
		}
		@Override
		public short getSampleSize() {
			return sampleSize;
		}
		@Override
		public short getSignificantBits() {
			return significantBits;
		}
		@Override
		public void setSampleSize(short s) {
			this.sampleSize = (short)(s / Byte.SIZE);
			buildMaskAndShift();
		}
		@Override
		public void setSignificantBits(short s) {
			this.significantBits = s;
			buildMaskAndShift();
		}
	}

	@Override
	public void connect(String address) {
		if(null == address || "".equals(address)) {
			try {
				String [] hosts = myIntellivue.listenForConnectIndication();
				
				if(null == hosts) {
					state(State.Disconnected, "no broadcast addresses");
				} else {
					state(State.Connecting, "listening  on " + Arrays.toString(hosts));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else {
			try {
				int port = Intellivue.DEFAULT_UNICAST_PORT;
				
				int colon = address.lastIndexOf(':');
				if(colon >= 0) {
					port = Integer.parseInt(address.substring(colon+1, address.length()));
					address = address.substring(0,colon);
				}
				
				InetAddress addr = InetAddress.getByName(address);
				
				myIntellivue.connect(addr, -1, port);
				state(State.Connecting, "trying " + address + ":"+port);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	protected final void state(State state, String connectionInfo) {
		if(!stateMachine.transitionWhenLegal(state, 5000L)) {
			throw new RuntimeException("timed out changing state");
		}
		setConnectionInfo(connectionInfo);
	}
	
//	private State state = State.Disconnected;
	
	protected final Map<Integer, Set<MyWaveform>> waveHandle = new HashMap<Integer, Set<MyWaveform>>();
		
	protected Collection<MyWaveform> getByHandle(int h) {
		Set<MyWaveform> set = waveHandle.get(h);
		if(null == set) {
			set = new HashSet<MyWaveform>();
			waveHandle.put(h, set);
		}
		return set;
	}
	
	protected final static long CONTINUOUS_POLL_INTERVAL = 60000L;


	

	

	
	protected final Attribute<CompoundNumericObservedValue> compoundObserved = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_NU_CMPD_VAL_OBS, CompoundNumericObservedValue.class);
	protected final Attribute<NumericObservedValue> observed = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_NU_VAL_OBS, NumericObservedValue.class);
	protected final Attribute<SampleArrayObservedValue> v = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_SA_VAL_OBS, SampleArrayObservedValue.class);
	protected final Attribute<SampleArrayCompoundObservedValue> cov = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_SA_CMPD_VAL_OBS, SampleArrayCompoundObservedValue.class);	
	protected final Attribute<ProductionSpecification> prodSpec = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_ID_PROD_SPECN, ProductionSpecification.class);
	
	protected final Attribute<org.mdpnp.devices.philips.intellivue.data.String> firstName = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_PT_NAME_GIVEN, org.mdpnp.devices.philips.intellivue.data.String.class);
	protected final Attribute<org.mdpnp.devices.philips.intellivue.data.String> lastName = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_PT_NAME_FAMILY, org.mdpnp.devices.philips.intellivue.data.String.class);
	protected final Attribute<org.mdpnp.devices.philips.intellivue.data.String> patientId = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_PT_ID, org.mdpnp.devices.philips.intellivue.data.String.class);
	

	protected final Map<ObservedValue, Label> waveformLabels = new HashMap<ObservedValue, Label>();
	protected final Map<ObservedValue, Label> numericLabels = new HashMap<ObservedValue, Label>();
	protected final Map<ObservedValue, MutableNumericUpdate> numericUpdates = new HashMap<ObservedValue, MutableNumericUpdate>();
	protected final Map<ObservedValue, MyWaveform> waveformUpdates = new HashMap<ObservedValue, MyWaveform>();
	
	
	protected final Attribute<Type> type = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_ID_TYPE, Type.class);
	protected final Attribute<MetricSpecification> metricSpecification = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_METRIC_SPECN, MetricSpecification.class);
	protected final Attribute<TextId> idLabel = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_ID_LABEL, TextId.class);
	protected final Attribute<org.mdpnp.devices.philips.intellivue.data.String> idLabelString = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_ID_LABEL_STRING, org.mdpnp.devices.philips.intellivue.data.String.class);
	protected final Attribute<DisplayResolution> displayResolution = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_DISP_RES, DisplayResolution.class);
	protected final Attribute<EnumValue<SimpleColor>> color = AttributeFactory.getEnumAttribute(AttributeId.NOM_ATTR_COLOR.asOid(), SimpleColor.class);
	
	
	protected final Attribute<Handle> handle = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_ID_HANDLE, Handle.class);
	protected final Attribute<RelativeTime> period = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_TIME_PD_SAMP, RelativeTime.class);
	protected final Attribute<SampleArraySpecification> spec = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_SA_SPECN, SampleArraySpecification.class);
	
	


	public void disconnect() {
		try {
			state(State.Disconnecting, "disassociating");
		} catch (RuntimeException re) {
			// TODO make this temporary
			re.printStackTrace();
		}
		try {
			myIntellivue.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	private String connectionInfo;
	
//	 @Override
//	public String getConnectionInfo() {
//		return connectionInfo;
//	}

//	protected IdentifiableUpdate<?> get(Identifier identifier) {
//		return updates.get(identifier);
//	}

	@Override
	public void update(IdentifiableUpdate<?> command) {
		if(Device.REQUEST_IDENTIFIED_UPDATES.equals(command.getIdentifier())) {
			IdentifierArrayUpdate iau = (IdentifierArrayUpdate) command;
			for(Identifier i : iau.getValue()) {
				IdentifiableUpdate<?> iu = get(i);
				if(null != iu) {
					gateway.update(DemoMP70.this, iu);
				}
			}
		} else if(ConnectedDevice.CONNECT_TO.equals(command.getIdentifier())) {
			connect( ((TextUpdate)command).getValue());
		} else if(ConnectedDevice.DISCONNECT.equals(command.getIdentifier())) {
			disconnect();
		} else if(Device.REQUEST_AVAILABLE_IDENTIFIERS.equals(command.getIdentifier())) {
			MutableIdentifierArrayUpdate upds = new MutableIdentifierArrayUpdateImpl(Device.GET_AVAILABLE_IDENTIFIERS);
			upds.setValue(this.updates.keySet().toArray(new Identifier[0]));
			gateway.update(DemoMP70.this, upds);
		}
	}

	@Override
	protected ConnectionType getConnectionType() {
		return ConnectionType.Network;
	}
	@Override
	protected String iconResourceName() {
		return "mp70.png";
	}
}
