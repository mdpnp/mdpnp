package org.mdpnp.apps.testapp.pca;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.mdpnp.apps.testapp.vital.BoundedRangeMultiModel;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.apps.testapp.vital.VitalModelListener;

public class VitalBoundedRangleMulti implements BoundedRangeMultiModel {
    private final Vital vital;

    public VitalBoundedRangleMulti(final Vital vital) {
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
    public int getValue(int idx) {
        switch (idx) {
        case 0:
            return (int) vital.getCriticalLow();
        case 1:
            return (int) vital.getWarningLow();
        case 2:
            return (int) vital.getWarningHigh();
        case 3:
            return (int) vital.getCriticalHigh();
        default:
            throw new IllegalArgumentException("No such idx=" + idx);
        }
    }

    @Override
    public void setValue(int idx, int newValue) {
        switch (idx) {
        case 0:
            vital.setCriticalLow(newValue);
            break;
        case 1:
            vital.setWarningLow(newValue);
            break;
        case 2:
            vital.setWarningHigh(newValue);
            break;
        case 3:
            vital.setCriticalHigh(newValue);
            break;
        default:
            throw new IllegalArgumentException("No such idx=" + idx);
        }
    }

    @Override
    public int getValueCount() {
        return 4;
    }

    private boolean valueIsAdjusting = false;

    @Override
    public void setValueIsAdjusting(boolean b) {
        valueIsAdjusting = b;
    }

    @Override
    public boolean getValueIsAdjusting() {
        return valueIsAdjusting;
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
            if (this.vital.equals(vital)) {
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

    protected final ChangeEvent CHANGE_EVENT = new ChangeEvent(this);

    @Override
    public void addChangeListener(ChangeListener x) {
        vital.getParent().addListener(new ChangeVitalAdapter(x, vital));
    }

    @Override
    public void removeChangeListener(ChangeListener x) {
        vital.getParent().removeListener(new ChangeVitalAdapter(x, vital));
    }

    @Override
    public int getMarkerCount() {
        return vital.getValues().size();
    }
    
    @Override
    public int getMarker(int idx) {
        return (int) vital.getValues().get(idx).getNumeric().value;
    }
}
