package org.mdpnp.apps.testapp.pumps.timing;

import java.awt.Checkbox;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.plaf.FileChooserUI;

import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.pumps.timing.PumpTimingTestApplication.TimeAndRate;
import org.mdpnp.guis.waveform.javafx.JavaFXWaveformPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.infrastructure.InstanceHandle_t;

import ice.FlowRateObjectiveDataWriter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class PumpWithListener {
	
	@FXML Label pumpId;
	@FXML Label currentSpeedLabel;
	@FXML Label currentFileLabel;
	//@FXML CheckBox selected; 
	@FXML HBox main;
		
	private Device pump;
	private FlowRateObjectiveDataWriter writer;
	private Connection dbconn;
	private PreparedStatement controlStatement;
	
	private File timingsFile;
	
	private static final Logger log = LoggerFactory.getLogger(PumpWithListener.class);
	private final String FLOW_RATE=rosetta.MDC_FLOW_FLUID_PUMP.VALUE;
	
	
	public void initialize() {
		
	}
	
	class NumericValueChangeListener implements ChangeListener<Number> {

		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			// TODO Auto-generated method stub
			System.err.println("pumpSpeedListener newValue is "+newValue.floatValue());
			currentSpeedLabel.setText(Float.toString(newValue.floatValue()));
		}
		
	}
	
	NumericValueChangeListener pumpSpeedListener=new NumericValueChangeListener();
	
	public void setPump(Device pump, NumericFxList list, FlowRateObjectiveDataWriter writer, Connection dbconn) {
		this.pump=pump;
		this.writer=writer;
		this.dbconn=dbconn;
		
		pumpId.textProperty().bind(pump.comPortProperty());
		
		list.forEach( n -> {
			log.info("handleDeviceChange numeric dev ident is "+n.getUnique_device_identifier()+" "+n.getMetric_id());
			if( ! n.getUnique_device_identifier().equals(pump.getUDI())) return;	//Some other device
			//When we get here, we are looking at a property for the currently selected device
			if(n.getMetric_id().equals(FLOW_RATE)) {
				currentSpeedLabel.setText(Integer.toString((int)n.getValue()));
				n.valueProperty().addListener(pumpSpeedListener);
				System.err.println("Added pump speed listener to "+pump.getComPort());
			}
		});

	}
	

	public void setTheFlowRate(float newRate) {
		ice.FlowRateObjective objective=new ice.FlowRateObjective();
		objective.newFlowRate=newRate;
		objective.unique_device_identifier=pump.getUDI();
		writer.write(objective, InstanceHandle_t.HANDLE_NIL);
		log.info("Published an objective for flow rate "+newRate);
		try {
			if(controlStatement==null && dbconn!=null) {
				controlStatement=dbconn.prepareStatement("INSERT INTO flowrequest(t_millis, target_udi, requestedRate) VALUES (?,?,?)");
			}
			if(controlStatement!=null) {
				controlStatement.setLong(1, System.currentTimeMillis()/1000);
				controlStatement.setString(2, objective.unique_device_identifier);
				controlStatement.setFloat(3, objective.newFlowRate);
				controlStatement.execute();
			}
		} catch (SQLException sqle) {
			log.error("Could not record request in database", sqle);
		}
	}
	
	public void selectFile() {
		FileChooser chooser=new FileChooser();
		chooser.setTitle("Select speed control file");
		File f=chooser.showOpenDialog(main.getScene().getWindow());
		if(f!=null) {
			timingsFile=f;
			currentFileLabel.setText(f.getName());
		}
	}
	
	void runTimings() {
		
	}
	
	/**
	 * It barely seems worth a class, but it is one...
	 * @author Simon
	 *
	 */
	class TimeAndRate {
		/**
		 * How long to sleep before asking for the given rate
		 */
		long interval;
		/**
		 * The rate to ask for.
		 */
		float rate;
	}
	
	private ArrayList<TimeAndRate> timesAndRates=new ArrayList<>();

	public void startSettingSpeeds() throws IOException {
		BufferedReader br=new BufferedReader(new FileReader(timingsFile));
		String nextLine;
		while( (nextLine=br.readLine())!=null) {
			String[] parts=nextLine.split(",");
			TimeAndRate tr=new TimeAndRate();
			tr.interval=Long.parseLong(parts[0]);
			tr.rate=Float.parseFloat(parts[1]);
			timesAndRates.add(tr);
		}
		br.close();
		//Now we have a full ArrayList of times and rates.
		//We need to make this a separate runnable, because
		//otherwise the sleeps cause the GUI to hang.
		Thread setterThread=new Thread() {
			public void run() {
				for(int i=0;i<timesAndRates.size();i++) {
					TimeAndRate tr=timesAndRates.get(i);
					try {
						Thread.sleep(tr.interval);
					} catch (InterruptedException ie) {
						ie.printStackTrace();
					}
					//Now we've slept that long, set the rate...
					setTheFlowRate(tr.rate);
				}
			}
		};
		setterThread.start();
		
	}
	


}
