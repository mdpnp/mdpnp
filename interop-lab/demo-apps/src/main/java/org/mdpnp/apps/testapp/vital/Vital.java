package org.mdpnp.apps.testapp.vital;

public interface Vital {
    /**
     * A String describing this vital sign
     * @return
     */
    String getLabel();
    
    /**
     * Units in which this vital sign is measured
     * @return
     */
    String getUnits();
    
    /**
     * Nomenclature identifiers mapped to this vital sign
     * @return
     */
    int[] getNames();
    
    /**
     * The minimum value in the domain of values for this vital sign
     * @return
     */
    float getMinimum();
    
    /**
     * The maximum value in the domain of values for this vital sign 
     * @return
     */
    float getMaximum();
    
    /**
     * A Value at or below this warning low will generate a warning
     * Further heuristics may turn warnings into alarms under some conditions
     * @return
     */
    float getWarningLow();
    
    /**
     * A Value at or above this warning high will generate a warning
     * Further heuristics may turn warnings into alarms under some conditions
     * @return
     */
    float getWarningHigh();
    
    /**
     * A Value at or below this critical low will generate an alarm
     * @return
     */
    float getCriticalLow();
    
    /**
     * A Value at or above this critical high will generate an alarm
     * @return
     */
    float getCriticalHigh();
    
    /**
     * Used for display purposes
     * (warning high - warning low) / 2 + warning high
     * @return
     */
    float getDisplayMaximum();
    
    /**
     * Used for display purposes
     * warning low - (warning high - warning low) / 2
     * @return
     */
    float getDisplayMinimum();
    
    /**
     * The display minimum formatted as a string and filtering out values
     * below the minimum
     * @return
     */
    String getLabelMinimum();
    
    /**
     * the display maximum formatted as a string and filtering out values
     * above the maximum
     * @return
     */
    String getLabelMaximum();
    
    /**
     * No values for a Vital generates a warning
     * @return
     */
    boolean isNoValueWarning();
    
    void setNoValueWarning(boolean noValueWarning);
    
    /**
     * milliseconds after which a persistent warning becomes an alarm
     * @return
     */
    long getWarningAgeBecomesAlarm();
    
    void setWarningAgeBecomesAlarm(long warningAgeBecomesAlarm);
    
    
    void setWarningLow(float low);
    void setWarningHigh(float high);
    void setCriticalLow(float low);
    void setCriticalHigh(float high);
    java.util.List<Value> getValues();
    VitalModel getParent();
    boolean isAnyOutOfBounds();
    int countOutOfBounds();
    
    boolean isIgnoreZero();
    void setIgnoreZero(boolean ignoreZero);
}
