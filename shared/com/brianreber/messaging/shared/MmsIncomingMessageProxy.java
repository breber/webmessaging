package com.brianreber.messaging.shared;

import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyForName(value = "org.reber.messaging.server.MmsIncomingMessage", locator = "org.reber.messaging.server.MmsIncomingMessageLocator")
public interface MmsIncomingMessageProxy extends ValueProxy {
	Long getId();
	void setId(Long id);
	String getUserId();
	void setUserId(String userId);
	String getEmailAddress();
	void setEmailAddress(String emailAddress);
	String getSender();
	void setSender(String sender);
	String getMessageText();
	void setMessageText(String messageText);
	String getBlobKey();
	void setBlobKey(String blobKey);
	Long getMessageDate();
	void setMessageDate(Long messageDate);
}
