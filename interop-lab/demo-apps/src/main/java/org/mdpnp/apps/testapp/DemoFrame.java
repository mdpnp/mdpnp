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
public class DemoFrame extends JFrame {
	public DemoFrame() {
		this("");
	}
	
	private class PostProcessKey implements KeyEventPostProcessor {

		@Override
		public boolean postProcessKeyEvent(KeyEvent e) {
			switch(e.getID()) {
			case KeyEvent.KEY_PRESSED:
				switch(e.getKeyCode()) {
				case KeyEvent.VK_F:
					if(e.isControlDown()) {
						requestToggleFullScreen();
						return true;
					}
					break;
				case KeyEvent.VK_Q:
					if(e.isControlDown()) {
						dispatchEvent(new WindowEvent(DemoFrame.this, WindowEvent.WINDOW_CLOSING));
						return true;
					}
					break;
				case KeyEvent.VK_W:
				    if(e.isMetaDown() && DemoFrame.this.isFocused()) {
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
		setWindowCanFullScreen(true);
		setQuitStrategy("CLOSE_ALL_WINDOWS");
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(new PostProcessKey());
	}

	private static Class<?> applicationClass, quitStrategyClass;
	private static Object application;
	
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
	    if(null == quitStrategyClass) {
	        quitStrategyClass = Class.forName("com.apple.eawt.QuitStrategy");
	    }
	    return quitStrategyClass;
	}
	
	public static final Class<?> getApplicationClass() throws ClassNotFoundException {
	    if(null == applicationClass) {
	        applicationClass = Class.forName("com.apple.eawt.Application");
	    }
	    return applicationClass;
	}
	public static final Object getApplication() throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	    if(null == application) {
	        Class<?> app = getApplicationClass();
	        Method method = app.getMethod("getApplication");
	        application = method.invoke(app);
	    }
	    return application;
	}
	
	public final boolean requestToggleFullScreen() {
		if(apple) {
			try {
		        Method method = getApplicationClass().getMethod("requestToggleFullScreen", Window.class);
		        method.invoke(getApplication(), this);
		        repaint();
		        return true;
			} catch (ClassNotFoundException e1) {
	//			e1.printStackTrace();
			} catch (Exception e1) {
	//			e1.printStackTrace();
			}
			return false;
		} else {
//			if(isUndecorated()) {
//				try {
//					Runnable r = new Runnable() {
//	
//						@Override
//						public void run() {
//							setVisible(false);
//							dispose();
//							setSize(800, 600);
//							setUndecorated(false);
//							setVisible(true);
//						}
//						
//					};
//					if(SwingUtilities.isEventDispatchThread()) {
//						r.run();
//					} else {
//						SwingUtilities.invokeAndWait(r);
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			} else {
//				try {
//					Runnable r = new Runnable() {
//	
//						@Override
//						public void run() {
//							setVisible(false);
//							dispose();
////							Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
//							setSize(1280, 700);
//							
//							setUndecorated(true);
//							setVisible(true);
//						}
//						
//					};
//					if(SwingUtilities.isEventDispatchThread()) {
//						r.run();
//					} else {
//						SwingUtilities.invokeAndWait(r);
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				
//			}
			final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			if(null == gd.getFullScreenWindow()) {
				gd.setFullScreenWindow(this);
			} else {
				gd.setFullScreenWindow(null);
			}
			repaint();
			return true;
		}
	}
	
	public final boolean setWindowCanFullScreen(boolean b) {
		Class<?> util;
		try {
			util = Class.forName("com.apple.eawt.FullScreenUtilities");
	        Class<?> params[] = new Class[]{Window.class, Boolean.TYPE};
	        Method method = util.getMethod("setWindowCanFullScreen", params);
	        method.invoke(util, this, b);
	        return true;
		} catch (Exception e) {
		}
		return false;
	}
}
