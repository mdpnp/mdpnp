package org.mdpnp.apps.testapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VitalsModel extends AbstractListModel implements ListModel {
	private final Set<Integer> numericInterest = new HashSet<Integer>();
	private final Set<Integer> sampleArrayInterest = new HashSet<Integer>();
	
	public void addNumericInterest(Integer i) {
		numericInterest.add(i);
	}
	public void addInterest(Integer i) {
		sampleArrayInterest.add(i);
	}
	private static final Logger log = LoggerFactory.getLogger(VitalsModel.class);
	public interface VitalsListener {
		void update(ice.Numeric n, MyDevice device);
		void deviceRemoved(MyDevice device);
		void deviceAdded(MyDevice device);
	}
	
	private VitalsListener listener;
	
	public void setListener(VitalsListener listener) {
		this.listener = listener;
	}
	
	private final Map<String, MyDevice> devices = new HashMap<String, MyDevice>();
	
	public VitalsModel() {
		
	}
	
	public static final class MyDevice {
		private final String source;
		private final DeviceIcon deviceIcon = new DeviceIcon();
		private String name;
		
		public MyDevice(String source) {
			this.source = source;
		}
		public DeviceIcon getDeviceIcon() {
			return deviceIcon;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public String getSource() {
			return source;
		}
	}
	
	public static final class Vitals {
		private final MyDevice device;
		private final Integer identifier;
		
		private Number number;
		public Vitals(MyDevice device, Integer identifier) {
			this.device = device;
			this.identifier = identifier;
		}
		public MyDevice getDevice() {
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

	public void fireDeviceChanged(MyDevice device) {
		for(int i = 0; i < vitals.size(); i++) {
			if(vitals.get(i).getDevice().equals(device)) {
				fireContentsChanged(this, i, i);
			}
		}
	}
	
	public boolean removeDevice(MyDevice device) {
		for(int i = 0; i < vitals.size(); i++) {
			if(vitals.get(i).getDevice().equals(device)) {
				log.debug("removed " + vitals.get(i).getIdentifier() + " " + device.getSource());
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
	
	private static Number fromUpdate(IdentifiableUpdate<?> update) {
		if(update instanceof NumericUpdate) {
			return ((NumericUpdate)update).getValue();
		} else if(update instanceof WaveformUpdate) {
			Number[] values = ((WaveformUpdate)update).getValues();
			if(null == values || values.length == 0) {
				return null;
			} else {
				return values[values.length - 1];
			}
		} else {
			return null;
		}
	}
	
	@Override
	public void update(IdentifiableUpdate<?> update) {
		if(null==update) {
			return;
		}
		String source = update.getSource();
		if(null == source || "*".equals(source)) {
			return;
		}
		MyDevice device = devices.get(source);
		if(device == null) {
			device = new MyDevice(source);
			devices.put(source, device);
			
		}
		
		if(Association.DISSEMINATE.equals(update.getIdentifier())) {
			Set<String> sources = new HashSet<String>();
			for(String d : devices.keySet()) {
				sources.add(d);
			}
			log.debug("Existing:"+sources.toString());
			for(String s : ((TextArrayUpdate)update).getValue()) {
				sources.remove(s);
			}
			log.debug("To Remove:"+sources.toString());
			for(String s : sources) {
				MyDevice d = devices.get(s);
				while(removeDevice(d)) {
					
				}
				devices.remove(s);
			}
		} else if(interest.contains(update.getIdentifier())) {
			Vitals v = null;
			for(int i = 0; i < vitals.size(); i++) {
				v = vitals.get(i);
				if(v.getDevice().getSource().equals(source) && v.getIdentifier().equals(update.getIdentifier())) {
					v.setNumber(fromUpdate(update));
					
					if(listener != null) {
						listener.update(update.getIdentifier(), v.getNumber(), device);
					}
					fireContentsChanged(this, i, i);
					return;
				}
			}
			int i = vitals.size();
			v = new Vitals(device, update.getIdentifier());
			v.setNumber(fromUpdate(update));
			vitals.add(v);
			fireIntervalAdded(this, 0, 0);
			fireContentsChanged(this, 0, vitals.size()-1);
			if(listener != null) {
				listener.deviceAdded(device);
			}
		} else if(Device.ICON.equals(update.getIdentifier())) {
			device.getDeviceIcon().setImage((ImageUpdate) update);
			fireDeviceChanged(device);
		} else if(Device.NAME.equals(update.getIdentifier())) {
			device.setName(((TextUpdate)update).getValue());
			fireDeviceChanged(device);
		} else if(ConnectedDevice.STATE.equals(update.getIdentifier())) {
			ConnectedDevice.State state = (State) ((EnumerationUpdate)update).getValue();
			if(null != state) {
				switch(state) {
				case Connected:
					device.getDeviceIcon().setConnected(true);
					break;
				default:
					device.getDeviceIcon().setConnected(false);
					break;
				}
			} else {
				device.getDeviceIcon().setConnected(false);
			}
			fireDeviceChanged(device);
		}
	}

}
