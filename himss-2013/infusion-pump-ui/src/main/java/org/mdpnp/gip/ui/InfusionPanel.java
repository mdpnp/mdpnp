package org.mdpnp.gip.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
//import java.beans.Transient;
import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.mdpnp.gip.ui.units.RatioUnits;
import org.mdpnp.gip.ui.units.TimeUnits;
import org.mdpnp.gip.ui.units.VolumeUnits;
import org.mdpnp.gip.ui.values.Value;
import org.mdpnp.gip.ui.values.ValueAdapter;
import org.mdpnp.gip.ui.values.ValueListener;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class InfusionPanel extends javax.swing.JPanel {
	private InfusionModel infusion;
	
	private final Icon upIcon = new JArrowIcon(0);
	private final Icon downIcon = new JArrowIcon(180);
	
	private final JSlider rateSlider = new JSlider(JSlider.VERTICAL);
//	private final JKnob rateKnob = new JKnob();
	private final JButton rateArrowUp = new JButton(upIcon);
	private final JButton rateArrowDown = new JButton(downIcon);
	private final ValueField<RatioUnits> rateValue = new ValueField<RatioUnits>();
	
	private final JSlider vtbiSlider = new JSlider(JSlider.VERTICAL);
//	private final JKnob vtbiKnob = new JKnob();
	private final JButton vtbiArrowUp = new JButton(upIcon);
	private final JButton vtbiArrowDown = new JButton(downIcon);
	private final ValueField<VolumeUnits> vtbiValue = new ValueField<VolumeUnits>();
	
	private final JSlider durationSlider = new JSlider(JSlider.VERTICAL);
//	private final JKnob durationKnob = new JKnob();
	private final JButton durationArrowUp = new JButton(upIcon);
	private final JButton durationArrowDown = new JButton(downIcon);
	private final ValueField<TimeUnits> durationValue = new ValueField<TimeUnits>();

	private final ValueListener<TimeUnits> durationListener = new ValueAdapter<TimeUnits>() {
		public void boundsChanged(org.mdpnp.gip.ui.values.Value<TimeUnits> v) {
			boundSlider(durationSlider, v.getMinimum(), v.getMaximum(), v.getSoftMinimum(), v.getSoftMaximum(), v.getStarting());
		};
		@Override
		protected void anythingChanged(Value<TimeUnits> v) {
			Double _v = v.getValue();
			if(_v != null) {
				durationSlider.setValue((int)(double)v.getValue());
			} else {
				durationSlider.setValue(0);
			}
		}
	};
	static void boundSlider(JSlider slider, Double minimum, Double maximum, Double softMinimum, Double softMaximum, Double start) {
		boolean changed = false;
		
		if(null != minimum) {
			int min = (int)(double)minimum;
			if(min != slider.getMinimum()) {
				slider.setMinimum(min);
				changed = true;
			}
		}
		if(null != maximum) {
			int max = (int)(double)maximum;
			if(max != slider.getMaximum()) {
				slider.setMaximum(max);
				changed = true;
			}
		} else if(null != softMaximum) {
			int max = (int)(double)softMaximum;
			max *= 2;
			if(max != slider.getMaximum()) {
				slider.setMaximum(max);
				changed = true;
			}
		}
		changed = true;
		if(changed) {
			slider.setPaintLabels(true);
			slider.setPaintTicks(true);
			int incr = (slider.getMaximum() - slider.getMinimum()) / 5;
			Hashtable<Integer, JLabel> ht = new Hashtable<Integer, JLabel>();

//			ht.put(slider.getMinimum(), new JLabel(Double.toString(slider.getMinimum())));
			ht.put(slider.getMaximum(),  new JLabel(Double.toString(slider.getMaximum())));
//			if(start != null) {
//				ht.put((int)(double)start,  new JLabel(Integer.toString((int)(double)start)));
//			}
			if(softMaximum!=null) {
				ht.put((int)(double)softMaximum, new JLabel(Double.toString(softMaximum)));
			}
			if(softMinimum!=null) {
				ht.put((int)(double)softMinimum, new JLabel(Double.toString(softMinimum)));
			}
			
			slider.setLabelTable(ht);
//			slider.setLabelTable(slider.createStandardLabels( incr ));
			slider.setMajorTickSpacing(incr);
		}
	}
	
	private final ValueListener<VolumeUnits> vtbiListener = new ValueAdapter<VolumeUnits>() {
		public void boundsChanged(org.mdpnp.gip.ui.values.Value<VolumeUnits> v) {
			boundSlider(vtbiSlider, v.getMinimum(), v.getMaximum(), v.getSoftMinimum(), v.getSoftMaximum(), v.getStarting());
		};
		
		@Override
		protected void anythingChanged(Value<VolumeUnits> v) {
			Double _v = v.getValue();
			if(_v != null) {
				vtbiSlider.setValue((int)(double)v.getValue());
			} else {
				vtbiSlider.setValue(0);
			}
		}
	};
	private final ValueListener<RatioUnits> rateListener = new ValueAdapter<RatioUnits>() {
		@Override
		public void boundsChanged(Value<RatioUnits> v) {
			boundSlider(rateSlider, v.getMinimum(), v.getMaximum(), v.getSoftMinimum(), v.getSoftMaximum(), v.getStarting());
		}
		@Override
		protected void anythingChanged(Value<RatioUnits> v) {
			Double _v = v.getValue();
			if(_v != null) {
				rateSlider.setValue((int)(double)v.getValue());
			} else {
				rateSlider.setValue(0);
			}
		}
	};
	public void setModel(InfusionModel infusion) {
		if(this.infusion != null) {
			this.infusion.getDuration().removeListener(durationListener);
			this.infusion.getVolumeToBeInfused().removeListener(vtbiListener);
			this.infusion.getRate().removeListener(rateListener);
		}

		
		this.infusion = infusion;
		
		if(this.infusion != null) {
			this.infusion.getDuration().addListener(durationListener);
			durationValue.setModel(this.infusion.getDuration());
			
			this.infusion.getVolumeToBeInfused().addListener(vtbiListener);
			vtbiListener.boundsChanged(this.infusion.getVolumeToBeInfused());
			rateListener.boundsChanged(this.infusion.getRate());
			durationListener.boundsChanged(this.infusion.getDuration());
			vtbiValue.setModel(this.infusion.getVolumeToBeInfused());
			
			this.infusion.getRate().addListener(rateListener);
			rateValue.setModel(this.infusion.getRate());
		} else {
			durationValue.setModel(null);
			vtbiValue.setModel(null);
			rateValue.setModel(null);
		}
		update();
	}
	public InfusionModel getModel() {
		return infusion;
	}
	private static final JLabel centerLabel(String txt) {
		JLabel lbl = new JLabel(txt);
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		return lbl;
	}
	
	private static ActionListener key(final JComponent c, final int key) {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				KeyEvent event = new KeyEvent(c, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, key, ' ');
				c.dispatchEvent(event);
			}
			
		};
	}
	
	
	public InfusionPanel() {
		super(new GridBagLayout());
		
		
		
		durationSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				InfusionModel model = getModel();
				if(model != null) {
					model.getDuration().setValue((double)durationSlider.getValue());
				}
			}
			
		});
		vtbiSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				InfusionModel model = getModel();
				if(model != null) {
					model.getVolumeToBeInfused().setValue((double)vtbiSlider.getValue());
				}
			}
		});
		
		rateSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				InfusionModel model = getModel();
				if(model != null) {
					model.getRate().setValue((double)rateSlider.getValue());
				}
			}
			
		});
		
		rateArrowUp.addActionListener(key(rateValue.getSpinner(), KeyEvent.VK_UP));
		rateArrowDown.addActionListener(key(rateValue.getSpinner(), KeyEvent.VK_DOWN));
		vtbiArrowUp.addActionListener(key(vtbiValue.getSpinner(), KeyEvent.VK_UP));
		vtbiArrowDown.addActionListener(key(vtbiValue.getSpinner(), KeyEvent.VK_DOWN));
		durationArrowUp.addActionListener(key(durationValue.getSpinner(), KeyEvent.VK_UP));
		durationArrowDown.addActionListener(key(durationValue.getSpinner(), KeyEvent.VK_DOWN));
		
		
		GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,1.0,1.0,GridBagConstraints.BASELINE, GridBagConstraints.NONE, new Insets(1,1,1,1), 1, 1);
		
		gbc.gridwidth = 2;
		add(centerLabel("Rate"), gbc);
		gbc.gridx += 2;
		add(centerLabel("VTBI"), gbc);
		gbc.gridx += 2;
		add(centerLabel("Duration"), gbc);
		
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		gbc.weighty = 100.0;
		gbc.gridy = 1;
		gbc.gridheight = 2;
		gbc.fill = GridBagConstraints.VERTICAL;
		add(rateSlider, gbc);
		gbc.gridx+=2;
		add(vtbiSlider, gbc);
		gbc.gridx+=2;
		add(durationSlider, gbc);
		
