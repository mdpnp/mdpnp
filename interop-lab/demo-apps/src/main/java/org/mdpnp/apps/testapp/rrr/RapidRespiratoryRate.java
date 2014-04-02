/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.apps.testapp.rrr;

import ice.Numeric;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.mdpnp.apps.testapp.DeviceListCellRenderer;
import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.guis.waveform.SampleArrayWaveformSource;
import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.swing.SwingWaveformPanel;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.SampleArrayInstanceModel;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class RapidRespiratoryRate extends JPanel implements ListDataListener {

    private final JList<ice.SampleArray> capnoSources = new JList<ice.SampleArray>();
    private final JPanel controlPanel = new JPanel();
    private final JLabel rrLabel = new JLabel("???");
    private final WaveformPanel wavePanel = new SwingWaveformPanel();
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
            deviceIdentity.model = "Respiratory Rate Calc";
            deviceIdentity.serial_number = "1234";
            AbstractSimulatedDevice.randomUDI(deviceIdentity);
            writeDeviceIdentity();
        }

        private InstanceHolder<Numeric> rate;

        void updateRate(float rate) {
            // TODO clearly a synchronization issue here.
            // enforce a singular calling thread or synchronize accesses
            this.rate = numericSample(this.rate, (int) Math.round(rate), rosetta.MDC_RESP_RATE.VALUE, null);
        }
    }

    private RespiratoryRateDevice rrDevice;
    private final EventLoop eventLoop;

    @SuppressWarnings("unchecked")
    public RapidRespiratoryRate(final int domainId, final EventLoop eventLoop, DeviceListCellRenderer deviceCellRenderer) {
        super(new GridLayout(2, 2));
        this.eventLoop = eventLoop;
        // rrDevice = new RespiratoryRateDevice(domainId, eventLoop);
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
        capnoSources.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                ice.SampleArray sa = capnoSources.getSelectedValue();
                if(null == sa) {
                    wavePanel.setSource(null);
                } else {
                    wavePanel.setSource(new SampleArrayWaveformSource(model.getReader(), sa));
                }
            }
            
        });
        device.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (device.isSelected()) {
                    if (rrDevice == null) {
                        rrDevice = new RespiratoryRateDevice(domainId, eventLoop);
                    }
                } else {
                    if (rrDevice != null) {
                        rrDevice.shutdown();
                        rrDevice = null;
                    }
                }
            }

        });
        capnoSources.setCellRenderer(deviceCellRenderer);   
        wavePanel.start();
    }

    private SampleArrayInstanceModel model;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setModel(SampleArrayInstanceModel model) {
        String selectedUdi = null;
        Object selected = capnoSources.getSelectedValue();
        if (null != selected && selected instanceof ice.SampleArray) {
            selectedUdi = ((ice.SampleArray) selected).unique_device_identifier;
        }

        capnoSources.setModel(null == model ? new DefaultListModel() : model);
        if (null != selectedUdi && model != null) {
            for (int i = 0; i < model.getSize(); i++) {
                if (selectedUdi.equals(model.getElementAt(i).unique_device_identifier)) {
                    capnoSources.setSelectedValue(model.getElementAt(i), true);
                }
            }
        }

        capnoSources.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        if (this.model != null) {
            this.model.removeListDataListener(this);
        }
        this.model = model;
        if (this.model != null) {
            this.model.addListDataListener(this);
        }
    }

    private final static int HISTORY = 200;
    private long[] times = new long[HISTORY];
    private float[] values = new float[HISTORY];
    private float[] coeffs = new float[HISTORY];
    private int current;

    private final int minus(int x) {
        return --x < 0 ? (HISTORY - 1) : x;
    }

    private final int plus(int x) {
        return plus(x, 1);
    }

    private final int plus(int x, int delta) {
        return (x += delta) >= HISTORY ? (x % HISTORY) : x;
    }

