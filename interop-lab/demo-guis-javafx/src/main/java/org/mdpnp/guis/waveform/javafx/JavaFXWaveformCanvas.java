package org.mdpnp.guis.waveform.javafx;

import org.mdpnp.guis.waveform.WaveformCanvas;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class JavaFXWaveformCanvas implements WaveformCanvas {
    protected GraphicsContext currentContext;
    protected JavaFXWaveformPane pane;
    protected final ExtentImpl extent = new ExtentImpl();
    
    
    public JavaFXWaveformCanvas(JavaFXWaveformPane pane) {
        this.pane = pane;
        this.currentContext = pane.canvas.getGraphicsContext2D();
        currentContext.setStroke(Color.BLACK);
    }
    
    protected static class ExtentImpl extends Rectangle implements Extent {

        @Override
        public int getMinX() {
            return (int) getX();
        }

        @Override
        public int getMaxX() {
            return (int) (getX()+getWidth());
        }

        @Override
        public int getMinY() {
            return (int) getY();
        }

        @Override
        public int getMaxY() {
            return (int) (getY()+getHeight());
        }
    }
    

    @Override
    public void drawLine(int x0, int y0, int x1, int y1) {
        currentContext.strokeLine(x0, y0, x1, y1);
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
        currentContext.clearRect(x, y, width, height);
    }

    @Override
    public void drawString(String str, int x, int y) {
        currentContext.strokeText(str, x, y);
    }

    @Override
    public Extent getExtent() {
        extent.heightProperty().set(pane.getHeight());
        extent.widthProperty().set(pane.getWidth());
        return extent;
    }
}
