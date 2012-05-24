package com.brianreber.messaging.client;

import com.brianreber.messaging.shared.IncomingOutgoingMessageProxy;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

/**
 * The Table that contains the Conversation list
 * 
 * @author breber
 */
public class ConversationListTable extends CellTable<IncomingOutgoingMessageProxy> {
	/**
	 * The number of records to show on a page
	 */
	public static final int PAGE_SIZE = 20;

	public Column<IncomingOutgoingMessageProxy, SafeHtml> imageColumn;
	public Column<IncomingOutgoingMessageProxy, String> senderColumn;
	public Column<IncomingOutgoingMessageProxy, String> deleteColumn;

	interface TableStyle extends CellTable.Style {
		String columnImage();
		String columnSender();
		String columnTrash();
	}

	/**
	 * Creates a new table with page size = PAGE_SIZE
	 */
	public ConversationListTable() {
		super(PAGE_SIZE);

		imageColumn = new Column<IncomingOutgoingMessageProxy, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(IncomingOutgoingMessageProxy object) {
				String img = object.getPhotoUrl();
				SafeHtmlBuilder builder = new SafeHtmlBuilder();
				builder.appendHtmlConstant("<div style=\"background-image:url('" + img + "'); background-size:contain;" +
						"width:35px; height:35px;\"></div>");
				return builder.toSafeHtml();
			}
		};
		imageColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		addColumn(imageColumn);

		ButtonCell buttonCell = new ButtonCell(new SafeHtmlRenderer<String>() {
			@Override
			public SafeHtml render(String object) {
				return SafeHtmlUtils.fromTrustedString(object);
			}

			@Override
			public void render(String object, SafeHtmlBuilder builder) {
				builder.append(render(object));
			}
		});

		senderColumn = new Column<IncomingOutgoingMessageProxy, String>(buttonCell) {
			@Override
			public String getValue(IncomingOutgoingMessageProxy object) {
				return "<div style='text-align:left'><span>" + SafeHtmlUtils.htmlEscape(object.getName()) + "</span><br />" +
						"<span style='font-size:80%'>" + DateTimeFormat.getFormat("MM/dd/yy hh:mm a").format(object.getMessageDate()) + "</span></div>";
			}
		};
		senderColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		addColumn(senderColumn);

		// The delete button and cell
		buttonCell = new ButtonCell(new SafeHtmlRenderer<String>() {
			@Override
			public SafeHtml render(String object) {
				return SafeHtmlUtils.fromTrustedString("<div style=\"background-image:url('delete.png'); " +
						"width:14px; height:14px;\"></div>");
			}

			@Override
			public void render(String object, SafeHtmlBuilder builder) {
				builder.append(render(object));
			}
		});
		deleteColumn = new Column<IncomingOutgoingMessageProxy, String>(buttonCell) {
			@Override
			public String getValue(IncomingOutgoingMessageProxy object) {
				return "delete.png";
			}
		};
		addColumn(deleteColumn);
	}
}

