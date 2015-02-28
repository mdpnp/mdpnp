package org.mdpnp.apps.testapp.export;

import ice.Numeric;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.apps.testapp.vital.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;


public class DataCollectorApp extends JComponent implements DataCollector.DataSampleEventListener {

    private static final Logger log = LoggerFactory.getLogger(DataCollectorApp.class);

    DefaultTableModel tblModel = new DefaultTableModel(
            new Object[][]{},
            new Object[]{"DeviceId", "InstanceId", "MetricId", "Time", "Value"});

    private JPanel persisterContainer = new JPanel();

    private final DataCollector   dataCollector;
    private final DeviceListModel deviceListModel;

    private final DataFilter      dataFilter;
    private final DeviceTreeModel deviceTreeModel = new DeviceTreeModel();

    private final AbstractAction  startControl;

    private List<FileAdapterApplicationFactory.PersisterUI> supportedPersisters = new ArrayList<>();

    public DataCollectorApp(DeviceListModel dlm, DataCollector dc) {

        // hold on to the references so that we we can unhook the listeners at the end
        //
        dataCollector   = dc;
        deviceListModel = dlm;

        // device list model maintains the list of what is out there.
        // add a listener to it so that we can dynamically build a tree representation
        // of that information.
        //
        // TODO Change this to the observablelist paradigm
//        deviceListModel.addListDataListener(deviceTreeModel);
        dataCollector.addDataSampleListener(deviceTreeModel);

        // create a data filter - it will act as as proxy between the data collector and
        // actual data consumers. all internal components with register with it for data
        // events.
        dataFilter = new DataFilter(deviceTreeModel);
        dataCollector.addDataSampleListener(dataFilter);

        // add self as a listener so that we can show some busy
        // data in the central panel.
        dataFilter.addDataSampleListener(this);

        setLayout(new BorderLayout());

        JTree tree = new JTree() {
            @Override
            public String convertValueToText(Object value, boolean selected,
                                             boolean expanded, boolean leaf, int row,
                                             boolean hasFocus) {
                return DeviceTreeModel.textForNode(value);

            }
        };
        tree.setCellRenderer(new SelectableNode.CheckBoxNodeRenderer());
        tree.setCellEditor(new SelectableNode.CheckBoxNodeEditor());
        tree.setEditable(true);
        tree.setModel(deviceTreeModel);

        JTable table = new JTable(tblModel);

        JSplitPane masterPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                                new JScrollPane(tree),
                                                new JScrollPane(table));
        add(masterPanel, BorderLayout.CENTER);

        supportedPersisters.add(new CSVPersister());
        supportedPersisters.add(new JdbcPersister());
        supportedPersisters.add(new VerilogVCDPersister());

        final CardLayout cl = new CardLayout();
        final JPanel cards = new JPanel(cl);
        cards.setBorder(BorderFactory.createLineBorder(Color.gray, 2, true));

        startControl = new AbstractAction("") {

            @Override
            public void actionPerformed (ActionEvent e){

                String s = e.getActionCommand();
                FileAdapterApplicationFactory.PersisterUI p =
                        (FileAdapterApplicationFactory.PersisterUI)getValue("mdpnp.appender");

                if("Start".equals(s) && p != null) {
                    try {
                        boolean v = p.start();
                        if (v) {
                            p.setBackground(java.awt.SystemColor.window);
                            dataFilter.addDataSampleListener(p);
                            putValue(Action.NAME, "Stop");
                        } else {
                            p.setBackground(Color.red);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else if("Stop".equals(s)) {
                    if(p != null) {
                        try {
                            dataFilter.removeDataSampleListener(p);
                            p.stop();

                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    putValue(Action.NAME, "Start");
                }
            }

            @Override
            public void putValue(String key, Object newValue) {
                if("mdpnp.appender".equals(key)) {
                    // if there was one, stop it...
                    actionPerformed(new ActionEvent(this, 0, "Stop"));
                }
                super.putValue(key, newValue);
            }
        };

        JPanel btns = new JPanel();
        btns.setLayout(new GridLayout(0, 1));
        btns.setBorder(BorderFactory.createLineBorder(Color.gray, 2, true));

        final ButtonGroup group = new ButtonGroup();

        for (FileAdapterApplicationFactory.PersisterUI p : supportedPersisters) {

            cards.add(p, p.getName());
            JRadioButton btn = new JRadioButton(p.getName());
            btns.add(btn);
            group.add(btn);
            btn.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    JRadioButton btn = (JRadioButton) e.getItem();
                    FileAdapterApplicationFactory.PersisterUI p =
                            (FileAdapterApplicationFactory.PersisterUI)btn.getClientProperty("mdpnp.appender");

                    if (e.getStateChange() == ItemEvent.DESELECTED) {
                        startControl.putValue("mdpnp.appender", null);
                    } else if (e.getStateChange() == ItemEvent.SELECTED) {
                        startControl.putValue("mdpnp.appender", p);
                    }
                    cl.show(cards, p.getName());
                }
            });
            // link the two so that we can go from one to the other.
            //
            p.putClientProperty("mdpnp.appender", btn);
            btn.putClientProperty("mdpnp.appender", p);
        }

        persisterContainer.setLayout(new BorderLayout());
        persisterContainer.add(btns, BorderLayout.WEST);
        persisterContainer.add(cards, BorderLayout.CENTER);
        persisterContainer.add(new JButton(startControl), BorderLayout.EAST);

        FileAdapterApplicationFactory.PersisterUI p = supportedPersisters.get(0);
        JRadioButton btn = (JRadioButton) p.getClientProperty("mdpnp.appender");
        group.setSelected(btn.getModel(), true);

        add(persisterContainer, BorderLayout.SOUTH);
    }

    public void stop() throws Exception {
        dataCollector.removeDataSampleListener(dataFilter);
        // TODO change to observablelist concept
//        deviceListModel.removeListDataListener(deviceTreeModel);

        startControl.putValue("mdpnp.appender", null);

        for (FileAdapterApplicationFactory.PersisterUI p : supportedPersisters) {
            dataFilter.removeDataSampleListener(p);
            p.stop();
        }
    }

    @Override
    public void handleDataSampleEvent(DataCollector.DataSampleEvent evt) throws Exception {

        // Add to the screen for visual.
        Value value = (Value)evt.getSource();

        Numeric n = value.getNumeric();
        long ms = DataCollector.toMilliseconds(n.device_time);
        String devTime = DataCollector.dateFormats.get().format(new Date(ms));
        Object[] row = new Object[]{
                value.getUniqueDeviceIdentifier(),
                value.getInstanceId(),
                value.getMetricId(),
                devTime,
                n.value
        };
        tblModel.insertRow(0, row);
        tblModel.setRowCount(250);
    }

    public static void main(String[] args) throws Exception {

        final AbstractApplicationContext context =
                new ClassPathXmlApplicationContext(new String[]{"DriverContext.xml"});
        context.registerShutdownHook();

        FileAdapterApplicationFactory factory = new FileAdapterApplicationFactory();
        final IceApplicationProvider.IceApp app = factory.create(context);

        app.activate(context);
        Component component = app.getUI();

        JFrame frame = new JFrame("UITest");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    app.stop();
                    app.destroy();
                    log.info("App " + app.getDescriptor().getName() + " stoped OK");
                } catch (Exception ex) {
                    log.error("Failed to stop the app", ex);
                }
                super.windowClosing(e);
            }
        });

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(component, BorderLayout.CENTER);
        frame.setSize(640, 480);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
