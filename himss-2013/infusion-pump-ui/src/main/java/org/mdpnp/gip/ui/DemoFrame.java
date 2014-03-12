package org.mdpnp.gip.ui;

import java.awt.AWTEvent;
import java.awt.Frame;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

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
				}
				break;
			}
			return false;
		}
		
	}
	
	private boolean apple;
	
	public DemoFrame(String title) {
		super(title);
		enableEvents(AWTEvent.WINDOW_STATE_EVENT_MASK);
		
		setWindowCanFullScreen(true);
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(new PostProcessKey());
	}

	@Override
	protected void processWindowStateEvent(WindowEvent e) {
		if(e.getNewState() != e.getOldState()) {
			switch(e.getNewState()) {
			case Frame.MAXIMIZED_BOTH:
				setVisible(false);
				dispose();
				setUndecorated(true);
				setVisible(true);
				repaint();
				break;
			case Frame.NORMAL:
				setVisible(false);
				dispose();
				setUndecorated(false);
				setVisible(true);
				repaint();
				break;
			}
		}
		
		super.processWindowStateEvent(e);
	}
	
	public final boolean requestToggleFullScreen() {
		if(apple) {
			Class<?> app;
			try {
				app = Class.forName("com.apple.eawt.Application");
				Method method = app.getMethod("getApplication");
		        Object a = method.invoke(app);
		        method = app.getMethod("requestToggleFullScreen", Window.class);
		        method.invoke(a, this);
		        repaint();
		        return true;
			} catch (ClassNotFoundException e1) {
	//			e1.printStackTrace();
			} catch (Exception e1) {
	//			e1.printStackTrace();
			}
			return false;
		} else {
//			Runnable r = new Runnable() {
//
//				@Override
//				public void run() {
//					if(isUndecorated()) {
//						setVisible(false);
//						dispose();
//						setSize(600, 800);
////						setUndecorated(false);
//						setVisible(true);
//					} else {
//						setVisible(false);
//						dispose();
//						Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
//						setSize(dim);
//						setLocation(0,0);
////						setUndecorated(true);
//						setVisible(true);
//					}
//				}
//				
//			};

			
//			final Dimension size = new Dimension();
//			Runnable r = new Runnable() {
//				public void run() {
//					GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
//					final GraphicsDevice[] gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
//
//					if(null != screenDevice) {
//						
////						gd[screenDevice].setFullScreenWindow(null);
//						screenDevice++;
//						if(screenDevice >= gd.length) {
//							screenDevice = null;
//						}
//					} else {
//						DemoFrame.this.getSize(size);
//						screenDevice = 0;
//					}
//					DemoFrame.this.setVisible(false);
//					DemoFrame.this.dispose();
//					if(null != screenDevice) {
//						
////						DemoFrame.this.setSize(gd[screenDevice].getDisplayMode().getWidth(), gd[screenDevice].getDisplayMode().getHeight());
//						DemoFrame.this.setUndecorated(true);
//						DemoFrame.this.setExtendedState(Frame.MAXIMIZED_BOTH | DemoFrame.this.getExtendedState());
////						gd[screenDevice].setFullScreenWindow(DemoFrame.this);
			
//					} else {
//						DemoFrame.this.setExtendedState(Frame.NORMAL);
//						DemoFrame.this.setSize(size);
//						DemoFrame.this.setUndecorated(false);
//					}
//					DemoFrame.this.setVisible(true);
////					DemoFrame.this.requestFocus();
//					repaint();
//				}
//			};
			Runnable r = new Runnable() {
				public void run() {
					switch(getExtendedState()) {
					case Frame.NORMAL:
						setExtendedState(Frame.MAXIMIZED_BOTH);
						break;
					case Frame.MAXIMIZED_BOTH:
						setExtendedState(Frame.NORMAL);
						break;
					}
				}
			};
			if(SwingUtilities.isEventDispatchThread()) {
				r.run();
			} else {
				try {
					SwingUtilities.invokeAndWait(r);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			return true;
		}
	}
	
//	private Integer screenDevice;
	
	public final boolean setWindowCanFullScreen(boolean b) {
		Class<?> util;
		try {
			util = Class.forName("com.apple.eawt.FullScreenUtilities");
//			Class<?> app = Class.forName("com.apple.eawt.Application");
	        Class<?> params[] = new Class[]{Window.class, Boolean.TYPE};
	        Method method = util.getMethod("setWindowCanFullScreen", params);
	        method.invoke(util, this, b);
	        // APPLE WHY DO YOU HATE ME?
	        // Turns out I wasn't enclosing my multiline JTextArea in a JScrollPane
	        apple = true;
	        return true;
		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
		} catch (Exception e) {
//			e.printStackTrace();
		}
		apple = false;
        return false;
	}

	
}
