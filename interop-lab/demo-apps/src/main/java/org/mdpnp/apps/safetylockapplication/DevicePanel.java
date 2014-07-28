package org.mdpnp.apps.safetylockapplication;

import ice.NumericDataReader;
import ice.SampleArrayDataReader;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.mdpnp.apps.safetylockapplication.Resources.Command;


import org.mdpnp.rtiapi.data.QosProfiles;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.topic.Topic;


public class DevicePanel extends JPanel implements PatientEventListener, ActionListener, CommandListener {
	private static final long serialVersionUID = 1L;
	
	private boolean rPulseOxIsSpawned = false;
	private boolean sPulseOxIsSpawned = false;
	private boolean sEkgIsSpawned = false;
	private boolean sCapnoIsSpawned = false;
	private boolean sPumpIsSpawned = false;
	private boolean pumpIsActive = false;
	
	ImageButton rPulseOxButton = new ImageButton(Resources.loadImage("NoninPulseOximeterUnselected.png"));
	ImageButton sPulseOxButton = new ImageButton(Resources.loadImage("SimulatedPulseOximeterUnselected.png"));
	ImageButton sEkgButton = new ImageButton(Resources.loadImage("SimulatedEcgUnselected.png"));
	ImageButton sPump = new ImageButton(Resources.loadImage("SimulatedPumpUnselected.png"));
	ImageButton sCapnoButton = new ImageButton(Resources.loadImage("SimulatedCapnographUnselected.png"));
	
	ImageButton o2Distressor = new ImageButton(Resources.loadImage("o2DistressorInactive.png"));
	ImageButton o2Roc = new ImageButton(Resources.loadImage("o2RocInactive.png"));
	ImageButton co2Distressor = new ImageButton(Resources.loadImage("co2DistressorInactive.png"));
	ImageButton co2Roc = new ImageButton(Resources.loadImage("co2RocInactive.png"));
	ImageButton respiratoryDistressor = new ImageButton(Resources.loadImage("RespiratoryRateDistressorInactive.png"));
	ImageButton respiratoryRoc = new ImageButton(Resources.loadImage("RespirationRocInactive.png"));
	ImageButton hrprDistressor = new ImageButton(Resources.loadImage("HrprDistressorInactive.png"));
	ImageButton hrprRoc = new ImageButton(Resources.loadImage("HrprRocInactive.png"));
	
	ArrayList<DevicePanelListener> listeners = new ArrayList<DevicePanelListener>();
	PatientEvent previousPatientEvent = null;
	ReceivePulseOximeterNumerics rReceiver;
	ReceivePlethysmograph pReceiver;
	
