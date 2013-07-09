package org.mdpnp.clinicalscenarios.server.tag;

import static org.mdpnp.clinicalscenarios.server.OfyService.ofy;

import com.google.web.bindery.requestfactory.shared.Locator;


public class TagLocator extends Locator<Tag, Long>  {


	@Override
	public Tag create(Class<? extends Tag> clazz) {
	    Tag tag = new Tag();
	    ofy().save().entity(tag).now();
	    return tag;
	}

	@Override
	public Tag find(Class<? extends Tag> clazz, Long id) {
		 return ofy().load().type(clazz).id(id).now();
	}

	@Override
	public Class<Tag> getDomainType() {
		return Tag.class;
	}

	@Override
	public Long getId(Tag domainObject) {
		return domainObject.getId();
	}

	@Override
	public Class<Long> getIdType() {
		return Long.class;
	}


	@Override
	public Object getVersion(Tag domainObject) {
		return domainObject.getVersion();
	}

}
