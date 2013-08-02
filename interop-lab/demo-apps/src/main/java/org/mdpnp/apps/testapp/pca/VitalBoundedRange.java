package org.mdpnp.apps.testapp.pca;

import javax.swing.BoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.apps.testapp.vital.VitalModelListener;

public class VitalBoundedRange implements BoundedRangeModel {
    private final Vital vital;
    
    public VitalBoundedRange(Vital vital) {
        this.vital = vital;
    }
    
    
    @Override
    public int getMinimum() {
        return (int) vital.getMinimum();
    }

    @Override
    public void setMinimum(int newMinimum) {
        
    }

    @Override
    public int getMaximum() {
        return (int) vital.getMaximum();
    }

    @Override
    public void setMaximum(int newMaximum) {
        
    }

    @Override
    public int getValue() {
        return (int) vital.getLow();
    }

    @Override
    public void setValue(int newValue) {
        vital.setLow(newValue);
    }

    private boolean valueIsAdjusting;
    @Override
    public void setValueIsAdjusting(boolean b) {
        this.valueIsAdjusting = b;
    }

    @Override
    public boolean getValueIsAdjusting() {
        return valueIsAdjusting;
    }

    @Override
    public int getExtent() {
        return (int) (vital.getHigh() - vital.getLow());
    }

    @Override
    public void setExtent(int newExtent) {
        vital.setHigh(vital.getLow() + newExtent);
    }

    @Override
    public void setRangeProperties(int value, int extent, int min, int max, boolean adjusting) {
        vital.setLow(value);
        vital.setHigh(value + extent);
        valueIsAdjusting = adjusting;
    }

    private final class ChangeVitalAdapter implements VitalModelListener {
        private final ChangeListener listener;
        private final Vital vital;
        
        public ChangeVitalAdapter(ChangeListener listener, Vital vital) {
            this.listener = listener;
            this.vital = vital;
        }
        @Override
        public boolean equals(Object obj) {
            return listener.equals(obj);
        }
        @Override
        public int hashCode() {
            return listener.hashCode();
        }
        @Override
        public void vitalChanged(VitalModel model, Vital vital) {
            if(this.vital.equals(vital)) {
                listener.stateChanged(CHANGE_EVENT);
            }
        }
        @Override
        public void vitalRemoved(VitalModel model, Vital vital) {
        }
        @Override
        public void vitalAdded(VitalModel model, Vital vital) {
        }
    }


    
    private final ChangeEvent CHANGE_EVENT = new ChangeEvent(this);

    @Override
    public void addChangeListener(ChangeListener x) {
        vital.getParent().addListener(new ChangeVitalAdapter(x, vital));
    }


    @Override
    public void removeChangeListener(ChangeListener x) {
        vital.getParent().removeListener(new ChangeVitalAdapter(x, vital));
    }

}
