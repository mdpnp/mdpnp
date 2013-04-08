package org.mdpnp.apps.testapp;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.mdpnp.comms.Gateway;
import org.mdpnp.comms.GatewayListener;
import org.mdpnp.comms.IdentifiableUpdate;
import org.mdpnp.comms.data.text.TextUpdate;
import org.mdpnp.comms.nomenclature.BloodPressure;
import org.mdpnp.comms.nomenclature.Device;
import org.mdpnp.comms.nomenclature.PulseOximeter;
import org.mdpnp.devices.simulation.SimulatedDevice;

@SuppressWarnings("serial")
public class EngineerConsole extends JPanel implements GatewayListener {
    private final Gateway gateway;
    private JTextField [] uidTextField = new JTextField[COUNT_DEVICES];
    private JTextField [] deviceModelTextField = new JTextField[COUNT_DEVICES];
    private JLabel [] currentStatusLabel = new JLabel[COUNT_DEVICES];
    private JPanel [] statusPanel = new JPanel[COUNT_DEVICES];
    
    private static final Rectangle[] devicePanelBounds = new Rectangle[] {
    	new Rectangle(10, 11, 234, 182),
    	new Rectangle(10, 204, 234, 182),
    	new Rectangle(10, 397, 234, 182),
    	new Rectangle(10, 587, 234, 182)
    };
    
    
    private static final Rectangle statusPanelBounds = new Rectangle(129, 77, 95, 27);
    private static final Rectangle currentStatusLabelBounds = new Rectangle(15, 0, 80, 22);
    private static final Rectangle deviceModelLabelBounds = new Rectangle(10, 114, 80, 22);
    private static final Rectangle uidLabelBounds = new Rectangle(10, 148, 80, 22);
    private static final Rectangle uidTextFieldBounds = new Rectangle(100, 149, 124, 22);
    private static final Rectangle deviceModelTextFieldBounds = new Rectangle(100, 115, 124, 22);
    private static final Rectangle imagePanelBounds = new Rectangle(9, 11, 124, 95);
    
    private final List<JPanel> [] lines = new List[COUNT_DEVICES];
    
    private static final Rectangle[][] lineBounds = new Rectangle[][] {
    	{ new Rectangle(244, 151, 105, 1), new Rectangle(348, 151, 1, 199), new Rectangle(348, 349, 61, 1) },
    	{ new Rectangle(244, 372, 168, 1) },
    	{ new Rectangle(244, 417, 168, 1) },
    	{ new Rectangle(244, 602, 105, 1), new Rectangle(348, 441, 1, 162), new Rectangle(348, 441, 64, 1) },
    };
    
    private static final Rectangle[][] appLineBounds = new Rectangle[][] {
    	{ new Rectangle(644, 385, 64, 1), new Rectangle(707, 224, 1, 162), new Rectangle(707, 224, 47, 1) },
    	{ new Rectangle(644, 441, 64, 1), new Rectangle(707, 441, 1, 162), new Rectangle(707, 602, 47, 1) },
    };
    
    private final List<JPanel> [] appLines = new List[COUNT_APPS];
    
    private JPanel [] appStatusPanel = new JPanel[COUNT_APPS];
    private JLabel [] appStatusLabel = new JLabel[COUNT_APPS];
    
    private static final Rectangle appStatusPanelBounds = new Rectangle(78, 74, 81, 27);
    private static final Rectangle appStatusLabelBounds = new Rectangle(28, 0, 28, 22);
    private static final Rectangle[] appBounds = new Rectangle[] {
    	new Rectangle(753, 486, 234, 182),
    	new Rectangle(753, 98, 234, 182)
    };
    private static final Rectangle[] appLabelBounds = new Rectangle[] {
    	new Rectangle(820, 453, 135, 22),
    	new Rectangle(797, 65, 135, 22)
    };
    
    private static final String [] appNames = new String[] {
    	"Blood Pressure App",
    	"Pulse Oximeter App"
    };
    
