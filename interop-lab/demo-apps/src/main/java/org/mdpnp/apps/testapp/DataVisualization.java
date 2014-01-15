package org.mdpnp.apps.testapp;

import java.util.concurrent.ScheduledExecutorService;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.mdpnp.apps.testapp.pca.PCAConfig;
import org.mdpnp.apps.testapp.pca.VitalMonitoring;
import org.mdpnp.apps.testapp.pump.PumpModel;
import org.mdpnp.apps.testapp.vital.VitalModel;

@SuppressWarnings("serial")
public class DataVisualization extends JSplitPane {
    private final PCAConfig pcaConfig;
    private final VitalMonitoring vitalMonitoring;

    public DataVisualization(ScheduledExecutorService executor) {
        super(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(new PCAConfig(executor)), new VitalMonitoring(executor));
        this.pcaConfig = (PCAConfig) ((JScrollPane)getLeftComponent()).getViewport().getComponent(0);
        this.vitalMonitoring = (VitalMonitoring) getRightComponent();
        setDividerLocation(0.2);
        setDividerSize(10);
    }

    public void setModel(VitalModel vitalModel, PumpModel pumpModel) {
        pcaConfig.setModel(vitalModel, pumpModel);
        vitalMonitoring.setModel(vitalModel);
    }
}
