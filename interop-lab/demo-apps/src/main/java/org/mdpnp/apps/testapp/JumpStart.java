package org.mdpnp.apps.testapp;

import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.jfree.util.Log;
import org.mdpnp.rti.dds.DDS;
import org.mdpnp.transport.WrapperFactory;
import org.slf4j.LoggerFactory;

public class JumpStart {
	private enum Application {
	    DemoApp,
		DeviceAdapter;
	}
	
	private static class Configuration {
	    private final Application application;
	    private final WrapperFactory.WrapperType transport;
	    private final String transportSettings;
//	    private final DeviceAdapter.Type deviceType;
	    
	    public Configuration(Application application, WrapperFactory.WrapperType transport, String transportSettings/*, DeviceAdapter.Type deviceType*/) {
	        this.application = application;
	        this.transport = transport;
	        this.transportSettings = transportSettings;
//	        this.deviceType = deviceType;
        }
	    public Application getApplication() {
            return application;
        }
	    public WrapperFactory.WrapperType getTransport() {
            return transport;
        }
	    public String getTransportSettings() {
            return transportSettings;
        }
//	    public DeviceAdapter.Type getDeviceType() {
//            return deviceType;
//        }
	    public void write(OutputStream os) throws IOException {
	        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "ASCII"));
	        bw.write("application");
	        bw.write("\t");
	        bw.write(application.name());
	        bw.write("\n");
	        
	        bw.write("transport");
	        bw.write("\t");
	        bw.write(transport.name());
	        bw.write("\n");
	        
	        if(null != transportSettings) {
    	        bw.write("transportSettings");
    	        bw.write("\t");
    	        bw.write(transportSettings);
    	        bw.write("\n");
	        }
	        
//	        bw.write("deviceType");
//	        bw.write("\t");
//	        bw.write(deviceType.name());
//	        bw.write("\n");
	        
