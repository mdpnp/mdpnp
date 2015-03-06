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

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.FlowPane;

import org.mdpnp.apps.testapp.vital.MultiRangeSlider;
import org.mdpnp.apps.testapp.vital.Vital;

public final class VitalView {
    private Vital vital;
    
    @FXML protected MultiRangeSlider slider;
    @FXML protected Label name;
    @FXML protected Button deleteButton;
    @FXML protected CheckBox ignoreZeroBox, requiredBox;
    @FXML protected FlowPane vitalValues;
    
    
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
    
    private static class ConcreteDoubleProperty extends SimpleDoubleProperty implements InvalidationListener {
        private final ObjectProperty<Double> source;
        private final double reportIfNull;
        public ConcreteDoubleProperty(ObjectProperty<Double> source) {
            this(source, Double.NaN);
        }
        
        public ConcreteDoubleProperty(ObjectProperty<Double> source, double reportIfNull) {
            this.source = source;
            this.reportIfNull = reportIfNull;
            // TODO register listener weakly
            source.addListener(this);
        }
        @Override
        public void set(double newValue) {
            super.set(newValue);
            source.set(newValue);
        }
        @Override
        public double get() {
            Double s = source.get();
            return null == s ? reportIfNull : s;
        }
        @Override
        public void invalidated(Observable observable) {
            fireValueChangedEvent();
        }
    }
    
    public VitalView set(final Vital vital) {
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
        slider.maxProperty().bind(vital.maximumProperty());
        slider.minProperty().bind(vital.minimumProperty());
        slider.lowestValueVisibleProperty().bind(vital.criticalLowProperty().isNotNull());
        slider.lowerValueVisibleProperty().bind(vital.warningLowProperty().isNotNull());
        slider.higherValueVisibleProperty().bind(vital.warningHighProperty().isNotNull());
        slider.highestValueVisibleProperty().bind(vital.criticalHighProperty().isNotNull());
        
        vital.warningLowProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {
                System.out.println("WARNING LOW CHANGED TO " + vital.getWarningLow());
            }
            
        });  
        vital.warningHighProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {
                System.out.println("WARNING HIGH CHANGED TO " + vital.getWarningHigh());
            }
            
        });
        vital.criticalLowProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {
                System.out.println("CRITICAL LOW CHANGED TO " + vital.getCriticalLow());
            }
            
        });        
        vital.criticalHighProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {
                System.out.println("CRITICAL HIGH CHANGED TO " + vital.getCriticalHigh());
            }
            
        });        
        
        
      
        // Cripes if you think about it the order here is really quite important since values will be clamped
        // down
        slider.highestValueProperty().bindBidirectional(new ConcreteDoubleProperty(vital.criticalHighProperty()));
        slider.lowestValueProperty().bindBidirectional(new ConcreteDoubleProperty(vital.criticalLowProperty()));
        slider.higherValueProperty().bindBidirectional(new ConcreteDoubleProperty(vital.warningHighProperty()));
        slider.lowerValueProperty().bindBidirectional(new ConcreteDoubleProperty(vital.warningLowProperty()));
        
//        VitalBoundedRangeMulti range = new VitalBoundedRangeMulti(vital);
//        slider = new JMultiSlider(range);
//        slider.setRangeColor(0, Color.red);
//        slider.setRangeColor(1, Color.yellow);
//        slider.setRangeColor(2, Color.green);
//        VitalValueMsBoundedRangeMulti range2 = new VitalValueMsBoundedRangeMulti(vital);
//        slider2 = new JMultiSlider(range2);
//        slider2.setFont(font);
//        slider2.setRangeColor(0, Color.yellow);
//        slider2.setRangeColor(1, Color.green);
//        slider2.setDrawThumbs(true);

        return this;
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
        final int N = vital.getValues().isEmpty() ? 1 : vital.getValues().size();
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

        if (vital.getValues().isEmpty()) {
            // ((JLabel)vitalValues.getComponent(0)).setForeground(Color.yellow);
            // ((JLabel)vitalValues.getComponent(0)).setBackground(Color.yellow);
            
            // TODO include this
//            ((ValueView) vitalValues.getChildren().get(0)).update(null, null, "<NO SOURCES>", null, null, 0L, 0L);
        } else {
            for (int i = 0; i < vital.getValues().size(); i++) {
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
}
