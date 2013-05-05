package org.mdpnp.android.pulseox;

import org.mdpnp.guis.opengl.GLRenderer;
import org.mdpnp.guis.waveform.CachingWaveformSource;
import org.mdpnp.guis.waveform.WaveformSource;
import org.mdpnp.guis.waveform.opengl.GLWaveformRenderer;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;

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
	
	@Override
	public void setBackground(int color) {
		getRenderer().setBackground(color(color));
	}
		
	@Override
	public void setForeground(int color) {
		getRenderer().setForeground(color(color));
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
	}
	
	public GLWaveformView(Context context, AttributeSet attrs) {
		super(context, attrs);
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
