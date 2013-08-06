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

import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.VitalsModel;
import org.mdpnp.apps.testapp.VitalsModel.VitalsListener;
import org.mdpnp.devices.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.infrastructure.Time_t;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.Subscriber;

public class PCAPanel extends JPanel implements VitalsListener {
	// TODO BOOKMARK HERE!
	private static final Vital[] vitals = new Vital[] {
		new Vital("end tidal CO\u2082", "mmHg", ice.MDC_AWAY_CO2_EXP.VALUE, //Ventilator.END_TIDAL_CO2_MMHG,
				  15.0, 45.0, 1.0, 100.0),
		new Vital("respiratory rate", "bpm", ice.MDC_RESP_RATE.VALUE, //  Ventilator.RESPIRATORY_RATE,
				  8.0, 100.0, 1.0, 200.0),
	    new Vital("heart rate", "bpm", ice.MDC_PULS_OXIM_PULS_RATE.VALUE, // PulseOximeter.PULSE,
	    		  50.0, 120.0, 20.0, 200.0),
	    new Vital("SpO\u2082", "%", ice.MDC_PULS_OXIM_SAT_O2.VALUE, // PulseOximeter.SPO2,
	    		  90.0, 101.0, 80.0, 101.0)
	};
	
//	private static final Identifier[] REQUEST_IDENTIFIERS = new Identifier[] {Ventilator.RESPIRATORY_RATE, PulseOximeter.PULSE, Ventilator.END_TIDAL_CO2_MMHG, PulseOximeter.SPO2/*, Capnograph.AIRWAY_RESPIRATORY_RATE*/,
//            DemoCapnostream20.FAST_STATUS, DemoCapnostream20.CAPNOSTREAM_UNITS, DemoCapnostream20.CO2_ACTIVE_ALARMS, DemoCapnostream20.EXTENDED_CO2_STATUS,
//            DemoCapnostream20.SLOW_STATUS};
	
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
	
	private final Subscriber subscriber;
	private final DeviceListModel deviceListModel;
	private final EventLoop eventLoop;
	private VitalsModel vitalsModel;
	
	private boolean  active;
	
	public void setActive(boolean active) {
	    if(active ^ this.active) {
	        if(active) {
	            reset();
	            vitalsModel = new VitalsModel(subscriber, deviceListModel, eventLoop);
	            vitalsModel.setListener(this);
	            list.setModel(vitalsModel);
	            for(Vital v : vitals) {
	                vitalsModel.addNumericInterest(v.numeric);
	            }
	        } else {
	            list.setModel(new DefaultListModel());
	            vitalsModel.setListener(null);
	            vitalsModel.tearDown();
	            vitalsModel = null;
	        }
	    }
	    this.active = active;
	}
	
	public PCAPanel(DeviceListModel model, Subscriber subscriber, EventLoop eventLoop) {
		super(new BorderLayout());
		this.subscriber = subscriber;
		this.deviceListModel = model;
		this.eventLoop = eventLoop;

		
		JPanel header  = new JPanel(new GridLayout(1,2, 10, 10));
		JPanel panel = new JPanel(new GridLayout(1,2,10,10));
		add(panel, BorderLayout.CENTER);
		add(header, BorderLayout.NORTH);
		JLabel l;
		header.add(l=new JLabel("Relevant Vitals"));
		l.setFont(l.getFont().deriveFont(20f));
		header.add(l=new JLabel("Infusion Status (Symbiq)"));
		l.setFont(l.getFont().deriveFont(20f));

		list = new JList();
		
		
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
		sendPumpCommand("Stop, \n", null);
		pump.setForeground(Color.white);
		pump.setBackground(Color.red);
		pump.setText("Infusion " + reason);

	}
	
	// RESTART THE PUMP
	public void reset() {
		sendPumpCommand(resetCommand, null);
		pump.setForeground(Color.black);
		pump.setBackground(Color.green);
		pump.setText("Infusion " + resetCommand.substring(0, resetCommand.length()-1) +"mL/h");
		
		reflectState();
	}
	
//	Number sat = null;
//	Number rr = null;
//	Number etco2 = null;
//	Number pulse = null;
	
//	MyDevice lastSatSource = null;
//	MyDevice lastRRSource = null;
//	MyDevice lastETCO2Source = null;
//	MyDevice lastPulseSource = null;
	
