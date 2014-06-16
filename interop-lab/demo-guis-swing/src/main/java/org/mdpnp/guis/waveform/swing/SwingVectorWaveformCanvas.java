package org.mdpnp.guis.waveform.swing;

import org.mdpnp.guis.waveform.WaveformPanel;

public class SwingVectorWaveformCanvas extends SwingWaveformCanvas {

    public SwingVectorWaveformCanvas(WaveformPanel component) {
        super(component);
    }
    @Override
    public void drawLine(int x0, int y0, int x1, int y1) {
        int height = extent.getMaxY() - extent.getMinY();
        currentGraphics.drawLine(x0, height - y0, x1, height - y1);
    }
    
    @Override
    public void drawString(String str, int x, int y) {
        int height = extent.getMaxY() - extent.getMinY();
        currentGraphics.drawString(str, x, height - y);
    }
}
