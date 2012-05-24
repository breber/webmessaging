package com.brianreber.messaging.server;


import com.google.web.bindery.requestfactory.shared.Locator;


public class SmsOutgoingMessageLocator extends Locator<SmsOutgoingMessage, Void> {

	/* (non-Javadoc)
	 * @see com.google.web.bindery.requestfactory.shared.Locator#create(java.lang.Class)
	 */
	@Override
	public SmsOutgoingMessage create(Class<? extends SmsOutgoingMessage> clazz) {
		return new SmsOutgoingMessage();
	}

	/* (non-Javadoc)
	 * @see com.google.web.bindery.requestfactory.shared.Locator#find(java.lang.Class, java.lang.Object)
	 */
	@Override
	public SmsOutgoingMessage find(Class<? extends SmsOutgoingMessage> clazz, Void id) {
		return create(clazz);
	}

	@Override
	public Class<SmsOutgoingMessage> getDomainType() {
		return SmsOutgoingMessage.class;
	}

	@Override
	public Class<Void> getIdType() {
		return Void.class;
	}

	/* (non-Javadoc)
	 * @see com.google.web.bindery.requestfactory.shared.Locator#getId(java.lang.Object)
	 */
	@Override
	public Void getId(SmsOutgoingMessage domainObject) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.google.web.bindery.requestfactory.shared.Locator#getVersion(java.lang.Object)
	 */
	@Override
	public Object getVersion(SmsOutgoingMessage domainObject) {
		return null;
	}

}
