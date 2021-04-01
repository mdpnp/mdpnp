package org.mdpnp.apps.testapp.pumps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

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
import ice.MDSConnectivity;
import ice.NumericSQIObjectiveDataWriter;
import ice.Patient;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
	private SampleArrayFxList samples;
	private FlowRateObjectiveDataWriter writer;
	private NumericSQIObjectiveDataWriter sqiWriter;
	private MDSHandler mdsHandler;
	
	@FXML VBox pumps;
		
	private final String FLOW_RATE=rosetta.MDC_FLOW_FLUID_PUMP.VALUE;
	private final String ARTERIAL=rosetta.MDC_PRESS_BLD_ART_ABP.VALUE;
	
	private static final Logger log = LoggerFactory.getLogger(PumpControllerTestApplication.class);
	
	private boolean listenerPresent;
	
	private String[] SYS_PARAMS=new String[] { rosetta.MDC_PRESS_BLD_SYS.VALUE, rosetta.MDC_PRESS_BLD_ART_SYS.VALUE, rosetta.MDC_PRESS_INTRA_CRAN_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_AORT_SYS.VALUE, rosetta.MDC_PRESS_BLD_ART_ABP_SYS.VALUE, rosetta.MDC_PRESS_BLD_ART_FEMORAL_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_ART_PULM_SYS.VALUE, rosetta.MDC_PRESS_BLD_ART_UMB_SYS.VALUE, rosetta.MDC_PRESS_BLD_ATR_LEFT_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_ATR_RIGHT_SYS.VALUE
    };
	
	private String[] DIA_PARAMS=new String[] {
			rosetta.MDC_PRESS_BLD_DIA.VALUE, rosetta.MDC_PRESS_BLD_ART_DIA.VALUE, rosetta.MDC_PRESS_INTRA_CRAN_DIA.VALUE,
            rosetta.MDC_PRESS_BLD_AORT_DIA.VALUE, rosetta.MDC_PRESS_BLD_ART_ABP_DIA.VALUE, rosetta.MDC_PRESS_BLD_ART_FEMORAL_DIA.VALUE,
            rosetta.MDC_PRESS_BLD_ART_PULM_DIA.VALUE, rosetta.MDC_PRESS_BLD_ART_UMB_DIA.VALUE, rosetta.MDC_PRESS_BLD_ATR_LEFT_DIA.VALUE,
            rosetta.MDC_PRESS_BLD_ATR_RIGHT_DIA.VALUE
	};
	
	private String[] MEAN_PARAMS=new String[] {
			rosetta.MDC_PRESS_BLD_MEAN.VALUE, rosetta.MDC_PRESS_BLD_ART_MEAN.VALUE, rosetta.MDC_PRESS_INTRA_CRAN_MEAN.VALUE,
            rosetta.MDC_PRESS_BLD_AORT_MEAN.VALUE, rosetta.MDC_PRESS_BLD_ART_ABP_MEAN.VALUE, rosetta.MDC_PRESS_BLD_ART_FEMORAL_MEAN.VALUE,
            rosetta.MDC_PRESS_BLD_ART_PULM_MEAN.VALUE, rosetta.MDC_PRESS_BLD_ART_UMB_MEAN.VALUE, rosetta.MDC_PRESS_BLD_ATR_LEFT_MEAN.VALUE,
            rosetta.MDC_PRESS_BLD_ATR_RIGHT_MEAN.VALUE
	};
	
	private HashMap<String, Parent> udiToPump=new HashMap<>();
	
	/**
	 * The "current" patient, used to determine if the patient has changed
	 */
	private Patient currentPatient;
	
	private Connection dbconn;
	
	public void set(DeviceListModel dlm, NumericFxList numeric, SampleArrayFxList samples,
			FlowRateObjectiveDataWriter writer, NumericSQIObjectiveDataWriter sqiWriter, MDSHandler mdsHandler) {
		this.dlm=dlm;
		this.numeric=numeric;
		this.samples=samples;
		this.writer=writer;
		this.sqiWriter=sqiWriter;
		this.mdsHandler=mdsHandler;
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
						if(n.getMetric_id().equals(FLOW_RATE)) {
							//Flow rate published - add to panel.  addPumpToMainPanel avoids duplication of devices anyway,
							//so just call it here.
							addPumpToMainPanel(dlm.getByUniqueDeviceIdentifier(n.getUnique_device_identifier()));
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
			FXMLLoader loader = new FXMLLoader(PumpWithListener.class.getResource("PumpWithListener.fxml"));
			try {
		        final Parent ui = loader.load();
		        
		        final PumpWithListener controller = ((PumpWithListener) loader.getController());
		        controller.setPump(d,numeric,writer, sqiWriter, dbconn);
		        pumps.getChildren().add(ui);
		        udiToPump.put(d.getUDI(), ui);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	
	private void removePumpFromMainPanel(Device d) {
		pumps.getChildren().remove(udiToPump.get(d.getUDI()));
	}
	
	/**
	 * Use this to allow access to the numeric sample that has a listener attached.
	 * Then if the pump is changed, the listener can be detached from the previous numeric
	 */
	private NumericFx currentPumpNumeric;
	
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
