package com.brianreber.messaging.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.servlet.ServletContext;

import com.brianreber.messaging.shared.C2DMConstants;
import com.google.android.c2dm.server.PMF;
import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailService.Message;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;

public class DataStore {

	private static final String PHOTO_URL_FIELD = "photoUrl";
	private static final String SENDER_FIELD = "sender";
	private static final String RECIPIENT_FIELD = "recipient";
	private static final String USER_ID_FIELD = "userId";
	private static final String BLOB_FIELD = "blob";
	private static final String MESSAGE_TEXT_FIELD = "messageText";
	private static final String NAME_FIELD = "name";
	private static final String ID_FIELD = "id";
	private static final String MESSAGE_DATE_FIELD = "messageDate";
	private static final String EMAIL_ADDRESS_FIELD = "emailAddress";

	private static final Logger log = Logger.getLogger(SendMessage.class.getSimpleName());

	private final String userEmailAddress = getUserEmail();

	/**
	 * Find a {@link SmsOutgoingMessage} by id.
	 * 
	 * @param id the {@link SmsOutgoingMessage} id
	 * @return the associated {@link SmsOutgoingMessage}, or null if not found
	 */
	public SmsOutgoingMessage findOutgoing(Long id) {
		if (id == null) {
			return null;
		}

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		try {
			Query query = new Query(SmsOutgoingMessage.class.getSimpleName());
			query.addFilter(ID_FIELD, FilterOperator.EQUAL, id);
			query.addFilter(EMAIL_ADDRESS_FIELD, FilterOperator.EQUAL, userEmailAddress);

			PreparedQuery pq = datastore.prepare(query);

			Entity result = pq.asSingleEntity();

			SmsOutgoingMessage msg = new SmsOutgoingMessage();
			msg.setId((Long) result.getProperty(ID_FIELD));
			msg.setUserId((String) result.getProperty(USER_ID_FIELD));
			msg.setEmailAddress((String) result.getProperty(EMAIL_ADDRESS_FIELD));
			msg.setRecipient((String) result.getProperty(RECIPIENT_FIELD));
			msg.setMessageText((String) result.getProperty(MESSAGE_TEXT_FIELD));
			msg.setMessageDate((Long) result.getProperty(MESSAGE_DATE_FIELD));

			return msg;
		} catch (Exception e) {
			log.log(Level.SEVERE, "caught exception " + e.getMessage());
			log.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
		}

		return null;
	}

	/**
	 * Find a {@link SmsIncomingMessage} by id.
	 * 
	 * @param id the {@link SmsIncomingMessage} id
	 * @return the associated {@link SmsIncomingMessage}, or null if not found
	 */
	public SmsIncomingMessage findIncoming(Long id) {
		if (id == null) {
			return null;
		}

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		try {
			Query query = new Query(SmsIncomingMessage.class.getSimpleName());
			query.addFilter(ID_FIELD, FilterOperator.EQUAL, id);
			query.addFilter(EMAIL_ADDRESS_FIELD, FilterOperator.EQUAL, userEmailAddress);

			PreparedQuery pq = datastore.prepare(query);

			Entity result = pq.asSingleEntity();

			SmsIncomingMessage msg = new SmsIncomingMessage();
			msg.setId((Long) result.getProperty(ID_FIELD));
			msg.setUserId((String) result.getProperty(USER_ID_FIELD));
			msg.setEmailAddress((String) result.getProperty(EMAIL_ADDRESS_FIELD));
			msg.setSender((String) result.getProperty(SENDER_FIELD));
			msg.setMessageText((String) result.getProperty(MESSAGE_TEXT_FIELD));
			msg.setMessageDate((Long) result.getProperty(MESSAGE_DATE_FIELD));

			return msg;
		} catch (Exception e) {
			log.log(Level.SEVERE, "caught exception " + e.getMessage());
			log.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
		}

		return null;
	}

