package org.mdpnp.apps.testapp.vital;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.util.concurrent.ExecutorService;

import javax.swing.JComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JValueChart extends JComponent {
    private Value value;

    public JValueChart(final Value value) {
        enableEvents(ComponentEvent.COMPONENT_EVENT_MASK);
        this.value = value;
    }

    private static final Logger log = LoggerFactory.getLogger(JValueChart.class);

    public void setValue(Value value) {
        this.value = value;
        if (null == value) {
            return;
        }

        if (null != image) {
            final int historyCount = value.getHistoryCount();

            Graphics g = image.getGraphics(); 

            Integer last_x = null;
            int last_y = 0;

            long last = Long.MIN_VALUE;
            long first = Long.MAX_VALUE;
            float high = Float.MIN_VALUE, low = Float.MAX_VALUE;

            for (int i = 0; i < historyCount; i++) {
                long tm = value.getHistoryTime(i);
                float v = value.getHistoryValue(i);
                high = Math.max(high, v);
                low = Math.min(low, v);
                last = Math.max(last, tm);
                first = Math.min(first, tm);
            }

            first = last - RECENT_HISTORY;

            Color c = g.getColor();
            g.setColor(getBackground());
            g.fillRect(0, 0, size.width, size.height);

            g.setColor(getForeground());

            for (int i = 0; i < historyCount; i++) {
                long tm = value.getHistoryTime(i);
                float v = value.getHistoryValue(i);
                
                int x = (int) (size.width * 1.0 * (tm - first) / (last - first));
                int y = size.height - (int) ((size.height-1) * 1.0 * (v - low) / (high - low)) - 1;
                
                if (null != last_x) {
                    if (last_x <= x && (x >= 0 || last_x >= 0)) {
                        g.drawLine(last_x, last_y, x, y);
                    }
                }

                last_x = x;
                last_y = y;
            }



            g.setColor(c);

            g.dispose();
        }
        repaint();
    }

    private final Dimension size = new Dimension();
    private Image image;

    private static final long RECENT_HISTORY = 30000L;


    @Override
    protected void processComponentEvent(ComponentEvent e) {
        switch (e.getID()) {
        case ComponentEvent.COMPONENT_RESIZED:
        case ComponentEvent.COMPONENT_SHOWN:
            getSize(size);
             size.width = size.width;
             size.height = size.height;

            if (size.width != 0 && size.height != 0) {
                this.image = createImage(size.width, size.height);
            }
            break;
        }
        super.processComponentEvent(e);
    }

    @Override
    protected void paintComponent(Graphics g_) {
         super.paintComponent(g_);

         if(null != image) {
            g_.drawImage(image, 0, 0, size.width, size.height, this);
        }

    }
}
