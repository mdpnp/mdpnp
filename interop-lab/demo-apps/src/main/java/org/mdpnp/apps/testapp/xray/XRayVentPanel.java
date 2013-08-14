package org.mdpnp.apps.testapp.xray;

import ice.DeviceConnectivity;
import ice.Numeric;
import ice.SampleArray;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.mdpnp.apps.testapp.DemoPanel;
import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.DeviceListCellRenderer;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.devices.EventLoop;
import org.mdpnp.guis.swing.DeviceMonitor;
import org.mdpnp.guis.swing.DeviceMonitorListener;
import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformUpdateWaveformSource;
import org.mdpnp.guis.waveform.swing.SwingWaveformPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.Subscriber;

@SuppressWarnings("serial")
public class XRayVentPanel extends JPanel implements DeviceMonitorListener {
	private FramePanel cameraPanel;

	private WaveformPanel waveformPanel;
	private WaveformUpdateWaveformSource wuws;
	@SuppressWarnings("rawtypes")
    private JList deviceList;
	
	public enum Strategy { 
		Manual,
		Automatic
	}
	
	public enum TargetTime {
		EndInspiration,
		EndExpiration
	}

	private final DemoPanel demoPanel;
//	private final static long startOfTime = System.currentTimeMillis();
	
//	private static final String XRAY = "xray";
	
//	private final JList<Strategy> strategies = new JList<Strategy>(Strategy.values());
//	private final JList<TargetTime> targetTime = new JList<TargetTime>(TargetTime.values());
	private final JButton imageButton = new JButton("IMAGE <space bar>");
	private final JButton resetButton = new JButton("RESET <escape>");
	private final ButtonGroup strategiesGroup = new ButtonGroup();
	private final ButtonGroup targetTimesGroup = new ButtonGroup();
	private static final float FONT_SIZE = 20f;
	protected JPanel buildRadioButtons(Object[] values, ButtonGroup buttonGroup) {
		JPanel panel = new JPanel(new GridLayout(values.length, 1)); // new GridLayout(values.length, 1));
		boolean seenFirstButton = false;
		
		for(Object o : values) {
			JRadioButton r = new JRadioButton(o.toString());
			r.setFont(r.getFont().deriveFont(FONT_SIZE));
			r.setActionCommand(o.toString());
			if(!seenFirstButton) {
				r.setSelected(true);
				seenFirstButton = true;
			}
			buttonGroup.add(r);
			panel.add(r);
		}
		
		return panel;
	}
	private static final Logger log = LoggerFactory.getLogger(XRayVentPanel.class);
	
	private DeviceMonitor deviceMonitor;
	
	public void changeSource(String source, DomainParticipant participant, EventLoop eventLoop) {
	    if(null != deviceMonitor) {
	        if(deviceMonitor.getUniversalDeviceIdentifier().equals(source)) {
	            return;
	        } else {
	            deviceMonitor.stop();
	            deviceMonitor = null;
	        }
	    }
	    
        deviceMonitor = new DeviceMonitor(source);
        deviceMonitor.addListener(XRayVentPanel.this);
        deviceMonitor.start(participant, eventLoop);
		log.trace("new source is " + source);
	}
	
//	private static final Font RADIO_FONT = Font.decode("verdana-20");
	@SuppressWarnings({ "rawtypes", "unchecked" })
    protected JPanel buildXRay(final DeviceListModel devices, final Subscriber subscriber, final EventLoop eventLoop) {
		JPanel panel = new JPanel(new GridLayout(2,2));
		
		JPanel textPanel = new JPanel(new BorderLayout());
		JLabel text = new JLabel();
		
		text.setText("Sources");
		text.setFont(text.getFont().deriveFont(FONT_SIZE));
		textPanel.add(text, BorderLayout.NORTH);
		deviceList = new JList(devices);
		deviceList.setCellRenderer(new DeviceListCellRenderer());
		textPanel.add(new JScrollPane(deviceList), BorderLayout.CENTER);
		panel.add(textPanel);
		
		deviceList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
			    int idx = deviceList.getSelectedIndex();
			    if(idx >= 0) {
			        Device device = devices.getElementAt(idx);
			        if(null != device) {
			            changeSource(device.getDeviceIdentity().universal_device_identifier, subscriber.get_participant(), eventLoop);
			        }
			    }
			}
		});
		
