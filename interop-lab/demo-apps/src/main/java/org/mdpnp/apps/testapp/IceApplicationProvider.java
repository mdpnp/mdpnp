package org.mdpnp.apps.testapp;

import org.springframework.context.ApplicationContext;

public interface IceApplicationProvider {

    IceAppsContainer.AppType getAppType();
    IceAppsContainer.IceApp create(ApplicationContext context);
}
