package org.mdpnp.apps.testapp.xray;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;

import org.mdpnp.devices.io.util.StateMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sarxos.webcam.Webcam;

@SuppressWarnings("serial")
public class FramePanel extends JComponent implements Runnable {

    public enum State {
        Freezing,
        Frozen,
        Thawed,
        Thawing
    }

    protected final StateMachine<State> stateMachine = new StateMachine<State>(
            new State[][] {
                    { State.Thawed, State.Freezing },
                    { State.Freezing, State.Frozen },
                    { State.Frozen, State.Thawing },
                    { State.Thawing, State.Thawed }
            }, State.Thawed) {
        public void emit(State newState, State oldState) {
            log.debug(oldState + " --> " + newState);
        };
    };

    private final ScheduledExecutorService executor;

    public void setWebcam(Webcam webcam) {
        this.proposedWebcam = webcam;
        log.debug("Proposed webcam:"+webcam);
    }

    public FramePanel(ScheduledExecutorService executor) {
        this.executor = executor;
        enableEvents(ComponentEvent.COMPONENT_RESIZED);
    }

    public void freeze() {
        freeze(0L);
    }

    public void freeze(long exposureTime) {
        if(stateMachine.transitionIfLegal(State.Freezing)) {
            freezeBy = exposureTime > 0L ? (System.currentTimeMillis() + exposureTime) : 0L;
            log.info("will freeze:"+freezeBy);
        } else {
            log.info("cannot enter Freezing state");
        }
    }

    public void unfreeze() {
        if(stateMachine.transitionIfLegal(State.Thawing)) {
            log.info("will thaw");
        } else {
            log.info("cannot enter Thawing state");
        }
    }


    public void toggle() {
        switch(stateMachine.getState()) {
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
        if(null == future) {
            future = executor.scheduleWithFixedDelay(this, 0L, FRAME_INTERVAL, TimeUnit.MILLISECONDS);
        }
    }

    public synchronized void stop() {

        if(future != null) {
            future.cancel(false);
            future = null;
        }
        if(null != acceptedWebcam) {
            acceptedWebcam.close();
            acceptedWebcam = null;
        }
    }
    private long freezeBy;

    private final AlphaComposite composite = AlphaComposite.SrcOver.derive(0.1f);

    private static final void gray(BufferedImage bi) {
        WritableRaster wr = bi.getRaster();
        float[] rgb = new float[4];
        for(int i = 0; i < wr.getWidth(); i++) {
            for(int j = 0; j < wr.getHeight(); j++) {
                rgb = wr.getPixel(i, j, rgb);
                float f = (rgb[0]+rgb[1]+rgb[2])/3.0f;
                rgb[0]=rgb[1]=rgb[2]=f;
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
        bufferedCameraImage = acceptedWebcam.getImage();
        // Build a compositing buffer if necessary (reset when camera or size changes)
        if(null == renderCameraImage) {
            renderCameraImage = new BufferedImage(bufferedCameraImage.getWidth(), bufferedCameraImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        }
        return bufferedCameraImage;
    }

    @Override
    public void run() {
        // We must drive these state transitions regardless of the camera state
        boolean fromFreezingToFrozen = State.Freezing.equals(stateMachine.getState()) && System.currentTimeMillis() >= freezeBy && stateMachine.transitionIfLegal(State.Frozen);
        @SuppressWarnings("unused")
        boolean fromThawingToThawed = State.Thawing.equals(stateMachine.getState()) && stateMachine.transitionIfLegal(State.Thawed);

        // Somebody called setWebcam, let's accept it
        if(proposedWebcam != acceptedWebcam) {
            if(acceptedWebcam != null && acceptedWebcam.isOpen()) {
                acceptedWebcam.close();
            }
            acceptedWebcam = proposedWebcam;
            log.debug("Accepted webcam:"+acceptedWebcam);
            renderCameraImage = null;
            renderCameraGraphics = null;
            resizeAt = 0L;
        }

        // No currently selected webcam
        if(acceptedWebcam == null) {
            // Nothing to do!
            return;
        }

        // There's been a resize
        if(System.currentTimeMillis() >= resizeAt) {
            log.trace("resizeAt has expired");
            getSize(size);
            if(size.width <= 0 || size.height <= 0) {
                log.trace("Not resizing to " + size);
                return;
            }
            if(!size.equals(acceptedWebcam.getViewSize())) {
                log.trace("Resizing the webcam");
                bufferedCameraImage = null;

                if(null != renderCameraGraphics) {
                    renderCameraGraphics.dispose();
                    renderCameraGraphics = null;
                }
                renderCameraImage = null;
                repaint();

                if(acceptedWebcam.isOpen()) {
                    log.trace("Closing the accepted webcam for resize");
                    acceptedWebcam.close();
                }


                // cam holds references to these dimensions
                acceptedWebcam.setCustomViewSizes(new Dimension[] { new Dimension(size) });
                acceptedWebcam.setViewSize(new Dimension(size));

                if(!size.equals(acceptedWebcam.getViewSize())) {
                    log.trace("cam did not accept resolution " + size + " looking for best fit");
                    for(Dimension d : acceptedWebcam.getViewSizes()) {
                        if(d.width <= size.width && d.height <= size.height) {
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
        if(!acceptedWebcam.isOpen()) {
            acceptedWebcam.open();
        }

        // Image from the camera
        BufferedImage bufferedCameraImage;
        switch(stateMachine.getState()) {
        case Frozen:
            if(fromFreezingToFrozen) {
                if(null == renderCameraGraphics) {
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
            if(null == renderCameraGraphics) {
                // First pass in freezing state, build a graphics to use for compositing *after* a baseline image copied
                renderCameraGraphics = renderCameraImage.createGraphics();
                renderCameraGraphics.drawImage(bufferedCameraImage, 0, 0, null);
                renderCameraGraphics.setComposite(composite);
            } else {
                // Add to the composite
                renderCameraGraphics.drawImage(bufferedCameraImage, 0, 0, this);
            }
            break;
        case Thawed:
            bufferedCameraImage = grabFrame();
            break;
        default:
        }

        repaint();
    }

    @Override
    protected void processComponentEvent(ComponentEvent e) {
        switch(e.getID()) {
        case ComponentEvent.COMPONENT_RESIZED:
            resizeAt = System.currentTimeMillis() + RESIZE_DELAY;
            log.trace("Resizing at : " + resizeAt);
            break;
        }
        super.processComponentEvent(e);
    }
    private final Dimension size = new Dimension();
    private final Dimension paintSize = new Dimension();
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        getSize(paintSize);

        BufferedImage imageToPaint = null;

        switch(stateMachine.getState()) {
        case Freezing:
        case Thawing:
        case Frozen:
            imageToPaint = renderCameraImage;
            break;
        case Thawed:
        default:
            imageToPaint = bufferedCameraImage;
            break;
        }

        if(paintSize.width > 0 && paintSize.height > 0) {
            if(null != imageToPaint && imageToPaint.getWidth() > 0 && imageToPaint.getHeight() > 0) {
                g.drawImage(imageToPaint, (paintSize.width - imageToPaint.getWidth()) / 2 , (paintSize.height - imageToPaint.getHeight()) / 2, this);
            } else {
                Color c = g.getColor();
                g.setColor(Color.gray);
                g.fillRect(0, 0, paintSize.width, paintSize.height);
                g.setColor(c);
            }
        }
    }
}
