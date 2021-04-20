package org.mdpnp.apps.testapp.networkmonitor;

import java.io.IOException;
import java.net.URL;

import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.ApplicationContext;

import com.rti.dds.subscription.Subscriber;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class NetworkMonitorAppFactory implements IceApplicationProvider {

	private IceApplicationProvider.AppType type=new IceApplicationProvider.AppType(
			
			"Network Monitor", "NoNetworkMonitor", (URL) NetworkMonitorAppFactory.class.getResource("networkmonitorapp.png"), 0.75, false
		);

	public NetworkMonitorAppFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public AppType getAppType() {
		return type;
	}

	@Override
	public IceApp create(ApplicationContext parentContext) throws IOException {
		final DeviceListModel deviceListModel = parentContext.getBean("deviceListModel", DeviceListModel.class);
		final NumericFxList numericList = parentContext.getBean("numericList", NumericFxList.class);
		final SampleArrayFxList sampleList = parentContext.getBean("sampleArrayList", SampleArrayFxList.class);
		final Subscriber subscriber = (Subscriber) parentContext.getBean("subscriber");
        final EventLoop eventLoop = (EventLoop) parentContext.getBean("eventLoop");
		FXMLLoader loader = new FXMLLoader(NetworkMonitorApp.class.getResource("NetworkMonitorApp.fxml"));

        final Parent ui = loader.load();
       
        final NetworkMonitorApp controller = ((NetworkMonitorApp) loader.getController());

        controller.set(parentContext, subscriber, deviceListModel, numericList, sampleList);
        controller.start(eventLoop, subscriber);
        
		return new IceApplicationProvider.IceApp() {
			
			@Override
			public void stop() throws Exception {
				controller.stop();
			}
			
			@Override
			public Parent getUI() {
				return ui;
			}
			
			@Override
			public AppType getDescriptor() {
				return getAppType();
			}
			
			@Override
			public void destroy() throws Exception {
				controller.destroy();
			}
			
			@Override
			public void activate(ApplicationContext context) {
				controller.activate();
			}
			
			@Override
			public int getPreferredWidth() {
				return 910;
			}

			@Override
			public int getPreferredHeight() {
				return 400;
			}
		};
	}
}
