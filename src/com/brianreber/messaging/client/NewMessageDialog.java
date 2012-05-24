package com.brianreber.messaging.client;

import java.util.Date;

import com.brianreber.messaging.shared.SmsOutgoingMessageProxy;
import com.brianreber.messaging.shared.SmsRequestFactory;
import com.brianreber.messaging.shared.SmsRequestFactory.SmsRequest;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.requestfactory.shared.Receiver;

/**
 * The dialog that allows the user to start a new conversation
 * 
 * @author breber
 */
public class NewMessageDialog extends DialogBox {

	private TextBox inputMessage;
	private TextBox inputRecipient;
	private Label labelMessage;
	private Label labelRecipient;

	public NewMessageDialog(final SmsRequestFactory requestFactory) {
		setGlassEnabled(true);

		setText("New Message");

		Panel layout = new VerticalPanel();

		labelMessage = new Label("Message:");
		inputMessage = new TextBox();
		inputMessage.getElement().setPropertyString("placeholder", "Message");
		labelRecipient = new Label("Recipient:");
		inputRecipient = new TextBox();
		inputRecipient.getElement().setPropertyString("placeholder", "Recipient");

		layout.add(labelRecipient);
		layout.add(inputRecipient);

		layout.add(labelMessage);
		layout.add(inputMessage);

		final Button ok = new Button("Send");
		ok.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				boolean canAdd = inputMessage.getValue() != "";
				canAdd = canAdd && inputMessage.getValue() != "";

				if (canAdd) {
					SmsRequest req = requestFactory.smsRequest();

					SmsOutgoingMessageProxy msg = req.create(SmsOutgoingMessageProxy.class);
					msg.setMessageDate(new Date().getTime());
					msg.setMessageText(inputMessage.getValue());
					msg.setRecipient(inputRecipient.getValue());

					inputMessage.setEnabled(false);
					ok.setEnabled(false);

					req.updateOutgoing(msg, true).fire(new Receiver<SmsOutgoingMessageProxy>() {
						@Override
						public void onSuccess(SmsOutgoingMessageProxy response) {
							NewMessageDialog.this.hide();
						}
					});
				}
			}
		});
		ok.setWidth("100%");
		ok.setStyleName("margin-tb");

		Button cancel = new Button("Cancel");
		cancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				NewMessageDialog.this.hide();
			}
		});

		cancel.setWidth("100%");
		cancel.setStyleName("margin-tb");

		layout.add(ok);
		layout.add(cancel);

		setWidget(layout);
		setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				int left = ((Window.getClientWidth() - offsetWidth) / 2) >> 0;
				int top = ((Window.getClientHeight() - offsetHeight) / 2) >> 0;
				setPopupPosition(left, top);
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.DialogBox#onBrowserEvent(com.google.gwt.user.client.Event)
	 */
	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);

		int left = ((Window.getClientWidth() - this.getOffsetWidth()) / 2) >> 0;
		int top = ((Window.getClientHeight() - this.getOffsetHeight()) / 2) >> 0;
		setPopupPosition(left, top);
	}
}
