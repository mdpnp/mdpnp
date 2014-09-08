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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.mdpnp.apps.testapp.vital.Value;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.apps.testapp.vital.VitalModelImpl;
import org.mdpnp.apps.testapp.vital.VitalModelListener;
import org.mdpnp.devices.EventLoopHandler;
import org.mdpnp.rtiapi.data.EventLoop;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class VitalMonitoring extends JComponent implements VitalModelListener, Runnable {

    private final Dimension size = new Dimension();
    private final Point center = new Point();

    private static final long REFRESH_RATE_MS = 100L;

    protected static Color deriveColor(Color c, float a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (255f * a));
    }

    private final ScheduledExecutorService executor;
    private ScheduledFuture<?> future;

    public VitalMonitoring() {
        this(null);
    }

    public VitalMonitoring(ScheduledExecutorService executor) {
        this.executor = executor;

    }

    public void run() {
        if (isVisible()) {
            repaint();
        }
    }

    private VitalModel model;

    public void setModel(VitalModel model) {
        if (null != this.model) {
            if (future != null) {
                future.cancel(true);
                future = null;
            }
            this.model.removeListener(this);
        }
        this.model = model;
        if (null != this.model) {
            this.model.addListener(this);
            if (executor != null) {
                future = executor.scheduleAtFixedRate(this, 0L, REFRESH_RATE_MS, TimeUnit.MILLISECONDS);
            }
        }
    }

    public VitalModel getModel() {
        return model;
    }

    private static final Color IDEAL_COLOR = deriveColor(Color.blue, 0.8f);
    private static final Color DATA_COLOR = deriveColor(Color.green, 0.3f);
    private static final Color WARN_DATA_COLOR = deriveColor(Color.yellow, 0.5f);
    private static final Color ALARM_DATA_COLOR = deriveColor(Color.red, 0.9f);
    private static final Color WHITEN_COLOR = deriveColor(Color.white, 0.8f);
    private static final Stroke LINE_STROKE = new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    private float[] vital_values = new float[10];

    // For use only in rendering (AWT event queue)
    private final Polygon chartArea = new Polygon();
    private final Polygon dataArea = new Polygon();
    private final Polygon idealArea = new Polygon();

    // / THIS LOGIC SHOULD LIVE OUTSIDE OF THE AWT THREAD
    // / and probably draw an offscreen buffer
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        VitalModel model = this.model;
        if (model == null) {
            return;
        }

        int N = model.getCount();

        getSize(size);
        center.y = size.height / 2;
        center.x = size.width / 2;

        if (N < 3) {
            String s = "Please add at least three vital signs.";
            int width = g.getFontMetrics().stringWidth(s);
            int height = g.getFontMetrics().getHeight();
            g.drawString("Please add at least three vital signs.", center.x - width / 2, center.y + height / 2);
            return;
        }

        int radius = (int) (0.8 * Math.min(center.x, center.y));
        double radiansPerArc = 2.0 * Math.PI / N;

        if (g instanceof Graphics2D) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            ((Graphics2D) g).setStroke(LINE_STROKE);
        }

        g.setColor(Color.black);

        chartArea.reset();
        dataArea.reset();
        idealArea.reset();

        // int countVitalsOut = 0;
        // int countVitalsAbsent = 0;

        // find the vertices of the data axes
        for (int v = 0; v < N; v++) {
            Vital vital = model.getVital(v);
            // countVitalsOut += vital.countOutOfBounds();
            double r1 = v * radiansPerArc;
            double r2 = (v == (N - 1) ? 0 : (v + 1)) * radiansPerArc;

            int x1 = (int) Math.round(center.x + radius * Math.cos(r1));
            int x2 = (int) Math.round(center.x + radius * Math.cos(r2));
            int y1 = (int) Math.round(center.y + radius * Math.sin(r1));
            int y2 = (int) Math.round(center.y + radius * Math.sin(r2));

            final boolean REVERSE_DIRECTION = y2 > y1;
            final boolean VERTICAL = Math.abs(x2 - x1) <= 1;

            float minimum = REVERSE_DIRECTION ? vital.getDisplayMaximum() : vital.getDisplayMinimum();
            float maximum = REVERSE_DIRECTION ? vital.getDisplayMinimum() : vital.getDisplayMaximum();

            String minimumLabel = REVERSE_DIRECTION ? vital.getLabelMaximum() : vital.getLabelMinimum();
            String maximumLabel = REVERSE_DIRECTION ? vital.getLabelMinimum() : vital.getLabelMaximum();

            Float low = vital.getWarningLow();
            Float high = vital.getWarningHigh();

            if (null == low) {
                if (null == high) {
                    low = vital.getMinimum();
                    high = vital.getMaximum();
                } else {
                    low = high - 2 * (high - vital.getMinimum());
                }
            } else {
                if (null == high) {
                    high = low + 2 * (vital.getMaximum() - low);
                } else {
                    // All's well
                }
            }

            // float low = REVERSE_DIRECTION ? vital.getHigh() : vital.getLow();
            // float high = REVERSE_DIRECTION ? vital.getLow() :
            // vital.getHigh();

            // Draw an axis line for this vital
            // g.drawLine(x1, y1, x2, y2);
            chartArea.addPoint(x1, y1);

            double slope = 1.0 * (y2 - y1) / (x2 - x1);
            double intercept = y1 - slope * x1;

            if (REVERSE_DIRECTION) {
                double proportion = 1.0 * (high - minimum) / (maximum - minimum);
                int x_ideal = (int) (proportion * (x2 - x1) + x1);
                int y_ideal = (int) (slope * x_ideal + intercept);
                if (VERTICAL) {
                    // vertical line is a special case
                    x_ideal = x1;
                    y_ideal = (int) (proportion * (y2 - y1) + y1);
                }
                idealArea.addPoint(x_ideal, y_ideal);

                proportion = 1.0 * (low - minimum) / (maximum - minimum);
                x_ideal = (int) (proportion * (x2 - x1) + x1);
                y_ideal = (int) (slope * x_ideal + intercept);
                if (VERTICAL) {
                    // vertical line is a special case
                    x_ideal = x1;
                    y_ideal = (int) (proportion * (y2 - y1) + y1);
                }
                idealArea.addPoint(x_ideal, y_ideal);

            } else {
                double proportion = 1.0 * (low - minimum) / (maximum - minimum);
                int x_ideal = (int) (proportion * (x2 - x1) + x1);
                int y_ideal = (int) (slope * x_ideal + intercept);
                if (VERTICAL) {
                    // vertical line is a special case
                    x_ideal = x1;
                    y_ideal = (int) (proportion * (y2 - y1) + y1);
                }

                idealArea.addPoint(x_ideal, y_ideal);

                proportion = 1.0 * (high - minimum) / (maximum - minimum);
                x_ideal = (int) (proportion * (x2 - x1) + x1);
                y_ideal = (int) (slope * x_ideal + intercept);
                if (VERTICAL) {
                    // vertical line is a special case
                    x_ideal = x1;
                    y_ideal = (int) (proportion * (y2 - y1) + y1);
                }

                idealArea.addPoint(x_ideal, y_ideal);
            }
            g.setFont(g.getFont().deriveFont(20f));
            int length = (int) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));

            int x_ideal = (int) (0.5 * (x2 - x1) + x1);
            int y_ideal = (int) (slope * x_ideal + intercept);
            if (VERTICAL) {
                // vertical line is a special case
                x_ideal = x1;
                y_ideal = (int) (0.5 * (y2 - y1) + y1);
            }

            if (g instanceof Graphics2D) {
                Graphics2D g2d = (Graphics2D) g;
                AffineTransform at = g2d.getTransform();
                g2d.translate(x_ideal, y_ideal);
                // g2d.rotate(Math.asin( (y2-y1) / Math.sqrt( (x2-x1)*(x2-x1) +
                // (y2-y1)*(y2-y1))));
                double rotate = Math.atan2((y2 - y1), (x2 - x1));
                boolean FLIP = false;
                if (VERTICAL) {
                    rotate -= Math.PI;
                    FLIP = true;
                    // vertical line case
                    // rotate -= Math.PI;
                    // FLIP = ;
                } else if (rotate > Math.PI / 2.0) {
                    rotate -= Math.PI;
                    FLIP = true;
                } else if (rotate < -Math.PI / 2.0) {
                    rotate += Math.PI;
                    FLIP = true;
                }
                int FLIP_SIGN = FLIP ? -1 : 1;
                // System.out.println(vital.getLabel() + " rotate " +
                // Math.toDegrees(rotate));
                g2d.rotate(rotate);
                // String lbl = v < LABEL.length ? LABEL[v] : "";

                // Vital name
                String lbl = vital.getLabel() + " (" + vital.getUnits() + ")";
                int maxDescent = g.getFontMetrics().getMaxDescent();
                int height = g.getFontMetrics().getHeight();
                int str_w = g.getFontMetrics().stringWidth(lbl);
                if (FLIP) {
                    g.drawString(lbl, -str_w / 2, 3 * height + maxDescent);
                } else {
                    g.drawString(lbl, -str_w / 2, FLIP_SIGN * (-2 * height - maxDescent));
                }

                // Low end of the scale
                lbl = minimumLabel;
                str_w = g.getFontMetrics().stringWidth(lbl);
                if (FLIP) {
                    g.drawString(lbl, length / 2 - str_w, maxDescent + height + 5);
                    g.drawLine(length / 2, 0, length / 2, 5);
                } else {
                    g.drawString(lbl, -length / 2, -maxDescent - 5);
                    g.drawLine(-length / 2, 0, -length / 2, -5);
                }

                // Alarm low limit
                if (null != vital.getWarningLow()) {
                    Color c = g.getColor();
                    g.setColor(IDEAL_COLOR);
                    lbl = Integer.toString((int) (float) low);
                    str_w = g.getFontMetrics().stringWidth(lbl);
                    double proportion = 1.0 * (low - minimum) / (maximum - minimum);
                    proportion -= 0.5;
                    int xloc = (int) (proportion * length);
                    if (FLIP) {
                        g.drawString(lbl, -xloc - str_w / 2, maxDescent + height + 5);
                        g.drawLine(-xloc, 0, -xloc, 5);
                    } else {
                        g.drawString(lbl, xloc - str_w / 2, -maxDescent - 5);
                        g.drawLine(xloc, 0, xloc, -5);
                    }
                    g.setColor(c);
                }

                // High end of the scale
                lbl = maximumLabel;
                str_w = g.getFontMetrics().stringWidth(lbl);
                if (FLIP) {
                    g.drawString(lbl, -length / 2, maxDescent + 5 + height);
                    g.drawLine(-length / 2, 0, -length / 2, 5);
                } else {
                    g.drawString(lbl, length / 2 - str_w, -maxDescent - 5);
                    g.drawLine(length / 2, 0, length / 2, -5);
                }

                // Alarm high limit
                if (null != vital.getWarningHigh()) {
                    Color c = g.getColor();
                    g.setColor(IDEAL_COLOR);
                    lbl = Integer.toString((int) (float) high);
                    str_w = g.getFontMetrics().stringWidth(lbl);
                    double proportion = 1.0 * (high - minimum) / (maximum - minimum);
                    proportion -= 0.5;
                    int xloc = (int) (proportion * length);
                    if (FLIP) {
                        g.drawString(lbl, -xloc - str_w / 2, maxDescent + height + 5);
                        g.drawLine(-xloc, 0, -xloc, 5);
                    } else {
                        g.drawString(lbl, xloc - str_w / 2, -maxDescent - 5);
                        g.drawLine(xloc, 0, xloc, -5);
                    }
                    g.setColor(c);
                }
                // Middle of the scale
                lbl = Integer.toString((int) ((maximum - minimum) / 2 + minimum));
                str_w = g.getFontMetrics().stringWidth(lbl);
                if (FLIP) {
                    g.drawString(lbl, -str_w / 2, maxDescent + 5 + height);
                    g.drawLine(0, 0, 0, 5);
                } else {
                    g.drawString(lbl, -str_w / 2, -maxDescent - 5);
                    g.drawLine(0, 0, 0, -5);
                }

                g2d.setTransform(at);

            }

            if (vital.getValues().isEmpty()) {
                // countVitalsAbsent++;
                continue;
            } else {
                while (vital.getValues().size() > vital_values.length) {
                    vital_values = new float[vital_values.length * 2 + 1];
                }

                int i = 0;
                for (Value val : vital.getValues()) {
                    if (!val.isIgnore()) {
                        vital_values[i++] = val.getNumeric().value;
                    }
                }
                Arrays.sort(vital_values, 0, i);

                if (REVERSE_DIRECTION && i > 1) {
                    // System.out.println(i+ " "
                    // +Arrays.toString(vital_values));
                    for (int k = 0; k < i / 2; k++) {
                        float tmp = vital_values[k];
                        vital_values[k] = vital_values[i - 1 - k];
                        vital_values[i - 1 - k] = tmp;
                    }
                }

                for (int j = 0; j < i; j++) {
                    float f = vital_values[j];
                    double proportion = 1.0 * (f - minimum) / (maximum - minimum);
                    int x = (int) Math.floor(proportion * (x2 - x1) + x1);
                    int y = (int) Math.floor(slope * x + intercept);

                    if (VERTICAL) {
                        // vertical line
                        x = x1;
                        y = (int) (proportion * (y2 - y1) + y1);
                    }
                    dataArea.addPoint(x, y);
                }
            }
        }
        g.setColor(WHITEN_COLOR);
        g.fillPolygon(chartArea);
        g.setColor(Color.black);

        g.drawPolygon(chartArea);

        g.setColor(IDEAL_COLOR);
        g.drawPolygon(idealArea);

        switch (model.getState()) {
        case Alarm:
            g.setColor(ALARM_DATA_COLOR);
            break;
        case Warning:
            g.setColor(WARN_DATA_COLOR);
            break;
        case Normal:
            g.setColor(DATA_COLOR);
            break;
        default:
        }

        if (dataArea.npoints > 1) {
            if (dataArea.npoints < 3) {
                g.drawLine(dataArea.xpoints[0], dataArea.ypoints[0], dataArea.xpoints[1], dataArea.ypoints[1]);
            } else {
                g.fillPolygon(dataArea);
            }
        }
    }

    public static final void main(String[] args) {

        final DomainParticipant p = DomainParticipantFactory.get_instance().create_participant(0, DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT,
                null, StatusKind.STATUS_MASK_NONE);
        final Subscriber s = p.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        final Publisher pub = p.create_publisher(DomainParticipant.PUBLISHER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        final VitalModel vm = new VitalModelImpl(null);
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        
        EventLoop eventLoop = new EventLoop();
        final EventLoopHandler eventLoopHandler = new EventLoopHandler(eventLoop);

        vm.start(s, pub, eventLoop);

        JFrame frame = new JFrame("UITest");
        frame.getContentPane().setBackground(Color.white);
        final VitalMonitoring uiTest = new VitalMonitoring(null);
        uiTest.setModel(vm);
        executor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                uiTest.repaint();
            }
        }, 0L, 500L, TimeUnit.MILLISECONDS);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                vm.stop();
                try {
                    eventLoopHandler.shutdown();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                p.delete_subscriber(s);
                p.delete_contained_entities();
                DomainParticipantFactory.get_instance().delete_participant(p);
                DomainParticipantFactory.finalize_instance();
                super.windowClosing(e);
            }
        });
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(uiTest, BorderLayout.CENTER);
        // frame.getContentPane().add(slider, BorderLayout.SOUTH);
        frame.setSize(640, 480);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public void vitalChanged(VitalModel model, Vital vital) {
        if (null == executor) {
            repaint();
        }
    }

    @Override
    public void vitalRemoved(VitalModel model, Vital vital) {
        if (null == executor) {
            repaint();
        }
    }

    @Override
    public void vitalAdded(VitalModel model, Vital vital) {
        if (null == executor) {
            repaint();
        }
    }
}