//		rateArrowUp.setForeground(rateLabel.getForeground());
//		rateArrowDown.setForeground(rateLabel.getForeground());
//		vtbiArrowUp.setForeground(rateLabel.getForeground());
//		vtbiArrowDown.setForeground(rateLabel.getForeground());
//		durationArrowUp.setForeground(rateLabel.getForeground());
//		durationArrowDown.setForeground(rateLabel.getForeground());
		
		gbc.gridx = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.NONE;
//		rateArrowUp.setBackground(Color.red);
		
		add(rateArrowUp, gbc);
		
		gbc.gridx+=2;
		add(vtbiArrowUp, gbc);
		gbc.gridx+=2;
		add(durationArrowUp, gbc);
		
		gbc.gridx = 1;
		gbc.gridy++;
		add(rateArrowDown, gbc);
		gbc.gridx+=2;
		add(vtbiArrowDown, gbc);
		gbc.gridx+=2;
		add(durationArrowDown, gbc);
		
//		gbc.gridx = 1;
//		gbc.fill = GridBagConstraints.NONE;
//		add(rateKnob, gbc);
//		gbc.gridx += 2;
//		add(vtbiKnob, gbc);
//		gbc.gridx += 2;
//		add(durationKnob, gbc);
		
		gbc.weightx = 100.0;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.gridy = 3;
		gbc.weighty = 1.0;
		add(rateValue, gbc);
		gbc.gridx += 2;
		add(vtbiValue, gbc);
		gbc.gridx += 2;
		add(durationValue, gbc);
		

		
	}
	
	@Override
//	@Transient
	public Color getForeground() {
		return super.getForeground();
	}
	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);
	}
	private void update() {
		
	}
}
