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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableList;


public interface Vital extends ObservableList<Value> {
    /**
     * A String describing this vital sign
     * 
     * @return
     */
    ReadOnlyStringProperty labelProperty();
    String getLabel();

    /**
     * Units in which this vital sign is measured
     * 
     * @return
     */
    ReadOnlyStringProperty unitsProperty();
    String getUnits();

    /**
     * Nomenclature identifiers mapped to this vital sign
     * 
     * @return
     */
    ReadOnlyObjectProperty<String[]> metricIdsProperty();
    String[] getMetricIds();

    /**
     * The minimum value in the domain of values for this vital sign
     * 
     * @return
     */
    ReadOnlyDoubleProperty minimumProperty();
    double getMinimum();

    /**
     * The maximum value in the domain of values for this vital sign
     * 
     * @return
     */
    ReadOnlyDoubleProperty maximumProperty();
    double getMaximum();

    ObjectProperty<Long> valueMsWarningLowProperty();
    Long getValueMsWarningLow();

    ObjectProperty<Long> valueMsWarningHighProperty();
    Long getValueMsWarningHigh();

    /**
     * A Value at or below this warning low will generate a warning Further
     * heuristics may turn warnings into alarms under some conditions
     * 
     * @return
     */
    ObjectProperty<Double> warningLowProperty();
    Double getWarningLow();

    /**
     * A Value at or above this warning high will generate a warning Further
     * heuristics may turn warnings into alarms under some conditions
     * 
     * @return
     */
    ObjectProperty<Double> warningHighProperty();
    Double getWarningHigh();

    /**
     * A Value at or below this critical low will generate an alarm
     * 
     * @return
     */
    ObjectProperty<Double> criticalLowProperty();
    Double getCriticalLow();

    /**
     * A Value at or above this critical high will generate an alarm
     * 
     * @return
     */
    ObjectProperty<Double> criticalHighProperty();
    Double getCriticalHigh();

    /**
     * Used for display purposes (warning high - warning low) / 2 + warning high
     * 
     * @return
     */
    ReadOnlyDoubleProperty displayMaximumProperty();
    double getDisplayMaximum();

    /**
     * Used for display purposes warning low - (warning high - warning low) / 2
     * 
     * @return
     */
    ReadOnlyDoubleProperty displayMinimumProperty();
    double getDisplayMinimum();

    /**
     * The display minimum formatted as a string and filtering out values below
     * the minimum
     * 
     * @return
     */
    ReadOnlyStringProperty labelMinimumProperty();
    String getLabelMinimum();

    /**
     * the display maximum formatted as a string and filtering out values above
     * the maximum
     * 
     * @return
     */
    ReadOnlyStringProperty labelMaximumProperty();
    String getLabelMaximum();

    /**
     * No values for a Vital generates a warning
     * 
     * @return
     */
    BooleanProperty noValueWarningProperty();
    boolean isNoValueWarning();

    void setNoValueWarning(boolean noValueWarning);

    /**
     * milliseconds after which a persistent warning becomes an alarm
     * 
     * @return
     */
    LongProperty warningAgeBecomesAlarmProperty();
    long getWarningAgeBecomesAlarm();

    void setWarningAgeBecomesAlarm(long warningAgeBecomesAlarm);

    void destroy();

    void setWarningLow(Double low);

    void setWarningHigh(Double high);

    void setCriticalLow(Double low);

    void setCriticalHigh(Double high);

    void setValueMsWarningLow(Long low);

    void setValueMsWarningHigh(Long high);

    VitalModel getParent();

    ReadOnlyBooleanProperty anyOutOfBoundsProperty();
    boolean isAnyOutOfBounds();

    ReadOnlyIntegerProperty countOutOfBoundsProperty();
    int countOutOfBounds();

    BooleanProperty ignoreZeroProperty();
    boolean isIgnoreZero();
    void setIgnoreZero(boolean ignoreZero);
    
    BooleanProperty requiredProperty();
    boolean isRequired();
    void setRequired(boolean required);

    /**
     * Vitals are attached to a VitalModel that has a state. The transition between states
     * is determined by the combination of states of the all vitals that make up that model.
     * If a collection of vitals is modeling a particular scenario, an alarm condition could
     * be triggered when a particular parameter is in the 'normal' range.
     */
    ReadOnlyObjectProperty<VitalModel.State> modelStateTransitionConditionProperty();
    VitalModel.State getModelStateTransitionCondition();
    void setModelStateTransitionCondition(VitalModel.State v);

}
