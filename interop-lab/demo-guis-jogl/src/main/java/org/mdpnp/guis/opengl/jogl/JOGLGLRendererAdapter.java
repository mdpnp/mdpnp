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
package org.mdpnp.guis.opengl.jogl;

import java.nio.Buffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.mdpnp.guis.opengl.GLRenderer;
import org.mdpnp.guis.opengl.OpenGL;

/**
 * @author Jeff Plourde
 *
 */
public class JOGLGLRendererAdapter implements GLEventListener, OpenGL {

    @Override
    public void glClearColor(float red, float green, float blue, float alpha) {
        gl.glClearColor(red, green, blue, alpha);
    }

    @Override
    public void glColor4f(float red, float green, float blue, float alpha) {
        gl.getGL2().glColor4f(red, green, blue, alpha);
    }

    @Override
    public void glClear(int mask) {
        gl.glClear(mask);
    }

    @Override
    public void glMatrixMode(int mode) {
        gl.getGL2().glMatrixMode(mode);
    }

    @Override
    public void glLoadIdentity() {
        gl.getGL2().glLoadIdentity();
    }

    @Override
    public void glEnable(int cap) {
        gl.glEnable(cap);
    }

    @Override
    public void glHint(int target, int mode) {
        gl.glHint(target, mode);
    }

    @Override
    public void glLineWidth(float width) {
        gl.getGL2().glLineWidth(width);
    }

    @Override
    public void glEnableClientState(int array) {
        gl.getGL2().glEnableClientState(array);
    }

    @Override
    public void glVertexPointer(int size, int type, int stride, Buffer pointer) {
        gl.getGL2().glVertexPointer(size, type, stride, pointer);
    }

    @Override
    public void glDrawArrays(int mode, int first, int count) {
        gl.getGL2().glDrawArrays(mode, first, count);
    }

    @Override
    public void glDisableClientState(int array) {
        gl.getGL2().glDisableClientState(array);
    }

    @Override
    public void glPushMatrix() {
        gl.getGL2().glPushMatrix();
    }

    @Override
    public void glPopMatrix() {
        gl.getGL2().glPopMatrix();
    }

    @Override
    public void glScalef(float x, float y, float z) {
        gl.getGL2().glScalef(x, y, z);
    }

    @Override
    public void glTranslatef(float x, float y, float z) {
        gl.getGL2().glTranslatef(x, y, z);
    }

    @Override
    public void glOrthof(float left, float right, float bottom, float top, float zNear, float zFar) {
        gl.getGL2().glOrthof(left, right, bottom, top, zNear, zFar);
    }

    private volatile GL gl;
    private final GLRenderer renderer;
    private int width, height;

    public JOGLGLRendererAdapter(GLRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void display(GLAutoDrawable arg0) {
        this.gl = arg0.getGL();
        renderer.render(this, width, height);

    }

    @Override
    public void dispose(GLAutoDrawable arg0) {
        this.gl = arg0.getGL();
    }

    @Override
    public void init(GLAutoDrawable arg0) {
        this.gl = arg0.getGL();
    }

    @Override
    public void reshape(GLAutoDrawable arg0, int x, int y, int width, int height) {
        this.gl = arg0.getGL();
        this.width = width;
        this.height = height;
        renderer.init(this, width, height);
    }

    @Override
    public void glViewport(int x, int y, int width, int height) {
        gl.glViewport(x, y, width, height);
    }
}
