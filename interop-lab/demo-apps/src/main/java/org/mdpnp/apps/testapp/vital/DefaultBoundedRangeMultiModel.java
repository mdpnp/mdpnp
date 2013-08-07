package org.mdpnp.apps.testapp.vital;

import java.util.Arrays;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DefaultBoundedRangeMultiModel implements BoundedRangeMultiModel {

    private int minimum = 0, maximum = 100;
    private Float[] values = new Float[] {0f,25f,50f,75f};
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
        Float left = idx <= 0 ? minimum : values[idx-1];
        Float right = idx>=(values.length-1)?maximum:values[idx+1];
        if(newValue < left) {
            newValue = left;
        }
        if(newValue > right) {
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
        if(listeners.length < (listenerCount+1)) {
            this.listeners = Arrays.copyOf(this.listeners, listenerCount+1);
        }
        this.listeners[listenerCount++] = x;
    }

    @Override
    public synchronized void removeChangeListener(ChangeListener x) {
        int j = 0;
        int listenerCount = this.listenerCount;
        for(int i = 0; i < listenerCount; i++) {
            if(!listeners[i].equals(x)) {
                listeners[j++] = listeners[i];
            }
        }
        this.listenerCount = j;
        for(int i = j; i < this.listeners.length; i++) {
            this.listeners[i] = null;
        }
    }
    
    private final ChangeEvent changeEvent = new ChangeEvent(this);
    protected void fireChangeEvent() {
        int listenerCount;
        ChangeListener[] listeners;
        synchronized(this) {
            listenerCount = this.listenerCount;
            listeners = this.listeners;
        }
        for(int i = 0; i < listenerCount; i++) {
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
