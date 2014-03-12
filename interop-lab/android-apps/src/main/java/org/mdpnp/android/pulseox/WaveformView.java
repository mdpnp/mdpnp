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
package org.mdpnp.android.pulseox;

import org.mdpnp.guis.waveform.CachingWaveformSource;
import org.mdpnp.guis.waveform.EvenTempoWaveformSource;
import org.mdpnp.guis.waveform.WaveformCanvas;
import org.mdpnp.guis.waveform.WaveformRenderer;
import org.mdpnp.guis.waveform.WaveformSource;
import org.mdpnp.guis.waveform.WaveformSourceListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;

/**
 * @author Jeff Plourde
 *
 */
public class WaveformView extends android.view.View implements WaveformSourceListener, WaveformCanvas,
        WaveformCanvas.Extent, WaveformRepresentation, OnScaleGestureListener {

    private WaveformRenderer renderer;
    private WaveformSource source;

    private Paint backgroundPaint = new Paint();
    private Paint foregroundPaint = new Paint();
    private Paint secondaryPaint = new Paint();

    @Override
    public void setBackground(int color) {
        backgroundPaint.setColor(color);
    }

    @Override
    public void setForeground(int color) {
        foregroundPaint.setColor(color);
    }

    @Override
    public int getForeground() {
        return foregroundPaint.getColor();
    }

    public Paint getBackgroundPaint() {
        return backgroundPaint;
    }

    public Paint getForegroundPaint() {
        return foregroundPaint;
    }

    public void setForegroundPaint(Paint foregroundPaint) {
        this.foregroundPaint = foregroundPaint;
    }

    public void setBackgroundPaint(Paint backgroundPaint) {
        this.backgroundPaint = backgroundPaint;
    }

    private void init() {
        setDrawingCacheBackgroundColor(Color.BLACK);
        backgroundPaint.setColor(Color.BLACK);
        backgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        foregroundPaint.setColor(Color.GREEN);
        foregroundPaint.setAntiAlias(true);
        foregroundPaint.setStrokeWidth(1.5f);
        foregroundPaint.setStrokeCap(Cap.ROUND);
        foregroundPaint.setStrokeJoin(Join.ROUND);
        secondaryPaint.setColor(Color.RED);
        setDrawingCacheEnabled(true);

    }

    private final CanvasAdapter canvasAdapter = USE_A_CANVAS ? new CanvasAdapter() : null;

    private final class CanvasAdapter extends Canvas implements WaveformCanvas, WaveformCanvas.Extent {

        @Override
        public void drawLine(int x0, int y0, int x1, int y1) {
            // Log.d(WaveformView.class.getName(), ""+x0+","+y0+","+x1+","+y1);
            super.drawLine(x0, y0, x1, y1, foregroundPaint);
        }

        @Override
        public void drawSecondaryLine(int x0, int y0, int x1, int y1) {

            super.drawLine(x0, y0, x1, y1, secondaryPaint);
        }

        @Override
        public void clearRect(int x, int y, int width, int height) {
            drawRect(x, y, x + width, y + height, backgroundPaint);
        }

        @Override
        public Extent getExtent() {
            return this;
        }

        @Override
        public int getMinX() {
            return 0;
        }

        @Override
        public int getMaxX() {
            return getWidth();
        }

        @Override
        public int getMinY() {

            return getHeight();

        }

        @Override
        public int getMaxY() {
            return 0;
        }
        // @Override
        // public void clearAll() {
        // drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);
        // }

    }

    public WaveformView(Context context) {
        super(context);
        init();
    }

    public WaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveformView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    // private int drawingBitmap = 0;
    // private Bitmap[] bitmap = new Bitmap[2];
    private Bitmap drawingBitmap;

    // private Bitmap renderingBitmap;
    // private IntBuffer xferBuffer;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        w = Math.max(w, 10);
        h = Math.max(h, 10);
        drawingBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        if (USE_A_CANVAS) {
            canvasAdapter.setBitmap(drawingBitmap);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawingBitmap != null) {
            canvas.drawBitmap(drawingBitmap, 0, 0, null);
        }
    }

    private final WaveformRenderer.Rect rect = new WaveformRenderer.Rect();

    private static final boolean USE_A_CANVAS = true;

    @Override
    public void waveform(WaveformSource source) {
        if (drawingBitmap != null) {
            if (USE_A_CANVAS) {
                renderer.render(canvasAdapter, rect);
            } else {
                renderer.render(this, rect);
            }
            postInvalidate();
        }

    }

    public WaveformSource getSource() {
        return source;
    }

    public void setSource(WaveformSource source) {
        if (null != this.source) {
            this.source.removeListener(this);
        }
        this.source = null == source ? null : new EvenTempoWaveformSource(new CachingWaveformSource(source, 5000L));

        if (null != this.source) {
            this.source.addListener(this);
            this.renderer = new WaveformRenderer(this.source);
            // this.renderer.addOtherSource(255, 0, 0, 255, dct_source);
        } else {
            this.renderer = null;
            // this.dct_source = null;
        }
    }

    @Override
    public void drawLine(int x0, int y0, int x1, int y1) {
        drawLine(getForegroundPaint().getColor(), x0, y0, x1, y1);
    }

    public void drawLine(int color, int x0, int y0, int x1, int y1) {
        // simple interpolation .. just to see if this works
        int spanx = x1 - x0;
        int spany = y1 - y0;
        for (int x = x0; x <= x1; x++) {
            int y = (int) (1.0f * (x - x0) / spanx * spany + y0);
            // Log.d(WaveformView.class.getName(),
            // "x="+x+",y="+y+",width="+drawingBitmap.getWidth()+",height="+drawingBitmap.getHeight()+",x0="+x0+",x1="+x1+",y0="+y0+",y1="+y1);
            drawingBitmap.setPixel(x, y, color);
        }
    }

    @Override
    public void drawSecondaryLine(int x0, int y0, int x1, int y1) {
        drawLine(secondaryPaint.getColor(), x0, y0, x1, y1);
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
        int color = backgroundPaint.getColor();
        for (int i = x; i < (x + width); i++) {
            for (int j = y; j < (y + height); j++) {
                drawingBitmap.setPixel(i, j, color);
            }
        }
    }

    @Override
    public Extent getExtent() {
        return this;
    }

    @Override
    public int getMinX() {
        return 0;
    }

    @Override
    public int getMaxX() {

        return drawingBitmap.getWidth();
    }

    @Override
    public int getMinY() {
        return drawingBitmap.getHeight();
    }

    @Override
    public int getMaxY() {
        return 0;
    }

    @Override
    public void reset(WaveformSource source) {
        if (drawingBitmap != null) {
            if (USE_A_CANVAS) {
                renderer.render(canvasAdapter, rect);
            } else {
                renderer.render(this, rect);
            }

            postInvalidate();
        }
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setOutOfTrack(boolean outOfTrack) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onScale(ScaleGestureDetector arg0) {
        CachingWaveformSource cws = renderer.cachingSource();
        Log.d(GLWaveformView.class.getName(), "scaled:" + arg0.getScaleFactor());
        if (cws != null) {
            cws.setFixedTimeDomain((long) (cws.getFixedTimeDomain() / arg0.getScaleFactor()));
        }
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector arg0) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector arg0) {

    }

    private final ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(getContext(), this);

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        if (renderer != null) {
            renderer.rescaleValue();
        }
        return true;
    }

    // @Override
    // public void clearAll() {
    // bitmap[drawingBitmap].eraseColor(backgroundPaint.getColor());
    // }

}
