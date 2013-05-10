package org.mdpnp.clinicalscenarios.server;

import org.mdpnp.clinicalscenarios.server.scenario.ScenarioEntity;
import org.mdpnp.clinicalscenarios.server.user.UserInfo;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

public class OfyService {
    static {
        factory().register(ScenarioEntity.class);
        factory().register(UserInfo.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
