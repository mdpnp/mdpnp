package org.mdpnp.qmdi.ack;

import java.net.URL;

import org.restlet.Component;
import org.restlet.data.Protocol;
import org.restlet.resource.ServerResource;

/**
 * @author Jeff Plourde
 *
 */
public class Await extends ServerResource implements AcknowledgmentResource {
    public static void main(String[] args) throws Exception {
    	String url = "http://localhost:8083/acknowledgment";
    	
    	if(args.length > 0) {
    		url = args[0];
    	}
    	URL _url = new URL(url);
    	
    	if(!"http".equals(_url.getProtocol())) {
    		throw new IllegalArgumentException(_url.getProtocol() + " is not a supported protocol, try http");
    	}
    	if(_url.getPort() < 0) {
    		throw new IllegalArgumentException(_url.getPort() + " is not a valid port");
    	}
    	
    	Component component = new Component();
    	component.getServers().add(Protocol.HTTP, _url.getPort());
    	component.getDefaultHost().attach(_url.getPath(), Await.class);
    	component.start();
    }

	@Override
	public boolean submit(String ack) {
		System.out.println("Received an acknowledgment:"+ack);
		return true;
	}

}