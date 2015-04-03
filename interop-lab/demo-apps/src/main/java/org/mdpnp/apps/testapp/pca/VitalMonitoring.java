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

import java.util.Arrays;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Affine;
import javafx.util.Duration;

import org.mdpnp.apps.testapp.vital.Value;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;

/**
 * @author Jeff Plourde
 *
 */
public class VitalMonitoring implements VitalModelContainer {
    @FXML BorderPane container;
    @FXML Canvas canvas;
    
    private double width, height, center_x, center_y;

    protected static Color deriveColor(Color c, float a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
    }


    public VitalMonitoring() {
    }

    public void setup() {
    }
    
    private VitalModel model;
    private Timeline timeline = new Timeline(new KeyFrame(new Duration(100L), new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {
            render(canvas.getGraphicsContext2D());
        }
        
    }));
    
    public void setModel(VitalModel model) {
        
        if (null != this.model) {
            timeline.stop();
        }
        this.model = model;
        if (null != this.model) {
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        }
    }

    public VitalModel getModel() {
        return model;
    }

    private static final Color IDEAL_COLOR = deriveColor(Color.BLUE, 0.8f);
    private static final Color DATA_COLOR = deriveColor(Color.GREEN, 0.3f);
    private static final Color WARN_DATA_COLOR = deriveColor(Color.YELLOW, 0.5f);
    private static final Color ALARM_DATA_COLOR = deriveColor(Color.RED, 0.9f);
    private static final Color WHITEN_COLOR = deriveColor(Color.WHITE, 0.8f);