	/**
	 * Find a {@link MmsIncomingMessage} by id.
	 * 
	 * @param id the {@link MmsIncomingMessage} id
	 * @return the associated {@link MmsIncomingMessage}, or null if not found
	 */
	public MmsIncomingMessage findIncomingMms(Long id) {
		if (id == null) {
			return null;
		}

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		try {
			Query query = new Query(MmsIncomingMessage.class.getSimpleName());
			query.addFilter(ID_FIELD, FilterOperator.EQUAL, id);
			query.addFilter(EMAIL_ADDRESS_FIELD, FilterOperator.EQUAL, userEmailAddress);

			PreparedQuery pq = datastore.prepare(query);

			Entity result = pq.asSingleEntity();

			MmsIncomingMessage msg = new MmsIncomingMessage();
			msg.setId((Long) result.getProperty(ID_FIELD));
			msg.setUserId((String) result.getProperty(USER_ID_FIELD));
			msg.setEmailAddress((String) result.getProperty(EMAIL_ADDRESS_FIELD));
			msg.setSender((String) result.getProperty(SENDER_FIELD));
			msg.setMessageText((String) result.getProperty(MESSAGE_TEXT_FIELD));
			msg.setBlobKey((String) result.getProperty("blobKey"));
			msg.setMessageDate((Long) result.getProperty(MESSAGE_DATE_FIELD));

			return msg;
		} catch (Exception e) {
			log.log(Level.SEVERE, "caught exception " + e.getMessage());
			log.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
		}

		return null;
	}

