package org.mdpnp.apps.testapp;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.mdpnp.apps.testapp.Configuration.DeviceType;
import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.EventLoopHandler;
import org.mdpnp.devices.connected.GetConnected;
import org.mdpnp.devices.connected.GetConnectedToFixedAddress;
import org.mdpnp.devices.cpc.bernoulli.DemoBernoulli;
import org.mdpnp.devices.draeger.medibus.DemoApollo;
import org.mdpnp.devices.draeger.medibus.DemoEvitaXL;
import org.mdpnp.devices.hospira.symbiq.DemoSymbiq;
import org.mdpnp.devices.masimo.radical.DemoRadical7;
import org.mdpnp.devices.nellcor.pulseox.DemoN595;
import org.mdpnp.devices.nonin.pulseox.DemoPulseOx;
import org.mdpnp.devices.oridion.capnostream.DemoCapnostream20;
import org.mdpnp.devices.philips.intellivue.DemoMP70;
import org.mdpnp.devices.serial.SerialProviderFactory;
import org.mdpnp.devices.serial.TCPSerialProvider;
import org.mdpnp.devices.simulation.DemoSimulatedBloodPressure;
import org.mdpnp.devices.simulation.co2.SimCapnometer;
import org.mdpnp.devices.simulation.ecg.SimElectroCardioGram;
import org.mdpnp.devices.simulation.pulseox.SimPulseOximeter;
import org.mdpnp.guis.swing.CompositeDevicePanel;
import org.mdpnp.guis.swing.DeviceMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceAdapter {
	
	
	private JFrame frame;
	private GetConnected getConnected;
	private AbstractDevice device;
	private EventLoopHandler handler;
	
	public JFrame getFrame() {
        return frame;
    }
	public GetConnected getGetConnected() {
        return getConnected;
    }
	
//	private static Collection<org.mdpnp.guis.swing.DevicePanel> panels;
	
	public static final AbstractDevice buildDevice(DeviceType type, int domainId, EventLoop eventLoop) throws NoSuchFieldException, SecurityException, IOException {
		switch(type) {
		case Nonin:
			return new DemoPulseOx(domainId, eventLoop);
		case NellcorN595:
			return new DemoN595(domainId, eventLoop);
		case MasimoRadical7:
			return new DemoRadical7(domainId, eventLoop);
		case PO_Simulator:
			return new SimPulseOximeter(domainId, eventLoop);
		case NBP_Simulator:
			return new DemoSimulatedBloodPressure(domainId, eventLoop);
		case PhilipsMP70:
			return new DemoMP70(domainId, eventLoop);
		case DragerApollo:
			return new DemoApollo(domainId, eventLoop);
		case DragerEvitaXL:
			return new DemoEvitaXL(domainId, eventLoop);
		case Bernoulli:
			return new DemoBernoulli(domainId, eventLoop);
		case Capnostream20:
			return new DemoCapnostream20(domainId, eventLoop);
		case Symbiq:
			return new DemoSymbiq(domainId, eventLoop);
		case ECG_Simulator:
		    return new SimElectroCardioGram(domainId, eventLoop);
		case CO2_Simulator:
		    return new SimCapnometer(domainId, eventLoop);
		default:
			throw new RuntimeException("Unknown type:"+type);
		}
	}
	
	private synchronized void killAdapter() {
//		if(adapter != null) {
//			adapter.depart();
//			adapter.tearDown();
//			adapter = null;
//		}
		if(getConnected!=null) {
			getConnected.disconnect();
			getConnected.shutdown();
			getConnected = null;
		}
		if(device!=null) {
		    device.shutdown();
		    device = null;
		}
		if(handler!= null) {
		    try {
                handler.shutdown();
                handler = null;
            } catch (InterruptedException e) {
                log.error("Interrupted in handler.shutdown", e);
            }
		}
	}
	
	private static final Logger log = LoggerFactory.getLogger(DeviceAdapter.class);
	
	public void start(DeviceType type, int domainId, final String address, boolean gui) throws Exception {
	    start(type, domainId, address, gui, true);
	}
	
	public void start(DeviceType type, int domainId, final String address, boolean gui, boolean exit) throws Exception {
		if(null!=address && address.contains(":")) {
            SerialProviderFactory.setDefaultProvider(new TCPSerialProvider());
            log.info("Using the TCPSerialProvider, be sure you provided a host:port target");
        }
		final EventLoop eventLoop = new EventLoop();

		handler = new EventLoopHandler(eventLoop);

		device = buildDevice(type, domainId, eventLoop);
		
		if(gui) {
		 // find the appropriate GUI representations for this device
		    final CompositeDevicePanel cdp = new CompositeDevicePanel();
		    final DeviceMonitor deviceMonitor = new DeviceMonitor(device.getParticipant(), device.getDeviceIdentity().universal_device_identifier, cdp, eventLoop);
//            panels = DevicePanelFactory.findPanel(
//            panels = new ArrayList<DevicePane>
            
		    frame = new JFrame("ICE Device Adapter - "+type);
		    frame.setIconImage(ImageIO.read(DeviceAdapter.class.getResource("icon.png")));
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    new Thread(new Runnable() {
                        public void run() {
                            deviceMonitor.shutdown();
                            cdp.reset();
                            killAdapter();
                            
                        }
                    }).start();
                    super.windowClosing(e);
                }
            });
            frame.setDefaultCloseOperation(exit?JFrame.EXIT_ON_CLOSE:JFrame.DISPOSE_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setSize(640, 480);
            frame.getContentPane().setLayout(new BorderLayout());
            JTextArea descriptionText = new JTextArea();
            descriptionText.setEditable(false);
            descriptionText.setLineWrap(true);
            descriptionText.setWrapStyleWord(true);
            InputStream is = ConfigurationDialog.class.getResourceAsStream("device-adapter");
            if(null != is) {
                try {
                
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    StringBuffer sb = new StringBuffer();
                    String line = null;
                    while(null != (line = br.readLine())) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    descriptionText.setText(sb.toString().replaceAll("\\%\\%DEVICE\\_TYPE\\%\\%", type.toString()));
                } catch (IOException e) {
                    log.error("Error getting window text", e);
                }
            }
            
            frame.getContentPane().add(new JScrollPane(descriptionText), BorderLayout.NORTH);
            frame.getContentPane().add(cdp, BorderLayout.CENTER);
//            frame.getContentPane().setLayout(new GridLayout(panels.size(), 1));
//            for(org.mdpnp.guis.swing.DevicePanel panel : panels) {
//                frame.getContentPane().add(panel);
//            }
//            frame.getContentPane().setLayout(new BorderLayout());
//            frame.getContentPane().add(new CompositeDevicePanel(device.getParticipant(), device.getDeviceIdentity().universal_device_identifier), BorderLayout.CENTER);
            
            frame.getContentPane().validate();
            frame.setVisible(true);
		}
			

		if(null == address) {
		    getConnected = new GetConnected(frame, domainId, device.getDeviceIdentity().universal_device_identifier, eventLoop);
		} else {
		    getConnected = new GetConnectedToFixedAddress(frame, domainId, device.getDeviceIdentity().universal_device_identifier, address, eventLoop);
		}
		
		if(gui) {
			getConnected.connect();
		
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					killAdapter();
				}
			});
		
			
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				public void run() {
					killAdapter();
				}
			}));
