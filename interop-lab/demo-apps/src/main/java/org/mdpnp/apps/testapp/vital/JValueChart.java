package org.mdpnp.apps.testapp.vital;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class JValueChart extends JComponent {
    private Value value;
    
    public JValueChart(final Value value) {
        enableEvents(ComponentEvent.COMPONENT_EVENT_MASK);
        this.value = value;
    }
    
    public void setValue(Value value) {
        this.value = value;
        repaint();
    }
    
    private final Dimension size = new Dimension();
    private BufferedImage image;
    
    @Override
    protected void processComponentEvent(ComponentEvent e) {
        switch(e.getID()) {
        case ComponentEvent.COMPONENT_RESIZED:
            getSize(size);
            if (size.width != 0 && size.height != 0) {
                BufferedImage newImage = (BufferedImage) createImage(size.width, size.height);
                Graphics g = newImage.createGraphics();
                g.dispose();
                this.image = newImage;
            }
            break;
        }
        super.processComponentEvent(e);
    }
    
    @Override
    protected void paintComponent(Graphics g_) {
        super.paintComponent(g_);
        
        if(null == value) {
            return;
        }
        
        if(null != image) {
            Graphics g = image.getGraphics();
            
            float high = Float.MIN_VALUE;
            float low = Float.MAX_VALUE;
            long first = Long.MAX_VALUE;
            long last = Long.MIN_VALUE;
            
            for(int i = 0; i < value.getHistoryCount(); i++) {
                high = Math.max(high, value.getHistoryValue(i));
                low = Math.min(low, value.getHistoryValue(i));
                first = Math.min(first, value.getHistoryTime(i));
                last = Math.max(last, value.getHistoryTime(i));
            }
            Color c = g.getColor();
            g.setColor(getBackground());
            g.fillRect(0, 0, size.width,  size.height);
            
            g.setColor(getForeground());

            Integer last_x = null;
            Integer last_y = null;
            
            for(int i = 0; i < value.getHistoryCount(); i++) {
                int x = (int) (size.width * 1.0*(value.getHistoryTime(i)-first)/(last-first));
                int y = size.height - (int) (size.height * 1.0 * (value.getHistoryValue(i)-low)/(high-low));
                if(null != last_x && null != last_y) {
                    g.drawLine(last_x, last_y, x, y);
                }
                
                last_x = x;
                last_y = y;
            }
            
            g.setColor(c);
            
            g.dispose();
            g_.drawImage(image, 0, 0, this);
        }
        
    }
}   
