package org.mdpnp.apps.testapp.pca;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Builder;
import javafx.util.BuilderFactory;
import javafx.util.Callback;

import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.apps.testapp.DeviceFactory.Pump_SimulatorProvider;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.rtiapi.data.InfusionStatusInstanceModel;
import org.springframework.context.ApplicationContext;

/**
 *
 */
public class PCAApplicationFactory implements IceApplicationProvider {

    private final IceApplicationProvider.AppType PCA =
            new  IceApplicationProvider.AppType("Infusion Safety", "NOPCA", IceApplicationProvider.class.getResource("infusion-safety.png"), 0.75);

    @Override
    public IceApplicationProvider.AppType getAppType() {
        return PCA;

    }

    @Override
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) throws IOException {

        final ScheduledExecutorService refreshScheduler = (ScheduledExecutorService) parentContext.getBean("refreshScheduler");
        final ice.InfusionObjectiveDataWriter objectiveWriter = (ice.InfusionObjectiveDataWriter) parentContext.getBean("objectiveWriter");
        final VitalModel vitalModel = (VitalModel) parentContext.getBean("vitalModel");
        final InfusionStatusInstanceModel pumpModel = (InfusionStatusInstanceModel) parentContext.getBean("pumpModel");
        FXMLLoader loader = new FXMLLoader(PCAPanel.class.getResource("PCAPanel.fxml"));
        final PCAPanel pcaPanel = new PCAPanel();
        final PCAConfig pcaConfig = new PCAConfig();
        
        loader.setControllerFactory(new Callback<Class<?>,Object>() {

            @Override
            public Object call(Class<?> param) {
                if(PCAPanel.class.equals(param)) {
                    return pcaPanel;
                } else if(PCAConfig.class.equals(param)) {
                    return pcaConfig;
                } else {
                    try {
                        return param.newInstance();
                    } catch (InstantiationException e) {
                        // TODO 
//                        log.error("",e);
                    } catch (IllegalAccessException e) {
                        // TODO 
//                        log.error("",e);
                    }
                    return null;
                }
            }
            
        });
        Parent ui = loader.load();
        
        
        pcaPanel.set(refreshScheduler, objectiveWriter);
        pcaPanel.setModel(vitalModel, pumpModel);
        pcaConfig.set(refreshScheduler, objectiveWriter);
        pcaConfig.setModel(vitalModel, pumpModel);

        return new IceApplicationProvider.IceApp() {

            @Override
            public IceApplicationProvider.AppType getDescriptor() {
                return PCA;
            }

            @Override
            public Parent getUI() {
                return ui;
            }

            @Override
            public void activate(ApplicationContext context) {
//                VitalModel vitalModel = (VitalModel)context.getBean("vitalModel");
//                InfusionStatusInstanceModel pumpModel = (InfusionStatusInstanceModel)context.getBean("pumpModel");
//                controller.setModel(vitalModel, pumpModel);
            }

            @Override
            public void stop() {
                pcaPanel.setModel(null, null);
                pcaConfig.setModel(null, null);
            }

            @Override
            public void destroy() {
            }
        };
    }
}
