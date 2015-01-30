package org.mdpnp.apps.testapp.export;

import org.mdpnp.apps.testapp.vital.Value;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;

import java.util.List;


public class MockedVital implements Vital {

    public MockedVital() {

    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public String getUnits() {
        return null;
    }

    @Override
    public String[] getMetricIds() {
        return new String[0];
    }

    @Override
    public float getMinimum() {
        return 0;
    }

    @Override
    public float getMaximum() {
        return 0;
    }

    @Override
    public Long getValueMsWarningLow() {
        return null;
    }

    @Override
    public Long getValueMsWarningHigh() {
        return null;
    }

    @Override
    public Float getWarningLow() {
        return (float)0.0;
    }

    @Override
    public Float getWarningHigh() {
        return (float)0.0;
    }

    @Override
    public Float getCriticalLow() {
        return null;
    }

    @Override
    public Float getCriticalHigh() {
        return null;
    }

    @Override
    public float getDisplayMaximum() {
        return 0;
    }

    @Override
    public float getDisplayMinimum() {
        return 0;
    }

    @Override
    public String getLabelMinimum() {
        return null;
    }

    @Override
    public String getLabelMaximum() {
        return null;
    }

    @Override
    public boolean isNoValueWarning() {
        return false;
    }

    @Override
    public void setNoValueWarning(boolean noValueWarning) {

    }

    @Override
    public long getWarningAgeBecomesAlarm() {
        return 0;
    }

    @Override
    public void setWarningAgeBecomesAlarm(long warningAgeBecomesAlarm) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void setWarningLow(Float low) {

    }

    @Override
    public void setWarningHigh(Float high) {

    }

    @Override
    public void setCriticalLow(Float low) {

    }

    @Override
    public void setCriticalHigh(Float high) {

    }

    @Override
    public void setValueMsWarningLow(Long low) {

    }

    @Override
    public void setValueMsWarningHigh(Long high) {

    }

    @Override
    public List<Value> getValues() {
        return null;
    }

    @Override
    public VitalModel getParent() {
        return null;
    }

    @Override
    public boolean isAnyOutOfBounds() {
        return false;
    }

    @Override
    public int countOutOfBounds() {
        return 0;
    }

    @Override
    public boolean isIgnoreZero() {
        return false;
    }

    @Override
    public void setIgnoreZero(boolean ignoreZero) {

    }
}
