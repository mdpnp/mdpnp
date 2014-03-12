package org.mdpnp.gip.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Plourde
 *
 */
public class GenericInfusionPumpApp {
	public final static Color darkBlue = new Color(51, 0, 101);
	public final static Color lightBlue = new Color(1, 153, 203);
	
//	private static final boolean MDCF = true;
	private static final Logger log = LoggerFactory.getLogger(GenericInfusionPumpApp.class);
	public static void main(String[] args) throws IOException {
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}

		Font f = new Font(Font.SANS_SERIF, Font.PLAIN, 18);

		// TODO most of this can be represented in xml
		UIManager.put("Label.font", f);
		UIManager.put("Label[Enabled].font", f);
		UIManager.put("ComboBox.font", f);
		UIManager.put("ComboBox[Enabled].font", f);
		UIManager.put("List.font", f);
		UIManager.put("List[Enabled].font", f);
		UIManager.put("ComboBox[Focused].font", f);
//		UIManager.put("Panel.font", f);
		UIManager.put("Button.font", f);
		UIManager.put("TextField.font", f);
		UIManager.put("FormattedTextField.font", f);
		UIManager.put("TitledBorder.font", f);
		UIManager.put("TabbedPane[Enabled].font", f);
//		
		UIManager.put("Label.textForeground", darkBlue);
		UIManager.put("Label[Enabled].textForeground", darkBlue);
		UIManager.put("ComboBox.textForeground", darkBlue);
		UIManager.put("ComboBox[Enabled].textForeground", darkBlue);
		UIManager.put("SliderThumb.foreground", darkBlue);
		UIManager.put("ComboBox[Focused].textForeground", darkBlue);
		UIManager.put("ComboBox.foreground", darkBlue);
		UIManager.put("ComboBox[Enabled].foreground", darkBlue);
		UIManager.put("ComboBox[Focused].foreground", darkBlue);
		UIManager.put("List.foreground", darkBlue);
		UIManager.put("List[Enabled].foreground", darkBlue);
		UIManager.put("List[Enabled].textForeground", darkBlue);
		UIManager.put("List[Enabled].background", Color.white);
		UIManager.put("Panel.foreground", darkBlue);
		UIManager.put("Panel[Enabled].foreground", darkBlue);
		UIManager.put("Button.foreground", darkBlue);
		UIManager.put("Button[Enabled].foreground", darkBlue);
		UIManager.put("Button.textForeground", darkBlue);
		UIManager.put("Button[Enabled].textForeground", darkBlue);
		
		UIManager.put("ComboBox[Enabled].background", Color.white);
		UIManager.put("TabbedPane:TabbedPaneTab[Enabled].textForeground", darkBlue);
		// JRE 1.7 feature
//		Painter noPainter = new Painter() {
//			@Override
//			public void paint(Graphics2D g, Object object, int width, int height) {
//				
//			}
//		};

//		UIManager.put("TabbedPane:TabbedPaneTabArea[Enabled].backgroundPainter", noPainter);
//		UIManager.put("TabbedPane:TabbedPaneTabArea[Enabled+MouseOver].backgroundPainter", noPainter);
//		UIManager.put("TabbedPane:TabbedPaneTabArea[Enabled+Pressed].backgroundPainter", noPainter);
//		UIManager.put("TabbedPane:TabbedPaneTabArea[Disabled].backgroundPainter", noPainter);

		UIManager.put("TabbedPane:TabbedPaneTab[Enabled].contentMargins", new Insets(0,5,0,5));
		
		UIManager.put("TabbedPane.background", Color.white);
		
//		UIManager.put("Panel.foreground", darkBlue);
		UIManager.put("TextField.textForeground", darkBlue);
		UIManager.put("TextField[Enabled].textForeground", darkBlue);
		UIManager.put("TextField.foreground", darkBlue);
		UIManager.put("TextField[Enabled].foreground", darkBlue);
//		UIManager.put("TextField.background", new Color(185, 223, 246));
		UIManager.put("FormattedTextField.textForeground", darkBlue);
		UIManager.put("FormattedTextField[Enabled].textForeground", darkBlue);
		UIManager.put("FormattedTextField.foreground", darkBlue);
		UIManager.put("FormattedTextField[Enabled].foreground", darkBlue);
//		UIManager.put("FormattedTextField.background", new Color(185, 223, 246));
//		UIManager.put("ComboBox.background", new Color(185, 223, 246));
		UIManager.put("TitledBorder.titleColor", darkBlue);
//		UIManaged
		UIManager.put("Panel.background", new Color(185, 223, 246));
		UIManager.put("Panel[Enabled].background", new Color(185, 223, 246));
		UIManager.put("TabbedPane.contentOpaque", false);
		
		
		final InfusionPumpModel infusionPumpModel = new InfusionPumpModelImpl();
/**
 * Note from jplourde on Apr 9, 2013
 * MDCF components were enabled for the HIMSS 2013 demo
 * but they were built on a pre-release build of MDCF		
 */
//		if(MDCF) {
//			final InfusionPumpModelMDCFAdapter mdcfAdapter = new InfusionPumpModelMDCFAdapter(infusionPumpModel); 
//			mdcfAdapter.connectToNetworkController();
//			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
//
//				@Override
//				public void run() {
//					mdcfAdapter.disconnectFromNetworkController();
//				}
//				
//			}));
//		}
		
		final DemoFrame frame = new DemoFrame("Infusion Pump (generic)");
		

		frame.setSize(600, 800);

		final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
		GIPPanel gipp = new GIPPanel(infusionPumpModel, timer);
		
		gipp.getHeaderPanel().addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				frame.requestToggleFullScreen();
				super.mouseClicked(e);
			}
		});
		frame.getContentPane().add(gipp);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				timer.shutdown();
				try {
					timer.awaitTermination(5, TimeUnit.SECONDS);
					log.info("TIMER IS SHUTDOWN");
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}));
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				super.windowClosing(e);
			}
		});
		frame.setVisible(true);
		// This repaint is XP specific (with intel gfx)
		frame.repaint();
	}
}
