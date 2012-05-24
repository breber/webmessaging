package com.brianreber.messaging.shared;

/**
 * Constants used in the Messaging app
 * 
 * @author breber
 */
public class Constants {

	// URLs and authentication information
	public static final String SCOPE 			= "https://www.google.com/m8/feeds";
	public static final String FEED_URL 		= SCOPE + "/contacts/default/full";
	public static final String CONSUMER_KEY 	= "apps.brianreber.com";
	public static final String CONSUMER_SECRET 	= "aBGGDSJFith0R8BdUbeOIVlc";


	// Default image
	public static final String DEFAULT_IMAGE = "/images/contact.png";


	// Session storage keys
	public static final String HTML_EMAIL_KEY 				= "email";
	public static final String HTML_AUTH_KEY 				= "authToken";
	public static final String HTML_AUTH_SECRET_KEY 		= "authTokenSecret";
	public static final String HTML_MY_PIC_KEY 				= "myAccountPicture";
	public static final String HTML_USER_PIC_KEY_PATTERN 	= "$1;$2";
}