	private static final boolean equal(Number n1, Number n2) {
		if(null == n1) {
			if(null == n2) {
				return true;
			} else {
				return false;
			}
		} else {
			if(null == n2) {
				return false;
			} else {
				return 0 == Double.compare(n1.doubleValue(), n2.doubleValue());
			}
		}
	}
	
	
	public void reflectState() {
		String[] advisories = new String[vitals.length+1];
		
		String time = timeFormat.format(new Date());
		boolean anyAdvisory = false;
		
		int countOutOfRange = 0;
		StringBuilder outOfRange = new StringBuilder();
		
		for(int i = 0; i < vitals.length; i++) {
			ice.Numeric value = vitals[i].getValue();
			if(!vitals[i].isSet()) {
				anyAdvisory = true;
				advisories[i] = "- no source of " + vitals[i].getName() + "\r\n";
			} else if(vitals[i].getAdvisory_minimum() != null && value.value <= vitals[i].getAdvisory_minimum()) {
				anyAdvisory = true;
				advisories[i] = "- low " + vitals[i].getName() + " " + value.value + " " + vitals[i].getUnits() + "\r\n";
				countOutOfRange++;
				outOfRange.append(advisories[i]);
			} else if(vitals[i].getAdvisory_maximum() != null && value.value >= vitals[i].getAdvisory_maximum()) {
				anyAdvisory = true;
				advisories[i] = "- high " + vitals[i].getName() + " " + value.value + " " + vitals[i].getUnits() + "\r\n";
				countOutOfRange++;
				outOfRange.append(advisories[i]);
			}
		}
		
		if(fastStatusBuilder.length() > 0) {
		    anyAdvisory = true;
		    advisories[advisories.length - 1] = "- CO\u2082 Status: "+fastStatusBuilder.toString()+"\n"; 
		}
		
		
		// Pump stopping rules
		if(active) {
		
    		if(countOutOfRange >= 2) {
    			stop("Stopped\r\n" + outOfRange.toString() + "at " + time + "\r\nnurse alerted");
    		} else {
    			for(Vital v : vitals) {
    				ice.Numeric value = v.getValue();
    				if(v.isSet()) {
    					if(v.getCritical_minimum() != null && value.value <= v.getCritical_minimum()) {
    						stop("Stopped - " + v.getName() + " outside of critical range (" + v.getValue().value + " " + v.getUnits() + ")\r\nat " + time + "\r\nnurse alerted");
    						break;
    					} else if(v.getCritical_maximum() != null && value.value >= v.getCritical_maximum()) {
    						stop("Stopped - " + v.getName() + " outside of critical range (" + v.getValue().value + " " + v.getUnits() + ")\r\nat " + time + "\r\nnurse alerted");
    						break;
    					}
    				}
    			}
    		}
		}
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
	public void update(ice.Numeric n, SampleInfo sampleInfo, Device device) {
		boolean stateChanged = false;
		
		// TODO DO NOT INCLUDE THIS
		if(null != n && n.value <= 5.0f) {
			log.warn("Ignoring " + n.name + " " + n.value + " " + device.getDeviceIdentity().universal_device_identifier);
			return;
		}
		
		for(Vital v : vitals) {
			if(v.getNumeric().equals(n.name)) {
				if(!equal(v.getValue().value, n.value)) {
					if(v.getLastSource() != null && v.getValue() != null && !v.getLastSource().equals(device.getDeviceIdentity().universal_device_identifier) && null == n) {
						
					} else {
					    v.set(n, device);
						stateChanged = true;
					}
				}
			}
		}
				
		if(stateChanged) {
			reflectState();
		}
	}

	@Override
	public void deviceRemoved(Device device) {
		boolean stateChanged = false;
		if(device != null) {
			for(Vital v : vitals) {
				if(device.equals(v.getLastSource())) {
				    v.unset();
					stateChanged = true;
				}
			}
		}
		if(stateChanged) {
			reflectState();
		}
	}

	@Override
	public void deviceAdded(Device device) {

	}	
	
	@Override
	public void deviceChanged(Device device) {
	    
	}
}
