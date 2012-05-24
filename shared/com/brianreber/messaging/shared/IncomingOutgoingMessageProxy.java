package com.brianreber.messaging.shared;

import java.util.Date;

import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

/**
 * A wrapper class for display on the UI.
 * 
 * @author breber
 */
@ProxyForName(value = "org.reber.messaging.server.IncomingOutgoingMessage", locator = "org.reber.messaging.server.IncomingOutgoingMessageLocator")
public interface IncomingOutgoingMessageProxy extends ValueProxy {
	public String getName();
	public void setName(String name);
	public String getMessageText();
	public void setMessageText(String messageText);
	public String getBlob();
	public void setBlob(String blob);
	public Date getMessageDate();
	public void setMessageDate(Date messageDate);
	public String getPhotoUrl();
	public void setPhotoUrl(String photoUrl);
}
