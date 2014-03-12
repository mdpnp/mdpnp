package org.mdpnp.gip.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class GIPPanel extends JPanel {
//	private int img_src_x1, img_src_y1;
	
	@Override
	protected void processComponentEvent(ComponentEvent e) {
		super.processComponentEvent(e);
		switch(e.getID()) {
		case ComponentEvent.COMPONENT_RESIZED:
//			Dimension size = getSize();
//			double img_width = ice_cubes.getImage().getWidth(this);
//			double img_height = ice_cubes.getImage().getHeight(this);
			
//			if(img_width > 0 && img_height > 0) {
//				double scr_width = size.getWidth();
//				double scr_height = size.getHeight();
				
				
//				img_src_x1 = (int)((img_width-scr_width) / 2.0);
//				img_src_x2 = (int)( img_src_x1 + scr_width );
	
//				img_src_y1 = (int)((img_height-scr_height) /2.0);
//				img_src_y2 = (int)(img_src_y1+scr_height);
//			}			
			break;
		}
		
	}

	
	public static final class HeaderPanel extends JPanel {
		private final ImageIcon mdpnp = new ImageIcon(GIPPanel.HeaderPanel.class.getResource("mdpnp-smaller.png"));
		public HeaderPanel() {
			super(new GridLayout(1, 3));

			JLabel label = new JLabel("Stopped");
			label.setForeground(Color.red);
			label.setHorizontalTextPosition(SwingConstants.CENTER);
			label.setHorizontalAlignment(SwingConstants.CENTER);
			
			JLabel mdpnp = new JLabel(this.mdpnp);
			mdpnp.setHorizontalTextPosition(SwingConstants.LEFT);
			mdpnp.setHorizontalAlignment(SwingConstants.LEFT);
			add(mdpnp);
			add(label);
			add(new JLabel(""));
		}
	}
	public static final class FooterPanel extends JPanel implements Runnable {
		private final JLabel time = new JLabel("HH:MM:SS");
		private final Date now = new Date();
		private final DateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		private ScheduledFuture<?> timeFuture;
		private ScheduledExecutorService executor;
		
		@Override
		protected void processHierarchyEvent(HierarchyEvent e) {
			super.processHierarchyEvent(e);
			if ((HierarchyEvent.SHOWING_CHANGED & e.getChangeFlags()) != 0) {
				if(isShowing()) {
					this.timeFuture = executor.scheduleAtFixedRate(this, 1000L - (System.currentTimeMillis() % 1000L) + 10L, 1000L, TimeUnit.MILLISECONDS);
				} else {
					this.timeFuture.cancel(false);
				}
		    }
		}
		
		@Override
		protected void processComponentEvent(ComponentEvent e) {
			super.processComponentEvent(e);
			switch(e.getID()) {
			case ComponentEvent.COMPONENT_SHOWN:
				this.timeFuture = executor.scheduleAtFixedRate(this, 1000L - (System.currentTimeMillis() % 1000L) + 10L, 1000L, TimeUnit.MILLISECONDS);
				break;
			case ComponentEvent.COMPONENT_HIDDEN:
				this.timeFuture.cancel(false);
				break;
			}
		}
		public FooterPanel(java.util.concurrent.ScheduledExecutorService timer) {
			super(new BorderLayout());
			enableEvents(ComponentEvent.HIERARCHY_EVENT_MASK);
			add(time, BorderLayout.EAST);
			this.executor = timer;
		}
		@Override
		public void run() {
			now.setTime(System.currentTimeMillis());
			time.setText(sdf.format(now));
		}
		
	}
	private final DrugLibrary drugLibrary;
	private final PatientPanel patientPanel;
	private final InfusionPanel infusionPanel;
	private final DrugPanel drugPanel;
	private final JPanel headerPanel;
	private final JPanel footerPanel;
	
//	private final InfusionPumpModel infusionPumpModel;
//	private final ScheduledExecutorService executor;
	private final static boolean tabbed = false;
	public GIPPanel(InfusionPumpModel infusionPumpModel, ScheduledExecutorService executor) throws IOException {
		super(new BorderLayout());
//		this.infusionPumpModel = infusionPumpModel;
//		setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 36));
//		setBackground(new Color(255,255,255,180));
//		UIManager.put("FormattedTextField.background", new Color(185, 223, 246));
		setBorder(new EmptyBorder(10,10,10,10));
		enableEvents(ComponentEvent.COMPONENT_EVENT_MASK);
//		this.executor = executor;
		drugLibrary = new DrugLibrary();
		patientPanel = new PatientPanel(drugLibrary);
		infusionPanel = new InfusionPanel();
		drugPanel = new DrugPanel();
		headerPanel = new HeaderPanel();
		footerPanel = new FooterPanel(executor);
		add(headerPanel, BorderLayout.NORTH);
		add(footerPanel, BorderLayout.SOUTH);
		
		patientPanel.setModel(infusionPumpModel.getPatient());
		drugPanel.setModel(infusionPumpModel.getDrug());
		drugPanel.setPatientModel(infusionPumpModel.getPatient());
		drugPanel.setInfusionModel(infusionPumpModel.getInfusion());
		infusionPanel.setModel(infusionPumpModel.getInfusion());
		
		if(tabbed) {
			JTabbedPane mainPanel = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
			mainPanel.addTab("Patient", patientPanel);
			mainPanel.addTab("Drug", drugPanel);
			mainPanel.addTab("Infusion", infusionPanel);
			
			add(mainPanel, BorderLayout.CENTER);
			
		} else {
			JPanel mainPanel = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 5, 5);
			mainPanel.add(patientPanel, gbc);
			addTitledBorder(patientPanel, "Patient");
			addTitledBorder(drugPanel, "Drug");
			addTitledBorder(infusionPanel, "Infusion");
			gbc.gridy++;
			mainPanel.add(drugPanel, gbc);

			gbc.gridy++;
			gbc.weighty=200;
			gbc.fill = GridBagConstraints.BOTH;
			mainPanel.add(infusionPanel, gbc);
			
			add(mainPanel, BorderLayout.CENTER);
		}
		

		// Drug Name
		// Concentration  total mass/total volume in container
		
		// Weight BSA (from weight/height) m^2
		// Dose mL/hr , mL/kg/hr, mcg/min|hr|day, mcg/kg/time, 
		//      volume / time , volume / body-mass / hr, drug mass / time, drug mass / body mass / time, drug mass / BSA / time, drug mass / time 



		

	}
	
	private static void addTitledBorder(JComponent comp, String text) {
//		comp.setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED), text));
		TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(comp.getForeground(), 1/*, false*/), text, TitledBorder.LEFT, TitledBorder.TOP);
		comp.setBorder(border);
	}
