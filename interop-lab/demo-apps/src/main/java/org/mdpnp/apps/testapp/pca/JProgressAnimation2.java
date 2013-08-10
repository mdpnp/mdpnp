package org.mdpnp.apps.testapp.pca;

import java.awt.AWTEvent;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class JProgressAnimation2 extends JComponent implements Runnable {

    private final Polygon arrowPolygon = new Polygon();
    private Shape scaleArrow;

    private BufferedImage image;
    
    private final static String TEXT = "Active Infusion";
    
    private final static int FIT_ARROWS = 5;
    private int arrowWidth, offset, offsetDelta;

    @Override
    protected void processComponentEvent(ComponentEvent e) {
        
        
        switch (e.getID()) {
        case ComponentEvent.COMPONENT_RESIZED:
            getSize(size);
            if(size.width != 0 && size.height != 0) {
                BufferedImage newImage = (BufferedImage) createImage(size.width, size.height);
                arrowWidth = size.width / FIT_ARROWS; 
                AffineTransform transform = AffineTransform.getScaleInstance(1.0 * 0.75 *arrowWidth / arrowPolygon.getBounds().width, 1.0 * size.height / 4 / arrowPolygon.getBounds().height); 
                scaleArrow = transform.createTransformedShape(arrowPolygon);
                offsetDelta = (int) (0.01 * size.width);
                offsetDelta = offsetDelta<=0?1:offsetDelta;
                
                while(getFontMetrics(getFont()).stringWidth(TEXT) < (size.width-10)) {
                    setFont(getFont().deriveFont(getFont().getSize()+1f));
                }
                
                while(getFontMetrics(getFont()).stringWidth(TEXT) > (size.width-10)) {
                    setFont(getFont().deriveFont(getFont().getSize()-1f));
                }
                
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
        
        arrowPolygon.addPoint(0,2);
        arrowPolygon.addPoint(8,2);
        arrowPolygon.addPoint(8,4);
        arrowPolygon.addPoint(16,0);
        arrowPolygon.addPoint(8,-4);
        arrowPolygon.addPoint(8,-2);
        arrowPolygon.addPoint(0,-2);
        
        
        
        this.executor = executor;
        

        double radiansPerLine = 2.0 * Math.PI / 6.0;
        for (int i = 0; i < 6; i++) {
            hexagon.addPoint((int) (LINE_R2 * Math.cos(i * radiansPerLine)),
                    (int) (LINE_R2 * Math.sin(i * radiansPerLine)));
            innerHexagon.addPoint((int) ((LINE_R2 - 4) * Math.cos(i * radiansPerLine)),
                    (int) ((LINE_R2 - 4) * Math.sin(i * radiansPerLine)));
        }

    }

    private static final double LINE_R2 = 80.0;
    
    private ScheduledFuture<?> future;
    private final Polygon hexagon = new Polygon();
    private final Polygon innerHexagon = new Polygon();

    public void toggle() {
        if (null == future) {
            start();
        } else {
            stop();
        }
    }

    public void start() {
        if (null == future) {
            if(null != image) {
                Graphics g = image.createGraphics();
                g.setColor(getBackground());
                g.fillRect(0,0,size.width,size.height);
                g.dispose();
            }
            repaint();
            future = executor.scheduleAtFixedRate(this, 0L, 50L, TimeUnit.MILLISECONDS);
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

        offset = (offset+=offsetDelta)>=arrowWidth?0:offset;
        repaint();
    }


    private final Dimension size = new Dimension();
    private static final Stroke STOP_STROKE = new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private static final double SCALE_RADIUS = 100.0;
    
    @Override
    protected void paintComponent(Graphics g_) {
        super.paintComponent(g_);
        if(image != null) {
            Graphics g = image.getGraphics();
            Graphics2D g2d = (Graphics2D) g;
            g.setColor(getBackground());
            g.fillRect(0, 0, size.width, size.height);
            
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (null == future) {
                int radius = (int) (Math.min(size.width / 2, size.height / 2));
                
                g2d.translate(size.width/2, size.height/2);
                g2d.scale(radius / SCALE_RADIUS, radius / SCALE_RADIUS);
                g.setColor(Color.red);
                g.fillPolygon(hexagon);
                g2d.setStroke(STOP_STROKE);
                g.setColor(Color.white);
                g.drawPolygon(innerHexagon);
//                String stop = "Arr\u00EAt";
//                g.setFont(g.getFont().deriveFont(30f));
//                int width = g.getFontMetrics().stringWidth(stop);
//                int height = g.getFontMetrics().getHeight();
                
//                height *= SCALE_RADIUS / radius;
                // height is not scaled
//                g.drawString(stop, -width/2, height / 2);
            } else {
                g2d.setColor(Color.green);
                g.setFont(getFont());
                int w = g.getFontMetrics().stringWidth(TEXT);
                g.drawString(TEXT, size.width/2-w/2, size.height / 3);
                ((Graphics2D)g).translate(-arrowWidth+offset, 2 * size.height / 3);
                for(int i = 0; i <= FIT_ARROWS; i++) {
                    
//                    ((Graphics2D)g).scale(5.0, 5.0);
                    ((Graphics2D)g).fill(scaleArrow);
//                    ((Graphics2D)g).scale(1.0/5.0, 1.0/5.0);
                    ((Graphics2D)g).translate(arrowWidth, 0);
                }
            }
            
            

            g.dispose();
            g_.drawImage(image, 0, 0, this);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("TEST THIS");
        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        frame.getContentPane().setLayout(new BorderLayout());
        final JProgressAnimation2 c = new JProgressAnimation2(executor);
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
