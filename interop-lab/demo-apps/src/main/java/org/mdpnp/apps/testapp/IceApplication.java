package org.mdpnp.apps.testapp;

public abstract class IceApplication extends javafx.application.Application {
    protected Configuration configuration;
    public void setConfiguration(Configuration config) {
        this.configuration = config;
    }
    public Configuration getConfiguration() {
        return configuration;
    }
    
    /**
     * A method to lock the application.  A typical implementation should find all the child windows/stages/whatever
     * that should be inaccessible during a "locked" state, and either hide them or at least block input.  The exact
     * implementation is left to the extending class.
     * 
     * If a subclass overrides this method, it MUST override isLocked() as well in order to give a correct reflection of
     * the lock state.
     */
    public void lockScreen() {
    	
    }
    
    /**
     * A method to unlock the application.  In general terms, reverse the actions taken in lockScreen.
     */
    public void unlockScreen() {
    	
    }
    
    public boolean isLocked() {
    	return false;
    }
}