//	private final ImageIcon ice_cubes = new ImageIcon(GIPPanel.class.getResource("gradient.png"));
	
	
	@Override
	protected void paintComponent(Graphics g) {
		
//		g.drawImage(ice_cubes.getImage(), 0, 0, getWidth(), getHeight(), img_src_x1, img_src_y1, img_src_x2, img_src_y2, null);
//		g.drawImage(mdpnp.getImage(), 0, 0, null);
		super.paintComponent(g);
		
//		super.paintComponent(g);
	}
	
	public static void main(String[] args) throws IOException, UnsupportedLookAndFeelException {
	}
	public static void setChildrenOpaque(Component c, boolean opaque) {
		if(c instanceof Container) {
			for(Component co : ((Container)c).getComponents()) {
				setChildrenOpaque(co, opaque);
			}
		}
		if(c instanceof JTextField) {
			return;
		}
		
		if(c instanceof JComponent) {
			((JComponent)c).setOpaque(opaque);
		}
		if(c instanceof JScrollPane) {
			((JScrollPane)c).getViewport().setOpaque(opaque);
		}
		if(c instanceof JTable) {
			((JTable)c).getTableHeader().setOpaque(opaque);
		}
		if(c instanceof JList) {
			
		}
	}
	public JPanel getHeaderPanel() {
		return headerPanel;
	}
}
