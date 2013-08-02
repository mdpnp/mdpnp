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

import org.mdpnp.apps.testapp.vital.Value;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalListModelAdapterImpl;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.apps.testapp.vital.VitalModelListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.infrastructure.Time_t;

public class PCAPanel extends JPanel implements VitalModelListener {
	private final JList list;
	
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
	
	public PCAPanel() {
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
		list.setCellRenderer(new VitalsListCellRenderer());
		
		JPanel panel2 = new JPanel(new GridLayout(2, 1, 10, 10));
//		pump.setBackground(Color.green);
//		warnings.setBackground(Color.white);
		panel2.add(new JScrollPane(pump));

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
					String cmd = JOptionPane.showInputDialog(PCAPanel.this, "Pump Command?", "Stop, ") + "\n";
					resetCommand = cmd;
					if(null != cmd) {
						sendPumpCommand(cmd, PCAPanel.this);
						reflectState();
					}
				} else {
					reset();
				}
			}
			
		});
//		resetButton.setHorizontalAlignment(SwingConstants.RIGHT);
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
		
//		gateway.addListener(new GatewayListener() {
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
//		try {
//			FileOutputStream fos = new FileOutputStream("C:\\pump_control_1");
//			fos.write(command.getBytes("ASCII"));
//			fos.flush();
//			fos.close();
//		} catch (Throwable t) {
//			log.error("PUMP COMMAND BY FILE FAILED", t);
//		}
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
	
	public void normal(String status) {
		warnings.setBackground(Color.white);
		warnings.setForeground(Color.black);
		warnings.setText("");

	}

	public void advise(String reason) {
		warnings.setBackground(Color.yellow);
		warnings.setForeground(Color.black);
		warnings.setText(reason);
		
	}
	
	// STOP THE PUMP
	public void stop(String reason) {
//		sendPumpCommand("Stop, \n", null);
		pump.setForeground(Color.white);
		pump.setBackground(Color.red);
		pump.setText("Infusion " + reason);

	}
	
	// RESTART THE PUMP
	public void reset() {
//		sendPumpCommand(resetCommand, null);
		pump.setForeground(Color.black);
		pump.setBackground(Color.green);
		pump.setText("Infusion " + resetCommand.substring(0, resetCommand.length()-1) +"mL/h");
		
		reflectState();
	}
	
	public void reflectState() {
	    VitalModel model = this.model;
	    if(null == model) {
	        return;
	    }
		String[] advisories = new String[model.getCount()+1];
		
		String time = timeFormat.format(new Date());
		boolean anyAdvisory = false;
		
		int countOutOfRange = 0;
		StringBuilder outOfRange = new StringBuilder();
		
		for(int i = 0; i < model.getCount(); i++) {
		    Vital vital = model.getVital(i);
		    
		    if(vital.getValues().isEmpty()) {
		        anyAdvisory = true;
                advisories[i] = "- no source of " + vital.getLabel() + "\r\n";
		    } else {
		        for(Value val : vital.getValues()) {
		            if(val.getNumeric().value <= vital.getLow()) {
		                anyAdvisory = true;
		                advisories[i] = "- low " + vital.getLabel() + " " + val.getNumeric().value + " " + vital.getUnits() + "\r\n";
		                countOutOfRange++;
		                outOfRange.append(advisories[i]);
		            }
		            if(val.getNumeric().value >= vital.getHigh()) {
		                anyAdvisory = true;
		                advisories[i] = "- high " + vital.getLabel() + " " + val.getNumeric().value + " " + vital.getUnits() + "\r\n";
		                countOutOfRange++;
		                outOfRange.append(advisories[i]);
		            }
		        }
		    }
		}
		
		if(fastStatusBuilder.length() > 0) {
		    anyAdvisory = true;
		    advisories[advisories.length - 1] = "- CO\u2082 Status: "+fastStatusBuilder.toString()+"\n"; 
		}
		
		
		// Pump stopping rules
//		if(active) {
		
    		if(countOutOfRange >= 2) {
    			stop("Stopped\r\n" + outOfRange.toString() + "at " + time + "\r\nnurse alerted");
    		} else {
    			for(int i = 0; i < model.getCount(); i++) {
    			    Vital vital = model.getVital(i);
    			    for(Value val : vital.getValues()) {
    					if(val.getNumeric().value <= vital.getMinimum()) {
    						stop("Stopped - " + vital.getLabel() + " outside of critical range (" + val.getNumeric().value + " " + vital.getUnits() + ")\r\nat " + time + "\r\nnurse alerted");
    						break;
    					} else if(val.getNumeric().value >= vital.getMaximum()) {
    						stop("Stopped - " + vital.getLabel() + " outside of critical range (" + val.getNumeric().value + " " + vital.getUnits() + ")\r\nat " + time + "\r\nnurse alerted");
    						break;
    					}
    				}
    			}
    		}
//		}
//		if(null != rr && null != etco2 && != pulse && (rr.doubleValue() <= ADV_RR_MIN && rr.doubleValue()  && (etco2.doubleValue() <= ADV_ETCO2_MIN || etco2.doubleValue() >= ADV_ETCO2_MAX)) {
//			// Stop on contingent rule
//			stop("Stopped - Respiratory rate below limit ("+ rr+" bpm) and EtCO\u2082 out of range ("+etco2+"mmHg)");
//		} 
		
		
		
//		else if(null != rr && rr.doubleValue() <= 6.0) {
//			stop("Stopped - Respiratory rate below critical limit ("+ rr+" bpm)");
//		} else if(null != etco2 && (etco2.doubleValue() <= 20.0 || etco2.doubleValue() >= 70)) {
//			stop("Stopped - EtCO\u2082 outside critical range ("+ etco2+"mmHg)");
//		}
		
		// Advisory processing
		if(anyAdvisory) {
			StringBuilder sb = new StringBuilder();
			for(String a : advisories) {
				if(null != a) {
					sb.append(a);
				}
			}
			sb.append("at ").append(time);
			advise(sb.toString());
		} else {
			normal("");
		}
	}
	
	private final static Date date(Time_t t) {
	    return new Date(t.sec * 1000L + t.nanosec / 1000000L);
	}
	
	private static final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

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
    public void setActive(boolean b) {
        // TODO Auto-generated method stub
        
    }
}
