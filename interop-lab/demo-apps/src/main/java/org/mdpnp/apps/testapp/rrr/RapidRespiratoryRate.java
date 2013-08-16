package org.mdpnp.apps.testapp.rrr;

import ice.Numeric;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.util.Arrays;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.mdpnp.apps.testapp.co2.Capno;
import org.mdpnp.apps.testapp.co2.CapnoListModel;
import org.mdpnp.apps.testapp.co2.CapnoModel;
import org.mdpnp.apps.testapp.co2.CapnoModelListener;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformUpdateWaveformSource;
import org.mdpnp.guis.waveform.swing.SwingWaveformPanel;

public class RapidRespiratoryRate extends JPanel implements CapnoModelListener {
    
    private final JList capnoSources = new JList();
    private final JPanel controlPanel = new JPanel();
    private final JLabel rrLabel = new JLabel("???");
    private final WaveformUpdateWaveformSource wuws = new WaveformUpdateWaveformSource();
    private final WaveformPanel wavePanel = new SwingWaveformPanel(wuws);
    private final JSlider thresholdSlider = new JSlider(0, 100, 20);
    private final JCheckBox device = new JCheckBox("Create Device");

    private final class RespiratoryRateDevice extends AbstractDevice {

        @Override
        protected String iconResourceName() {
            return "rrr.png";
        }
        
        public RespiratoryRateDevice(int domainId, EventLoop eventLoop) {
            super(domainId, eventLoop);
            deviceIdentity.manufacturer = "";
            deviceIdentity.model = "Rapid Respiratory Rate";
            deviceIdentity.serial_number = "1234";
            AbstractSimulatedDevice.randomUDI(deviceIdentity);
        }
        
        private boolean resumed = false;
        public void resume() {
            deviceIdentityHandle = deviceIdentityWriter.register_instance(deviceIdentity);
            deviceIdentityWriter.write(deviceIdentity, deviceIdentityHandle);
            resumed = true;
        }
        
        public void pause() {
            resumed = false;
            deviceIdentityWriter.unregister_instance(deviceIdentity, deviceIdentityHandle);
            deviceIdentityHandle = null;
        }
        
        private InstanceHolder<Numeric> rate;
        void updateRate(float rate) {
            if(resumed) {
                this.rate = numericSample(this.rate, (int)Math.round(rate), ice.Physio._MDC_RESP_RATE);
            }
        }
        
    }
    
    private RespiratoryRateDevice rrDevice;
    
