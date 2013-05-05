package org.mdpnp.guis.opengl.jogl;

import javax.media.opengl.awt.GLCanvas;

import org.mdpnp.guis.opengl.GLRenderer;

@SuppressWarnings("serial")
public class GLPanel extends GLCanvas {
	private final JOGLGLRendererAdapter adapter;
	
	public GLPanel(GLRenderer renderer) {
		super();
		adapter  = new JOGLGLRendererAdapter(renderer);
		addGLEventListener(adapter);
	}
	
}
