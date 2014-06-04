package org.mdpnp.guis.waveform.swing;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

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
        public int getMinX() {
            return 0;
        }

        @Override
        public int getMaxX() {
            return width;
        }

        @Override
        public int getMinY() {
            return 0;
        }

        @Override
        public int getMaxY() {
            return height;
        }
    }

    public SwingWaveformCanvas(WaveformPanel component) {
        this.component = component;
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
        currentGraphics.setColor(component.asComponent().getBackground());
        currentGraphics.fillRect(x, y, width, height);
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
            component.asComponent().getSize(extent);
            currentGraphics.setColor(component.asComponent().getBackground());
            currentGraphics.fillRect(extent.getMinX(), extent.getMinY(), extent.getMaxX(), extent.getMaxY());
            currentGraphics.setColor(component.asComponent().getForeground());
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