	        bw.flush();
	    }
	    public static Configuration read(InputStream is) throws IOException {
	        BufferedReader br = new BufferedReader(new InputStreamReader(is, "ASCII"));
	        
	        String line = null;
	        
	        Application app = null;
	        WrapperFactory.WrapperType transport = null;
	        String transportSettings = null;
	        DeviceAdapter.Type deviceType = null;
	        
	        while(null != (line = br.readLine())) {
	            String[] v = line.split("\t");
	            if("application".equals(v[0])) {
	                app = Application.valueOf(v[1]);
	            } else if("transport".equals(v[0])) {
	                transport = WrapperFactory.WrapperType.valueOf(v[1]);
	            } else if("transportSettings".equals(v[0])) {
	                transportSettings = v[1];
	            } 
//	            else if("deviceType".equals(v[0])) {
//	                deviceType = DeviceAdapter.Type.valueOf(v[1]);
//	            }
	        }
	        
	        return new Configuration(app, transport, transportSettings/*, deviceType*/);
	    }
	}
	
	private static class ConfigurationDialog extends JDialog {
	    private final JComboBox applications = new JComboBox(Application.values());
	    private final JComboBox transports = new JComboBox(WrapperFactory.WrapperType.values());
	    private final JButton start = new JButton("Start");
	    private final JButton quit = new JButton("Quit");
	    private final JTextField transportSettings = new JTextField("0", 2);
	    private boolean quitPressed = true;
//	    private final JComboBox deviceType = new JComboBox(DeviceAdapter.Type.values());
//	    private final JLabel deviceTypeLabel = new JLabel("Device Type:");
	    
	    protected void setApplication(Application app) {
	        switch(app) {
//	        case DeviceAdapter:
//	            deviceType.setVisible(true);
//	            deviceTypeLabel.setVisible(true);
//	            start.setText("Start " + deviceType.getSelectedItem());
//	            break;
	        default:
//	            deviceType.setVisible(false);
//	            deviceTypeLabel.setVisible(false);
	            start.setText("Start " + app);
	            break;
	        }
	        pack();
	    }
	    
	    protected void setTransport(WrapperFactory.WrapperType transport) {
	        switch(transport) {
	        case RTI_DDS:
	            transportSettings.setVisible(true);
	            break;
	        default:
	            transportSettings.setVisible(false);
	        }
	        pack();
	    }
	    
//	    protected void setDeviceType(DeviceAdapter.Type deviceType) {
//	        switch((Application)applications.getSelectedItem()) {
//	        case DeviceAdapter:
//	            start.setText("Start " +deviceType);
//	            pack();
//	            break;
//	        }
//	        
//	    }
	    
	    
	    public ConfigurationDialog(Configuration conf) {
	        super( (JDialog)null, true);
	        
	        if(null != conf) {
	            if(null != conf.getApplication()) {
	                applications.setSelectedItem(conf.getApplication());
	            }
//	            if(null != conf.getDeviceType()) {
//	                deviceType.setSelectedItem(conf.getDeviceType());
//	            } 
	            if(null != conf.getTransport()) {
	                transports.setSelectedItem(conf.getTransport());
	            }
	            if(null != conf.getTransportSettings()) {
	                transportSettings.setText(conf.getTransportSettings());
	            }
	        }
	        
	        
            setTitle("MD PnP JumpStart");
            setLocationRelativeTo(null);
            setLayout(new GridBagLayout());
            
            transportSettings.setHorizontalAlignment(SwingConstants.RIGHT);
            
            GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,1.0,1.0,GridBagConstraints.BASELINE, GridBagConstraints.BOTH, new Insets(2,0,2,0), 2, 2);
            
            getContentPane().add(new JLabel("Application:"), gbc);
            gbc.gridx++;
            getContentPane().add(applications, gbc);
            
            
//            gbc.gridy = 1;
//            gbc.gridx = 0;
//            getContentPane().add(deviceTypeLabel, gbc);
//            gbc.gridx++;
//            getContentPane().add(deviceType, gbc);
            
            gbc.gridy = 1;
            gbc.gridx = 0;
            getContentPane().add(new JLabel("Transport:"), gbc);
            gbc.gridx++;
            getContentPane().add(transports, gbc);
            gbc.gridx++;
            getContentPane().add(transportSettings, gbc);

            
            gbc.gridy = 2;
            gbc.gridx = 0;
            getContentPane().add(quit, gbc);
            gbc.gridx++;
            gbc.gridwidth = 2;
            getContentPane().add(start, gbc);

            getRootPane().setDefaultButton(start);
            start.requestFocus(false);
            
            quit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ConfigurationDialog.this.setVisible(false);
                }
            });
            
            start.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    quitPressed = false;
                    ConfigurationDialog.this.setVisible(false);
                }
            });
            
            applications.addItemListener(new ItemListener() {

                @Override
                public void itemStateChanged(ItemEvent e) {
                    if(e.getStateChange()==ItemEvent.SELECTED) {
                        setApplication((Application)e.getItem());
                    }
                }
                
            });
            
            transports.addItemListener(new ItemListener() {
               @Override
                public void itemStateChanged(ItemEvent e) {
                   if(e.getStateChange()==ItemEvent.SELECTED) {
                       setTransport((WrapperFactory.WrapperType)e.getItem());
                   }
                } 
            });
            
//            deviceType.addItemListener(new ItemListener() {
//               @Override
//                public void itemStateChanged(ItemEvent e) {
//                   if(e.getStateChange()==ItemEvent.SELECTED) {
//                       setDeviceType((DeviceAdapter.Type)e.getItem());
//                   }
//                } 
//            });
            setApplication((Application)applications.getSelectedItem());
            setTransport((WrapperFactory.WrapperType)transports.getSelectedItem());
