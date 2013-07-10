package org.mdpnp.clinicalscenarios.client.tag;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.InstanceRequest;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;


@ServiceName("org.mdpnp.clinicalscenarios.server.tag.Tag")
public interface TagRequest extends RequestContext {	
	Request<List<TagProxy>> findAll();
	Request<TagProxy> create();
	InstanceRequest<TagProxy, TagProxy> persist();
	InstanceRequest<TagProxy, Void> remove();

}

