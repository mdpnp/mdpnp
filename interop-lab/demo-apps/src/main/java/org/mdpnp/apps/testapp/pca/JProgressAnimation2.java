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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class JProgressAnimation2 extends JComponent implements Runnable {

    private final Polygon arrowPolygon = new Polygon();
    private Shape scaleArrow;

    private BufferedImage image;

    private final static int FIT_ARROWS = 5;
    private int arrowWidth, offset, offsetDelta;

    private void growFontAsNeeded(String s) {
        if (null != s) {
            while (getFontMetrics(getFont()).stringWidth(s) < (size.width - 10) || getFontMetrics(getFont()).getHeight() < (size.height / ROWS)) {
                setFont(getFont().deriveFont(getFont().getSize() + 1f));
            }
        }
    }

    private void shrinkFontAsNeeded(String s) {
        if (null != s) {
            while (getFontMetrics(getFont()).stringWidth(s) > (size.width - 10) || getFontMetrics(getFont()).getHeight() > (size.height / ROWS)) {
                setFont(getFont().deriveFont(getFont().getSize() - 1f));
            }
        }
    }

    @Override
    protected void processComponentEvent(ComponentEvent e) {

        switch (e.getID()) {
        case ComponentEvent.COMPONENT_RESIZED:
            getSize(size);
            if (size.width != 0 && size.height != 0) {
                BufferedImage newImage = (BufferedImage) createImage(size.width, size.height);
                arrowWidth = size.width / FIT_ARROWS;
                AffineTransform transform = AffineTransform.getScaleInstance(1.0 * 0.75 * arrowWidth / arrowPolygon.getBounds().width, 1.0
                        * size.height / (ROWS + 2) / arrowPolygon.getBounds().height);
                scaleArrow = transform.createTransformedShape(arrowPolygon);
                offsetDelta = (int) (0.01 * size.width);
                offsetDelta = offsetDelta <= 0 ? 1 : offsetDelta;

                growFontAsNeeded(l1);
                shrinkFontAsNeeded(l1);

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

    public JProgressAnimation2(ScheduledExecutorService executor) {
        enableEvents(AWTEvent.COMPONENT_EVENT_MASK);

        arrowPolygon.addPoint(0, 2);
        arrowPolygon.addPoint(8, 2);
        arrowPolygon.addPoint(8, 4);
        arrowPolygon.addPoint(16, 0);
        arrowPolygon.addPoint(8, -4);
        arrowPolygon.addPoint(8, -2);
        arrowPolygon.addPoint(0, -2);

        this.executor = executor;

        double radiansPerLine = 2.0 * Math.PI / 8.0;
        double offset = 2.0 * Math.PI / 16.0;
        for (int i = 0; i < 8; i++) {
            octagon.addPoint((int) (LINE_R2 * Math.cos(offset + i * radiansPerLine)), (int) (LINE_R2 * Math.sin(offset + i * radiansPerLine)));
            innerOctagon.addPoint((int) ((LINE_R2 - 4) * Math.cos(offset + i * radiansPerLine)),
                    (int) ((LINE_R2 - 4) * Math.sin(offset + i * radiansPerLine)));
        }

    }

    private static final double LINE_R2 = 80.0;

    private ScheduledFuture<?> future;
    private final Polygon octagon = new Polygon();
    private final Polygon innerOctagon = new Polygon();

    private boolean populated = false;

    public void setPopulated(boolean populated) {
        this.populated = populated;
        repaint();
    }

    private String l1;

    public void start(String drug, int vtbi, int seconds, float progress) {
        populated = true;

        l1 = drug + " PCA";

        growFontAsNeeded(l1);
        shrinkFontAsNeeded(l1);

        if (null == future) {
            if (null != image) {
                Graphics g = image.createGraphics();
                g.setColor(getBackground());
                g.fillRect(0, 0, size.width, size.height);
                g.dispose();
            }

            future = executor.scheduleAtFixedRate(this, 0L, 50L, TimeUnit.MILLISECONDS);
        }
        repaint();
    }

    public void stop() {
        populated = true;
        if (null != future) {
            future.cancel(true);
            future = null;
            repaint();
        }
    }

    public void run() {

        offset = (offset += offsetDelta) >= arrowWidth ? 0 : offset;
        repaint();
    }

    private final Dimension size = new Dimension();
    private static final Stroke STOP_STROKE = new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private static final double SCALE_RADIUS = 100.0;

    private static final int ROWS = 3;

    private String interlockText;

    public void setInterlockText(String interlockText) {
        if (null != this.interlockText && null == interlockText) {

        }
        this.interlockText = interlockText;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g_) {
        super.paintComponent(g_);
        if (image != null) {
            Graphics g = image.getGraphics();
            Graphics2D g2d = (Graphics2D) g;
            g.setColor(getBackground());
            g.fillRect(0, 0, size.width, size.height);

            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (populated) {
                if (null == future) {
                    int radius = (int) (Math.min(size.width / 4, size.height / 4));
                    AffineTransform t = g2d.getTransform();
                    g2d.translate(3 * size.width / 4, 3 * size.height / 4);
                    g2d.scale(radius / SCALE_RADIUS, radius / SCALE_RADIUS);
                    g.setColor(Color.red);
                    g.fillPolygon(octagon);
                    g2d.setStroke(STOP_STROKE);
                    g.setColor(Color.white);
                    g.drawPolygon(innerOctagon);
                    g2d.setTransform(t);
                } else {
                    g.setColor(Color.black);
                    g.drawLine(0, 2 * size.height / ROWS - scaleArrow.getBounds().height / 2, size.width,
                            2 * size.height / ROWS - scaleArrow.getBounds().height / 2);
                    g.drawLine(0, 2 * size.height / ROWS + scaleArrow.getBounds().height / 2, size.width,
                            2 * size.height / ROWS + scaleArrow.getBounds().height / 2);

                    g.setColor(getForeground());
                    g.setFont(getFont());

                    int w = g.getFontMetrics().stringWidth(l1);
                    g.drawString(l1, size.width / 2 - w / 2, 1 * size.height / ROWS);

                    ((Graphics2D) g).translate(-arrowWidth + offset, 2 * size.height / ROWS);
                    for (int i = 0; i <= FIT_ARROWS; i++) {
                        ((Graphics2D) g).fill(scaleArrow);
                        ((Graphics2D) g).translate(arrowWidth, 0);
                    }
                }
                if (interlockText != null) {

                    g.setColor(Color.red);

                    String[] lines = interlockText.split("\\n");
                    for (int i = 0; i < lines.length; i++) {
                        shrinkFontAsNeeded(lines[i]);
                        g.setFont(getFont());
                        g.drawString(lines[i], 0, (i + 1) * g.getFontMetrics().getHeight());
                    }
                }
            } else {
                g.setColor(Color.black);
                g.setFont(getFont());
                int w = g.getFontMetrics().stringWidth(NO_PUMP);
                growFontAsNeeded(NO_PUMP);
                shrinkFontAsNeeded(NO_PUMP);
                g.drawString(NO_PUMP, size.width / 2 - w / 2, size.height / 2);
            }

            g.dispose();
            g_.drawImage(image, 0, 0, this);
        }
    }

    private static final String NO_PUMP = "No Pump Selected";
}
