package org.mdpnp.clinicalscenarios.client.feedback;

import com.google.web.bindery.requestfactory.shared.InstanceRequest;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

@ServiceName("org.mdpnp.clinicalscenarios.server.feedback.Feedback")
public interface FeedbackRequest extends RequestContext{

	Request<FeedbackProxy> create();
	InstanceRequest<FeedbackProxy, FeedbackProxy> persist();
}
