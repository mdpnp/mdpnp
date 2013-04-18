package org.mdpnp.devices.draeger.medibus;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.mdpnp.apps.testapp.DemoFrame;
import org.mdpnp.apps.testapp.DemoPanel;
import org.mdpnp.devices.draeger.medibus.RTMedibus;
import org.mdpnp.devices.draeger.medibus.types.Command;
import org.mdpnp.devices.draeger.medibus.types.RealtimeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import purejavacomm.CommPortIdentifier;
import purejavacomm.NoSuchPortException;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

public class MedibusTestGUI extends DemoPanel {
	
	private enum Device {
		Apollo,
		EvitaXL,
		
	}
	

	
	private final JComboBox list = new JComboBox(Device.values());
	private final JTextField port = new JTextField("/dev/ttyO2");
	private final JButton connect = new JButton("Connect");
	private final JButton disconnect = new JButton("Disconnect");
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private final JLabel time = new JLabel("  ");
	private final JLabel deviceId = new JLabel("  ");
	
	private final JPanel settings = new JPanel(new BorderLayout()), texts = new JPanel(new BorderLayout()), alarms = new JPanel(new BorderLayout()), measuredDatas = new JPanel(new BorderLayout()), rtDatas = new JPanel(new BorderLayout());
	private final DataTableModel settingsModel = new DataTableModel(), textsModel = new DataTableModel(), alarmsTableModel = new DataTableModel(), measuredDatasModel = new DataTableModel(), rtDatasModel = new DataTableModel();
	private final JTable settingsTable = new JTable(settingsModel), textsTable = new JTable(textsModel), alarmsTable = new JTable(alarmsTableModel), measuredDatasTable = new JTable(measuredDatasModel), rtDatasTable = new JTable(rtDatasModel);
	private final JButton settingsButton = new JButton("Request Settings"), textsButton = new JButton("Request Text Messages"), alarmsButton = new JButton("Request Alarms"), measuredDatasButton = new JButton("Request Measured Data"), rtDatasButton = new JButton("Request R/T Data");
	private static class DataTableModel extends AbstractTableModel implements TableModel {
		
		private final List<Row> rows = new ArrayList<Row>();
		
		private static class Row {
			String name, value;
		}
		
		
		public void update(String name, String value) {
			for(int i = 0; i < rows.size(); i++) {
				Row r = rows.get(i);
				if(r.name.equals(name)) {
					r.value = value;
					fireTableRowsUpdated(i, i);
					return;
				}
			}
			Row r = new Row();
			r.name = name;
			r.value = value;
			rows.add(r);
			fireTableRowsInserted(rows.size()-1, rows.size()-1);
		}
		
