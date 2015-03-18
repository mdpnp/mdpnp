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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
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

/**
 * @author Jeff Plourde
 *
 */
public class LabeledFramePanel extends FramePanel {

    public LabeledFramePanel(ScheduledExecutorService executor) {
//        super(executor);
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

//    @Override
//    public void paint(Graphics g) {
//        super.paint(g);
//        FontMetrics fontMetrics;
//
//        if (null == bigFont) {
//            float fontSize = 50f;
//            String s = System.getProperty("XRAYVENTFONTSIZE");
//            if (null != s) {
//                try {
//                    fontSize = Float.parseFloat(s);
//                } catch (NumberFormatException nfe) {
//                    log.error("Cannot read XRAYVENTFONTSIZE system property", nfe);
//                }
//            }
//            bigFont = g.getFont().deriveFont(fontSize);
//        }
//        g.setFont(bigFont);
//
//        fontMetrics = g.getFontMetrics();
//        final int Y = 70;
//        final int X = 20;
//        int y = Y - fontMetrics.getHeight() + fontMetrics.getDescent();
//
//        switch (stateMachine.getState()) {
//        case Freezing:
//            g.setColor(transparentWhite);
//            g.fillRect(X, y, fontMetrics.stringWidth(acquiringImage), fontMetrics.getHeight());
//            g.setColor(Color.black);
//            g.drawString(acquiringImage, X, Y);
//            break;
//        case Frozen:
//            g.setColor(transparentRed);
//            g.fillRect(X, y, fontMetrics.stringWidth(imageAcquired), fontMetrics.getHeight());
//            g.setColor(Color.black);
//            g.drawString(imageAcquired, X, Y);
//            break;
//        case Thawed:
//            g.setColor(transparentWhite);
//            if (liveVideo != null) {
//                g.fillRect(X, y, fontMetrics.stringWidth(liveVideo), fontMetrics.getHeight());
//                g.setColor(Color.black);
//                g.drawString(liveVideo, X, Y);
//            }
//            break;
//        default:
//        }
//    }

    public static void main(String[] args) throws WebcamException, TimeoutException {
        liveVideo = null;
        JFrame top = new JFrame("Live Video");

        top.setAlwaysOnTop(true);
        Webcam webcam = Webcam.getDefault(5000L);
        final FramePanel panel = new LabeledFramePanel(Executors.newSingleThreadScheduledExecutor());
        panel.setWebcam(webcam);
        top.getContentPane().setLayout(new BorderLayout());
//        top.getContentPane().add(panel, BorderLayout.CENTER);

        top.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                panel.stop();
                super.windowClosing(e);
            }
        });

        // panel.addMouseListener(new MouseAdapter() {
        // @Override
        // public void mouseReleased(MouseEvent e) {
        // panel.toggle();
        // super.mouseReleased(e);
        // }
        // });

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
