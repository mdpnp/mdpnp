package org.mdpnp.guis.waveform.swing;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.mdpnp.guis.waveform.WaveformSource;
import org.mdpnp.guis.waveform.WaveformSourceListener;

public final class WaveformSourceTableModel extends AbstractTableModel implements TableModel, WaveformSourceListener {
	private final WaveformSource source;
	
	public WaveformSourceTableModel(WaveformSource source) {
		this.source = source;
	}

	@Override
	public void waveform(WaveformSource source) {
		fireTableDataChanged();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return Integer.class;
	}
	
	@Override
	public int getColumnCount() {
		return 2;
	}
	@Override
	public String getColumnName(int column) {
		switch(column) {
		case 0:
			return "Index";
		case 1:
			return "Value";
		default:
			return null;
		}
	}
	@Override
	public int getRowCount() {
		return source.getMax();
	}
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch(columnIndex) {
		case 0:
			return rowIndex;
		case 1:
			return source.getValue(rowIndex);
		default:
			return null;
		}
	}

	@Override
	public void reset(WaveformSource source) {
	}
}