package org.mdpnp.gip.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import org.mdpnp.gip.ui.units.Units;
import org.mdpnp.gip.ui.values.Value;
import org.mdpnp.gip.ui.values.ValueListener;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class ValueField<U extends Units> extends javax.swing.JPanel implements ValueListener<U> {
	private final JSpinner spinner = new JSpinner();
	private final JComboBox units = new JComboBox();
	private Value<U> model;
	
	public void setModel(Value<U> model) {
		if(null != this.model) {
			this.model.removeListener(this);
		}
		this.model = model;
		if(null != this.model) {
			units.setModel(new DefaultComboBoxModel(this.model.getAcceptableUnits()));
			this.model.addListener(this);
		} else {
			units.setModel(new DefaultComboBoxModel());
		}
		update();
	}
	@SuppressWarnings("unchecked")
	public ValueField() {
		final JTextField editorTextField = ((JSpinner.NumberEditor)spinner.getEditor()).getTextField();
		editorTextField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
			}
		});
		
		editorTextField.addFocusListener(new FocusListener() {
			private Popup popup;
			@Override
			public void focusGained(FocusEvent e) {
				JFrame f = (JFrame) editorTextField.getTopLevelAncestor();
				Point frameLocation = f.getLocationOnScreen();
				Dimension frameDimensions = f.getSize();
				Point boxLocation = editorTextField.getLocationOnScreen();
				Dimension boxDimensions = editorTextField.getSize();
				
				editorTextField.select(0, editorTextField.getDocument().getLength());

				int frameTop = frameLocation.y;
				int frameBottom = frameTop + frameDimensions.height;
				int boxTop = boxLocation.y;
				int boxBottom = boxTop + boxDimensions.height;
				
				int spaceBelow = frameBottom - boxBottom;
				int spaceAbove = boxTop - frameTop;
				
				Dimension preferredSize = new Dimension(200, 200);
				
				int posy;
				
				if(spaceBelow >= preferredSize.height) {
					posy = boxBottom;
				} else if(spaceAbove >= preferredSize.height) {
					posy = boxTop - preferredSize.height;
				} else {
					if(spaceBelow>=spaceAbove) {
						preferredSize.height = spaceBelow;
						posy = boxBottom;
					} else {
						posy = boxTop - preferredSize.height;
					}
				}
				
				if(preferredSize.width > frameDimensions.width) {
					preferredSize.width = frameDimensions.width;
				}
				
				int posx = boxLocation.x;
				
				if( (frameLocation.x+frameDimensions.width-posx) < preferredSize.width) {
					posx = frameLocation.x+frameDimensions.width - preferredSize.width;
				} else if( (frameLocation.x+frameDimensions.width-posx) < 0) {
					posx = frameLocation.x;
				}
				
				Keypad kp = new Keypad();
				
				kp.setPreferredSize(preferredSize);
				kp.setComponent(editorTextField);
				
				popup = PopupFactory.getSharedInstance().getPopup(null, kp, posx, posy);
				
				popup.show();

			}

			@Override
			public void focusLost(FocusEvent e) {
				popup.hide();
				popup = null;
			}
			
		});
		
		units.setRenderer( new BasicComboBoxRenderer() {
		    
			@Override
			@SuppressWarnings("rawtypes")
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				Component c = super.getListCellRendererComponent(list, value, index, isSelected,
						cellHasFocus);
//				if(cellHasFocus) {
				if(null != value) {
					setText(((Units)value).getAbbreviatedName());
				}
//				}
				return c;
			}
		});
		units.setEditor(new BasicComboBoxEditor() {
			
			@Override
			public void setItem(Object anObject) {
				super.setItem(anObject);
				if(anObject instanceof Units) {
					editor.setText(((Units)anObject).getAbbreviatedName());
				}
			}
			
		});
		add(spinner);
		add(units);
		((JSpinner.NumberEditor)spinner.getEditor()).getTextField().setHorizontalAlignment(SwingConstants.RIGHT);
		((JSpinner.NumberEditor)spinner.getEditor()).getTextField().setColumns(3);
		
		
		spinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				model.setValue(((Number)spinner.getValue()).doubleValue());
			}
			
		});

		units.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				switch(e.getStateChange()) {
				case ItemEvent.SELECTED:
					model.setUnits((U)units.getSelectedItem());
					break;
				}
			}
			
		});
		
	}
	
	private void update() {
		Value<U> model = this.model;
		if(null != model) {
			Double value = model.getValue(); 
			spinner.setValue(null == value ? 0.0 : value);
			U u = model.getUnits();
			units.setSelectedItem(u);
			Double massStepSize = model.getStepSize();
			if(massStepSize != null) {
				((SpinnerNumberModel)spinner.getModel()).setStepSize(massStepSize);
			}
		}
	}

	public JSpinner getSpinner() {
		return spinner;
	}
	
	@Override
	public void valueChanged(Value<U> v) {
		update();
	}

	@Override
	public void unitsChanged(Value<U> v) {
		update();
	}

	@Override
	public void boundsChanged(Value<U> v) {
		update();
	}
}
