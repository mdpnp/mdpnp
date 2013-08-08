package org.mdpnp.apps.testapp.pca;


import java.awt.Color;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.JSplitPane;
import javax.swing.border.Border;

import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.apps.testapp.vital.VitalModelListener;

@SuppressWarnings("serial")
public class PCAPanel extends JSplitPane implements VitalModelListener {

    private final PCAConfig pcaConfig;
    private final VitalMonitoring vitalMonitor;
    
    private static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(15, 15, 15, 15);
    private static final Border YELLOW_BORDER = BorderFactory.createLineBorder(Color.yellow, 15, false);
    private static final Border RED_BORDER = BorderFactory.createLineBorder(Color.red, 15, false);
    
    private Clip drugDeliveryAlarm, generalAlarm;
    
    public PCAPanel(ScheduledExecutorService refreshScheduler) {
        super(JSplitPane.HORIZONTAL_SPLIT, true, new PCAConfig(refreshScheduler), new VitalMonitoring(refreshScheduler));
        pcaConfig = (PCAConfig) getLeftComponent();
        vitalMonitor = (VitalMonitoring) getRightComponent();
        
        setBorder(EMPTY_BORDER);

        setDividerSize(4);

        setDividerLocation(0.5);
        
        try {
            // http://www.anaesthesia.med.usyd.edu.au/resources/alarms/
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(PCAPanel.class.getResourceAsStream("drughi.wav"));
            drugDeliveryAlarm = AudioSystem.getClip();
            drugDeliveryAlarm.open(audioInputStream);
            drugDeliveryAlarm.setLoopPoints(0, -1);
            System.out.println(drugDeliveryAlarm.getFrameLength());
            audioInputStream = AudioSystem.getAudioInputStream(PCAPanel.class.getResourceAsStream("genhi.wav"));
            generalAlarm = AudioSystem.getClip();
            generalAlarm.open(audioInputStream);
            generalAlarm.setLoopPoints(0, -1);
            
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
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
        if(model.isInfusionStopped()) {
            if(null != drugDeliveryAlarm && !drugDeliveryAlarm.isRunning()) {
                drugDeliveryAlarm.loop(Clip.LOOP_CONTINUOUSLY);
            }
            if(null != generalAlarm) {
                generalAlarm.stop();
            }
        } else {
            if(null != drugDeliveryAlarm && drugDeliveryAlarm.isRunning()) {
                drugDeliveryAlarm.stop();
            }
            // Put this here so we don't get concurrent alarms
            switch(model.getState()) {
            case Alarm:
                if(null != generalAlarm && !generalAlarm.isRunning()) {
                    generalAlarm.loop(Clip.LOOP_CONTINUOUSLY);
                }
                break;
            case Warning:
            case Normal:
                if(null != generalAlarm) {
                    generalAlarm.stop();
                }
            default:
            }
        }
        
        
        
        if(model.isInfusionStopped() || model.getState().equals(VitalModel.State.Alarm)) {
            setBorder(RED_BORDER);
        } else if(VitalModel.State.Warning.equals(model.getState())) {
            setBorder(YELLOW_BORDER);
        } else {
            setBorder(EMPTY_BORDER);
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
