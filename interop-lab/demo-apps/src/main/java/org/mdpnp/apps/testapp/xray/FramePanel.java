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

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import org.mdpnp.devices.io.util.StateMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sarxos.webcam.Webcam;

/**
 * @author Jeff Plourde
 *
 */
public class FramePanel extends StackPane implements Runnable {

    public enum State {
        Freezing, Frozen, Thawed, Thawing
    }

    protected final StateMachine<State> stateMachine = new StateMachine<State>(new State[][] { { State.Thawed, State.Freezing },
            { State.Freezing, State.Frozen }, { State.Frozen, State.Thawing }, { State.Thawing, State.Thawed } }, State.Thawed, "initial") {
        @Override
        public void emit(State newState, State oldState, String transitionNote) {
            log.debug(oldState + " --> " + newState +" ("+transitionNote+")");
        };
    };

    private ScheduledExecutorService executor;
    
    private Label text = new Label();
    private ImageView image = new ImageView();

    public void setWebcam(Webcam webcam) {
        this.proposedWebcam = webcam;
        log.debug("Proposed webcam:" + webcam);
    }

    public FramePanel() {
        InvalidationListener listener = new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {
                resizeAt = System.currentTimeMillis() + RESIZE_DELAY;
                log.trace("Resizing at : " + resizeAt);
            }
            
        };
//        heightProperty().addListener(listener);
//        widthProperty().addListener(listener);
        getChildren().add(text);
        getChildren().add(image);
    }
    
    public FramePanel set(ScheduledExecutorService executor) {
        this.executor = executor;
        return this;
    }

    public void freeze() {
        freeze(0L);
    }

    public void freeze(long exposureTime) {
        if (stateMachine.transitionIfLegal(State.Freezing, "freeze requested")) {
            freezeBy = exposureTime > 0L ? (System.currentTimeMillis() + exposureTime) : 0L;
            log.info("will freeze:" + freezeBy);
        } else {
            log.info("cannot enter Freezing state");
        }
    }

    public void unfreeze() {
        if (stateMachine.transitionIfLegal(State.Thawing, "unfreeze requested")) {
            log.info("will thaw");
        } else {
            log.info("cannot enter Thawing state");
        }
    }

    public void toggle() {
        switch (stateMachine.getState()) {
        case Thawed:
        case Thawing:
            freeze();
            break;
        case Frozen:
        case Freezing:
            unfreeze();
            break;
        }
    }

    @SuppressWarnings("rawtypes")
    private ScheduledFuture future;

    public synchronized void start() {
        if (null == future) {
            future = executor.scheduleWithFixedDelay(this, 0L, FRAME_INTERVAL, TimeUnit.MILLISECONDS);
        }
    }

    public synchronized void stop() {

        if (future != null) {
            future.cancel(false);
            future = null;
        }
        if (null != acceptedWebcam) {
            acceptedWebcam.close();
            acceptedWebcam = null;
        }
    }

    private long freezeBy;

    private final AlphaComposite composite = AlphaComposite.SrcOver.derive(0.1f);

    private static final void gray(BufferedImage bi) {
        WritableRaster wr = bi.getRaster();
        float[] rgb = new float[4];
        for (int i = 0; i < wr.getWidth(); i++) {
            for (int j = 0; j < wr.getHeight(); j++) {
                rgb = wr.getPixel(i, j, rgb);
                float f = (rgb[0] + rgb[1] + rgb[2]) / 3.0f;
                rgb[0] = rgb[1] = rgb[2] = f;
                wr.setPixel(i, j, rgb);
            }
        }
    }

    private static final long FRAME_INTERVAL = 1000L / 30L;
    private static final long RESIZE_DELAY = 1000L;
    private static final Logger log = LoggerFactory.getLogger(FramePanel.class);

    private volatile Webcam proposedWebcam, acceptedWebcam;
    private volatile long resizeAt = Long.MAX_VALUE;

    // Image we use to create a blur effect
    private BufferedImage renderCameraImage = null, bufferedCameraImage = null;
    private Graphics2D renderCameraGraphics = null;

    private BufferedImage grabFrame() {
        
        BufferedImage img = acceptedWebcam.getImage();
        // This is wasteful in memory space, but this image will render much better via drawImage on mac retina displays
        BufferedImage tempImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_USHORT_555_RGB);
        Graphics2D g = tempImage.createGraphics();
        g.drawImage(img, 0,0,null);
         
        bufferedCameraImage = tempImage;
                
        // Build a compositing buffer if necessary (reset when camera or size
        // changes)
        if (null == renderCameraImage) {
            renderCameraImage = new BufferedImage(bufferedCameraImage.getWidth(), bufferedCameraImage.getHeight(), BufferedImage.TYPE_USHORT_555_RGB);
        }
        return bufferedCameraImage;
    }

    @Override
    public void run() {
        // We must drive these state transitions regardless of the camera state
        boolean fromFreezingToFrozen = State.Freezing.equals(stateMachine.getState()) && System.currentTimeMillis() >= freezeBy
                && stateMachine.transitionIfLegal(State.Frozen, "freezing time elapsed");
        @SuppressWarnings("unused")
        boolean fromThawingToThawed = State.Thawing.equals(stateMachine.getState()) && stateMachine.transitionIfLegal(State.Thawed, "thawing time elapsed");

        // Somebody called setWebcam, let's accept it
        if (proposedWebcam != acceptedWebcam) {
            if (acceptedWebcam != null && acceptedWebcam.isOpen()) {
                acceptedWebcam.close();
            }
            acceptedWebcam = proposedWebcam;
            log.debug("Accepted webcam:" + acceptedWebcam);
            renderCameraImage = null;
            renderCameraGraphics = null;
            resizeAt = 0L;
        }

        // No currently selected webcam
        if (acceptedWebcam == null) {
            // Nothing to do!
            return;
        }

        // There's been a resize
        if (System.currentTimeMillis() >= resizeAt) {
            log.trace("resizeAt has expired");
            size.width = (int) getWidth();
            size.height = (int) getHeight();
            if (size.width <= 0.0 || size.height <= 0.0) {
                log.trace("Not resizing to " + size);
                return;
            }
            if (!size.equals(acceptedWebcam.getViewSize())) {
                log.trace("Resizing the webcam");
                bufferedCameraImage = null;

                if (null != renderCameraGraphics) {
                    renderCameraGraphics.dispose();
                    renderCameraGraphics = null;
                }
                renderCameraImage = null;
//                repaint();

                if (acceptedWebcam.isOpen()) {
                    log.trace("Closing the accepted webcam for resize");
                    acceptedWebcam.close();
                }

                // cam holds references to these dimensions
                acceptedWebcam.setCustomViewSizes(new Dimension[] { new Dimension(size) });
                acceptedWebcam.setViewSize(new Dimension(size));

                if (!size.equals(acceptedWebcam.getViewSize())) {
                    log.trace("cam did not accept resolution " + size + " looking for best fit");
                    for (Dimension d : acceptedWebcam.getViewSizes()) {
                        if (d.width <= size.width && d.height <= size.height) {
                            log.trace("setViewSize " + d);
                            acceptedWebcam.setViewSize(d);
                        }
                    }
                }
            } else {
                log.trace("Already sized correctly " + size + " and " + acceptedWebcam.getViewSize());
            }
            resizeAt = Long.MAX_VALUE;
        }

        // Get the camera open
        if (!acceptedWebcam.isOpen()) {
            acceptedWebcam.open();
        }

        // Image from the camera
        BufferedImage bufferedCameraImage;
        switch (stateMachine.getState()) {
        case Frozen:
            if (fromFreezingToFrozen) {
                if (null == renderCameraGraphics) {
                    bufferedCameraImage = grabFrame();
                    // Straight to frozen; no pass through Freezing
                    renderCameraGraphics = renderCameraImage.createGraphics();
                    renderCameraGraphics.drawImage(bufferedCameraImage, 0, 0, null);
                }
                // Finish
                renderCameraGraphics.dispose();
                renderCameraGraphics = null;
                gray(renderCameraImage);
            }
            break;
        case Freezing:
            bufferedCameraImage = grabFrame();
            if (null == renderCameraGraphics) {
                // First pass in freezing state, build a graphics to use for
                // compositing *after* a baseline image copied
                renderCameraGraphics = renderCameraImage.createGraphics();
                renderCameraGraphics.drawImage(bufferedCameraImage, 0, 0, null);
                renderCameraGraphics.setComposite(composite);
            } else {
                // Add to the composite
//                renderCameraGraphics.drawImage(bufferedCameraImage, 0, 0, this);
            }
            break;
        case Thawed:
//            bufferedCameraImage = grabFrame();
            final BufferedImage image = acceptedWebcam.getImage();
            if(null != image) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        Image mainimage = SwingFXUtils.toFXImage(image, null);
                        FramePanel.this.image.imageProperty().set(mainimage);
                    }
                });

                image.flush();

            }
            break;
        default:
        }

//        repaint();
    }

    public BufferedImage getBufferedCameraImage() {
        return bufferedCameraImage;
    }

    private final Dimension size = new Dimension();
//    private final Dimension paintSize = new Dimension();

//    @Override
//    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
//        getSize(paintSize);
//
//        BufferedImage imageToPaint = null;
//
//        switch (stateMachine.getState()) {
//        case Freezing:
//        case Thawing:
//        case Frozen:
//            imageToPaint = renderCameraImage;
//            break;
//        case Thawed:
//        default:
//            imageToPaint = bufferedCameraImage;
//            break;
//        }
//
//        if (paintSize.width > 0 && paintSize.height > 0) {
//            if (null != imageToPaint && imageToPaint.getWidth() > 0 && imageToPaint.getHeight() > 0) {
//                g.drawImage(imageToPaint, (paintSize.width - imageToPaint.getWidth()) / 2, (paintSize.height - imageToPaint.getHeight()) / 2, this);
//            } else {
//                Color c = g.getColor();
//                g.setColor(Color.gray);
//                g.fillRect(0, 0, paintSize.width, paintSize.height);
//                g.setColor(c);
//            }
//        }
//    }
}
