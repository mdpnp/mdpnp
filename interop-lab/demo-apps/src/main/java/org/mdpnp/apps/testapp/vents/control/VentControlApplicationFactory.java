package org.mdpnp.apps.testapp.vents.control;

import java.io.IOException;

import com.google.common.eventbus.EventBus;
import com.rti.dds.subscription.Subscriber;

import himss.PatientAssessmentDataWriter;
import ice.KeyValueObjectiveDataWriter;
import ice.VentModeObjectiveDataWriter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.mdpnp.apps.fxbeans.AlertFxList;
import org.mdpnp.apps.fxbeans.InfusionStatusFxList;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.devices.MDSHandler;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.ApplicationContext;

public class VentControlApplicationFactory implements IceApplicationProvider {
    private final IceApplicationProvider.AppType ventTestApplication = new IceApplicationProvider.AppType("Ventilator Control", "NOVENTCONTROL",
            VentControlApplicationFactory.class.getResource("nkv550.png"), 0.50, false);

    @Override
    public IceApplicationProvider.AppType getAppType() {
        return ventTestApplication;

    }


    @Override
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) throws IOException {

        FXMLLoader loader = new FXMLLoader(VentControl.class.getResource("Ventilator.fxml"));

        final Parent ui = loader.load();

        final String udi = parentContext.getBean("supervisorUdi", String.class);

        final VitalModel model = (VitalModel) parentContext.getBean("vitalModel");
        final VentControl controller = loader.getController();
        
        final DeviceListModel deviceListModel = parentContext.getBean("deviceListModel", DeviceListModel.class);
		
		final NumericFxList numericList = parentContext.getBean("numericList", NumericFxList.class);
		
		final SampleArrayFxList sampleList = parentContext.getBean("sampleArrayList", SampleArrayFxList.class);
		
		final AlertFxList patientAlertList = parentContext.getBean("patientAlertList", AlertFxList.class);
		
		final Subscriber subscriber = (Subscriber) parentContext.getBean("subscriber");

        final EventLoop eventLoop = (EventLoop) parentContext.getBean("eventLoop");
        
        final InfusionStatusFxList infusionList = (InfusionStatusFxList) parentContext.getBean("infusionStatusList");
        
        final MDSHandler mdsHandler=(MDSHandler)parentContext.getBean("mdsConnectivity",MDSHandler.class);
        mdsHandler.start();
        
        final VentModeObjectiveDataWriter ventModeWriter=(VentModeObjectiveDataWriter)parentContext.getBean("ventModeObjectiveWriter",VentModeObjectiveDataWriter.class);

        final KeyValueObjectiveDataWriter keyValueWriter=(KeyValueObjectiveDataWriter)parentContext.getBean("keyValueObjectiveWriter",KeyValueObjectiveDataWriter.class);
        
        controller.set(parentContext, deviceListModel, numericList, sampleList, patientAlertList, mdsHandler, infusionList, ventModeWriter, keyValueWriter);

        final EventBus eventBus = parentContext.getBean("eventBus", EventBus.class);
        eventBus.register(controller);

        return new IceApplicationProvider.IceApp() {

            @Override
            public IceApplicationProvider.AppType getDescriptor() {
                return ventTestApplication;
            }

            @Override
            public Parent getUI() {
                return ui;
            }

            @Override
            public void activate(ApplicationContext context) {
                if(controller instanceof WithPatientAssessmentDataWriter) {
                    final PatientAssessmentDataWriter writer = (PatientAssessmentDataWriter) parentContext.getBean("patientAssessmentWriter");
                    ((WithPatientAssessmentDataWriter)controller).configurePatientAssessmentWriter(udi, writer);
                }
            }

            @Override
            public void stop() {
                if(controller instanceof WithPatientAssessmentDataWriter) {
                    ((WithPatientAssessmentDataWriter)controller).configurePatientAssessmentWriter(null, null);
                }
            }

            @Override
            public void destroy() throws Exception {
                eventBus.unregister(controller);
            }

			@Override
			public int getPreferredWidth() {
				return 1024;
			}

			@Override
			public int getPreferredHeight() {
				// TODO Auto-generated method stub
				return 768;
			}
            
            
        };
    }

    public interface WithVitalModel {
        void setModel(VitalModel vitalModel);
    }

    public interface WithPatientAssessmentDataWriter {
        void configurePatientAssessmentWriter(String udi, PatientAssessmentDataWriter writer);
    }


}
