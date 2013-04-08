package org.mdpnp.apps.gui.swing;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.mdpnp.apps.gui.waveform.WaveformUpdateWaveformSource;
import org.mdpnp.apps.gui.waveform.swing.SwingWaveformPanel;
import org.mdpnp.comms.Gateway;
import org.mdpnp.comms.IdentifiableUpdate;
import org.mdpnp.comms.Identifier;
import org.mdpnp.comms.data.enumeration.EnumerationUpdate;
import org.mdpnp.comms.data.numeric.Numeric;
import org.mdpnp.comms.data.numeric.NumericUpdate;
import org.mdpnp.comms.data.text.TextUpdate;
import org.mdpnp.comms.data.waveform.WaveformUpdate;
import org.mdpnp.comms.nomenclature.Capnograph;
import org.mdpnp.comms.nomenclature.ConnectedDevice;
import org.mdpnp.comms.nomenclature.PulseOximeter;
import org.mdpnp.comms.nomenclature.Ventilator;
import org.mdpnp.comms.nomenclature.ConnectedDevice.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VentilatorPanel extends DevicePanel {
	private JLabel nameLabel, guidLabel;
//	private JLabel percent_oxygen, peep, percent_oxygenLabel, peepLabel, nameLabel, guidLabel;
//	private JPanel percent_oxygenPanel, peepPanel, percent_oxygenBounds, peepBounds;
//	private JPanel percent_oxygenPanel, peepPanel, percent_oxygenBounds, peepBounds;
	private SwingWaveformPanel flowPanel, pressurePanel, co2Panel; // , pulsePanel;
	private JLabel etco2;
	private JLabel time, connected;
	private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public void setName(String name) {
		if(built && name != null) {
			this.nameLabel.setText(name);
		}
	}
	
	public void setGuid(String guid) {
		if(built && guid != null) {
			this.guidLabel.setText(guid);
		}
	}

	
	protected void buildComponents() {
//		percent_oxygenBounds = new JPanel();
//		percent_oxygenBounds.setOpaque(false);
//		percent_oxygenBounds.setLayout(new GridLayout(1, 1));
//		percent_oxygenBounds.add(percent_oxygenLabel = new JLabel("%O2"));
//
//		
//		percent_oxygenPanel = new JPanel();
//		percent_oxygenPanel.setOpaque(false);
//		percent_oxygenPanel.setLayout(new BorderLayout());
//		percent_oxygenPanel.add(percent_oxygen = new JLabel("---"), BorderLayout.CENTER);
//		percent_oxygen.setHorizontalAlignment(JLabel.RIGHT);
//		percent_oxygen.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
//		percent_oxygenPanel.add(percent_oxygenBounds, BorderLayout.EAST);
//		
//		peepBounds = new JPanel();
//		peepBounds.setOpaque(false);
//		peepBounds.setLayout(new GridLayout(1,1));
//
//		peepBounds.add(peepLabel = new JLabel("PEEP"));
//		
//		peepPanel = new JPanel();
//		peepPanel.setOpaque(false);
//		peepPanel.setLayout(new BorderLayout());
//		peepPanel.add(peep = new JLabel("---"), BorderLayout.CENTER);
//		peep.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
//		peep.setHorizontalAlignment(JLabel.RIGHT);
//		peepPanel.add(peepBounds, BorderLayout.EAST);
		
		
//		JPanel bigger = new JPanel();
//		bigger.setOpaque(false);
//		bigger.setLayout(new GridLayout(1, 2));
		flowPanel = new SwingWaveformPanel();
		pressurePanel = new SwingWaveformPanel();
		co2Panel = new SwingWaveformPanel();
		flowPanel.setOpaque(false);
		pressurePanel.setOpaque(false);
		co2Panel.setOpaque(false);
//		pulsePanel = new WaveformPanel();
		
//		pulsePanel.setOpaque(false);
		
//		bigger.add(plethPanel);
		
		JPanel upper = new JPanel();
		upper.setOpaque(false);
		upper.setLayout(new GridBagLayout());
//		upper.setLayout(new GridLayout(2, 1));
		
		GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0);

//		gbc.gridheight = 2;
//		gbc.gridwidth = 1;
//		gbc.weightx = 2.0;
//		upper.add(pulsePanel, gbc);
		
//		gbc.gridx = 1;
		upper.add(flowPanel, gbc);
		
		gbc.gridy = 1;
		upper.add(pressurePanel, gbc);
		
		gbc.gridy = 2;
		upper.add(co2Panel, gbc);
		
//		gbc.weightx = 0.1;
//		gbc.gridheight = 1;
//		gbc.gridwidth = 1;
//		gbc.gridx = 1;
//
//		upper.add(percent_oxygenPanel, gbc);
//		gbc.gridy = 1;
//		upper.add(peepPanel, gbc);
		
//		bigger.add(upper);
		
		setLayout(new BorderLayout());
		add(upper, BorderLayout.CENTER);
		
		JPanel lower = new JPanel();
		lower.setOpaque(false);
		lower.setLayout(new GridLayout(2, 1));
		
		
		lower.add(time = new JLabel("TIME"));
		time.setHorizontalAlignment(JLabel.RIGHT);
		lower.add(connected = new JLabel("ConnectState"));
		connected.setHorizontalAlignment(JLabel.RIGHT);
		add(lower, BorderLayout.SOUTH);
		
		
		JPanel headers = new JPanel();
		headers.setLayout(new GridLayout(3,1));
		headers.setOpaque(false);
		headers.add(nameLabel = new JLabel("NAME"));
		headers.add(guidLabel = new JLabel("GUID"));
		headers.add(etco2 = new JLabel("ETCO2"));
		add(headers, BorderLayout.NORTH);
	
		nameLabel.setHorizontalAlignment(JLabel.RIGHT);
		guidLabel.setHorizontalAlignment(JLabel.RIGHT);
		etco2.setHorizontalAlignment(JLabel.RIGHT);
		
	}
	
	
	protected static float maxFontSize(JLabel label) {
		Font labelFont = label.getFont();
		String labelText = label.getText();

		int stringWidth = label.getFontMetrics(labelFont).stringWidth(labelText);
		int stringHeight = label.getFontMetrics(labelFont).getHeight();
		int componentWidth = label.getWidth();
		int componentHeight = label.getHeight();

		// Find out how much the font can grow in width.
		double widthRatio = (double)componentWidth / (double)stringWidth;
		double heightRatio = 1.0 * componentHeight / stringHeight;

		double smallerRatio = Math.min(widthRatio, heightRatio) - 0.5f;
		
		return (float) (labelFont.getSize2D() * smallerRatio);
	}
	
	protected static void resizeFontToFill(JLabel... label) {
		float fontSize = Float.MAX_VALUE;
		
		for(JLabel l : label) {
			fontSize = Math.min(fontSize, maxFontSize(l));
		}
		
		for(JLabel l : label) {
			l.setFont(l.getFont().deriveFont(fontSize));
		}
	}
	
	
	
	@Override
	protected void processComponentEvent(ComponentEvent e) {
		if(e.getID() == ComponentEvent.COMPONENT_RESIZED) {
//			resizeFontToFill(percent_oxygen, peep);
		}
		super.processComponentEvent(e);
	}
	private boolean built = false;
	public VentilatorPanel(Gateway gateway, String source) {
		super(gateway, source);
		buildComponents();
		flowPanel.setSource(flowWave);
		pressurePanel.setSource(pressureWave);
		co2Panel.setSource(etco2Wave);
//		pulsePanel.setSource(pulseWave);
//		pulsePanel.cachingSource().setFixedTimeDomain(120000L);
		enableEvents(ComponentEvent.COMPONENT_RESIZED);
		built = true;
		
		registerAndRequestRequiredIdentifiedUpdates();
	}
	
	@Override
	public Collection<Identifier> requiredIdentifiedUpdates() {
		List<Identifier> ids = new ArrayList<Identifier>(super.requiredIdentifiedUpdates());
		ids.addAll(Arrays.asList(new Identifier[] {ConnectedDevice.STATE, ConnectedDevice.CONNECTION_INFO}));
		return ids;
	}
	
