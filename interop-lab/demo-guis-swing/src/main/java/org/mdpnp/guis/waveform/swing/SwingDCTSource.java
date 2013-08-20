/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.guis.waveform.swing;

import java.util.Set;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.mdpnp.devices.math.DCT;
import org.mdpnp.guis.waveform.DCTSource;
import org.mdpnp.guis.waveform.WaveformSource;
import org.mdpnp.guis.waveform.WaveformSourceListener;

public class SwingDCTSource extends DCTSource implements WaveformSource, WaveformSourceListener, TableModel {

	public SwingDCTSource(WaveformSource source) {
	    super(source);
	}

	
	@Override
	public void waveform(WaveformSource source) {
	    super.waveform(source);
		
	    int count = getCount();
	    
		tme.setFirstRow(count);
		tme.setLastRow(count);
		
		for(TableModelListener listener : tableListeners) {
			listener.tableChanged(tme);
		}
	}
	
	private static class MutableTableModelEvent extends TableModelEvent {
		public MutableTableModelEvent(TableModel source) {
			super(source);
		}
		
		private int first, last;
		@Override
		public int getFirstRow() {return first;}
		@Override
		public int getLastRow() {return last;}
		public void setFirstRow(int first) {
			this.first = first;
		}
		public void setLastRow(int last) {
			this.last = last;
		}
	}

	private final MutableTableModelEvent tme = new MutableTableModelEvent(this);
	
	private final Set<TableModelListener> tableListeners = new java.util.concurrent.CopyOnWriteArraySet<TableModelListener>();
	
	@Override
	public void addTableModelListener(TableModelListener arg0) {
		this.tableListeners.add(arg0);
	}

	@Override
	public Class<?> getColumnClass(int arg0) {
		switch(arg0) {
		case 0:
			return Integer.class;
		case 1:
		case 2:
		case 3:
			return Double.class;
		default:
			return null;
		}
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public String getColumnName(int arg0) {
		switch(arg0) {
		case 0:
			return "Index";
		case 1:
			return "Coefficient";
		case 2:
			return "Source Data";
		case 3:
			return "iDCT Data";
		default:
			return null;
		}
	}

	@Override
	public int getRowCount() {
		return getMax();
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		switch(arg1) {
		case 0:
			return arg0;
		case 1:
			return getCoeffs()[arg0];
		case 2:
			return getSourceData()[arg0];
		case 3:
			return getData()[arg0];
		default:
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}

	@Override
	public void removeTableModelListener(TableModelListener arg0) {
		this.tableListeners.remove(arg0);
	}

	@Override
	public void setValueAt(Object arg0, int arg1, int arg2) {
		
	}
}
