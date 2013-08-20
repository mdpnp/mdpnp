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
public class JProgressAnimation2 extends JComponent implements Runnable {

    private final Polygon arrowPolygon = new Polygon();
    private Shape scaleArrow;

    private BufferedImage image;
    
    private final static String TEXT = "Active Infusion";
    
    private final static int FIT_ARROWS = 5;
    private int arrowWidth, offset, offsetDelta;

    
    private void growFontAsNeeded(String s) {
        if(null != s) {
            while(getFontMetrics(getFont()).stringWidth(s) < (size.width-10) || getFontMetrics(getFont()).getHeight() < (size.height/ROWS)) {
                setFont(getFont().deriveFont(getFont().getSize()+1f));
            }
        }
    }
    
    private void shrinkFontAsNeeded(String s) {
        if(null != s) {
            while(getFontMetrics(getFont()).stringWidth(s) > (size.width-10) || getFontMetrics(getFont()).getHeight() > (size.height / ROWS)) {
                setFont(getFont().deriveFont(getFont().getSize()-1f));
            }    
        }
    }
    
    @Override
    protected void processComponentEvent(ComponentEvent e) {
        
        
        switch (e.getID()) {
        case ComponentEvent.COMPONENT_RESIZED:
            getSize(size);
            if(size.width != 0 && size.height != 0) {
                BufferedImage newImage = (BufferedImage) createImage(size.width, size.height);
                arrowWidth = size.width / FIT_ARROWS; 
                AffineTransform transform = AffineTransform.getScaleInstance(1.0 * 0.75 *arrowWidth / arrowPolygon.getBounds().width, 1.0 * size.height / (ROWS+2) / arrowPolygon.getBounds().height); 
                scaleArrow = transform.createTransformedShape(arrowPolygon);
                offsetDelta = (int) (0.01 * size.width);
                offsetDelta = offsetDelta<=0?1:offsetDelta;
                
                growFontAsNeeded(TEXT);
                shrinkFontAsNeeded(TEXT);
                shrinkFontAsNeeded(l1);
                shrinkFontAsNeeded(l2);
                
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
        

        double radiansPerLine = 2.0 * Math.PI / 8.0;
        double offset = 2.0 * Math.PI / 16.0;
        for (int i = 0; i < 8; i++) {
            octagon.addPoint((int) (LINE_R2 * Math.cos(offset + i * radiansPerLine)),
                    (int) (LINE_R2 * Math.sin(offset + i * radiansPerLine)));
            innerOctagon.addPoint((int) ((LINE_R2 - 4) * Math.cos(offset + i * radiansPerLine)),
                    (int) ((LINE_R2 - 4) * Math.sin(offset + i * radiansPerLine)));
        }

    }

    private static final double LINE_R2 = 80.0;
    
    private ScheduledFuture<?> future;
    private final Polygon octagon = new Polygon();
    private final Polygon innerOctagon = new Polygon();

//    public void toggle() {
//        if (null == future) {
//            start();
//        } else {
//            stop();
//        }
//    }

    private boolean populated = false;
    
    public void setPopulated(boolean populated) {
        this.populated = populated;
        repaint();
    }
    
    private String l1, l2;
    
    public void start(String drug, int vtbi, int seconds, float progress) {
        populated = true;
        
        l1 = drug + " " + vtbi + " mL";
        double done = progress * seconds;
        l2 = secondsToHHMMSS((int)done) + " / " + secondsToHHMMSS(seconds);
        
        shrinkFontAsNeeded(l1);
        shrinkFontAsNeeded(l2);
        
        if (null == future) {
            if(null != image) {
                Graphics g = image.createGraphics();
                g.setColor(getBackground());
                g.fillRect(0,0,size.width,size.height);
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

        offset = (offset+=offsetDelta)>=arrowWidth?0:offset;
        repaint();
    }




    private final Dimension size = new Dimension();
    private static final Stroke STOP_STROKE = new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private static final double SCALE_RADIUS = 100.0;
    
    private static final int ROWS = 5;
    
    private static final String twoDigits(int n) {
        if(n == 0) {
            return "00";
        } else if(n < 10) {
            return "0"+n;
        } else {
            return Integer.toString(n);
        }
    }
    
    private static final String secondsToHHMMSS(int seconds) {
        int hrs = seconds / 3600;
        int mins = (seconds - hrs * 3600) / 60;
        int s = (seconds - hrs * 3600 - mins * 60);
        return twoDigits(hrs)+":"+twoDigits(mins)+":"+twoDigits(s);
        
    }
    
    private String interlockText;
    
    public void setInterlockText(String interlockText) {
        if(null != this.interlockText && null == interlockText) {
            growFontAsNeeded(TEXT);
        }
        this.interlockText = interlockText;
    }
    
    @Override
    protected void paintComponent(Graphics g_) {
        super.paintComponent(g_);
        if(image != null) {
            Graphics g = image.getGraphics();
            Graphics2D g2d = (Graphics2D) g;
            g.setColor(getBackground());
            g.fillRect(0, 0, size.width, size.height);
            
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if(populated) {
                if (null == future) {
                    int radius = (int) (Math.min(size.width / 4, size.height / 4));
                    AffineTransform t = g2d.getTransform();
                    g2d.translate(3*size.width/4, 3*size.height/4);
                    g2d.scale(radius / SCALE_RADIUS, radius / SCALE_RADIUS);
                    g.setColor(Color.red);
                    g.fillPolygon(octagon);
                    g2d.setStroke(STOP_STROKE);
                    g.setColor(Color.white);
                    g.drawPolygon(innerOctagon);
                    g2d.setTransform(t);
    //                String stop = "Arr\u00EAt";
    //                String stop = "STOP";
    //                g.setFont(g.getFont().deriveFont(50f));
    //                int width = g.getFontMetrics().stringWidth(stop);
    //                int height = g.getFontMetrics().getHeight();
                    
    //                height *= SCALE_RADIUS / radius;
    //                width *= SCALE_RADIUS / radius;
    //                 height is not scaled
    //                g.drawString(stop, -width/2, -height / 2);
                } else {
                    g.setColor(Color.black);
                    g.drawLine(0, 3 * size.height / ROWS - scaleArrow.getBounds().height / 2, size.width, 3 * size.height / ROWS - scaleArrow.getBounds().height / 2);
                    g.drawLine(0, 3 * size.height / ROWS + scaleArrow.getBounds().height / 2, size.width, 3 * size.height / ROWS + scaleArrow.getBounds().height / 2);
                    
                    g.setColor(getForeground());
                    g.setFont(getFont());
                    int w = g.getFontMetrics().stringWidth(TEXT);
                    g.drawString(TEXT, size.width/2-w/2, size.height / ROWS);
                    
                    w = g.getFontMetrics().stringWidth(l1);
                    g.drawString(l1, size.width/2-w/2, 2 * size.height / ROWS);
                    
                    w = g.getFontMetrics().stringWidth(l2);
                    g.drawString(l2, size.width/2-w/2, 5 * size.height / ROWS);
                    
                    
                    ((Graphics2D)g).translate(-arrowWidth+offset, 3 * size.height / ROWS);
                    for(int i = 0; i <= FIT_ARROWS; i++) {
                        
    //                    ((Graphics2D)g).scale(5.0, 5.0);
                        ((Graphics2D)g).fill(scaleArrow);
    //                    ((Graphics2D)g).scale(1.0/5.0, 1.0/5.0);
                        ((Graphics2D)g).translate(arrowWidth, 0);
                    }
                }
            } else {
                g.setColor(Color.black);
                g.setFont(getFont());
                int w = g.getFontMetrics().stringWidth(NO_PUMP);
                shrinkFontAsNeeded(NO_PUMP);
                g.drawString(NO_PUMP, size.width/2-w/2, size.height/2);
            }
            if(interlockText != null) {
                
                g.setColor(Color.red);
                
                String[] lines = interlockText.split("\\n");
                for(int i = 0; i < lines.length; i++) {
                    shrinkFontAsNeeded(lines[i]);
                    g.setFont(getFont());
                    g.drawString(lines[i], 0, (i+1)*g.getFontMetrics().getHeight());
                }
            }
            g.dispose();
            g_.drawImage(image, 0, 0, this);
        }
    }
    private static final String NO_PUMP = "No Pump Selected";
//    public static void main(String[] args) {
//        JFrame frame = new JFrame("TEST THIS");
//        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
//        frame.getContentPane().setLayout(new BorderLayout());
//        final JProgressAnimation2 c = new JProgressAnimation2(executor);
//        frame.getContentPane().add(c, BorderLayout.CENTER);
//        c.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                c.toggle();
//                super.mouseClicked(e);
//            }
//        });
//        c.setBackground(Color.white);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(320, 240);
//        frame.addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                executor.shutdownNow();
//                super.windowClosing(e);
//            }
//        });
//        frame.setVisible(true);
//    }
}
