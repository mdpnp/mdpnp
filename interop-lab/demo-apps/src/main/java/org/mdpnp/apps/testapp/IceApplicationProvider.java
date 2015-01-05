package org.mdpnp.apps.testapp;

import org.springframework.context.ApplicationContext;

public interface IceApplicationProvider {

    AppType getAppType(); // MIKEF remove - backward compatibility
    IceAppsContainer.IceApp create(ApplicationContext context);
}
