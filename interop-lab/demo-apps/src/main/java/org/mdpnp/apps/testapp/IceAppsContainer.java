/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.apps.testapp;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import com.google.common.eventbus.EventBus;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.mdpnp.apps.device.DeviceDataMonitor;
import org.mdpnp.apps.fxbeans.InfusionStatusFxList;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.IceApplicationProvider.AppType;
import org.mdpnp.apps.testapp.device.DeviceView;
import org.mdpnp.apps.testapp.patient.EMRFacade;
import org.mdpnp.apps.testapp.patient.PatientInfo;
import org.mdpnp.devices.BuildInfo;
import org.mdpnp.devices.MDSHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;
import com.sun.glass.ui.Screen;

/**
 * Container responsible for discovery and hosting of ICE applications. Its main
 * purpose is enforcement of standard life-cycle of the participating
 * components.
 *
 * Any application that implements IceApplicationProvider interface and complies
 * with java's <a href=
 * "http://docs.oracle.com/javase/7/docs/api/java/util/ServiceLoader.html"
 * >ServiceLoader</a> pattern will be discovered by this container. The
 * application could be either JavaFX UI or headless background thread (but not
 * a console-based green-screen). For the UI apps, it could be either standalone
 * frames or embeddable panels.
 *
 * Regardless of the implementation details, all apps will be started the same
 * way via {@link IceApplicationProvider.IceApp#activate} API, and shut down via
 * call to {@link IceApplicationProvider.IceApp#stop}. It is assumed that the
 * apps could be stateful - i.e when created at the activate of the container
 * they could span thread, keep references to resources as long as they dispose
 * of them properly in the implementation of
 * {@link IceApplicationProvider.IceApp#destroy} API.
 *
 * @see IceApplicationProvider
 *
 */
public class IceAppsContainer extends IceApplication {

    private static final Logger log = LoggerFactory.getLogger(IceAppsContainer.class);

    @SuppressWarnings("unused")
    private static IceApplicationProvider.AppType Main = new IceApplicationProvider.AppType("Main Menu", null, (URL) null, 0, false);
    private static IceApplicationProvider.AppType Device = new IceApplicationProvider.AppType("Device Info", null, (URL) null, 0, false);

    DeviceApp driverWrapper = new DeviceApp();

    final Map<IceApplicationProvider.AppType, IceApplicationProvider.IceApp> activeApps = new HashMap<>();

    private DemoPanel panelController;
    private Parent panelRoot;

    @SuppressWarnings("unused")
    private MainMenu mainMenuController;
    private Parent mainMenuRoot;

    private PartitionChooserModel partitionChooserModel;
    
    /**
     * SK - to keep and use for child popup windows
     */
    private Stage parentStage;
    
    private Hashtable<Object, Stage> appStageMap;
    private Hashtable<Stage, double[]> coordinates;

    public IceAppsContainer() {

    }

