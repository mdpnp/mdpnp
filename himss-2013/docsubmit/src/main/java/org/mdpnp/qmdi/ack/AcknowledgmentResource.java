package org.mdpnp.qmdi.ack;

import org.restlet.resource.Post;

public interface AcknowledgmentResource {
    @Post
    boolean submit(String ack);

}