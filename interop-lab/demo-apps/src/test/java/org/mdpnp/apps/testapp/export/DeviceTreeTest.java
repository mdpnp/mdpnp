package org.mdpnp.apps.testapp.export;


import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.JFrame;

import com.google.common.eventbus.Subscribe;
import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DeviceTreeTest {

    private static final Logger log = LoggerFactory.getLogger(DeviceTreeTest.class);

    public static void main(String[] args) throws Exception {

        final AbstractApplicationContext context =
                new ClassPathXmlApplicationContext(new String[]{"DriverContext.xml"});
        context.registerShutdownHook();

        final DeviceListModel nc = (DeviceListModel)context.getBean("deviceListModel");
        // TODO fix this
        final NumericsDataCollector dc = new NumericsDataCollector(null);

        final DeviceTreeModel tm = new DeviceTreeModel();
        // TODO fix this
//        nc.addListDataListener(tm);
        dc.addDataSampleListener(tm);
        dc.addDataSampleListener(new Object() {
             @Subscribe
             public void handleDataSampleEvent(NumericsDataCollector.NumericSampleEvent evt) throws Exception {
                 if(tm.isEnabled(evt)) {
                     log.info("Processing is enabled for " + DeviceTreeModel.toKey(evt));
                 }
                 else {
                     log.info("Processing is disabled for " + DeviceTreeModel.toKey(evt));
                 }
             }
        });



//        JTree tree = new JTree() {
//            @Override
//            public String convertValueToText(Object value, boolean selected,
//                                             boolean expanded, boolean leaf, int row,
//                                             boolean hasFocus) {
//                return DeviceTreeModel.textForNode(value);
//
//            }
//        };
//        tree.setCellRenderer(new SelectableNode.CheckBoxNodeRenderer());
//        tree.setCellEditor(new SelectableNode.CheckBoxNodeEditor());
//        tree.setEditable(true);
//        tree.setModel(tm);

        JFrame frame = new JFrame("UITest");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dc.destroy();
                super.windowClosing(e);
            }
        });


        (new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    simpleDataMock(dc, tm);
                } catch (Exception ex) {
                    log.error("Failed to handle data mock.", ex);
                }
            }
        })).start();

        frame.getContentPane().setLayout(new BorderLayout());
//        frame.getContentPane().add(tree, BorderLayout.CENTER);
        frame.setSize(640, 480);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    private static void simpleDataMock(DataCollector dc, DeviceTreeModel tm) throws Exception {

        sleep(2000);

        final ArrayList<Device> devices = new ArrayList<>();
        devices.add(new Device("DEVICE0"));
        devices.add(new Device("DEVICE1"));
        devices.add(new Device("DEVICE2"));

        AbstractListModel<Device> dlm = new AbstractListModel<Device>()
        {
            @Override
            public int getSize() {
                return devices.size();
            }

            @Override
            public Device getElementAt(int index) {
                return devices.get(index);
            }
        };

        long now = System.currentTimeMillis();

//        tm.intervalAdded(new ListDataEvent(dlm, ListDataEvent.INTERVAL_ADDED, 0, 0));
//        sleep(1000);
//        tm.intervalAdded(new ListDataEvent(dlm, ListDataEvent.INTERVAL_ADDED, 1, 1));
//        sleep(1000);
//        tm.intervalAdded(new ListDataEvent(dlm, ListDataEvent.INTERVAL_ADDED, 2, 2));
//        sleep(1000);

        for (int n = 0; n < 100; n++) {
            long t = (n%10)*1000;
            int d = n%devices.size();
            int m = d; //n%10;
            int i = d; //n%10;

            NumericsDataCollector.NumericSampleEvent ev = NumericsDataCollector.toEvent("DEVICE"+d, "METRIC"+m, i, t+now, n);
            dc.fireDataSampleEvent(ev);
            sleep(500);

        }
    }

    private static void randomDataMock(DataCollector dc, DeviceTreeModel tm) throws Exception {

        sleep(2000);

        final ArrayList<Device> devices = new ArrayList<>();
        devices.add(new Device("DEVICE0"));
        devices.add(new Device("DEVICE1"));
        devices.add(new Device("DEVICE2"));

        AbstractListModel<Device> dlm = new AbstractListModel<Device>()
        {
            @Override
            public int getSize() {
                return devices.size();
            }

            @Override
            public Device getElementAt(int index) {
                return devices.get(index);
            }
        };

        long now = System.currentTimeMillis();

//        tm.intervalAdded(new ListDataEvent(dlm, ListDataEvent.INTERVAL_ADDED, 0, 0));
//        sleep(1000);
//        tm.intervalAdded(new ListDataEvent(dlm, ListDataEvent.INTERVAL_ADDED, 1, 1));
//        sleep(1000);
//        tm.intervalAdded(new ListDataEvent(dlm, ListDataEvent.INTERVAL_ADDED, 2, 2));
//        sleep(1000);

        for (int n = 0; n < 100; n++) {
            long t = (n + (int) (Math.random() * 10))*1000;
            for (int m = (int) (Math.random() * 5); m > 0; m--) {
                int d = (int) (Math.random() * devices.size());
                int i = (int) (Math.random() * 10);             // let instance range from 0 to 9

                NumericsDataCollector.NumericSampleEvent ev = NumericsDataCollector.toEvent("DEVICE"+d, "METRIC"+m, i, t+now, n);
                dc.fireDataSampleEvent(ev);
                sleep(500);
            }
        }
    }

    private static void sleep(long ms) throws Exception {
        Thread.sleep(ms);
    }

}