    private void activateGoBack(final IceApplicationProvider.IceApp app) {
    	
    	if(appStageMap==null) {
    		appStageMap=new Hashtable<>();
    	}
    	if(coordinates==null) {
    		coordinates=new Hashtable<>();
    	}
    	
    	/*
    	 * BASICALLY, WE NEED TO STICK SOMETHING ELSE HERE TO HOLD app.getUI()
    	 * AND DISPLAY THAT INSTEAD OF REFERRING TO panelController
    	 */
    	
        final String appName = app.getDescriptor().getName();
        //System.err.println("appName is "+appName+" with class "+app.getClass().getName());
        Stage possibleStage;
        if(app instanceof DeviceApp) {
        	DeviceApp da=(DeviceApp)app;
        	//System.err.println("Device app checking appStageMap for "+da.getUID());
        	possibleStage=appStageMap.get(da.getUID());
        } else {
        	possibleStage=appStageMap.get(app);
        }
        
        if(possibleStage==null) {
        	//System.err.println("That does NOT have a stage already");
        	Stage newStage= new Stage();
        	newStage.initOwner(parentStage);
        	newStage.setTitle(appName);
        	final Scene sceneToSet=new Scene(app.getUI(),app.getPreferredWidth(),app.getPreferredHeight());
        	newStage.setScene(sceneToSet);
        	if(app instanceof DeviceApp) {
        		appStageMap.put( ((DeviceApp)app).getUID(), newStage);
        	} else {
        		appStageMap.put(app, newStage);
        	}
        	if(app.getDescriptor().isCoordinatorApp()) {
        		//Extra handling required.
        		newStage.focusedProperty().addListener(new ChangeListener<Boolean>() {

					@Override
					public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
						if(oldValue) {
							panelController.patientsLabel.setVisible(true);
							panelController.patientSelector.setVisible(true);
						} else {
							panelController.patientsLabel.setVisible(false);
							panelController.patientSelector.setVisible(false);
						}
					}
				});
        	}
        	newStage.show();
        	newStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

    			@Override
    			public void handle(WindowEvent event) {
    				//System.err.println("Consuming the onCloseRequest...");
    				try {
    					//Do not stop DeviceApps
    					if( ! (app instanceof DeviceApp) ) {
    						app.stop();
    					}
    					//x position, y position, width, height
    					double xy[]=new double[4];
    					xy[0]=newStage.getX();
    					xy[1]=newStage.getY();
    					xy[2]=newStage.getWidth();
    					xy[3]=newStage.getHeight();
    					coordinates.put(newStage, xy);
    				} catch (Exception ex) {
    					log.error("Failed to stop " + appName, ex);
    				}
    				if(app.getDescriptor().isCoordinatorApp()) {
	    				panelController.patientsLabel.setVisible(true);
		                panelController.patientSelector.setVisible(true);
    				}
    				newStage.hide();
    			}
        		
        	});
        	
        	
        	
        } else {
        	//System.err.println("That does have a stage already");
        	double[] xy=coordinates.get(possibleStage);
        	/*
        	 * Seemed to get an NPE here without this !=null, check, but why?
        	 * How is xy null and therefore coordinates.put not been called,
        	 * is stage already exists? 
        	 */
        	if(xy!=null) {
	        	possibleStage.setX(xy[0]);
	        	possibleStage.setY(xy[1]);
	        	possibleStage.setWidth(xy[2]);
	        	possibleStage.setHeight(xy[3]);
        	}
        	possibleStage.show();
        	
        	app.getUI().setVisible(true);
        }
    	


        Runnable goBackAction = new Runnable() {
            public void run() {
                try {
                    panelController.appTitle.setText("");
                    app.stop();
                    panelController.patientSelector.setVisible(true);
                    panelController.patientsLabel.setVisible(true);
                } catch (Exception ex) {
                    log.error("Failed to stop " + appName, ex);
                }
            }
        };
        if(app.getDescriptor().isCoordinatorApp()) {
            panelController.patientsLabel.setVisible(false);
            panelController.patientSelector.setVisible(false);
        }
        panelController.back.setVisible(true);
