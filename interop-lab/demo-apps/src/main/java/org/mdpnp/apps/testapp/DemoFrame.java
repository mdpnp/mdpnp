package org.mdpnp.apps.testapp;

import java.awt.CardLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;

import javax.swing.JFrame;

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
		setWindowCanFullScreen(true);
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(new PostProcessKey());
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
			Class<?> app = Class.forName("com.apple.eawt.Application");
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
	
	public static void main(String[] args) {
		JFrame frame = new DemoFrame("TEST");
		DemoPanel panel = new DemoPanel();
		frame.getContentPane().add(panel);
		
		CardLayout ol = new CardLayout();
		panel.getContent().setLayout(ol);
		
		final MainMenuPanel mainMenuPanel = new MainMenuPanel();
		panel.getContent().add(mainMenuPanel, "main");
		ol.show(panel.getContent(), "main");
		
		final DevicePanel devicePanel = new DevicePanel();
		panel.getContent().add(devicePanel, "devicepanel");
		
//		final PCAPanel pcaPanel = new PCAPanel(null, null);
//		panel.getContent().add(pcaPanel, "pca");
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(640,480);
//		frame.getContentPane().setLayout(new BorderLayout());
//		frame.getContentPane().add(new DemoPanel(), BorderLayout.CENTER);
		frame.setVisible(true);
	}
	
}
