package org.mdpnp.apps.testapp.vital;

import javax.swing.event.ChangeListener;

public interface BoundedRangeMultiModel {
    int getMinimum();

    void setMinimum(int newMinimum);

    int getMaximum();

    void setMaximum(int newMaximum);

    Float getValue(int idx);

    void setValue(int idx, Float newValue);
    
    int getValueCount();
    
    int getMarkerCount();
    
    Float getMarker(int idx);

    void setValueIsAdjusting(boolean b);

    boolean getValueIsAdjusting();

    void addChangeListener(ChangeListener x);

    void removeChangeListener(ChangeListener x);

}
