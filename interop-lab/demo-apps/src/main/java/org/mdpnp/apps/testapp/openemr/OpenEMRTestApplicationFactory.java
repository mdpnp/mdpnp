package org.mdpnp.apps.testapp.openemr;

import java.io.IOException;
import java.net.URL;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.apps.testapp.IceApplicationProvider.AppType;
import org.mdpnp.apps.testapp.closedloopcontrol.ClosedLoopControlTestApplication;
import org.mdpnp.apps.testapp.patient.EMRFacade;
import org.mdpnp.apps.testapp.patient.EMRFacade.EMRType;
import org.mdpnp.devices.MDSHandler;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.ApplicationContext;

import com.rti.dds.subscription.Subscriber;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class OpenEMRTestApplicationFactory implements IceApplicationProvider {
	
	private IceApplicationProvider.AppType type=new IceApplicationProvider.AppType(
			"OpenEMR", "NoEMR", (URL) OpenEMRTestApplicationFactory.class.getResource("openemr.png"), 0.75, false
		);
	

	public OpenEMRTestApplicationFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public AppType getAppType() {
		// TODO Auto-generated method stub
		return type;
	}

	@Override
	public IceApp create(ApplicationContext parentContext) throws IOException {
		/*
		 * The first thing we do is to get an emr instance, and check if it is
		 * an OpenEMR instance.  If it is not, there is no point creating the OpenEMR
		 * application.
		 */
		final EMRFacade emr = (EMRFacade) parentContext.getBean("emr");

		FXMLLoader loader = new FXMLLoader(OpenEMRTestApplication.class.getResource("OpenEMRTestApplication.fxml"));

        final Parent ui = loader.load();
       
        final OpenEMRTestApplication controller = ((OpenEMRTestApplication) loader.getController());
        
        final Subscriber subscriber = (Subscriber) parentContext.getBean("subscriber");

        final EventLoop eventLoop = (EventLoop) parentContext.getBean("eventLoop");
        
        final MDSHandler mdsHandler=(MDSHandler)parentContext.getBean("mdsConnectivity",MDSHandler.class);
        mdsHandler.start();
        
        controller.set(mdsHandler, emr);
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
				//controller.activate();
				
			}

			@Override
			public void stop() throws Exception {
				//controller.stop();
				
			}

			@Override
			public void destroy() throws Exception {
				//controller.destroy();
				
			}
			
			@Override
			public int getPreferredWidth() {
				return 800;
			}
			
			public int getPreferredHeight() {
				return 600;
			}
			
		};
	}
	
}