//	@Override
//	public void setModel(Device device) {
//		if(device instanceof PulseOximeter) {
//			setModel((PulseOximeter)device);
//		}
//	}
	
//	private final WaveformUpdateWaveformSource plethWave = new WaveformUpdateWaveformSource();
//	private final NumericUpdateWaveformSource flowWave = new NumericUpdateWaveformSource(16L);
	private final WaveformUpdateWaveformSource flowWave = new WaveformUpdateWaveformSource();
	private final WaveformUpdateWaveformSource pressureWave = new WaveformUpdateWaveformSource();
	private final WaveformUpdateWaveformSource etco2Wave = new WaveformUpdateWaveformSource();
	
	public void setModel(PulseOximeter model) {
//		if(this.device != null) {
//			plethPanel.setSource(null);
//			
//			if(this.device instanceof ConnectedDevice) {
//				((ConnectedDevice)this.device).removeListener((ConnectedDeviceListener)this);
//			}
//
//		}
//		super.setModel(model);
//
//		if(this.device != null) {
//			plethPanel.setSource(plethWave);
//			if(this.device instanceof ConnectedDevice) {
//				connectedDevice((ConnectedDevice)this.device);
//				((ConnectedDevice)this.device).addListener((ConnectedDeviceListener)this);
//			}
//		}
	}
		
	public static void main(String[] args) {
		JFrame frame = new JFrame("TEST");
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Gateway gateway = new Gateway();
		frame.getContentPane().add(new PulseOximeterPanel(gateway, ""));
		
		frame.setSize(640, 480);
		frame.setVisible(true);
	}

	private ConnectedDevice.State connectedState;
	private String connectionInfo;
	
