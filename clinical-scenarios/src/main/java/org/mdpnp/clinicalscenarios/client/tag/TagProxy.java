package org.mdpnp.clinicalscenarios.client.tag;

import org.mdpnp.clinicalscenarios.server.tag.Tag;
import org.mdpnp.clinicalscenarios.server.tag.TagLocator;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

@ProxyFor(value=Tag.class,locator=TagLocator.class)
public interface TagProxy extends EntityProxy{
	
	public String getName();

	public void setName(String name);

	public String getDescription();

	public void setDescription(String description);
}
