package org.mdpnp.apps.testapp.mindware;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

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
