package com.brianreber.messaging.client;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.brianreber.messaging.shared.IncomingOutgoingMessageProxy;
import com.brianreber.messaging.shared.SmsRequestFactory;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.web.bindery.requestfactory.shared.Receiver;

/**
 * An asynchronous data provider for getting conversation names
 * 
 * @author breber
 */
public class ConversationDataProvider extends AsyncDataProvider<IncomingOutgoingMessageProxy> {

	private final SmsRequestFactory requestFactory;
	private final Set<IncomingOutgoingMessageProxy> conversations = new HashSet<IncomingOutgoingMessageProxy>();
	private final String userId;

	public ConversationDataProvider(SmsRequestFactory requestFactory, String userId) {
		this.requestFactory = requestFactory;
		this.userId = userId;
	}

	public void updateData(HasData<IncomingOutgoingMessageProxy> display) {
		onRangeChanged(display);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(final HasData<IncomingOutgoingMessageProxy> display) {
		conversations.clear();
		Range visibleRange = display.getVisibleRange();

		requestFactory.smsRequest().queryConversation(userId, visibleRange.getStart() + visibleRange.getLength()).fire(new Receiver<List<IncomingOutgoingMessageProxy>>() {
			@Override
			public void onSuccess(List<IncomingOutgoingMessageProxy> items) {
				int start = display.getVisibleRange().getStart();
				int end = start + display.getVisibleRange().getLength();
				end = end >= items.size() ? items.size() : end;

				updateRowData(start, items.subList(start, end));
				updateRowCount(items.size(), false);
			}
		});
	}

}
