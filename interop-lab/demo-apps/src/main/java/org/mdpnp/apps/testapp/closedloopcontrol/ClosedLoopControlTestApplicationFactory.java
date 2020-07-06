package org.mdpnp.apps.testapp.closedloopcontrol;

import java.io.IOException;
import java.net.URL;

import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.apps.testapp.patient.EMRFacade;
import org.mdpnp.apps.testapp.patient.EMRFacade.EMRFacadeFactory;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.devices.MDSHandler;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.ApplicationContext;

import com.rti.dds.subscription.Subscriber;

import ice.FlowRateObjectiveDataWriter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class ClosedLoopControlTestApplicationFactory implements IceApplicationProvider {
	
	private IceApplicationProvider.AppType type=new IceApplicationProvider.AppType(
			"Closed Loop Control", "NoCLC", (URL) ClosedLoopControlTestApplicationFactory.class.getResource("loop.png"), 0.75, false
		);

	@Override
	public AppType getAppType() {
		// TODO Auto-generated method stub
		return type;
	}

	@Override
	public IceApp create(ApplicationContext parentContext) throws IOException {
		
		final DeviceListModel deviceListModel = parentContext.getBean("deviceListModel", DeviceListModel.class);
		
		final NumericFxList numericList = parentContext.getBean("numericList", NumericFxList.class);
		
		final SampleArrayFxList sampleList = parentContext.getBean("sampleArrayList", SampleArrayFxList.class);
		
		final Subscriber subscriber = (Subscriber) parentContext.getBean("subscriber");

        final EventLoop eventLoop = (EventLoop) parentContext.getBean("eventLoop");
        
        final VitalModel vitalModel= (VitalModel) parentContext.getBean("vitalModel");
        
        //"flowRateObjectiveWriter" is a new bean in IceAppContainerContext.xml
        final FlowRateObjectiveDataWriter objectiveWriter=(FlowRateObjectiveDataWriter) parentContext.getBean("flowRateObjectiveWriter");
        
        final MDSHandler mdsHandler=(MDSHandler)parentContext.getBean("mdsConnectivity",MDSHandler.class);
        mdsHandler.start();

        final EMRFacade emr=(EMRFacade) parentContext.getBean("emr");
		
		FXMLLoader loader = new FXMLLoader(ClosedLoopControlTestApplication.class.getResource("ClosedLoopControlTestApplication.fxml"));

        final Parent ui = loader.load();
       
        final ClosedLoopControlTestApplication controller = ((ClosedLoopControlTestApplication) loader.getController());
        
        controller.set(parentContext, deviceListModel, numericList, sampleList, objectiveWriter, mdsHandler, vitalModel, subscriber, emr);
        
        controller.start(eventLoop, subscriber);
		
		return new IceApplicationProvider.IceApp() {

			@Override
			public AppType getDescriptor() {
				return type;
			}

			@Override
			public Parent getUI() {
				return ui;
			}

			@Override
			public void activate(ApplicationContext context) {
				controller.activate();
				
			}

			@Override
			public void stop() throws Exception {
				controller.stop();
				
			}

			@Override
			public void destroy() throws Exception {
				controller.destroy();
				
			}
			
			@Override
			public int getPreferredWidth() {
				return 800;
			}
			
			public int getPreferredHeight() {
				return 800;
			}
			
		};
	}

}