	private void updateConversations(String contact, String messageText, String blob, Long messageDate) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction tr = pm.currentTransaction();
		try {
			tr.begin();
			String name = SmsService.getContactName(contact) + " <" + contact + ">";

			Query query = new Query(IncomingOutgoingMessage.class.getSimpleName());
			query.addFilter(EMAIL_ADDRESS_FIELD, FilterOperator.EQUAL, userEmailAddress);
			query.addFilter(NAME_FIELD, FilterOperator.EQUAL, name);

			PreparedQuery pq = datastore.prepare(query);

			Entity result = pq.asSingleEntity();

			if (result != null) {
				if (((Date) result.getProperty(MESSAGE_DATE_FIELD)).compareTo(new Date(messageDate)) < 0) {
					if (messageText != null) {
						result.setProperty(MESSAGE_TEXT_FIELD, messageText);
					}

					result.setProperty(BLOB_FIELD, blob);

					result.setProperty(MESSAGE_DATE_FIELD, new Date(messageDate));

					result.setProperty(PHOTO_URL_FIELD, new Text(SmsService.getContactPhotoLink(contact)));

					datastore.put(result);
				} else {
					log.log(Level.FINE, "Message Date is not more recent than the newer one...");
				}
			}

			tr.commit();
		} catch (Exception e) {
			log.log(Level.SEVERE, "caught exception " + e.getMessage());
			log.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));

			IncomingOutgoingMessage msg1 = new IncomingOutgoingMessage(contact, messageText, blob, new Date(messageDate), SmsService.getContactPhotoLink(contact));
			pm.makePersistent(msg1);

			if (tr.isActive()) {
				tr.rollback();
			}

			pm.close();
		}
	}

	/**
	 * Finds all conversations
	 * 
	 * @return the list of phone numbers there is a conversation with
	 */
	public List<IncomingOutgoingMessage> findAllConversations() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Query mQuery = new Query(IncomingOutgoingMessage.class.getSimpleName());
			log.log(Level.SEVERE, "created query " + IncomingOutgoingMessage.class.getSimpleName());
			mQuery.addFilter(EMAIL_ADDRESS_FIELD, FilterOperator.EQUAL, userEmailAddress);
			mQuery.addSort(MESSAGE_DATE_FIELD, SortDirection.DESCENDING);

			PreparedQuery pq = datastore.prepare(mQuery);

			List<IncomingOutgoingMessage> list = new ArrayList<IncomingOutgoingMessage>();

			for (Entity result : pq.asIterable()) {
				IncomingOutgoingMessage msg = new IncomingOutgoingMessage();
				msg.setId((Long) result.getProperty(ID_FIELD));
				msg.setName((String) result.getProperty(NAME_FIELD));
				msg.setMessageText((String) result.getProperty(MESSAGE_TEXT_FIELD));
				msg.setBlob((String) result.getProperty(BLOB_FIELD));
				msg.setMessageDate((Date) result.getProperty(MESSAGE_DATE_FIELD));
				msg.setPhotoUrl(((Text) result.getProperty(PHOTO_URL_FIELD)).getValue());
				msg.setEmailAddress((String) result.getProperty(EMAIL_ADDRESS_FIELD));

				list.add(msg);
			}

			log.log(Level.SEVERE, "result list = " + list.size());

			return list;
		} catch (Exception e) {
			log.log(Level.SEVERE, "caught exception " + e.getMessage());
			log.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
		} finally {
			pm.close();
		}

		return new ArrayList<IncomingOutgoingMessage>();
	}

	/**
	 * Finds conversation with a given user
	 * 
	 * @return the list of phone numbers there is a conversation with
	 */
	public List<IncomingOutgoingMessage> findConversation(String otherUser, int end) {
		List<SmsIncomingMessage> incoming = findAllIncoming(otherUser, end);
		List<SmsOutgoingMessage> outgoing = findAllOutgoing(otherUser, end);
		List<MmsIncomingMessage> incomingMms = findAllIncomingMms(otherUser);

		String myImageUrl = SmsService.getMyPhotoLink();
		String theirImageUrl = SmsService.getContactPhotoLink(otherUser);
		String theirName = SmsService.getContactName(otherUser);

		List<IncomingOutgoingMessage> toRet = new ArrayList<IncomingOutgoingMessage>();

		for (SmsIncomingMessage msg : incoming) {
			IncomingOutgoingMessage temp = new IncomingOutgoingMessage(theirName, msg.getMessageText(),
					null, new Date(msg.getMessageDate()), theirImageUrl);

			toRet.add(temp);
		}

		for (SmsOutgoingMessage msg : outgoing) {
			IncomingOutgoingMessage temp = new IncomingOutgoingMessage("Me", msg.getMessageText(),
					null, new Date(msg.getMessageDate()), myImageUrl);

			toRet.add(temp);
		}

		for (MmsIncomingMessage msg : incomingMms) {
			IncomingOutgoingMessage temp = new IncomingOutgoingMessage(theirName, msg.getMessageText(),
					msg.getBlobKey(), new Date(msg.getMessageDate()), theirImageUrl);

			toRet.add(temp);
		}

		Collections.sort(toRet);

		return toRet;
	}

	/**
	 * Finds all outgoing messages
	 * 
	 * @return the associated {@link List<SmsOutgoingMessage>}, or null if not found
	 */
	public List<SmsOutgoingMessage> findAllOutgoing(int end) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		try {
			Query mQuery = new Query(SmsOutgoingMessage.class.getSimpleName());
			mQuery.addFilter(EMAIL_ADDRESS_FIELD, FilterOperator.EQUAL, userEmailAddress);
			mQuery.addSort(MESSAGE_DATE_FIELD, SortDirection.DESCENDING);
			PreparedQuery pq = datastore.prepare(mQuery);

			List<SmsOutgoingMessage> list = new ArrayList<SmsOutgoingMessage>();

			for (Entity result : pq.asIterable(FetchOptions.Builder.withLimit(end))) {
				SmsOutgoingMessage msg = new SmsOutgoingMessage();
				msg.setId((Long) result.getProperty(ID_FIELD));
				msg.setUserId((String) result.getProperty(USER_ID_FIELD));
				msg.setEmailAddress((String) result.getProperty(EMAIL_ADDRESS_FIELD));
				msg.setRecipient((String) result.getProperty(RECIPIENT_FIELD));
				msg.setMessageText((String) result.getProperty(MESSAGE_TEXT_FIELD));
				msg.setMessageDate((Long) result.getProperty(MESSAGE_DATE_FIELD));
				list.add(msg);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "caught exception " + e.getMessage());
			log.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
		}

		return new ArrayList<SmsOutgoingMessage>();
	}

	/**
	 * Finds all outgoing messages to this user
	 * 
	 * @param recipient the {@link SmsOutgoingMessage} id
	 * @return the associated {@link List<SmsOutgoingMessage>}, or null if not found
	 */
	public List<SmsOutgoingMessage> findAllOutgoing(String recipient, int end) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		try {
			Query mQuery = new Query(SmsOutgoingMessage.class.getSimpleName());
			mQuery.addFilter(EMAIL_ADDRESS_FIELD, FilterOperator.EQUAL, userEmailAddress);
			mQuery.addFilter(RECIPIENT_FIELD, FilterOperator.EQUAL, recipient);
			mQuery.addSort(MESSAGE_DATE_FIELD, SortDirection.DESCENDING);
			PreparedQuery pq = datastore.prepare(mQuery);

			List<SmsOutgoingMessage> list = new ArrayList<SmsOutgoingMessage>();

			for (Entity result : pq.asIterable(FetchOptions.Builder.withLimit(end))) {
				SmsOutgoingMessage msg = new SmsOutgoingMessage();
				msg.setId((Long) result.getProperty(ID_FIELD));
				msg.setUserId((String) result.getProperty(USER_ID_FIELD));
				msg.setEmailAddress((String) result.getProperty(EMAIL_ADDRESS_FIELD));
				msg.setRecipient((String) result.getProperty(RECIPIENT_FIELD));
				msg.setMessageText((String) result.getProperty(MESSAGE_TEXT_FIELD));
				msg.setMessageDate((Long) result.getProperty(MESSAGE_DATE_FIELD));
				list.add(msg);
			}

			return list;
		} catch (Exception e) {
			log.log(Level.SEVERE, "caught exception " + e.getMessage());
			log.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
		}

		return new ArrayList<SmsOutgoingMessage>();
	}

	/**
	 * Finds all incoming messages
	 * 
	 * @param recipient the {@link SmsIncomingMessage} id
	 * @return the associated {@link List<SmsIncomingMessage>}, or null if not found
	 */
	public List<SmsIncomingMessage> findAllIncoming(int end) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		try {
			Query mQuery = new Query(SmsIncomingMessage.class.getSimpleName());
			mQuery.addFilter(EMAIL_ADDRESS_FIELD, FilterOperator.EQUAL, userEmailAddress);
			mQuery.addSort(MESSAGE_DATE_FIELD, SortDirection.DESCENDING);
			PreparedQuery pq = datastore.prepare(mQuery);

			List<SmsIncomingMessage> list = new ArrayList<SmsIncomingMessage>();

			for (Entity result : pq.asIterable(FetchOptions.Builder.withLimit(end))) {
				SmsIncomingMessage msg = new SmsIncomingMessage();
				msg.setId((Long) result.getProperty(ID_FIELD));
				msg.setUserId((String) result.getProperty(USER_ID_FIELD));
				msg.setEmailAddress((String) result.getProperty(EMAIL_ADDRESS_FIELD));
				msg.setSender((String) result.getProperty(SENDER_FIELD));
				msg.setMessageText((String) result.getProperty(MESSAGE_TEXT_FIELD));
				msg.setMessageDate((Long) result.getProperty(MESSAGE_DATE_FIELD));
				list.add(msg);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "caught exception " + e.getMessage());
			log.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
		}

		return new ArrayList<SmsIncomingMessage>();
	}

	/**
	 * Finds all incoming messages from this user
	 * 
	 * @param recipient the {@link SmsIncomingMessage} id
	 * @return the associated {@link List<SmsIncomingMessage>}, or null if not found
	 */
	public List<SmsIncomingMessage> findAllIncoming(String recipient, int end) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		try {
			Query mQuery = new Query(SmsIncomingMessage.class.getSimpleName());
			mQuery.addFilter(EMAIL_ADDRESS_FIELD, FilterOperator.EQUAL, userEmailAddress);
			mQuery.addFilter(SENDER_FIELD, FilterOperator.EQUAL, recipient);
			mQuery.addSort(MESSAGE_DATE_FIELD, SortDirection.DESCENDING);
			PreparedQuery pq = datastore.prepare(mQuery);

			List<SmsIncomingMessage> list = new ArrayList<SmsIncomingMessage>();

			for (Entity result : pq.asIterable(FetchOptions.Builder.withLimit(end))) {
				SmsIncomingMessage msg = new SmsIncomingMessage();
				msg.setId((Long) result.getProperty(ID_FIELD));
				msg.setUserId((String) result.getProperty(USER_ID_FIELD));
				msg.setEmailAddress((String) result.getProperty(EMAIL_ADDRESS_FIELD));
				msg.setSender((String) result.getProperty(SENDER_FIELD));
				msg.setMessageText((String) result.getProperty(MESSAGE_TEXT_FIELD));
				msg.setMessageDate((Long) result.getProperty(MESSAGE_DATE_FIELD));
				list.add(msg);
			}

			return list;
		} catch (Exception e) {
			log.log(Level.SEVERE, "caught exception " + e.getMessage());
			log.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
		}

		return new ArrayList<SmsIncomingMessage>();
	}

	/**
	 * Finds all incoming MMS messages from this user
	 * 
	 * @return the associated {@link List<MmsIncomingMessage>}, or null if not found
	 */
	public List<MmsIncomingMessage> findAllIncomingMms() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		try {
			Query query = new Query(MmsIncomingMessage.class.getSimpleName());
			query.addFilter(EMAIL_ADDRESS_FIELD, FilterOperator.EQUAL, userEmailAddress);
			query.addSort(MESSAGE_DATE_FIELD, SortDirection.DESCENDING);

			PreparedQuery pq = datastore.prepare(query);

			List<MmsIncomingMessage> list = new ArrayList<MmsIncomingMessage>();

			for (Entity result : pq.asIterable()) {
				MmsIncomingMessage msg = new MmsIncomingMessage();
				msg.setId((Long) result.getProperty(ID_FIELD));
				msg.setUserId((String) result.getProperty(USER_ID_FIELD));
				msg.setEmailAddress((String) result.getProperty(EMAIL_ADDRESS_FIELD));
				msg.setSender((String) result.getProperty(SENDER_FIELD));
				msg.setMessageText((String) result.getProperty(MESSAGE_TEXT_FIELD));
				msg.setBlobKey((String) result.getProperty("blobKey"));
				msg.setMessageDate((Long) result.getProperty(MESSAGE_DATE_FIELD));
				list.add(msg);
			}

			return list;
		} catch (Exception e) {
			log.log(Level.SEVERE, "caught exception " + e.getMessage());
			log.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
		}

		return new ArrayList<MmsIncomingMessage>();
	}

	/**
	 * Finds all incoming MMS messages from this user
	 * 
	 * @param recipient the {@link * @param recipient the {@link SmsIncomingMessage} id} id
	 * @return the associated {@link List<MmsIncomingMessage>}, or null if not found
	 */
	public List<MmsIncomingMessage> findAllIncomingMms(String recipient) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		try {
			Query query = new Query(MmsIncomingMessage.class.getSimpleName());
			query.addFilter(EMAIL_ADDRESS_FIELD, FilterOperator.EQUAL, userEmailAddress);
			query.addFilter(RECIPIENT_FIELD, FilterOperator.EQUAL, recipient);
			query.addSort(MESSAGE_DATE_FIELD, SortDirection.DESCENDING);

			PreparedQuery pq = datastore.prepare(query);

			List<MmsIncomingMessage> list = new ArrayList<MmsIncomingMessage>();

			for (Entity result : pq.asIterable()) {
				MmsIncomingMessage msg = new MmsIncomingMessage();
				msg.setId((Long) result.getProperty(ID_FIELD));
				msg.setUserId((String) result.getProperty(USER_ID_FIELD));
				msg.setEmailAddress((String) result.getProperty(EMAIL_ADDRESS_FIELD));
				msg.setSender((String) result.getProperty(SENDER_FIELD));
				msg.setMessageText((String) result.getProperty(MESSAGE_TEXT_FIELD));
				msg.setBlobKey((String) result.getProperty("blobKey"));
				msg.setMessageDate((Long) result.getProperty(MESSAGE_DATE_FIELD));
				list.add(msg);
			}

			return list;
		} catch (Exception e) {
			log.log(Level.SEVERE, "caught exception " + e.getMessage());
			log.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
		}

		return new ArrayList<MmsIncomingMessage>();
	}

	/**
	 * Persist this object in the datastore.
	 */
	public SmsOutgoingMessage updateOutgoing(SmsOutgoingMessage item, boolean sendC2DM) {
		item.setUserId(getUserId());
		item.setEmailAddress(userEmailAddress);

		PersistenceManager pm = PMF.get().getPersistenceManager();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		try {
			Query query = new Query(SmsOutgoingMessage.class.getSimpleName());
			query.addFilter(EMAIL_ADDRESS_FIELD, FilterOperator.EQUAL, userEmailAddress);
			query.addFilter(RECIPIENT_FIELD, FilterOperator.EQUAL, item.getRecipient());
			query.addFilter(MESSAGE_DATE_FIELD, FilterOperator.EQUAL, item.getMessageDate());

			PreparedQuery pq = datastore.prepare(query);
			Entity result = pq.asSingleEntity();

			if (result == null) {
				pm.makePersistent(item);
			}

			updateConversations(item.getRecipient(), item.getMessageText(), null, item.getMessageDate());

			if (sendC2DM) {
				sendC2DMUpdate(C2DMConstants.NEW_MESSAGE + C2DMConstants.MESSAGE_SEPARATOR + item.getRecipient() + C2DMConstants.MESSAGE_SEPARATOR +
						item.getMessageText());
			}
			return item;
		} catch (Exception e) {
			log.log(Level.SEVERE, "caught exception " + e.getMessage());
			log.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
		} finally {
			pm.close();
		}

		return null;
	}

	/**
	 * Persist this object in the datastore.
	 */
	public SmsIncomingMessage updateIncoming(SmsIncomingMessage item) {
		item.setUserId(getUserId());
		item.setEmailAddress(userEmailAddress);

		PersistenceManager pm = PMF.get().getPersistenceManager();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		try {
			Query query = new Query(SmsIncomingMessage.class.getSimpleName());
			query.addFilter(EMAIL_ADDRESS_FIELD, FilterOperator.EQUAL, userEmailAddress);
			query.addFilter(SENDER_FIELD, FilterOperator.EQUAL, item.getSender());
			query.addFilter(MESSAGE_DATE_FIELD, FilterOperator.EQUAL, item.getMessageDate());

			PreparedQuery pq = datastore.prepare(query);
			Entity result = pq.asSingleEntity();

			if (result == null) {
				pm.makePersistent(item);
			}

			updateConversations(item.getSender(), item.getMessageText(), null, item.getMessageDate());

			return item;
		} catch (Exception e) {
			log.log(Level.SEVERE, "caught exception for " + item.getSender());
			log.log(Level.SEVERE, "caught exception " + e.getMessage());
			log.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
		} finally {
			pm.close();
		}

		return null;
	}

	/**
	 * Persist this object in the datastore.
	 */
	public MmsIncomingMessage updateIncoming(MmsIncomingMessage item) {
		item.setUserId(getUserId());
		item.setEmailAddress(userEmailAddress);

		PersistenceManager pm = PMF.get().getPersistenceManager();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		try {
			Query query = new Query(MmsIncomingMessage.class.getSimpleName());
			query.addFilter(EMAIL_ADDRESS_FIELD, FilterOperator.EQUAL, userEmailAddress);
			query.addFilter(SENDER_FIELD, FilterOperator.EQUAL, item.getSender());
			query.addFilter(MESSAGE_DATE_FIELD, FilterOperator.EQUAL, item.getMessageDate());

			PreparedQuery pq = datastore.prepare(query);
			Entity result = pq.asSingleEntity();

			if (result == null) {
				pm.makePersistent(item);
			}

			updateConversations(item.getSender(), item.getMessageText(), null, item.getMessageDate());

			return item;
		} catch (Exception e) {
			log.log(Level.SEVERE, "caught exception " + e.getMessage());
			log.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
		} finally {
			pm.close();
		}

		return null;
	}

	/**
	 * Remove this object from the data store.
	 */
	public void deleteOutgoing(Long id) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			SmsOutgoingMessage item = pm.getObjectById(SmsOutgoingMessage.class, id);
			pm.deletePersistent(item);
		} finally {
			pm.close();
		}
	}

	/**
	 * Remove this object from the data store.
	 */
	public void deleteIncoming(Long id) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			SmsIncomingMessage item = pm.getObjectById(SmsIncomingMessage.class, id);
			pm.deletePersistent(item);
		} finally {
			pm.close();
		}
	}

	/**
	 * Remove this object from the data store.
	 */
	public void deleteIncomingMms(Long id) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			MmsIncomingMessage item = pm.getObjectById(MmsIncomingMessage.class, id);
			pm.deletePersistent(item);
		} finally {
			pm.close();
		}
	}

	/**
	 * Remove all incoming and outgoing messages from/to this recipient
	 */
	public void deleteConversation(String recipient) {
		AsyncDatastoreService datastore = DatastoreServiceFactory.getAsyncDatastoreService();
		try {
			// Delete Incoming
			Query query = new Query(SmsIncomingMessage.class.getSimpleName());
			query.addFilter(EMAIL_ADDRESS_FIELD, FilterOperator.EQUAL, userEmailAddress);
			query.addFilter(SENDER_FIELD, FilterOperator.EQUAL, recipient);
			PreparedQuery pq = datastore.prepare(query);

			for (Entity result : pq.asIterable()) {
				datastore.delete(result.getKey());
			}

			// Delete outgoing
			query = new Query(SmsOutgoingMessage.class.getSimpleName());
			query.addFilter(EMAIL_ADDRESS_FIELD, FilterOperator.EQUAL, userEmailAddress);
			query.addFilter(RECIPIENT_FIELD, FilterOperator.EQUAL, recipient);
			pq = datastore.prepare(query);

			for (Entity result : pq.asIterable()) {
				datastore.delete(result.getKey());
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "caught exception " + e.getMessage());
		}
	}

	/**
	 * Finds all incoming messages from this user
	 * 
	 * @param recipient the {@link SmsIncomingMessage} id
	 * @return the associated {@link List<SmsIncomingMessage>}, or null if not found
	 */
	public AuthenticatedUser getAuthenticatedUser() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		try {
			Query query = new Query(AuthenticatedUser.class.getSimpleName());
			query.addFilter(EMAIL_ADDRESS_FIELD, FilterOperator.EQUAL, userEmailAddress);
			PreparedQuery pq = datastore.prepare(query);

			Entity result = pq.asSingleEntity();

			if (result == null) {
				return null;
			}

			AuthenticatedUser user = new AuthenticatedUser();
			user.setId((Long) result.getProperty(ID_FIELD));
			user.setEmailAddress((String) result.getProperty(EMAIL_ADDRESS_FIELD));
			user.setUserId((String) result.getProperty(USER_ID_FIELD));
			user.setAuthToken((String) result.getProperty("authToken"));
			user.setAuthTokenSecret((String) result.getProperty("authTokenSecret"));

			return user;
		}  catch (Exception e) {
			log.log(Level.SEVERE, "caught exception " + e.getMessage());
		}

		return null;
	}


	/**
	 * Get the id of the currently logged in user
	 * 
	 * @return the id of the currently logged in user
	 */
	public static String getUserId() {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		if (user != null) {
			return user.getUserId();
		} else {
			return null;
		}
	}

	/**
	 * Gets the email address of the currently logged in user
	 * 
	 * @return the email address of the currently logged in user
	 */
	public static String getUserEmail() {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		if (user != null) {
			return user.getEmail();
		} else {
			return null;
		}
	}

	/**
	 * Sends a C2DM message to the user's Android device (if one exists)
	 * 
	 * @param message The message to send
	 */
	public static void sendC2DMUpdate(String message) {
		ServletContext context = RequestFactoryServlet.getThreadLocalRequest().getSession().getServletContext();
		com.brianreber.messaging.server.Message msg = new com.brianreber.messaging.server.Message(context);
		msg.setMessage(message);
		msg.setRecipient(getUserEmail());
		msg.send();
	}

	/**
	 * Send an email to admins with the given subject and body
	 * 
	 * @param subject
	 * @param body
	 */
	public static void sendEmailToAdmins(String subject, Object body) {
		MailService service = MailServiceFactory.getMailService();

		Message msg = new Message();
		msg.setSender("reber.brian@gmail.com");
		msg.setSubject(subject);
		msg.setHtmlBody((body == null) ? "" : body.toString());
		try {
			service.sendToAdmins(msg);
		} catch (IOException e) {
			log.log(Level.WARNING, "Error emailing - sendEmailToAdmins");
			e.printStackTrace();
		}
	}
}
