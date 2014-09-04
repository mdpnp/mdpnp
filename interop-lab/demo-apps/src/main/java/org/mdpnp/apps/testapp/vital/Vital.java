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


public interface Vital {
    /**
     * A String describing this vital sign
     * 
     * @return
     */
    String getLabel();

    /**
     * Units in which this vital sign is measured
     * 
     * @return
     */
    String getUnits();

    /**
     * Nomenclature identifiers mapped to this vital sign
     * 
     * @return
     */
    String[] getMetricIds();

    /**
     * The minimum value in the domain of values for this vital sign
     * 
     * @return
     */
    float getMinimum();

    /**
     * The maximum value in the domain of values for this vital sign
     * 
     * @return
     */
    float getMaximum();

    Long getValueMsWarningLow();

    Long getValueMsWarningHigh();

    /**
     * A Value at or below this warning low will generate a warning Further
     * heuristics may turn warnings into alarms under some conditions
     * 
     * @return
     */
    Float getWarningLow();

    /**
     * A Value at or above this warning high will generate a warning Further
     * heuristics may turn warnings into alarms under some conditions
     * 
     * @return
     */
    Float getWarningHigh();

    /**
     * A Value at or below this critical low will generate an alarm
     * 
     * @return
     */
    Float getCriticalLow();

    /**
     * A Value at or above this critical high will generate an alarm
     * 
     * @return
     */
    Float getCriticalHigh();

    /**
     * Used for display purposes (warning high - warning low) / 2 + warning high
     * 
     * @return
     */
    float getDisplayMaximum();

    /**
     * Used for display purposes warning low - (warning high - warning low) / 2
     * 
     * @return
     */
    float getDisplayMinimum();

    /**
     * The display minimum formatted as a string and filtering out values below
     * the minimum
     * 
     * @return
     */
    String getLabelMinimum();

    /**
     * the display maximum formatted as a string and filtering out values above
     * the maximum
     * 
     * @return
     */
    String getLabelMaximum();

    /**
     * No values for a Vital generates a warning
     * 
     * @return
     */
    boolean isNoValueWarning();

    void setNoValueWarning(boolean noValueWarning);

    /**
     * milliseconds after which a persistent warning becomes an alarm
     * 
     * @return
     */
    long getWarningAgeBecomesAlarm();

    void setWarningAgeBecomesAlarm(long warningAgeBecomesAlarm);

    void destroy();

    void setWarningLow(Float low);

    void setWarningHigh(Float high);

    void setCriticalLow(Float low);

    void setCriticalHigh(Float high);

    void setValueMsWarningLow(Long low);

    void setValueMsWarningHigh(Long high);

    java.util.List<Value> getValues();

    VitalModel getParent();

    boolean isAnyOutOfBounds();

    int countOutOfBounds();

    boolean isIgnoreZero();

    void setIgnoreZero(boolean ignoreZero);
}
