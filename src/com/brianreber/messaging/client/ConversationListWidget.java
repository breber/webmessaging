package com.brianreber.messaging.client;

import com.brianreber.messaging.shared.Constants;
import com.brianreber.messaging.shared.IncomingOutgoingMessageProxy;
import com.brianreber.messaging.shared.SmsRequestFactory;
import com.brianreber.messaging.shared.SmsRequestFactory.SmsRequest;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.google.web.bindery.requestfactory.shared.Receiver;

/**
 * The widget that displays a list of the Conversations
 *
 * @author breber
 */
public class ConversationListWidget extends Composite implements ProvidesResize {

	interface TheUiBinder extends UiBinder<Widget, ConversationListWidget> { }
	private static TheUiBinder uiBinder = GWT.create(TheUiBinder.class);
	private final SmsRequestFactory requestFactory = GWT.create(SmsRequestFactory.class);
	private final EventBus eventBus = new SimpleEventBus();
	private ConversationListDataProvider listDataProvider;

	@UiField
	ConversationListTable conversationListTable;
	@UiField
	ConversationWidget conversationTable;

	/**
	 * Creates a ConversationListWidget
	 */
	public ConversationListWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		requestFactory.initialize(eventBus);

		Window.setTitle("Messaging");

		openChannel();

		listDataProvider = new ConversationListDataProvider(requestFactory);
		listDataProvider.addDataDisplay(conversationListTable);

		final SingleSelectionModel<IncomingOutgoingMessageProxy> selectionModel = new SingleSelectionModel<IncomingOutgoingMessageProxy>();
		conversationListTable.setSelectionModel(selectionModel);

		//		newConversationButton.addClickHandler(new ClickHandler() {
		//			@Override
		//			public void onClick(ClickEvent event) {
		//				new NewMessageDialog(requestFactory).show();
		//			}
		//		});
		//		newConversationButton.addStyleName("margin-tb");
		//
		//		html5NotifButton.setVisible(Messaging.checkNotifStatus());
		//		html5NotifButton.addClickHandler(new ClickHandler() {
		//			@Override
		//			public void onClick(ClickEvent event) {
		//				Messaging.registerForHtml5Notif();
		//			}
		//		});
		//		html5NotifButton.addStyleName("margin-tb");

		conversationListTable.addStyleName("table");

		conversationListTable.senderColumn.setFieldUpdater(new FieldUpdater<IncomingOutgoingMessageProxy, String>() {
			@Override
			public void update(int index, IncomingOutgoingMessageProxy object, String value) {
				String number = selectionModel.getSelectedObject().getName();

				if (number != null) {
					setCurrentConversation(Messaging.getNumberFromCondensedString(number));
				}
			}
		});

		conversationListTable.deleteColumn.setFieldUpdater(new FieldUpdater<IncomingOutgoingMessageProxy, String>() {
			@Override
			public void update(int index, IncomingOutgoingMessageProxy data, String value) {
				boolean confirmed = Window.confirm("Are you sure you want to delete this conversation?");

				if (confirmed) {
					SmsRequest request = requestFactory.smsRequest();
					request.deleteConversation(Messaging.getNumberFromCondensedString(data.getName())).fire(new Receiver<Void>() {
						@Override
						public void onSuccess(Void response) {
							listDataProvider.updateData(conversationListTable);
						}
					});
				}
			}
		});

		// Sets the logged in as text
		Storage storage = Storage.getSessionStorageIfSupported();
		if (storage == null) {
			requestFactory.userRequest().getLoggedInUser().fire(new Receiver<String>() {
				@Override
				public void onSuccess(String response) {
					updateUsername(response);
				}
			});
		} else {
			updateUsername(storage.getItem(Constants.HTML_EMAIL_KEY));
		}
	}

	private native void updateUsername(String str) /*-{
    	$wnd.document.getElementById("username").innerText = str;
	}-*/;

	private static native String getMd5(String str) /*-{
        return $wnd.hex_md5(str);
	}-*/;

	/**
	 * Open a Channel with the server for push notifications.
	 */
	private void openChannel() {
		String url = "/getToken";
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(url));

		try {
			builder.sendRequest(null, new RequestCallback() {
				@Override
				public void onError(Request request, Throwable exception) {
					// Couldn't connect to server (could be timeout, SOP violation, etc.)
					Window.alert("Error getting token...");
				}

				@Override
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						createChannel(response.getText());
					} else {
						// Handle the error.  Can get the status text from response.getStatusText()
					}
				}
			});
		} catch (RequestException e) {
			// Couldn't connect to server
			Window.alert("Error authorizing");
		}
	}

	/**
	 * Actually open a channel with the server
	 * 
	 * @param clientId the client id for the channel
	 */
	private void createChannel(final String clientId) {
		//		ChannelFactory.createChannel(clientId, new ChannelCreatedCallback() {
		//			@Override
		//			public void onChannelCreated(Channel channel) {
		//				channel.open(new SocketListener() {
		//					@Override
		//					public void onOpen() {
		//						Messaging.log("Channel opened!");
		//					}
		//
		//					@Override
		//					public void onMessage(String message) {
		//						conversationTable.updateList();
		//						listDataProvider.updateData(conversationListTable);
		//						Messaging.sendHtml5Notif(Messaging.parseJson(message));
		//					}
		//
		//					@Override
		//					public void onError(SocketError error) {
		//						Messaging.log("Error: " + error.getDescription());
		//					}
		//
		//					@Override
		//					public void onClose() {
		//						Messaging.log("Channel closed! Reopening...");
		//						createChannel(clientId);
		//					}
		//				});
		//			}
		//		});
	}

	public void setCurrentConversation(String number) {
		conversationTable.setContact(number);
	}
}
