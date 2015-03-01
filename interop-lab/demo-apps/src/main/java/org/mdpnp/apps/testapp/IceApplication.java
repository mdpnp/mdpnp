package org.mdpnp.apps.testapp;

public abstract class IceApplication extends javafx.application.Application {
    protected Configuration configuration;
    public void setConfiguration(Configuration config) {
        this.configuration = config;
    }
    public Configuration getConfiguration() {
        return configuration;
    }
}