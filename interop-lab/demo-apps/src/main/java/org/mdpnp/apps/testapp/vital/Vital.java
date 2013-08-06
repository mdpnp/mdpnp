package org.mdpnp.apps.testapp.vital;

public interface Vital {
    String getLabel();
    String getUnits();
    int[] getNames();
    
    float getMinimum();
    float getMaximum();
    float getLow();
    float getHigh();
    float getDisplayMaximum();
    float getDisplayMinimum();
    String getLabelMinimum();
    String getLabelMaximum();
    
    void setLow(float low);
    void setHigh(float high);
    java.util.List<Value> getValues();
    VitalModel getParent();
    boolean isAnyOutOfBounds();
    int countOutOfBounds();
    
    boolean isIgnoreZero();
    void setIgnoreZero(boolean ignoreZero);
}
