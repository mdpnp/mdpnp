package org.mdpnp.apps.testapp;

import ice.DeviceIdentity;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.TimeZone;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.DefaultComboBoxModel;

import org.mdpnp.apps.testapp.Configuration.DeviceType;
import org.mdpnp.apps.testapp.xray.XRayVentPanel;
//import org.mdpnp.apps.testapp.xray.XRayVentPanel;
import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.EventLoopHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.subscription.Subscriber;

public class DemoApp {
	
	private static String goback = null;
	private static Runnable goBackAction = null;
	private static DemoPanel panel;
	private static String gobackPatient, gobackBed;
	private static Font gobackPatientFont;
	private static Color gobackPatientColor;
	private static int verticalAlignment, verticalTextAlignment;
	private static CardLayout ol;
	
	private static void setGoBack(String goback, Runnable goBackAction) {
		DemoApp.goback = goback;
		DemoApp.goBackAction = goBackAction;
		DemoApp.gobackPatient = panel.getPatientLabel().getText();
		DemoApp.gobackBed = panel.getBedLabel().getText();
		DemoApp.gobackPatientFont = panel.getPatientLabel().getFont();
		DemoApp.gobackPatientColor = panel.getPatientLabel().getForeground();
		DemoApp.verticalAlignment = panel.getPatientLabel().getVerticalAlignment();
		DemoApp.verticalTextAlignment = panel.getPatientLabel().getVerticalTextPosition();
		panel.getBack().setVisible(null != goback);
	}
	
	private static void goback() {
		if(null != goBackAction) {
			goBackAction.run();
			goBackAction = null;
		}
		panel.getPatientLabel().setFont(gobackPatientFont);
		panel.getPatientLabel().setForeground(gobackPatientColor);
		panel.getPatientLabel().setText(DemoApp.gobackPatient);
		panel.getBedLabel().setText(DemoApp.gobackBed);
		ol.show(panel.getContent(), DemoApp.goback);
		panel.getPatientLabel().setVerticalAlignment(DemoApp.verticalAlignment);
		panel.getPatientLabel().setVerticalTextPosition(DemoApp.verticalTextAlignment);
		panel.getBack().setVisible(false);
	}
	
	private static final Logger log = LoggerFactory.getLogger(DemoApp.class);
	
	public static final void start(final int domainId) throws Exception {
		UIManager.setLookAndFeel(new MDPnPLookAndFeel());


		final EventLoop eventLoop = new EventLoop();
		final EventLoopHandler handler = new EventLoopHandler(eventLoop);
		
//				Pointer logger = RTICLibrary.INSTANCE.NDDS_Config_Logger_get_instance();
//		RTICLibrary.INSTANCE.NDDS_Config_Logger_set_verbosity(logger, RTICLibrary.NDDS_CONFIG_LOG_VERBOSITY_STATUS_ALL);
//		RTICLibrary.INSTANCE.NDDS_Config_Logger_set_print_format(logger, RTICLibrary.NDDS_CONFIG_LOG_PRINT_FORMAT_MAXIMAL);
		
		// This could prove confusing
		TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
//		final Gateway gateway = new Gateway();
		final DomainParticipantQos pQos = new DomainParticipantQos(); 
		DomainParticipantFactory.get_instance().get_default_participant_qos(pQos);
		pQos.participant_name.name = "DemoApp ICE_Supervisor";
		final DomainParticipant participant = DomainParticipantFactory.get_instance().create_participant(domainId, pQos, null, StatusKind.STATUS_MASK_NONE);
		final Subscriber subscriber = participant.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
		final DeviceListModel nc = new DeviceListModel(subscriber, eventLoop);
		
		
		final DemoFrame frame = new DemoFrame("ICE Supervisor");
		frame.setIconImage(ImageIO.read(DemoApp.class.getResource("icon.png")));
		panel = new DemoPanel();
		switch(domainId) {
		case 3:
		    panel.getBedLabel().setText("Operating Room "+domainId);
		    break;
		default:
		    panel.getBedLabel().setText("Intensive Care " + domainId);
		    break;
		}
		
		String version = BuildInfo.getVersion();
		
		if(null == version) {
		    panel.getVersion().setText("Development Version");
		} else {
		    panel.getVersion().setText("v"+version+" built:"+BuildInfo.getDate()+" "+BuildInfo.getTime());
		}
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(goBackAction != null) {
					goBackAction.run();
					goBackAction = null;
				}
				nc.tearDown();
				try {
                    handler.shutdown();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
				super.windowClosing(e);
			}
		});
		
		frame.getContentPane().add(panel);
		ol = new CardLayout();
		panel.getContent().setLayout(ol);
		
