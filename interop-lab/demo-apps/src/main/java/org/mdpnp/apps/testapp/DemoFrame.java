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
package org.mdpnp.apps.testapp;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class DemoFrame extends JFrame {
    public DemoFrame() {
        this("");
    }

    private class PostProcessKey implements KeyEventPostProcessor {

        @Override
        public boolean postProcessKeyEvent(KeyEvent e) {
            switch (e.getID()) {
            case KeyEvent.KEY_PRESSED:
                switch (e.getKeyCode()) {
                case KeyEvent.VK_F:
                    if (e.isControlDown()) {
                        requestToggleFullScreen();
                        return true;
                    }
                    break;
                case KeyEvent.VK_Q:
                    if (e.isControlDown()) {
                        dispatchEvent(new WindowEvent(DemoFrame.this, WindowEvent.WINDOW_CLOSING));
                        return true;
                    }
                    break;
                case KeyEvent.VK_W:
                    if (e.isMetaDown() && DemoFrame.this.isFocused()) {
                        dispatchEvent(new WindowEvent(DemoFrame.this, WindowEvent.WINDOW_CLOSING));
                        return true;
                    }
                    break;
                }
                break;
            }
            return false;
        }

    }

    private boolean apple;

    public DemoFrame(String title) {
        super(title);
        apple = setWindowCanFullScreen(true);
        setQuitStrategy("CLOSE_ALL_WINDOWS");
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(new PostProcessKey());
    }

    private static Class<?> applicationClass, quitStrategyClass;
    private static Object application;

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(DemoFrame.class);

    public static final boolean setQuitStrategy(final String quitStrategy) {
        try {
            Method m = getApplicationClass().getMethod("setQuitStrategy", getQuitStrategyClass());
            Object o = getQuitStrategyClass().getMethod("valueOf", String.class).invoke(null, quitStrategy);
            m.invoke(getApplication(), o);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public static final Class<?> getQuitStrategyClass() throws ClassNotFoundException {
        if (null == quitStrategyClass) {
            quitStrategyClass = Class.forName("com.apple.eawt.QuitStrategy");
        }
        return quitStrategyClass;
    }

    public static final Class<?> getApplicationClass() throws ClassNotFoundException {
        if (null == applicationClass) {
            applicationClass = Class.forName("com.apple.eawt.Application");
        }
        return applicationClass;
    }

    public static final Object getApplication() throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        if (null == application) {
            Class<?> app = getApplicationClass();
            Method method = app.getMethod("getApplication");
            application = method.invoke(app);
        }
        return application;
    }

    public final boolean requestToggleFullScreen() {
        if (apple) {
            try {
                Method method = getApplicationClass().getMethod("requestToggleFullScreen", Window.class);
                method.invoke(getApplication(), this);
                repaint();
                return true;
            } catch (ClassNotFoundException e1) {
                // e1.printStackTrace();
            } catch (Exception e1) {
                // e1.printStackTrace();
            }
            return false;
        } else {
            final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            if (null == gd.getFullScreenWindow()) {
                gd.setFullScreenWindow(this);
            } else {
                gd.setFullScreenWindow(null);
            }
            repaint();
            return true;
        }
    }

    public final boolean setWindowCanFullScreen(boolean b) {
        return setWindowCanFullScreen(b, this);
    }
    
    public final static boolean setWindowCanFullScreen(boolean b, JFrame frame) {
        Class<?> util;
        try {
            util = Class.forName("com.apple.eawt.FullScreenUtilities");
            Class<?> params[] = new Class[] { Window.class, Boolean.TYPE };
            Method method = util.getMethod("setWindowCanFullScreen", params);
            method.invoke(util, frame, b);
            return true;
        } catch (Exception e) {
        }
        return false;
    }
}
