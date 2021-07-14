package org.mdpnp.apps.testapp.mindware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mdpnp.apps.testapp.mindware.BioLabApp.ChannelData;
import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.DeviceDriverProvider;
import org.mdpnp.devices.PartitionAssignmentController;
import org.mdpnp.devices.AbstractDevice.InstanceHolder;
import org.mdpnp.devices.DeviceDriverProvider.DeviceType;
import org.mdpnp.devices.MDSHandler;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriberQos;

import ice.MDSConnectivity;
import impl.org.controlsfx.i18n.SimpleLocalizedStringProperty;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.Parent;

public class BioLabChannelController {
	
	@FXML
	private Label channelNameLabel;
	
	@FXML
	private GridPane channelRoot;
	
	@FXML
	private TextField metricID;
	
	@FXML
	private ComboBox clinicians;
	
	@FXML
	private Button startStopPublish;

	/**
	 * The BioLab channel name for this channel
	 */
	private String channelName;

	/**
	 * Observable property so other parts of the system can tell when this channel is set to publish.
	 */
	private SimpleBooleanProperty publishing;
	
	/**
	 * Get the observable property that indicates if this channel is publishing.
	 * @return the publishing status observable.
	 */
	public SimpleBooleanProperty getPublishing() {
		return publishing;
	}
	
	public String getClinicianName() {
		return clinicians.getSelectionModel().getSelectedItem().toString();
	}

	public BioLabChannelController() {
		publishing=new SimpleBooleanProperty(false);
	}
	
	public Parent getUI() {
		return channelRoot;
	}
	
	public void setChannelName(String channelName) {
		this.channelName=channelName;
		//The channel name won't change, so we don't need observables etc.
		channelNameLabel.setText("Channel Name: "+channelName);
	}
	
	public StringProperty getMetricID() {
		return metricID.textProperty();
	}

	public void startStopPublishChannel() {
		System.err.println("startStopPublishChannel for "+channelNameLabel.getText());
		if(metricID.getText().length()==0) {
			return;
		}
		publishing.set( ! publishing.get() );
	}
	
	public void toggleStopStart() {
		if(startStopPublish.getText().equals("Publish")) {
			startStopPublish.setText("Stop");
		} else {
			startStopPublish.setText("Publish");
		}
	}
	

	
	
}
