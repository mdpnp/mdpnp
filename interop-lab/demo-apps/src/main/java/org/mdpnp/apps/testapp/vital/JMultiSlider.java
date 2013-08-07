package org.mdpnp.apps.testapp.vital;

import java.awt.AWTEvent;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class JMultiSlider extends JComponent implements ChangeListener {
    
    private final Dimension size = new Dimension();
    private final List<Rectangle> rectangles = new ArrayList<Rectangle>();
    private BoundedRangeMultiModel model;
    private Color[] rangeColor = new Color[0];

    private static final Stroke LINE_STROKE = new BasicStroke(3f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        
        g.setFont(getFont());
        
        Graphics2D g2d = (Graphics2D) g;
        getSize(size);
        
        
        
        g.drawLine(0,size.height/2,size.width,size.height/2);
        for(int i = 0; i < 10; i++) {
            
        }
        int idx = 0;
        
        
        g.drawLine(0, size.height/2, 0, size.height/2+2);
        String s = Integer.toString(model.getMinimum());
        
        int h = g.getFontMetrics().getHeight();
        g.drawString(s, 0, size.height/2+h+2);
        
        
        g.drawLine(size.width-1, size.height/2, size.width-1, size.height/2+2);
        s = Integer.toString(model.getMaximum());
        int w = g.getFontMetrics().stringWidth(s);
        
        g.drawString(s, size.width - w, size.height/2+h+2);
        
        final int N = 6;
         
        w = size.width / (N-1);
        for(int i = 1; i < N; i++) {
            int val = (int) (1.0 * i / N * (model.getMaximum() - model.getMinimum()) + model.getMinimum());
            s = Integer.toString(val);
            w = g.getFontMetrics().stringWidth(s);
            int x = (int) (1.0 * i / N * size.width);
            g.drawLine(x, size.height/2, x, size.height/2+2);
            g.drawString(s, x-w/2, size.height/2+h+2);
        }
        
//        s = Integer.toString( (model.getMaximum()+model.getMinimum()) / 2);
//        w = g.getFontMetrics().stringWidth(s);
//        g.drawLine(size.width/2, size.height/2, size.width/2, size.height/2+2);
//        g.drawString(s, size.width / 2 - w / 2, size.height/2+h+2);
        
        
        g2d.setStroke(LINE_STROKE);
        
        if(rangeColor.length > 0 && rangeColor[0] != null) {
            g.setColor(rangeColor[0]);
            Rectangle right = rectangles.get(0);
            Rectangle left = rectangles.get(rectangles.size()-1);
            g.fillRect(0, right.y, right.x, right.height);
            g.fillRect(left.x+left.width, left.y, size.width - (left.x+left.width), left.height);
        }
        
        int leftofcenter = rectangles.size() / 2;
        
        for(int i = 0; i < leftofcenter; i++) {
            if((i+1) < rangeColor.length && rangeColor[i+1] != null) {
                g.setColor(rangeColor[i+1]);
                Rectangle left = rectangles.get(i);
                Rectangle right = rectangles.get(i+1);
                g.fillRect(left.x, left.y, right.x-left.x, left.height);
                
                right = rectangles.get(rectangles.size()-i-1);
                left = rectangles.get(rectangles.size()-i-2);
                g.fillRect(left.x+left.width, left.y, right.x+right.width-(left.x+left.width), left.height);
            }
        }
        
        
        // middle segment
        if(rectangles.size() > 1 && (leftofcenter+1) < rangeColor.length && rangeColor[leftofcenter+1] != null) {
            g.setColor(rangeColor[leftofcenter+1]);
            Rectangle left = rectangles.get(leftofcenter);
            Rectangle right = rectangles.get(leftofcenter+1);
            g.fillRect(left.x, left.y, right.x-left.x+right.width, left.height);
            
//            right = rectangles.get(rectangles.size()-i-1);
//            left = rectangles.get(rectangles.size()-i-2);
//            g.fillRect(left.x+left.width, left.y, right.x+right.width-(left.x+left.width), left.height);
        }
        
        
        g.setColor(getForeground());
        for(Rectangle r : rectangles) {
//            System.out.println(r);
            if(idx < rectangles.size() / 2) {
                // left bound
                g.drawLine(r.x, r.y, r.x, r.y + r.height);
                g.drawLine(r.x, r.y, r.x + r.width, r.y);
            } else {
                // right bound
                g.drawLine(r.x+r.width, r.y, r.x+r.width, r.y + r.height);
                g.drawLine(r.x, r.y, r.x + r.width, r.y);
            }
//            g.fillRect(r.x, r.y, r.width, r.height);
            idx++;
        }
        g.setColor(Color.blue);
        for(int i = 0; i < model.getMarkerCount(); i++) {
            Float val = model.getMarker(i);
            if(null != val) {
                double p = 1.0 * (val - model.getMinimum()) / (model.getMaximum() - model.getMinimum());
                int x = (int) (p * size.width);
                g.drawLine(x, size.height/2-9, x, size.height/2);
            }
        }
        
    }
    
    public JMultiSlider() {
        this(new DefaultBoundedRangeMultiModel());
    }
    
    public JMultiSlider(BoundedRangeMultiModel model) {
        enableEvents(AWTEvent.COMPONENT_EVENT_MASK);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
        setModel(model);
        setMinimumSize(new Dimension(100, 50));
        setPreferredSize(new Dimension(100, 50));
    }
    
    public void setModel(BoundedRangeMultiModel model) {
        if(null != this.model) {
            this.model.removeChangeListener(this);
        }
        this.model = model;
        if(null != this.model) {
            this.model.addChangeListener(this);
            stateChanged(new ChangeEvent(model));
        }
    }
    
    public BoundedRangeMultiModel getModel() {
        return model;
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(new JMultiSlider(), BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 100);
        frame.setVisible(true);
    }

    private void updateIt(BoundedRangeMultiModel model) {
        getSize(size);
//        System.out.println(size);
        while(rectangles.size() < model.getValueCount()) {
            rectangles.add(new Rectangle(9, 9));
        }
        while(rectangles.size() > model.getValueCount()) {
            rectangles.remove(0);
        }
        int minimum = model.getMinimum();
        int maximum = model.getMaximum();
        int range = maximum - minimum;
        for(int i = 0; i < model.getValueCount(); i++) {
            Rectangle r = rectangles.get(i);
            r.y = size.height/2-r.height;
            Float value = model.getValue(i);
            if(null == value) {
                if(i < model.getValueCount()/2) {
                    r.x = -r.width;
                } else {
                    r.x = size.width;
                }
            } else {
                double p = 1.0 * (value - minimum) / range;
                if(i < model.getValueCount() / 2) {
                    r.x = (int) (p * size.width);
                } else {
                    r.x = (int) (p * size.width) - r.width;
                }
            }
        }
//        System.out.println(rectangles);
        repaint();
    }
    
    @Override
    public void stateChanged(ChangeEvent e) {
        updateIt((BoundedRangeMultiModel) e.getSource());
    }
    
    @Override
    protected void processComponentEvent(ComponentEvent e) {
        switch(e.getID()) { 
        case ComponentEvent.COMPONENT_RESIZED:
            updateIt(model);
            break;
        }
        super.processComponentEvent(e);
    }
    

    private Point startingMousePoint;
    private Point startingRectPoint;
    private int idx;

    @Override
    protected void processMouseMotionEvent(MouseEvent e) {
        switch(e.getID()) {
        case MouseEvent.MOUSE_DRAGGED:
//            System.out.println(e);
            if(null != startingMousePoint) {
                float mouse_position;
                if(idx < model.getValueCount()/2) {
                    mouse_position = e.getPoint().x - rectangles.get(idx).width/2;
                } else {
                    mouse_position = e.getPoint().x + rectangles.get(idx).width/2;
                }
                mouse_position = (float) (1.0 * mouse_position / size.width * (model.getMaximum() - model.getMinimum()) + model.getMinimum());
//                int x_delta = e.getPoint().x - startingMousePoint.x;
//                int x = startingRectPoint.x + x_delta;
                model.setValue(idx, mouse_position);
//                System.out.println(idx + " " + model.getValue(idx));
//                updateIt(model);
            }
            
            break;
        }
        super.processMouseMotionEvent(e);
    }
    
    @Override
    protected void processMouseEvent(MouseEvent e) {
        getSize(size);
        
        switch(e.getID()) {
        case MouseEvent.MOUSE_PRESSED:
            idx = 0;
            for(Rectangle r : rectangles) {
                if(r.contains(e.getPoint())) {
                    startingMousePoint = new Point(e.getPoint());
                    startingRectPoint = new Point(r.x, r.y);
                    model.setValueIsAdjusting(true);
//                    updateIt(model);
                    break;
                }
                idx++;
            }
            
            break;
        case MouseEvent.MOUSE_RELEASED:
            if(startingMousePoint!=null) {
                startingMousePoint = null;
                startingRectPoint = null;
                model.setValueIsAdjusting(false);
//                updateIt(model);
            }
            break;
        }
        
        super.processMouseEvent(e);
    }
    
    public void setRangeColor(int idx, Color color) {
        if(idx >= rangeColor.length) {
            rangeColor = Arrays.copyOf(rangeColor, idx+1);
        }
        rangeColor[idx] = color;
    }
}
