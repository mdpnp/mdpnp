package org.mdpnp.apps.testapp.alaris;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;

import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.patient.EMRFacade;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.devices.AbstractDevice.InstanceHolder;
import org.mdpnp.devices.DeviceDriverProvider;
import org.mdpnp.devices.MDSHandler;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.rti.dds.subscription.Subscriber;

import ice.FlowRateObjectiveDataWriter;
import ice.Numeric;
import ice.Patient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class AlarisSimulatorApplication {
	
	private static final Logger log = LoggerFactory.getLogger(AlarisSimulatorApplication.class);
	
	@FXML
	VBox main;
	
	@FXML
	TextArea loggingArea;
	
	
	private NumericFxList numericList;

	private ApplicationContext parentContext;

	private Subscriber subscriber;
	
	/**
	 * The "current" patient, used to determine if the patient has changed
	 */
	private Patient currentPatient;

	private MDSHandler mdsHandler;

	private EventLoop eventLoop;
	
	/**
	 * A reader to read lines from the requesting device, e.g. EasyTIVA
	 */
	private BufferedReader fromRequestor;
	
	/**
	 * An output stream to send bytes to the requesting device, e.g. EasyTIVA
	 */
	private BufferedOutputStream toRequestor;
	
	/**
	 * Last flow rate requested by the client.
	 */
	private float lastRequestedRate=0.1f;
	
	/**
	 * The volume infused.  Can be reset by VI_CLEAR command;
	 */
	private float volumeInfused=0;
	
	/**
	 * Instance holder to allow the flow rate to be published.
	 */
	private InstanceHolder<Numeric> flowRateHolder;
	
	public void set(ApplicationContext parentContext, DeviceListModel deviceListModel, NumericFxList numericList,
			SampleArrayFxList sampleList, FlowRateObjectiveDataWriter objectiveWriter, MDSHandler mdsHandler,
			VitalModel vitalModel, Subscriber subscriber, EMRFacade emr) {
		this.numericList=numericList;
		this.parentContext=parentContext;
		this.subscriber=subscriber;
		this.mdsHandler=mdsHandler;
		
	}

	public void start(EventLoop eventLoop, Subscriber subscriber) {
		try {
			FXMLLoader loader1 = new FXMLLoader(SingleSimAlaris.class.getResource("SingleSimAlaris.fxml"));
			Parent ui1 = loader1.load();
			SingleSimAlaris controller1=(SingleSimAlaris)loader1.getController();
			controller1.set(parentContext, mdsHandler, subscriber, loggingArea);
			controller1.start(eventLoop, subscriber);
	//		controller1.setDevice(device);
	//		controller1.setInfusionObjectiveWriter(infusionObjectiveWriter);
	//		controller1.setInfusionProgramDataWriter(infusionProgramDataWriter);
	//		controller1.setNumericFxList(numericList);
	//		controller1.setAlertFxList(alertList);
	//		controller1.setMyFlowRate("MDC_FLOW_FLUID_PUMP_1");
	//		controller1.setChannel(1);
	//		controller1.start();
			
			main.getChildren().add(0,ui1);
			
			//Repeat for second channel
			FXMLLoader loader2 = new FXMLLoader(SingleSimAlaris.class.getResource("SingleSimAlaris.fxml"));
			Parent ui2 = loader2.load();
			SingleSimAlaris controller2=(SingleSimAlaris)loader2.getController();
			controller2.set(parentContext, mdsHandler, subscriber, loggingArea);
			controller2.start(eventLoop, subscriber);
	//		controller2.setDevice(device);
	//		controller2.setInfusionObjectiveWriter(infusionObjectiveWriter);
	//		controller2.setInfusionProgramDataWriter(infusionProgramDataWriter);
	//		controller2.setNumericFxList(numericList);
	//		controller2.setAlertFxList(alertList);
	//		controller2.setMyFlowRate("MDC_FLOW_FLUID_PUMP_2");
	//		controller2.setChannel(2);
	//		controller2.start();
			
			main.getChildren().add(0,ui2);
		} catch (IOException ioe) {
			log.error("Failed to load child pumps", ioe);
		}
	}

	public void activate() {
		// TODO Auto-generated method stub
		
	}

	public void stop() {
		// TODO Auto-generated method stub
		
	}

	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	
	private DeviceDriverProvider.DeviceAdapter simAlarisDeviceAdapter;
	
	
	
	
	
	
	

}
