package org.mdpnp.guis.swing;

import ice.Numeric;
import ice.SampleArray;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformPanelFactory;
import org.mdpnp.guis.waveform.WaveformUpdateWaveformSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.subscription.SampleInfo;

@SuppressWarnings("serial")
public class VentilatorPanel extends DevicePanel {
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
		
		JPanel upper = new JPanel(new GridLayout(3,1));
		upper.setOpaque(false);
		
		upper.add(flowPanel.asComponent());
		upper.add(pressurePanel.asComponent());
		upper.add(co2Panel.asComponent());
		
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
	

	public VentilatorPanel() {
		super();
		buildComponents();
		flowPanel.setSource(flowWave);
		pressurePanel.setSource(pressureWave);
		co2Panel.setSource(etco2Wave);
	}
		
	private final WaveformUpdateWaveformSource flowWave = new WaveformUpdateWaveformSource();
	private final WaveformUpdateWaveformSource pressureWave = new WaveformUpdateWaveformSource();
	private final WaveformUpdateWaveformSource etco2Wave = new WaveformUpdateWaveformSource();
	

	@SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(VentilatorPanel.class);

	public static boolean supported(Set<Integer> identifiers) {
		return identifiers.contains(ice.MDC_PRESS_AWAY.VALUE) || identifiers.contains(ice.MDC_CAPNOGRAPH.VALUE);
	}


    @Override
    public void numeric(Numeric numeric, SampleInfo sampleInfo) {
        // TODO handle end tidal CO2
    }

    private final Date date = new Date();
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
        date.setTime(sampleInfo.source_timestamp.sec*1000L + sampleInfo.source_timestamp.nanosec / 1000000L);
        time.setText(dateFormat.format(date));
    }

}
