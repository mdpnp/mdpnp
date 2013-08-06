package org.mdpnp.apps.testapp.pca;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;

import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.vital.Value;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalListModelAdapterImpl;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.apps.testapp.vital.VitalModelListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.infrastructure.Time_t;

public class PCAMonitor extends JPanel implements VitalModelListener {
    private final JList list;
    private final JProgressAnimation pumpProgress;
    private final JTextArea pump = new JTextArea(" ") {
        @Override
        public void setOpaque(boolean isOpaque) {
            super.setOpaque(true);
        };
    };
    private final JTextArea warnings = new JTextArea(" ") {
        public void setOpaque(boolean isOpaque) {
            super.setOpaque(true);
        };
    };

    protected final StringBuilder fastStatusBuilder = new StringBuilder();
    private VitalModel model;
    private final static ListModel EMPTY_MODEL = new DefaultListModel();
    public void setModel(VitalModel model) {
        if(this.model != null) {
            this.model.removeListener(this);
            this.list.setModel(EMPTY_MODEL);
        }
        this.model = model;
        if(this.model != null) {
            this.model.addListener(this);
            this.list.setModel(new VitalListModelAdapterImpl(this.model));
        }
    }
    public VitalModel getModel() {
        return model;
    }
    
    public PCAMonitor(DeviceListModel deviceListModel, ScheduledExecutorService executor) {
        super(new BorderLayout());


        
        JPanel header  = new JPanel(new GridLayout(1,2, 10, 10));
        JPanel panel = new JPanel(new GridLayout(1,2,10,10));
        add(panel, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);
        JLabel l;
        header.add(l=new JLabel("Relevant Vitals"));
        l.setFont(l.getFont().deriveFont(20f));
        header.add(l=new JLabel("Infusion Status (Symbiq)"));
        l.setFont(l.getFont().deriveFont(20f));

        list = new JList(EMPTY_MODEL);
        
        
        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        list.setCellRenderer(new VitalsListCellRenderer(deviceListModel));
        
        JPanel panel2 = new JPanel(new GridLayout(2, 1, 10, 10));
//      pump.setBackground(Color.green);
//      warnings.setBackground(Color.white);
        JPanel panel4 = new JPanel(new GridLayout(1,2,0,0));
        panel4.add(pumpProgress = new JProgressAnimation(executor));
        pumpProgress.setBackground(new Color(1f,1f,1f,.5f));
        pumpProgress.setOpaque(false);
        panel4.add(new JScrollPane(pump));
        panel2.add(panel4);
        JPanel panel3 = new JPanel(new BorderLayout());
        panel3.add(l=new JLabel("Informational Messages"), BorderLayout.NORTH);
        l.setFont(l.getFont().deriveFont(20f));
        panel3.add(new JScrollPane(warnings), BorderLayout.CENTER);
        panel2.add(panel3);
        panel.add(panel2);
        
        JButton resetButton = new JButton("Reset Demo");
        resetButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(0 != (e.getModifiers() & ActionEvent.SHIFT_MASK)) {
                    String cmd = JOptionPane.showInputDialog(PCAMonitor.this, "Pump Command?", "Stop, ") + "\n";
                    resetCommand = cmd;
                    if(null != cmd) {
                        sendPumpCommand(cmd, PCAMonitor.this);
                        reflectState();
                    }
                } else {
                    model.resetInfusion();
                }
            }
            
        });
//      resetButton.setHorizontalAlignment(SwingConstants.RIGHT);
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(resetButton, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);
        pump.setFont(pump.getFont().deriveFont(40f));
        pump.setEditable(false);
        pump.setLineWrap(true);
        pump.setWrapStyleWord(true);
        
        warnings.setFont(pump.getFont());
        warnings.setEditable(false);
        warnings.setLineWrap(true);
        warnings.setWrapStyleWord(true);
        
