package com.brianreber.messaging.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import org.json.JSONException;
import org.json.JSONObject;

import com.brianreber.messaging.shared.Constants;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.util.ServiceException;


public class SmsService {
	private static final Logger log = Logger.getLogger(SmsService.class.getName());
	private static DataStore data = new DataStore();

	public static SmsOutgoingMessage findOutgoing(Long id) {
		return data.findOutgoing(id);
	}

	public static SmsIncomingMessage findIncoming(Long id) {
		return data.findIncoming(id);
	}

	public static MmsIncomingMessage findIncomingMms(Long id) {
		return data.findIncomingMms(id);
	}

	public static SmsOutgoingMessage updateOutgoing(SmsOutgoingMessage item, boolean sendC2DM) {
		item.setEmailAddress(DataStore.getUserEmail());
		item.setUserId(DataStore.getUserId());

		item = data.updateOutgoing(item, sendC2DM);

		return item;
	}

	public static SmsIncomingMessage updateIncoming(SmsIncomingMessage item) {
		item.setEmailAddress(DataStore.getUserEmail());
		item.setUserId(DataStore.getUserId());

		item = data.updateIncoming(item);

		AuthenticatedUser user = AuthenticatedUserService.getCurrentUser();
		if (user != null) {
			JSONObject obj = new JSONObject();
			try {
				obj.put("iconUrl", "http://www.brianreber.com/images/contact.png");
				obj.put("title", "SMS from " + getContactName(item.getSender()));
				obj.put("text", item.getMessageText());
			} catch (JSONException e) {
				e.printStackTrace();
			}

			ChannelServiceFactory.getChannelService().sendMessage(new ChannelMessage(user.getUserId(), obj.toString()));
		}

		return item;
	}

	public static MmsIncomingMessage updateIncoming(MmsIncomingMessage item) {
		item.setEmailAddress(DataStore.getUserEmail());
		item.setUserId(DataStore.getUserId());

		item = data.updateIncoming(item);

		AuthenticatedUser user = AuthenticatedUserService.getCurrentUser();
		if (user != null) {
			JSONObject obj = new JSONObject();
			try {
				obj.put("iconUrl", "http://www.brianreber.com/images/contact.png");
				obj.put("title", "MMS from " + getContactName(item.getSender()));
				obj.put("text", item.getMessageText());
			} catch (JSONException e) {
				e.printStackTrace();
			}

			ChannelServiceFactory.getChannelService().sendMessage(new ChannelMessage(user.getUserId(), obj.toString()));
		}

		return item;
	}

	public static void deleteOutgoing(SmsOutgoingMessage item) {
		data.deleteOutgoing(item.getId());
	}

	public static void deleteIncoming(SmsIncomingMessage item) {
		data.deleteIncoming(item.getId());
	}

	public static void deleteIncoming(MmsIncomingMessage item) {
		data.deleteIncomingMms(item.getId());
	}

	public static List<IncomingOutgoingMessage> queryRecipients() {
		return data.findAllConversations();
	}

	public static List<IncomingOutgoingMessage> queryConversation(String otherUser, int end) {
		return data.findConversation(otherUser, end);
	}

	public static List<SmsOutgoingMessage> queryOutgoing(int end) {
		return data.findAllOutgoing(end);
	}

	public static List<SmsIncomingMessage> queryIncoming(int end) {
		return data.findAllIncoming(end);
	}

	public static List<MmsIncomingMessage> queryIncomingMms() {
		return data.findAllIncomingMms();
	}

	public static List<SmsOutgoingMessage> queryOutgoingByRecipient(String recipient, int end) {
		return data.findAllOutgoing(recipient, end);
	}

	public static List<SmsIncomingMessage> queryIncomingByRecipient(String recipient, int end) {
		return data.findAllIncoming(recipient, end);
	}

	public static List<MmsIncomingMessage> queryIncomingMmsByRecipient(String recipient) {
		return data.findAllIncomingMms(recipient);
	}

	public static void deleteConversation(String recipient) {
		data.deleteConversation(recipient);
	}

	public static String getContactName(String address) {
		AuthenticatedUser user = AuthenticatedUserService.getCurrentUser();

		try {
			return ContactRetrieval.getContactName(user.getAuthToken(), user.getAuthTokenSecret(), address);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		} catch (OAuthException e) {
			e.printStackTrace();
		}

		return address;
	}

	public static String getContactPhotoLink(String address) {
		AuthenticatedUser user = AuthenticatedUserService.getCurrentUser();
		String cacheKey = getLoggedInUser() + ";" + address;

		try {
			CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
			Cache cache = cacheFactory.createCache(Collections.emptyMap());

			if (cache.containsKey(cacheKey)) {
				return (String) cache.get(cacheKey);
			} else {
				String data = ContactRetrieval.getContactPhotoUrl(user.getAuthToken(), user.getAuthTokenSecret(), address);

				cache.put(cacheKey, data);

				return data;
			}
		} catch (IOException e) {
			log.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
		} catch (ServiceException e) {
			log.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
		} catch (OAuthException e) {
			log.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
		} catch (CacheException e) {
			log.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
		}

		return Constants.DEFAULT_IMAGE;
	}

	public static String getMyPhotoLink() {
		return ContactRetrieval.getMyPhotoUrl(getLoggedInUser());
	}

	public static String getLoggedInUser() {
		return DataStore.getUserEmail();
	}
}
