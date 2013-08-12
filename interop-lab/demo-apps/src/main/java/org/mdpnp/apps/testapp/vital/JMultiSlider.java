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
import java.awt.image.BufferedImage;
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

    private static final int[] HAPPY_INTERVALS = {5, 10, 20, 50, 100};
    
    @Override
    protected void paintComponent(Graphics g_) {
        super.paintComponent(g_);

        if (image != null) {
            Graphics g = image.getGraphics();

          g.setColor(getBackground());
          g.fillRect(0, 0, size.width, size.height);
            
            // Graphics2D g2d = (Graphics2D) g;
            // getSize(size);
          
          g.setColor(getForeground());

            g.drawLine(0, size.height / 2, size.width, size.height / 2);
            for (int i = 0; i < 10; i++) {

            }

            g.drawLine(0, size.height / 2, 0, size.height / 2 + 2);
            String s = Integer.toString(model.getMinimum());

            int h = g.getFontMetrics().getHeight();
            g.drawString(s, 0, size.height / 2 + h + 2);

            g.drawLine(size.width - 1, size.height / 2, size.width - 1, size.height / 2 + 2);
            s = Integer.toString(model.getMaximum());
            int w = g.getFontMetrics().stringWidth(s);

            g.drawString(s, size.width - w, size.height / 2 + h + 2);

            
            int range = model.getMaximum() - model.getMinimum();
            
            int width_max = Math.max(g.getFontMetrics().stringWidth(Integer.toString(model.getMaximum())), g.getFontMetrics().stringWidth(Integer.toString(model.getMinimum())));
            // leave some extra wiggle room
            width_max += 5;
            for(int i = 0; i < HAPPY_INTERVALS.length; i++) {
                int required_width = width_max * (range / HAPPY_INTERVALS[i]);
                if(required_width < size.width) {
                    int N = range/HAPPY_INTERVALS[i];
                    w = size.width / (N - 1);
                    for (int j = 1; j < N; j++) {
                        int val = ((int) (1.0 * j / N * (model.getMaximum() - model.getMinimum()) + model.getMinimum()) / HAPPY_INTERVALS[i]) * HAPPY_INTERVALS[i];
                        s = Integer.toString(val);
                        w = g.getFontMetrics().stringWidth(s);
                        int x = (int) (1.0 * j / N * size.width);
                        g.drawLine(x, size.height / 2, x, size.height / 2 + 2);
                        g.drawString(s, x - w / 2, size.height / 2 + h + 2);
                    }
                    break;
                }
            }
            



            // s = Integer.toString( (model.getMaximum()+model.getMinimum()) /
            // 2);
            // w = g.getFontMetrics().stringWidth(s);
            // g.drawLine(size.width/2, size.height/2, size.width/2,
            // size.height/2+2);
            // g.drawString(s, size.width / 2 - w / 2, size.height/2+h+2);

            ((Graphics2D) g).setStroke(LINE_STROKE);

            if (rangeColor.length > 0 && rangeColor[0] != null) {
                g.setColor(rangeColor[0]);
                Rectangle right = rectangles.get(0);
                Rectangle left = rectangles.get(rectangles.size() - 1);
                g.fillRect(0, right.y, right.x, right.height);
                g.fillRect(left.x + left.width, left.y, size.width - (left.x + left.width), left.height);
            }

            int leftofcenter = rectangles.size() / 2;

            for (int i = 0; i < leftofcenter; i++) {
                if ((i + 1) < rangeColor.length && rangeColor[i + 1] != null) {
                    g.setColor(rangeColor[i + 1]);
                    Rectangle left = rectangles.get(i);
                    Rectangle right = rectangles.get(i + 1);
                    g.fillRect(left.x, left.y, right.x - left.x, left.height);

                    right = rectangles.get(rectangles.size() - i - 1);
                    left = rectangles.get(rectangles.size() - i - 2);
                    g.fillRect(left.x + left.width, left.y, right.x + right.width - (left.x + left.width), left.height);
                }
            }

            // middle segment
            if (rectangles.size() > 1 && (leftofcenter + 1) < rangeColor.length && rangeColor[leftofcenter + 1] != null) {
                g.setColor(rangeColor[leftofcenter + 1]);
                Rectangle left = rectangles.get(leftofcenter);
                Rectangle right = rectangles.get(leftofcenter + 1);
                g.fillRect(left.x, left.y, right.x - left.x + right.width, left.height);

                // right = rectangles.get(rectangles.size()-i-1);
                // left = rectangles.get(rectangles.size()-i-2);
                // g.fillRect(left.x+left.width, left.y,
                // right.x+right.width-(left.x+left.width), left.height);
            }

            g.setColor(getForeground());
            int idx = 0;
            for (Rectangle r : rectangles) {
                // System.out.println(r);
                if (idx < rectangles.size() / 2) {
                    // left bound
                    g.drawLine(r.x, r.y, r.x, r.y + r.height);
                    g.drawLine(r.x, r.y, r.x + r.width, r.y);
                    Float f = model.getValue(idx);
                    String l = null == f ? null : Integer.toString((int)(float)f);
                    if(null != f) {
                        int wi = g.getFontMetrics().stringWidth(l);
                        g.drawString(l, r.x+r.width/2-wi/2, size.height / 2 - 14);
                    }
                } else {
                    // right bound
                    g.drawLine(r.x + r.width, r.y, r.x + r.width, r.y + r.height);
                    g.drawLine(r.x, r.y, r.x + r.width, r.y);
                    Float f = model.getValue(idx);
                    String l = null == f ? null : Integer.toString((int)(float)f);
                    if(null != f) {
                        int wi = g.getFontMetrics().stringWidth(l);
                        g.drawString(l, r.x+r.width/2-wi/2, size.height / 2 - 14);
                    }
                }
                // g.fillRect(r.x, r.y, r.width, r.height);
                idx++;
            }
            g.setColor(Color.blue);
            for (int i = 0; i < model.getMarkerCount(); i++) {
                Float val = model.getMarker(i);
                if (null != val) {
                    double p = 1.0 * (val - model.getMinimum()) / (model.getMaximum() - model.getMinimum());
                    int x = (int) (p * size.width);
                    
                    // pull in points that would go over the edge
                    if ((size.width - x) < 3) {
                        x -= 3;
                    }
                    if ((x - 0) < 3) {
                        x += 3;
                    }
                    
                    g.drawLine(x, size.height / 2 - 9, x, size.height / 2);
                    
                    String l = Integer.toString((int)(float)val);
                    int wi = g.getFontMetrics().stringWidth(l);
                    g.drawString(l, x-wi/2, size.height/2 - 14);
                }
            }
            g.dispose();
        }

        g_.drawImage(image, 0, 0, this);
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
        if (null != this.model) {
            this.model.removeChangeListener(this);
        }
        this.model = model;
        if (null != this.model) {
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

        // System.out.println(size);
        while (rectangles.size() < model.getValueCount()) {
            rectangles.add(new Rectangle(9, 9));
        }
        while (rectangles.size() > model.getValueCount()) {
            rectangles.remove(0);
        }
        int minimum = model.getMinimum();
        int maximum = model.getMaximum();
        int range = maximum - minimum;
        for (int i = 0; i < model.getValueCount(); i++) {
            Rectangle r = rectangles.get(i);
            r.y = size.height / 2 - r.height;
            Float value = model.getValue(i);
            if (null == value) {
                if (i < model.getValueCount() / 2) {
                    r.x = -r.width-10;
                } else {
                    r.x = size.width+10;
                }
            } else {
                double p = 1.0 * (value - minimum) / range;
                if (i < model.getValueCount() / 2) {
                    r.x = (int) (p * size.width);
                } else {
                    r.x = (int) (p * size.width) - r.width;
                }
            }
        }
        // System.out.println(rectangles);
        repaint();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        updateIt((BoundedRangeMultiModel) e.getSource());
    }

    private BufferedImage image;

    @Override
    protected void processComponentEvent(ComponentEvent e) {
        switch (e.getID()) {
        case ComponentEvent.COMPONENT_RESIZED:
            getSize(size);
            if (size.width != 0 && size.height != 0) {
                BufferedImage newImage = (BufferedImage) createImage(size.width, size.height);

                Graphics g = newImage.createGraphics();

                this.image = newImage;
                g.dispose();
            }
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
        switch (e.getID()) {
        case MouseEvent.MOUSE_DRAGGED:
            // System.out.println(e);
            if (null != startingMousePoint) {
                float mouse_position;
                if (idx < model.getValueCount() / 2) {
                    mouse_position = e.getPoint().x - rectangles.get(idx).width / 2;
                } else {
                    mouse_position = e.getPoint().x + rectangles.get(idx).width / 2;
                }
                mouse_position = (float) (1.0 * mouse_position / size.width * (model.getMaximum() - model.getMinimum()) + model
                        .getMinimum());
                // int x_delta = e.getPoint().x - startingMousePoint.x;
                // int x = startingRectPoint.x + x_delta;
                model.setValue(idx, mouse_position);
                // System.out.println(idx + " " + model.getValue(idx));
                // updateIt(model);
            }

            break;
        }
        super.processMouseMotionEvent(e);
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        getSize(size);

        switch (e.getID()) {
        case MouseEvent.MOUSE_PRESSED:
            idx = 0;
            for (Rectangle r : rectangles) {
                if (r.contains(e.getPoint())) {
                    startingMousePoint = new Point(e.getPoint());
                    startingRectPoint = new Point(r.x, r.y);
                    model.setValueIsAdjusting(true);
                    // updateIt(model);
                    break;
                }
                idx++;
            }

            break;
        case MouseEvent.MOUSE_RELEASED:
            if (startingMousePoint != null) {
                startingMousePoint = null;
                startingRectPoint = null;
                model.setValueIsAdjusting(false);
                // updateIt(model);
            }
            break;
        }

        super.processMouseEvent(e);
    }

    public void setRangeColor(int idx, Color color) {
        if (idx >= rangeColor.length) {
            rangeColor = Arrays.copyOf(rangeColor, idx + 1);
        }
        rangeColor[idx] = color;
    }
}