//		final Border border = new BevelBorder(BevelBorder.LOWERED, DemoPanel.lightBlue, DemoPanel.darkBlue);
		final Border border = new LineBorder(DemoPanel.darkBlue, 2); 
		
		JPanel enclosingFramePanel = new JPanel(new BorderLayout());
		JLabel l;
		enclosingFramePanel.add(l=new JLabel("X-Ray Viewer"), BorderLayout.NORTH);
		l.setFont(l.getFont().deriveFont(FONT_SIZE));
		cameraPanel = new FramePanel(0);
		cameraPanel.setBorder(border);
		enclosingFramePanel.add(cameraPanel, BorderLayout.CENTER);
//		cameraPanel.getInsets().set(20, 20, 20, 20);
        panel.add(enclosingFramePanel);
        
        wuws = new WaveformUpdateWaveformSource();
        

        JPanel enclosingWaveformPanel = new JPanel(new BorderLayout());
        enclosingWaveformPanel.add(l=new JLabel("Flow Inspiration/Expiration"), BorderLayout.NORTH);
        l.setFont(l.getFont().deriveFont(FONT_SIZE));

        waveformPanel = new SwingWaveformPanel();
//        waveformPanel = new WaveformPanelFactory().createWaveformPanel();
        if(waveformPanel instanceof JComponent) {
            ((JComponent)waveformPanel).setBorder(border);
        }
//        waveformPanel.setBackground(new Color(1.0f,1.0f,1.0f,0.0f));
        
        waveformPanel.setEvenTempo(false);
        waveformPanel.setSource(wuws);
        waveformPanel.cachingSource().setFixedTimeDomain(6000L);

//        waveformPanel.getRenderer().setContinuousRescale(false);
        enclosingWaveformPanel.add(waveformPanel.asComponent(), BorderLayout.CENTER);
        panel.add(enclosingWaveformPanel);
        
        
        JPanel controlsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.ipadx = 10;
        gbc.ipady = 0;
        gbc.insets = new Insets(0,10,0,10);
        gbc.gridwidth = 1; 
        gbc.gridheight = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        gbc.gridwidth = 2;
        
        controlsPanel.add(l= new JLabel("Exposure Time (seconds)"), gbc);
        l.setFont(l.getFont().deriveFont(FONT_SIZE));
        
        gbc.gridy++;
        
        exposureTime.setMajorTickSpacing(100);
        exposureTime.setSnapToTicks(true);
        exposureTime.setName("Exposure Time");
//        exposureTime.setFont(exposureTime.getFont().deriveFont(FONT_SIZE));
        exposureTime.setPaintTicks(true);
        exposureTime.setPaintLabels(true);
        Dictionary dict = exposureTime.createStandardLabels(100, 0);
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMinimumFractionDigits(1);
        nf.setMaximumFractionDigits(1);
        exposureTime.setFont(exposureTime.getFont().deriveFont(FONT_SIZE));
        //TOTAL KLUGE
        for(int i = exposureTime.getMinimum(); i <= exposureTime.getMaximum(); i+= 100) {
        	Object o = dict.get(i);
        	if(o != null && o instanceof JLabel) {
        		((JLabel)o).setText(nf.format(i/1000.0));
        	}
        }
  
        exposureTime.setLabelTable(dict);
        

        controlsPanel.add(exposureTime, gbc);
        
        gbc.gridy++;
        gbc.gridwidth = 1;
        
        JLabel strategyLabel = new JLabel("Synchronization Strategy");
        strategyLabel.setFont(strategyLabel.getFont().deriveFont(FONT_SIZE));
        strategyLabel.setHorizontalAlignment(SwingConstants.LEFT);
        strategyLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        controlsPanel.add(strategyLabel, gbc);
        
        gbc.gridx = 1;
        
        JLabel targetTimeLabel = new JLabel("Phase of Ventilation");
        targetTimeLabel.setFont(targetTimeLabel.getFont().deriveFont(FONT_SIZE));
        targetTimeLabel.setHorizontalAlignment(SwingConstants.LEFT);
        targetTimeLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        controlsPanel.add(targetTimeLabel, gbc);
        
        gbc.gridy++;
        gbc.gridx = 0;
//        gbc.weighty = 1.5;
        controlsPanel.add(buildRadioButtons(Strategy.values(), strategiesGroup), gbc);
        gbc.gridx++;

        controlsPanel.add(buildRadioButtons(TargetTime.values(), targetTimesGroup), gbc);
        gbc.gridy++;
