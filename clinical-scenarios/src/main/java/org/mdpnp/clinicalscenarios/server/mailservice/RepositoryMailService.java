package org.mdpnp.clinicalscenarios.server.mailservice;

import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class RepositoryMailService {
	
	public static final String ADMIN_GMAIL_ACCOUNT = "md.pnp.team@gmail.com";
	
	String host = "smtp.google.com";
	String to = "md.pnp.team@gmail.com";
	String from = "md.pnp.team@gmail.com";
	String subject = "Clinical Repository submission";
	String messageText = "Your scenario has been revised";
	boolean sessionDebug = false;
	   
	private Logger logger = Logger.getLogger(RepositoryMailService.class.getName());
	   
	public RepositoryMailService(String to, String subject, String messageText){
		this.to = to;
		this.subject = subject;
		this.messageText = messageText;
	}
	
	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}



	public void send() /*throws UnsupportedEncodingException*/{
		try {
			// Create some properties and get the default Session.
			Properties props = System.getProperties();
			props.put("mail.host", host);
			props.put("mail.transport.protocol", "smtp");
			Session mailSession = Session.getDefaultInstance(props, null);
	
			// Set debug on the Session
			// Passing false will not echo debug info, and passing True will.
			mailSession.setDebug(sessionDebug);
	
			// Instantiate a new MimeMessage and fill it with the 
			// required information.
			Message msg = new MimeMessage(mailSession);
			msg.setFrom(new InternetAddress(from));
			InternetAddress[] address = { new InternetAddress(to) };
			msg.setRecipients(Message.RecipientType.TO, address);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			msg.setText(messageText);
	
			// Hand the message to the default transport service for delivery.
			Transport.send(msg);
//			logger.info("Email sent to"+to+" w/ subject "+subject+" MSG "+messageText);
		
    
        } catch (AddressException e) {
            // ...
        	e.printStackTrace();
        } catch (MessagingException e) {
            // ...
        	e.printStackTrace();
        }
	}

}
