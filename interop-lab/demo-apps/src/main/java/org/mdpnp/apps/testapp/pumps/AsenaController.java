package org.mdpnp.apps.testapp.pumps;

import java.io.IOException;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.mdpnp.apps.fxbeans.AlertFx;
import org.mdpnp.apps.fxbeans.AlertFxList;
import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.testapp.Device;

import com.rti.dds.infrastructure.InstanceHandle_t;

import ice.InfusionObjectiveDataWriter;
import ice.InfusionProgram;
import ice.InfusionProgramDataWriter;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * A UI controller1 for the Alaris Asena.  This is to work in the PumpControllerTestApplication,
 * not to interface with the device itself.
 * 
 * @author Simon
 * 
 */
public class AsenaController extends AbstractControllablePump {
	
//	@FXML GridPane main;
	@FXML
	Label sysIDLabel;
	@FXML
	Label caseIDLabel;
	@FXML
	Label drugLabel;
	@FXML
	Label infusionRateLabel;
	@FXML
	Label volumeInfusedLabel;
	@FXML
	Label targetVolumeInfusedLabel;
	@FXML
	Label bolusInfusedLabel;
	@FXML
	Label channelLabel;
	@FXML
	Spinner<Double> targetInfusionRate;
	@FXML
	Spinner<Double>	targetVTBI;
	@FXML
	Button pauseResumeInfusion;
	@FXML
	Button programInfusion;
	@FXML
	Spinner<Double> bolusDose;
	@FXML
	Spinner<Double> bolusRate;
	@FXML
	Button startBolus;
	@FXML
	Label pumpSerialLabel;
	@FXML
	Label pumpUDILabel;
	@FXML
	Label modelLabel;
	
	private final int myChannel = 1;
	
	
	private String myDrugMetric, myVTBIRemainingMetric, myTargetVTBIMetric;
	
	private String myFlowRate = "MDC_FLOW_FLUID_PUMP";
	

	/**
	 * The current infusion rate for the head.  We have this in a variable so that we can reference
	 * it in the pause/resume code, where we assume that if the infusion rate is 0, then we are going
	 * to resume, because we must be paused.
	 */
	private float currentInfusionRate;
	
	public AsenaController() {
		// TODO Auto-generated constructor stub
	}
	
	private void assign(NumericFx n) {
		if( ! n.getUnique_device_identifier().equals(device.getUDI())) {
			//Not our device
			return;
		}
		if(n.getMetric_id().equals(myFlowRate)) {
			//Our device and our metric.
			addListener(n, infusionRateLabel);
		}
		if(n.getMetric_id().equals("VOLUME_INFUSED")) {
			addListener(n, volumeInfusedLabel);
		}
		if(n.getMetric_id().equals("VTBI")) {
			addListener(n, targetVolumeInfusedLabel);
		}
		
	}
	
	private void assign(AlertFx a) {
		if( ! a.getUnique_device_identifier().equals(device.getUDI())) {
			//Not our device
			return;
		}
		if(a.getIdentifier().equals("serialNumber")) {
			//It seems VERY unlikely that this will change, but we'll handle it.
			addAlertListener(a,pumpSerialLabel);
		}
		if(a.getIdentifier().equals("UDI")) {
			addAlertListener(a, pumpUDILabel);
		}
		if(a.getIdentifier().equals("Model")) {
			addAlertListener(a, modelLabel);
		}
	}

	@Override
	public void start() {
		try {
//			
			
			numericList.addListener(new ListChangeListener<NumericFx>() {

				@Override
				public void onChanged(Change<? extends NumericFx> c) {
					while(c.next()) {
						c.getAddedSubList().forEach( n -> {
							assign(n);
						});
					}
				}
			});
			numericList.forEach( n -> {
				assign(n);
			});
			
			alertList.addListener(new ListChangeListener<AlertFx>() {

				@Override
				public void onChanged(Change<? extends AlertFx> c) {
					while(c.next()) {
						c.getAddedSubList().forEach( a -> {
							assign(a);
						});
					}
				}
			});
			alertList.forEach( a -> {
				assign(a);
			});
			
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}
		
	}
	
	
	private void addAlertListener(AlertFx a, Label targetLabel) {
		if(targetLabel.getUserData()==null) {
			//Set the user data to the base text of the label, so that we can use the base text
			//as the description of the label.
			targetLabel.setUserData(targetLabel.getText());
		}

		targetLabel.textProperty().bindBidirectional(a.textProperty(), new StringConverter<String>() {

			@Override
			public String toString(String object) {
				return targetLabel.getUserData()+" "+object;
			}

			@Override
			public String fromString(String string) {
				System.err.println("stringconverter fromString called with "+string);
				return null;
			}
			
		});
		
		
	}

	/**
	 * Adds a listener to the given numeric.  We listen for changes to the source timestamp,
	 * even though what we really care about is the numeric value.  That's because the source
	 * timestamp is always changing, even if the numeric value is not, so this change listener
	 * gets triggered even if the value is not changing.  This makes the bindDirectional look odd,
	 * because we don't use the bound value of Date to get the contents of the label.
	 * 
	 * @param n
	 */
	private void addListener(NumericFx n, Label targetLabel) {
		
		if(n.getMetric_id().equals(myFlowRate)) {
			n.device_timeProperty().addListener( l -> {
				currentInfusionRate=n.getValue();
				if(n.getValue()>0) {
					pauseResumeInfusion.setText("Pause");
				} else {
					pauseResumeInfusion.setText("Resume");
				}
			});
		}
		
		if(targetLabel.getUserData()==null) {
			//Set the user data to the base text of the label, so that we can use the base text
			//as the description of the label.
			targetLabel.setUserData(targetLabel.getText());
		}
		
		targetLabel.textProperty().bindBidirectional(n.presentation_timeProperty(), new StringConverter<Date>() {

			@Override
			public String toString(Date object) {
				return targetLabel.getUserData()+" "+n.getValue();
			}

			@Override
			public Date fromString(String string) {
				System.err.println("stringconverter fromString called with "+string);
				return null;
			}
			
		});
	}
	
