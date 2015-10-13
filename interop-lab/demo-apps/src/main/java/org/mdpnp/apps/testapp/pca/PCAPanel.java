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
package org.mdpnp.apps.testapp.pca;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ScheduledExecutorService;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.mdpnp.apps.fxbeans.InfusionStatusFxList;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.rtiapi.data.EventLoop;

import com.rti.dds.subscription.Subscriber;

/**
 * @author Jeff Plourde
 *
 */
public class PCAPanel implements InvalidationListener {
    @FXML BorderPane main;
    
    @FXML protected BorderPane pcaConfig;
    @FXML protected PCAConfig pcaConfigController;
    
    private static final Border NO_BORDER = new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, new CornerRadii(1), new BorderWidths(3) ));
    private static final Border YELLOW_BORDER = new Border(new BorderStroke(Color.YELLOW, BorderStrokeStyle.SOLID, new CornerRadii(1), new BorderWidths(3) ));
    private static final Border RED_BORDER = new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(1), new BorderWidths(3) ));

    private Clip drugDeliveryAlarm, generalAlarm;

    private static final InputStream inMemory(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[2048];
        int b = is.read(buf);
        while (b >= 0) {
            baos.write(buf, 0, b);
            b = is.read(buf);
        }
        is.close();
        return new ByteArrayInputStream(baos.toByteArray());
    }

    public PCAPanel set(ScheduledExecutorService refreshScheduler, ice.InfusionObjectiveDataWriter objectiveWriter, 
            DeviceListModel deviceListModel, InfusionStatusFxList infusionStatusList) {
        pcaConfigController.set(refreshScheduler, objectiveWriter, deviceListModel, infusionStatusList);
        return this;
    }
    
    public PCAPanel() {

        try {
            // http://www.anaesthesia.med.usyd.edu.au/resources/alarms/

            // Per the documentation for AudioSystem.getAudioInputStream
            // mark/reset on the stream is required
            // so we'll load it into memory because I stored the audio clips in
            // a Jar file and the inflating
            // InputStream does not support mark/reset
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inMemory(PCAPanel.class.getResourceAsStream("drughi.wav")));
            AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 22050.0f, 16, 1, 2, AudioSystem.NOT_SPECIFIED, false);
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            drugDeliveryAlarm = (Clip) AudioSystem.getLine(info);
            drugDeliveryAlarm.open(audioInputStream);
            drugDeliveryAlarm.setLoopPoints(0, -1);

            format = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, 22050.0f, 8, 1, 1, AudioSystem.NOT_SPECIFIED, true);
            info = new DataLine.Info(Clip.class, format);
            audioInputStream = AudioSystem.getAudioInputStream(inMemory(PCAPanel.class.getResourceAsStream("genhi.wav")));
            generalAlarm = (Clip) AudioSystem.getLine(info);
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

    private VitalModel model;
    
    public void setModel(VitalModel vitalModel) {
        if (this.model != null) {
            this.model.stateProperty().removeListener(this);
            pcaConfigController.infusionPumpModel.isInfusionStoppedProperty().removeListener(this);
        }
        this.model = vitalModel;
        pcaConfigController.setModel(vitalModel);
        if(null != vitalModel) {
            model.stateProperty().addListener(this);
            pcaConfigController.infusionPumpModel.isInfusionStoppedProperty().addListener(this);
        } else {
            generalAlarm.stop();
            drugDeliveryAlarm.stop();
        }
    }

    public VitalModel getVitalModel() {
        return model;
    }

    @Override
    public void invalidated(Observable observable) {
        if(null != model) {
            if (pcaConfigController.infusionPumpModel.isInfusionStopped()) {
                if (null != drugDeliveryAlarm && !drugDeliveryAlarm.isRunning()) {
                    drugDeliveryAlarm.loop(Clip.LOOP_CONTINUOUSLY);
                }
                if (null != generalAlarm) {
                    generalAlarm.stop();
                }
            } else {
                if (null != drugDeliveryAlarm && drugDeliveryAlarm.isRunning()) {
                    drugDeliveryAlarm.stop();
                }
                // Put this here so we don't get concurrent alarms
                switch (model.getState()) {
                case Alarm:
                    if (null != generalAlarm && !generalAlarm.isRunning()) {
                        generalAlarm.loop(Clip.LOOP_CONTINUOUSLY);
                        // PCAMonitor.sendPumpCommand("Stop, \n", null);
                    }
                    break;
                case Warning:
                case Normal:
                    if (null != generalAlarm) {
                        // PCAMonitor.sendPumpCommand("Start, 10\n", null);
                        generalAlarm.stop();
                    }
                default:
                }
            }
            if (pcaConfigController.infusionPumpModel.isInfusionStopped() || model.getState().equals(VitalModel.State.Alarm)) {
                main.setBorder(RED_BORDER);
            } else if (VitalModel.State.Warning.equals(model.getState())) {
                main.setBorder(YELLOW_BORDER);
            } else {
                main.setBorder(NO_BORDER);
            }
        }
    }

}
