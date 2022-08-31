package org.mdpnp.apps.testapp.pumps;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

/**
 * A UI controller1 for the AP-4000.  This is to work in the PumpControllerTestApplication,
 * not to interface with the device itself.
 * 
 * @author Simon
 * 
 */
public class AP4000Controller extends AbstractControllablePump {
	
	@FXML VBox veryTop;

	@Override
	public int getChanneCount() {
		return 2;
	}

	@Override
	public void start() {
		try {
			FXMLLoader loader1 = new FXMLLoader(AP4000Controller.class.getResource("neurowave_ap-4000-channel.fxml"));
			Parent ui1 = loader1.load();
			AP4000ChannelController controller1=(AP4000ChannelController)loader1.getController();
			controller1.setDevice(device);
			controller1.setInfusionObjectiveWriter(infusionObjectiveWriter);
			controller1.setInfusionProgramDataWriter(infusionProgramDataWriter);
			controller1.setNumericFxList(numericList);
			controller1.setAlertFxList(alertList);
			controller1.setMyFlowRate("MDC_FLOW_FLUID_PUMP_1");
			controller1.setChannel(1);
			controller1.start();
			
			veryTop.getChildren().add(ui1);
			
			//Repeat for second channel
			FXMLLoader loader2 = new FXMLLoader(AP4000Controller.class.getResource("neurowave_ap-4000-channel.fxml"));
			Parent ui2 = loader2.load();
			AP4000ChannelController controller2=(AP4000ChannelController)loader2.getController();
			controller2.setDevice(device);
			controller2.setInfusionObjectiveWriter(infusionObjectiveWriter);
			controller2.setInfusionProgramDataWriter(infusionProgramDataWriter);
			controller2.setNumericFxList(numericList);
			controller2.setAlertFxList(alertList);
			controller2.setMyFlowRate("MDC_FLOW_FLUID_PUMP_2");
			controller2.setChannel(2);
			controller2.start();
			
			veryTop.getChildren().add(ui2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
	}
	
	
	
}
