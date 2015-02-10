package org.mdpnp.apps.testapp.export;

import ice.Numeric;
import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.apps.testapp.vital.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;


public class DataCollectorApp extends JComponent implements DataCollector.DataSampleEventListener {

    private static final Logger log = LoggerFactory.getLogger(DataCollectorApp.class);

    DefaultTableModel tblModel = new DefaultTableModel(
            new Object[][]{},
            new Object[]{"DeviceId", "InstanceId", "MetricId", "Time", "Value"});

    private JPanel persisterContainer = new JPanel();

    private final DataCollector dataCollector;

    private List<FileAdapterApplicationFactory.PersisterUI> supportedPersisters = new ArrayList<>();

    public DataCollectorApp(DataCollector dc) {
        dataCollector = dc;

        setLayout(new BorderLayout());

        JTable table = new JTable(tblModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        supportedPersisters.add(new CSVPersister());
        supportedPersisters.add(new JdbcPersister());
        supportedPersisters.add(new VerilogVCDPersister());

        final CardLayout cl = new CardLayout();
        final JPanel cards = new JPanel(cl);
        cards.setBorder(BorderFactory.createLineBorder(Color.gray, 2, true));

        JPanel btns = new JPanel();
        btns.setLayout(new GridLayout(0, 1));
        btns.setBorder(BorderFactory.createLineBorder(Color.gray, 2, true));

        ButtonGroup group = new ButtonGroup();

        dataCollector.addDataSampleListener(this);

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

                    //String name = ((JRadioButton) e.getItem()).getActionCommand();
                    try {
                        // lock on the main object so that the change of the
                        // persister is handled in a synchronized way.
                        //
                        synchronized (DataCollectorApp.this) {
                            if (e.getStateChange() == ItemEvent.DESELECTED) {
                                dataCollector.removeDataSampleListener(p);
                                p.stop();
                            } else if (e.getStateChange() == ItemEvent.SELECTED) {
                                boolean v = p.start();
                                if (v) {
                                    dataCollector.addDataSampleListener(p);
                                    p.setBackground(java.awt.SystemColor.window);
                                } else {
                                    p.setBackground(Color.red);
                                }
                            }
                        }
                        cl.show(cards, p.getName());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            // link the two so that we can go from one to the other.
            //
            p.putClientProperty("mdpnp.appender", btn);
            btn.putClientProperty("mdpnp.appender", p);
        }

        persisterContainer.setLayout(new BorderLayout());
        persisterContainer.add(cards, BorderLayout.CENTER);
        persisterContainer.add(btns, BorderLayout.WEST);

        FileAdapterApplicationFactory.PersisterUI p = supportedPersisters.get(0);
        JRadioButton btn = (JRadioButton) p.getClientProperty("mdpnp.appender");
        group.setSelected(btn.getModel(), true);

        add(persisterContainer, BorderLayout.SOUTH);
    }

    public void stop() throws Exception {
        for (FileAdapterApplicationFactory.PersisterUI p : supportedPersisters) {
            dataCollector.removeDataSampleListener(p);
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