//			MutableIdentifierArrayUpdate miau = new MutableIdentifierArrayUpdateImpl(Device.REQUEST_IDENTIFIED_UPDATES);
//	        miau.setValue(new Identifier[] {Device.NAME, Device.GUID, Device.ICON});
//	        deviceGateway.update(bootstrapListener, miau);
//	        
//			MutableTextUpdate tu = new MutableTextUpdateImpl(Device.REQUEST_AVAILABLE_IDENTIFIERS);
//			tu.setTarget(adapter.getSource());
//			// This request will drive GUI creation
//			deviceGateway.update(bootstrapListener, tu);
			
		} else {
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				public void run() {
					killAdapter();
					synchronized(DeviceAdapter.class) {
					    interrupted = true;
					    DeviceAdapter.class.notifyAll();
					}
					        
				}
			}));
//			System.err.println("Type quit<enter> to exit");

			getConnected.connect();

//			MutableIdentifierArrayUpdate miau = new MutableIdentifierArrayUpdateImpl(Device.REQUEST_IDENTIFIED_UPDATES);
//	        miau.setValue(new Identifier[] {Device.NAME, Device.GUID, Device.ICON});
//	        deviceGateway.update(miau);
			
//			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//			String line = null;
//			while(!"quit".equals(line)) {
//				line = br.readLine();
//			}
	        synchronized(this) {
	            while(!interrupted) {
	                wait();
	            }
	        }
			int n = Thread.activeCount() + 10;
			Thread[] threads = new Thread[n];
			n = Thread.enumerate(threads);
			for(int i = 0; i < n; i++) {
			    if(threads[i].isAlive() && !threads[i].isDaemon() && !Thread.currentThread().equals(threads[i])) {
			        log.warn("Non-Daemon thread would block exit: "+threads[i].getName());
			    }
			}
			if(exit) {
			    System.exit(0);
			}
		}
	}
	private boolean interrupted = false;
}
