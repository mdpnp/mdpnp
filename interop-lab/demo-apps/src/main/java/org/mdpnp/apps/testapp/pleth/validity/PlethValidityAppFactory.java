package org.mdpnp.apps.testapp.pleth.validity;

import java.io.IOException;
import java.net.URL;

import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.apps.testapp.pumps.PumpControllerTestApplication;
import org.springframework.context.ApplicationContext;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class PlethValidityAppFactory implements IceApplicationProvider {
	
	private AppType type=new AppType("Pleth Validation", "noPlethValidation", (URL)PlethValidityAppFactory.class.getResource("pleth.png"), 0.75f, false);
	
	@Override
	public AppType getAppType() {
		return type;
	}

	@Override
	public IceApp create(ApplicationContext parentContext) throws IOException {
		
		final DeviceListModel deviceListModel = parentContext.getBean("deviceListModel", DeviceListModel.class);
		
		/**
		 * The list of numeric values currently in the system
		 */
		final NumericFxList numericList = parentContext.getBean("numericList", NumericFxList.class);
		
		final SampleArrayFxList sampleList = parentContext.getBean("sampleArrayList", SampleArrayFxList.class);
		
		/**
		 * The FXML file handler
		 */
		FXMLLoader loader = new FXMLLoader(PlethValidityAppFactory.class.getResource("PlethValidity.fxml"));
		
		Parent ui=loader.load();
		
		PlethValidityApp app=(PlethValidityApp)loader.getController();
		
		app.set(deviceListModel, numericList, sampleList);
		
		return new IceApp() {

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
				app.start();
			}

			@Override
			public void stop() throws Exception {
				app.stop();
				
			}

			@Override
			public void destroy() throws Exception {
				// TODO Auto-generated method stub
				
			}
			
		};
	}

}
