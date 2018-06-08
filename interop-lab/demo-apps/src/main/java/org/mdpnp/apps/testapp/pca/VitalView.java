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
package org.mdpnp.apps.testapp.pca;

import java.io.IOException;
import java.util.Iterator;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import org.mdpnp.apps.testapp.vital.ConcreteDoubleProperty;
import org.mdpnp.apps.testapp.vital.MultiRangeSlider;
import org.mdpnp.apps.testapp.vital.Value;
import org.mdpnp.apps.testapp.vital.Vital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VitalView implements ListChangeListener<Value> {
    private Vital vital;
    
    @FXML protected MultiRangeSlider slider;
    @FXML protected Label name;
    @FXML protected Button deleteButton;
    @FXML protected CheckBox ignoreZeroBox, requiredBox;
    @FXML protected HBox vitalValues;
    @FXML protected Spinner lowestSpinner, lowerSpinner, higherSpinner, highestSpinner;
    
    
//    private final JMultiSlider slider, slider2;

    // private JLabel limitsLabel = new JLabel("Limits:");
    // private JLabel configureLabel = new JLabel("Configure:");

    // private boolean showConfiguration = false;

    public Vital getVital() {
        return vital;
    }

    @FXML public void deleteButtonAction(ActionEvent evt) {
        vital.getParent().remove(vital);
    }
    
    @FXML public void ignoreZeroAction(ActionEvent evt) {
        vital.setIgnoreZero(ignoreZeroBox.isSelected());
    }
    
    @FXML public void requiredAction(ActionEvent evt) {
        vital.setNoValueWarning(requiredBox.isSelected());
    }
    
    public VitalView set(final Vital vital, ReadOnlyBooleanProperty configuration) {
        this.vital = vital;
        String lbl = vital.getLabel();
        if (lbl.contains(" ")) {
            lbl = lbl.replaceAll("\\ +", "<br/>");
            lbl = lbl + " (" + vital.getUnits() + ")";
            lbl = "<html>" + lbl + "</html>";
        } else {
            lbl = lbl + " (" + vital.getUnits() + ")";
        }
        name.textProperty().bind(vital.labelProperty());

        ignoreZeroBox.selectedProperty().bindBidirectional(vital.ignoreZeroProperty());
        requiredBox.selectedProperty().bindBidirectional(vital.requiredProperty());

        controls.visibleProperty().bind(configuration);

        bindSlider(slider, vital);
        bindSpinnersToSlider();
        
        for(Value v : vital) {
            add(v);
        }
        // TODO how do I cope with this... how can i iterate and add listener atomically?
        vital.addListener(this);
        
        return this;
    }
    
    private void bindSpinnersToSlider() {
    	
    	lowestSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(slider.minProperty().doubleValue(), slider.maxProperty().doubleValue()));
    	lowestSpinner.setEditable(true);
    	lowerSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(slider.minProperty().doubleValue(), slider.maxProperty().doubleValue()));
    	lowerSpinner.setEditable(true);
    	higherSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(slider.minProperty().doubleValue(), slider.maxProperty().doubleValue()));
    	higherSpinner.setEditable(true);
    	highestSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(slider.minProperty().doubleValue(), slider.maxProperty().doubleValue()));
    	highestSpinner.setEditable(true);
    	
    	lowestSpinner.getValueFactory().setValue(slider.lowestValueProperty().getValue());
    	lowerSpinner.getValueFactory().setValue(slider.lowerValueProperty().getValue());
    	higherSpinner.getValueFactory().setValue(slider.higherValueProperty().getValue());
    	highestSpinner.getValueFactory().setValue(slider.highestValueProperty().getValue());
    	
    	//When we have stuff bound in both directions, we will need a little lock to prevent a race condition
    	
    	lowestSpinner.valueProperty().addListener( (observable, oldValue, newValue) ->
    		{
    			if( ((Number)newValue).doubleValue() > slider.lowerValueProperty().doubleValue()) {
    				//Can only change the lowest value if it's less than lower
    				lowestSpinner.getValueFactory().setValue(oldValue);
    				return;
    			}
    			slider.lowestValueProperty().setValue((Number)newValue);
			}
    	);
    	
    	lowerSpinner.valueProperty().addListener( (observable, oldValue, newValue) ->
			{
				if( ((Number)newValue).doubleValue() < slider.lowestValueProperty().doubleValue()) {
					//Can only change the lower value if it's more than lowest
					lowerSpinner.getValueFactory().setValue(oldValue);
					return;
				}
				if( ((Number)newValue).doubleValue() > slider.higherValueProperty().doubleValue()) {
					//Can only change the lower value if it's less than higher
					lowerSpinner.getValueFactory().setValue(oldValue);
					return;
				}
				slider.lowerValueProperty().setValue((Number)newValue);
			}
    	);
    	
    	higherSpinner.valueProperty().addListener( (observable, oldValue, newValue) ->
			{
				if( ((Number)newValue).doubleValue() > slider.highestValueProperty().doubleValue()) {
					//Can only change the higher value if it's less than highest
					higherSpinner.getValueFactory().setValue(oldValue);
					return;
				}
				if( ((Number)newValue).doubleValue() < slider.lowerValueProperty().doubleValue()) {
					//Can only change the higher value if it's more than lower
					higherSpinner.getValueFactory().setValue(oldValue);
					return;
				}
				slider.higherValueProperty().setValue((Number)newValue);
				
			}
    	);
    	
    	highestSpinner.valueProperty().addListener( (observable, oldValue, newValue) ->
			{
				if( ((Number)newValue).doubleValue() > slider.higherValueProperty().doubleValue()) {
					//Can only change the highest value if it's more than higher
					highestSpinner.getValueFactory().setValue(oldValue);
					return;
				}
				slider.highestValueProperty().setValue((Number)newValue);
				
			}
    	);
    	
    	/*
    	 * With these binds enabled, nasty crash seems to occur when moving the slider
    	 */
    	/*
    	slider.lowestValueProperty().bind(lowestSpinner.valueProperty());
    	slider.lowerValueProperty().bind(lowerSpinner.valueProperty());
    	slider.higherValueProperty().bind(higherSpinner.valueProperty());
    	slider.highestValueProperty().bind(highestSpinner.valueProperty());
    	*/

    	/*
    	 * Obviously, we also want to bind the other way round so the spinners change when the slider does.
    	 * CHECK FOR RACE!!!
    	 */
    	slider.lowestValueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				lowestSpinner.getValueFactory().setValue(newValue);
			}
    	});
    	
    	slider.lowerValueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				lowerSpinner.getValueFactory().setValue(newValue);
			}
    	});
    	
    	slider.higherValueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				higherSpinner.getValueFactory().setValue(newValue);
			}
    	});
    	
    	slider.highestValueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				highestSpinner.getValueFactory().setValue(newValue);
			}
    	});

    }

    /*
     * SK - not sure why this is static - stops us referencing other instance specific controls...
     */
    static public void bindSlider(MultiRangeSlider slider, Vital vital) {

        // Before we bind the properties, set the values so that validation logic is not
        // going crazy 'cause of values interdependencies and the order or bind commands.
        //
        slider.maxProperty().set(vital.getMaximum());
        slider.minProperty().set(vital.getMinimum());
        slider.highestValueProperty().set(vital.getCriticalHigh());
        slider.lowerValueProperty().set(vital.getWarningLow());
        slider.lowestValueProperty().set(vital.getCriticalLow());
        slider.higherValueProperty().set(vital.getWarningHigh());

        slider.maxProperty().bind(vital.maximumProperty());
        slider.minProperty().bind(vital.minimumProperty());
        slider.lowestValueVisibleProperty().bind(vital.criticalLowProperty().isNotNull());
        slider.lowerValueVisibleProperty().bind(vital.warningLowProperty().isNotNull());
        slider.higherValueVisibleProperty().bind(vital.warningHighProperty().isNotNull());
        slider.highestValueVisibleProperty().bind(vital.criticalHighProperty().isNotNull());

        slider.highestValueProperty().bindBidirectional(new ConcreteDoubleProperty(vital.criticalHighProperty()));
        slider.lowerValueProperty().bindBidirectional(new ConcreteDoubleProperty(vital.warningLowProperty()));
        slider.lowestValueProperty().bindBidirectional(new ConcreteDoubleProperty(vital.criticalLowProperty()));
        slider.higherValueProperty().bindBidirectional(new ConcreteDoubleProperty(vital.warningHighProperty()));
    }

    public VitalView() {


    }


    public void setShowConfiguration(boolean showConfiguration) {
//        slider.setDrawThumbs(showConfiguration);
        // slider.setVisible(showConfiguration);
//        slider2.setVisible(showConfiguration);
        ignoreZeroBox.setVisible(showConfiguration);
        requiredBox.setVisible(showConfiguration);
        deleteButton.setVisible(showConfiguration);

//        validate();

    }


    // @Override
    // public void stateChanged(ChangeEvent e) {
    // float range = vital.getMaximum() - vital.getMinimum();
    // int incr = (int) (range / 5f);
    // if(incr != slider.getMajorTickSpacing()) {
    // slider.setLabelTable(slider.createStandardLabels(incr));
    // slider.setMajorTickSpacing(incr);
    // }
    // }

    public void updateData() {
        final int N = vital.isEmpty() ? 1 : vital.size();
        ignoreZeroBox.setSelected(vital.isIgnoreZero());
        requiredBox.setSelected(vital.isNoValueWarning());
        // name.setText(vital.getLabel());
        try {
            if (N != vitalValues.getChildren().size()) {
//                Runnable r = new Runnable() {
//                    public void run() {
//
//                        while (vitalValues.getChildren().size() < N) {
//                            for (int i = 0; i < (N - vitalValues.getChildren().size()); i++) {
//                                ValueView val = new ValueView();
//                                
////                                val.setBorder(new LineBorder(Color.black));
////                                vitalValues.getChildren().add(val);
//                            }
////                            validate();
//                        }
//                        while (N < vitalValues.getChildren().size()) {
//                            for (int i = 0; i < (vitalValues.getChildren().size() - N); i++) {
////                                vitalValues.remove(0);
//                            }
//                        }
//
//                    }
//                };

            }
//        } catch (InvocationTargetException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
        }
        finally {}

        if (vital.isEmpty()) {
            // ((JLabel)vitalValues.getComponent(0)).setForeground(Color.yellow);
            // ((JLabel)vitalValues.getComponent(0)).setBackground(Color.yellow);
            
            // TODO include this
//            ((ValueView) vitalValues.getChildren().get(0)).update(null, null, "<NO SOURCES>", null, null, 0L, 0L);
        } else {
            for (int i = 0; i < vital.size(); i++) {
//                Value val = vital.getValues().get(i);
//                ValueView lbl = (ValueView) vitalValues.getComponent(i);

//                ice.DeviceIdentity di = vital.getParent().getDeviceIdentity(val.getUniqueDeviceIdentifier());
//                DeviceIcon dicon = vital.getParent().getDeviceIcon(val.getUniqueDeviceIdentifier());
                // dicon = null == dicon ? null : new ScaledDeviceIcon(dicon,
                // 0.5);
//                if (null != di) {
//                    String s = di.manufacturer.equals(di.model) ? di.manufacturer : (di.manufacturer + " " + di.model);
//                    lbl.update(val, dicon, s, val.getNumeric(), val.getSampleInfo(), val.getValueMsBelowLow(), val.getValueMsAboveHigh());
//                } else {
//                    lbl.update(val, dicon, "", val.getNumeric(), val.getSampleInfo(), val.getValueMsBelowLow(), val.getValueMsAboveHigh());
//                }

            }
        }
    }
    private static final Logger log = LoggerFactory.getLogger(VitalView.class);

    @FXML FlowPane controls;
    private void add(Value value) {
        try {
            FXMLLoader loader = new FXMLLoader(ValueView.class.getResource("ValueView.fxml"));
            Parent node = loader.load();
            ValueView v = loader.getController();
            v.set(value);
            node.setUserData(value);
            vitalValues.getChildren().add(node);
            //This view is the minituare device view on the right hand side.
        } catch (IOException ioe) {
            log.error("Unable to load a UI for new Value", ioe);
        }
        
    }

    @Override
    public void onChanged(javafx.collections.ListChangeListener.Change<? extends Value> c) {
        while(c.next()) {
            for(Value v : c.getRemoved()) {
                for(Iterator<Node> itr = vitalValues.getChildren().iterator(); itr.hasNext(); ) {
                    if(v.equals(itr.next().getUserData())) {
                       log.debug("Removed a UI element for " + v.getMetricId());
                        itr.remove();
                    }
                }
            }
            for(Value v : c.getAddedSubList()) {
                add(v);
            }
        }

    }
}