//        gbc.weighty=0.7;
        gbc.gridx = 0;
        controlsPanel.add(imageButton, gbc);
        
        gbc.gridx = 1;
        controlsPanel.add(resetButton, gbc);
        resetButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
	        	cameraPanel.unfreeze();
        	}
        });
        panel.add(controlsPanel);
        
        
        return panel;
	}
//	private CardLayout panelLayout;
	
	
	private boolean imageButtonDown = false;
//	private Clip shutterClip;
	
//	private DeviceListModel devices;
	public XRayVentPanel(DemoPanel demoPanel, DeviceListModel devices, Subscriber subscriber, EventLoop eventLoop) {
		super(new BorderLayout());

//		this.devices = devices;
        this.demoPanel = demoPanel;
		
		
	        
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_SPACE) {
					switch(e.getID()) {
					case KeyEvent.KEY_PRESSED:
						imageButton.getModel().setSelected(true);
						imageButton.getModel().setPressed(true);
						return true;
					case KeyEvent.KEY_RELEASED:
						imageButton.getModel().setPressed(false);
						imageButton.getModel().setSelected(false);
						return true;
					default:
						return false;
					}
				} else if(e.getKeyCode()==KeyEvent.VK_ESCAPE) {
					switch(e.getID()) {
					case KeyEvent.KEY_RELEASED:
						resetButton.doClick();
						return true;
					default:
						return false;
					}
				} else {
					return false;
				}
				
			}
		});

		
//		imageButton.getModel().setArmed(true);
		imageButton.getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				imageButtonDown = imageButton.getModel().isPressed();
				if(imageButtonDown && Strategy.Manual.equals(Strategy.valueOf(strategiesGroup.getSelection().getActionCommand()))) {
					noSync();
				}
				log.info(""+imageButtonDown);
			}
		});	
//		JPanel content = demoPanel.getContent();
//		JPanel content = demoPanel;
//		content.setLayout(panelLayout = new CardLayout());
//		demoPanel.getBedLabel().setText("Device Integration Demo");
////		setTitle("Device Integration Demo");
//		demoPanel.getPatientLabel().setText("");
//		demoPanel.getPatientLabel().setFont(Font.decode("courier-12"));
//		demoPanel.getPatientLabel().setVerticalAlignment(SwingConstants.TOP);
//		demoPanel.getPatientLabel().setVerticalTextPosition(SwingConstants.TOP);
//		content.add(buildIntro(), INTRO);
		add(buildXRay(devices, subscriber, eventLoop), BorderLayout.CENTER);
//		panelLayout.show(content, XRAY);
        DemoPanel.setChildrenOpaque(this, false);
//        demoPanel.setOpaque(true);
        cameraPanel.setOpaque(true);
//        start();
       
	}
//	private RTRegression airwayPressure = new RTRegressionImpl(10);
//	private RTRegression flow = new RTRegressionImpl(10);
	private boolean started = false;
	public void stop() {
	    waveformPanel.stop();
	    if(null != deviceMonitor) {
	        deviceMonitor.stop();
	        deviceMonitor = null;
	    }
		if(started) {
			started = false;
			cameraPanel.stop();
		}
	}
	
	public void start() {
		if(!started) {
		    waveformPanel.start();
			demoPanel.getBedLabel().setText("X-Ray / Ventilator Synchronization");
			demoPanel.getPatientLabel().setText("");
			demoPanel.getPatientLabel().setFont(Font.decode("courier-bold-20"));
			demoPanel.getPatientLabel().setVerticalAlignment(SwingConstants.TOP);
			demoPanel.getPatientLabel().setVerticalTextPosition(SwingConstants.TOP);
			started = true;
			cameraPanel.start();
		}
	}
	
