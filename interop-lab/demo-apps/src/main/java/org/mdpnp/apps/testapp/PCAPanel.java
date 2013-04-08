package org.mdpnp.apps.testapp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;

import org.mdpnp.apps.testapp.VitalsModel.MyDevice;
import org.mdpnp.apps.testapp.VitalsModel.Vitals;
import org.mdpnp.apps.testapp.VitalsModel.VitalsListener;
import org.mdpnp.comms.Gateway;
import org.mdpnp.comms.Identifier;
import org.mdpnp.comms.data.identifierarray.MutableIdentifierArrayUpdate;
import org.mdpnp.comms.data.identifierarray.MutableIdentifierArrayUpdateImpl;
import org.mdpnp.comms.data.numeric.Numeric;
import org.mdpnp.comms.nomenclature.Device;
import org.mdpnp.comms.nomenclature.PulseOximeter;
import org.mdpnp.comms.nomenclature.Ventilator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PCAPanel extends JPanel implements VitalsListener {
	// TODO BOOKMARK HERE!
	private static final Vital[] vitals = new Vital[] {
		new Vital("end tidal CO2", "mmHg", Ventilator.END_TIDAL_CO2_MMHG,
				  25.0, 45.0, 1.0, 100.0),
		new Vital("respiratory rate", "bpm", Ventilator.RESPIRATORY_RATE,
				  8.0, 100.0, 1.0, 200.0),
	    new Vital("heart rate", "bpm", PulseOximeter.PULSE,
	    		  50.0, 120.0, 20.0, 200.0),
	    new Vital("SpO2", "%", PulseOximeter.SPO2,
	    		  90.0, 100.0, 0.0, 100.0)
	};
	
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

	private static class VitalsRenderer extends JPanel implements ListCellRenderer {

		private final JLabel name = new JLabel(" ");
		private final JLabel deviceName = new JLabel(" ");
		private final JLabel value = new JLabel(" ");
		private final JLabel icon = new JLabel(" ");
		
		public VitalsRenderer() {
			super(new GridLayout(1, 2));
			setBorder(BorderFactory.createLineBorder(Color.gray, 1));
			setOpaque(false);
			name.setFont(name.getFont().deriveFont(18f));
			value.setFont(value.getFont().deriveFont(24f));
			deviceName.setFont(deviceName.getFont().deriveFont(14f));
//			value.setFont(value.getFont().deriveFont(14f));
			JPanel pan = new JPanel(new GridLayout(3,1));
			pan.setOpaque(false);
			pan.add(name);
			pan.add(value);
			pan.add(deviceName);
			add(pan);
			add(icon);
		}
		
		@Override
		public Component getListCellRendererComponent(JList list, Object val,
				int index, boolean isSelected, boolean cellHasFocus) {
			VitalsModel.Vitals v = (Vitals) val;
			String name = v.getIdentifier().getField().getName();
			String units = "";
			if("SPO2".equals(name)) {
				name = "SpO2";
				units = "%";
			} else if("RESPIRATORY_RATE".equals(name)) {
				name = "Respiratory Rate";
				units = "bpm";
			} else if("PULSE".equals(name)) {
				name = "Heart Rate";
				units = "bpm";
			} else if("END_TIDAL_CO2_MMHG".equals(name)) {
				name = "etCO2";
				units = "mmHg";
			}
			this.name.setText(name);
			if(v.getNumber() == null) {
				value.setText("<unavailable>");
			} else {
				value.setText(""+v.getNumber()+" "+units);
			}
			
			icon.setIcon(v.getDevice().getDeviceIcon());
			deviceName.setText(v.getDevice().getName());
			return this;
		}
		
	}
	
	public PCAPanel(VitalsModel model, Gateway gateway) {
		super();
		setLayout(new BorderLayout());
		

		
		// Reset before we can get callbacks
		reset();
		
		
		if(model != null) {
			model.setListener(this);
		}
		
		// Requests data from existing devices
		MutableIdentifierArrayUpdate miau = new MutableIdentifierArrayUpdateImpl(Device.REQUEST_IDENTIFIED_UPDATES);
		miau.setValue(new Identifier[] {Ventilator.RESPIRATORY_RATE, PulseOximeter.PULSE, Ventilator.END_TIDAL_CO2_MMHG, PulseOximeter.SPO2/*, Capnograph.AIRWAY_RESPIRATORY_RATE*/});
		
		for(int i = 0; i < model.getSize(); i++) {
			Vitals device = (Vitals) model.getElementAt(i);
			miau.setTarget(device.getDevice().getSource());
			miau.setSource("*");
			gateway.update(miau);
		}
		
		JPanel header  = new JPanel(new GridLayout(1,2, 10, 10));
		JPanel panel = new JPanel(new GridLayout(1,2,10,10));
		add(panel, BorderLayout.CENTER);
		add(header, BorderLayout.NORTH);
		JLabel l;
		header.add(l=new JLabel("Relevant Vitals"));
		l.setFont(l.getFont().deriveFont(20f));
		header.add(l=new JLabel("Infusion Status (Symbiq)"));
		l.setFont(l.getFont().deriveFont(20f));

		list = null == model ? new JList() : new JList(model);
		
		
		
		panel.add(new JScrollPane(list), BorderLayout.CENTER);
		list.setCellRenderer(new VitalsRenderer());
		
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
		
	}
	private String resetCommand = "Start, 100\n";

	private static final Logger log = LoggerFactory.getLogger(PCAPanel.class);
	
	private static String lastPumpCommand;
	
	private static final void sendPumpCommand(final String command, final JComponent parent) {
		lastPumpCommand = command;
		try {
			FileOutputStream fos = new FileOutputStream("C:\\pump_control_1");
			fos.write(command.getBytes("ASCII"));
			fos.flush();
			fos.close();
		} catch (Throwable t) {
			log.error("PUMP COMMAND BY FILE FAILED", t);
		}
		Thread t = new Thread(new Runnable() {
			public void run() { 
				try { 
					Class<?> cls = Class.forName("org.mdpnp.PumpControlUDP");
					Method send = cls.getMethod("send", String.class);
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
	
	
	private static class Vital {
		private final String name;
		private final String units;
		private final Numeric numeric;
		private final Double advisory_minimum;
		private final Double advisory_maximum;
		private final Double critical_minimum;
		private final Double critical_maximum;
		
		private MyDevice lastSource;
		private Number value;
		
		public Number getValue() {
			return value;
		}
		public void setValue(Number value) {
			this.value = value;
		}
		public String getName() {
			return name;
		}
		public Numeric getNumeric() {
			return numeric;
		}
		public Double getAdvisory_maximum() {
			return advisory_maximum;
		}
		public Double getAdvisory_minimum() {
			return advisory_minimum;
		}
		public Double getCritical_maximum() {
			return critical_maximum;
		}
		public Double getCritical_minimum() {
			return critical_minimum;
		}
		public String getUnits() {
			return units;
		}
		public MyDevice getLastSource() {
			return lastSource;
		}
		public void setLastSource(MyDevice lastSource) {
			this.lastSource = lastSource;
		}
		public Vital (String name, String units, Numeric numeric, Double advisory_minimum, Double advisory_maximum, Double critical_minimum, Double critical_maximum) {
			this.name = name;
			this.units = units;
			this.numeric = numeric;
			this.advisory_maximum = advisory_maximum;
			this.advisory_minimum = advisory_minimum;
			this.critical_maximum = critical_maximum;
			this.critical_minimum = critical_minimum;
		}
		
	}
	

		
	

	
	public void reflectState() {
		String[] advisories = new String[vitals.length];
		
		String time = timeFormat.format(new Date());
		boolean anyAdvisory = false;
		
		int countOutOfRange = 0;
		StringBuilder outOfRange = new StringBuilder();
		
		for(int i = 0; i < vitals.length; i++) {
			Number value = vitals[i].getValue();
			if(null == value) {
				anyAdvisory = true;
				advisories[i] = "- no source of " + vitals[i].getName() + "\r\n";
			} else if(vitals[i].getAdvisory_minimum() != null && value.doubleValue() <= vitals[i].getAdvisory_minimum()) {
				anyAdvisory = true;
				advisories[i] = "- low " + vitals[i].getName() + " " + value + " " + vitals[i].getUnits() + "\r\n";
				countOutOfRange++;
				outOfRange.append(advisories[i]);
			} else if(vitals[i].getAdvisory_maximum() != null && value.doubleValue() >= vitals[i].getAdvisory_maximum()) {
				anyAdvisory = true;
				advisories[i] = "- high " + vitals[i].getName() + " " + value + " " + vitals[i].getUnits() + "\r\n";
				countOutOfRange++;
				outOfRange.append(advisories[i]);
			}
		}
		
		
		// Pump stopping rules
		
		
		if(countOutOfRange >= 2) {
			stop("Stopped\r\n" + outOfRange.toString() + "at " + time + "\r\nnurse alerted");
		} else {
			for(Vital v : vitals) {
				Number value = v.getValue();
				if(null != value) {
					if(v.getCritical_minimum() != null && value.doubleValue() <= v.getCritical_minimum()) {
						stop("Stopped - " + v.getName() + " outside of critical range (" + v.getValue() + " " + v.getUnits() + ")\r\nat " + time + "\r\nnurse alerted");
						break;
					} else if(v.getCritical_maximum() != null && value.doubleValue() >= v.getCritical_maximum()) {
						stop("Stopped - " + v.getName() + " outside of critical range (" + v.getValue() + " " + v.getUnits() + ")\r\nat " + time + "\r\nnurse alerted");
						break;
					}
				}
			}
		}
//		if(null != rr && null != etco2 && != pulse && (rr.doubleValue() <= ADV_RR_MIN && rr.doubleValue()  && (etco2.doubleValue() <= ADV_ETCO2_MIN || etco2.doubleValue() >= ADV_ETCO2_MAX)) {
//			// Stop on contingent rule
//			stop("Stopped - Respiratory rate below limit ("+ rr+" bpm) and EtCO2 out of range ("+etco2+"mmHg)");
//		} 
		
		
		
//		else if(null != rr && rr.doubleValue() <= 6.0) {
//			stop("Stopped - Respiratory rate below critical limit ("+ rr+" bpm)");
//		} else if(null != etco2 && (etco2.doubleValue() <= 20.0 || etco2.doubleValue() >= 70)) {
//			stop("Stopped - EtCO2 outside critical range ("+ etco2+"mmHg)");
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
	private static final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	@Override
	public void update(Identifier identifier, Number n, MyDevice device) {
		boolean stateChanged = false;
		
		// TODO DO NOT INCLUDE THIS
		if(null != n && n.doubleValue() <= 5.0) {
			System.err.println("Ignoring " + identifier + " " + n + " " + device);
			return;
		}
		
		for(Vital v : vitals) {
			if(v.getNumeric().equals(identifier)) {
				if(!equal(v.getValue(), n)) {
					if(v.getLastSource() != null && v.getValue() != null && !v.getLastSource().equals(device.getSource()) && null == n) {
						
					} else {
						v.setValue(null == n ? null : n.doubleValue());
						v.setLastSource(device);
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
	public void deviceRemoved(MyDevice device) {
		boolean stateChanged = false;
		if(device != null) {
			for(Vital v : vitals) {
				if(device.equals(v.getLastSource())) {
					v.setValue(null);
					v.setLastSource(null);
					stateChanged = true;
				}
			}
		}
		if(stateChanged) {
			reflectState();
		}
	}

	@Override
	public void deviceAdded(MyDevice device) {
	}	
}