	private void pauseInfusion() {
		Alert confirm=new Alert(AlertType.CONFIRMATION,"Confirm you want to pause infusion for channel "+myChannel,new ButtonType[] {ButtonType.OK,ButtonType.CANCEL});
		Optional<ButtonType> result=confirm.showAndWait();
		try {
			ButtonType bt=result.get();
			if(bt.equals(ButtonType.OK)) {
				ice.InfusionObjective objective=new ice.InfusionObjective();
				objective.stopInfusion=true;
				objective.unique_device_identifier=device.getUDI();
				objective.head=myChannel;
				infusionObjectiveWriter.write(objective, InstanceHandle_t.HANDLE_NIL);
			}
		} catch (NoSuchElementException nsee) {
			//No return from dialog - no action...
			System.err.println("dialog was closed without result");
		}
	}
	
	private void resumeInfusion() {
		Alert confirm=new Alert(AlertType.CONFIRMATION,"Confirm you want to resume infusion for channel "+myChannel,new ButtonType[] {ButtonType.OK,ButtonType.CANCEL});
		Optional<ButtonType> result=confirm.showAndWait();
		try {
			ButtonType bt=result.get();
			if(bt.equals(ButtonType.OK)) {
				ice.InfusionObjective objective=new ice.InfusionObjective();
				objective.stopInfusion=false;
				objective.unique_device_identifier=device.getUDI();
				objective.head=myChannel;
				infusionObjectiveWriter.write(objective, InstanceHandle_t.HANDLE_NIL);
			}
		} catch (NoSuchElementException nsee) {
			//No return from dialog - no action...
			System.err.println("dialog was closed without result");
		}
	}
	
	public void pauseResumeInfusion() {
		if(currentInfusionRate==0) {
			resumeInfusion();
		} else {
			pauseInfusion();
		}
	}
	
	/**
	 * Program an infusion.  For the current UI design, we are handling the programming of
	 * infusion and bolus separately, even though in the API for AP-4000, they are programmed
	 * using the same call.  So this code ignores bolus, by using the convention of -1 in the
	 * relevant fields, and the bolus method will ignore the infusion fields.
	 */
	public void programInfusion() {
		String msg="Confirm you want to set infusion rate "+targetInfusionRate.getValue().floatValue()+" ml/h and VTBI "+targetVTBI.getValue().floatValue() + " ml";
		Alert confirm=new Alert(AlertType.CONFIRMATION,msg,new ButtonType[] {ButtonType.OK,ButtonType.CANCEL});
		Optional<ButtonType> result=confirm.showAndWait();
		try {
			ButtonType bt=result.get();
			if(bt.equals(ButtonType.OK)) {
				InfusionProgram program=new InfusionProgram();
				program.head=myChannel;	//TODO: variable here
				program.bolusRate=-1;	//Ignore
				program.bolusVolume=-1;	//Ignore
				program.infusionRate=targetInfusionRate.getValue().floatValue();
				program.VTBI=targetVTBI.getValue().floatValue();
				program.unique_device_identifier=device.getUDI();
				program.requestor="ControlApp";	//Not really used at the moment.
				infusionProgramDataWriter.write(program, InstanceHandle_t.HANDLE_NIL);
			}
		} catch (NoSuchElementException nsee) {
			//No return from dialog - no action...
			System.err.println("dialog was closed without result");
		}
	}
	
	/**
	 * Program an infusion.  For the current UI design, we are handling the programming of
	 * infusion and bolus separately, even though in the API for AP-4000, they are programmed
	 * using the same call.  So this code ignores bolus, by using the convention of -1 in the
	 * relevant fields, and the bolus method will ignore the infusion fields.
	 */
	public void programBolus() {
//		String msg="Confirm you want to set channel "+myChannel+" to have bolus rate "+bolusRate.getValue()+" and dose "+bolusDose.getValue();
//		Alert confirm=new Alert(AlertType.CONFIRMATION,msg,new ButtonType[] {ButtonType.OK,ButtonType.CANCEL});
//		Optional<ButtonType> result=confirm.showAndWait();
		String msg="Bolus is not supported by Alaris Asena pump";
		Alert confirm=new Alert(AlertType.INFORMATION,msg,new ButtonType[] {ButtonType.OK});
		Optional<ButtonType> result=confirm.showAndWait();
//		try {
//			InfusionProgram program=new InfusionProgram();
//			program.head=myChannel;	//TODO: variable here
//			program.bolusRate=bolusRate.getValue();
//			program.bolusVolume=bolusDose.getValue();
//			program.infusionRate=-1;	//Ignore
//			program.VTBI=-1;	//Ignore
//			program.unique_device_identifier=device.getUDI();
//			program.requestor="ControlApp";	//Not really used at the moment.
//			infusionProgramDataWriter.write(program, InstanceHandle_t.HANDLE_NIL);
//		} catch (NoSuchElementException nsee) {
//			//No return from dialog - no action...
//			System.err.println("dialog was closed without result");
//		}
	}
	
	
	
}
