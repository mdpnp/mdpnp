package org.mdpnp.android.pulseox;

import java.nio.Buffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.mdpnp.guis.opengl.GLRenderer;
import org.mdpnp.guis.opengl.OpenGL;

import android.opengl.GLES10;
import android.opengl.GLSurfaceView;

public class AndroidGLRendererAdapter implements GLSurfaceView.Renderer, OpenGL {

	private final GLRenderer renderer;
	private int width, height;
	
	
	public AndroidGLRendererAdapter(GLRenderer renderer) {
		this.renderer = renderer;
	}
	
	@Override
	public void onDrawFrame(GL10 arg0) {
		renderer.render(this, width, height);
	}

	@Override
	public void onSurfaceChanged(GL10 arg0, int width, int height) {
		this.width = width;
		this.height = height;
		renderer.init(this, width, height);
	}

	@Override
	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
		
	}

	@Override
	public void glClearColor(float red, float green, float blue, float alpha) {
		GLES10.glClearColor(red, green, blue, alpha);
	}

	@Override
	public void glColor4f(float red, float green, float blue, float alpha) {
		GLES10.glColor4f(red, green, blue, alpha);
	}

	@Override
	public void glClear(int mask) {
		GLES10.glClear(mask);
	}

	@Override
	public void glMatrixMode(int mode) {
		GLES10.glMatrixMode(mode);
	}

	@Override
	public void glLoadIdentity() {
		GLES10.glLoadIdentity();
	}

	@Override
	public void glEnable(int cap) {
		GLES10.glEnable(cap);
	}

	@Override
	public void glHint(int target, int mode) {
		GLES10.glHint(target, mode);
	}

	@Override
	public void glLineWidth(float width) {
		GLES10.glLineWidth(width);
	}

	@Override
	public void glEnableClientState(int array) {
		GLES10.glEnableClientState(array);
	}

	@Override
	public void glVertexPointer(int size, int type, int stride, Buffer pointer) {
		GLES10.glVertexPointer(size, type, stride, pointer);
	}

	@Override
	public void glDrawArrays(int mode, int first, int count) {
		GLES10.glDrawArrays(mode, first, count);
	}

	@Override
	public void glDisableClientState(int array) {
		GLES10.glDisableClientState(array);
	}

	@Override
	public void glPushMatrix() {
		GLES10.glPushMatrix();
	}

	@Override
	public void glPopMatrix() {
		GLES10.glPopMatrix();
	}

	@Override
	public void glScalef(float x, float y, float z) {
		GLES10.glScalef(x, y, z);
	}

	@Override
	public void glTranslatef(float x, float y, float z) {
		GLES10.glTranslatef(x, y, z);
	}

	@Override
	public void glOrthof(float left, float right, float bottom, float top,
			float zNear, float zFar) {
		GLES10.glOrthof(left, right, bottom, top, zNear, zFar);
	}

	@Override
	public void glViewport(int x, int y, int width, int height) {
		GLES10.glViewport(x, y, width, height);
	}

}
