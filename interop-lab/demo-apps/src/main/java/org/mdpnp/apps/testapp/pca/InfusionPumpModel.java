package org.mdpnp.apps.testapp.pca;


import javafx.beans.property.*;

public class InfusionPumpModel {

    private static final String DEFAULT_INTERLOCK_TEXT = "";

    private StringProperty interlockText = new SimpleStringProperty(this, "interlockText", DEFAULT_INTERLOCK_TEXT);
    private BooleanProperty interlock = new SimpleBooleanProperty(this, "interlock", false);


    public final void stopInfusion(String str) {
        if (!interlock.get()) {
            interlock.set(true);
            interlockText.set(str);
        }
    }

    /**
     * Has the infusion been stopped?
     */
    public ReadOnlyBooleanProperty isInfusionStoppedProperty() {
        return interlock;
    }
    public boolean isInfusionStopped() {
        return interlock.get();
    }

    /**
     * Reset the infusion pump interlock
     */
    public void resetInfusion() {
        interlock.set(false);
        interlockText.set(DEFAULT_INTERLOCK_TEXT);
    }

    /**
     * Why has the infusion been stopped
     *
     * @return
     */
    public ReadOnlyStringProperty interlockTextProperty() {
        return interlockText;
    }
    public String getInterlockText() {
        return interlockText.get();
    }
}
