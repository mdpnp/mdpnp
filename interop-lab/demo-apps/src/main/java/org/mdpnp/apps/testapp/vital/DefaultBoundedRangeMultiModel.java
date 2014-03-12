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
package org.mdpnp.apps.testapp.vital;

import java.util.Arrays;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Jeff Plourde
 *
 */
public class DefaultBoundedRangeMultiModel implements BoundedRangeMultiModel {

    private int minimum = 0, maximum = 100;
    private Float[] values = new Float[] { 0f, 25f, 50f, 75f };
    private boolean valueIsAdjusting = false;
    private ChangeListener[] listeners = new ChangeListener[2];
    private int listenerCount = 0;

    @Override
    public int getMinimum() {
        return minimum;
    }

    @Override
    public void setMinimum(int newMinimum) {
        this.minimum = newMinimum;
        fireChangeEvent();
    }

    @Override
    public int getMaximum() {
        return maximum;
    }

    @Override
    public void setMaximum(int newMaximum) {
        this.maximum = newMaximum;
        fireChangeEvent();
    }

    @Override
    public Float getValue(int idx) {
        return values[idx];
    }

    @Override
    public void setValue(int idx, Float newValue) {
        Float left = idx <= 0 ? minimum : values[idx - 1];
        Float right = idx >= (values.length - 1) ? maximum : values[idx + 1];
        if (newValue < left) {
            newValue = left;
        }
        if (newValue > right) {
            newValue = right;
        }

        this.values[idx] = newValue;
        fireChangeEvent();
    }

    @Override
    public int getValueCount() {
        return this.values.length;
    }

    @Override
    public void setValueIsAdjusting(boolean b) {
        this.valueIsAdjusting = b;
        fireChangeEvent();
    }

    @Override
    public boolean getValueIsAdjusting() {
        return valueIsAdjusting;
    }

    @Override
    public synchronized void addChangeListener(ChangeListener x) {
        if (listeners.length < (listenerCount + 1)) {
            this.listeners = Arrays.copyOf(this.listeners, listenerCount + 1);
        }
        this.listeners[listenerCount++] = x;
    }

    @Override
    public synchronized void removeChangeListener(ChangeListener x) {
        int j = 0;
        int listenerCount = this.listenerCount;
        for (int i = 0; i < listenerCount; i++) {
            if (!listeners[i].equals(x)) {
                listeners[j++] = listeners[i];
            }
        }
        this.listenerCount = j;
        for (int i = j; i < this.listeners.length; i++) {
            this.listeners[i] = null;
        }
    }

    private final ChangeEvent changeEvent = new ChangeEvent(this);

    protected void fireChangeEvent() {
        int listenerCount;
        ChangeListener[] listeners;
        synchronized (this) {
            listenerCount = this.listenerCount;
            listeners = this.listeners;
        }
        for (int i = 0; i < listenerCount; i++) {
            listeners[i].stateChanged(changeEvent);
        }
    }

    public static void main(String[] args) {
        DefaultBoundedRangeMultiModel m = new DefaultBoundedRangeMultiModel();
        ChangeListener c1 = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                System.out.println("C1");
            }

        };
        ChangeListener c2 = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                System.out.println("C2");
            }

        };
        m.addChangeListener(c1);
        m.addChangeListener(c2);
        System.out.println(m.listenerCount + " " + Arrays.toString(m.listeners));
        m.fireChangeEvent();

        m.removeChangeListener(c1);
        System.out.println(m.listenerCount + " " + Arrays.toString(m.listeners));
        m.fireChangeEvent();
    }

    @Override
    public int getMarkerCount() {
        return 0;
    }

    @Override
    public Float getMarker(int idx) {
        return 0f;
    }
}
