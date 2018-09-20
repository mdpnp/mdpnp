package org.mdpnp.apps.testapp.oximetry;

import java.io.IOException;
import java.net.URL;

import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.devices.MDSHandler;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.ApplicationContext;

import com.rti.dds.subscription.Subscriber;

import ice.MDSConnectivity;
import ice.OximetryAveragingObjectiveDataWriter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class OximetryTestApplicationFactory implements IceApplicationProvider {
	
	private IceApplicationProvider.AppType type=new IceApplicationProvider.AppType(
		"Oximetry Devices", "Disabled", (URL) OximetryTestApplicationFactory.class.getResource("oximetry.png"), 0.75, false
	);

	@Override
	public AppType getAppType() {
		// TODO Auto-generated method stub
		return type;
	}

	@Override
	public IceApplicationProvider.IceApp create(ApplicationContext parentContext) throws IOException {
		
		final DeviceListModel deviceListModel = parentContext.getBean("deviceListModel", DeviceListModel.class);
		
		final NumericFxList numericList = parentContext.getBean("numericList", NumericFxList.class);
		
		final Subscriber subscriber = (Subscriber) parentContext.getBean("subscriber");

        final EventLoop eventLoop = (EventLoop) parentContext.getBean("eventLoop");
        
        //"oximetryObjectiveWriter" is a new bean in IceAppContainerContext.xml
        final OximetryAveragingObjectiveDataWriter objectiveWriter=(OximetryAveragingObjectiveDataWriter) parentContext.getBean("oximetryObjectiveWriter");
        
        final MDSHandler mdsHandler=(MDSHandler)parentContext.getBean("mdsConnectivity",MDSHandler.class);
        mdsHandler.start();

        FXMLLoader loader = new FXMLLoader(OximetryTestApplication.class.getResource("OximetryTestApplication.fxml"));

        final Parent ui = loader.load();

        final OximetryTestApplication controller = ((OximetryTestApplication) loader.getController());
        
        controller.set(deviceListModel, numericList, objectiveWriter, mdsHandler);

        controller.start(eventLoop, subscriber);
		
		return new IceApplicationProvider.IceApp() {

			@Override
			public AppType getDescriptor() {
				// TODO Auto-generated method stub
				return type;
			}

			@Override
			public Parent getUI() {
				// TODO Auto-generated method stub
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
				controller.stop();
				
			}
			
		};
	}

}
