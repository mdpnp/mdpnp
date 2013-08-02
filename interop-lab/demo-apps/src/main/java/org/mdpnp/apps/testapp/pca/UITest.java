package org.mdpnp.apps.testapp.pca;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
//    static int VERTICES = 4;
//    static int[][] SCALE = new int[20][2];
//    static int[] VALUE;
//    static Point[] VALUE_POINTS;
//    static Point[] IDEAL_POINTS;
//    static String[] LABEL = new String[] { "SpO\u2082", "etCO\u2082", "Resp Rate", "Heart Rate" };
    
    protected static Color deriveColor(Color c, float a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)(255f * a));
    }
    
    static {
//        VALUE = new int[SCALE.length];
//        
//        for(int i = 0; i < SCALE.length; i++) {
//            SCALE[i][0] = 0;
//            SCALE[i][1] = 100;
//            VALUE[i] = (int) (Math.random() * (SCALE[i][1]-SCALE[i][0]) + SCALE[i][0]);
//        }
//        VALUE_POINTS = new Point[VALUE.length];
//        for(int i = 0; i < VALUE_POINTS.length; i++) {
//            VALUE_POINTS[i] = new Point();
//        }
//        IDEAL_POINTS = new Point[VALUE.length]; 
//        for(int i = 0; i < IDEAL_POINTS.length; i++) {
//            IDEAL_POINTS[i] = new Point();
//        }
    }
    
    
    private VitalModel model;
    
    public void setModel(VitalModel model) {
        if(null != this.model) {
            this.model.removeListener(this);
        }
        this.model = model;
        if(null != this.model) {
            this.model.addListener(this);
        }
    }
    public VitalModel getModel() {
        return model;
    }

    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        VitalModel model = this.model;
        if(model == null) {
            return;
        }
        int N = model.getCount();
        g = g.create();
        getSize(size);
        center.y = size.height / 2;
        center.x = size.width / 2;
        int radius = (int)(0.8*Math.min(center.x, center.y));
        double radiansPerArc = 2.0 * Math.PI / N;
        
        if(g instanceof Graphics2D) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            ((Graphics2D) g).setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        }
        
        g.setColor(Color.black);
        List<Point> VALUE_POINTS = new ArrayList<Point>();
        List<Point> IDEAL_POINTS = new ArrayList<Point>();
        // find the vertices of the data axes
        for(int v = 0; v < N; v++) {
            Vital vital = model.getVital(v);
            double r1 = v * radiansPerArc;
            double r2 = (v==(N-1)?0:(v+1)) * radiansPerArc;
            
            int x1 = (int) (center.x + radius * Math.cos(r1));
            int x2 = (int) (center.x + radius * Math.cos(r2));
            int y1 = (int) (center.y + radius * Math.sin(r1));
            int y2 = (int) (center.y + radius * Math.sin(r2));
            
            g.drawLine(x1, y1, x2, y2);
            
            // Incidentally find where the data values are hanging out
//            if(v < VALUE.length && v < SCALE.length) {
                double slope = 1.0*(y2-y1)/(x2-x1);
                double intercept = y1 - slope * x1;

                int x_ideal = (int) (0.50 * (x2-x1)+x1);
                int y_ideal = (int) (slope * x_ideal + intercept);
                if(x1 == x2) {
                    // vertical line
                    x_ideal = x1;
                    y_ideal = (int)(0.5 * (y2-y1) + y1);
                }
                int length = (int) Math.sqrt( (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
                IDEAL_POINTS.add(new Point(x_ideal, y_ideal));
                g.setFont(g.getFont().deriveFont(20));
                if(g instanceof Graphics2D) {
                    Graphics2D g2d = (Graphics2D) g;
                    AffineTransform at = g2d.getTransform();
                    g2d.translate(x_ideal, y_ideal);
//                    g2d.rotate(Math.asin( (y2-y1) / Math.sqrt( (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1))));
                    g2d.rotate(Math.atan2( (y2-y1) , (x2 - x1)));
//                    String lbl = v < LABEL.length ? LABEL[v] : "";
                    String lbl = vital.getLabel();
                    int maxDescent = g.getFontMetrics().getMaxDescent();
                    int height = g.getFontMetrics().getHeight();
                    int str_w = g.getFontMetrics().stringWidth(lbl);
                    g2d.drawString(lbl, -str_w/2, -1*height-maxDescent);
                    lbl = Integer.toString((int)vital.getMinimum());
                    str_w = g.getFontMetrics().stringWidth(lbl);
                    g.drawString(lbl, -length/2, -maxDescent);
                    
                    lbl = Integer.toString((int)vital.getMaximum());
                    str_w = g.getFontMetrics().stringWidth(lbl);
                    g.drawString(lbl, length/2 - str_w, -maxDescent);
                    g2d.setTransform(at);
                    
                }
                
                if(vital.getValues().isEmpty()) {
                    continue;
                }
                
//                double proportion = 1.0 * (VALUE[v] - SCALE[v][0]) / (SCALE[v][1]-SCALE[v][0]);
                float[] values = new float[vital.getValues().size()];
                int i = 0;
                for(Value val : vital.getValues()) {
                    values[i++] = val.getNumeric().value;
                }
                Arrays.sort(values);
                
                for(float f : values) {
                    double proportion = 1.0 * (f - vital.getMinimum()) / (vital.getMaximum() - vital.getMinimum());
                    int x = (int) Math.floor(proportion * (x2-x1)+x1);
                    int y = (int) Math.floor(slope * x + intercept);
                    
                    
                    
                    if(x1 == x2) {
                        // vertical line
                        x = x1;
                        y = (int) (proportion * (y2-y1) + y1);
                    }
                    
    //                System.err.println(x1+","+y1+"  "+x2+","+y2+"  "+x+","+y+" m="+slope+" b="+intercept+" proportion="+proportion);
                    VALUE_POINTS.add(new Point(x, y));
                }
//                VALUE_POINTS[v].x = x;
//                VALUE_POINTS[v].y = y;
//                IDEAL_POINTS[v].x = x_ideal;
//                IDEAL_POINTS[v].y = y_ideal;
//                g.fillOval(x, y, 5, 5);
                

//            }
        }
        g.setColor(new Color(0f, 0f, 1f, .1f));
        
        
        int n = N < IDEAL_POINTS.size() ? N : IDEAL_POINTS.size();
        int[] x_points = new int[n];
        int[] y_points = new int[n];
        for(int v = 0; v < n; v++) {
            Point p1 = IDEAL_POINTS.get(v);
//            Point p2 = IDEAL_POINTS[(v==(n-1)?0:(v+1))];
            x_points[v] = p1.x;
            y_points[v] = p1.y;
//            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
        if(n > 1) {
            g.fillPolygon(x_points, y_points, n);
        }
//        System.err.println(VALUE_POINTS.size());

        g.setColor(deriveColor(Color.green, 0.3f));
//        g.setColor(new Color(Color.yellow.getRed(), Color.yellow.getGreen(), Color.yellow.getBlue(), 100));
//        n = N < VALUE_POINTS.size() ? N : VALUE_POINTS.size();
        n = VALUE_POINTS.size();
        x_points = new int[n];
        y_points = new int[n];
        for(int v = 0; v < n; v++) {
            Point p1 = VALUE_POINTS.get(v);
//            Point p2 = IDEAL_POINTS[(v==(n-1)?0:(v+1))];
            x_points[v] = p1.x;
            y_points[v] = p1.y;
//            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
        if(n > 1) {
            if(n < 3) {
                g.drawLine(x_points[0], y_points[0], x_points[1], y_points[1]);
            } else {
                g.fillPolygon(x_points, y_points, n);
            }
        }
//        g.setColor(getBackground());
//        g.fillOval(center.x-25, center.y-25, 50, 50);
        
    }
    public static final void main(String[] args) {
        
        DDS.init(false);
        final DomainParticipant p = DomainParticipantFactory.get_instance().create_participant(0, DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        final Subscriber s = p.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        final VitalModel vm = new VitalModelImpl();
        
//        vm.addListener(new VitalModelListener() {
//            
//            @Override
//            public void vitalRemoved(VitalModel model, Vital vital) {
//                System.out.println("Removed:"+vital);
//            }
//            
//            @Override
//            public void vitalChanged(VitalModel model, Vital vital) {
//                System.out.println(new Date()+" Changed:"+vital);
//            }
//            
//            @Override
//            public void vitalAdded(VitalModel model, Vital vital) {
//                System.out.println("Added:"+vital);
//            }
//        });
        vm.addVital("Heart Rate", new int[] {ice.MDC_PULS_OXIM_PULS_RATE.VALUE}, 20, 200);
        vm.addVital("SpO\u2082", new int[] {ice.MDC_PULS_OXIM_SAT_O2.VALUE}, 70, 130);
        vm.addVital("Respiratory Rate", new int[] {ice.MDC_RESP_RATE.VALUE}, 0, 20);
        vm.addVital("etCO\u2082", new int[] {ice.MDC_AWAY_CO2_EXP.VALUE}, 0, 50);
        EventLoop eventLoop = new EventLoop();
        final EventLoopHandler eventLoopHandler = new EventLoopHandler(eventLoop);
        
        vm.start(s, eventLoop);
     
        
        
        JFrame frame = new JFrame("UITest");
        frame.getContentPane().setBackground(Color.white);
        final UITest uiTest = new UITest();
        uiTest.setModel(vm);
//        uiTest.setBackground(Color.white);
//        uiTest.setOpaque(false);
//        final JSlider slider = new JSlider();
//        slider.setMaximum(20);
//        slider.setMinimum(2);
//        slider.setValue(4);
//        slider.setMajorTickSpacing(1);
//        slider.setSnapToTicks(true);
//        slider.setLabelTable(slider.createStandardLabels(1));
//        slider.setPaintLabels(true);
//        slider.getModel().addChangeListener(new ChangeListener() {
//
//            @Override
//            public void stateChanged(ChangeEvent e) {
//                VERTICES = slider.getValue();
//                uiTest.repaint();
//            }
//            
//        });
//        final ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();
//        
//        final ScheduledFuture<?> future = es.scheduleAtFixedRate(new Runnable() {
//            public void run() {
//                for(int v = 0; v < VALUE.length; v++) {
//                    VALUE[v] += Math.round( (1.4*Math.random()-.7) );
//                    VALUE[v] = Math.min(SCALE[v][1], VALUE[v]);
//                    VALUE[v] = Math.max(SCALE[v][0], VALUE[v]);
//                }
//                uiTest.repaint();
//            }
//        }, 0L, 1000L, TimeUnit.MILLISECONDS);
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
//        frame.getContentPane().add(slider, BorderLayout.SOUTH);
        frame.setSize(640, 480);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    @Override
    public void vitalChanged(VitalModel model, Vital vital) {
        repaint();
    }
    @Override
    public void vitalRemoved(VitalModel model, Vital vital) {
        repaint();
    }
    @Override
    public void vitalAdded(VitalModel model, Vital vital) {
        repaint();
    }
}
