/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.guis.swing;

import ice.NumericDataReader;
import ice.NumericSeq;
import ice.NumericTypeSupport;
import ice.SampleArrayDataReader;
import ice.SampleArraySeq;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Set;

import javax.media.opengl.GLAutoDrawable;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.mdpnp.guis.waveform.NumericUpdateWaveformSource;
import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformPanelFactory;
import org.mdpnp.guis.waveform.WaveformUpdateWaveformSource;
import org.mdpnp.guis.waveform.swing.GLWaveformPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jogamp.opengl.util.FPSAnimator;
import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.ConditionSeq;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.RETCODE_TIMEOUT;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.infrastructure.WaitSet;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderListener;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.LivelinessChangedStatus;
import com.rti.dds.subscription.QueryCondition;
import com.rti.dds.subscription.RequestedDeadlineMissedStatus;
import com.rti.dds.subscription.RequestedIncompatibleQosStatus;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleLostStatus;
import com.rti.dds.subscription.SampleRejectedStatus;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriptionMatchedStatus;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.TopicDescription;

@SuppressWarnings("serial")
public class PulseOximeterPanel extends DevicePanel {
	
	
	private JLabel spo2, heartrate, spo2Label, heartrateLabel, nameLabel, guidLabel;
	private JLabel spo2Low, spo2Up, heartrateLow, heartrateUp;
	private JPanel spo2Bounds, heartrateBounds;
	private JPanel spo2Panel, heartratePanel;
	private WaveformPanel pulsePanel;
	private WaveformPanel plethPanel;
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
		spo2Bounds = new JPanel();
		spo2Bounds.setOpaque(false);
		spo2Bounds.setLayout(new GridLayout(3, 1));
		spo2Bounds.add(spo2Up = new JLabel("--"));
		spo2Bounds.add(spo2Low = new JLabel("--"));
		spo2Bounds.add(spo2Label = new JLabel("%SpO\u2082"));
		spo2Up.setVisible(false);
		spo2Low.setVisible(false);
		
		
		spo2Panel = new JPanel();
		spo2Panel.setOpaque(false);
		spo2Panel.setLayout(new BorderLayout());
		spo2Panel.add(spo2 = new JLabel("----"), BorderLayout.CENTER);
		spo2.setHorizontalAlignment(JLabel.RIGHT);
		spo2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		spo2Panel.add(spo2Bounds, BorderLayout.EAST);
		
		heartrateBounds = new JPanel();
		heartrateBounds.setOpaque(false);
		heartrateBounds.setLayout(new GridLayout(3,1));
		heartrateBounds.add(heartrateUp = new JLabel("--"));
		heartrateBounds.add(heartrateLow = new JLabel("--"));
		heartrateBounds.add(heartrateLabel = new JLabel("BPM"));
		heartrateUp.setVisible(false);
		heartrateLow.setVisible(false);
		
		heartratePanel = new JPanel();
		heartratePanel.setOpaque(false);
		heartratePanel.setLayout(new BorderLayout());
		heartratePanel.add(heartrate = new JLabel("----"), BorderLayout.CENTER);
		heartrate.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		heartrate.setHorizontalAlignment(JLabel.RIGHT);
		heartratePanel.add(heartrateBounds, BorderLayout.EAST);
		
		SpaceFillLabel.attachResizeFontToFill(this, spo2, heartrate);
		
		WaveformPanelFactory fact = new WaveformPanelFactory();
		
		plethPanel = fact.createWaveformPanel();
		pulsePanel = fact.createWaveformPanel();
		
