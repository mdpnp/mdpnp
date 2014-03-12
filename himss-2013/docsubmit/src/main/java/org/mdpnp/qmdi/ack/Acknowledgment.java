package org.mdpnp.qmdi.ack;

import java.util.Date;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class Acknowledgment implements java.io.Serializable {
	private String documentId;
	private String userId;
	private Date timestamp;

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	@Override
	public String toString() {
		return "[documentId="+documentId+",userId="+userId+",timestamp="+timestamp+"]";
	}
}