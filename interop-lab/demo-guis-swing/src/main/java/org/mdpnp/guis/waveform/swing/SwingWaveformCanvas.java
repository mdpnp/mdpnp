package org.mdpnp.guis.waveform.swing;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Component;

import org.mdpnp.guis.waveform.WaveformCanvas;
import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformRenderer;

public abstract class SwingWaveformCanvas implements WaveformCanvas {
    protected final WaveformPanel component;
    protected final ExtentImpl extent = new ExtentImpl();
    private long timeDomain = 10000L;
    
    protected Graphics currentGraphics;
    
    @SuppressWarnings("serial")
    protected static class ExtentImpl extends Dimension implements Extent {

        @Override
        public double getMinX() {
            return 0;
        }

        @Override
        public double getMaxX() {
            return width;
        }

        @Override
        public double getMinY() {
            return 0;
        }

        @Override
        public double getMaxY() {
            return height;
        }
    }

    public SwingWaveformCanvas(WaveformPanel component) {
        this.component = component;
    }

    @Override
    public void clearRect(double x, double y, double width, double height) {
        currentGraphics.setColor(((Component)component).getBackground());
        // TODO Reorient?
        currentGraphics.fillRect((int)x, (int)y, (int)width, (int)height);
    }

    @Override
    public Extent getExtent() {
        return extent;
    }

    private final BasicStroke stroke = new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
    public void run(WaveformRenderer renderer, Graphics graphics) {
        currentGraphics = graphics;
        if(currentGraphics != null) {
            if(currentGraphics instanceof Graphics2D) {
                ((Graphics2D) currentGraphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ((Graphics2D) currentGraphics).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                ((Graphics2D) currentGraphics).setStroke(stroke);
            }
    
            long now = System.currentTimeMillis();
            ((Component)component).getSize(extent);
            currentGraphics.setColor(((Component)component).getBackground());
            currentGraphics.fillRect((int)extent.getMinX(), (int)extent.getMinY(), (int)extent.getMaxX(), (int)extent.getMaxY());
            currentGraphics.setColor(((Component)component).getForeground());
            renderer.render(component.getSource(), this, now - 2000L - timeDomain, now - 2000L);
            currentGraphics.dispose();
            currentGraphics = null;
        }
    }

    public long getTimeDomain() {
        return timeDomain;
    }

    public void setTimeDomain(long timeDomain) {
        this.timeDomain = timeDomain;
    }

}
