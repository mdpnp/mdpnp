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
package org.mdpnp.apps.testapp.vital;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;

import javax.swing.JComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class JValueChart extends JComponent {

    public JValueChart(final Value value) {
        enableEvents(ComponentEvent.COMPONENT_EVENT_MASK);
    }

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(JValueChart.class);

    public void setValue(Value value) {
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
                int y = size.height - (int) ((size.height - 1) * 1.0 * (v - low) / (high - low)) - 1;

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

        if (null != image) {
            g_.drawImage(image, 0, 0, size.width, size.height, this);
        }

    }
}