    /**
     * Image panels for the devices, call setVisible(true) when the devices are discovered
     * to show the images.
     */
    private final static int COUNT_DEVICES = 4;
    private final static int COUNT_APPS = 2;
    
    private final ImagePanel [] deviceImage = new ImagePanel[COUNT_DEVICES];

    public EngineerConsole(Gateway gateway) {
    	this.gateway = gateway;
        initialize();
    }

    
    private void buildDevicePanels() {
        JPanel [] devicePanel = new JPanel[COUNT_DEVICES];

        for(int i = 0; i < COUNT_DEVICES; i++) {
        	devicePanel[i] = new JPanel();
        	devicePanel[i].setBorder(new LineBorder(Color.black));
        	devicePanel[i].setLayout(null);
        	devicePanel[i].setBackground(Color.white);
        	devicePanel[i].setBounds(devicePanelBounds[i]);
        	add(devicePanel[i]);
        	
            JLabel label = new JLabel();
            label.setText("Device Model");
            label.setForeground(Color.BLACK);
            label.setFont(new Font("Dialog", Font.BOLD, 12));
            label.setBounds(deviceModelLabelBounds);
            devicePanel[i].add(label);
            
            JLabel label_1 = new JLabel();
            label_1.setText("UID");
            label_1.setHorizontalAlignment(SwingConstants.CENTER);
            label_1.setForeground(Color.BLACK);
            label_1.setFont(new Font("Dialog", Font.BOLD, 12));
            label_1.setBounds(uidLabelBounds);
            devicePanel[i].add(label_1);
            
            uidTextField[i] = new JTextField();
            uidTextField[i].setEditable(false);
            uidTextField[i].setBounds(uidTextFieldBounds);
            devicePanel[i].add(uidTextField[i]);
            uidTextField[i].setText("Dev " + (i+1) + " UUID");  
            
            deviceModelTextField[i] = new JTextField();
            deviceModelTextField[i].setEditable(false);
            deviceModelTextField[i].setBounds(deviceModelTextFieldBounds);
            devicePanel[i].add(deviceModelTextField[i]);
            deviceModelTextField[i].setText("Dev " + (i+1) + " Model");
        	
            deviceImage[i] = new ImagePanel();
            deviceImage[i].setBounds(imagePanelBounds);
            devicePanel[i].add(deviceImage[i]);
            
            statusPanel[i] = new JPanel();
            statusPanel[i].setLayout(null);
            statusPanel[i].setBackground(Color.BLACK);
            statusPanel[i].setBounds(statusPanelBounds);
            devicePanel[i].add(statusPanel[i]);
            
            currentStatusLabel[i] = new JLabel();
            currentStatusLabel[i].setText("Unknown");
            currentStatusLabel[i].setForeground(Color.WHITE);
            currentStatusLabel[i].setFont(new Font("Dialog", Font.BOLD, 12));
            currentStatusLabel[i].setBounds(currentStatusLabelBounds);
            statusPanel[i].add(currentStatusLabel[i]);      
            
            
            lines[i] = new ArrayList<JPanel>();
            for(int j = 0; j < lineBounds[i].length; j++) {
                JPanel line = new JPanel();
                line.setBorder(new LineBorder(Color.black));
                line.setLayout(null);
                line.setBackground(Color.WHITE);
                line.setBounds(lineBounds[i][j]);
                add(line);
                lines[i].add(line);
            }
            
        }
    }
    
