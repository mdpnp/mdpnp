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

import java.lang.reflect.Method;

import org.mdpnp.guis.opengl.GLRenderer;
import org.mdpnp.guis.waveform.CachingWaveformSource;
import org.mdpnp.guis.waveform.WaveformSource;
import org.mdpnp.guis.waveform.opengl.GLWaveformRenderer;
import org.mdpnp.rtbb.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;

/**
 * @author Jeff Plourde
 *
 */
public class GLWaveformView extends GLView implements WaveformRepresentation, OnScaleGestureListener {
	
	@Override
	protected GLRenderer buildRenderer() {
		return new GLWaveformRenderer();
	}
	
	@Override
	public GLWaveformRenderer getRenderer() {
		return (GLWaveformRenderer) super.getRenderer();
	}
	
	private static final GLWaveformRenderer.Color color(int color) {
		return new GLWaveformRenderer.Color(Color.red(color)/255f, Color.green(color)/255f, Color.blue(color)/255f, Color.alpha(color)/255f);
	}
	
	private static final int color(GLWaveformRenderer.Color color) {
	    return Color.argb((int)(color.alpha*255f), (int)(color.red*255f),(int)(color.green*255f), (int)(color.blue*255f));
	}
	
	@Override
	public void setBackgroundColor(int color) {
		super.setBackgroundColor(color);
		getRenderer().setBackground(color(color));
	}
		
	
	@Override
	public void setBackground(int color) {
		getRenderer().setBackground(color(color));
	}
	@Override
	public void setForeground(int color) {
		getRenderer().setForeground(color(color));
	}
	
	@Override
	public int getForeground() {
	    return color(getRenderer().getForeground());
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		scaleGestureDetector.onTouchEvent(event);
		getRenderer().rescaleValue();
		return true;
	}
	
	private final ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(getContext(), this);
	
	public GLWaveformView(Context context) {
		super(context);
		setupBackground();
	}
	
	private void setupBackground() {
	    try {
            Class<?> colorDrawable = Class.forName("android.graphics.drawable.ColorDrawable");
            Method getColor = colorDrawable.getMethod("getColor");
            Drawable background = getBackground();
            if(colorDrawable.isInstance(background)) {
                setBackground((Integer) getColor.invoke(background));
            }
        } catch (Throwable t) {
            Log.i("GLWaveformView", "No ColorDrawable.getColor available on this device");
        }
	}
	public GLWaveformView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray a = context.obtainStyledAttributes(attrs,
		        R.styleable.org_mdpnp_android_pulseox_GLWaveformView);
		     
		    final int N = a.getIndexCount();
		    for (int i = 0; i < N; ++i)
		    {
		        int attr = a.getIndex(i);
		        switch (attr) {
		        case R.styleable.org_mdpnp_android_pulseox_GLWaveformView_foreground:
		            setForeground(a.getColor(attr, Color.BLACK));
		            break;
		        
		        }
		    }
		a.recycle();
		setupBackground();
		
		
	}

	@Override
	public WaveformSource getSource() {
	    return getRenderer().getSource();
	}
	
	@Override
	public void setSource(WaveformSource source) {
		getRenderer().setSource(source);
	}
	
	public CachingWaveformSource cachingSource() {
		return getRenderer().cachingSource();
	}

	@Override
	public void pause() {
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}


	@Override
	public void resume() {
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}


	@Override
	public boolean onScale(ScaleGestureDetector arg0) {
		CachingWaveformSource cws = cachingSource();
		Log.d(GLWaveformView.class.getName(), "scaled:"+arg0.getScaleFactor());
		if(cws != null) {
			cws.setFixedTimeDomain((long)(cws.getFixedTimeDomain()/arg0.getScaleFactor()));
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

	@Override
	public void setOutOfTrack(boolean outOfTrack) {
		getRenderer().setOutOfTrack(outOfTrack);
	}
	
}
