package org.mdpnp.apps.testapp.dicom;

import java.io.IOException;
import java.net.URL;

import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.apps.testapp.IceApplicationProvider.AppType;
import org.mdpnp.devices.MDSHandler;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.ApplicationContext;

import com.rti.dds.subscription.Subscriber;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class DicomReplicationAppFactory implements IceApplicationProvider {
	
	private IceApplicationProvider.AppType type=new IceApplicationProvider.AppType(
			"Dicom", "NoDicom", (URL) DicomReplicationAppFactory.class.getResource("dicom-logo.jpg"), 0.75, false
		);

	public DicomReplicationAppFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public AppType getAppType() {
		// TODO Auto-generated method stub
		return type;
	}

	@Override
	public IceApp create(ApplicationContext parentContext) throws IOException {
		FXMLLoader loader = new FXMLLoader(DicomReplicationAppFactory.class.getResource("DicomApp.fxml"));

        final Parent ui = loader.load();
       
        final DicomReplicationApp controller = ((DicomReplicationApp) loader.getController());
		
        final Subscriber subscriber = (Subscriber) parentContext.getBean("subscriber");

        final EventLoop eventLoop = (EventLoop) parentContext.getBean("eventLoop");
        
        final MDSHandler mdsHandler=(MDSHandler)parentContext.getBean("mdsConnectivity",MDSHandler.class);
        mdsHandler.start();
        
        controller.set(parentContext, mdsHandler, subscriber);
        controller.start(eventLoop, subscriber);
		
		return new IceApplicationProvider.IceApp() {
			
			@Override
			public void stop() throws Exception {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Parent getUI() {
				return ui;
			}
			
			@Override
			public AppType getDescriptor() {
				// TODO Auto-generated method stub
				return type;
			}
			
			@Override
			public void destroy() throws Exception {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void activate(ApplicationContext context) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public int getPreferredWidth() {
				return 1024;
			}

			@Override
			public int getPreferredHeight() {
				return 768;
			}
		};
	}
}
