package org.mdpnp.apps.testapp.bpcontrol;

import java.net.URL;

import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFx;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.devices.MDSHandler;
import org.mdpnp.rtiapi.data.EventLoop;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.fxml.FXML;

import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.subscription.Subscriber;

import ice.BPObjectiveDataWriter;
import ice.BPPauseResumeObjectiveDataWriter;

public class BPControllerApplication {
	
	private DeviceListModel dlm;
	private NumericFxList numeric;
	private SampleArrayFxList samples;
	private BPObjectiveDataWriter bpWriter;
	private BPPauseResumeObjectiveDataWriter pauseResumeWriter;
	private MDSHandler mdsHandler;
	
	@FXML private Label deviceID;
	@FXML private TextField requestedChange;
	
	
	private String currentDeviceId;
	
	public BPControllerApplication() {
		// TODO Auto-generated constructor stub
	}

	public void set(DeviceListModel dlm, NumericFxList numeric, SampleArrayFxList samples,
			BPObjectiveDataWriter writer, BPPauseResumeObjectiveDataWriter pauseResumeWriter, MDSHandler mdsHandler) {
		this.dlm=dlm;
		this.numeric=numeric;
		this.samples=samples;
		this.bpWriter=writer;
		this.mdsHandler=mdsHandler;
		this.pauseResumeWriter=pauseResumeWriter;
	}

	public void start(EventLoop eventLoop, Subscriber subscriber) {
		//Rely on addition of metrics to add devices...
		samples.addListener(new ListChangeListener<SampleArrayFx>() {
			@Override
			public void onChanged(Change<? extends SampleArrayFx> change) {
				while(change.next()) {
					change.getAddedSubList().forEach( s -> {
						if(s.getMetric_id().equals(rosetta.MDC_PRESS_BLD_ART_ABP.VALUE)) {
							//Flow rate published - add to panel.  addPumpToMainPanel avoids duplication of devices anyway,
							//so just call it here.
							currentDeviceId=s.getUnique_device_identifier();
							deviceID.setText(currentDeviceId);
						}
					});
				}
			}
		});
		
	}
	
	public void requestTheChange() {
		ice.BPObjective objective=new ice.BPObjective();
		float desiredChange=Float.parseFloat(requestedChange.getText());
		objective.changeBy=desiredChange;
		objective.unique_device_identifier=currentDeviceId;
		bpWriter.write(objective, InstanceHandle_t.HANDLE_NIL);
	}
	
	public void runMonitor() {
		ice.BPPauseResumeObjective objective=new ice.BPPauseResumeObjective();
		objective.running=true;
		objective.unique_device_identifier=currentDeviceId;
		pauseResumeWriter.write(objective, InstanceHandle_t.HANDLE_NIL);
	}
	
	public void stopMonitor() {
		ice.BPPauseResumeObjective objective=new ice.BPPauseResumeObjective();
		objective.running=false;
		objective.unique_device_identifier=currentDeviceId;
		pauseResumeWriter.write(objective, InstanceHandle_t.HANDLE_NIL);
	}


	public void activate() {
		// TODO Auto-generated method stub
		
	}

	public void stop() {
		// TODO Auto-generated method stub
		
	}
	
	public void destroy() {
		
	}
	

}
