package org.mdpnp.guis.waveform.javafx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

import org.mdpnp.guis.javafx.ResizableCanvas;
import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformRenderer;
import org.mdpnp.guis.waveform.WaveformSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaFXWaveformPane extends BorderPane implements WaveformPanel {
//    private static final double SPACING_X = 25;
//    private static final double SPACING_Y = 20;
//    private static final double RADIUS = 1.5;
    protected final ResizableCanvas canvas;
    private final WaveformRenderer renderer = new WaveformRenderer();
    private WaveformSource source; 
    private final JavaFXWaveformCanvas waveformCanvas;
    private Timeline waveformRender;
 
    public ResizableCanvas getCanvas() {
        return canvas;
    }
    
    public JavaFXWaveformPane() {
        super(new ResizableCanvas());
        canvas = (ResizableCanvas) getCenter();
        waveformCanvas = new JavaFXWaveformCanvas(this);

        setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                System.err.println("RESCALE");
                renderer.rescaleValue();
            }
            
        });
    }
 
    @Override protected void layoutChildren() {
        final int top = (int)snappedTopInset();
        final int right = (int)snappedRightInset();
        final int bottom = (int)snappedBottomInset();
        final int left = (int)snappedLeftInset();
        final int w = (int)getWidth() - left - right;
        final int h = (int)getHeight() - top - bottom;
        canvas.setLayoutX(left);
        canvas.setLayoutY(top);
        if (w != canvas.getWidth() || h != canvas.getHeight()) {
            canvas.setWidth(w);
            canvas.setHeight(h);
//            GraphicsContext g = canvas.getGraphicsContext2D();
//            g.clearRect(0, 0, w, h);
//            g.setFill(Color.gray(0,0.2));
// 
//            for (int x = 0; x < w; x += SPACING_X) {
//                for (int y = 0; y < h; y += SPACING_Y) {
//                    double offsetY = (y%(2*SPACING_Y)) == 0 ? SPACING_X /2 : 0;
//                    g.fillOval(x-RADIUS+offsetY,y-RADIUS,RADIUS+RADIUS,RADIUS+RADIUS);
//                }
//            }
        }
    }

    @Override
    public void setSource(WaveformSource source) {
        this.source = source;
    }

    @Override
    public WaveformSource getSource() {
        return source;
    }

    @Override
    public void setOutOfTrack(boolean outOfTrack) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void start() {
        if(waveformRender==null) {
            waveformRender = new Timeline(new KeyFrame(Duration.millis(100), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    long tm = System.currentTimeMillis();
                    if(null != source) {
                        renderer.render(source, waveformCanvas, tm-12000L, tm-2000L);
                    }
                }
            }));
            waveformRender.setCycleCount(Timeline.INDEFINITE);
            waveformRender.play();
        }
    }
    private static final Logger log = LoggerFactory.getLogger(JavaFXWaveformPane.class);
    @Override
    public void stop() {
        setSource(null);
        if(null != waveformRender) {
            waveformRender.stop();
            try {
                renderer.awaitLastRender(2000L);
            } catch (InterruptedException e) {
                log.error("Interrupted waiting for render to end", e);
            }
            waveformRender = null;
        }
    }
}
