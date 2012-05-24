package com.brianreber.messaging.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Logger;

import com.brianreber.messaging.shared.Constants;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import com.google.appengine.tools.admin.OAuth2ServerConnection.OAuthException;
import com.google.gdata.client.Service;
import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.google.gdata.client.contacts.ContactQuery;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.Link;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.extensions.PhoneNumber;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.common.util.Base64;

/**
 * Utilites for getting Contact information from the Google Contacts API
 * 
 * @author breber
 */
public class ContactRetrieval {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ContactRetrieval.class.getName());

	/**
	 * Gets the contact name for the given phone number
	 * @param userOAuthToken
	 * The logged in user's oauth token
	 * @param userOAuthTokenSecret
	 * The logged in user's oauth token secret
	 * @param number
	 * The phone number of the user to get the name for
	 * @return
	 * The name for the user in the logged in user's contacts. Empty string if it can't be found
	 * @throws IOException
	 * @throws ServiceException
	 * @throws OAuthException
	 * @throws com.google.gdata.client.authn.oauth.OAuthException
	 */
	public static String getContactName(String userOAuthToken, String userOAuthTokenSecret, String number) throws IOException, ServiceException, OAuthException, com.google.gdata.client.authn.oauth.OAuthException {
		GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
		oauthParameters.setOAuthConsumerKey(Constants.CONSUMER_KEY);
		oauthParameters.setOAuthConsumerSecret(Constants.CONSUMER_SECRET);
		oauthParameters.setScope(Constants.SCOPE);
		oauthParameters.setOAuthToken(userOAuthToken);
		oauthParameters.setOAuthTokenSecret(userOAuthTokenSecret);

		try {
			ContactsService service = new ContactsService("reber-Messaging-v1");
			service.setOAuthCredentials(oauthParameters, new OAuthHmacSha1Signer());

			ContactQuery query = new ContactQuery(new URL(Constants.FEED_URL));
			query.setFullTextQuery(number);

			ContactFeed feed = service.getFeed(query, ContactFeed.class);

			List<ContactEntry> contacts = feed.getEntries();

			for (ContactEntry entry : contacts) {
				if (entry.getPhoneNumbers() != null) {
					for (PhoneNumber num : entry.getPhoneNumbers()) {
						if (num != null && number.equals(num.getPhoneNumber().replaceAll("[^0-9]", ""))) {
							return entry.getTitle().getPlainText();
						}
					}
				}
			}
		} catch (RuntimeException e) {
			return "";
		}

		return "";
	}

	/**
	 * Gets the contact photo data from the given number
	 * 
	 * @param userOAuthToken
	 * The logged in user's oauth token
	 * @param userOAuthTokenSecret
	 * The logged in user's oauth token secret
	 * @param number
	 * The phone number of the user to get the image for
	 * @return
	 * The base-64 encoded image data for the user
	 * @throws IOException
	 * @throws ServiceException
	 * @throws OAuthException
	 * @throws com.google.gdata.client.authn.oauth.OAuthException
	 */
	public static String getContactPhotoUrl(String userOAuthToken, String userOAuthTokenSecret, String number) throws IOException, ServiceException, OAuthException, com.google.gdata.client.authn.oauth.OAuthException {
		GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
		oauthParameters.setOAuthConsumerKey(Constants.CONSUMER_KEY);
		oauthParameters.setOAuthConsumerSecret(Constants.CONSUMER_SECRET);
		oauthParameters.setScope(Constants.SCOPE);
		oauthParameters.setOAuthToken(userOAuthToken);
		oauthParameters.setOAuthTokenSecret(userOAuthTokenSecret);

		try {
			ContactsService service = new ContactsService("reber-Messaging-v1");
			service.setOAuthCredentials(oauthParameters, new OAuthHmacSha1Signer());

			ContactQuery query = new ContactQuery(new URL(Constants.FEED_URL));
			query.setFullTextQuery(number);

			ContactFeed feed = service.getFeed(query, ContactFeed.class);
			List<ContactEntry> contacts = feed.getEntries();

			for (ContactEntry entry : contacts) {
				Link photoLink = entry.getContactPhotoLink();

				if (photoLink.getEtag() != null) {
					Service.GDataRequest photoRequest = service.createLinkQueryRequest(photoLink);
					photoRequest.execute();

					ByteArrayInputStream is = (ByteArrayInputStream) photoRequest.getResponseStream();
					ByteArrayOutputStream buffer = new ByteArrayOutputStream();

					int nRead;
					byte[] data = new byte[16384];

					while ((nRead = is.read(data, 0, data.length)) != -1) {
						buffer.write(data, 0, nRead);
					}

					buffer.flush();

					byte[] bytes = buffer.toByteArray();
					Image img = ImagesServiceFactory.makeImage(bytes);
					Transform resize = ImagesServiceFactory.makeResize(40, 40);

					// Transform the image to 40px by 40px
					img = ImagesServiceFactory.getImagesService().applyTransform(resize, img);
					String imgData = Base64.encode(img.getImageData());

					// Return the base64 encoded image data
					return "data:image/" + img.getFormat().toString() + ";base64," + imgData;
				}
			}
		} catch (RuntimeException e) {
			return "";
		}

		return Constants.DEFAULT_IMAGE;
	}

	/**
	 * Gets the logged in user's image url from Gravatar
	 * 
	 * @param userName
	 * The user's email address
	 * @return
	 * The url for the Gravatar image for the user
	 */
	public static String getMyPhotoUrl(String userName) {
		return "https://secure.gravatar.com/avatar/" + md5Hex(userName) + "?d=" + Constants.DEFAULT_IMAGE;
	}

	/**
	 * Convert a string into an MD5 hex string
	 * 
	 * @param message
	 * The string to convert
	 * @return
	 * A MD5 representation of the given string
	 */
	private static String md5Hex(String message) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");

			if (message != null) {
				message = message.toLowerCase().trim();

				byte[] array = md.digest(message.getBytes("CP1252"));

				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < array.length; ++i) {
					sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
				}

				return sb.toString();
			}
		} catch (NoSuchAlgorithmException e) {
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}
}
