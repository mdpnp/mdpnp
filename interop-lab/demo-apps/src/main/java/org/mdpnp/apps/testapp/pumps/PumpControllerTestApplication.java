package org.mdpnp.apps.testapp.pumps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

import org.mdpnp.apps.fxbeans.AlertFxList;
import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFx;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.devices.MDSHandler;
import org.mdpnp.devices.MDSHandler.Connectivity.MDSEvent;
import org.mdpnp.devices.MDSHandler.Connectivity.MDSListener;
import org.mdpnp.devices.MDSHandler.Patient.PatientEvent;
import org.mdpnp.devices.MDSHandler.Patient.PatientListener;
import org.mdpnp.devices.PartitionAssignmentController;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.sql.SQLLogging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.subscription.Subscriber;

import ice.FlowRateObjectiveDataWriter;
import ice.InfusionObjectiveDataWriter;
import ice.InfusionProgramDataWriter;
import ice.MDSConnectivity;
import ice.Patient;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class PumpControllerTestApplication {
	
	private DeviceListModel dlm;
	private NumericFxList numeric;
	private AlertFxList alert;
	private FlowRateObjectiveDataWriter flowRateWriter;
	private InfusionObjectiveDataWriter infusionObjWriter;
	private InfusionProgramDataWriter infusionProgWriter;
	private MDSHandler mdsHandler;
	
	@FXML VBox pumps;
		
	//@FXML private ComboBox<Device> pumpCombo;
	@FXML private TextField systolic;
	@FXML private TextField diastolic;
	@FXML private TextField mean;
		
	private final String FLOW_RATE=rosetta.MDC_FLOW_FLUID_PUMP.VALUE;
	
	private static final Logger log = LoggerFactory.getLogger(PumpControllerTestApplication.class);
	
	private boolean listenerPresent;
		
	private HashMap<String, Parent> udiToPump=new HashMap<>();
	
	/**
	 * The "current" patient, used to determine if the patient has changed
	 */
	private Patient currentPatient;
	
	private Connection dbconn;
	
	public void set(DeviceListModel dlm, NumericFxList numeric, FlowRateObjectiveDataWriter writer,
			InfusionObjectiveDataWriter infusionObjWriter, InfusionProgramDataWriter programWriter, MDSHandler mdsHandler,
			AlertFxList alert) {
		this.dlm=dlm;
		this.numeric=numeric;
		this.flowRateWriter=writer;
		this.infusionObjWriter=infusionObjWriter;
		this.infusionProgWriter=programWriter;
		this.mdsHandler=mdsHandler;
		this.alert=alert;
	}
	
	public void stop() {
		//TODO: Stop listening to the BP waveform for efficiency?
	}
	
	public void destroy() {
		if(dbconn!=null) {
			try {
				dbconn.close();
			} catch (SQLException e) {
				log.error("Could not cleanly close SQL Connection",e);
			}
		}
	}
	
	public void activate() {

		log.info("QCT.activate does nothing at the moment");
		System.err.println("In PumpControllerTestApplication.activate");

	}

	public void start(EventLoop eventLoop, Subscriber subscriber) {
		
		//Rely on addition of metrics to add devices...
		numeric.addListener(new ListChangeListener<NumericFx>() {
			@Override
			public void onChanged(Change<? extends NumericFx> change) {
				while(change.next()) {
					change.getAddedSubList().forEach( n -> {
						if(n.getMetric_id().equals(FLOW_RATE) ||
								n.getMetric_id().equals("MDC_FLOW_FLUID_PUMP_1") || n.getMetric_id().equals("MDC_FLOW_FLUID_PUMP_2")) {
							//Flow rate published - add to panel.  addPumpToMainPanel avoids duplication of devices anyway,
							//so just call it here.
							addPumpToMainPanel(dlm.getByUniqueDeviceIdentifier(n.getUnique_device_identifier()));
							//addPumpToSelectionBox(dlm.getByUniqueDeviceIdentifier(n.getUnique_device_identifier()));
						}
					});
				}
			}

		});
		
		//...and removal of devices to remove devices.
		dlm.getContents().addListener(new ListChangeListener<Device>() {
			@Override
			public void onChanged(Change<? extends Device> change) {
				while(change.next()) {
					change.getRemoved().forEach( d-> {
						//icepumps.getItems().remove(d);
						removePumpFromMainPanel(d);
					});
				}
			}
		});
		
		listenerPresent=true;
		
		/*
		pumpCombo.setCellFactory(new Callback<ListView<Device>,ListCell<Device>>() {

			@Override
			public ListCell<Device> call(ListView<Device> device) {
				return new DeviceListCell();
			}
			
		});
		
		pumpCombo.setConverter(new StringConverter<Device>() {

			@Override
			public Device fromString(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String toString(Device device) {
				// TODO Auto-generated method stub
				return device.getModel();
			}
			
		});
		
		pumpCombo.getSelectionModel().selectedItemProperty().addListener( new ChangeListener<Device>() {

			@Override
			public void changed(ObservableValue<? extends Device> observable, Device oldValue, Device newValue) {
				//Remove the old one...
				addPumpToMainPanel(newValue);
				
			}
		});
		*/
		
		mdsHandler.addPatientListener(new PatientListener() {

			@Override
			public void handlePatientChange(PatientEvent evt) {
				
			}
			
		});
		
		mdsHandler.addConnectivityListener(new MDSListener() {

			@Override
			public void handleConnectivityChange(MDSEvent evt) {
		        ice.MDSConnectivity c = (MDSConnectivity) evt.getSource();

		        String mrnPartition = PartitionAssignmentController.findMRNPartition(c.partition);

		        if(mrnPartition != null) {
		            //log.info("udi " + c.unique_device_identifier + " is MRN=" + mrnPartition);

		            Patient p = new Patient();
		            p.mrn = PartitionAssignmentController.toMRN(mrnPartition);
		            
		            if(currentPatient==null) {
		            	/*
		            	 * The patient has definitely changed - even if the selected patient is "Unassigned",
		            	 * then that "Patient" has an ID
		            	 */
		            	currentPatient=p;
		            	return;	//Nothing else to do.
		            }
		            if( ! currentPatient.mrn.equals(p.mrn) ) {
		            	//Patient has changed
		            	currentPatient=p;
		            }
		            
		            //deviceUdiToPatientMRN.put(c.unique_device_identifier, p);
		        }
		    }
			
		});
		
    	dbconn = SQLLogging.getConnection();
	}
	
	private void addPumpToMainPanel(Device d) {
		if(!udiToPump.containsKey(d.getUDI()) && numeric!=null) {
			
			String fxmlFile=getCorrectFXML(d);
			URL url = PumpControllerTestApplication.class.getResource(fxmlFile);
			String f = url.getFile();
			File testfile = new File (f);
			if (!testfile.exists()) {
				return;
			}
			
			FXMLLoader loader = new FXMLLoader(url);
			
			try {
		        final Parent ui = loader.load();
		        
//		        final PumpWithListener controller = ((PumpWithListener) loader.getController());
//		        controller.setPump(d,numeric,writer, dbconn);
		        final AbstractControllablePump controller = (AbstractControllablePump)loader.getController();
		        controller.setDevice(d);
		        controller.setNumerics(numeric);
		        controller.setInfusionObjectiveDataWriter(infusionObjWriter);
		        controller.setInfusionProgramDataWriter(infusionProgWriter);
		        controller.setAlerts(alert);
		        controller.start();
		        //Assume that only one other pump is possible.
		        ObservableList<Node> currentChildren=pumps.getChildren();
		        //if(currentChildren.size()>0) {
		        //	currentChildren.remove(0);
		        //}
		        currentChildren.add(ui);
		        udiToPump.put(d.getUDI(), ui);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	
	/**
	 * Get the correct FXML file name for the specified device.  We <i>could<i> try skipping
	 * this method and just making the FXML file name be &lt;MANUFACTURER&gt;_&lt;MODEL&gt;
	 * but we could still have issues with spaces or other special characters.  So we use this
	 * method to work out what the file name is for a particular device.
	 * @param d
	 * @return
	 */
	private String getCorrectFXML(Device d) {
		//TODO: Turn all this into a file with a map in it...
		/*
		 * deviceIdentity.manufacturer="Neurowave";
		deviceIdentity.model="AP-4000";
		
		 */
		
		String testFileName = d.getManufacturer().toLowerCase() + "_" + d.getModel().toLowerCase() + ".fxml";
		return testFileName;
		
//		if(d.getManufacturer().equals("Neurowave")) {
//			if(d.getModel().equals("AP-4000")) {
//				return "neurowave_ap-4000.fxml";
//				
//			}
//		}
//		return null;
	}
	
	/*
	private void addPumpToSelectionBox(Device device) {
		pumpCombo.getItems().add(device);
		
	}
	*/
	
	private void removePumpFromMainPanel(Device d) {
		pumps.getChildren().remove(udiToPump.get(d.getUDI()));
	}
	
	private float[] getMinAndMax(Number[] numbers) {
		float[] minAndMax=new float[] {numbers[0].floatValue(),numbers[0].floatValue()};
		for(int i=1;i<numbers.length;i++) {
			if(numbers[i].floatValue()<minAndMax[0]) minAndMax[0]=numbers[i].floatValue();
			if(numbers[i].floatValue()>minAndMax[1]) minAndMax[1]=numbers[i].floatValue();
		}
		return minAndMax;
	}
	

	

	class DeviceListCell extends ListCell<Device> {
        @Override protected void updateItem(Device device, boolean empty) {
            super.updateItem(device, empty);
            if (!empty && device != null) {
                setText(device.getModel()+"("+device.getComPort()+")");
            } else {
                setText(null);
            }
        }
    }

	public void refresh() {
		int childCount=pumps.getChildren().size();
		pumps.getChildren().remove(0, childCount);
		activate();
	}

}
