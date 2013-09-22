package org.mdpnp.apps.testapp.xray;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;

@SuppressWarnings("serial")
public class FramePanel extends JComponent implements Runnable {

    public enum State {
        Freezing,
        Frozen,
        Thawed
    }


    public void start() {
        if(cameraThread == null) {
            running = true;
            cameraThread = new Thread(this, "Camera Thread");
            cameraThread.setDaemon(true);
            cameraThread.start();
        }
    }

    private Webcam webcam;
    private final ScheduledExecutorService executor;

    public void setWebcam(Webcam webcam) {

        if(null != this.webcam) {
            if(null != webcam && this.webcam.equals(webcam)) {
                return;
            }
            this.webcam = null;
        }

        Dimension size = getSize();
        if(null != webcam) {
            if(size.width > 0 && size.height > 0) {
                if(webcam.isOpen()) {
                    webcam.close();
                }
                webcam.setCustomViewSizes(new Dimension[] { size });
                webcam.setViewSize(size);

                if(!size.equals(webcam.getViewSize())) {
                    for(Dimension d : webcam.getViewSizes()) {
                        if(d.width <= size.width && d.height <= size.height) {
                            webcam.setViewSize(d);
                        }
                    }
                }
            }
        }

        this.webcam = webcam;

        if(this.webcam != null) {
            if(running && !webcam.isOpen() && size.width > 0 && size.height > 0) {
                webcam.open();
            }
            if(!running && webcam.isOpen()) {
                webcam.close();
            }
        } else {
            imageToPaint = null;
        }
    }

    public FramePanel(ScheduledExecutorService executor) {
        this.executor = executor;
        enableEvents(ComponentEvent.COMPONENT_RESIZED);
    }

    public void freeze() {
        freeze(0L);
    }

    public synchronized void freeze(long exposureTime) {
        if(State.Frozen.equals(state)) {
            return;
        }
        if(exposureTime > 0L) {
            freezeBy = System.currentTimeMillis() + exposureTime;
            log.info("will freeze:"+freezeBy);
            state = State.Freezing;
            notifyAll();
        } else {
            state = State.Frozen;
            notifyAll();
        }
    }

    public synchronized void unfreeze() {
        while(State.Freezing.equals(state)) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        state = State.Thawed;
        notifyAll();
    }

    private Thread cameraThread;




    public synchronized void toggle() {
        while(State.Freezing.equals(state)) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        switch(state) {
        case Thawed:
            freeze();
            break;
        default:
            unfreeze();
            break;
        }
//        state = State.Thawed.equals(state)?State.Frozen:State.Thawed;
        notifyAll();
    }

