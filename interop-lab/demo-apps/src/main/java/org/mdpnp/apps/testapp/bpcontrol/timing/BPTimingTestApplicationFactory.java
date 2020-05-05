package org.mdpnp.apps.testapp.bpcontrol.timing;

import java.io.IOException;
import java.net.URL;

import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.devices.MDSHandler;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.ApplicationContext;

import com.rti.dds.subscription.Subscriber;

import ice.BPObjectiveDataWriter;
import ice.FlowRateObjectiveDataWriter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class BPTimingTestApplicationFactory implements IceApplicationProvider {
	
	private IceApplicationProvider.AppType type=new IceApplicationProvider.AppType(
			"BP Timing", "NoBPTiming", (URL) BPTimingTestApplicationFactory.class.getResource("bp.png"), 0.75, false
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
        
        //"flowRateObjectiveWriter" is a new bean in IceAppContainerContext.xml
        final BPObjectiveDataWriter objectiveWriter=(BPObjectiveDataWriter) parentContext.getBean("bpObjectiveWriter");
        
        final MDSHandler mdsHandler=(MDSHandler)parentContext.getBean("mdsConnectivity",MDSHandler.class);
        mdsHandler.start();
		
		FXMLLoader loader = new FXMLLoader(BPTimingTestApplication.class.getResource("BPTimeTestApplication.fxml"));

        final Parent ui = loader.load();
        
        final BPTimingTestApplication controller = ((BPTimingTestApplication) loader.getController());
        
        controller.set(deviceListModel, numericList, sampleList, objectiveWriter, mdsHandler);
        
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
			
		};
	}

}
