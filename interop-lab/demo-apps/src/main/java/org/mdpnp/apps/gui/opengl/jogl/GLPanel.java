package org.mdpnp.apps.gui.opengl.jogl;

import javax.media.opengl.awt.GLCanvas;

import org.mdpnp.apps.gui.opengl.GLRenderer;

@SuppressWarnings("serial")
public class GLPanel extends GLCanvas {
	private final JOGLGLRendererAdapter adapter;
	
	public GLPanel(GLRenderer renderer) {
		super();
		adapter  = new JOGLGLRendererAdapter(renderer);
		addGLEventListener(adapter);
	}
	
}