    public void stop() {
        if (cameraThread != null) {
            running = false;
            unfreeze();

            try {
                cameraThread.join();
                cameraThread = null;
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            Webcam webcam = this.webcam;
            if(webcam.isOpen()) {
                webcam.close();
            }
        }
    }
    private long freezeBy;
    private State state = State.Thawed;
    private volatile boolean running = false;

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
    private static final Logger log = LoggerFactory.getLogger(FramePanel.class);

    private BufferedImage imageToPaint = null;

    @Override
    public void run() {

        Webcam webcam = null;

        try {
            long lastStart = 0L;

            // Image from the camera
            BufferedImage bufferedCameraImage = null;

            // Image we use to create a blur effect
            BufferedImage renderCameraImage = null;
            Graphics2D renderCameraGraphics = null;



            while (running) {
                // New webcam specified
                if(this.webcam != webcam) {
                    webcam = this.webcam;
                    if(null != webcam) {
                        bufferedCameraImage = webcam.getImage();
                        if(bufferedCameraImage != null) {
                            renderCameraImage = new BufferedImage(bufferedCameraImage.getWidth(), bufferedCameraImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                        }
                    }
                } else {
                    if(webcam != null) {
                        bufferedCameraImage = webcam.getImage();
                    }
                }

                lastStart = System.currentTimeMillis();
                if(webcam != null && bufferedCameraImage!=null) {
                    switch(state) {

                    case Frozen:
                        if(null == renderCameraGraphics) {
                            // Straight to frozen
                            renderCameraGraphics = renderCameraImage.createGraphics();
                            renderCameraGraphics.drawImage(bufferedCameraImage, 0, 0, null);
                            imageToPaint = renderCameraImage;
                        }
                        renderCameraGraphics.dispose();
                        renderCameraGraphics = null;
                        gray(renderCameraImage);
                        break;
                    case Freezing:
                        if(null == renderCameraGraphics) {
                            renderCameraGraphics = renderCameraImage.createGraphics();
                            renderCameraGraphics.drawImage(bufferedCameraImage, 0, 0, null);
                            renderCameraGraphics.setComposite(composite);
                            imageToPaint = renderCameraImage;
                        } else {
                            renderCameraGraphics.drawImage(bufferedCameraImage, 0, 0, this);
                        }
                        break;
                    case Thawed:
                        imageToPaint = bufferedCameraImage;
                        break;
                    default:
                    }
                }


                synchronized (this) {
                    if(State.Freezing.equals(state) && System.currentTimeMillis() >= freezeBy) {
                        log.info("frozen:"+System.currentTimeMillis());
                        state = State.Frozen;
                        continue;
                    } else {

                        while(running && State.Frozen.equals(state)) {
                            repaint();
                            try {
                                wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
//					renderCameraGraphics.fillRect(0, 0, image.width(), image.height());
                }
                repaint();
                long now = System.currentTimeMillis();
                if( FRAME_INTERVAL > (now - lastStart) ) {
                    try {
                        Thread.sleep(FRAME_INTERVAL - (now - lastStart));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    log.warn("Frame took " + (now-lastStart) + "ms which exceeds FRAME_INTERVAL="+ FRAME_INTERVAL+"ms");
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(webcam != null) {
                    webcam.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static final String ACQUIRING_IMAGE = "Acquiring image...";
    private static final String IMAGE_ACQUIRED = "Image Acquired";
    private static final String LIVE_VIDEO = "Live Video";
    private static final Color transparentWhite = new Color(1.0f, 1.0f, 1.0f, 0.8f);
    private static final Color transparentRed = new Color(1.0f, 0.0f, 0.0f, 0.8f);

    @SuppressWarnings("rawtypes")
    ScheduledFuture resizerFuture;

    private final class Resizer implements Runnable {
        public void run() {


            Webcam webcam = FramePanel.this.webcam;
            if(!webcam.getViewSize().equals(getSize())) {
                setWebcam(null);
                setWebcam(webcam);
            }
        }
    }
    private final Resizer resizer = new Resizer();

    @Override
    protected void processComponentEvent(ComponentEvent e) {
        switch(e.getID()) {
        case ComponentEvent.COMPONENT_RESIZED:
            if(null != resizerFuture) {
                resizerFuture.cancel(false);
            }
            resizerFuture = executor.schedule(resizer, 1500L, TimeUnit.MILLISECONDS);

            break;
        }
        super.processComponentEvent(e);
    }

    private static Font bigFont;
    private final Dimension paintSize = new Dimension();
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage imageToPaint = this.imageToPaint;
        if(null != imageToPaint) {
            getSize(paintSize);
            g.drawImage(imageToPaint, (paintSize.width - imageToPaint.getWidth()) / 2 , (paintSize.height - imageToPaint.getHeight()) / 2, this);
        } else {
            Color c = g.getColor();
            g.setColor(Color.gray);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(c);
        }
        FontMetrics fontMetrics;

        if(null == bigFont) {
            float fontSize = 50f;
            String s = System.getProperty("XRAYVENTFONTSIZE");
            if(null != s) {
                try {
                    fontSize = Float.parseFloat(s);
                } catch (NumberFormatException nfe) {
                    log.error("Cannot read XRAYVENTFONTSIZE system property", nfe);
                }
            }
            bigFont = g.getFont().deriveFont(fontSize);
//		    System.out.println("bigFont");
        }
        g.setFont(bigFont);

        fontMetrics = g.getFontMetrics();
        final int Y = 70;
        final int X = 20;
        int y = Y - fontMetrics.getHeight() + fontMetrics.getDescent();

        switch(state) {
        case Freezing:
            g.setColor(transparentWhite);
            g.fillRect(X, y, fontMetrics.stringWidth(ACQUIRING_IMAGE), fontMetrics.getHeight());
            g.setColor(Color.black);
            g.drawString(ACQUIRING_IMAGE, X, Y);
            break;
        case Frozen:
            g.setColor(transparentRed);
            g.fillRect(X, y, fontMetrics.stringWidth(IMAGE_ACQUIRED), fontMetrics.getHeight());
            g.setColor(Color.black);
            g.drawString(IMAGE_ACQUIRED, X, Y);
            break;
        case Thawed:
            g.setColor(transparentWhite);
            g.fillRect(X, y, fontMetrics.stringWidth(LIVE_VIDEO), fontMetrics.getHeight());
            g.setColor(Color.black);
            g.drawString(LIVE_VIDEO, X, Y);
            break;
        default:
        }

    }

    public static void main(String[] args) throws WebcamException, TimeoutException {
        JFrame top = new JFrame("FramePanel test");
        Webcam webcam = Webcam.getDefault(5000L);
        final FramePanel panel = new FramePanel(Executors.newSingleThreadScheduledExecutor());
        panel.setWebcam(webcam);
        top.getContentPane().setLayout(new BorderLayout());
        top.getContentPane().add(panel, BorderLayout.CENTER);


        top.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                panel.stop();
                super.windowClosing(e);
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                panel.toggle();
                super.mouseReleased(e);
            }
        });

        top.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        top.setSize(640, 480);
        top.setLocationRelativeTo(null);
        top.setVisible(true);

        panel.start();
    }
}
