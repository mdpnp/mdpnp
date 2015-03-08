package org.mdpnp.apps.testapp.pca;

import java.util.concurrent.ScheduledExecutorService;

import javafx.scene.Parent;

import org.mdpnp.apps.testapp.DataVisualization;
import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.rtiapi.data.InfusionStatusInstanceModel;
import org.springframework.context.ApplicationContext;

/**
 *
 */
public class PCAVizApplicationFactory implements IceApplicationProvider {

    private final IceApplicationProvider.AppType PCAViz =
            new IceApplicationProvider.AppType("Data Visualization", "NOPCAVIZ", DataVisualization.class.getResource("data-viz.png"), 0.75);

    @Override
    public IceApplicationProvider.AppType getAppType() {
        return PCAViz;

    }

    @Override
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) {

        final ScheduledExecutorService refreshScheduler = (ScheduledExecutorService) parentContext.getBean("refreshScheduler");
        final ice.InfusionObjectiveDataWriter objectiveWriter = (ice.InfusionObjectiveDataWriter) parentContext.getBean("objectiveWriter");

//        final DataVisualization ui =
//                new DataVisualization(refreshScheduler, objectiveWriter, new VitalMonitoring(refreshScheduler));

        return new IceApplicationProvider.IceApp() {

            @Override
            public IceApplicationProvider.AppType getDescriptor() {
                return PCAViz;
            }

            @Override
            public Parent getUI() {
                return null;
            }

            @Override
            public void activate(ApplicationContext context) {
                VitalModel vitalModel = (VitalModel)context.getBean("vitalModel");
                InfusionStatusInstanceModel pumpModel = (InfusionStatusInstanceModel)context.getBean("pumpModel");
//                ui.setModel(vitalModel, pumpModel);
            }

            @Override
            public void stop() {
//                ui.setModel(null, null);
            }

            @Override
            public void destroy() {
            }
        };
    }
}
