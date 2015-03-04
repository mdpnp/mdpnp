package org.mdpnp.apps.testapp.export;

import ice.Numeric;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.apps.testapp.vital.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DataCollectorApp implements DataCollector.DataSampleEventListener {

    private static final Logger log = LoggerFactory.getLogger(DataCollectorApp.class);

    @FXML protected TreeView tree;
    @FXML protected TableView<Row> table;
    @FXML protected SplitPane masterPanel;
    @FXML protected BorderPane persisterContainer;
    @FXML protected Button startControl;
    @FXML protected VBox btns;
    
    protected ObservableList<Row> tblModel = FXCollections.observableArrayList();

    protected static class Row {
        private final String uniqueDeviceIdentifier, instanceId, metricId, devTime;
        private final float value;
        
        public Row(final String uniqueDeviceIdentifier, final String instanceId, 
                   final String metricId, final String devTime, final float value) {
            this.uniqueDeviceIdentifier = uniqueDeviceIdentifier;
            this.instanceId = instanceId;
            this.metricId = metricId;
            this.devTime = devTime;
            this.value = value;
        }
        
        public float getValue() {
            return value;
        }
        
        public String getDevTime() {
            return devTime;
        }
        public String getInstanceId() {
            return instanceId;
        }
        public String getMetricId() {
            return metricId;
        }
        public String getUniqueDeviceIdentifier() {
            return uniqueDeviceIdentifier;
        }
    }
    
    private DataCollector   dataCollector;

    private DataFilter      dataFilter;
    private final DeviceTreeModel deviceTreeModel = new DeviceTreeModel();

    private List<FileAdapterApplicationFactory.PersisterUI> supportedPersisters = new ArrayList<>();

    public DataCollectorApp() {
        
    }
    
    public DataCollectorApp set(DataCollector dc) {
        table.setItems(tblModel);
        // hold on to the references so that we we can unhook the listeners at the end
        //
        dataCollector   = dc;

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
//        dataCollector.addDataSampleListener(dataFilter);

        // add self as a listener so that we can show some busy
        // data in the central panel.
        dataCollector.addDataSampleListener(this);

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
//        tree.setModel(deviceTreeModel);
//
        supportedPersisters.add(new CSVPersister());
        supportedPersisters.add(new JdbcPersister());
        supportedPersisters.add(new VerilogVCDPersister());


//        startControl = new AbstractAction("") {
//
//            @Override
//            public void actionPerformed (ActionEvent e){
//
//            }
//
//            @Override
//            public void putValue(String key, Object newValue) {
//                if("mdpnp.appender".equals(key)) {
//                    // if there was one, stop it...
//                    actionPerformed(new ActionEvent(this, 0, "Stop"));
//                }
//                super.putValue(key, newValue);
//            }
//        };
//
//
//        final ButtonGroup group = new ButtonGroup();

//        for (FileAdapterApplicationFactory.PersisterUI p : supportedPersisters) {
//
//            cards.add(p, p.getName());
//            JRadioButton btn = new JRadioButton(p.getName());
//            btns.add(btn);
//            group.add(btn);
//            btn.addItemListener(new ItemListener() {
//                @Override
//                public void itemStateChanged(ItemEvent e) {
//                    JRadioButton btn = (JRadioButton) e.getItem();
//                    FileAdapterApplicationFactory.PersisterUI p =
//                            (FileAdapterApplicationFactory.PersisterUI)btn.getClientProperty("mdpnp.appender");
//
//                    if (e.getStateChange() == ItemEvent.DESELECTED) {
//                        startControl.putValue("mdpnp.appender", null);
//                    } else if (e.getStateChange() == ItemEvent.SELECTED) {
//                        startControl.putValue("mdpnp.appender", p);
//                    }
//                    cl.show(cards, p.getName());
//                }
//            });
//            // link the two so that we can go from one to the other.
//            //
//            p.putClientProperty("mdpnp.appender", btn);
//            btn.putClientProperty("mdpnp.appender", p);
//        }
//
//        persisterContainer.add(btns, BorderLayout.WEST);
//        persisterContainer.add(cards, BorderLayout.CENTER);
//
//        FileAdapterApplicationFactory.PersisterUI p = supportedPersisters.get(0);
//        JRadioButton btn = (JRadioButton) p.getClientProperty("mdpnp.appender");
//        group.setSelected(btn.getModel(), true);
        return this;
    }

    public void clickStartControl(ActionEvent evt) {
//        String s = e.getActionCommand();
//        FileAdapterApplicationFactory.PersisterUI p =
//                (FileAdapterApplicationFactory.PersisterUI)getValue("mdpnp.appender");
//
//        if("Start".equals(s) && p != null) {
//            try {
//                boolean v = p.start();
//                if (v) {
//                    p.setBackground(java.awt.SystemColor.window);
//                    dataFilter.addDataSampleListener(p);
//                    putValue(Action.NAME, "Stop");
//                } else {
//                    p.setBackground(Color.red);
//                }
//            } catch (Exception ex) {
//                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        }
//        else if("Stop".equals(s)) {
//            if(p != null) {
//                try {
//                    dataFilter.removeDataSampleListener(p);
//                    p.stop();
//
//                } catch (Exception ex) {
//                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            }
//            putValue(Action.NAME, "Start");
//        }
//
    }
    
    public void stop() throws Exception {
        dataCollector.removeDataSampleListener(dataFilter);
        // TODO change to observablelist concept
//        deviceListModel.removeListDataListener(deviceTreeModel);

//        startControl.putValue("mdpnp.appender", null);

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
        final Row row = new Row(value.getUniqueDeviceIdentifier(), ""+value.getInstanceId(),
                          value.getMetricId(), devTime, n.value);
        Platform.runLater(new Runnable() {
            public void run() {
                tblModel.add(0, row);
                if(tblModel.size()>250) {
                    tblModel.subList(250, tblModel.size()).clear();
                }
                
            }
        });
    }

    public static void main(String[] args) throws Exception {

        final AbstractApplicationContext context =
                new ClassPathXmlApplicationContext(new String[]{"DriverContext.xml"});
        context.registerShutdownHook();

        FileAdapterApplicationFactory factory = new FileAdapterApplicationFactory();
        final IceApplicationProvider.IceApp app = factory.create(context);

        app.activate(context);
//        Component component = app.getUI();

//        JFrame frame = new JFrame("UITest");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                try {
//                    app.stop();
//                    app.destroy();
//                    log.info("App " + app.getDescriptor().getName() + " stoped OK");
//                } catch (Exception ex) {
//                    log.error("Failed to stop the app", ex);
//                }
//                super.windowClosing(e);
//            }
//        });
//
//        frame.getContentPane().setLayout(new BorderLayout());
//        frame.getContentPane().add(component, BorderLayout.CENTER);
//        frame.setSize(640, 480);
//        frame.setLocationRelativeTo(null);
//        frame.setVisible(true);
    }

}