//	@Override
//	public void connectedDevice(ConnectedDevice device) {
//		String connectionInfo = device.getConnectionInfo();
//		connected.setText(""+device.getState()+(null==connectionInfo?"":(" ("+connectionInfo+")")));
//	}

	private final void setInt(IdentifiableUpdate<?> nu, Numeric numeric, JLabel label, String def) {
		if(numeric.equals(nu.getIdentifier())) {
			
			setInt(((NumericUpdate)nu).getValue(), label, def);
			if(!label.isVisible()) {
				label.setVisible(true);
			}
		}
	}

	private static final Logger log = LoggerFactory.getLogger(VentilatorPanel.class);
	@Override
	protected void doUpdate(IdentifiableUpdate<?> n) {
		if(!built) {
			return;
		}
//		setInt(n, Ventilator.TIDAL_VOLUME, this.peep, "---");
//		setInt(n, Ventilator.FREQUENCY_IPPV, this.percent_oxygen, "---");

//		if(Ventilator.O2_INSP_EXP.equals(n.getIdentifier())) {
//			Date date = ((NumericUpdate)n).getUpdateTime();
//			this.time.setText(null == date ? "---" : dateFormat.format(date));
//		}
		if(Ventilator.FLOW_INSP_EXP.equals(n.getIdentifier())) {
			WaveformUpdate wu = (WaveformUpdate) n;
//				System.out.println("WaveformUpdate:"+wu);
			flowWave.applyUpdate(wu);
			
		} else if(Ventilator.AIRWAY_PRESSURE.equals(n.getIdentifier())) {
			WaveformUpdate wu = (WaveformUpdate) n;
//		System.out.println("WaveformUpdate:"+wu);
			pressureWave.applyUpdate(wu);
//			log.trace(wu.toString());
		} else if(ConnectedDevice.STATE.equals(n.getIdentifier())) {
			connectedState = (State) ((EnumerationUpdate)n).getValue();
			connected.setText(""+connectedState+(null==connectionInfo?"":(" ("+connectionInfo+")")));
		} else if(ConnectedDevice.CONNECTION_INFO.equals(n.getIdentifier())) {
			connectionInfo = ((TextUpdate)n).getValue();
			connected.setText(""+connectedState+(null==connectionInfo?"":(" ("+connectionInfo+")")));
		} else if(PulseOximeter.PULSE.equals(n.getIdentifier())) {
			NumericUpdate nu = (NumericUpdate) n;
//			pulseWave.applyUpdate(nu);
		} else if(Ventilator.END_TIDAL_CO2_MMHG.equals(n.getIdentifier())) {
			NumericUpdate nu = (NumericUpdate) n;
			etco2.setText(""+nu.getValue());
		} else if(Ventilator.EXP_CO2_MMHG.equals(n.getIdentifier())) {
			WaveformUpdate wu = (WaveformUpdate) n;
			etco2Wave.applyUpdate(wu);
		} else if(Capnograph.CAPNOGRAPH.equals(n.getIdentifier())) {
			WaveformUpdate wu = (WaveformUpdate) n;
			etco2Wave.applyUpdate(wu);
		}
	}
	public static boolean supported(Set<Identifier> identifiers) {
		return identifiers.contains(Ventilator.AIRWAY_PRESSURE) || identifiers.contains(Capnograph.CAPNOGRAPH);
	}

	@Override
	public void setIcon(Image image) {
		// TODO Auto-generated method stub
		
	}
}
