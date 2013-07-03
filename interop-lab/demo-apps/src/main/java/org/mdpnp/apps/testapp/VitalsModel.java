package org.mdpnp.apps.testapp;

import static org.mdpnp.devices.TopicUtil.lookupOrCreateTopic;
import ice.Numeric;
import ice.NumericSeq;
import ice.SampleArraySeq;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.EventLoop.ConditionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.QueryCondition;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.TopicDescription;

public class VitalsModel extends AbstractListModel implements ListModel, ListDataListener {
	private final Set<QueryCondition> numericInterest = new HashSet<QueryCondition>();
	private final Set<QueryCondition> sampleArrayInterest = new HashSet<QueryCondition>();
	
	public void addNumericInterest(Integer i) {
	       // TODO this should probably be a ContentFilteredTopic to allow the writer to do the filtering
	    StringSeq params = new StringSeq();
	    params.add(Integer.toString(i));
        QueryCondition qc = numericDataReader.create_querycondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE,
                "name = %0",  params);
		numericInterest.add(qc);
        final NumericSeq num_seq = new NumericSeq();
        final SampleInfoSeq info_seq = new SampleInfoSeq();
		eventLoop.addHandler(qc, new ConditionHandler() {

            @Override
            public void conditionChanged(Condition condition) {
                try {
                    for(;;) {
                        numericDataReader.read_w_condition(num_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, (QueryCondition) condition);
                        for(int i = 0; i < info_seq.size(); i++) {
                            SampleInfo sampleInfo = (SampleInfo) info_seq.get(i);
                            if(sampleInfo.valid_data) {
                                Numeric n = (Numeric) num_seq.get(i);
                                log.trace("VitalsModel interested in: " + n);
                                updateNumeric(n);
//                                listener.update(n, deviceModel.getByUniversalDeviceIdentifier(n.universal_device_identifier));
                                
                            }
                        }
                        numericDataReader.return_loan(num_seq, info_seq);
                    }
                } catch (RETCODE_NO_DATA noData) {
                    
                } finally {
                    
                }
            }
		    
		});
		log.debug("New QueryCondition for numeric name="+i);
	}
	public void addSampleArrayInterest(Integer i) {
	    StringSeq params = new StringSeq();
        params.add('\'' + i + '\'');
        QueryCondition qc = sampleArrayDataReader.create_querycondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE,
                "name = %0",  params);
        sampleArrayInterest.add(qc);
        final SampleArraySeq sa_seq = new SampleArraySeq();
        final SampleInfoSeq info_seq = new SampleInfoSeq();
        eventLoop.addHandler(qc, new ConditionHandler() {

            @Override
            public void conditionChanged(Condition condition) {
                try {
                    for(;;) {
                        sampleArrayDataReader.read_w_condition(sa_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, (QueryCondition) condition);
                        for(int i = 0; i < info_seq.size(); i++) {
                            SampleInfo sampleInfo = (SampleInfo) info_seq.get(i);
                            if(sampleInfo.valid_data) {
                                ice.SampleArray sa = (ice.SampleArray) sa_seq.get(i);
                                // TODO report SampleArray changes to listener
//                                listener.update(sa, deviceModel.getByUniversalDeviceIdentifier(sa.universal_device_identifier));
                            }
                        }
                        sampleArrayDataReader.return_loan(sa_seq, info_seq);
                    }
                } catch (RETCODE_NO_DATA noData) {
                    
                } finally {
                    
                }
            }
            
        });
        log.debug("New QueryCondition for sampleArray name="+i);
	}
	private static final Logger log = LoggerFactory.getLogger(VitalsModel.class);
	public interface VitalsListener {
		void update(ice.Numeric n, Device device);
		void deviceRemoved(Device device);
		void deviceAdded(Device device);
		void deviceChanged(Device device);
	}
	
	private VitalsListener listener;
	
	public void setListener(VitalsListener listener) {
		this.listener = listener;
	}
	
	private final ice.NumericDataReader numericDataReader;
	private final ice.SampleArrayDataReader sampleArrayDataReader;
	private final DeviceListModel deviceModel;
	private final EventLoop eventLoop;
	
	public VitalsModel(Subscriber subscriber, DeviceListModel deviceModel, EventLoop eventLoop) {
	    this.deviceModel = deviceModel;
	    this.eventLoop = eventLoop;
	    
	    TopicDescription numericTopic = lookupOrCreateTopic(subscriber.get_participant(), ice.NumericTopic.VALUE, ice.NumericTypeSupport.class);
	    numericDataReader = (ice.NumericDataReader) subscriber.create_datareader(numericTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
	    
	    TopicDescription sampleArrayTopic = lookupOrCreateTopic(subscriber.get_participant(), ice.SampleArrayTopic.VALUE, ice.SampleArrayTypeSupport.class);
	    sampleArrayDataReader = (ice.SampleArrayDataReader) subscriber.create_datareader(sampleArrayTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);

	    deviceModel.addListDataListener(this);
	    
	    
	}
	
	public static final class Vitals {
		private final Device device;
		private final Integer identifier;
		
		private Number number;
		public Vitals(Device device, Integer identifier) {
			this.device = device;
			this.identifier = identifier;
		}
		public Device getDevice() {
			return device;
		}
		public void setNumber(Number number) {
			this.number = number;
		}
		public Number getNumber() {
			return number;
		}
		public Integer getIdentifier() {
			return identifier;
		}
	}
	
	private final List<Vitals> vitals = new ArrayList<Vitals>();
	
	@Override
	public int getSize() {
		return vitals.size();
	}

	@Override
	public Object getElementAt(int index) {
		return vitals.get(index);
	}

	public void fireDeviceChanged(Device device) {
		for(int i = 0; i < vitals.size(); i++) {
			if(vitals.get(i).getDevice().equals(device)) {
				fireContentsChanged(this, i, i);
				if(listener != null) {
				    listener.deviceChanged(device);
				}
			}
		}
	}
	
	public boolean removeDevice(Device device) {
		for(int i = 0; i < vitals.size(); i++) {
			if(vitals.get(i).getDevice().equals(device)) {
				log.debug("removed " + vitals.get(i).getIdentifier() + " " + device.getMakeAndModel());
				vitals.remove(i);
				fireIntervalRemoved(this, 0, 1);
				fireContentsChanged(this, 0, vitals.size()-1);
				if(listener != null) {
					listener.deviceRemoved(device);
				}
				return true;
			}
		}
		return false;
	}
    @Override
    public void intervalAdded(ListDataEvent e) {
//        for(int idx = e.getIndex0(); idx <= e.getIndex1(); idx++) {
//            Device d = ((DeviceListModel)e.getSource()).getElementAt(idx);
//            if(listener != null) {
//                listener.deviceAdded(d);
//            }
//        }
    }
    @Override
    public void intervalRemoved(ListDataEvent e) {
        if(e.getIndex0() != e.getIndex1()) {
            throw new IllegalArgumentException("Cannot handle multi-row deletes; needs refactoring to support it");
        }
        removeDevice(((DeviceListModel)e.getSource()).getLastRemoved());
    }
    @Override
    public void contentsChanged(ListDataEvent e) {
        for(int idx = e.getIndex0(); idx <= e.getIndex1(); idx++) {
            removeDevice(((DeviceListModel)e.getSource()).getElementAt(idx));
        }
    }
	
//	private static Number fromUpdate(IdentifiableUpdate<?> update) {
//		if(update instanceof NumericUpdate) {
//			return ((NumericUpdate)update).getValue();
//		} else if(update instanceof WaveformUpdate) {
//			Number[] values = ((WaveformUpdate)update).getValues();
//			if(null == values || values.length == 0) {
//				return null;
//			} else {
//				return values[values.length - 1];
//			}
//		} else {
//			return null;
//		}
//	}
	
    
    protected void updateNumeric(ice.Numeric n) {
      Vitals v = null;
      for(int i = 0; i < vitals.size(); i++) {
          v = vitals.get(i);
          // This vital already known from this device!
          if(v.getDevice().getDeviceIdentity().universal_device_identifier.equals(n.universal_device_identifier) && v.getIdentifier().equals(n.name)) {
              v.setNumber(n.value);

              
              if(listener != null) {
                  listener.update(n, v.getDevice());
              }
              fireContentsChanged(this, i, i);
              return;
          }
      }
      // New vital/device combination
      Device device = deviceModel.getByUniversalDeviceIdentifier(n.universal_device_identifier);
      if(null != device) {
          int i = vitals.size();
          v = new Vitals(device, n.name);
          v.setNumber(n.value);
          vitals.add(0, v);
          fireIntervalAdded(this, 0, 0);
          fireContentsChanged(this, 0, vitals.size()-1);
          if(listener != null) {
              listener.deviceAdded(device);
          }
      } else {
          log.warn("Numeric from unknown Device:" + n.universal_device_identifier);
      }
    }
    
//	@Override
//	public void update(IdentifiableUpdate<?> update) {
//		if(null==update) {
//			return;
//		}
//		String source = update.getSource();
//		if(null == source || "*".equals(source)) {
//			return;
//		}
//		MyDevice device = devices.get(source);
//		if(device == null) {
//			device = new MyDevice(source);
//			devices.put(source, device);
//			
//		}
//		
//		if(Association.DISSEMINATE.equals(update.getIdentifier())) {
//			Set<String> sources = new HashSet<String>();
//			for(String d : devices.keySet()) {
//				sources.add(d);
//			}
//			log.debug("Existing:"+sources.toString());
//			for(String s : ((TextArrayUpdate)update).getValue()) {
//				sources.remove(s);
//			}
//			log.debug("To Remove:"+sources.toString());
//			for(String s : sources) {
//				MyDevice d = devices.get(s);
//				while(removeDevice(d)) {
//					
//				}
//				devices.remove(s);
//			}
//		} else if(interest.contains(update.getIdentifier())) {
//			Vitals v = null;
//			for(int i = 0; i < vitals.size(); i++) {
//				v = vitals.get(i);
//				if(v.getDevice().getSource().equals(source) && v.getIdentifier().equals(update.getIdentifier())) {
//					v.setNumber(fromUpdate(update));
//					
//					if(listener != null) {
//						listener.update(update.getIdentifier(), v.getNumber(), device);
//					}
//					fireContentsChanged(this, i, i);
//					return;
//				}
//			}
//			int i = vitals.size();
//			v = new Vitals(device, update.getIdentifier());
//			v.setNumber(fromUpdate(update));
//			vitals.add(v);
//			fireIntervalAdded(this, 0, 0);
//			fireContentsChanged(this, 0, vitals.size()-1);
//			if(listener != null) {
//				listener.deviceAdded(device);
//			}
//		} else if(Device.ICON.equals(update.getIdentifier())) {
//			device.getDeviceIcon().setImage((ImageUpdate) update);
//			fireDeviceChanged(device);
//		} else if(Device.NAME.equals(update.getIdentifier())) {
//			device.setName(((TextUpdate)update).getValue());
//			fireDeviceChanged(device);
//		} else if(ConnectedDevice.STATE.equals(update.getIdentifier())) {
//			ConnectedDevice.State state = (State) ((EnumerationUpdate)update).getValue();
//			if(null != state) {
//				switch(state) {
//				case Connected:
//					device.getDeviceIcon().setConnected(true);
//					break;
//				default:
//					device.getDeviceIcon().setConnected(false);
//					break;
//				}
//			} else {
//				device.getDeviceIcon().setConnected(false);
//			}
//			fireDeviceChanged(device);
//		}
//	}

}
