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
package org.mdpnp.apps.testapp.xray;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDiscoveryEvent;
import com.github.sarxos.webcam.WebcamDiscoveryListener;

/**
 * @author Jeff Plourde
 *
 */
public class CameraComboBoxModel implements WebcamDiscoveryListener {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(CameraComboBoxModel.class);

    private ObservableList<Webcam> items = FXCollections.observableArrayList();
    
    public CameraComboBoxModel() {

    }

    public synchronized void addElement(final Webcam webcam) {
        Platform.runLater(new Runnable() {
            public void run() {
                if(!items.contains(webcam)) {
                    items.add(webcam);
                }
            }
        });
    }

    public synchronized void removeElement(Webcam webcam) {
        Platform.runLater(new Runnable() {
            public void run() {
                items.remove(webcam);
            }
        });
    }

    public void start() {
        Webcam.addDiscoveryListener(this);
        for (Webcam c : Webcam.getWebcams()) {
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
    
    public ObservableList<Webcam> getItems() {
        return items;
    }

}
