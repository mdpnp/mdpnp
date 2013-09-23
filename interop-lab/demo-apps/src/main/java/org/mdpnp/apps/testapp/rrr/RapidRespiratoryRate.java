package org.mdpnp.apps.testapp.rrr;

import ice.Numeric;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;

import org.mdpnp.apps.testapp.co2.Capno;
import org.mdpnp.apps.testapp.co2.CapnoListModel;
import org.mdpnp.apps.testapp.co2.CapnoModel;
import org.mdpnp.apps.testapp.co2.CapnoModelListener;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.math.DCT;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformUpdateWaveformSource;
import org.mdpnp.guis.waveform.swing.SwingWaveformPanel;

@SuppressWarnings("serial")
public class RapidRespiratoryRate extends JPanel implements CapnoModelListener {

    @SuppressWarnings("rawtypes")
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
            writeDeviceIdentity();
        }

        private InstanceHolder<Numeric> rate;
        void updateRate(float rate) {
            // TODO clearly a synchronization issue here.
            // enforce a singular calling thread or synchronize accesses
            this.rate = numericSample(this.rate, (int)Math.round(rate), ice.Physio._MDC_RESP_RATE);
        }
    }

    private RespiratoryRateDevice rrDevice;
    private final EventLoop eventLoop;

    @SuppressWarnings("unchecked")
    public RapidRespiratoryRate(final int domainId, final EventLoop eventLoop) {
        super(new GridLayout(2,2));
        this.eventLoop = eventLoop;
//        rrDevice = new RespiratoryRateDevice(domainId, eventLoop);
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
                    if(rrDevice == null) {
                        rrDevice = new RespiratoryRateDevice(domainId, eventLoop);
                    }
                } else {
                    if(rrDevice != null) {
                        rrDevice.shutdown();
                        rrDevice = null;
                    }
                }
            }

        });
        capnoSources.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                String udi = null;
                if(value != null && value instanceof Capno) {
                    udi = ((Capno)value).getSampleArray().universal_device_identifier;
                    VitalModel model = RapidRespiratoryRate.this.vitalModel;
                    if(model != null) {
                        ice.DeviceIdentity di = model.getDeviceIdentity(udi);
                        if(null != di) {
                            value = di.manufacturer + " " + di.model;
                        }
                    }
                }
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(null != udi && c instanceof JLabel && vitalModel != null) {
                    ((JLabel)c).setIcon(vitalModel.getDeviceIcon(udi));
                }
                return c;
            }
        });
    }

    private CapnoModel model;
    private VitalModel vitalModel;
    @SuppressWarnings({ "rawtypes", "unchecked" })
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
        capnoSources.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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

    private final static int HISTORY = 200;
    private long[] times = new long[HISTORY];
    private float[] values = new float[HISTORY];
    private float[] coeffs = new float[HISTORY];
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
//    private Float highWaterMark;

//    private float high = Float.MIN_VALUE, low = Float.MAX_VALUE;


    private double rr;

    private final Runnable updateRate = new Runnable() {
        public void run() {
            if(rrDevice != null) {
                rrDevice.updateRate((float) Math.round(rr));
            }
        }
    };

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
                        rr = 60000.0 / (times[current] - lastBreathTime);
//                        rrLabel.setText(Double.toString(rr));
//                        if(rrDevice != null) {
//                            rrDevice.updateRate((float) rr);
//                        }
                    }
                    lastBreathTime = times[current];
                }
                current = plus(current);
            }

//            long mostRecentTime = times[minus(current)];
//            long oneHalfSecondAgo = times[minus(current)]-500L;
            DCT.dct(values, current, coeffs, 10);
            double weighted = 0.0, sum = 0.0;
            double max = Double.MIN_VALUE;
            int index = 0;

            for(int i = 1; i < 10; i++) {
                weighted += i * Math.abs(coeffs[i]);
                sum += Math.abs(coeffs[i]);
                if(Math.abs(coeffs[i]) > max) {
                    index = i;
                    max = Math.abs(coeffs[i]);
                }
            }
            double weighted_C = weighted / sum;
            double bpm = (Math.PI * HISTORY * msPerSample / 1000.0) / (weighted_C + 2);
            bpm = 60.0 / bpm;
            rrLabel.setText(Double.toString(bpm) + "  " + weighted_C + "  " + index + "  " + rr);
            this.rr = bpm;
            eventLoop.doLater(updateRate);
//            if(rrDevice != null) {
//                rrDevice.updateRate((float) bpm);
//            }


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
