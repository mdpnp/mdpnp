package org.mdpnp.apps.testapp.xray;

import javax.swing.DefaultComboBoxModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDiscoveryEvent;
import com.github.sarxos.webcam.WebcamDiscoveryListener;

@SuppressWarnings({ "serial", "rawtypes", "unchecked" })
public class CameraComboBoxModel extends DefaultComboBoxModel implements WebcamDiscoveryListener {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(CameraComboBoxModel.class);

    public CameraComboBoxModel() {

    }

    public synchronized void addElement(Webcam webcam) {
        if(getIndexOf(webcam) < 0) {
            super.addElement(webcam);
        }
    }

    public synchronized void removeElement(Webcam webcam) {
        super.removeElement(webcam);
    }

    public void start() {
        Webcam.addDiscoveryListener(this);
        for(Webcam c : Webcam.getWebcams()) {
            addElement(c);
        }
    }

    public void stop() {
        Webcam.removeDiscoveryListener(this);
    }

    @Override
    public void webcamFound(WebcamDiscoveryEvent event) {
        addElement(event.getWebcam());
    }

    @Override
    public void webcamGone(WebcamDiscoveryEvent event) {
        removeElement(event.getWebcam());
    }

}
