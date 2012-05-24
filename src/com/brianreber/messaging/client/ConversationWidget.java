package com.brianreber.messaging.client;

import java.util.Date;

import com.brianreber.messaging.shared.IncomingOutgoingMessageProxy;
import com.brianreber.messaging.shared.SmsOutgoingMessageProxy;
import com.brianreber.messaging.shared.SmsRequestFactory;
import com.brianreber.messaging.shared.SmsRequestFactory.SmsRequest;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.google.web.bindery.requestfactory.shared.Receiver;

/**
 * The widget that displays a single conversation
 *
 * @author breber
 */
public class ConversationWidget extends Composite implements ProvidesResize {

	interface TheUiBinder extends UiBinder<Widget, ConversationWidget> { }
	private static TheUiBinder uiBinder = GWT.create(TheUiBinder.class);
	private final SmsRequestFactory requestFactory = GWT.create(SmsRequestFactory.class);
	private final EventBus eventBus = new SimpleEventBus();
	private ConversationDataProvider dataProvider;

	@UiField
	HTMLPanel noContents;
	@UiField
	HTMLPanel contents;
	@UiField
	Label withLabel;
	@UiField(provided = true)
	CellList<IncomingOutgoingMessageProxy> table;
	@UiField
	TextBox messageText;
	@UiField
	Button sendButton;
	@UiField
	SimplePager pager;

	private String userId;

	public ConversationWidget() {
		this("");
	}

	/**
	 * Creates a ConversationWidget
	 */
	public ConversationWidget(final String userId) {
		requestFactory.initialize(eventBus);

		table = new CellList<IncomingOutgoingMessageProxy>(new MessageCell());
		table.setPageSize(15);
		table.setKeyboardPagingPolicy(KeyboardPagingPolicy.INCREASE_RANGE);
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

		initWidget(uiBinder.createAndBindUi(this));

		this.userId = userId;

		getContactInfo();
	}

	public void setContact(String userId) {
		if (dataProvider != null) {
			dataProvider.removeDataDisplay(table);
		}
		this.userId = userId;
		getContactInfo();
	}

	private void getContactInfo() {
		if (!"".equals(userId)) {
			requestFactory.smsRequest().getContactName(userId).fire(new Receiver<String>() {
				@Override
				public void onSuccess(String response) {
					postContactRetrieval(response);
				}
			});
		} else {
			this.noContents.setVisible(true);
			this.contents.setVisible(false);
		}
	}

	private void postContactRetrieval(String name) {
		dataProvider = new ConversationDataProvider(requestFactory, userId);

		// Connect the table to the data provider.
		dataProvider.addDataDisplay(table);

		pager.addStyleName("pagerClass");
		pager.setDisplay(table);

		withLabel.setText(name + " <" + userId + ">");

		this.noContents.setVisible(false);
		this.contents.setVisible(true);
	}

	public void updateList() {
		if (dataProvider != null) {
			dataProvider.updateData(table);
		}
	}

	@UiHandler("sendButton")
	void onSendButtonClick(ClickEvent event) {
		String msgText = messageText.getText();

		if (!"".equals(msgText)) {
			SmsRequest req = requestFactory.smsRequest();

			SmsOutgoingMessageProxy msg = req.create(SmsOutgoingMessageProxy.class);
			msg.setMessageDate(new Date().getTime());
			msg.setMessageText(msgText);
			msg.setRecipient(userId);

			messageText.setEnabled(false);
			sendButton.setEnabled(false);

			req.updateOutgoing(msg, true).fire(new Receiver<SmsOutgoingMessageProxy>() {
				@Override
				public void onSuccess(SmsOutgoingMessageProxy response) {
					dataProvider.updateData(table);
					messageText.setText("");
					messageText.setEnabled(true);
					sendButton.setEnabled(true);
				}
			});
		}
	}

	/**
	 * The Cell used to render
	 */
	static class MessageCell extends AbstractCell<IncomingOutgoingMessageProxy> {
		@Override
		public void render(Context context, IncomingOutgoingMessageProxy value, SafeHtmlBuilder sb) {
			// Value can be null, so do a null check..
			if (value == null) {
				return;
			}

			sb.appendHtmlConstant("<table style='padding:10px;width:100%'>");
			sb.appendHtmlConstant("<tr>");

			if ("Me".equals(value.getName())) {
				appendImage(sb, value.getPhotoUrl());
				appendMessage(sb, value);
			} else {
				appendMessage(sb, value);
				appendImage(sb, value.getPhotoUrl());
			}

			sb.appendHtmlConstant("</tr>");
			sb.appendHtmlConstant("</table>");
		}

		private void appendImage(SafeHtmlBuilder sb, String src) {
			sb.appendHtmlConstant("<td style='margin-left:auto; margin-right:auto;width:50px;'>");
			sb.appendHtmlConstant("<img src='" + src + "' width='35px' height='35px'/>");
			sb.appendHtmlConstant("</td>");
		}

		private void appendMessage(SafeHtmlBuilder sb, IncomingOutgoingMessageProxy value) {
			String date = DateTimeFormat.getFormat("MM/dd/yy hh:mm a").format(value.getMessageDate());

			sb.appendHtmlConstant("<td style='" + (("Me".equals(value.getName())) ? "text-align:left;" : "text-align:right;") + "'>");

			sb.appendHtmlConstant("<div>");
			sb.appendHtmlConstant("<b>");
			sb.appendEscaped(value.getName());
			sb.appendHtmlConstant("</b>");
			sb.appendHtmlConstant("</div>");

			sb.appendHtmlConstant("<div style='font-size:80%'>");
			sb.appendEscaped(date);
			sb.appendHtmlConstant("</div>");

			sb.appendHtmlConstant("<div>");
			sb.appendEscaped(value.getMessageText());
			sb.appendHtmlConstant("</div>");

			if (value.getBlob() != null) {
				sb.appendHtmlConstant("<div>");
				sb.appendHtmlConstant("<image src='/serve?blob-key=");
				sb.appendEscaped(value.getBlob());
				sb.appendHtmlConstant("' />");
				sb.appendHtmlConstant("</div>");
			}

			sb.appendHtmlConstant("</td>");
		}
	}
}
