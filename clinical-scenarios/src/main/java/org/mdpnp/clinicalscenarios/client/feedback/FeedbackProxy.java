package org.mdpnp.clinicalscenarios.client.feedback;

import org.mdpnp.clinicalscenarios.server.feedback.Feedback;
import org.mdpnp.clinicalscenarios.server.feedback.FeedbackLocator;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

@ProxyFor(value=Feedback.class,locator=FeedbackLocator.class)
public interface FeedbackProxy extends EntityProxy{
	
	public String getUsersEmail();
	public void setUsersEmail(String usersEmail);

	public String getUsersFeedback();
	public void setUsersFeedback(String usersFeedback);

}
