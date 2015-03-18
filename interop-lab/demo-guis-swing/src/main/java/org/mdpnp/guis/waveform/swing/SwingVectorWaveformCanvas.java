package org.mdpnp.guis.waveform.swing;

import org.mdpnp.guis.waveform.WaveformPanel;

public class SwingVectorWaveformCanvas extends SwingWaveformCanvas {

    public SwingVectorWaveformCanvas(WaveformPanel component) {
        super(component);
    }
    @Override
    public void drawLine(double x0, double y0, double x1, double y1) {
        double height = extent.getMaxY() - extent.getMinY();
        currentGraphics.drawLine((int)x0, (int)(height - y0), (int)x1, (int)(height - y1));
    }
    
    @Override
    public void drawString(String str, double x, double y) {
        double height = extent.getMaxY() - extent.getMinY();
        currentGraphics.drawString(str, (int)x, (int)(height - y));
    }
}
