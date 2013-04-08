package org.mdpnp.apps.gui.opengl;

public interface GLRenderer {
	void init(OpenGL gl, int width, int height);
	void render(OpenGL gl, int width, int height);
}
