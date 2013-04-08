package org.mdpnp.apps.testapp.xray;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.Dictionary;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
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

import org.mdpnp.apps.gui.waveform.WaveformUpdateWaveformSource;
import org.mdpnp.apps.gui.waveform.swing.SwingWaveformPanel;
import org.mdpnp.apps.testapp.DemoFrame;
import org.mdpnp.apps.testapp.DemoPanel;
import org.mdpnp.apps.testapp.DeviceListCellRenderer;
import org.mdpnp.comms.Gateway;
import org.mdpnp.comms.GatewayListener;
import org.mdpnp.comms.IdentifiableUpdate;
import org.mdpnp.comms.Identifier;
import org.mdpnp.comms.data.enumeration.EnumerationUpdate;
import org.mdpnp.comms.data.identifierarray.MutableIdentifierArrayUpdate;
import org.mdpnp.comms.data.identifierarray.MutableIdentifierArrayUpdateImpl;
import org.mdpnp.comms.data.numeric.NumericUpdate;
import org.mdpnp.comms.data.text.TextUpdate;
import org.mdpnp.comms.data.waveform.WaveformUpdate;
import org.mdpnp.comms.nomenclature.ConnectedDevice;
import org.mdpnp.comms.nomenclature.Device;
import org.mdpnp.comms.nomenclature.Ventilator;
import org.mdpnp.comms.nomenclature.ConnectedDevice.State;
import org.mdpnp.devices.draeger.medibus.DemoApollo;
import org.mdpnp.transport.GetConnected;
import org.mdpnp.transport.MutableDevice;
import org.mdpnp.transport.NetworkController.AcceptedDevices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_objdetect;
import com.jeffplourde.util.math.RTRegression;

public class JeffGUI extends JPanel implements GatewayListener {
	private FramePanel cameraPanel;
	private final Gateway gateway;

	private SwingWaveformPanel waveformPanel;
	private WaveformUpdateWaveformSource wuws;
	private JList deviceList;
//	private JLabel dFlow, dPressure;
	
	public enum Strategy { 
		NoSynchronization,
		DeadReckoning
	}
	
	public enum TargetTime {
		EndInspiration,
		EndExpiration
	}

//	private final DemoPanel demoPanel;
	private final DemoPanel demoPanel;
	private final static long startOfTime = System.currentTimeMillis();
	
	private static final String XRAY = "xray";
	
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
	private static final Logger log = LoggerFactory.getLogger(JeffGUI.class);
	
	public void changeSource(String source) {

		log.trace("new source is " + source);
		this.source = source;
		MutableIdentifierArrayUpdate ia = new MutableIdentifierArrayUpdateImpl(Device.REQUEST_IDENTIFIED_UPDATES);
		ia.setTarget(source);
		ia.setValue(new Identifier[] {ConnectedDevice.STATE} );
		gateway.update(this, ia);
	}
	
//	private static final Font RADIO_FONT = Font.decode("verdana-20");
	protected JPanel buildXRay(final AcceptedDevices devices) {
		JPanel panel = new JPanel(new GridLayout(2,2));
		
		JPanel textPanel = new JPanel(new BorderLayout());
		JLabel text = new JLabel();
		
		text.setText("X-Ray / Ventilator Synchronization");
		text.setFont(text.getFont().deriveFont(FONT_SIZE));
		textPanel.add(text, BorderLayout.NORTH);
		deviceList = new JList(devices);
		deviceList.setCellRenderer(new DeviceListCellRenderer());
		textPanel.add(new JScrollPane(deviceList), BorderLayout.CENTER);
		panel.add(textPanel);
		deviceList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int idx  = deviceList.locationToIndex(e.getPoint());
				if(idx>=0) {
					MutableDevice o = (MutableDevice) devices.getElementAt(idx);
					changeSource(o.getSource());
				}
				super.mouseClicked(e);
			}
		});
//		
//		deviceList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//			@Override
//			public void valueChanged(ListSelectionEvent e) {
//				changeSource(devices.getElementAt(e.getFirstIndex()).getSource());
//				
//			}
//		});
		
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
        waveformPanel.setBorder(border);
