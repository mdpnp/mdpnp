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
