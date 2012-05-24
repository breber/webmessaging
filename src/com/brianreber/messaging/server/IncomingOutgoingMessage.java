package com.brianreber.messaging.server;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Text;

/**
 * A wrapper class for display on the UI.
 * 
 * @author breber
 */
@Entity
public class IncomingOutgoingMessage implements Comparable<IncomingOutgoingMessage> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String messageText;
	private String blob;
	private Date messageDate;
	private Text photoUrl;
	private String emailAddress;

	public IncomingOutgoingMessage() {	}

	/**
	 * @param name
	 * @param messageText
	 * @param messageDate
	 * @param photoUrl
	 */
	public IncomingOutgoingMessage(String name, String messageText, String blob,
			Date messageDate, String photoUrl) {
		this.name = name;
		this.messageText = messageText;
		this.blob = blob;
		this.messageDate = messageDate;
		this.photoUrl = new Text(photoUrl);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the messageText
	 */
	public String getMessageText() {
		return messageText;
	}
	/**
	 * @param messageText the messageText to set
	 */
	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}
	/**
	 * @return the blob
	 */
	public String getBlob() {
		return blob;
	}

	/**
	 * @param blob the blob to set
	 */
	public void setBlob(String blob) {
		this.blob = blob;
	}

	/**
	 * @return the messageDate
	 */
	public Date getMessageDate() {
		return messageDate;
	}
	/**
	 * @param messageDate the messageDate to set
	 */
	public void setMessageDate(Date messageDate) {
		this.messageDate = messageDate;
	}
	/**
	 * @return the photoUrl
	 */
	public String getPhotoUrl() {
		return photoUrl.getValue();
	}
	/**
	 * @param photoUrl the photoUrl to set
	 */
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = new Text(photoUrl);
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(IncomingOutgoingMessage o) {
		return -messageDate.compareTo(o.messageDate);
	}

}