//      gateway.addListener(new GatewayListener() {
//            
//            @Override
//            public void update(IdentifiableUpdate<?> update) {
//                if(DemoCapnostream20.FAST_STATUS.equals(update.getIdentifier()) ||
//                   DemoCapnostream20.CAPNOSTREAM_UNITS.equals(update.getIdentifier()) ||
//                   DemoCapnostream20.CO2_ACTIVE_ALARMS.equals(update.getIdentifier()) || 
//                   DemoCapnostream20.EXTENDED_CO2_STATUS.equals(update.getIdentifier()) || 
//                   DemoCapnostream20.SLOW_STATUS.equals(update.getIdentifier())) {
//                    log.trace(update.toString());
//                    if(DemoCapnostream20.EXTENDED_CO2_STATUS.equals(update.getIdentifier())) {
//                        NumericUpdate nu = (NumericUpdate) update;
//                        Number v = nu.getValue();
//                        if(v != null) {
//                            fastStatusBuilder.delete(0, fastStatusBuilder.length());
//                            if(0 != (Capnostream.ExtendedCO2Status.CHECK_CALIBRATION & v.intValue())) {
//                                fastStatusBuilder.append("CHECK_CALIBRATION ");
//                            } else if(0 != (Capnostream.ExtendedCO2Status.CHECK_FLOW & v.intValue())) {
//                                fastStatusBuilder.append("CHECK_FLOW ");
//                            } else if(0 != (Capnostream.ExtendedCO2Status.PUMP_OFF & v.intValue())) {
//                                fastStatusBuilder.append("PUMP_OFF ");
//                            } else if(0 != (Capnostream.ExtendedCO2Status.BATTERY_LOW & v.intValue())) {
//                                fastStatusBuilder.append("BATTERY_LOW ");
//                            }
//                            reflectState();
//                        }
//                    }
//                    
////                    NumericUpdate nu = (NumericUpdate) update;
////                    Capnostream.FastStatus.fastStatus(nu.getValue().intValue(), fastStatusBuilder);
////                    reflectState();
//                }
//            }
//        });

    }
    private String resetCommand = "Start, 10\n";

    private static final Logger log = LoggerFactory.getLogger(PCAPanel.class);
    
    private static String lastPumpCommand;
    
    private static final void sendPumpCommand(final String command, final JComponent parent) {
        lastPumpCommand = command;
        
        // removed by JP May 2, 2013
        // We rarely run this software colocated with the software intended to detect this file
        // all this does 99% of the time is create litter in the file system
//      try {
//          FileOutputStream fos = new FileOutputStream("C:\\pump_control_1");
//          fos.write(command.getBytes("ASCII"));
//          fos.flush();
//          fos.close();
//      } catch (Throwable t) {
//          log.error("PUMP COMMAND BY FILE FAILED", t);
//      }
        Thread t = new Thread(new Runnable() {
            public void run() { 
                try { 
                    Class<?> cls = Class.forName("org.mdpnp.PumpControlUDP");
                    Method send = cls.getMethod("send", String.class);
                    log.debug("Sending Pump command " + command.replaceAll("\\n", "\\\\n"));
                    if(!(Boolean)send.invoke(null, command)) {
                        log.error("No response to UDP pump command");
                        if(null != parent) {
                            JOptionPane.showMessageDialog(parent, "Command NOT acknowledged");
                        }
                    } else {
                        
                        if(null != parent) {
                            JOptionPane.showMessageDialog(parent, "Command ACKed");
                        }
                    }
                } catch (ClassNotFoundException e) {
                    log.warn("No PumpControlUDP found", e);
                } catch (NoSuchMethodException e) {
                    log.warn("No PumpControlUDP.send(String):boolean found", e);
                } catch (SecurityException e) {
                    log.warn("PumpControlUDP.send(String):boolean SecurityException", e);
                } catch (HeadlessException e) {
                    log.warn("",e);
                } catch (IllegalAccessException e) {
                    log.warn("", e);
                } catch (IllegalArgumentException e) {
                    log.warn("",e);
                } catch (InvocationTargetException e) {
                    if(e.getCause() instanceof IOException) {
                        IOException ioe = (IOException) e.getCause();
                        log.error("PUMP COMMAND BY UDP FAILED", ioe);
                        if(null != parent) {
                            JOptionPane.showMessageDialog(parent, "Error sending command:"+ioe.getMessage());
                        }
                    } else {
                        throw new RuntimeException(e.getCause());
                    }
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }
    
    public void reflectState() {
        VitalModel model = this.model;
        if(null == model) {
            return;
        }
        
        if(model.isInfusionStopped()) {
            pumpProgress.stop();
            pump.setBackground(Color.red);
            pump.setText(model.getInterlockText());
        } else {
            pumpProgress.start();
            pump.setBackground(Color.green);
            pump.setText(model.getInterlockText());
        }

        switch(model.getState()) {
        case Alarm:
            warnings.setBackground(Color.red);
            break;
        case Normal:
            warnings.setBackground(Color.white);
            break;
        case Warning:
            warnings.setBackground(Color.yellow);
            break;
        default:
        }
        warnings.setText(model.getWarningText());
    }
    

    @Override
    public void vitalChanged(VitalModel model, org.mdpnp.apps.testapp.vital.Vital vital) {
        reflectState();
    }
    @Override
    public void vitalRemoved(VitalModel model, org.mdpnp.apps.testapp.vital.Vital vital) {
        reflectState();
    }
    @Override
    public void vitalAdded(VitalModel model, org.mdpnp.apps.testapp.vital.Vital vital) {
        reflectState();
    }
}
