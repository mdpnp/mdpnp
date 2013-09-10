package org.mdpnp.clinicalscenarios.server;

import org.mdpnp.clinicalscenarios.server.scenario.ScenarioEntity;
import org.mdpnp.clinicalscenarios.server.tag.Tag;
import org.mdpnp.clinicalscenarios.server.user.UserInfo;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

public class OfyService {
	//Register the Entities
    static {
        factory().register(ScenarioEntity.class);
        factory().register(UserInfo.class);
        factory().register(Tag.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
//    	return ObjectifyService.ofy().cache(false);
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
