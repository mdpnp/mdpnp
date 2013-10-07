package org.mdpnp.guis.swing;

import ice.InfusionStatus;
import ice.Numeric;
import ice.SampleArray;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Set;

import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformUpdateWaveformSource;
import org.mdpnp.guis.waveform.swing.SwingWaveformPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.subscription.SampleInfo;

@SuppressWarnings("serial")
public class MultiPulseOximeterPanel extends DevicePanel {
    private WaveformPanel[] plethPanel;
    private final WaveformUpdateWaveformSource[] plethWave;

    private static final int N = 12;
    protected void buildComponents() {
//        WaveformPanelFactory fact = new WaveformPanelFactory();
        this.plethPanel = new WaveformPanel[N];
        setLayout(new GridLayout(N,1));
        for(int i = 0; i < N; i++) {
//            plethPanel[i] = fact.createWaveformPanel();
            plethPanel[i] = new SwingWaveformPanel();
        }

        setForeground(Color.green);
        setBackground(Color.black);
        setOpaque(true);
    }

    public MultiPulseOximeterPanel() {
        setBackground(Color.black);
        setOpaque(true);
        buildComponents();
        plethWave = new WaveformUpdateWaveformSource[N];
        for(int i = 0 ; i < N; i++) {
            plethWave[i] = new WaveformUpdateWaveformSource();
            plethPanel[i].setSource(plethWave[i]);
            plethPanel[i].start();
        }
    }

    @Override
    public void destroy() {
        for(int i = 0; i < N; i++) {
            plethPanel[i].setSource(null);
            plethPanel[i].stop();
        }
        super.destroy();
    }

    public static boolean supported(Set<String> names) {
        return names.contains(rosetta.MDC_PULS_OXIM_PLETH.VALUE);
    }
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(PulseOximeterPanel.class);
//    private final Date date = new Date();

    @Override
    public void sampleArray(SampleArray sampleArray, SampleInfo sampleInfo) {
        if(sampleArray.instance_id>=0&&sampleArray.instance_id<N) {
            plethWave[sampleArray.instance_id].applyUpdate(sampleArray, sampleInfo);
        }
    }

    @Override
    public void infusionStatus(InfusionStatus infusionStatus, SampleInfo sampleInfo) {

    }

    @Override
    public void connected() {
        for(WaveformUpdateWaveformSource wuws : plethWave) {
            wuws.reset();
        }
    }

    @Override
    public void numeric(Numeric numeric, SampleInfo sampleInfo) {
        // NO OP
    }
}
