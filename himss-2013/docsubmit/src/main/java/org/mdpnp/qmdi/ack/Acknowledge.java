package org.mdpnp.qmdi.ack;

import org.restlet.resource.ClientResource;

/**
 * Example app that submits a new document acknowledgment
 * @author jplourde
 *
 */
public class Acknowledge {
	public static void main(String[] args) {
		String url = "http://localhost:8083/acknowledgment";
		if (args.length > 0) {
			url = args[0];
		}
		ClientResource cr = new ClientResource(url);
		
		AcknowledgmentResource ackResource = cr.wrap(AcknowledgmentResource.class);
		
		String ack = "Test 1,User 1,"+new java.util.Date();

		//		Acknowledgment ack = new Acknowledgment();
		//		ack.setDocumentId("Test 1");
		//		ack.setUserId("User 1");
		//		ack.setTimestamp(new Date());
		
		if (ackResource.submit(ack)) {
			System.out.println("Acknowledgment Submitted");
		} else {
			System.out.println("Acknowledgment Failed");
		}
	}
}