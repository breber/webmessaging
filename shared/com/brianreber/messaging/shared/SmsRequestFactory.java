package com.brianreber.messaging.shared;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.RequestFactory;
import com.google.web.bindery.requestfactory.shared.ServiceName;

public interface SmsRequestFactory extends RequestFactory {

	@ServiceName("org.reber.messaging.server.SmsService")
	public interface SmsRequest extends RequestContext {
		Request<SmsOutgoingMessageProxy> findOutgoing(Long id);
		Request<SmsIncomingMessageProxy> findIncoming(Long id);
		Request<MmsIncomingMessageProxy> findIncomingMms(Long id);
		Request<SmsOutgoingMessageProxy> updateOutgoing(SmsOutgoingMessageProxy item, boolean sendC2DM);
		Request<SmsIncomingMessageProxy> updateIncoming(SmsIncomingMessageProxy item);
		Request<MmsIncomingMessageProxy> updateIncoming(MmsIncomingMessageProxy item);
		Request<Void> deleteOutgoing(SmsOutgoingMessageProxy item);
		Request<Void> deleteIncoming(SmsIncomingMessageProxy item);
		Request<Void> deleteIncoming(MmsIncomingMessageProxy item);
		Request<List<IncomingOutgoingMessageProxy>> queryRecipients();
		Request<List<IncomingOutgoingMessageProxy>> queryConversation(String otherUser, int end);
		Request<List<SmsOutgoingMessageProxy>> queryOutgoing(int end);
		Request<List<SmsIncomingMessageProxy>> queryIncoming(int end);
		Request<List<MmsIncomingMessageProxy>> queryIncomingMms();
		Request<List<SmsOutgoingMessageProxy>> queryOutgoingByRecipient(String recipient, int end);
		Request<List<SmsIncomingMessageProxy>> queryIncomingByRecipient(String recipient, int end);
		Request<List<MmsIncomingMessageProxy>> queryIncomingMmsByRecipient(String recipient);
		Request<Void> deleteConversation(String recipient);
		Request<String> getContactName(String address);
		Request<String> getContactPhotoLink(String address);
		Request<String> getMyPhotoLink();
	}

	SmsRequest smsRequest();

	@ServiceName("org.reber.messaging.server.SmsService")
	public interface UserRequest extends RequestContext {
		Request<String> getLoggedInUser();
	}

	UserRequest userRequest();
}
