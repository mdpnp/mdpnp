/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.apps.gui.waveform.swing;

import java.util.Set;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.mdpnp.apps.gui.waveform.WaveformSource;
import org.mdpnp.apps.gui.waveform.WaveformSourceListener;
import org.mdpnp.devices.math.DCT;

public final class DCTSource implements WaveformSource, WaveformSourceListener, TableModel {
	private double[] sourceData;
	private double[] data;
	private double[] coeffs;
	private final Set<WaveformSourceListener> listeners = new java.util.concurrent.CopyOnWriteArraySet<WaveformSourceListener>();
	private int count;
	private int maxCoeff;
	private double resolution = 1;
	
	private int checkMax(final int max) {
		if(data == null || max != data.length) {
			if(max == -1) {
				this.data = this.sourceData = this.coeffs = new double[0];
			} else {
				this.data = new double[max];
				this.sourceData = new double[max];
				this.coeffs = new double[max];
			}
		}
		return max;
	}
	
	DCTSource(WaveformSource source) {
		resolution = source.getMillisecondsPerSample();
		int max = source.getMax();
		checkMax(max);
		maxCoeff = this.coeffs.length;
		this.count = source.getCount();
		source.addListener(this);
	}
	
	@Override
	public int getValue(int x) {
		return x >= data.length ? 0 : (int) data[x];
	}

	@Override
	public int getMax() {
		return data.length;
	}
	
	@Override
	public double getMillisecondsPerSample() {
		return resolution;
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public void addListener(WaveformSourceListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(WaveformSourceListener listener) {
		listeners.remove(listener);
	}

	public void setMaxCoeff(int maxCoeff) {
		this.maxCoeff = maxCoeff;
	}
	
	public int getMaxCoeff() {
		return this.maxCoeff;
	}
	
	@Override
	public void waveform(WaveformSource source) {
		if(checkMax(source.getMax())<0) {
			return;
		}
		
		for(int i = 0; i < sourceData.length; i++) {
			sourceData[i] = source.getValue(i);
		}
		DCT.dct(sourceData, coeffs);
		DCT.idct(coeffs, 0, maxCoeff , data);
		count = source.getCount();
		for(WaveformSourceListener listener : listeners) {
			listener.waveform(this);
		}
		
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
			return coeffs[arg0];
		case 2:
			return sourceData[arg0];
		case 3:
			return data[arg0];
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
		this.listeners.remove(arg0);
	}

	@Override
	public void setValueAt(Object arg0, int arg1, int arg2) {
		
	}

	@Override
	public void reset(WaveformSource source) {
		// TODO Auto-generated method stub
		
	}
	
}