    private void buildAppPanels() {
        for(int i = 0; i < COUNT_APPS; i++) {
        	JPanel panel = new JPanel();
        	panel.setBorder(new LineBorder(Color.black));
        	panel.setLayout(null);
        	panel.setBackground(Color.white);
        	panel.setBounds(appBounds[i]);
        	add(panel);
        	
        	appStatusPanel[i] = new JPanel();
        	appStatusPanel[i].setLayout(null);
        	appStatusPanel[i].setBackground(Color.red);
        	appStatusPanel[i].setBounds(appStatusPanelBounds);
        	panel.add(appStatusPanel[i]);
        	
        	appStatusLabel[i] = new JLabel();
        	appStatusLabel[i].setText("Off");
            appStatusLabel[i].setForeground(Color.BLACK);
            appStatusLabel[i].setFont(new Font("Dialog", Font.BOLD, 12));
            appStatusLabel[i].setBounds(appStatusLabelBounds);
            appStatusPanel[i].add(appStatusLabel[i]);

            JLabel appLabel = new JLabel();
            appLabel.setText(appNames[i]);
            appLabel.setForeground(Color.BLACK);
            appLabel.setFont(new Font("Dialog", Font.BOLD, 12));
            appLabel.setBounds(appLabelBounds[i]);
            add(appLabel);
            
            appLines[i] = new ArrayList<JPanel>();
            for(int j = 0; j < appLineBounds[i].length; j++) {
                JPanel line = new JPanel();
                line.setBorder(new LineBorder(Color.black));
                line.setLayout(null);
                line.setBackground(Color.WHITE);
                line.setBounds(appLineBounds[i][j]);
                add(line);
                appLines[i].add(line);
            }
        }
    }
    
    private void buildIceManager() {
        JPanel panel_9 = new JPanel();
        panel_9.setBorder(new LineBorder(new Color(0, 0, 0)));
        panel_9.setLayout(null);
        panel_9.setBounds(new Rectangle(12, 222, 543, 156));
        panel_9.setBackground(Color.WHITE);
        panel_9.setBounds(411, 312, 234, 182);
        add(panel_9);
        
        JPanel panel_10 = new JPanel();
        panel_10.setLayout(null);
        panel_10.setBackground(Color.GREEN);
        panel_10.setBounds(78, 74, 81, 27);
        panel_9.add(panel_10);
        
        JLabel lblOn = new JLabel();
        lblOn.setText("On");
        lblOn.setForeground(Color.BLACK);
        lblOn.setFont(new Font("Dialog", Font.BOLD, 12));
        lblOn.setBounds(31, 0, 28, 22);
        panel_10.add(lblOn);
        
        JLabel lblIceManager = new JLabel();
        lblIceManager.setText("ICE Manager");
        lblIceManager.setForeground(Color.BLACK);
        lblIceManager.setFont(new Font("Dialog", Font.BOLD, 12));
        lblIceManager.setBounds(478, 279, 94, 22);
        add(lblIceManager);
    }
    
    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
//        frmEngineerConsole = new JFrame();
//        frmEngineerConsole.setTitle("Engineer's Console");
//        frmEngineerConsole.setBounds(100, 100, 1020, 816);
//        frmEngineerConsole.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    	setLayout(null);
//        panel = new JPanel();
//        panel.setLayout(null);
        setBackground(Color.WHITE);
//        frmEngineerConsole.getContentPane().add(panel, BorderLayout.CENTER);
        buildDevicePanels();

        buildIceManager();
        