//        waveformPanel.setBackground(new Color(1.0f,1.0f,1.0f,0.0f));
        
        waveformPanel.setEvenTempo(false);
        waveformPanel.setSource(wuws);
        waveformPanel.cachingSource().setFixedTimeDomain(6000L);

        waveformPanel.getRenderer().setContinuousRescale(false);
        enclosingWaveformPanel.add(waveformPanel, BorderLayout.CENTER);
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
        
        JLabel strategyLabel = new JLabel("Sync Strategy");
        strategyLabel.setFont(strategyLabel.getFont().deriveFont(FONT_SIZE));
        strategyLabel.setHorizontalAlignment(SwingConstants.LEFT);
        strategyLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        controlsPanel.add(strategyLabel, gbc);
        
        gbc.gridx = 1;
        
        JLabel targetTimeLabel = new JLabel("Target Time");
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
	
	private AcceptedDevices devices;
	public JeffGUI(Gateway gateway, DemoPanel demoPanel, AcceptedDevices devices) {
		super(new BorderLayout());
//		try {
//			shutterClip = Manager.createPlayer(new MediaLocator(JeffGUI.class.getResource("shutter.mp3")));
//			player.start();
//			shutterClip = AudioSystem.getClip();
//			AudioInputStream inputStream = AudioSystem.getAudioInputStream(JeffGUI.class.getResourceAsStream("camera-click.wav"));
//			AudioInputStream mp3Stream = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.MPEGLAYER3).getEncoding(), inputStream);
//	        shutterClip.open(inputStream);
//		} catch (LineUnavailableException e1) {
			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (UnsupportedAudioFileException e1) {
			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (IOException e1) {
			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		this.devices = devices;
        this.demoPanel = demoPanel;
		this.gateway = gateway;
		
		
	        
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
				if(imageButtonDown && Strategy.NoSynchronization.equals(Strategy.valueOf(strategiesGroup.getSelection().getActionCommand()))) {
					noSync();
				}
				System.out.println(imageButtonDown);
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
		add(buildXRay(devices), BorderLayout.CENTER);
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
		if(started) {
			started = false;
			cameraPanel.stop();
			gateway.removeListener(this);
		}
	}
	
	public void start() {
		if(!started) {
			demoPanel.getBedLabel().setText("Device Integration Demo");
			demoPanel.getPatientLabel().setText("");
			demoPanel.getPatientLabel().setFont(Font.decode("courier-bold-20"));
			demoPanel.getPatientLabel().setVerticalAlignment(SwingConstants.TOP);
			demoPanel.getPatientLabel().setVerticalTextPosition(SwingConstants.TOP);
			started = true;
			cameraPanel.start();
			gateway.addListener(this);
		}
	}
	
	
	
	public static void main(String[] args) {
		Loader.load(opencv_objdetect.class);
		
		final Gateway gateway = new Gateway();
		final Device device = new DemoApollo(gateway);
		final DemoFrame frame = new DemoFrame("X-Ray Ventilator Synchronization");
		frame.getContentPane().setLayout(new BorderLayout());
		
		
		DemoPanel demoPanel = new DemoPanel();
//		demoPanel = new JPanel();
		
		demoPanel.getBack().setVisible(false);

		frame.getContentPane().add(demoPanel, BorderLayout.CENTER);
		
		demoPanel.getContent().setLayout(new BorderLayout());
//		demoPanel.setLayout(new BorderLayout());
		final JeffGUI jeffGUI = new JeffGUI(gateway, demoPanel, new AcceptedDevices());
		demoPanel.getContent().add(jeffGUI, BorderLayout.CENTER);
		frame.setSize(640,480);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final GetConnected getConnected = new GetConnected(frame, gateway);
		jeffGUI.start();
		getConnected.connect();
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				jeffGUI.stop();
				getConnected.disconnect();
			}
		});
		
		
	}
	
	private static final void regressionUpdate(String name, WaveformUpdate wu, RTRegression rtRegression) {
		final Number[] values = wu.getValues();
		double msPerSample = wu.getMillisecondsPerSample();
		double first_sample = System.currentTimeMillis() - startOfTime - values.length * msPerSample;
		
		for(int i = 0; i < values.length; i++) {
			double x = first_sample+i*msPerSample;
			double y = values[i].doubleValue();
//			System.err.println(""+x+","+y);
			rtRegression.newPoint(x, y);
			
		}
//		System.err.println("slope:"+rtRegression.getRegressedSlope());
	}
	
	private Number frequencyIMV, frequencyIPPV;
	private long inspiratoryTime;
	private long period;
	protected ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	
	private final Callable<Void> freezeCallable = new Callable<Void>() {
		@Override
		public Void call() throws Exception {
			if(imageButtonDown) {
				cameraPanel.freeze(exposureTime.getValue());
//				AudioInputStream inputStream = AudioSystem.getAudioInputStream(JeffGUI.class.getResourceAsStream("camera-click.wav"));
//				AudioInputStream mp3Stream = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.MPEGLAYER3).getEncoding(), inputStream);
//		        shutterClip.open(inputStream);
//				shutterClip.start();
			}
			return null;
		}
	};
	
	private JSlider exposureTime = new JSlider(0, 1000, 0);
	
	private final void noSync() {
		cameraPanel.freeze(exposureTime.getValue());
//		AudioInputStream inputStream;
//		try {
//			inputStream = AudioSystem.getAudioInputStream(JeffGUI.class.getResourceAsStream("camera-click.wav"));
//			shutterClip.open(inputStream);
//		shutterClip.stop();
//		shutterClip.setFramePosition(0);
//			shutterClip.start();
//		} catch (UnsupportedAudioFileException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
////		AudioInputStream mp3Stream = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.MPEGLAYER3).getEncoding(), inputStream);
// catch (LineUnavailableException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
	}
	
	private final void deadReckoning(TargetTime targetTime) {
		long period = 0L;
		
		switch(targetTime) {
		case EndExpiration:
			executor.schedule(freezeCallable, period-50L, TimeUnit.MILLISECONDS);
			break;
		case EndInspiration:
			executor.schedule(freezeCallable, inspiratoryTime-50L, TimeUnit.MILLISECONDS);
			break;
		}
	}
	
	private String source;
	
	@Override
	public void update(IdentifiableUpdate<?> update) {
		if(update == null || update.getSource()==null) {
			return;
		}
		if(null == source || !source.equals(update.getSource())) {
			return;
		}
		Identifier i = update.getIdentifier();
		if(update instanceof WaveformUpdate) {
			WaveformUpdate wu = (WaveformUpdate) update;
			if(Ventilator.FLOW_INSP_EXP.equals(i)) {
//				regressionUpdate("flow:",wu, flow);
//				double slope = flow.getRegressedSlope();
//				
//				if(slope < 0) {
//					dFlow.setBackground(Color.green);
//				} else if(Double.compare(slope, 0.0)==0) {
//					dFlow.setBackground(Color.red);
//				} else {
//					dFlow.setBackground(Color.white);
//				}
//				dFlow.setText(Double.toString(slope));
				wuws.applyUpdate(wu);
			} else if(Ventilator.AIRWAY_PRESSURE.equals(i)) {
//				regressionUpdate("pressure:", wu, airwayPressure);
//				dPressure.setText(Double.toString(airwayPressure.getRegressedSlope()));
			}
		} else if(update instanceof TextUpdate) {
			TextUpdate tu = (TextUpdate) update;
			if(Ventilator.START_INSPIRATORY_CYCLE.equals(i)) {
//				System.out.println("START_INSPIRATORY_CYCLE");
				Strategy strategy = Strategy.valueOf(strategiesGroup.getSelection().getActionCommand());
				TargetTime targetTime = TargetTime.valueOf(targetTimesGroup.getSelection().getActionCommand());
				
				switch(strategy) {
				case DeadReckoning:
					deadReckoning(targetTime);
					break;
				case NoSynchronization:
					break;
				}
			}
		} else if(update instanceof NumericUpdate) {
			NumericUpdate nu = (NumericUpdate) update;
			Number value = nu.getValue();
			if(null == value) {
				return;
			}
			if(Ventilator.FREQUENCY_IMV.equals(i)) {
				frequencyIMV = value;
			} else if(Ventilator.FREQUENCY_IPPV.equals(i)) {
				frequencyIPPV = value;
				period = (long)( 60000.0 / frequencyIPPV.doubleValue() );
				System.out.println("FrequencyIPPV="+frequencyIPPV+" period="+period);
			
			} else if(Ventilator.INSPIRATORY_TIME.equals(i)) {
				inspiratoryTime = (long) (1000.0 * value.doubleValue());
			}
		} else if(update instanceof EnumerationUpdate) {
			EnumerationUpdate eu = (EnumerationUpdate) update;
			if(ConnectedDevice.STATE.equals(i)) {
				ConnectedDevice.State state = (State) eu.getValue();
				demoPanel.getPatientLabel().setText(state.toString());
				switch(state) {
				case Connected:
					demoPanel.getPatientLabel().setForeground(normalGreen);
					break;
				default:
					demoPanel.getPatientLabel().setForeground(alertPink);
					break;
				}
			}
		}
	}
	private static final Color alertPink = new Color(200, 20, 0);
	private static final Color normalGreen = new Color(20, 200, 20);
}