//    private static final Stroke LINE_STROKE = new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    private float[] vital_values = new float[10];

    // For use only in rendering
    private final Polygon chartArea = new Polygon(10);
    private final Polygon dataArea = new Polygon(10);
    private final Polygon idealArea = new Polygon(10);

    
    
    // / THIS LOGIC SHOULD LIVE OUTSIDE OF THE FX THREAD
    // / and probably draw an offscreen buffer
    public void render(GraphicsContext g) {
        
        VitalModel model = this.model;
        if (model == null) {
            return;
        }

        int N = model.size();

        width = g.getCanvas().getWidth();
        height = g.getCanvas().getHeight();

        g.clearRect(0, 0, width, height);

        center_y = height / 2;
        center_x = width / 2;

        if (N < 3) {
            final String s = "Please add at least three vital signs.";
            final FontMetrics fm = Toolkit.getToolkit().getFontLoader().getFontMetrics(g.getFont());
            final float height = fm.getLineHeight();
            final float str_w = fm.computeStringWidth(s);
            g.fillText(s, center_x - str_w / 2, center_y + height / 2);
            return;
        }

        int radius = (int) (0.8 * Math.min(center_x, center_y));
        double radiansPerArc = 2.0 * Math.PI / N;

//        if (g instanceof Graphics2D) {
//            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//            ((Graphics2D) g).setStroke(LINE_STROKE);
//        }

        g.setStroke(Color.BLACK);
        g.setFill(Color.BLACK);

        chartArea.clear();
        dataArea.clear();
        idealArea.clear();

        // int countVitalsOut = 0;
        // int countVitalsAbsent = 0;

        // find the vertices of the data axes
        for (int v = 0; v < N; v++) {
            Vital vital = model.get(v);
            // countVitalsOut += vital.countOutOfBounds();
            double r1 = v * radiansPerArc;
            double r2 = (v == (N - 1) ? 0 : (v + 1)) * radiansPerArc;

            double x1 = center_x + radius * Math.cos(r1);
            double x2 = center_x + radius * Math.cos(r2);
            double y1 = center_y + radius * Math.sin(r1);
            double y2 = center_y + radius * Math.sin(r2);

            final boolean REVERSE_DIRECTION = y2 > y1;
            final boolean VERTICAL = Math.abs(x2 - x1) <= 1;

            double minimum = REVERSE_DIRECTION ? vital.getDisplayMaximum() : vital.getDisplayMinimum();
            double maximum = REVERSE_DIRECTION ? vital.getDisplayMinimum() : vital.getDisplayMaximum();

            String minimumLabel = REVERSE_DIRECTION ? vital.getLabelMaximum() : vital.getLabelMinimum();
            String maximumLabel = REVERSE_DIRECTION ? vital.getLabelMinimum() : vital.getLabelMaximum();

            Double low = vital.getWarningLow();
            Double high = vital.getWarningHigh();

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
            g.strokeLine(x1, y1, x2, y2);
            chartArea.addPoint(x1, y1);

            double slope = 1.0 * (y2 - y1) / (x2 - x1);
            double intercept = y1 - slope * x1;

            if (REVERSE_DIRECTION) {
                double proportion = 1.0 * (high - minimum) / (maximum - minimum);
                double x_ideal = proportion * (x2 - x1) + x1;
                double y_ideal = slope * x_ideal + intercept;
                if (VERTICAL) {
                    // vertical line is a special case
                    x_ideal = x1;
                    y_ideal = (int) (proportion * (y2 - y1) + y1);
                }
                idealArea.addPoint(x_ideal, y_ideal);

                proportion = 1.0 * (low - minimum) / (maximum - minimum);
                x_ideal = proportion * (x2 - x1) + x1;
                y_ideal = slope * x_ideal + intercept;
                if (VERTICAL) {
                    // vertical line is a special case
                    x_ideal = x1;
                    y_ideal = (int) (proportion * (y2 - y1) + y1);
                }
                idealArea.addPoint(x_ideal, y_ideal);

            } else {
                double proportion = 1.0 * (low - minimum) / (maximum - minimum);
                double x_ideal = proportion * (x2 - x1) + x1;
                double y_ideal = slope * x_ideal + intercept;
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
//            g.setFont(g.getFont()
            double length = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));

            double x_ideal = 0.5 * (x2 - x1) + x1;
            double y_ideal = slope * x_ideal + intercept;
            if (VERTICAL) {
                // vertical line is a special case
                x_ideal = x1;
                y_ideal = (int) (0.5 * (y2 - y1) + y1);
            }

//            if (g instanceof Graphics2D) {
//                Graphics2D g2d = (Graphics2D) g;
//                AffineTransform at = g2d.getTransform();
            Affine at = g.getTransform();
            g.translate(x_ideal, y_ideal);
//                g2d.translate(x_ideal, y_ideal);
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
                g.rotate(Math.toDegrees(rotate));
                // String lbl = v < LABEL.length ? LABEL[v] : "";

                // Vital name
                String lbl = vital.getLabel() + " (" + vital.getUnits() + ")";
                FontMetrics fm = Toolkit.getToolkit().getFontLoader().getFontMetrics(g.getFont());
                float maxDescent = fm.getMaxDescent();
                float height = fm.getLineHeight();
                float str_w = fm.computeStringWidth(lbl);
                if (FLIP) {
                    g.fillText(lbl, -str_w / 2,  3 * height + maxDescent);
//                    g.drawString(lbl, -str_w / 2, 3 * height + maxDescent);
                } else {
                    g.fillText(lbl, -str_w / 2, FLIP_SIGN * (-2 * height - maxDescent));
//                    g.drawString(lbl, -str_w / 2, FLIP_SIGN * (-2 * height - maxDescent));
                }

                // Low end of the scale
                lbl = minimumLabel;
                str_w = fm.computeStringWidth(lbl);
                if (FLIP) {
                    g.fillText(lbl, length / 2 - str_w, maxDescent + height + 5);
                    g.strokeLine(length / 2, 0, length / 2,  5);
                } else {
                    g.fillText(lbl, -length / 2, -maxDescent - 5);
                    g.strokeLine(-length/2, 0, -length/2, -5);
                }

                // Alarm low limit
                if (null != vital.getWarningLow()) {
                    Paint c = g.getStroke();
                    g.setStroke(IDEAL_COLOR);
                    lbl = Integer.toString((int) (double) low);
                    str_w = fm.computeStringWidth(lbl);
                    double proportion = 1.0 * (low - minimum) / (maximum - minimum);
                    proportion -= 0.5;
                    int xloc = (int) (proportion * length);
                    if (FLIP) {
                        g.fillText(lbl, -xloc - str_w / 2, maxDescent + height + 5);
                        g.strokeLine(-xloc, 0, -xloc, 5);
                    } else {
                        g.fillText(lbl, xloc - str_w / 2, -maxDescent - 5);
                        g.strokeLine(xloc, 0, xloc, -5);
                    }
                    g.setStroke(c);
                }

                // High end of the scale
                lbl = maximumLabel;
                str_w = fm.computeStringWidth(lbl);
                if (FLIP) {
                    g.fillText(lbl, -length / 2,  maxDescent + 5 + height);
                    g.strokeLine(-length / 2,  0, -length / 2, 5);
                } else {
                    g.fillText(lbl, length / 2 - str_w, -maxDescent - 5);
                    g.strokeLine(length / 2, 0, length / 2, -5);
                }

                // Alarm high limit
                if (null != vital.getWarningHigh()) {
                    Paint c = g.getStroke();
                    g.setStroke(IDEAL_COLOR);
                    lbl = Integer.toString((int) (double) high);
                    str_w = fm.computeStringWidth(lbl);
                    double proportion = 1.0 * (high - minimum) / (maximum - minimum);
                    proportion -= 0.5;
                    double xloc = proportion * length;
                    if (FLIP) {
                        g.fillText(lbl, -xloc - str_w / 2, maxDescent + height + 5);
                        g.strokeLine(-xloc, 0, -xloc, 5);
                    } else {
                        g.fillText(lbl, xloc - str_w / 2, -maxDescent - 5);
                        g.strokeLine(xloc, 0, xloc, -5);
                    }
                    g.setStroke(c);
                }
                // Middle of the scale
                lbl = Integer.toString((int) ((maximum - minimum) / 2 + minimum));
                str_w = fm.computeStringWidth(lbl);
                if (FLIP) {
                    g.fillText(lbl, -str_w / 2, maxDescent + 5 + height);
                    g.strokeLine(0, 0, 0, 5);
                } else {
                    g.fillText(lbl, -str_w / 2, -maxDescent - 5);
                    g.strokeLine(0, 0, 0, -5);
                }

                g.setTransform(at);

//            }

            if (vital.isEmpty()) {
                // countVitalsAbsent++;
                continue;
            } else {
                while (vital.size() > vital_values.length) {
                    vital_values = new float[vital_values.length * 2 + 1];
                }

                int i = 0;
                for (Value val : vital) {
                    if (!val.isIgnore()) {
                        vital_values[i++] = val.getValue();
                    }
                }
                Arrays.sort(vital_values, 0, i);

                if (REVERSE_DIRECTION && i > 1) {
                    for (int k = 0; k < i / 2; k++) {
                        float tmp = vital_values[k];
                        vital_values[k] = vital_values[i - 1 - k];
                        vital_values[i - 1 - k] = tmp;
                    }
                }

                for (int j = 0; j < i; j++) {
                    float f = vital_values[j];
                    double proportion = 1.0 * (f - minimum) / (maximum - minimum);
                    double x = proportion * (x2 - x1) + x1;
                    double y = slope * x + intercept;

                    if (VERTICAL) {
                        // vertical line
                        x = x1;
                        y = (int) (proportion * (y2 - y1) + y1);
                    }
                    dataArea.addPoint(x, y);
                }
            }
        }
        g.setFill(WHITEN_COLOR);
        chartArea.fill(g);
        g.setStroke(Color.BLACK);
        chartArea.stroke(g);
        g.setStroke(IDEAL_COLOR);
        idealArea.stroke(g);

        switch (model.getState()) {
        case Alarm:
            g.setStroke(ALARM_DATA_COLOR);
            g.setFill(ALARM_DATA_COLOR);
            break;
        case Warning:
            g.setStroke(WARN_DATA_COLOR);
            g.setFill(WARN_DATA_COLOR);
            break;
        case Normal:
            g.setStroke(DATA_COLOR);
            g.setFill(DATA_COLOR);
            break;
        default:
        }

        if (dataArea.getCount() > 1) {
            if (dataArea.getCount() < 3) {
                g.strokeLine(dataArea.getXPoints()[0], dataArea.getYPoints()[0], dataArea.getXPoints()[1], dataArea.getYPoints()[1]);
            } else {
                dataArea.fill(g);
            }
        }
    }

    public static final void main(String[] args) {

//        RtConfig.loadAndSetIceQos();
//
//        RtConfig rtSetup = RtConfig.setupDDS(0);
//        final EventLoop eventLoop=rtSetup.getEventLoop();
//        final Publisher pub=rtSetup.getPublisher();
//        final Subscriber s=rtSetup.getSubscriber();
//        final DomainParticipant participant=rtSetup.getParticipant();
//        final TimeManager timeManager = new TimeManager(pub, s, 
//                AbstractSimulatedDevice.randomUDI(), "VitalMonitoring");
//        final DeviceListModel nc = new DeviceListModel(rtSetup.getSubscriber(),
//                rtSetup.getEventLoop(),
//                timeManager);
//        
//        final EventLoopHandler handler = rtSetup.getHandler();
//
//        final VitalModel vm = new VitalModelImpl(nc);
//
//        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
//
//        rtSetup.getEventLoop().doLater(new Runnable() {
//            public void run() {
//                nc.start();
//            }
//        });
//        vm.start(s, pub, eventLoop);
//
//        eventLoop.doLater(new Runnable() {
//            public void run() {
//                VitalSign.SpO2.addToModel(vm);
//                VitalSign.RespiratoryRate.addToModel(vm);
//                VitalSign.EndTidalCO2.addToModel(vm);
//            }
//        });
//
//
//        JFrame frame = new JFrame("UITest");
//        frame.getContentPane().setBackground(Color.white);
//        final VitalMonitoring uiTest = new VitalMonitoring();
//        uiTest.setModel(vm);
//        executor.scheduleAtFixedRate(new Runnable() {
//            public void run() {
//                uiTest.repaint();
//            }
//        }, 0L, 500L, TimeUnit.MILLISECONDS);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                vm.stop();
//                try {
//                    handler.shutdown();
//                } catch (InterruptedException e1) {
//                    e1.printStackTrace();
//                }
//                participant.delete_subscriber(s);
//                participant.delete_contained_entities();
//                DomainParticipantFactory.get_instance().delete_participant(participant);
//                DomainParticipantFactory.finalize_instance();
//                super.windowClosing(e);
//            }
//        });
//        frame.getContentPane().setLayout(new BorderLayout());
//        frame.getContentPane().add(uiTest, BorderLayout.CENTER);
//        // frame.getContentPane().add(slider, BorderLayout.SOUTH);
//        frame.setSize(640, 480);
//        frame.setLocationRelativeTo(null);
//        frame.setVisible(true);
    }
}