        buildAppPanels();
    }
    
    /**
     * Private methods waiting to be invoked by listeners for the different app/device
     * panels.
     */
    
    private void changeDeviceStatus(JPanel currentStatusPanel, JLabel currentStatusLabel, List<JPanel> lines, ComponentStatus status) {
        setComponentTextAndPanel(currentStatusPanel, currentStatusLabel, status);
        
        for (JPanel line : lines) {
            line.setBorder(BorderFactory.createLineBorder(status.getPanelColor(), 1));
        }
    }
    
    private void changeDeviceStatusNum(int device, ComponentStatus status) {
    	changeDeviceStatus(statusPanel[device], currentStatusLabel[device], lines[device], status);
    }

    
    private void setComponentTextAndPanel(JPanel currentStatusPanel, JLabel currentStatusLabel, ComponentStatus status) {
        currentStatusPanel.setBackground(status.getPanelColor());
        currentStatusLabel.setForeground(status.getTextColor());
        currentStatusLabel.setText(status.getText());
    }
    
    private void changeAppStatus(JPanel currentStatusPanel, JLabel currentStatusLabel, ComponentStatus status) {
        setComponentTextAndPanel(currentStatusPanel, currentStatusLabel, status);
    }
    
    private void changeDeviceModelBox(ImagePanel imagePanel, JTextField deviceModel, JTextField uid, String deviceModelString, String uidString) {
        deviceModel.setText(deviceModelString);
        uid.setText(uidString);
        URL url = EngineerConsole.class.getResource("images/"+deviceModelString + ".jpg");
        imagePanel.setImage(url);
        
        repaint();
    }
    
    private void changeDeviceModelNameAndUidNum(int deviceID, String model, String uid) {
    	changeDeviceModelBox(deviceImage[deviceID], deviceModelTextField[deviceID], uidTextField[deviceID], model, uid);
    }
    
    private ComponentStatus MyStatus(String s)
    {
    	if (s.equals("AUTHENTICATING") || s.equals("AUTHENTICATED"))
    	{
    		return ComponentStatus.CONNECTED;
    	}
    	else 
    	{
    		return ComponentStatus.SENDING_DATA;
    	}
    }
    
