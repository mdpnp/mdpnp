package org.mdpnp.messaging;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.mdpnp.data.IdentifiableUpdate;
import org.mdpnp.data.Identifier;
import org.mdpnp.data.enumeration.EnumerationUpdate;
import org.mdpnp.data.identifierarray.MutableIdentifierArrayUpdate;
import org.mdpnp.data.identifierarray.MutableIdentifierArrayUpdateImpl;
import org.mdpnp.data.image.ImageUpdate;
import org.mdpnp.data.text.MutableTextUpdate;
import org.mdpnp.data.text.MutableTextUpdateImpl;
import org.mdpnp.data.text.TextUpdate;
import org.mdpnp.data.textarray.MutableTextArrayUpdate;
import org.mdpnp.data.textarray.MutableTextArrayUpdateImpl;
import org.mdpnp.messaging.Binding.Role;
import org.mdpnp.messaging.BindingFactory.BindingType;
import org.mdpnp.nomenclature.Association;
import org.mdpnp.nomenclature.ConnectedDevice.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkController implements GatewayListener {

	private static final Logger log = LoggerFactory
			.getLogger(NetworkController.class);

	@SuppressWarnings("serial")
	private static class DeviceTableModel extends AbstractTableModel implements
			ListDataListener {
		@SuppressWarnings("rawtypes")
        private final ListModel model;

		public DeviceTableModel(@SuppressWarnings("rawtypes") ListModel model) {
			this.model = model;
			model.addListDataListener(this);
		}

		@Override
		public int getRowCount() {
			return model.getSize();
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Device d = (Device) model.getElementAt(rowIndex);
			switch (columnIndex) {
			case 0:
				return d.getSource();
			case 1:
				return d.getGuid();
			case 2:
				return d.getName();
			default:
				return null;
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		@Override
		public String getColumnName(int column) {
			switch (column) {
			case 0:
				return "Source";
			case 1:
				return "GUID";
			case 2:
				return "Name";
			default:
				return null;
			}
		}

		@Override
		public void intervalAdded(ListDataEvent e) {
			fireTableRowsUpdated(e.getIndex0(), e.getIndex1());
		}

		@Override
		public void intervalRemoved(ListDataEvent e) {
			fireTableRowsDeleted(e.getIndex0(), e.getIndex1());
		}

		@Override
		public void contentsChanged(ListDataEvent e) {
			fireTableRowsUpdated(e.getIndex0(), e.getIndex1());
		}
	}

	private final String source = UUID.randomUUID().toString();

	private final ScheduledExecutorService executor = Executors
			.newSingleThreadScheduledExecutor();

	public String getSource() {
		return source;
	}

	public void solicit() {
		MutableTextUpdate solicit = new MutableTextUpdateImpl(
				Association.SOLICIT);
		solicit.setSource(source);
		solicit.setTarget("*");
		solicit.setValue("");
		log.debug("SOLICIT announcements");
		gateway.update(this, solicit);

	}

	public void acknowledgeDepart(String target) {
		MutableTextUpdate acknowledge = new MutableTextUpdateImpl(
				Association.ACKNOWLEDGE_DEPART);
		acknowledge.setSource(source);
		acknowledge.setTarget(target);
		acknowledge.setValue("");
		log.debug("ACKNOWLEDGE_DEPART of " + target);
		gateway.update(this, acknowledge);
	}

	public void acknowledgeArrive(String target) {
		MutableTextUpdate acknowledge = new MutableTextUpdateImpl(
				Association.ACKNOWLEDGE_ARRIVE);
		acknowledge.setSource(source);
		acknowledge.setTarget(target);
		acknowledge.setValue("");
		log.debug("ACKNOWLEDGE_ARRIVE of " + target);
		gateway.update(this, acknowledge);

	}

	public void disseminate() {
		MutableTextArrayUpdate disseminate = new MutableTextArrayUpdateImpl(
				Association.DISSEMINATE);
		disseminate.setSource(source);
		disseminate.setTarget("*");
		// TODO populate this for real
		disseminate.setValue(acceptedDevices.devicesBySource.keySet().toArray(
				new String[0]));
		log.debug("DISSEMINATE device information for "
				+ disseminate.getValue().length + " devices");
		gateway.update(this, disseminate);

	}

	private final AcceptedDevices acceptedDevices = new AcceptedDevices();
	private final TableModel deviceTableModel = new DeviceTableModel(
			acceptedDevices);

	public TableModel getDeviceTableModel() {
		return deviceTableModel;
	}

	@SuppressWarnings({ "serial", "rawtypes" })
    public static class AcceptedDevices extends AbstractListModel {
		private final Map<String, MutableDevice> devicesBySource = new HashMap<String, MutableDevice>();
		private final List<MutableDevice> contents = new ArrayList<MutableDevice>();
		private final List<MutableDevice> removed = new ArrayList<MutableDevice>();

		public void add(MutableDevice element) {
			contents.add(element);
			devicesBySource.put(element.getSource(), element);
			fireIntervalAdded(this, contents.size() - 1, contents.size() - 1);
		}

		public void clear() {
			devicesBySource.clear();
			int sz = contents.size();
			contents.clear();
			fireIntervalRemoved(this, 0, sz - 1);
		}

		public List<MutableDevice> getRemoved() {
			return removed;
		}

		public boolean remove(Device obj) {
			removed.clear();

			obj = devicesBySource.remove(((Device) obj).getSource());
			int idx = contents.indexOf(obj);
			if (idx >= 0) {
				removed.add((MutableDevice) obj);
				contents.remove(obj);
				fireIntervalRemoved(this, idx, idx);
				return true;
			} else {
				return false;
			}
		}

		public Map<String, MutableDevice> getDevicesBySource() {
			return devicesBySource;
		}

		public void update(MutableDevice device) {
			for (int i = 0; i < getSize(); i++) {
				if (device.equals(getElementAt(i))) {
					fireContentsChanged(this, i, i);
					return;
				}
			}
		}

		@Override
		public int getSize() {
			return contents.size();
		}

		@Override
		public MutableDevice getElementAt(int index) {
			return index < contents.size() ? contents.get(index) : null;
		}
	}

	public AcceptedDevices getAcceptedDevices() {
		return acceptedDevices;
	}

	private final Gateway gateway;
	private final Binding binding;

	public NetworkController(Gateway gateway, BindingType type, String settings)
			throws Exception {
		this.gateway = gateway;
		gateway.addListener(this);
		this.binding = BindingFactory.createBinding(type, gateway, Role.Controller, settings);

		executor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				boolean removed = false;
				for (int i = 0; i < acceptedDevices.getSize(); i++) {
					MutableDevice device = acceptedDevices.getElementAt(i);
					if ((System.currentTimeMillis() - device.getLastUpdate()) > 5000L) {
						log.debug("Removing unalive device " + device.getName()
								+ " " + device.getSource());
						acceptedDevices.remove(device);
						acknowledgeDepart(device.getSource());
						disseminate();
					}
				}
				if (removed) {
					solicit();
				}
			}
		}, 3000L, 3000L, TimeUnit.MILLISECONDS);
	}

	public void tearDown() {
		gateway.removeListener(this);
//		wrapper.disconnect();
		binding.tearDown();
	}

	@Override
	public void update(IdentifiableUpdate<?> update) {
		if (Association.ANNOUNCE_ARRIVE.equals(update.getIdentifier())) {
			final String source = update.getSource();
			log.debug("ANNOUNCE_ARRIVE of " + source);
			if (acceptedDevices.getDevicesBySource().containsKey(source)) {
				log.debug("Removed existing on new announcement " + source);
				Device device = acceptedDevices.getDevicesBySource()
						.get(source);
				acceptedDevices.remove(device);
			}
			// TODO THIS IS ALL TOTAL KLUGE
			MutableDevice device = new DeviceImpl(source, ((TextUpdate) update).getValue());
			acceptedDevices.add(device);
			acknowledgeArrive(source);
			disseminate();
			log.debug("Requesting ICON,NAME,GUID,STATE for new device " + source);
			MutableIdentifierArrayUpdate miau = new MutableIdentifierArrayUpdateImpl(
					org.mdpnp.nomenclature.Device.REQUEST_IDENTIFIED_UPDATES);
			miau.setTarget(source);
			miau.setSource(NetworkController.this.source);
			miau.setValue(new Identifier[] {
					org.mdpnp.nomenclature.Device.ICON,
					org.mdpnp.nomenclature.Device.NAME,
					org.mdpnp.nomenclature.Device.GUID,
					org.mdpnp.nomenclature.ConnectedDevice.STATE });
			gateway.update(this, miau);
		} else if (Association.ANNOUNCE_DEPART.equals(update.getIdentifier())) {
			final String source = update.getSource();
			if (acceptedDevices.getDevicesBySource().containsKey(source)) {
				log.debug("ANNOUNCE_DEPART " + source);
				MutableDevice device = acceptedDevices.getDevicesBySource()
						.get(source);
				acceptedDevices.remove(device);
			} else {
				log.debug("ANNOUNCE_DEPART of unaccepted " + source);
			}

			acknowledgeDepart(source);
			disseminate();
		} else if (org.mdpnp.nomenclature.Device.NAME.equals(update
				.getIdentifier())) {

			String source = update.getSource();
			TextUpdate textUpdate = (TextUpdate) update;
			if (acceptedDevices.getDevicesBySource().containsKey(source)) {
				MutableDevice device = acceptedDevices.getDevicesBySource()
						.get(source);
				device.setName(textUpdate.getValue());
				log.trace("NAME for " + source + " " + textUpdate.getValue());
				acceptedDevices.update(device);
			}
		} else if (org.mdpnp.nomenclature.Device.GUID.equals(update
				.getIdentifier())) {
			String source = update.getSource();
			TextUpdate textUpdate = (TextUpdate) update;
			if (acceptedDevices.getDevicesBySource().containsKey(source)) {
				MutableDevice device = acceptedDevices.getDevicesBySource()
						.get(source);
				device.setGuid(textUpdate.getValue());
				log.trace("GUID for " + source + " " + textUpdate.getValue());
				acceptedDevices.update(device);
			}
		} else if (org.mdpnp.nomenclature.Association.HEARTBEAT
				.equals(update.getIdentifier())) {
			String source = update.getSource();
			MutableDevice device = acceptedDevices.getDevicesBySource().get(
					source);
			if (null != device) {
				device.setLastUpdate(System.currentTimeMillis());
				log.trace("HEARTBEAT for " + source);
				acceptedDevices.update(device);
			}
		} else if (org.mdpnp.nomenclature.ConnectedDevice.STATE
				.equals(update.getIdentifier())) {
			String source = update.getSource();
			EnumerationUpdate e = (EnumerationUpdate) update;
			MutableDevice device = acceptedDevices.getDevicesBySource().get(
					source);
			if (null != device) {
				log.trace("STATE changed for " + source + " to " + e.getValue());
				device.setConnectedState((State) e.getValue());
				acceptedDevices.update(device);
			} else {
				log.warn("Received a state update " + e.getValue()
						+ " for unknown source " + source);
			}
		} else if (org.mdpnp.nomenclature.Association.REQUEST_DISSEMINATE
				.equals(update.getIdentifier())) {
			disseminate();
		} else if(org.mdpnp.nomenclature.Device.ICON.equals(update.getIdentifier())) {
			final String source = update.getSource();
			log.trace("ICON changed for " + source + " to " + update);
			MutableDevice device = acceptedDevices.getDevicesBySource().get(source);
			if(device != null) {
				ImageUpdate imageUpdate = (ImageUpdate) update;
				int width = imageUpdate.getWidth();
				int height = imageUpdate.getHeight();
				BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				byte[] raster = imageUpdate.getRaster();
				IntBuffer ib = ByteBuffer.wrap(raster).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
				for(int y = 0; y < height; y++) {
					for(int x = 0; x < width; x++) {
						bi.setRGB(x, y, ib.get());
					}
				}
				device.setIcon(new DeviceIcon(bi));
				acceptedDevices.update(device);
			}			
		}
	}

}
