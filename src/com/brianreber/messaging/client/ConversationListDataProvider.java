package com.brianreber.messaging.client;

import java.util.List;

import com.brianreber.messaging.shared.IncomingOutgoingMessageProxy;
import com.brianreber.messaging.shared.SmsRequestFactory;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.web.bindery.requestfactory.shared.Receiver;

/**
 * An asynchronous data provider for getting conversation names
 * 
 * @author breber
 */
public class ConversationListDataProvider extends AsyncDataProvider<IncomingOutgoingMessageProxy> {

	private final SmsRequestFactory requestFactory;

	public ConversationListDataProvider(SmsRequestFactory requestFactory) {
		this.requestFactory = requestFactory;
	}

	public void updateData(HasData<IncomingOutgoingMessageProxy> display) {
		onRangeChanged(display);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(final HasData<IncomingOutgoingMessageProxy> display) {
		requestFactory.smsRequest().queryRecipients().fire(new Receiver<List<IncomingOutgoingMessageProxy>>() {
			@Override
			public void onSuccess(List<IncomingOutgoingMessageProxy> items) {
				int start = display.getVisibleRange().getStart();
				int end = start + display.getVisibleRange().getLength();
				end = end >= items.size() ? items.size() : end;

				updateRowData(start, items.subList(start, end));
				updateRowCount(items.size(), true);
			}
		});
	}
}
