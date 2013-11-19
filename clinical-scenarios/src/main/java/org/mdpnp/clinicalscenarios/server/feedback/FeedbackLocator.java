package org.mdpnp.clinicalscenarios.server.feedback;

import static org.mdpnp.clinicalscenarios.server.OfyService.ofy;

import com.google.web.bindery.requestfactory.shared.Locator;

public class FeedbackLocator extends Locator<Feedback, Long>{

	@Override
	public Feedback create(Class<? extends Feedback> clazz) {
		Feedback fb = new Feedback();
	    ofy().save().entity(fb).now();
		return fb;
	}

	@Override
	public Feedback find(Class<? extends Feedback> clazz, Long id) {
		return ofy().load().type(clazz).id(id).now();
	}

	@Override
	public Class<Feedback> getDomainType() {
		return Feedback.class;
	}

	@Override
	public Long getId(Feedback domainObject) {
		return domainObject.getId();
	}

	@Override
	public Class<Long> getIdType() {
		return Long.class;
	}

	@Override
	public Object getVersion(Feedback domainObject) {
		return domainObject.getVersion();
	}

}
