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

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

import com.sun.javafx.tk.Toolkit;

/**
 * @author Jeff Plourde
 *
 */
public class JProgressAnimation2 {

    private final Polygon arrowPolygon = new Polygon(10);

    private final static int FIT_ARROWS = 5;
    private int arrowWidth, offset, offsetDelta;

//    private void growFontAsNeeded(String s) {
//        if (null != s) {
//            while (getFontMetrics(getFont()).stringWidth(s) < (size.width - 10) || getFontMetrics(getFont()).getHeight() < (size.height / ROWS)) {
//                setFont(getFont().deriveFont(getFont().getSize() + 1f));
//            }
//        }
//    }
//
//    private void shrinkFontAsNeeded(String s) {
//        if (null != s) {
//            while (getFontMetrics(getFont()).stringWidth(s) > (size.width - 10) || getFontMetrics(getFont()).getHeight() > (size.height / ROWS)) {
//                setFont(getFont().deriveFont(getFont().getSize() - 1f));
//            }
//        }
//    }

//    @Override
//    protected void processComponentEvent(ComponentEvent e) {
//
//        switch (e.getID()) {
//        case ComponentEvent.COMPONENT_RESIZED:
//            getSize(size);
//            if (size.width != 0 && size.height != 0) {
//                BufferedImage newImage = (BufferedImage) createImage(size.width, size.height);
//                arrowWidth = size.width / FIT_ARROWS;
//                AffineTransform transform = AffineTransform.getScaleInstance(1.0 * 0.75 * arrowWidth / arrowPolygon.getBounds().width, 1.0
//                        * size.height / (ROWS + 2) / arrowPolygon.getBounds().height);
//                scaleArrow = transform.createTransformedShape(arrowPolygon);
//                offsetDelta = (int) (0.01 * size.width);
//                offsetDelta = offsetDelta <= 0 ? 1 : offsetDelta;
//
////                growFontAsNeeded(l1);
////                shrinkFontAsNeeded(l1);
//
//                Graphics g = newImage.createGraphics();
//                if (this.image != null) {
//
//                    g.drawImage(this.image, 0, 0, size.width, size.height, this);
//
//                } else {
//                    g.setColor(getBackground());
//                    g.fillRect(0, 0, size.width, size.height);
//                }
//                this.image = newImage;
//                g.dispose();
//            }
//            break;
//        }
//        super.processComponentEvent(e);
//    }

    public JProgressAnimation2() {
        arrowPolygon.addPoint(0, 2);
        arrowPolygon.addPoint(8, 2);
        arrowPolygon.addPoint(8, 4);
        arrowPolygon.addPoint(16, 0);
        arrowPolygon.addPoint(8, -4);
        arrowPolygon.addPoint(8, -2);
        arrowPolygon.addPoint(0, -2);

        double radiansPerLine = 2.0 * Math.PI / 8.0;
        double offset = 2.0 * Math.PI / 16.0;
        for (int i = 0; i < 8; i++) {
            octagon.addPoint((int) (LINE_R2 * Math.cos(offset + i * radiansPerLine)), (int) (LINE_R2 * Math.sin(offset + i * radiansPerLine)));
            innerOctagon.addPoint((int) ((LINE_R2 - 4) * Math.cos(offset + i * radiansPerLine)),
                    (int) ((LINE_R2 - 4) * Math.sin(offset + i * radiansPerLine)));
        }

    }

    private static final double LINE_R2 = 80.0;

    private final Polygon octagon = new Polygon(10);
    private final Polygon innerOctagon = new Polygon(10);

    private boolean populated = false;

    public void setPopulated(boolean populated) {
        this.populated = populated;
    }

    private String l1;

    public void start(String drug, int vtbi, int seconds, float progress) {
        populated = true;

        l1 = drug + " PCA";

    }

    public void stop() {
        populated = true;
    }

//    private final Dimension size = new Dimension();
//    private static final Stroke STOP_STROKE = new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private static final double SCALE_RADIUS = 100.0;

    private static final int ROWS = 3;

    private String interlockText;

    public void setInterlockText(String interlockText) {
        if (null != this.interlockText && null == interlockText) {

        }
        this.interlockText = interlockText;
    }

    public void render(GraphicsContext g) {
        offset = (offset += offsetDelta) >= arrowWidth ? 0 : offset;
        
        double width = g.getCanvas().getWidth();
        double height = g.getCanvas().getHeight();

        if (populated) {
            int radius = (int) (Math.min(width / 4, height / 4));
            Affine t = g.getTransform();
            g.translate(3 * width / 4, 3 * height / 4);
            g.scale(radius / SCALE_RADIUS, radius / SCALE_RADIUS);
            g.setFill(Color.RED);
            octagon.fill(g);
    //                    g2d.setStroke(STOP_STROKE);
            g.setStroke(Color.WHITE);
            innerOctagon.stroke(g);
            g.setTransform(t);
        } else {
            g.setStroke(Color.BLACK);
            g.setFill(Color.BLACK);
//            RectBounds bounds = scaleArrow.getBounds();
//            g.strokeLine(0, 2 * height / ROWS - bounds.getHeight() / 2, width,
//                    2 * height / ROWS - bounds.getHeight() / 2);
//            g.strokeLine(0, 2 * height / ROWS + bounds.getHeight() / 2, width,
//                    2 * height / ROWS + bounds.getHeight() / 2);

            double w = Toolkit.getToolkit().getFontLoader().getFontMetrics(g.getFont()).computeStringWidth(l1);
            g.fillText(l1, width / 2 - w / 2, 1 * height / ROWS);

            g.translate(-arrowWidth + offset, 2 * height / ROWS);
            for (int i = 0; i <= FIT_ARROWS; i++) {
                arrowPolygon.fill(g);
//                g.fill(scaleArrow);
                g.translate(arrowWidth, 0);
            }
            System.err.print("DRAW");
        }
        if (interlockText != null) {

            g.setFill(Color.RED);

            String[] lines = interlockText.split("\\n");
            for (int i = 0; i < lines.length; i++) {
                g.fillText(lines[i], 0, (i + 1) * Toolkit.getToolkit().getFontLoader().getFontMetrics(g.getFont()).getLineHeight());
            }
        }
        g.setFill(Color.BLACK);
        double w = Toolkit.getToolkit().getFontLoader().getFontMetrics(g.getFont()).computeStringWidth(NO_PUMP);
        g.fillText(NO_PUMP, width / 2 - w / 2, height / 2);
    }

    private static final String NO_PUMP = "No Pump Selected";
    
    
}
