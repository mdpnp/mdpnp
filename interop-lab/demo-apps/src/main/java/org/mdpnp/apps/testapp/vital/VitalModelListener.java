package org.mdpnp.apps.testapp.vital;

public interface VitalModelListener {
    void vitalChanged(VitalModel model, Vital vital);
    void vitalRemoved(VitalModel model, Vital vital);
    void vitalAdded(VitalModel model, Vital vital);
}