//    private class VisualChanListener implements IMdcfMessageListener
//	{
//		@Override
//		public void onMessage(MdcfMessage message) {
//			try {
//				String msgText = message.getTextMsg();
//				String[] splitMsg = msgText.split(" : ");
//				if (!splitMsg[1].startsWith("null"))
//				{
//					if(splitMsg[0].startsWith("M1"))
//					{
//						if (devices.contains(splitMsg[1]))
//						{
//							if (devices.indexOf(splitMsg[1]) == 0 )
//							{
//								ComponentStatus currentStatus = MyStatus(splitMsg[2]);
//								changeDeviceStatusNum(0, currentStatus);	
//							}
//							else if (devices.indexOf(splitMsg[1]) == 1 )
//							{
//								ComponentStatus currentStatus = MyStatus(splitMsg[2]);
//								changeDeviceStatusNum(1, currentStatus);
//							}
//							else if (devices.indexOf(splitMsg[1]) == 2 )
//							{
//								ComponentStatus currentStatus = MyStatus(splitMsg[2]);
//								changeDeviceStatusNum(2, currentStatus);
//							}
//							else if (devices.indexOf(splitMsg[1]) == 3 )
//							{
//								ComponentStatus currentStatus = MyStatus(splitMsg[2]);
//								changeDeviceStatusNum(3, currentStatus);
//							}
//						}
//						else
//						{
//							if (devices.size() < 4)
//							{
//								devices.add(splitMsg[1]);
//								deviceUidNameMap.put(splitMsg[1], splitMsg[3]);
//								if (devices.indexOf(splitMsg[1]) == 0 )
//								{
//									ComponentStatus currentStatus = MyStatus(splitMsg[2]);
//									changeDeviceStatusNum(0, currentStatus);
//									changeDeviceModelNameAndUidNum(0, splitMsg[3]);
//								}
//								else if (devices.indexOf(splitMsg[1]) == 1 )
//								{
//									ComponentStatus currentStatus = MyStatus(splitMsg[2]);
//									changeDeviceStatusNum(1, currentStatus);
//									changeDeviceModelNameAndUidNum(1, splitMsg[3]);
//								}
//								else if (devices.indexOf(splitMsg[1]) == 2 )
//								{
//									ComponentStatus currentStatus = MyStatus(splitMsg[2]);
//									changeDeviceStatusNum(2, currentStatus);
//									changeDeviceModelNameAndUidNum(2, splitMsg[3]);
//								}
//								else if (devices.indexOf(splitMsg[1]) == 3 )
//								{
//									ComponentStatus currentStatus = MyStatus(splitMsg[2]);
//									changeDeviceStatusNum(3, currentStatus);
//									changeDeviceModelNameAndUidNum(3, splitMsg[3]);
//								}
//							}
//						}
//					}
//					/*if (splitMsg[0].startsWith("M2"))
//					{
//						if (devices.contains(splitMsg[1]))
//						{
//							if (devices.indexOf(splitMsg[1]) == 0 )
//							{
//								
//							}
//							else if (devices.indexOf(splitMsg[1]) == 1 )
//							{
//								
//							}
//							else if (devices.indexOf(splitMsg[1]) == 2 )
//							{
//								
//							}
//							else if (devices.indexOf(splitMsg[1]) == 3 )
//							{
//								
//							}
//						}
//					}*/
//					if(splitMsg[0].startsWith("M3"))
//					{
//					    if (splitMsg[2].contains("HIMSS")) 
//					    {
//					        if (deviceUidNameMap.containsKey(splitMsg[1]))
//					        {
//					            if (deviceUidNameMap.get(splitMsg[1]).toLowerCase().contains("po"))
//					            {
//					                changeAppStatus(poAppCurrentStatusPanel, poAppCurrentStatusLabel, ComponentStatus.ON);
//					            }
//					            else if (deviceUidNameMap.get(splitMsg[1]).toLowerCase().contains("bp") ||
//					                    deviceUidNameMap.get(splitMsg[1]).toLowerCase().contains("and"))
//					            {
//					                changeAppStatus(bpAppCurrentStatusPanel, bpAppCurrentStatusLabel, ComponentStatus.ON);
//					            }
//					            
//					            apps.add(splitMsg[2]);
//					        }
//					        else 
//					        {
//					            if (apps.size() < 2)
//					            {
//					                apps.add(splitMsg[1]);
//					                if (apps.indexOf(splitMsg[1]) == 0 )
//					                {
//					                    ComponentStatus currentStatus = MyStatus(splitMsg[2]);
//					                    changeDeviceStatusNum(0, currentStatus);
//					                    changeDeviceModelNameAndUidNum(0, splitMsg[3]);
//					                }
//					                else if (apps.indexOf(splitMsg[1]) == 1 )
//					                {
//					                    ComponentStatus currentStatus = MyStatus(splitMsg[2]);
//					                    changeDeviceStatusNum(1, currentStatus);
//					                    changeDeviceModelNameAndUidNum(1, splitMsg[3]);
//					                }
//					            }
//					        }
//					    }
//					}
//				}
//				System.out.println("VisualChannel:"+ msgText);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
//	}
//    private static final Map<String, Integer> deviceMapping = new HashMap<String, Integer>();

    private static final Integer mapDevice(Device device) {
    	boolean simulated = device instanceof SimulatedDevice;
    	if(device == null) {
    		return null;
    	}
    	if(device instanceof PulseOximeter) {
    		return simulated ? 2 : 3;
    	} else if(device instanceof BloodPressure) {
    		return simulated ? 0 : 1;
    	}
    	return null;
    }
    

//    @Override
//    public void deviceAdded(Device device) {
//    	gateway.addListener(this);
////    	device.addListener(this);
//    	MutableIdentifierArrayUpdate miau = new MutableIdentifierArrayUpdateImpl(Device.REQUEST_IDENTIFIED_UPDATES);
//    	miau.setValue(new Identifier[] {Device.NAME});
//    	device.update(miau);
//    }
//    
//    @Override
//    public void deviceRemoved(Device device) {
//    	gateway.removeListener(this);
////    	device.removeListener(this);
//    	Integer idx = mapDevice(device);
//		if(null != idx) {
//			changeDeviceStatusNum(idx, ComponentStatus.DISCONNECTED);
//			changeDeviceModelNameAndUidNum(idx, "", "");
//		}
//    }


	@Override
	public void update(IdentifiableUpdate<?> update) {
		if(Device.NAME.equals(update.getIdentifier())) {
			String name = ((TextUpdate)update).getValue();
			// TODO THIS WILL FAIL
			Integer idx = mapDevice(null);
			if(idx != null) {
				changeDeviceStatusNum(idx, ComponentStatus.CONNECTED);
				changeDeviceModelNameAndUidNum(idx, name, update.getSource());
			}
		}
	}

}
