package org.mdpnp.guis.opengl;

import java.nio.Buffer;

public interface OpenGL {
	
	int GL_COLOR_BUFFER_BIT = 0x4000;
	int GL_PROJECTION = 0x1701;
	int GL_LINE_SMOOTH = 0xB20;
	int GL_LINE_SMOOTH_HINT = 0xC52;
	int GL_NICEST = 0x1102;
	int GL_VERTEX_ARRAY = 0x8074;
	int GL_FLOAT = 0x1406;
	int GL_LINE_LOOP = 0x0002;
	int GL_LINE_STRIP = 0x0003;
	int GL_MODELVIEW = 0x1700;
	
	void glClearColor(float red, float green, float blue, float alpha);
	void glColor4f(float red, float green, float blue, float alpha);
	void glClear(int mask);
	void glMatrixMode(int mode);
	void glLoadIdentity();
	void glEnable(int cap);
	void glHint(int target, int mode);
	void glLineWidth(float width);
	void glEnableClientState(int array);
	void glVertexPointer(int size, int type, int stride, Buffer pointer);
	void glDrawArrays(int mode, int first, int count);
	void glDisableClientState(int array);
	void glPushMatrix();
	void glPopMatrix();
	void glScalef(float x, float y, float z);
	void glTranslatef(float x, float y, float z);
	void glOrthof(float left, float right, float bottom, float top, float zNear, float zFar);
	void glViewport(int x, int y, int width, int height);
}
