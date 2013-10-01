package org.mdpnp.apps.testapp.co2;

public interface CapnoModelListener {
    void capnoAdded(CapnoModel model, Capno capno);
    void capnoRemoved(CapnoModel model, Capno capno);
    void capnoChanged(CapnoModel model, Capno capno);
}
