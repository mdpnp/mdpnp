package org.mdpnp.apps.testapp.pca;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.apps.testapp.vital.VitalModelListener;

@SuppressWarnings("serial")
public class PCAPanel extends JPanel implements VitalModelListener {

    private final PCAConfig pcaConfig;
    private final VitalMonitoring vitalMonitor;
    
    public PCAPanel(DeviceListModel deviceListModel, ScheduledExecutorService refreshScheduler) {
        super(new BorderLayout());
        vitalMonitor = new VitalMonitoring(refreshScheduler);
//        setOpaque(false);
        vitalMonitor.setOpaque(true);
        vitalMonitor.setBackground(Color.white);
        JPanel stuff = new JPanel(new GridLayout(1,2));
        stuff.add(pcaConfig = new PCAConfig(deviceListModel, refreshScheduler));
        stuff.add(vitalMonitor);
        add(stuff, BorderLayout.CENTER);
    }
    
    
    public void setActive(boolean b) {
        
    }

    private VitalModel model;
    
    public void setModel(VitalModel vitalModel) {
        if(this.model != null) {
            this.model.removeListener(this);
        }
        this.model = vitalModel;
        if(this.model != null) {
            this.model.addListener(this);
        }
        pcaConfig.setModel(model);
        vitalMonitor.setModel(vitalModel);
    }
    public VitalModel getVitalModel() {
        return model;
    }


    @Override
    public void vitalChanged(VitalModel model, Vital vital) {
        if(model.isInfusionStopped() || model.getState().equals(VitalModel.State.Alarm)) {
            setBorder(BorderFactory.createLineBorder(Color.red, 15, false));
        } else if(VitalModel.State.Warning.equals(model.getState())) {
            setBorder(BorderFactory.createLineBorder(Color.yellow, 15, false));
        } else {
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        }
        
    }


    @Override
    public void vitalRemoved(VitalModel model, Vital vital) {
        vitalChanged(model, vital);
    }


    @Override
    public void vitalAdded(VitalModel model, Vital vital) {
        vitalChanged(model, vital);
    }
    
}
