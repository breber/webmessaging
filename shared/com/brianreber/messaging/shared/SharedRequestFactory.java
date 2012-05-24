package com.brianreber.messaging.shared;

import com.google.web.bindery.requestfactory.shared.InstanceRequest;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.RequestFactory;
import com.google.web.bindery.requestfactory.shared.ServiceName;

/**
 * Request factory used for registering and sending C2DM messages
 * 
 * @author breber
 */
public interface SharedRequestFactory extends RequestFactory {

	@ServiceName("org.reber.messaging.server.RegistrationInfo")
	public interface RegistrationInfoRequest extends RequestContext {
		InstanceRequest<RegistrationInfoProxy, Void> register();
		InstanceRequest<RegistrationInfoProxy, Void> unregister();
	}

	RegistrationInfoRequest registrationInfoRequest();

	@ServiceName("org.reber.messaging.server.Message")
	public interface MessageRequest extends RequestContext {
		InstanceRequest<MessageProxy, String> send();
	}

	MessageRequest messageRequest();
}