		JPanel upper = new JPanel();
		upper.setOpaque(false);
		upper.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0);

		upper.add(plethPanel.asComponent(), gbc);
		
		gbc.gridy = 1;
		upper.add(pulsePanel.asComponent(), gbc);
		
		gbc.gridy = 0;
		gbc.weightx = 0.1;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.gridx = 1;

		upper.add(spo2Panel, gbc);
		gbc.gridy = 1;
		upper.add(heartratePanel, gbc);
		
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
		headers.setLayout(new GridLayout(2,1));
		headers.setOpaque(false);
		headers.add(nameLabel = new JLabel("NAME"));
		headers.add(guidLabel = new JLabel("GUID"));
		add(headers, BorderLayout.NORTH);
	
		nameLabel.setHorizontalAlignment(JLabel.RIGHT);
		guidLabel.setHorizontalAlignment(JLabel.RIGHT);
		if(plethPanel instanceof GLWaveformPanel) {
			((GLWaveformPanel)plethPanel).setAnimator(new FPSAnimator((GLAutoDrawable) plethPanel, FPSAnimator.DEFAULT_FRAMES_PER_INTERVAL));
			((GLWaveformPanel)plethPanel).getAnimator().start();
		}
		
		if(pulsePanel instanceof GLWaveformPanel) {
			((GLWaveformPanel)pulsePanel).setAnimator(new FPSAnimator((GLAutoDrawable) pulsePanel, FPSAnimator.DEFAULT_FRAMES_PER_INTERVAL));
			((GLWaveformPanel)pulsePanel).getAnimator().start();
		}
		
		setForeground(Color.green);
		setBackground(Color.black);
	}
	
	
	private final QueryCondition numericCondition;
	private final QueryCondition sampleArrayCondition;
	private final NumericDataReader numericDataReader;
	private final SampleArrayDataReader sampleArrayDataReader;
	private final WaitSet waitSet;
	
	private boolean built = false;
	public PulseOximeterPanel(Subscriber subscriber, String udi) {
		super(subscriber, udi);
		buildComponents();
		plethPanel.setSource(plethWave);
		
		pulsePanel.setSource(pulseWave);
		pulsePanel.cachingSource().setFixedTimeDomain(120000L);
		
		built = true;
		
		waitSet = new WaitSet();
		
		StringSeq udis = new StringSeq();
        udis.add("'"+udi+"'");
		
		TopicDescription topic = subscriber.get_participant().lookup_topicdescription(ice.NumericTopic.VALUE);
		if(null == topic) {
		    ice.NumericTypeSupport.register_type(subscriber.get_participant(), NumericTypeSupport.get_type_name());
		    topic = subscriber.get_participant().create_topic(ice.NumericTopic.VALUE, ice.NumericTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
		}
		
		numericDataReader = (NumericDataReader) subscriber.create_datareader(topic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
		numericCondition = numericDataReader.create_querycondition(SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE, "universal_device_identifier = %0", udis);
		waitSet.attach_condition(numericCondition);
		
		topic = subscriber.get_participant().lookup_topicdescription(ice.SampleArrayTopic.VALUE);
		if(null == topic) {
		    ice.SampleArrayTypeSupport.register_type(subscriber.get_participant(), ice.SampleArrayTypeSupport.get_type_name());
		    topic = subscriber.get_participant().create_topic(ice.SampleArrayTopic.VALUE, ice.SampleArrayTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
		}
		sampleArrayDataReader = (SampleArrayDataReader) subscriber.create_datareader(topic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
		sampleArrayCondition = sampleArrayDataReader.create_querycondition(SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE, "universal_device_identifier = %0", udis);
		waitSet.attach_condition(sampleArrayCondition);
//		registerAndRequestRequiredIdentifiedUpdates();
		// TODO this requires abstraction but for now is an experiment
		Thread t = new Thread(new Runnable() {
		    public void run() {
		        Duration_t timeout = new Duration_t(Duration_t.DURATION_INFINITE_SEC, Duration_t.DURATION_INFINITE_NSEC);
		        while(true) {
		            waitForIt(timeout);
		        }
		    }
		});
		t.setName("PulseOximeterPanel data handler");
		t.setDaemon(true);
		t.start();
	}
	
//	@Override
//	public Collection<Identifier> requiredIdentifiedUpdates() {
//		List<Identifier> ids = new ArrayList<Identifier>(super.requiredIdentifiedUpdates());
//		ids.addAll(Arrays.asList(new Identifier[] {ConnectedDevice.STATE, ConnectedDevice.CONNECTION_INFO, PulseOximeter.PULSE, PulseOximeter.SPO2, PulseOximeter.PLETH}));
//		return ids;
//	}
	
	private final WaveformUpdateWaveformSource plethWave = new WaveformUpdateWaveformSource();
	private final NumericUpdateWaveformSource pulseWave = new NumericUpdateWaveformSource(333L);
	

//	private ConnectedDevice.State connectedState;
	private String connectionInfo;
	
	
	
	@Override
	public void destroy() {
		if(plethPanel instanceof GLWaveformPanel) {
			GLWaveformPanel plethPanel = (GLWaveformPanel) this.plethPanel;
			plethPanel.getAnimator().stop();
			plethPanel.getAnimator().remove(plethPanel);
		}

		if(pulsePanel instanceof GLWaveformPanel) {
			GLWaveformPanel pulsePanel = (GLWaveformPanel) this.pulsePanel;
			pulsePanel.getAnimator().stop();
			pulsePanel.getAnimator().remove(pulsePanel);			
		}
		

		super.destroy();
	}
	
//	private final void setInt(IdentifiableUpdate<?> nu, Numeric numeric, JLabel label, String def) {
//		if(numeric.equals(nu.getIdentifier())) {
//			
//			setInt(((NumericUpdate)nu).getValue(), label, def);
//			if(!label.isVisible()) {
//				label.setVisible(true);
//			}
//		}
//	}
	private final void setInt(ice.Numeric sample, int name, JLabel label, String def) {
	    if(sample.name == name && udi.equals(sample.universal_device_identifier)) {
            setInt(sample.value, label, def);
            if(!label.isVisible()) {
                label.setVisible(true);
            }
        }
    }
	
	
//	@Override
//	protected void doUpdate(IdentifiableUpdate<?> n) {
//		if(!built) {
//			return;
//		}
//		if(null == n) {
//			log.warn("null update ");
//			return;
//		}
//		setInt(n, PulseOximeter.PULSE, this.heartrate, "---");
//		setInt(n, PulseOximeter.SPO2, this.spo2, "---");
//		setInt(n, PulseOximeter.PULSE_LOWER, this.heartrateLow, "--");
//		setInt(n, PulseOximeter.PULSE_UPPER, this.heartrateUp, "--");
//		setInt(n, PulseOximeter.SPO2_LOWER, this.spo2Low, "--");
//		setInt(n, PulseOximeter.SPO2_UPPER, this.spo2Up, "--");
//		if(PulseOximeter.PULSE.equals(n.getIdentifier())) {
//			Date date = ((NumericUpdate)n).getUpdateTime();
//			this.time.setText(null == date ? "---" : dateFormat.format(date));
//		}
//
//		if(ConnectedDevice.STATE.equals(n.getIdentifier())) {
//			connectedState = (State) ((EnumerationUpdate)n).getValue();
//			connected.setText(""+connectedState+(null==connectionInfo?"":(" ("+connectionInfo+")")));
//			plethWave.reset();
//			pulseWave.reset();
//		} else if(ConnectedDevice.CONNECTION_INFO.equals(n.getIdentifier())) {
//			connectionInfo = ((TextUpdate)n).getValue();
//			connected.setText(""+connectedState+(null==connectionInfo?"":(" ("+connectionInfo+")")));
//		} else if(DemoPulseOx.OUT_OF_TRACK.equals(n.getIdentifier())) {
//			EnumerationUpdate eu = (EnumerationUpdate) n;
//			
//			DemoPulseOx.Bool bool = (DemoPulseOx.Bool) eu.getValue();
//			if(null == bool) {
//				log.warn("Received null OUT_OF_TRACK");
//				return;
//			} else {
//				switch(bool) {
//				case False:
//					plethPanel.setOutOfTrack(false);
//					break;
//				case True:
//					plethPanel.setOutOfTrack(true);
//					break;
//				}
//			}
//		}
//	}
//	public static boolean supported(Set<Identifier> identifiers) {
//		return identifiers.contains(PulseOximeter.SPO2);
//	}
	
	public static boolean supported(Set<Integer> names) {
	    return names.contains(ice.MDC_PULS_OXIM_SAT_O2.VALUE);
	}
	private static final Logger log = LoggerFactory.getLogger(PulseOximeterPanel.class);
	@Override
	public void setIcon(Image image) {
		log.trace("setIcon");
		if(built && null != image) {
			nameLabel.setOpaque(false);
			nameLabel.setIcon(new ImageIcon(image));
		}
	}

	
	private final ConditionSeq condSeq = new ConditionSeq();
	private final NumericSeq nu_data_seq = new NumericSeq();
	private final SampleArraySeq sa_data_seq = new SampleArraySeq();
	private final SampleInfoSeq info_seq = new SampleInfoSeq();
	
	public void waitForIt(Duration_t timeout) {
	    condSeq.clear();
	    try {
	        waitSet.wait(condSeq, timeout);
	        for(Object o : condSeq) {
	            if(numericCondition.equals(o)) {
	                try {
    	                numericDataReader.take_w_condition(nu_data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, numericCondition);
    	                for(int i = 0; i < info_seq.size(); i++) {
    	                    if( ((SampleInfo)info_seq.get(i)).valid_data) {
    	                        ice.Numeric n = (ice.Numeric) nu_data_seq.get(i);
    //                            System.out.println("DATA:"+n);
                                setInt(n, ice.MDC_PULS_OXIM_SAT_O2.VALUE, spo2, null);
                                setInt(n, ice.MDC_PULS_OXIM_PULS_RATE.VALUE, heartrate, null);
                                if(ice.MDC_PULS_OXIM_PULS_RATE.VALUE == n.name) {
                                    pulseWave.applyUpdate(n);
                                }
    	                    }
    	                }
	                } finally {
	                    numericDataReader.return_loan(nu_data_seq, info_seq);
	                }
	            } else if(sampleArrayCondition.equals(o)) {
	                try {
    	                sampleArrayDataReader.take_w_condition(sa_data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, sampleArrayCondition);
    	                for(int i = 0; i < info_seq.size(); i++) {
    	                    if( ((SampleInfo)info_seq.get(i)).valid_data) {
    	                        ice.SampleArray sa = (ice.SampleArray) sa_data_seq.get(i);
    	                        plethWave.applyUpdate(sa);
    	                    }
    	                }
	                } finally {
	                    sampleArrayDataReader.return_loan(sa_data_seq, info_seq);
	                }
	            }
	        }
	    } catch (RETCODE_TIMEOUT to) {
	        
	    }
	}
}
