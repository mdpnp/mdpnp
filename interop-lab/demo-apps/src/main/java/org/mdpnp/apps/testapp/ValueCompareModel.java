package org.mdpnp.apps.testapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.mdpnp.comms.Gateway;
import org.mdpnp.comms.GatewayListener;
import org.mdpnp.comms.IdentifiableUpdate;
import org.mdpnp.comms.Identifier;
import org.mdpnp.comms.data.enumeration.EnumerationUpdate;
import org.mdpnp.comms.data.identifierarray.IdentifierArrayUpdate;
import org.mdpnp.comms.data.identifierarray.MutableIdentifierArrayUpdate;
import org.mdpnp.comms.data.identifierarray.MutableIdentifierArrayUpdateImpl;
import org.mdpnp.comms.data.image.ImageUpdate;
import org.mdpnp.comms.data.numeric.NumericUpdate;
import org.mdpnp.comms.data.text.MutableTextUpdate;
import org.mdpnp.comms.data.text.MutableTextUpdateImpl;
import org.mdpnp.comms.data.text.TextUpdate;
import org.mdpnp.comms.data.textarray.TextArrayUpdate;
import org.mdpnp.comms.nomenclature.ConnectedDevice;
import org.mdpnp.messaging.DeviceIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValueCompareModel extends AbstractTableModel implements TableModel, GatewayListener {
//	private final DDSNetworkController.AcceptedDevices acceptedDevices;
	
	private static final Logger log = LoggerFactory.getLogger(ValueCompareModel.class);
	
	public static class Row {
		public Identifier identifier;
		public List<Object> values = new ArrayList<Object>();
	}
	
	private static class Col { 
		public String source;
		public String name;
		public DeviceIcon icon;
	}
	
	private final List<Col> sources = new ArrayList<Col>();
	
	
	public Row getValues(Identifier identifier) {
		return mapRows.get(identifier);
	}
	
	private final List<Row> rows = new ArrayList<Row>();
	private final Map<Identifier, Row> mapRows = new HashMap<Identifier, Row>();
	
	public static final int FIXED_COLUMNS = 2;

	private final Gateway gateway;
	
	public ValueCompareModel(Gateway gateway) {
		this.gateway = gateway;
		gateway.addListener(this);
		gateway.update(this, new MutableTextUpdateImpl(org.mdpnp.comms.nomenclature.Association.REQUEST_DISSEMINATE));
	}

	@Override
	public int getRowCount() {
		return rows.size();
	}

	public DeviceIcon getDeviceIcon(int column) {
		switch(column) {
		case 0:
			return null;
		case 1:
			return null;
		default:
			Col source = sources.get(column - FIXED_COLUMNS);
			return null == source.icon ? null : source.icon;
		}
	}
	
	@Override
	public int getColumnCount() {
		return FIXED_COLUMNS + sources.size();
	}

	@Override
	public String getColumnName(int column) {
		switch(column) {
		case 0:
			return "Type";
		case 1:
			return "Identifier";
		default:
			Col source = sources.get(column - FIXED_COLUMNS);
			return null == source.name ? "???" : source.name;
		}
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch(columnIndex) {
		case 0:
			return rows.get(rowIndex).identifier.getField().getDeclaringClass().getSimpleName();
		case 1:
			return rows.get(rowIndex).identifier.getField().getName();
		default:
			columnIndex -= FIXED_COLUMNS;
			return rows.get(rowIndex).values.size()>columnIndex?rows.get(rowIndex).values.get(columnIndex):null;
		}
	}

	private final int indexOfSource(String source) {
		for(int i = 0; i < sources.size(); i++) {
			if(source != null && sources.get(i).source != null) {
				if(source.equals(sources.get(i).source)) {
					return i;
				}
			}
		}
		return -1;
	}
	
	private final int indexOfIdentifier(Identifier identifier) {
		for(int i = 0; i < rows.size(); i++) {
			if(identifier.equals(rows.get(i).identifier)) {
				return i;
			}
		}
		return -1;
	}
	
	private final boolean addSource(String source, boolean fire) {
		if(indexOfSource(source) < 0) {
			log.trace("Adding " + source + " and requesting a real name");
			Col c = new Col();
			c.source = source;
			sources.add(c);
			MutableTextUpdate mtu = new MutableTextUpdateImpl(org.mdpnp.comms.nomenclature.Device.REQUEST_AVAILABLE_IDENTIFIERS);
			mtu.setValue("");
			mtu.setTarget(source);
			mtu.setSource("*");
			gateway.update(this, mtu);
			
			MutableIdentifierArrayUpdate miau = new MutableIdentifierArrayUpdateImpl(org.mdpnp.comms.nomenclature.Device.REQUEST_IDENTIFIED_UPDATES);
			miau.setValue(new Identifier[] {org.mdpnp.comms.nomenclature.Device.NAME, org.mdpnp.comms.nomenclature.Device.ICON});
			miau.setTarget(source);
			miau.setSource("*");
			gateway.update(this, miau);
			if(fire) {
				fireTableStructureChanged();
			}
			return true;
		} else {
			return false;
		}
	}
	private static final boolean allNull(List<Object> o) {
		for(Object _o  : o) {
			if(_o != null) {
				return false;
			}
		}
		return true;
	}
	private final boolean removeSource(String source, boolean fire) {
		int idx = indexOfSource(source);
		if(idx >= 0) {
			log.trace("Removing " + sources.get(idx).name + " " + source);
			sources.remove(idx);
			for(Row row : rows) {
				if(idx < row.values.size()) {
					row.values.remove(idx);
				}
			}
			// prune!
			if(sources.isEmpty()) {
				rows.clear();
			} else {
				List<Row> clearRows = new ArrayList<Row>();
				for(Row row : rows) {
					if(row.values.isEmpty() || allNull(row.values)) {
						clearRows.add(row);
					}
				}
				rows.removeAll(clearRows);
				mapRows.clear();
			}
			if(fire) {
				fireTableStructureChanged();
				fireTableDataChanged();
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void update(IdentifiableUpdate<?> update) {
		String source = update.getSource();
		Identifier ident = update.getIdentifier();
		if(null == ident) {
			log.warn("received an update with a null identifier");
			return;
		}
		
		if(org.mdpnp.comms.nomenclature.Device.GET_AVAILABLE_IDENTIFIERS.equals(ident)) {
			MutableIdentifierArrayUpdate miau = new MutableIdentifierArrayUpdateImpl( org.mdpnp.comms.nomenclature.Device.REQUEST_IDENTIFIED_UPDATES );
			miau.setTarget(source);
			miau.setSource("*");
			miau.setValue(((IdentifierArrayUpdate)update).getValue());
			gateway.update(this, miau);
		}
		
		if(org.mdpnp.comms.nomenclature.ConnectedDevice.STATE.equals(ident)) {
			int idx = indexOfSource(update.getSource());
			if(idx >= 0) {
				Col c = sources.get(idx);
				if(c.icon != null) {
					EnumerationUpdate eu = (EnumerationUpdate) update;
					if(eu.getValue() != null) {
						switch( (ConnectedDevice.State) eu.getValue() ) {
						case Connected:
							c.icon.setConnected(true);
							break;
						default:
							c.icon.setConnected(false);
							break;
						}
						fireTableStructureChanged();
					}
				}
			} else {
				log.trace("Received connectedstate for unknown source " + update.getSource());
			}
			
		}
		
		if(org.mdpnp.comms.nomenclature.Association.class.equals(ident.getField().getDeclaringClass())) {
			if(org.mdpnp.comms.nomenclature.Association.DISSEMINATE.equals(ident)) {
				log.trace("Handling a disseminate");
				Set<String> nolongervalid = new HashSet<String>();
				for(Col c : sources) {
					nolongervalid.add(c.source);
				}
				for(String s : ((TextArrayUpdate)update).getValue()) {
					addSource(s, true);
					nolongervalid.remove(s);
				}
				for(String s : nolongervalid) {
					removeSource(s, true);
				}
				log.trace("Done handling disseminate");
			} 
			return;
		}		
		if(org.mdpnp.comms.nomenclature.Device.class.equals(ident.getField().getDeclaringClass())) {
			if(org.mdpnp.comms.nomenclature.Device.NAME.equals(ident)) {
				int idx = indexOfSource(update.getSource());
				if(idx >= 0) {
					String name = ((TextUpdate)update).getValue();
					if(name != null && !name.equals(sources.get(idx).name)) {
						sources.get(idx).name = name;
						fireTableStructureChanged();
					}
				} else {
					log.trace("Received name for unknown source " + update.getSource());
				}
			} else if(org.mdpnp.comms.nomenclature.Device.ICON.equals(ident)) {
				int idx = indexOfSource(update.getSource());
				if(idx >= 0) {
					sources.get(idx).icon = new DeviceIcon((ImageUpdate) update);
					fireTableStructureChanged();
				} else {
					log.trace("Received icon for unknown source " + update.getSource());
				}
			}
			return;
		}
		if(org.mdpnp.comms.nomenclature.ConnectedDevice.class.equals(ident.getField().getDeclaringClass())) {
			return;
		}	
		Object value = null;
		
		if(update instanceof NumericUpdate) {
			value = ((NumericUpdate)update).getValue();
		} else if(update instanceof TextUpdate) {
			value = ((TextUpdate)update).getValue();
		} else {
			return;
		}
		int indexOfSource = indexOfSource(source);
		
		if(indexOfSource < 0) {
			log.debug("Ignoring from unknown source: " + source + " " + update);
//			addSource(source);
			return;
		}
		
		

		int indexOfIdentifier = indexOfIdentifier(ident);
		Row row = null;
		if(indexOfIdentifier < 0) {
			indexOfIdentifier = rows.size();
			rows.add(row = new Row());
			row.identifier = update.getIdentifier();
			mapRows.put(row.identifier, row);
			fireTableRowsInserted(indexOfIdentifier, indexOfIdentifier);
		} else {
			row = rows.get(indexOfIdentifier);
		}
		
		while(indexOfSource>=row.values.size()) {
			row.values.add(null);
		}
		
		row.values.set(indexOfSource, value);
		fireTableCellUpdated(indexOfIdentifier, indexOfSource+FIXED_COLUMNS);
		
	}

	
	
	
}
