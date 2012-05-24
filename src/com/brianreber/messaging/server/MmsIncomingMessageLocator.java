package com.brianreber.messaging.server;


import com.google.web.bindery.requestfactory.shared.Locator;


public class MmsIncomingMessageLocator extends Locator<MmsIncomingMessage, Void> {

	/* (non-Javadoc)
	 * @see com.google.web.bindery.requestfactory.shared.Locator#create(java.lang.Class)
	 */
	@Override
	public MmsIncomingMessage create(Class<? extends MmsIncomingMessage> clazz) {
		return new MmsIncomingMessage();
	}

	/* (non-Javadoc)
	 * @see com.google.web.bindery.requestfactory.shared.Locator#find(java.lang.Class, java.lang.Object)
	 */
	@Override
	public MmsIncomingMessage find(Class<? extends MmsIncomingMessage> clazz, Void id) {
		return create(clazz);
	}

	@Override
	public Class<MmsIncomingMessage> getDomainType() {
		return MmsIncomingMessage.class;
	}

	@Override
	public Class<Void> getIdType() {
		return Void.class;
	}

	/* (non-Javadoc)
	 * @see com.google.web.bindery.requestfactory.shared.Locator#getId(java.lang.Object)
	 */
	@Override
	public Void getId(MmsIncomingMessage domainObject) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.google.web.bindery.requestfactory.shared.Locator#getVersion(java.lang.Object)
	 */
	@Override
	public Object getVersion(MmsIncomingMessage domainObject) {
		return null;
	}

}
