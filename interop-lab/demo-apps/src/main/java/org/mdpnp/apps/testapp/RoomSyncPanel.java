package org.mdpnp.apps.testapp;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.mdpnp.messaging.Gateway;

import com.jeffplourde.util.gui.table.XTable;
import com.jeffplourde.util.model.table.SortedTableModelImpl;
import com.jeffplourde.util.model.table.SyntheticTableModelImpl;

public class RoomSyncPanel extends JPanel {
	
	public RoomSyncPanel(Gateway gateway) {
		super(new BorderLayout());
		TableModel model = new ValueCompareModel(gateway);
		try {
			model = new SyntheticTableModelImpl().proxyModels(model, false, new String[] {
					SortedTableModelImpl.class.getName()
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		XTable table = new XTable(model, Integer.MAX_VALUE, false, false, false, true);
		add(new JScrollPane(table), BorderLayout.CENTER);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}

	
	
}