	public DevicePanel()
	{
		rPulseOxButton.addActionListener(this);
		sPulseOxButton.addActionListener(this);
		sEkgButton.addActionListener(this);
		sPump.addActionListener(this);
		sCapnoButton.addActionListener(this);
		//////////
		
		setLayout(new GridBagLayout());
		setBackground(Resources.physiologicalDisplayPanelBackgroundColor);
		setupGridLayout();
		
		//for pulse oximeter numerics
		rReceiver = new ReceivePulseOximeterNumerics();
        int domainId = 15;
        org.mdpnp.apps.testapp.Main.loadAndSetIceQosLibrary();
        final DomainParticipant participant = DomainParticipantFactory.get_instance().create_participant(domainId,
                DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        final Subscriber subscriber = participant.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        ice.NumericTypeSupport.register_type(participant, ice.NumericTypeSupport.get_type_name());
        final Topic topic = participant.create_topic(ice.NumericTopic.VALUE, ice.NumericTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT,
                null, StatusKind.STATUS_MASK_NONE);
        final ice.NumericDataReader reader = (NumericDataReader) subscriber.create_datareader_with_profile(topic, QosProfiles.ice_library,
                QosProfiles.numeric_data, rReceiver, StatusKind.DATA_AVAILABLE_STATUS);
        
        //for pulse oximeter array
        pReceiver = new ReceivePlethysmograph();
        final DomainParticipant participant2 = DomainParticipantFactory.get_instance().create_participant(domainId,
                DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        final Subscriber subscriber2 = participant.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        ice.SampleArrayTypeSupport.register_type(participant, ice.SampleArrayTypeSupport.get_type_name());
        final Topic topic2 = participant.create_topic(ice.SampleArrayTopic.VALUE, ice.SampleArrayTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT,
                null, StatusKind.STATUS_MASK_NONE);
        final ice.SampleArrayDataReader reader2 = (SampleArrayDataReader) subscriber.create_datareader_with_profile(topic2, QosProfiles.ice_library,
                QosProfiles.waveform_data, pReceiver, StatusKind.DATA_AVAILABLE_STATUS);

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

	        public void run() {
	        	subscriber.delete_datareader(reader);
	            participant.delete_topic(topic);
	            ice.NumericTypeSupport.unregister_type(participant, ice.NumericTypeSupport.get_type_name());
	            participant.delete_subscriber(subscriber);
	            DomainParticipantFactory.get_instance().delete_participant(participant);
	            DomainParticipantFactory.finalize_instance();
	            
	            subscriber2.delete_datareader(reader2);
	            participant2.delete_topic(topic2);
	            ice.NumericTypeSupport.unregister_type(participant2, ice.NumericTypeSupport.get_type_name());
	            participant.delete_subscriber(subscriber2);
	            DomainParticipantFactory.get_instance().delete_participant(participant2);
	            DomainParticipantFactory.finalize_instance();
	        }
	    }));
	}
	
	public void setupGridLayout()
	{
		GridBagConstraints gc = new GridBagConstraints();
		
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = 1;
		
		JPanel pulseOximetersPanel = new JPanel();
		pulseOximetersPanel.setBackground(Resources.physiologicalDisplayPanelBackgroundColor);
		pulseOximetersPanel.setLayout(new GridBagLayout());
		pulseOximetersPanel.setBorder(makeBorder("Pulse Oximeters"));
		
		pulseOximetersPanel.add(rPulseOxButton, gc);
		gc.gridy++;
		pulseOximetersPanel.add(sPulseOxButton, gc);
		
		JPanel ecgsPanel = new JPanel();
		ecgsPanel.setBackground(Resources.physiologicalDisplayPanelBackgroundColor);
		ecgsPanel.setLayout(new GridBagLayout());
		ecgsPanel.setBorder(makeBorder("Electrocardiograms"));
		
		gc.gridy = 0;
		ecgsPanel.add(sEkgButton, gc);
		
		JPanel capnographsPanel = new JPanel();
		capnographsPanel.setBackground(Resources.physiologicalDisplayPanelBackgroundColor);
		capnographsPanel.setLayout(new GridBagLayout());
		capnographsPanel.setBorder(makeBorder("Capnographs"));
		
		capnographsPanel.add(sCapnoButton, gc);
		
		JPanel pumpsPanel = new JPanel();
		pumpsPanel.setBackground(Resources.physiologicalDisplayPanelBackgroundColor);
		pumpsPanel.setLayout(new GridBagLayout());
		pumpsPanel.setBorder(makeBorder("Pumps"));
		
		pumpsPanel.add(sPump, gc);
		
		add(pulseOximetersPanel, gc);
		gc.gridy++;
		add(ecgsPanel, gc);
		gc.gridy++;
		add(capnographsPanel, gc);
		gc.gridy++;
		add(pumpsPanel, gc);
	}
	
	private Border makeBorder(String borderTitle)
	{
		Border inner = BorderFactory.createLineBorder(Color.GRAY);
		Font titleFont = new Font("Sans", Font.PLAIN, 15);
		Border innerBorder = BorderFactory.createTitledBorder(inner, borderTitle, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, titleFont, Color.WHITE);
		Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		return BorderFactory.createCompoundBorder(outerBorder, innerBorder);
	}
	
	@Override
	public void actionPerformed(PatientEvent patientEvent) {
		
		DeviceEvent deviceEvent = new DeviceEvent(patientEvent, null);
		if (!sPulseOxIsSpawned)
		{
			deviceEvent.o2Saturation = -1;
			deviceEvent.pulseRate = -1;
			deviceEvent.plethysmographSet.clear();
		}
		if (!sEkgIsSpawned)
		{
			deviceEvent.heartRate = -1;
		}
		if (!sCapnoIsSpawned)
		{
			deviceEvent.co2Saturation = -1;
			deviceEvent.respiratoryRate = -1;
		}
		
		if (sPumpIsSpawned)
		{
			if (pumpIsActive)
				deviceEvent.pumpMessage = "Interlock : Pump Active";
			else deviceEvent.pumpMessage = "Interlock : Pump Inactive";
		}
		else
			deviceEvent.pumpMessage = "";
		
		if (rPulseOxIsSpawned)
		{
			rReceiver.update(deviceEvent);
			pReceiver.update(deviceEvent);
		}
		
		
		for (DevicePanelListener listener : listeners)
		{
			listener.actionPerformed(deviceEvent);
		}
	}
	
	public void addDevicePanelListener(DevicePanelListener listener)
	{
		listeners.add(listener);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if (o == rPulseOxButton)
		{
			if (!rPulseOxIsSpawned)
			{
				rPulseOxButton.updateImage(Resources.loadImage("NoninPulseOximeterSelected.png"));
				rPulseOxIsSpawned = true;
				if (sPulseOxIsSpawned)
				{
					sPulseOxButton.updateImage(Resources.loadImage("SimulatedPulseOximeterUnselected.png"));
					sPulseOxIsSpawned = false;
				}
			}
			else
			{
				rPulseOxButton.updateImage(Resources.loadImage("NoninPulseOximeterUnselected.png"));
				rPulseOxIsSpawned = false;
			}
		}
		else if (o == sPulseOxButton)
		{
			if (!sPulseOxIsSpawned)
			{
				sPulseOxButton.updateImage(Resources.loadImage("SimulatedPulseOximeterSelected.png"));
				sPulseOxIsSpawned = true;
				if (rPulseOxIsSpawned)
				{
					rPulseOxButton.updateImage(Resources.loadImage("NoninPulseOximeterUnselected.png"));
					rPulseOxIsSpawned = false;
				}
			}
			else
			{
				sPulseOxButton.updateImage(Resources.loadImage("SimulatedPulseOximeterUnselected.png"));
				sPulseOxIsSpawned = false;
			}
		}
		else if (o == sEkgButton)
		{
			if (!sEkgIsSpawned)
			{
				sEkgButton.updateImage(Resources.loadImage("SimulatedEcgSelected.png"));
				sEkgIsSpawned = true;
			}
			else
			{
				sEkgButton.updateImage(Resources.loadImage("SimulatedEcgUnselected.png"));
				sEkgIsSpawned = false;
			}
		}
		else if (o == sCapnoButton)
		{
			if (!sCapnoIsSpawned)
			{
				sCapnoButton.updateImage(Resources.loadImage("SimulatedCapnographSelected.png"));
				sCapnoIsSpawned = true;
			}
			else
			{
				sCapnoButton.updateImage(Resources.loadImage("SimulatedCapnographUnselected.png"));
				sCapnoIsSpawned = false;
			}
		}
		else if (o == sPump)
		{
			if (!sPumpIsSpawned)
			{
				sPump.updateImage(Resources.loadImage("SimulatedPumpSelected.png"));
				sPumpIsSpawned = true;
			}
			else
			{
				sPump.updateImage(Resources.loadImage("SimulatedPumpUnselected.png"));
				sPumpIsSpawned = false;
				pumpIsActive = false;
			}
		}
	}

	@Override
	public void actionPerformed(CommandEvent e) {
		if (e.command == Command.STOP)
			pumpIsActive = false;
		else if (e.command == Command.START)
			pumpIsActive = true;
		
	}
}
