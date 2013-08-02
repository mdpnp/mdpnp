package org.mdpnp.apps.testapp.pca;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.mdpnp.apps.testapp.vital.Value;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.apps.testapp.vital.VitalModelImpl;
import org.mdpnp.apps.testapp.vital.VitalModelListener;
import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.EventLoopHandler;
import org.mdpnp.rti.dds.DDS;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.subscription.Subscriber;

public class UITest extends JComponent implements VitalModelListener {

    private final Dimension size = new Dimension();
    private final Point center = new Point();

    protected static Color deriveColor(Color c, float a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (255f * a));
    }

    private VitalModel model;

    public void setModel(VitalModel model) {
        if (null != this.model) {
            this.model.removeListener(this);
        }
        this.model = model;
        if (null != this.model) {
            this.model.addListener(this);
        }
    }

    public VitalModel getModel() {
        return model;
    }
    
    private static final Color IDEAL_COLOR = deriveColor(Color.blue, 0.1f);
    private static final Color DATA_COLOR = deriveColor(Color.green, 0.3f);
    private static final Stroke LINE_STROKE = new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    
    // For use only in rendering (AWT event queue)
    private int[] x_points = new int[10];
    private int[] y_points = new int[10];
    private int count_points = 0;

    private int[] x_ideal_points = new int[10];
    private int[] y_ideal_points = new int[10];
    private int count_ideal_points = 0;
    
    private final void addPoint(int x, int y) {
        if(count_points>=x_points.length) { 
            this.x_points = Arrays.copyOf(this.x_points, this.x_points.length * 2 + 1);
            this.y_points = Arrays.copyOf(this.y_points, this.y_points.length * 2 + 1);
        }
        this.x_points[count_points] = x;
        this.y_points[count_points] = y;
        this.count_points++;
    }
    private final void addIdealPoint(int x, int y) {
        if(count_ideal_points>=x_ideal_points.length) { 
            this.x_ideal_points = Arrays.copyOf(this.x_ideal_points, this.x_ideal_points.length * 2 + 1);
            this.y_ideal_points = Arrays.copyOf(this.y_ideal_points, this.y_ideal_points.length * 2 + 1);
        }
        this.x_ideal_points[count_ideal_points] = x;
        this.y_ideal_points[count_ideal_points] = y;
        this.count_ideal_points++;
    }
    
    private final void resetPoints() {
        this.count_points = 0;
    }
    
    private final void resetIdealPoints() {
        this.count_ideal_points = 0;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        VitalModel model = this.model;
        if (model == null) {
            return;
        }
        int N = model.getCount();
        g = g.create();
        getSize(size);
        center.y = size.height / 2;
        center.x = size.width / 2;
        int radius = (int) (0.8 * Math.min(center.x, center.y));
        double radiansPerArc = 2.0 * Math.PI / N;

        if (g instanceof Graphics2D) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            ((Graphics2D) g).setStroke(LINE_STROKE);
        }

        g.setColor(Color.black);

        resetPoints();
        resetIdealPoints();
        
        // find the vertices of the data axes
        for (int v = 0; v < N; v++) {
            Vital vital = model.getVital(v);
            double r1 = v * radiansPerArc;
            double r2 = (v == (N - 1) ? 0 : (v + 1)) * radiansPerArc;

            int x1 = (int) (center.x + radius * Math.cos(r1));
            int x2 = (int) (center.x + radius * Math.cos(r2));
            int y1 = (int) (center.y + radius * Math.sin(r1));
            int y2 = (int) (center.y + radius * Math.sin(r2));

            // Draw an axis line for this vital
            g.drawLine(x1, y1, x2, y2);

            double slope = 1.0 * (y2 - y1) / (x2 - x1);
            double intercept = y1 - slope * x1;

            int x_ideal = (int) (0.50 * (x2 - x1) + x1);
            int y_ideal = (int) (slope * x_ideal + intercept);
            if (x1 == x2) {
                // vertical line is a special case
                x_ideal = x1;
                y_ideal = (int) (0.5 * (y2 - y1) + y1);
            }
            int length = (int) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
            addIdealPoint(x_ideal, y_ideal);

            g.setFont(g.getFont().deriveFont(20));
            if (g instanceof Graphics2D) {
                Graphics2D g2d = (Graphics2D) g;
                AffineTransform at = g2d.getTransform();
                g2d.translate(x_ideal, y_ideal);
                // g2d.rotate(Math.asin( (y2-y1) / Math.sqrt( (x2-x1)*(x2-x1) +
                // (y2-y1)*(y2-y1))));
                g2d.rotate(Math.atan2((y2 - y1), (x2 - x1)));
                // String lbl = v < LABEL.length ? LABEL[v] : "";
                String lbl = vital.getLabel();
                int maxDescent = g.getFontMetrics().getMaxDescent();
                int height = g.getFontMetrics().getHeight();
                int str_w = g.getFontMetrics().stringWidth(lbl);
                g.drawString(lbl, -str_w / 2, -2 * height - maxDescent);

                lbl = Integer.toString((int) vital.getMinimum());
                str_w = g.getFontMetrics().stringWidth(lbl);
                g.drawString(lbl, -length / 2, -1 * maxDescent - 5);
                g.drawLine(-length / 2, 0, -length / 2, -5);

                lbl = Integer.toString((int) vital.getMaximum());
                str_w = g.getFontMetrics().stringWidth(lbl);
                g.drawString(lbl, length / 2 - str_w, -maxDescent - 5);
                g.drawLine(length / 2, 0, length / 2, -5);

                lbl = Integer.toString((int) ((vital.getMaximum() - vital.getMinimum()) / 2 + vital.getMinimum()));
                str_w = g.getFontMetrics().stringWidth(lbl);
                g.drawString(lbl, -str_w / 2, -maxDescent - 5);
                g.drawLine(0, 0, 0, -5);

                g2d.setTransform(at);

            }

            if (vital.getValues().isEmpty()) {
                continue;
            } else if(vital.getValues().size() == 1) {
                float f = vital.getValues().get(0).getNumeric().value;
                double proportion = 1.0 * (f - vital.getMinimum()) / (vital.getMaximum() - vital.getMinimum());
                int x = (int) Math.floor(proportion * (x2 - x1) + x1);
                int y = (int) Math.floor(slope * x + intercept);

                if (x1 == x2) {
                    // vertical line
                    x = x1;
                    y = (int) (proportion * (y2 - y1) + y1);
                }

                addPoint(x, y);
                
            } else {
    
                float[] values = new float[vital.getValues().size()];
                int i = 0;
                for (Value val : vital.getValues()) {
                    values[i++] = val.getNumeric().value;
                }
                Arrays.sort(values);
    
                for (float f : values) {
                    double proportion = 1.0 * (f - vital.getMinimum()) / (vital.getMaximum() - vital.getMinimum());
                    int x = (int) Math.floor(proportion * (x2 - x1) + x1);
                    int y = (int) Math.floor(slope * x + intercept);
    
                    if (x1 == x2) {
                        // vertical line
                        x = x1;
                        y = (int) (proportion * (y2 - y1) + y1);
                    }
    
                    addPoint(x, y);
                }
            }
        }
        g.setColor(IDEAL_COLOR);

        if (this.count_ideal_points > 1) {
            if (this.count_ideal_points < 3) {
                g.drawLine(this.x_ideal_points[0], this.y_ideal_points[0], this.x_ideal_points[1], this.y_ideal_points[1]);
            } else {
                g.fillPolygon(this.x_ideal_points, this.y_ideal_points, this.count_ideal_points);
            }
        }

        g.setColor(DATA_COLOR);

        if (this.count_points > 1) {
            if (this.count_points < 3) {
                g.drawLine(this.x_points[0], this.y_points[0], this.x_points[1], this.y_points[1]);
            } else {
                g.fillPolygon(this.x_points, this.y_points, this.count_points);
            }
        }
    }

    public static final void main(String[] args) {

        DDS.init(false);
        final DomainParticipant p = DomainParticipantFactory.get_instance().create_participant(0,
                DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        final Subscriber s = p.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null,
                StatusKind.STATUS_MASK_NONE);
        final VitalModel vm = new VitalModelImpl();
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        
        vm.addVital("Heart Rate", new int[] { ice.MDC_PULS_OXIM_PULS_RATE.VALUE }, 0, 120);
        vm.addVital("SpO\u2082", new int[] { ice.MDC_PULS_OXIM_SAT_O2.VALUE }, 70, 130);
        vm.addVital("Respiratory Rate", new int[] { ice.MDC_RESP_RATE.VALUE }, 0, 24);
        vm.addVital("etCO\u2082", new int[] { ice.MDC_AWAY_CO2_EXP.VALUE }, 0, 60);
        EventLoop eventLoop = new EventLoop();
        final EventLoopHandler eventLoopHandler = new EventLoopHandler(eventLoop);

        vm.start(s, eventLoop);

        JFrame frame = new JFrame("UITest");
        frame.getContentPane().setBackground(Color.white);
        final UITest uiTest = new UITest();
        uiTest.setModel(vm);
        executor.scheduleAtFixedRate(new Runnable() { public void run() { uiTest.repaint(); } }, 0L, 200L, TimeUnit.MILLISECONDS);
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
//        repaint();
    }

    @Override
    public void vitalRemoved(VitalModel model, Vital vital) {
//        repaint();
    }

    @Override
    public void vitalAdded(VitalModel model, Vital vital) {
//        repaint();
    }
}
