package org.mdpnp.apps.testapp.xray;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeoutException;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;

@SuppressWarnings("serial")
public class LabeledFramePanel extends FramePanel {

    public LabeledFramePanel(ScheduledExecutorService executor) {
        super(executor);
    }
    private static final String ACQUIRING_IMAGE = "Acquiring image...";
    private static final String IMAGE_ACQUIRED = "Image Acquired";
    private static final String LIVE_VIDEO = "Live Video";

    private static String acquiringImage = ACQUIRING_IMAGE;
    private static String imageAcquired = IMAGE_ACQUIRED;
    private static String liveVideo = LIVE_VIDEO;

    private static final Color transparentWhite = new Color(1.0f, 1.0f, 1.0f, 0.8f);
    private static final Color transparentRed = new Color(1.0f, 0.0f, 0.0f, 0.8f);
    private static Font bigFont;
    private static final Logger log = LoggerFactory.getLogger(LabeledFramePanel.class);

    @Override
    public void paint(Graphics g) {
        super.paint(g);
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
//          System.out.println("bigFont");
        }
        g.setFont(bigFont);

        fontMetrics = g.getFontMetrics();
        final int Y = 70;
        final int X = 20;
        int y = Y - fontMetrics.getHeight() + fontMetrics.getDescent();

        switch(stateMachine.getState()) {
        case Freezing:
            g.setColor(transparentWhite);
            g.fillRect(X, y, fontMetrics.stringWidth(acquiringImage), fontMetrics.getHeight());
            g.setColor(Color.black);
            g.drawString(acquiringImage, X, Y);
            break;
        case Frozen:
            g.setColor(transparentRed);
            g.fillRect(X, y, fontMetrics.stringWidth(imageAcquired), fontMetrics.getHeight());
            g.setColor(Color.black);
            g.drawString(imageAcquired, X, Y);
            break;
        case Thawed:
            g.setColor(transparentWhite);
            if(liveVideo != null) {
                g.fillRect(X, y, fontMetrics.stringWidth(liveVideo), fontMetrics.getHeight());
                g.setColor(Color.black);
                g.drawString(liveVideo, X, Y);
            }
            break;
        default:
        }
    }

    public static void main(String[] args) throws WebcamException, TimeoutException {
        liveVideo = null;
        JFrame top = new JFrame("Live Video");

        top.setAlwaysOnTop(true);
        Webcam webcam = Webcam.getDefault(5000L);
        final FramePanel panel = new LabeledFramePanel(Executors.newSingleThreadScheduledExecutor());
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

//        panel.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                panel.toggle();
//                super.mouseReleased(e);
//            }
//        });

        top.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                panel.stop();
                super.windowClosing(e);
            }
        });
        top.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        top.setSize(640, 480);
        top.setLocationRelativeTo(null);
        top.setVisible(true);

        panel.start();
    }

}
