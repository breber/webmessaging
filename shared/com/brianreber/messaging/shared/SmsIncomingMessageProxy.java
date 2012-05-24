package com.brianreber.messaging.shared;

import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyForName(value = "org.reber.messaging.server.SmsIncomingMessage", locator = "org.reber.messaging.server.SmsIncomingMessageLocator")
public interface SmsIncomingMessageProxy extends ValueProxy {
	public Long getId();
	public void setId(Long id);
	public String getUserId();
	public void setUserId(String userId);
	public String getEmailAddress();
	public void setEmailAddress(String emailAddress);
	public String getMessageText();
	public void setMessageText(String messageText);
	public String getSender();
	public void setSender(String sender);
	public Long getMessageDate();
	public void setMessageDate(Long date);
}
