package org.mdpnp.clinicalscenarios.server.scenario;

import static org.mdpnp.clinicalscenarios.server.OfyService.ofy;

import com.google.web.bindery.requestfactory.shared.Locator;

public class ScenarioLocator extends Locator<ScenarioEntity, Long>{
 
	@Override
	public ScenarioEntity create(Class<? extends ScenarioEntity> clazz) {
	    ScenarioEntity s = new ScenarioEntity();
	    ofy().save().entity(s).now();
	    return s;
	}

	@Override
	public ScenarioEntity find(Class<? extends ScenarioEntity> clazz, Long id) {
	    ScenarioEntity s = ofy().load().type(ScenarioEntity.class).id(id).now();
	    return s;
	}

	@Override
	public Class<ScenarioEntity> getDomainType() {
		return ScenarioEntity.class;
	}

	@Override
	public Long getId(ScenarioEntity domainObject) {
		return domainObject.getId();
	}

	@Override
	public Class<Long> getIdType() {
		return Long.class;
	}

	@Override
	public Object getVersion(ScenarioEntity domainObject) {
		return domainObject.getVersion();
	}

}
