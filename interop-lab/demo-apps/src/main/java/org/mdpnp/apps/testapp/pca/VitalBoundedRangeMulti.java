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

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.mdpnp.apps.testapp.vital.BoundedRangeMultiModel;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.apps.testapp.vital.VitalModelListener;

/**
 * @author Jeff Plourde
 *
 */
public class VitalBoundedRangeMulti implements BoundedRangeMultiModel {
    private final Vital vital;

    public VitalBoundedRangeMulti(final Vital vital) {
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
    public Float getValue(int idx) {
        switch (idx) {
        case 0:
            return vital.getCriticalLow();
        case 1:
            return vital.getWarningLow();
        case 2:
            return vital.getWarningHigh();
        case 3:
            return vital.getCriticalHigh();
        default:
            throw new IllegalArgumentException("No such idx=" + idx);
        }
    }

    @Override
    public void setValue(int idx, Float newValue) {
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
    public Float getMarker(int idx) {
        return vital.getValues().get(idx).getNumeric().value;
    }
}
