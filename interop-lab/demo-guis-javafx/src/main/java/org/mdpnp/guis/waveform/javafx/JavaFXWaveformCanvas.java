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
        public double getMinX() {
            return (int) getX();
        }

        @Override
        public double getMaxX() {
            return (int) (getX()+getWidth());
        }

        @Override
        public double getMinY() {
            return (int) getY();
        }

        @Override
        public double getMaxY() {
            return (int) (getY()+getHeight());
        }
    }
    

    @Override
    public void drawLine(double x0, double y0, double x1, double y1) {
        double height = extent.getMaxY() - extent.getMinY();
        currentContext.strokeLine(x0, height - y0, x1, height - y1);
    }

    @Override
    public void clearRect(double x, double y, double width, double height) {
        // TODO Reorient?
        currentContext.clearRect(x, y, width, height);
    }

    @Override
    public void drawString(String str, double x, double y) {
        double height = extent.getMaxY() - extent.getMinY();
        currentContext.strokeText(str, x, height-y);
    }

    @Override
    public Extent getExtent() {
        extent.heightProperty().set(pane.getHeight());
        extent.widthProperty().set(pane.getWidth());
        return extent;
    }
}