//	private static final void regressionUpdate(String name, ice.SampleArray wu, RTRegression rtRegression) {
//		double first_sample = System.currentTimeMillis() - startOfTime - wu.values.size() * wu.millisecondsPerSample;
//		
//		for(int i = 0; i < wu.values.size(); i++) {
//			double x = first_sample+i*wu.millisecondsPerSample;
//			double y = wu.values.getFloat(i);
//			rtRegression.newPoint(x, y);
//			
//		}
//	}
	
	private long inspiratoryTime;
	private long period;
	protected ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	
	private final Callable<Void> freezeCallable = new Callable<Void>() {
		@Override
		public Void call() throws Exception {
			if(imageButtonDown) {
				cameraPanel.freeze(exposureTime.getValue());
			}
			return null;
		}
	};
	
	private JSlider exposureTime = new JSlider(0, 1000, 0);
	
	private final void noSync() {
		cameraPanel.freeze(exposureTime.getValue());

        
	}
	
	private final void autoSync(TargetTime targetTime) {
		
		switch(targetTime) {
		case EndExpiration:
		    // JP Apr 29, 2013
		    // Luckily this works quickly enough to respond to *this* start breath
		    // event.  A more robust implementation would probably trigger just before
		    // the *next* start breath ... or a timeout of (this.period-50L) on the following line
			executor.schedule(freezeCallable, 0L, TimeUnit.MILLISECONDS);
			break;
		case EndInspiration:
			executor.schedule(freezeCallable, inspiratoryTime-50L, TimeUnit.MILLISECONDS);
			break;
		}
	}
	
	private static final Color alertPink = new Color(200, 20, 0);
	private static final Color normalGreen = new Color(20, 200, 20);
    @Override
    public void deviceIdentity(ice.DeviceIdentityDataReader reader, ice.DeviceIdentitySeq di_seq, SampleInfoSeq info_seq) {
    }
    private final Set<InstanceHandle_t> seenInstances = new HashSet<InstanceHandle_t>();
    @Override
    public void deviceConnectivity(ice.DeviceConnectivityDataReader reader, ice.DeviceConnectivitySeq dc_seq, SampleInfoSeq info_seq) {
        seenInstances.clear();
        for(int i = info_seq.size() - 1; i >= 0; i--) {
            SampleInfo si = (SampleInfo) info_seq.get(i);
            if(si.valid_data && !seenInstances.contains(si.instance_handle)) {
                seenInstances.add(si.instance_handle);
                DeviceConnectivity dc = (DeviceConnectivity) dc_seq.get(i);
                demoPanel.getPatientLabel().setText(dc.state.name());
                switch(dc.state.value()) {
                case ice.ConnectionState._Connected:
                    demoPanel.getPatientLabel().setForeground(normalGreen);
                    break;
                default:
                    demoPanel.getPatientLabel().setForeground(alertPink);
                    break;
                }
            }
        }

    }

    @Override
    public void numeric(ice.NumericDataReader reader, ice.NumericSeq nu_seq, SampleInfoSeq info_seq) {
        seenInstances.clear();
        for(int i = info_seq.size() - 1; i >= 0; i--) {
            SampleInfo si = (SampleInfo) info_seq.get(i);
            
            if(si.valid_data && !seenInstances.contains(si.instance_handle)) {
                seenInstances.add(si.instance_handle);
                ice.Numeric n = (Numeric) nu_seq.get(i);
                long previousPeriod = this.period;
                switch(n.name) {
                case ice.MDC_TIME_PD_INSPIRATORY.VALUE:
                    inspiratoryTime = (long) (1000.0 * n.value);
                    break;
                case ice.MDC_VENT_TIME_PD_PPV.VALUE:
                    
                    period = (long)( 60000.0 / n.value );
                    if(period != previousPeriod) {
                        log.debug("FrequencyIPPV="+n.value+" period="+period);
                    }
                    break;
                case ice.MDC_START_OF_BREATH.VALUE:
//                  log.trace("START_INSPIRATORY_CYCLE");
                  Strategy strategy = Strategy.valueOf(strategiesGroup.getSelection().getActionCommand());
                  TargetTime targetTime = TargetTime.valueOf(targetTimesGroup.getSelection().getActionCommand());
                  
                  switch(strategy) {
                  case Automatic:
                      autoSync(targetTime);
                      break;
                  case Manual:
                      break;
                  }
                  break;
                }
            }
        }
    }

    @Override
    public void sampleArray(ice.SampleArrayDataReader redaer, ice.SampleArraySeq sa_seq, SampleInfoSeq info_seq) {
        seenInstances.clear();
        for(int i = info_seq.size() - 1; i >= 0; i--) {
            SampleInfo si = (SampleInfo) info_seq.get(i);
            if(si.valid_data && !seenInstances.contains(si.instance_handle)) {
                seenInstances.add(si.instance_handle);
                ice.SampleArray sa = (SampleArray) sa_seq.get(i);
                switch(sa.name) {
                case ice.MDC_FLOW_AWAY.VALUE:
                    wuws.applyUpdate(sa);
                    break;
                }
            }
        }

    }

}
