package org.mdpnp.apps.testapp.pump;

public interface PumpModelListener {
    void pumpAdded(PumpModel model, Pump pump);
    void pumpRemoved(PumpModel model, Pump pump);
    void pumpChanged(PumpModel model, Pump pump);
}
