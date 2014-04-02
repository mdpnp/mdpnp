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

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ScheduledExecutorService;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.mdpnp.apps.testapp.DeviceListCellRenderer;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.apps.testapp.vital.VitalModelListener;
import org.mdpnp.rtiapi.data.InfusionStatusInstanceModel;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class PCAPanel extends JPanel implements VitalModelListener {

    private final PCAConfig pcaConfig;
    // private final VitalMonitoring vitalMonitor;

    private static final Border EMPTY_BORDER = new EmptyBorder(15, 15, 15, 15);
    private static final Border YELLOW_BORDER = new LineBorder(Color.yellow, 15, false);
    private static final Border RED_BORDER = new LineBorder(Color.red, 15, false);

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

    public PCAPanel(ScheduledExecutorService refreshScheduler, ice.InfusionObjectiveDataWriter objectiveWriter, DeviceListCellRenderer deviceCellRenderer) {
        // super(JSplitPane.HORIZONTAL_SPLIT, true, new
        // PCAConfig(refreshScheduler), new VitalMonitoring(refreshScheduler));
        super(new BorderLayout());
        // pcaConfig = (PCAConfig) getLeftComponent();
        // vitalMonitor = (VitalMonitoring) getRightComponent();
        pcaConfig = new PCAConfig(refreshScheduler, objectiveWriter, deviceCellRenderer);

        setBorder(EMPTY_BORDER);

        // pcaConfig.setBackground(Color.orange);

        add(pcaConfig, BorderLayout.CENTER);

        // setDividerSize(4);

        // setDividerLocation(0.5);

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

    public void setModel(VitalModel vitalModel, InfusionStatusInstanceModel pumpModel) {
        if (this.model != null) {
            this.model.removeListener(this);
        }
        this.model = vitalModel;
        if (this.model != null) {
            this.model.addListener(this);
        }
        pcaConfig.setModel(model, pumpModel);
        // vitalMonitor.setModel(vitalModel);
    }

    public VitalModel getVitalModel() {
        return model;
    }

    @Override
    public void vitalChanged(VitalModel model, Vital vital) {
        if (model.isInfusionStopped()) {
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

        if (model.isInfusionStopped() || model.getState().equals(VitalModel.State.Alarm)) {
            setBorder(RED_BORDER);
        } else if (VitalModel.State.Warning.equals(model.getState())) {
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
