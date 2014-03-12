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

import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;
import javax.swing.JFrame;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class JProgressAnimation extends JComponent implements Runnable {

    private final Line2D[] lines = new Line2D[18];
    private int drawLine = 0;

    private static final double SCALE_RADIUS = 100.0;
    private static final double LINE_R1 = 30.0, LINE_R2 = 80.0;

    private BufferedImage image;

    @Override
    protected void processComponentEvent(ComponentEvent e) {
        switch (e.getID()) {
        case ComponentEvent.COMPONENT_RESIZED:
            getSize(size);
            if (size.width != 0 && size.height != 0) {
                BufferedImage newImage = (BufferedImage) createImage(size.width, size.height);

                Graphics g = newImage.createGraphics();
                if (this.image != null) {

                    g.drawImage(this.image, 0, 0, size.width, size.height, this);

                } else {
                    g.setColor(getBackground());
                    g.fillRect(0, 0, size.width, size.height);
                }
                this.image = newImage;
                g.dispose();
            }
            break;
        }
        super.processComponentEvent(e);
    }

    private final ScheduledExecutorService executor;
    private final Polygon octagon = new Polygon();
    private final Polygon innerOctagon = new Polygon();

    public JProgressAnimation(ScheduledExecutorService executor) {
        this.executor = executor;
        enableEvents(AWTEvent.COMPONENT_EVENT_MASK);
        double radiansPerLine = 2.0 * Math.PI / lines.length;
        for (int i = 0; i < lines.length; i++) {
            double radians = i * radiansPerLine;
            double c = Math.cos(radians);
            double s = Math.sin(radians);
            lines[i] = new Line2D.Double(LINE_R1 * c, LINE_R1 * s, LINE_R2 * c, LINE_R2 * s);
        }
        radiansPerLine = 2.0 * Math.PI / 8.0;
        double offset = 2.0 * Math.PI / 16.0;
        for (int i = 0; i < 8; i++) {
            octagon.addPoint((int) (LINE_R2 * Math.cos(offset + i * radiansPerLine)), (int) (LINE_R2 * Math.sin(offset + i * radiansPerLine)));
            innerOctagon.addPoint((int) ((LINE_R2 - 4) * Math.cos(offset + i * radiansPerLine)),
                    (int) ((LINE_R2 - 4) * Math.sin(offset + i * radiansPerLine)));
        }

    }

    private ScheduledFuture<?> future;

    public void toggle() {
        if (null == future) {
            start();
        } else {
            stop();
        }
    }

    public void start() {
        if (null == future) {
            if (null != image) {
                Graphics g = image.createGraphics();
                g.setColor(getBackground());
                g.fillRect(0, 0, size.width, size.height);
                g.dispose();
            }
            repaint();
            future = executor.scheduleAtFixedRate(this, 0L, 100L, TimeUnit.MILLISECONDS);
        }
    }

    public void stop() {
        if (null != future) {
            future.cancel(true);
            future = null;
            repaint();
        }
    }

    public void run() {
        drawLine = ++drawLine >= lines.length ? 0 : drawLine;

        repaint();
    }

    private static final Stroke LINE_STROKE = new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private static final Stroke STOP_STROKE = new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    private final Dimension size = new Dimension();
    private final Point center = new Point();

    @Override
    protected void paintComponent(Graphics g_) {
        super.paintComponent(g_);
        if (image != null) {
            Graphics g = image.getGraphics();

            if (g instanceof Graphics2D) {
                Graphics2D g2d = (Graphics2D) g;
                if (null == future) {
                    g2d.setBackground(getBackground());
                    g2d.clearRect(0, 0, size.width, size.height);
                } else {
                    g.setColor(getBackground());
                    g2d.setComposite(AlphaComposite.SrcAtop.derive(.4f));
                    g.fillRect(0, 0, size.width, size.height);
                }

                g2d.setComposite(AlphaComposite.Src);
                AffineTransform transform = g2d.getTransform();
                center.y = size.height / 2;
                center.x = size.width / 2;
                int radius = (int) (Math.min(center.x, center.y));
                g2d.translate(center.x, center.y);
                g2d.scale(radius / SCALE_RADIUS, radius / SCALE_RADIUS);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                if (null == future) {
                    g.setColor(Color.red);
                    g.fillPolygon(octagon);
                    g2d.setStroke(STOP_STROKE);
                    g.setColor(Color.white);
                    g.drawPolygon(innerOctagon);
                    String stop = "Arr\u00EAt";
                    g.setFont(g.getFont().deriveFont(30f));
                    int width = g.getFontMetrics().stringWidth(stop);
                    int height = g.getFontMetrics().getHeight();
                    height *= SCALE_RADIUS / radius;
                    // height is not scaled
                    g.drawString(stop, -width / 2, height / 2);
                } else {
                    g2d.setColor(Color.green);

                    g2d.setStroke(LINE_STROKE);

                    // int radius = (int) (0.8 * Math.min(center.x, center.y));
                    // g2d.drawOval( (int)(radius *
                    // Math.cos(Math.toRadians(positionInDegrees)))-5,
                    // (int)(radius
                    // * Math.sin(Math.toRadians(positionInDegrees)))-5, 10,
                    // 10);
                    g2d.draw(lines[drawLine]);
                }
                g2d.setTransform(transform);
            }

            g.dispose();
            g_.drawImage(image, 0, 0, this);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("TEST THIS");
        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        frame.getContentPane().setLayout(new BorderLayout());
        final JProgressAnimation c = new JProgressAnimation(executor);
        frame.getContentPane().add(c, BorderLayout.CENTER);
        c.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                c.toggle();
                super.mouseClicked(e);
            }
        });
        c.setBackground(Color.white);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(320, 240);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                executor.shutdownNow();
                super.windowClosing(e);
            }
        });
        frame.setVisible(true);
    }
}
