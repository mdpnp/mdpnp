package org.mdpnp.apps.testapp;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.jeffplourde.util.gui.table.XTable;
import com.rti.dds.subscription.Subscriber;

@SuppressWarnings("serial")
public class RoomSyncPanel extends JPanel {

    public RoomSyncPanel(Subscriber subscriber) {
        super(new BorderLayout());
//		TableModel model = new ValueCompareModel(subscriber);
//		try {
//			model = new SyntheticTableModelImpl().proxyModels(model, false, new String[] {
//					SortedTableModelImpl.class.getName()
//			});
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
        XTable table = new XTable(null, Integer.MAX_VALUE, false, false, false, true);
        add(new JScrollPane(table), BorderLayout.CENTER);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }



}
