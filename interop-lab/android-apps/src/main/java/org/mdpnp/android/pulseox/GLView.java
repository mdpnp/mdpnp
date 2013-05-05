package org.mdpnp.android.pulseox;

import org.mdpnp.guis.opengl.GLRenderer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public abstract class GLView extends GLSurfaceView {

	private final GLRenderer renderer;
	
	public GLView(Context context) {
		super(context);
		setRenderer(new AndroidGLRendererAdapter(renderer = buildRenderer()));
	}
	public GLView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setRenderer(new AndroidGLRendererAdapter(renderer = buildRenderer()));
	}
	public GLRenderer getRenderer() {
		return renderer;
	}
	protected abstract GLRenderer buildRenderer();
}
