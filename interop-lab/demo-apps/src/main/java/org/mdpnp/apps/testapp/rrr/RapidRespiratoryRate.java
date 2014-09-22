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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
import org.mdpnp.guis.waveform.WaveformSource.WaveformIterator;
import org.mdpnp.guis.waveform.swing.SwingWaveformPanel;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.SampleArrayInstanceModel;

import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriberQos;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class RapidRespiratoryRate extends JPanel implements ListDataListener, Runnable {

    private final JList<ice.SampleArray> capnoSources = new JList<ice.SampleArray>();
    private final JPanel controlPanel = new JPanel();
    private final JLabel rrLabel = new JLabel("???");
    private final WaveformPanel wavePanel = new SwingWaveformPanel();
    private final JSlider thresholdSlider = new JSlider(0, 100, 20);
    private final JCheckBox device = new JCheckBox("Create Device");
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private SampleArrayWaveformSource waveformSource;

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
            this.rate = numericSample(this.rate, (int) Math.round(rate), rosetta.MDC_CO2_RESP_RATE.VALUE, 
                    rosetta.MDC_DIM_DIMLESS.VALUE, null);
        }
    }

    private RespiratoryRateDevice rrDevice;
    @SuppressWarnings("unused")
    private final EventLoop eventLoop;

    @SuppressWarnings("unchecked")
    public RapidRespiratoryRate(final int domainId, final EventLoop eventLoop, final Subscriber subscriber, DeviceListCellRenderer deviceCellRenderer) {
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
                    waveformSource = null;
                    wavePanel.setSource(waveformSource);
                } else {
                    waveformSource = new SampleArrayWaveformSource(model.getReader(), sa);
                    wavePanel.setSource(waveformSource);
                }
            }
            
        });
        device.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (device.isSelected()) {
                    if (rrDevice == null) {
                        rrDevice = new RespiratoryRateDevice(domainId, eventLoop);
                        // TODO Make this more elegant
                        List<String> strings = new ArrayList<String>();
                        SubscriberQos qos = new SubscriberQos();
                        subscriber.get_qos(qos);
                        
                        for(int i = 0; i < qos.partition.name.size(); i++) {
                            strings.add((String)qos.partition.name.get(i));
                        }
                        rrDevice.setPartition(strings.toArray(new String[0]));
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
        executor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                RapidRespiratoryRate.this.run();
//                eventLoop.doLater(RapidRespiratoryRate.this);
            }
        }, 1000L, 200L, TimeUnit.MILLISECONDS);
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


    private double rr;
    
    protected float min, max;
    protected Long lastCrossing, lastInterval;
    protected Float lastValue = null;
    
    @Override
    public void run() {
        SampleArrayWaveformSource source = this.waveformSource;
        if(source != null) {
            source.iterate(new WaveformIterator() {

                @Override
                public void begin() {
                    min = Float.MAX_VALUE;
                    max = Float.MIN_VALUE;
                }

                @Override
                public void sample(long time, float value) {
                    if(value > max) {
                        max = value;
                    } else if(value < min) {
                        min = value;
                    }
                }

                @Override
                public void end() {
                    
                }
                
            });
            final float threshold = max - 1f * thresholdSlider.getValue() / 100f * (max - min);
            source.iterate(new WaveformIterator() {

                @Override
                public void begin() {
                    lastCrossing = null;
                    lastInterval = null;
                    lastValue = null;
                }

                @Override
                public void sample(long time, float value) {
                    if(null != lastValue) {
                        if(value >= threshold && lastValue < threshold) {
                            if(lastCrossing != null) {
                                lastInterval = time - lastCrossing;
                            }
                            lastCrossing = time;
                        }
                        
                    }
                    lastValue = value;
                }

                @Override
                public void end() {
                    // ms per breath to breaths/ms
                    if(null != lastInterval) {
                        rr = Math.round(1.0 / (lastInterval / 60000.0));
                    }
//                    System.err.println("lastInterval="+lastInterval+ " lastCrossing="+lastCrossing+ " rr="+rr);
                    
                }
                
            });
        } else {
            rr = 0;
        }
        this.rrLabel.setText(""+Math.round(rr));
        if (rrDevice != null) {
            rrDevice.updateRate((float) Math.round(rr));
        }
    }
    
    
    @Override
    public void intervalAdded(ListDataEvent e) {
        
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        
    }

    @Override
    public void contentsChanged(ListDataEvent e) {

    }
}