    public RapidRespiratoryRate(int domainId, EventLoop eventLoop) {
        super(new GridLayout(2,2));
        rrDevice = new RespiratoryRateDevice(domainId, eventLoop);
        enableEvents(ComponentEvent.COMPONENT_EVENT_MASK);
        add(capnoSources);
        add(controlPanel);
        thresholdSlider.setPaintLabels(true);
        thresholdSlider.setPaintTicks(true);
        thresholdSlider.setLabelTable(thresholdSlider.createStandardLabels(10, 0));
        controlPanel.add(thresholdSlider);
        controlPanel.add(device);
        add(wavePanel.asComponent());
        add(rrLabel);
        device.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(device.isSelected()) {
                    if(rrDevice != null) {
                        rrDevice.resume();
                    }
                } else {
                    if(rrDevice != null) {
                        rrDevice.pause();
                    }
                }
            }
            
        });
        capnoSources.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                if(value != null && value instanceof Capno) {
                    String udi = ((Capno)value).getSampleArray().universal_device_identifier;
                    VitalModel model = RapidRespiratoryRate.this.vitalModel;
                    if(model != null) {
                        ice.DeviceIdentity di = model.getDeviceIdentity(udi);
                        if(null != di) {
                            value = di.manufacturer + " " + di.model;
                        }
                    }
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
    }
    
    private CapnoModel model;
    private VitalModel vitalModel;
    public void setModel(CapnoModel model, VitalModel vitalModel) {
        this.vitalModel = vitalModel;
        String selectedUdi = null;
        Object selected = capnoSources.getSelectedValue();
        if(null != selected && selected instanceof Capno) {
            selectedUdi  =((Capno)selected).getSampleArray().universal_device_identifier;
        }
        capnoSources.setModel(null==model?new DefaultListModel():new CapnoListModel(model));
        if(null != selectedUdi && model != null) {
            for(int i = 0; i < model.getCount(); i++) {
                if(selectedUdi.equals(model.getCapno(i).getSampleArray().universal_device_identifier)) {
                    capnoSources.setSelectedValue(model.getCapno(i), true);
                }
            }
        }
        if(this.model != null) {
            this.model.removeCapnoListener(this);
        }
        this.model = model;
        if(this.model != null) {
            this.model.addCapnoListener(this);
        }
    }
    @Override
    public void capnoAdded(CapnoModel model, Capno capno) {
        
    }
    @Override
    public void capnoRemoved(CapnoModel model, Capno capno) {
        
    }
    
    private final static int HISTORY = 60;
    private long[] times = new long[HISTORY];
    private float[] values = new float[HISTORY];
    private int current;
    
    private final int minus(int x) {
        return --x < 0 ? (HISTORY-1) : x;
    }
    
    private final int plus(int x) {
        return plus(x, 1);
    }
    
    private final int plus(int x, int delta) {
        return (x+=delta) >= HISTORY ? (x % HISTORY) : x;
    }
    
    private Long lastBreathTime;
    private Float highWaterMark;
    
    private float high = Float.MIN_VALUE, low = Float.MAX_VALUE;
    
    @Override
    public void capnoChanged(CapnoModel model, Capno capno) {
        if(null != capno && capno.equals(capnoSources.getSelectedValue())) {
            wuws.applyUpdate(capno.getSampleArray());
            // sample arrays ... why?  it's just obnoxious having data samples
            // artificially batched like this
            long src_time = capno.getSampleInfo().source_timestamp.sec * 1000L + capno.getSampleInfo().source_timestamp.nanosec / 1000000L;
            long msPerSample = capno.getSampleArray().millisecondsPerSample;
            final int sz  =capno.getSampleArray().values.size();
            int startedAtCurrent = current;
            for(int i = 0; i  < sz; i++) {
                times[current] = src_time + (i - sz) * msPerSample;
                values[current] = capno.getSampleArray().values.getFloat(i);
                if(values[minus(current)] <= thresholdSlider.getValue() && values[current] > thresholdSlider.getValue()) {
                    if(lastBreathTime != null) {
                        double rr = 60000.0 / (times[current] - lastBreathTime);
                        rrLabel.setText(Double.toString(rr));
                        if(rrDevice != null) {
                            rrDevice.updateRate((float) rr);
                        }
                    }
                    lastBreathTime = times[current];
                }
                current = plus(current);
            }
            
            long mostRecentTime = times[minus(current)];
            long oneHalfSecondAgo = times[minus(current)]-500L;
            
            
            // process each point as if it were coming in anew like real data samples would
            for(int localCurrent = startedAtCurrent; localCurrent < current; localCurrent = plus(localCurrent)) {
//                for(int i = minus(localCurrent); i != localCurrent; i = minus(i)) {
//                    if(times[i] != 0L && times[i] <= oneHalfSecondAgo) {
//                        // found it .. a reference point at least some distance back in time
//    //                    System.out.println("From " + times[i] + " to " + mostRecentTime + " value from " + values[i] + " to " + values[minus(current)]);
//                        double rateChange = 1.0 * (values[minus(localCurrent)] - values[i]) / (mostRecentTime - times[i]);
//                        low = (float) Math.min(low, rateChange);
//                        high = (float) Math.max(high, rateChange);
//                        rrLabel.setText(""+(1000.0*low) + " / " + (1000.0*high)+ " / "+Double.toString(1000.0*rateChange));
//                        break;
//                    }
//                }
            }
//            if(current == 0) {
//                System.err.println(Arrays.toString(values));
//            }
            
        }
    }
    
}
