package org.mdpnp.apps.testapp.pumps;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.testapp.Device;
import org.mdpnp.guis.waveform.javafx.JavaFXWaveformPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.infrastructure.InstanceHandle_t;

import ice.FlowRateObjectiveDataWriter;
import ice.NumericSQIObjectiveDataWriter;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class PumpWithListener {

	@FXML
	Label pumpId;
	@FXML
	TextField requestedSpeed;
	@FXML
	TextField requestedAccuracy;
	@FXML
	TextField requestedAccuracyDuration;
	@FXML
	TextField requestedCompleteness;
	@FXML
	TextField requestedPrecision;
	@FXML
	TextField requestedFrequency;
	@FXML
	Button setSpeedButton;
	@FXML
	Label currentSpeedLabel;
	@FXML
	Label currentAccuracyLabel;
	@FXML
	Label currentAccuracyDurationLabel;
	@FXML
	Label currentCompletenessLabel;
	@FXML
	Label currentPrecisionLabel;
	@FXML
	Label currentFrequencyLabel;

	private Device pump;
	private FlowRateObjectiveDataWriter writer;
	private NumericSQIObjectiveDataWriter sqiWriter;
	private Connection dbconn;
	private PreparedStatement controlStatement;

	private static final Logger log = LoggerFactory.getLogger(PumpWithListener.class);
	private final String FLOW_RATE = rosetta.MDC_FLOW_FLUID_PUMP.VALUE;

	public void initialize() {

	}

	class NumericValueChangeListener implements ChangeListener<Number> {

		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			if (observable instanceof SimpleFloatProperty) { // TODO: Better instanceof base class?
				SimpleFloatProperty sfp = (SimpleFloatProperty) observable;
				switch (sfp.getName()) {
				case "value":
					currentSpeedLabel.setText(Float.toString(newValue.floatValue()));
					break;
				case "sqi_accuracy":
					currentAccuracyLabel.setText(Float.toString(newValue.floatValue()));
					break;
				case "sqi_accuracy_duration":
					currentAccuracyDurationLabel.setText(Float.toString(newValue.floatValue()));
					break;
				case "sqi_completeness":
					currentCompletenessLabel.setText(Float.toString(newValue.floatValue()));
					break;
				case "sqi_precision":
					currentPrecisionLabel.setText(Float.toString(newValue.floatValue()));
					break;
				case "sqi_frequency":
					currentFrequencyLabel.setText(Float.toString(newValue.floatValue()));
				default:
					break;
				}
			}
		}

	}

	NumericValueChangeListener pumpSpeedListener = new NumericValueChangeListener();

	public void setPump(Device pump, NumericFxList list, 
			FlowRateObjectiveDataWriter writer, NumericSQIObjectiveDataWriter sqiWriter, Connection dbconn) {
		this.pump = pump;
		this.writer = writer;
		this.sqiWriter = sqiWriter;
		this.dbconn = dbconn;

		pumpId.textProperty().bind(pump.comPortProperty());

		list.forEach(n -> {
			log.info("handleDeviceChange numeric dev ident is " + n.getUnique_device_identifier() + " "
					+ n.getMetric_id());
			if (!n.getUnique_device_identifier().equals(pump.getUDI()))
				return; // Some other device
			// When we get here, we are looking at a property for the currently selected
			// device
			if (n.getMetric_id().equals(FLOW_RATE)) {
				currentSpeedLabel.setText(Integer.toString((int) n.getValue()));
				n.valueProperty().addListener(pumpSpeedListener);
				n.sqi_accuracyProperty().addListener(pumpSpeedListener);
				n.sqi_accuracy_durationProperty().addListener(pumpSpeedListener);
				n.sqi_completenessProperty().addListener(pumpSpeedListener);
				n.sqi_frequencyProperty().addListener(pumpSpeedListener);
				n.sqi_precisionProperty().addListener(pumpSpeedListener);
				// System.err.println("Added pump speed listener to "+pump.getComPort());
			}
		});

	}

	public void setTheFlowRate() {
//		requestedSpeed.setText(Integer.toString(desiredFlowRate));
		// ice.OximetryAveragingObjective objective=new
		// ice.OximetryAveragingObjective();
		if(requestedSpeed.getText().length()>0) {
			ice.FlowRateObjective objective = new ice.FlowRateObjective();
			float desiredFlowRate = Float.parseFloat(requestedSpeed.getText());
			objective.newFlowRate = desiredFlowRate;
			objective.unique_device_identifier = pump.getUDI();
			writer.write(objective, InstanceHandle_t.HANDLE_NIL);
			log.info("Published an objective for flow rate " + desiredFlowRate);
			try {
				if (controlStatement == null && dbconn != null) {
					controlStatement = dbconn.prepareStatement(
							"INSERT INTO flowrequest(t_millis, target_udi, requestedRate) VALUES (?,?,?)");
				}
				if (controlStatement != null) {
					controlStatement.setLong(1, System.currentTimeMillis() / 1000);
					controlStatement.setString(2, objective.unique_device_identifier);
					controlStatement.setFloat(3, objective.newFlowRate);
					controlStatement.execute();
				}
			} catch (SQLException sqle) {
				log.error("Could not record request in database", sqle);
			}
		}
		
		//Also publish the SQI objective....
		ice.NumericSQIObjective sqiObjective = new ice.NumericSQIObjective();
		sqiObjective.metric_id=FLOW_RATE;
		sqiObjective.unique_device_identifier=pump.getUDI();
		sqiObjective.newAccuracy=requestedAccuracy.getText().length()>0 ? Float.parseFloat(requestedAccuracy.getText()) : 0;
		sqiObjective.newAccuracy_duration=requestedAccuracyDuration.getText().length()>0 ? Float.parseFloat(requestedAccuracyDuration.getText()) : 0;
		sqiObjective.newCompleteness=requestedCompleteness.getText().length()>0 ? Float.parseFloat(requestedCompleteness.getText()) : 0;
		sqiObjective.newFrequency=requestedFrequency.getText().length()>0 ? Float.parseFloat(requestedFrequency.getText()) : 0;
		sqiObjective.newPrecision=requestedPrecision.getText().length()>0 ? Float.parseFloat(requestedPrecision.getText()) :0;
		sqiWriter.write(sqiObjective, InstanceHandle_t.HANDLE_NIL);
		log.info("Published an objective for Numeric SQI");
	}

}