//		LoginPanel loginPanel = new LoginPanel();
//		
//		panel.getContent().add(loginPanel, "login");
		
		
		final MainMenuPanel mainMenuPanel = new MainMenuPanel();
		panel.getContent().add(mainMenuPanel, "main");
		ol.show(panel.getContent(), "main");
				
		final DevicePanel devicePanel = new DevicePanel();
		panel.getContent().add(devicePanel, "devicepanel");
		
		String s = System.getProperty("NOPCA");
		PCAPanel _pcaPanel = null;
		if(null == s || !"true".equals(s)) {
		    _pcaPanel = new PCAPanel(nc, subscriber, eventLoop);
		    panel.getContent().add(_pcaPanel, "pca");
		}
		final PCAPanel pcaPanel = _pcaPanel;
		
		s = System.getProperty("NOXRAYVENT");
		XRayVentPanel _xrayVentPanel = null;
		if(null == s || !"true".equals(s)) {
		    _xrayVentPanel = new XRayVentPanel(panel, nc, subscriber, eventLoop);
		    panel.getContent().add(_xrayVentPanel, "xray");
		}
		final XRayVentPanel xrayVentPanel = _xrayVentPanel;
		
		
		panel.getBack().setVisible(false);
		
		panel.getBack().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				goback();
			}
			
		});
		
		mainMenuPanel.getSpawnDeviceAdapter().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ConfigurationDialog dia = new ConfigurationDialog();
                dia.getApplications().setModel(new DefaultComboBoxModel(new Configuration.Application[] {Configuration.Application.ICE_Device_Interface}));
                dia.set(Configuration.Application.ICE_Device_Interface, Configuration.DeviceType.PO_Simulator);
                dia.remove(dia.getDomainId());
                dia.remove(dia.getDomainIdLabel());
                dia.remove(dia.getWelcomeScroll());
                dia.getQuit().setText("Close");
                dia.pack();
                dia.setLocationRelativeTo(panel);
                final Configuration c = dia.showDialog();
                if(null != c) {
                    Thread t = new Thread(new Runnable() {
                        public void run() {
                            try {
                                DeviceAdapter da = new DeviceAdapter();
                                da.start(c.getDeviceType(), domainId, c.getAddress(), true, false);
                                log.info("DeviceAdapter ended");
                            } catch (Exception e) {
                                log.error("Error in spawned DeviceAdapter", e);
                            }
                        }
                    });
                    t.setDaemon(true);
                    t.start();
                }
            }
		    
		});
		
		
		mainMenuPanel.getDeviceList().setModel(nc);
		
		mainMenuPanel.getAppList().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int idx = mainMenuPanel.getAppList().locationToIndex(e.getPoint());
				if(idx >= 0) {
					Object o = mainMenuPanel.getAppList().getModel().getElementAt(idx);
					/*if("Data Fusion".equals(o) && null != roomSyncPanel) {
						setGoBack("main", null);
						ol.show(panel.getContent(), "roomsync");
					} else*/ if("Infusion Safety".equals(o) && null != pcaPanel) {
						setGoBack("main", new Runnable() {
						    public void run() {
						        // TODO these apps need a lifecycle defined
						        pcaPanel.setActive(false);
						    }
						});
						pcaPanel.setActive(true);
						ol.show(panel.getContent(), "pca");
					} else if("X-Ray Ventilator Sync".equals(o) && null != xrayVentPanel) {
						setGoBack("main", new Runnable() {
							public void run() {
							    xrayVentPanel.stop();
							}
						});
						ol.show(panel.getContent(), "xray");
						xrayVentPanel.start();
					}
				}
				super.mouseClicked(e);
			}
		});
		mainMenuPanel.getDeviceList().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int idx  = mainMenuPanel.getDeviceList().locationToIndex(e.getPoint());
				if(idx>=0) {
				    Device device = (Device) mainMenuPanel.getDeviceList().getModel().getElementAt(idx);
				    devicePanel.setModel(subscriber, device.getDeviceIdentity(), eventLoop);
					setGoBack("main", new Runnable() {
					    public void run() {
					        
					        devicePanel.setModel(null, null, null);
					    }
					});
					ol.show(panel.getContent(), "devicepanel");
				}
				super.mouseClicked(e);
			}
		});
		
//		mainMenuPanel.getDeviceList().setModel(nc.getAcceptedDevices());
		
//		loginPanel.getClinicianId().addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				setGoBack("login", null);
//				ol.show(panel.getContent(), "main");
//			}
//			
//		});
		
		
		
		DemoPanel.setChildrenOpaque(panel, false);
		
		
		panel.setOpaque(true);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(640,480);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
//		nc.solicit();
	}
}