//    private Long lastBreathTime;
    // private Float highWaterMark;

    // private float high = Float.MIN_VALUE, low = Float.MAX_VALUE;

    private double rr;

    private final Runnable updateRate = new Runnable() {
        public void run() {
            if (rrDevice != null) {
                rrDevice.updateRate((float) Math.round(rr));
            }
        }
    };

//    @Override
//    public void capnoChanged(CapnoModel model, Capno capno) {
//        if (null != capno && capno.equals(capnoSources.getSelectedValue())) {
////            wuws.applyUpdate(capno.getSampleArray(), capno.getSampleInfo());
//            // sample arrays ... why? it's just obnoxious having data samples
//            // artificially batched like this
//            long src_time = capno.getSampleInfo().source_timestamp.sec * 1000L + capno.getSampleInfo().source_timestamp.nanosec / 1000000L;
//            long msPerSample = capno.getSampleArray().millisecondsPerSample;
//            final int sz = capno.getSampleArray().values.userData.size();
//            int startedAtCurrent = current;
//            for (int i = 0; i < sz; i++) {
//                times[current] = src_time + (i - sz) * msPerSample;
//                values[current] = capno.getSampleArray().values.userData.getFloat(i);
//                if (values[minus(current)] <= thresholdSlider.getValue() && values[current] > thresholdSlider.getValue()) {
//                    if (lastBreathTime != null) {
//                        rr = 60000.0 / (times[current] - lastBreathTime);
//                        // rrLabel.setText(Double.toString(rr));
//                        // if(rrDevice != null) {
//                        // rrDevice.updateRate((float) rr);
//                        // }
//                    }
//                    lastBreathTime = times[current];
//                }
//                current = plus(current);
//            }
//
//            // long mostRecentTime = times[minus(current)];
//            // long oneHalfSecondAgo = times[minus(current)]-500L;
//            DCT.dct(values, current, coeffs, 10);
//            double weighted = 0.0, sum = 0.0;
//            double max = Double.MIN_VALUE;
//            int index = 0;
//
//            for (int i = 1; i < 10; i++) {
//                weighted += i * Math.abs(coeffs[i]);
//                sum += Math.abs(coeffs[i]);
//                if (Math.abs(coeffs[i]) > max) {
//                    index = i;
//                    max = Math.abs(coeffs[i]);
//                }
//            }
//            double weighted_C = weighted / sum;
//            double bpm = (Math.PI * HISTORY * msPerSample / 1000.0) / (weighted_C + 2);
//            bpm = 60.0 / bpm;
//            rrLabel.setText(Double.toString(bpm) + "  " + weighted_C + "  " + index + "  " + rr);
//            this.rr = bpm;
//            eventLoop.doLater(updateRate);
//            // if(rrDevice != null) {
//            // rrDevice.updateRate((float) bpm);
//            // }
//
//            // process each point as if it were coming in anew like real data
//            // samples would
//            for (int localCurrent = startedAtCurrent; localCurrent < current; localCurrent = plus(localCurrent)) {
//                // for(int i = minus(localCurrent); i != localCurrent; i =
//                // minus(i)) {
//                // if(times[i] != 0L && times[i] <= oneHalfSecondAgo) {
//                // // found it .. a reference point at least some distance back
//                // in time
//                // // System.out.println("From " + times[i] + " to " +
//                // mostRecentTime + " value from " + values[i] + " to " +
//                // values[minus(current)]);
//                // double rateChange = 1.0 * (values[minus(localCurrent)] -
//                // values[i]) / (mostRecentTime - times[i]);
//                // low = (float) Math.min(low, rateChange);
//                // high = (float) Math.max(high, rateChange);
//                // rrLabel.setText(""+(1000.0*low) + " / " + (1000.0*high)+
//                // " / "+Double.toString(1000.0*rateChange));
//                // break;
//                // }
//                // }
//            }
//            // if(current == 0) {
//            // System.err.println(Arrays.toString(values));
//            // }
//
//        }
//    }

    @Override
    public void intervalAdded(ListDataEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        // TODO Auto-generated method stub
        
    }

}
