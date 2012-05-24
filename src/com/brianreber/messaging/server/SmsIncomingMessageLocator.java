package com.brianreber.messaging.server;


import com.google.web.bindery.requestfactory.shared.Locator;


public class SmsIncomingMessageLocator extends Locator<SmsIncomingMessage, Void> {

	/* (non-Javadoc)
	 * @see com.google.web.bindery.requestfactory.shared.Locator#create(java.lang.Class)
	 */
	@Override
	public SmsIncomingMessage create(Class<? extends SmsIncomingMessage> clazz) {
		return new SmsIncomingMessage();
	}

	/* (non-Javadoc)
	 * @see com.google.web.bindery.requestfactory.shared.Locator#find(java.lang.Class, java.lang.Object)
	 */
	@Override
	public SmsIncomingMessage find(Class<? extends SmsIncomingMessage> clazz, Void id) {
		return create(clazz);
	}

	@Override
	public Class<SmsIncomingMessage> getDomainType() {
		return SmsIncomingMessage.class;
	}

	@Override
	public Class<Void> getIdType() {
		return Void.class;
	}

	/* (non-Javadoc)
	 * @see com.google.web.bindery.requestfactory.shared.Locator#getId(java.lang.Object)
	 */
	@Override
	public Void getId(SmsIncomingMessage domainObject) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.google.web.bindery.requestfactory.shared.Locator#getVersion(java.lang.Object)
	 */
	@Override
	public Object getVersion(SmsIncomingMessage domainObject) {
		return null;
	}

}