		@Override
		public int getRowCount() {
			return rows.size();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public String getColumnName(int columnIndex) {
			switch(columnIndex) {
			case 0:
				return "Name";
			case 1:
				return "Value";
			default:
				return null;
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch(columnIndex) {
			case 0:
				return String.class;
			case 1:
				return String.class;
			default:
				return null;
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Row r = rows.get(rowIndex);
			switch(columnIndex) {
			case 0:
				return r.name;
			case 1:
				return r.value;
			default:
				return null;
			}
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			
		}
		
	}
	
	
	private RTMedibus medibus;
	private Thread slowThread, fastThread;
	private SerialPort serialPort;
	
	private void destroyMyMedibus() throws IOException, InterruptedException {
		if(null != medibus) {
			medibus.sendCommand(Command.StopComm);
		}
		if(slowThread != null) {
			slowThread.join();
			
		}
		if(null != serialPort) {
			serialPort.close();
		}
		medibus = null;
		slowThread = null;
		serialPort = null;
	}
	
	private final Logger log = LoggerFactory.getLogger(MedibusTestGUI.class);
	private void buildMyMedibus() throws IOException, InterruptedException, NoSuchPortException, PortInUseException, UnsupportedCommOperationException {
		destroyMyMedibus();
		
		CommPortIdentifier portid = CommPortIdentifier.getPortIdentifier(port.getText());
		
		serialPort = (SerialPort) portid.open("ICE", 10000);
		switch((Device)list.getSelectedItem()) {
		case Apollo:
			serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_EVEN);
			break;
		case EvitaXL:
			serialPort.setSerialPortParams(19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			break;
		}

		medibus = new RTMedibus(serialPort.getInputStream(), serialPort.getOutputStream()) {
			@Override
			protected void receiveDeviceSetting(Data[] data, int n) {
				for(int i = 0; i < n; i++) {
					Data d = data[i];
					settingsModel.update(d.code.toString(), d.data);
				}
			}
			@Override
			protected void receiveTextMessage(Data[] data, int n) {
				for(int i = 0; i < n; i++) {
					Data d = data[i];
					textsModel.update(d.code.toString(), d.data);
				}
			}
			@Override
			protected void receiveMeasuredData(Data[] data, int n) {
				for(int i = 0; i < n; i++) {
					Data d = data[i];
					measuredDatasModel.update(d.code.toString(), d.data);
				}
			}
			@Override
			public void receiveDataValue(RTMedibus.RTDataConfig config, int multiplier, int streamIndex, Object realtimeData, int data) {
				rtDatasModel.update(realtimeData.toString(), Integer.toString(data));
			}
			@Override
			protected void receiveDateTime(Date date) {
				time.setText(date.toString());
			}
			@Override
			protected void receiveDeviceIdentification(String idNumber,
					String name, String revision) {
				deviceId.setText(idNumber + " " + name + " " + revision);
			}
			@Override
			protected void receiveAlarms(Alarm[] alarms) {
				for(Alarm a : alarms) {
					alarmsTableModel.update(a.alarmCode.toString(), a.alarmPhrase);
				}
			}
		};
		
		slowThread = new Thread(new Runnable() {
			public void run() {
				boolean keepGoing = true;
				while(keepGoing) {
					try {
						keepGoing = medibus.receive();
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				log.info("Medibus slow processing completed");
			}
		});
		slowThread.setDaemon(true);
		slowThread.start();
		
		fastThread = new Thread(new Runnable() {
			public void run() {
				boolean keepGoing = true;
				while(keepGoing) {
					try {
						keepGoing = medibus.receiveFast();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				log.info("Medibus fast processing completed");
			}
		});
		fastThread.setDaemon(true);
		fastThread.start();
	}
	
	public MedibusTestGUI() {
		setLayout(new BorderLayout());
//		setBackground(Color.black);
//		setForeground(Color.green);
		
		JPanel header = new JPanel(new GridLayout(1, 3));
		header.add(new JLabel("MD PnP"));
		header.add(new JLabel("Integerated Clinical Environment"));
		
		
		
		JPanel buttons = new JPanel();
		
		buttons.add(new JLabel("Device:"));
		buttons.add(list);
		buttons.add(new JLabel("Port:"));
		buttons.add(port);
		buttons.add(connect);
		buttons.add(disconnect);
		
		JPanel headerAndButtons = new JPanel(new GridLayout(2, 1));
		headerAndButtons.add(header);
		headerAndButtons.add(buttons);
		add(headerAndButtons, BorderLayout.NORTH);
		
		JTabbedPane jtp = new JTabbedPane();
		add(jtp, BorderLayout.CENTER);
		
		settingsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				executor.execute(new Runnable() {
					public void run() {
						try {
							medibus.sendCommand(Command.ReqDeviceSetting);
							medibus.sendCommand(Command.ReqDateTime);
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
				});
				
			}
			
		});
		
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(settingsButton, BorderLayout.NORTH);
		p.add(new JScrollPane(settingsTable), BorderLayout.CENTER);
		settings.add(p);
		
		textsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				executor.execute(new Runnable() {

					@Override
					public void run() {
						try {
							medibus.sendCommand(Command.ReqTextMessages);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
				});
			}
		});
		
		p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(textsButton, BorderLayout.NORTH);
		p.add(new JScrollPane(textsTable), BorderLayout.CENTER);
		texts.add(p);
		
		alarmsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				executor.execute(new Runnable() {

					@Override
					public void run() {
						try {
							medibus.sendCommand(Command.ReqAlarmsCP1);
							medibus.sendCommand(Command.ReqAlarmsCP2);
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
					
				});
			}
			
		});
		
		p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(alarmsButton, BorderLayout.NORTH);
		p.add(new JScrollPane(alarmsTable), BorderLayout.CENTER);
		alarms.add(p);
		
		measuredDatasButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				executor.execute(new Runnable() {

					@Override
					public void run() {
						try {
							medibus.sendCommand(Command.ReqMeasuredDataCP1);
							medibus.sendCommand(Command.ReqMeasuredDataCP2);
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
					
				});
			}
			
		});
		
		p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(measuredDatasButton, BorderLayout.NORTH);
		p.add(new JScrollPane(measuredDatasTable), BorderLayout.CENTER);
		measuredDatas.add(p);
		
		rtDatasButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				executor.execute(new Runnable() {

					@Override
					public void run() {
						try {
							medibus.enableRealtime(5000L, RealtimeData.O2InspExp, RealtimeData.FlowInspExp, RealtimeData.AirwayPressure, RealtimeData.ExpiratoryCO2mmHg);
//							medibus.sendCommand(Command.ReqRealtimeConfig);
//							medibus.sendRTTransmissionCommand(new RTMedibus.RTTransmit[] {
//									new RTMedibus.RTTransmit(0, 1),
//									new RTMedibus.RTTransmit(1, 1),
//									new RTMedibus.RTTransmit(5, 1),
//									new RTMedibus.RTTransmit(6, 1),
//									new RTMedibus.RTTransmit(7, 1),
//									new RTMedibus.RTTransmit(8, 1),
//							}, 1000L);
//							medibus.sendEnableRealtime(new int[] {0,1,2,3,4,5});
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						
						
					}
					
				});
			}
			
		});
		
		p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(rtDatasButton, BorderLayout.NORTH);
		p.add(new JScrollPane(rtDatasTable), BorderLayout.CENTER);
		rtDatas.add(p);

		add(time, BorderLayout.SOUTH);
		
		jtp.addTab("Settings", settings);
		jtp.addTab("Text Messages", texts);
		jtp.addTab("Alarms", alarms);
		jtp.addTab("Measured Data", measuredDatas);
		jtp.addTab("R/T Data",  rtDatas);
		
		connect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				executor.execute(new Runnable() {
					public void run() {
						try {
							buildMyMedibus();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
			
		});
		
		disconnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				executor.execute(new Runnable() {
					public void run() {
						try {
							destroyMyMedibus();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
//		setOpaque(true);
//		setChildrenOpaque(this, false);
	}
	
	
	
	public static void main(final String[] args) throws InvocationTargetException, InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

		final DemoFrame frame = new DemoFrame(MedibusTestGUI.class.getName());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final MedibusTestGUI mtg = new MedibusTestGUI();
		
		
		if(args.length > 0) {
			mtg.port.setText(args[0]);
		}
		frame.getContentPane().add(mtg);
		setChildrenOpaque(frame.getContentPane(), false);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				try {
					mtg.destroyMyMedibus();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		frame.setSize(800, 600);
		frame.setVisible(true);
		
		
	}


}
