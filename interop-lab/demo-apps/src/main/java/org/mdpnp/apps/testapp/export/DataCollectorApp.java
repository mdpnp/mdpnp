package org.mdpnp.apps.testapp.export;

import com.rti.dds.domain.DomainParticipant;
import ice.Numeric;
import org.mdpnp.apps.testapp.RtConfig;
import org.mdpnp.apps.testapp.vital.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class DataCollectorApp extends JComponent implements DataCollector.DataSampleEventListener {

    private static final Logger log = LoggerFactory.getLogger(DataCollectorApp.class);

    DefaultTableModel tblModel = new DefaultTableModel(
            new Object[][]{},
            new Object[]{"DeviceId", "MetricId", "InstanceId", "Time", "Value"});

    private FileAdapterApplicationFactory.PersisterUI persister;
    private JPanel persisterContainer = new JPanel();

    private final DataCollector dataCollector;

    private Map<String, FileAdapterApplicationFactory.PersisterUI> supportedPersisters = new HashMap<>();


    public DataCollectorApp(DataCollector dc) {
        dataCollector = dc;

        JTable table = new JTable(tblModel);
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);

        supportedPersisters.put("csv",             new CSVPersister());
        supportedPersisters.put("jdbc",            new JdbcPersister());
        supportedPersisters.put("vcd (ieee-1364)", new VerilogVCDPersister());

        final CardLayout cl = new CardLayout();
        final JPanel cards = new JPanel(cl);
        cards.setBorder(BorderFactory.createLineBorder(Color.gray, 2, true));

        JPanel btns = new JPanel();
        btns.setLayout(new GridLayout(0, 1));
        btns.setBorder(BorderFactory.createLineBorder(Color.gray, 2, true));

        ButtonGroup group = new ButtonGroup();

        dataCollector.addDataSampleListener(this);

        for (Map.Entry<String, FileAdapterApplicationFactory.PersisterUI> entry : supportedPersisters.entrySet()) {
            FileAdapterApplicationFactory.PersisterUI p = entry.getValue();
            cards.add(p, entry.getKey());
            JRadioButton btn = new JRadioButton(entry.getKey());
            btns.add(btn);
            group.add(btn);
            btn.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    String name = ((JRadioButton) e.getItem()).getActionCommand();
                    try {
                        // lock on the main object so that the change of the
                        // persister is handled in a synchronized way.
                        //
                        synchronized (DataCollectorApp.this) {
                            FileAdapterApplicationFactory.PersisterUI p = supportedPersisters.get(name);
                            if (e.getStateChange() == ItemEvent.DESELECTED) {
                                p.stop();
                                persister = null;
                            } else if (e.getStateChange() == ItemEvent.SELECTED) {
                                boolean v = p.start();
                                if (v) {
                                    persister = p;
                                    p.setBackground(java.awt.SystemColor.window);
                                } else {
                                    persister = null;
                                    p.setBackground(Color.red);
                                }
                            }
                        }
                        cl.show(cards, name);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            p.putClientProperty("mdpnp.appender", btn);
        }

        persisterContainer.setLayout(new BorderLayout());
        persisterContainer.add(cards, BorderLayout.CENTER);
        persisterContainer.add(btns, BorderLayout.WEST);

        JRadioButton btn = (JRadioButton) supportedPersisters.get("csv").getClientProperty("mdpnp.appender");
        group.setSelected(btn.getModel(), true);

        add(persisterContainer, BorderLayout.SOUTH);
    }

    public void stop() throws Exception {
        for (Map.Entry<String, FileAdapterApplicationFactory.PersisterUI> entry : supportedPersisters.entrySet()) {
            FileAdapterApplicationFactory.PersisterUI p = entry.getValue();
            dataCollector.removeDataSampleListener(p);
            p.stop();
        }
    }

    @Override
    public void handleDataSampleEvent(DataCollector.DataSampleEvent evt) throws Exception {

        // save it for real
        try {
            persister.handleDataSampleEvent(evt);
        } catch (Exception ex) {
            log.error("Failed to save data", ex);
        }

        // and add to the screen for visual.
        Value value = (Value)evt.getSource();
        Numeric n = value.getNumeric();
        long ms = DataCollector.toMilliseconds(n.device_time);
        String devTime = DataCollector.dateFormats.get().format(new Date(ms));
        Object[] row = new Object[]{
                value.getUniqueDeviceIdentifier(),
                value.getMetricId(),
                value.getInstanceId(),
                devTime,
                n.value
        };
        tblModel.insertRow(0, row);
        tblModel.setRowCount(250);
    }


    public static void main(String[] args) throws Exception {

        RtConfig.loadAndSetIceQos();

        final RtConfig rtSetup = RtConfig.setupDDS(0);
        final DomainParticipant participant = rtSetup.getParticipant();

        final DataCollector dc = new DataCollector(participant);
        final DataCollectorApp app = new DataCollectorApp(dc);
        dc.start();

        JFrame frame = new JFrame("UITest");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    dc.stop();
                    app.stop();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                super.windowClosing(e);
            }
        });

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(app, BorderLayout.CENTER);
        frame.setSize(640, 480);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
