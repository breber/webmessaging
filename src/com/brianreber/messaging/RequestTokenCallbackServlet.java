package com.brianreber.messaging;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brianreber.messaging.server.AuthenticatedUser;
import com.brianreber.messaging.shared.Constants;
import com.google.android.c2dm.server.PMF;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gdata.client.authn.oauth.GoogleOAuthHelper;
import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;

/**
 * @author breber
 *
 */
public class RequestTokenCallbackServlet extends HttpServlet {
	private static final long serialVersionUID = -6308106993256120696L;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// Create an instance of GoogleOAuthParameters
		GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
		oauthParameters.setOAuthConsumerKey(Constants.CONSUMER_KEY);
		oauthParameters.setOAuthConsumerSecret(Constants.CONSUMER_SECRET);

		GoogleOAuthHelper oauthHelper = new GoogleOAuthHelper(new OAuthHmacSha1Signer());

		// Remember the token secret that we stashed? Let's get it back
		// now. We need to add it to oauthParameters
		String oauthTokenSecret = (String) req.getSession().getAttribute("oauthTokenSecret");
		oauthParameters.setOAuthTokenSecret(oauthTokenSecret);

		// The query string should contain the oauth token, so we can just
		// pass the query string to our helper object to correctly
		// parse and add the parameters to our instance of oauthParameters
		oauthHelper.getOAuthParametersFromCallback(req.getQueryString(), oauthParameters);

		try {

			// Now that we have all the OAuth parameters we need, we can
			// generate an access token and access token secret. These
			// are the values we want to keep around, as they are
			// valid for all API calls in the future until a user revokes
			// our access.
			String accessToken = oauthHelper.getAccessToken(oauthParameters);
			String accessTokenSecret = oauthParameters.getOAuthTokenSecret();

			PersistenceManager pm = PMF.get().getPersistenceManager();
			UserService users = UserServiceFactory.getUserService();
			User user = users.getCurrentUser();

			if (user != null) {
				AuthenticatedUser authUser = new AuthenticatedUser();
				authUser.setAuthToken(accessToken);
				authUser.setAuthTokenSecret(accessTokenSecret);
				authUser.setEmailAddress(user.getEmail());
				authUser.setUserId(user.getUserId());
				pm.makePersistent(authUser);
			}

			resp.sendRedirect("/");
		} catch (OAuthException e) {
			// Something went wrong. Usually, you'll end up here if we have invalid
			// oauth tokens
		}
	}
}