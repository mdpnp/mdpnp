package org.mdpnp.apps.testapp.vital;

import javax.swing.event.ChangeListener;

public interface BoundedRangeMultiModel {
    int getMinimum();

    void setMinimum(int newMinimum);

    int getMaximum();

    void setMaximum(int newMaximum);

    int getValue(int idx);

    void setValue(int idx, int newValue);
    
    int getValueCount();
    
    int getMarkerCount();
    
    int getMarker(int idx);

    void setValueIsAdjusting(boolean b);

    boolean getValueIsAdjusting();

    void addChangeListener(ChangeListener x);

    void removeChangeListener(ChangeListener x);

}