//        panelController.content.setCenter(app.getUI());
        panelController.getBack().setOnAction(new GoBackAction(goBackAction));
    }

    /**
     * Utility class to be installed as an action handler on the 'back' button
     * of the main panel to stop the current app and bring the pre-defined
     * 'main' screen back forward.
     */
    private class GoBackAction implements EventHandler<ActionEvent> {
        final Runnable cmdOnExit;

        public GoBackAction(Runnable r) {
            cmdOnExit = r;
        }

        @Override
        public void handle(ActionEvent event) {
            if (null != cmdOnExit) {
                try {
                    cmdOnExit.run();
                } catch (Throwable t) {
                    log.error("Error in 'go back' logic", t);
                }
            }
            panelController.content.setCenter(mainMenuRoot);
            // TODO make this more elegant
            ((Button) event.getSource()).setVisible(false);
            ((Button) event.getSource()).setOnAction(null);
        }
    }

    /**
     * Utility class to wrap the driver object and preset it as a full-fledged
     * participating application. The main purpose is to enable a formal
     * life-cycle for startup and shutdown of the container.
     */
    private static class DeviceApp implements IceApplicationProvider.IceApp {
        private Parent ui;
        private DeviceView devicePanel;
        /**
         * A unique identifier to use for separating devices in the "multi device display" era
         */
        private String uid="not-started-yet";

        @Override
        public IceApplicationProvider.AppType getDescriptor() {
            return Device;
        }

        @Override
        public Parent getUI() {
            return ui;
        }

        @Override
        public void activate(ApplicationContext context) {
            throw new IllegalStateException("Internal activate(context,driver) API should be called for driver wrapper");
        }

        public void start(ApplicationContext context, final Device device) throws IOException {

            final NumericFxList numericList = context.getBean("numericList", NumericFxList.class);
            final SampleArrayFxList sampleArrayList = context.getBean("sampleArrayList", SampleArrayFxList.class);
            final InfusionStatusFxList infusionStatusList = context.getBean("infusionStatusList", InfusionStatusFxList.class);
            final DeviceListModel deviceListModel = context.getBean("deviceListModel", DeviceListModel.class);
            
            FXMLLoader loader = new FXMLLoader(DeviceView.class.getResource("DeviceView.fxml"));
            ui = loader.load();
            devicePanel = loader.getController();
            uid=device.getUDI();

            DeviceDataMonitor deviceMonitor = devicePanel.getModel();
            if (null != deviceMonitor) {
                deviceMonitor.stop();
            }
            deviceMonitor = new DeviceDataMonitor(device.getUDI(), deviceListModel, numericList, sampleArrayList, infusionStatusList);
            devicePanel.set(deviceMonitor);
        }

        @Override
        public void stop() {
            if (devicePanel != null) {
                DeviceDataMonitor deviceMonitor = devicePanel.getModel();
                if (null != deviceMonitor) {
                    deviceMonitor.stop();
                }
                devicePanel.set((DeviceDataMonitor) null);
            }
        }

        @Override
        public void destroy() {
        }
        
        public String getUID() {
        	return uid;
        }
        
        
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("OpenICE");
        this.parentStage=primaryStage;
        
        int visibleWidth  = Screen.getMainScreen().getVisibleWidth();
        int visibleHeight = Screen.getMainScreen().getVisibleHeight();
        
        int width = (int) (0.85 * visibleWidth);
        int height = (int) (0.85 * visibleHeight);
        
        Scene panelScene = new Scene(panelRoot);
        URL url=getClass().getResource("ice-apps-container.css");
        if(url!=null) {
            String s=url.toExternalForm();
        	ObservableList<String> sheets=panelScene.getStylesheets();
            sheets.add(s);
        }
        primaryStage.setOnHiding(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                final ExecutorService refreshScheduler = context.getBean("refreshScheduler", ExecutorService.class);
                refreshScheduler.shutdownNow();

                for (IceApplicationProvider.IceApp a : activeApps.values()) {
                    String aName = a.getDescriptor().getName();
                    try {
                        log.info("Shutting down " + aName + "...");
                        a.stop();
                        a.destroy();
                        log.info("Shut down " + aName + " OK");
                    } catch (Exception ex) {
                        // continue as there is nothing mich that can be done,
                        // but print the error out to the log.
                        log.error("Failed to stop/destroy " + aName, ex);
                    }
                }

                log.info("All apps closed, stop OK");
                stopOk.countDown();
            }

        });

        primaryStage.setScene(panelScene);
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private CountDownLatch stopOk;
    private AbstractApplicationContext context;

    @Override
    public void init() throws Exception {
        super.init();
        stopOk = new CountDownLatch(1);
        context = getConfiguration().createContext("IceAppContainerContext.xml");
        context.registerShutdownHook();
        final Publisher publisher = context.getBean("publisher", Publisher.class);
        final Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
        final EventBus eventBus = context.getBean("eventBus", EventBus.class);
        final String udi = context.getBean("supervisorUdi", String.class);
        final MDSHandler mdsConnectivity = context.getBean("mdsConnectivity", MDSHandler.class);

        final DeviceListModel nc = context.getBean("deviceListModel", DeviceListModel.class);
        final EMRFacade emr = context.getBean("emr", EMRFacade.class);


        partitionChooserModel = new PartitionChooserModel(udi, subscriber, publisher, eventBus);
        partitionChooserModel.setMdsHandler(mdsConnectivity).initModel(emr.getPatients());

        FXMLLoader loader = new FXMLLoader(DemoPanel.class.getResource("DemoPanel.fxml"));
        panelRoot = loader.load();
        panelController = loader.getController();
        panelController.setUdi(udi).setVersion(BuildInfo.getDescriptor()).setModel(partitionChooserModel).setDeviceListModel(nc);

        panelRoot.getStylesheets().add(getClass().getResource("application.css").toExternalForm());


        // Locate all available ice application via the service loader.
        // For documentation refer to
        // http://docs.oracle.com/javase/7/docs/api/java/util/ServiceLoader.html
        ServiceLoader<IceApplicationProvider> l = ServiceLoader.load(IceApplicationProvider.class);

        final Iterator<IceApplicationProvider> iter = l.iterator();
        while (iter.hasNext()) {
            IceApplicationProvider ap = iter.next();
            if (ap.getAppType().isDisabled())
                continue;

            try {
                IceApplicationProvider.IceApp a = ap.create(context);
                activeApps.put(ap.getAppType(), a);
            } catch (Throwable ex) {
                // continue as there is nothing mich that can be done,
                // but print the error out to the log.
                log.error("Failed to create " + ap.getAppType(), ex);
            }
        }

        // Now that we have a list of all active components, build up a menu and
        // add it to the app
        IceApplicationProvider.AppType[] at = activeApps.keySet().toArray(new IceApplicationProvider.AppType[activeApps.size()]);
        Arrays.sort(at, new Comparator<IceApplicationProvider.AppType>() {
            @Override
            public int compare(IceApplicationProvider.AppType o1, IceApplicationProvider.AppType o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        
        
        loader = new FXMLLoader(MainMenu.class.getResource("MainMenu.fxml"));
        mainMenuRoot = loader.load();
        final MainMenu mainMenuController = loader.getController();
        
        final SingleSelectionModel<PatientInfo> selectionModel = panelController.getPatientSelector().getSelectionModel();
        StringProperty patientNameProperty = new SimpleStringProperty("");
        patientNameProperty.bind(
                Bindings.when(selectionModel.selectedItemProperty().isNotNull())
                .then(selectionModel.selectedItemProperty().asString())
                .otherwise(""));
        
        ListProperty<Device> deviceListProperty = new SimpleListProperty<>(nc.getContents());
        
        mainMenuController.getDevicesEmptyText().textProperty().bind(
                Bindings.when(deviceListProperty.emptyProperty())
                .then(Bindings.concat(
                        "There are no devices associated with ", 
                        patientNameProperty, 
                        ".  Create an ICE Device Adapter connected to a physical medical device or a software simulator and associate that device with ", patientNameProperty, " using the Patient ID application."))
                .otherwise(""));
        mainMenuController.devicesLabel.textProperty().bind(
                Bindings.when(patientNameProperty.isEmpty())
                .then("Devices")
                .otherwise(Bindings.concat("Devices assigned to ", patientNameProperty)));
        
        
        mainMenuController.getAppList().setCellFactory(new AppTypeCellFactory(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                AppTypeGridCell cell = (AppTypeGridCell) event.getSource();
                AppType appType = (AppType) cell.getUserData();
                final IceApplicationProvider.IceApp app = activeApps.get(appType);
                if (app != null) {
                    if (app.getUI() != null) {
                        panelController.appTitle.setText(app.getDescriptor().getName());
                        activateGoBack(app);
                    }

                    app.activate(context);
                }

            }

        }));
        mainMenuController.getDeviceList().setCellFactory(new DeviceCellFactory(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                DeviceGridCell cell = (DeviceGridCell) event.getSource();
                Device device = (org.mdpnp.apps.testapp.Device) cell.getUserData();
                try {
                    driverWrapper.start(context, device);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                activateGoBack(driverWrapper);
            }

        }));

        // Add a wrapper for the driver adapter display. This is so that the
        // stop logic could
        // shut it down properly.
        activeApps.put(Device, driverWrapper);
        mainMenuController.setTypes(at).setDevices(nc.getContents());

        Platform.runLater(new Runnable() {
            public void run() {
                panelController.getContent().setCenter(mainMenuRoot);
            }
        });

    }

    @Override
    public void stop() throws Exception {
        // this will block until the frame is killed
        stopOk.await();
        panelController.stop();
        // kill the spring context that is owned by this component.
        context.destroy();
        super.stop();
    }
}
