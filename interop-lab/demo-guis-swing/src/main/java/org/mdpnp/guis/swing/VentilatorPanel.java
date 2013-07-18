package org.mdpnp.guis.swing;

import ice.Numeric;
import ice.SampleArray;

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

import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformPanelFactory;
import org.mdpnp.guis.waveform.WaveformUpdateWaveformSource;
import org.mdpnp.guis.waveform.swing.SwingWaveformPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.subscription.SampleInfo;

public class VentilatorPanel extends DevicePanel {
//	private JLabel percent_oxygen, peep, percent_oxygenLabel, peepLabel, nameLabel, guidLabel;
//	private JPanel percent_oxygenPanel, peepPanel, percent_oxygenBounds, peepBounds;
//	private JPanel percent_oxygenPanel, peepPanel, percent_oxygenBounds, peepBounds;
	private WaveformPanel flowPanel, pressurePanel, co2Panel; // , pulsePanel;
	private JLabel etco2;
	private JLabel time; 
	private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
	
	@Override
	public void destroy() {
	    flowPanel.stop();
        pressurePanel.stop();
        co2Panel.stop();
	    super.destroy();
	}
	protected void buildComponents() {
	    WaveformPanelFactory fact = new WaveformPanelFactory();
		flowPanel = fact.createWaveformPanel();
		pressurePanel = fact.createWaveformPanel();
		co2Panel = fact.createWaveformPanel();

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
		upper.add(flowPanel.asComponent(), gbc);
		
		gbc.gridy = 1;
		upper.add(pressurePanel.asComponent(), gbc);
		
		gbc.gridy = 2;
		upper.add(co2Panel.asComponent(), gbc);
		
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
		
		add(time = new JLabel("TIME"), BorderLayout.SOUTH);
		time.setHorizontalAlignment(JLabel.RIGHT);
		
		add(etco2 = new JLabel("etCO\u2082"), BorderLayout.NORTH);

		etco2.setHorizontalAlignment(JLabel.RIGHT);
		flowPanel.start();
        pressurePanel.start();
        co2Panel.start();
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
	public VentilatorPanel() {
		super();
		buildComponents();
		flowPanel.setSource(flowWave);
		pressurePanel.setSource(pressureWave);
		co2Panel.setSource(etco2Wave);
//		pulsePanel.setSource(pulseWave);
//		pulsePanel.cachingSource().setFixedTimeDomain(120000L);
		enableEvents(ComponentEvent.COMPONENT_RESIZED);
		built = true;
	}
		
//	private final WaveformUpdateWaveformSource plethWave = new WaveformUpdateWaveformSource();
//	private final NumericUpdateWaveformSource flowWave = new NumericUpdateWaveformSource(16L);
	private final WaveformUpdateWaveformSource flowWave = new WaveformUpdateWaveformSource();
	private final WaveformUpdateWaveformSource pressureWave = new WaveformUpdateWaveformSource();
	private final WaveformUpdateWaveformSource etco2Wave = new WaveformUpdateWaveformSource();
	

	private static final Logger log = LoggerFactory.getLogger(VentilatorPanel.class);

	public static boolean supported(Set<Integer> identifiers) {
		return identifiers.contains(ice.MDC_PRESS_AWAY.VALUE) || identifiers.contains(ice.MDC_CAPNOGRAPH.VALUE);
	}


    @Override
    public void numeric(Numeric numeric, SampleInfo sampleInfo) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void sampleArray(SampleArray sampleArray, SampleInfo sampleInfo) {
        switch(sampleArray.name) {
        case ice.MDC_FLOW_AWAY.VALUE:
            flowWave.applyUpdate(sampleArray);
            break;
        case ice.MDC_PRESS_AWAY.VALUE:
            pressureWave.applyUpdate(sampleArray);
            break;
        case ice.MDC_CAPNOGRAPH.VALUE:
            etco2Wave.applyUpdate(sampleArray);
            break;
        }
    }

}
