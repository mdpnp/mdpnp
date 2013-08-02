package org.mdpnp.apps.testapp.pca;


import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.mdpnp.apps.testapp.vital.VitalModel;

@SuppressWarnings("serial")
public class PCAPanel extends JTabbedPane {

    private final PCAConfig pcaConfig = new PCAConfig();
    private final PCAMonitor pcaMonitor = new PCAMonitor();
    private final VitalMonitoring vitalMonitor = new VitalMonitoring();
    
    public PCAPanel() {
        setOpaque(false);
        addTab("Configuration", pcaConfig);
        addTab("Monitoring", pcaMonitor);
        addTab("Graphic Monitoring", vitalMonitor);
    }
    
    
    public void setActive(boolean b) {
        
    }

    private VitalModel model;
    
    public void setModel(VitalModel vitalModel) {
        this.model = vitalModel;
        pcaMonitor.setModel(vitalModel);
        vitalMonitor.setModel(vitalModel);
    }
    public VitalModel getVitalModel() {
        return model;
    }
    
}
