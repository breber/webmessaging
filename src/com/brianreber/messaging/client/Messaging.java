package com.brianreber.messaging.client;

import com.brianreber.messaging.shared.Constants;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.storage.client.StorageMap;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Messaging implements EntryPoint {

	interface Resources extends ClientBundle {
		@Source("ajax-loader.gif")
		ImageResource ajaxLoader();
	}

	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {
		// Display a loading image while we wait
		Resources resources = GWT.create(Resources.class);
		Image loadingImage = new Image(resources.ajaxLoader());
		loadingImage.addStyleName("center");
		loadingImage.addStyleName("imgcenter");
		RootPanel.get().add(loadingImage);

		verifyOAuth();
	}

	private void verifyOAuth() {
		String url = "/getauthorized";
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(url));

		// Since we are using session storage, if the email already exists in the storage,
		// we know they have been here recently. So there is no need to ping the server
		Storage storage = Storage.getSessionStorageIfSupported();
		StorageMap map  = new StorageMap(storage);
		if (map.containsKey(Constants.HTML_EMAIL_KEY)) {
			RootPanel.get().clear();
			RootPanel.get().add(new ConversationListWidget());
			return;
		}

		try {
			builder.sendRequest(null, new RequestCallback() {
				@Override
				public void onError(Request request, Throwable exception) {
					// Couldn't connect to server (could be timeout, SOP violation, etc.)
					Window.alert("Error authorizing");
				}

				@Override
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						JSONObject obj = new JSONObject(parseJson(response.getText()));
						Storage storage = Storage.getSessionStorageIfSupported();

						JSONValue email = obj.get(Constants.HTML_EMAIL_KEY);
						JSONValue authToken = obj.get(Constants.HTML_AUTH_KEY);
						JSONValue authTokenSecret = obj.get(Constants.HTML_AUTH_SECRET_KEY);
						if (email != null && authToken != null && authTokenSecret != null &&
								email.isString() != null && authToken.isString() != null && authTokenSecret.isString() != null) {
							if (storage != null) {
								storage.setItem(Constants.HTML_EMAIL_KEY, email.isString().stringValue());
								storage.setItem(Constants.HTML_AUTH_KEY, authToken.isString().stringValue());
								storage.setItem(Constants.HTML_AUTH_SECRET_KEY, authTokenSecret.isString().stringValue());
							}
						} else {
							authorize();
						}

						RootPanel.get().clear();
						RootPanel.get().add(new ConversationListWidget());
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

	private void authorize() {
		Window.Location.assign(Window.Location.getProtocol() + "//" + Window.Location.getHostName() + "/FetchAccessToken");
	}

	/**
	 * Parse the number out of a string similar to "Anonymous <##########>"
	 * 
	 * @param condensed
	 * The condensed string
	 * @return
	 * The number from the string
	 */
	public static String getNumberFromCondensedString(String condensed) {
		return condensed.substring(condensed.lastIndexOf('<') + 1, condensed.lastIndexOf('>'));
	}

	/**
	 * Display an HTML5 notification
	 * 
	 * @param obj a JS object containing an image, title and text
	 */
	public static native void sendHtml5Notif(JavaScriptObject obj) /*-{
		$wnd.sendHtml5Notification(obj);
	}-*/;

	/**
	 * Register for HTML5 notifications
	 */
	public static native void registerForHtml5Notif() /*-{
		if (window.webkitNotifications) {
			if (window.webkitNotifications.checkPermission() != 0) { // 0 is PERMISSION_ALLOWED
				window.webkitNotifications.requestPermission();
			}
		} else {
			console.log("Notifications are not supported for this Browser/OS version yet.");
		}
	}-*/;

	/**
	 * Takes in a trusted JSON String and evals it.
	 * @param JSON String that you trust
	 * @return JavaScriptObject that you can cast to an Overlay Type
	 */
	public static native JavaScriptObject parseJson(String jsonStr) /*-{
	  return JSON.parse(jsonStr);
	}-*/;

	/**
	 * Prints a message to the Browser's console
	 * @param The message to print
	 */
	public static native void log(String msg) /*-{
	  return console.log(msg);
	}-*/;

	/**
	 * Check to see if we are allowed to send HTML5 notifications
	 * @return whether we can send HTML5 notifications or not
	 */
	public static native boolean checkNotifStatus() /*-{
		if (window.webkitNotifications) {
			if (window.webkitNotifications.checkPermission() != 0) { // 0 is PERMISSION_ALLOWED
				return true;
			}
			return false;
		} else {
			return false;
		}
	}-*/;
}
