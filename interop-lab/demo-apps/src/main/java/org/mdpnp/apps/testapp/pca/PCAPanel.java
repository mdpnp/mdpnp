package org.mdpnp.apps.testapp.pca;


import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.vital.VitalModel;

@SuppressWarnings("serial")
public class PCAPanel extends JTabbedPane {

    private final PCAConfig pcaConfig, pcaConfig2;
    private final PCAMonitor pcaMonitor;
    private final VitalMonitoring vitalMonitor;
    
    public PCAPanel(DeviceListModel deviceListModel) {
        pcaConfig = new PCAConfig();
        pcaMonitor = new PCAMonitor(deviceListModel);
        vitalMonitor = new VitalMonitoring();
        setOpaque(false);
        pcaConfig.setOpaque(false);
        pcaMonitor.setOpaque(false);
        vitalMonitor.setOpaque(false);
        JPanel stuff = new JPanel(new GridLayout(1,2));
        stuff.setOpaque(false);
        stuff.add(pcaConfig2 = new PCAConfig());
        stuff.add(vitalMonitor);
        addTab("Configuration", pcaConfig);
        addTab("Monitoring", pcaMonitor);
        addTab("Graphic Monitoring", stuff);
    }
    
    
    public void setActive(boolean b) {
        
    }

    private VitalModel model;
    
    public void setModel(VitalModel vitalModel) {
        this.model = vitalModel;
        pcaConfig.setModel(model);
        pcaConfig2.setModel(model);
        pcaMonitor.setModel(vitalModel);
        vitalMonitor.setModel(vitalModel);
    }
    public VitalModel getVitalModel() {
        return model;
    }
    
}
