/*
 * Copyright 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.brianreber.messaging.server;


import com.google.web.bindery.requestfactory.shared.Locator;

public class IncomingOutgoingMessageLocator extends Locator<IncomingOutgoingMessage, Void> {

	@Override
	public IncomingOutgoingMessage create(Class<? extends IncomingOutgoingMessage> clazz) {
		return new IncomingOutgoingMessage();
	}

	@Override
	public IncomingOutgoingMessage find(Class<? extends IncomingOutgoingMessage> clazz, Void id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Class<IncomingOutgoingMessage> getDomainType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Void getId(IncomingOutgoingMessage domainObject) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Class<Void> getIdType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getVersion(IncomingOutgoingMessage domainObject) {
		throw new UnsupportedOperationException();
	}
}