//            setDeviceType((DeviceAdapter.Type)deviceType.getSelectedItem());
        }
	    public Configuration showDialog() {
	        pack();
	        setVisible(true);
	        dispose();
	        return quitPressed ? null : new Configuration((Application)applications.getSelectedItem(), (WrapperFactory.WrapperType)transports.getSelectedItem(), transportSettings.getText()/*, (org.mdpnp.apps.testapp.DeviceAdapter.Type) deviceType.getSelectedItem()*/);
	    }
	}
	
	
	
	public static void main(String[] args) throws Exception {
//		System.err.println(LoggerFactory.getILoggerFactory().toString());
	    System.setProperty("java.net.preferIPv4Stack","true");
//		Pointer logger = RTICLibrary.INSTANCE.NDDS_Config_Logger_get_instance();
//		RTICLibrary.INSTANCE.NDDS_Config_Logger_set_verbosity(logger, RTICLibrary.NDDS_CONFIG_LOG_VERBOSITY_STATUS_ALL);
//		RTICLibrary.INSTANCE.NDDS_Config_Logger_set_print_format(logger, RTICLibrary.NDDS_CONFIG_LOG_PRINT_FORMAT_MAXIMAL);
		
		
//		new Thread(new Runnable() {
//			public void run() {
//				while(true) {
//					Thread[] t = new Thread[Thread.currentThread().getThreadGroup().activeCount()*2];
//					int n = Thread.currentThread().getThreadGroup().enumerate(t, true);
//					try {
//						Thread.sleep(2000L);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}).start();
//		if(!DDS.init()) {
//			throw new RuntimeException();
//
//		}
	    Configuration conf = null;
	    File jumpStartSettings = new File(".JumpStartSettings");
	    if(jumpStartSettings.exists() && jumpStartSettings.canRead()) {
	        FileInputStream fis = new FileInputStream(jumpStartSettings);
	        conf = Configuration.read(fis);
	        fis.close();
	    }
	    
		if(args.length > 0) {
		    Application app = Application.valueOf(args[0]);
		    int consumeArgs = 1;
		    try {
		        WrapperFactory.WrapperType transport = WrapperFactory.WrapperType.valueOf(args[1]);
		        conf = new Configuration(app, transport, null/*, null*/);
		        consumeArgs++;
		    } catch (Throwable t) {
		        conf = new Configuration(app, WrapperFactory.getType(), null/*, null*/);
		    }
		    args = Arrays.copyOfRange(args, consumeArgs, args.length);
		} else if(!GraphicsEnvironment.isHeadless()) {
		    ConfigurationDialog d = new ConfigurationDialog(conf);
		    conf = d.showDialog();
		} else {
		    // fall through to allow configuration via a file
		}
		if(null != conf) {
		    if(!jumpStartSettings.exists()) {
		        jumpStartSettings.createNewFile();
		    }
		    
		    
		    if(jumpStartSettings.canWrite()) {
		        FileOutputStream fos = new FileOutputStream(jumpStartSettings);
		        conf.write(fos);
		        fos.close();
		    }
		    
		    // This will tell the DeviceAdapter to run headless
//		    if(Application.DeviceAdapter.equals(conf.getApplication()) && null != conf.getDeviceType()) {
//		        String[] _args = new String[args.length+1];
//                System.arraycopy(args, 0, _args, 1, args.length);
//                _args[0] = conf.getDeviceType().name();
//                args = _args;
//            }
		    
		    
		    if(conf.getTransportSettings() != null) {
		        String[] _args = new String[args.length+1];
		        System.arraycopy(args, 0, _args, 1, args.length);
		        _args[0] = conf.getTransportSettings();
		        args = _args;
		    }
		    
		    
		    
		    WrapperFactory.setType(conf.getTransport());
		    switch(conf.getTransport()) {
		    case RTI_DDS:
		        if(!DDS.init()) {
		            Log.warn("Unable to initialize RTI DDS, falling back to JGroups transport");
		            WrapperFactory.setType(WrapperFactory.WrapperType.JGROUPS);
		        }
		        break;
		        
		    }
			switch(conf.getApplication()) {
//			case NetworkController:
//				NetworkController.main(args);
//				break;
			case DeviceAdapter:
				if(GraphicsEnvironment.isHeadless()) {
					VersionedDeviceAdapter.main(args);
				} else {
					DeviceAdapter.main(args);
				}
				break;
			case DemoApp:
			    DemoApp.main(args);
			    break;
			}
		} 
		
	}
}
