package com.brianreber.messaging;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brianreber.messaging.shared.Constants;
import com.google.gdata.client.authn.oauth.GoogleOAuthHelper;
import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;

/**
 * @author breber
 *
 */
public class FetcherServlet extends HttpServlet {
	private static final long serialVersionUID = 6596185185860834928L;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
		oauthParameters.setOAuthConsumerKey(Constants.CONSUMER_KEY);
		oauthParameters.setOAuthConsumerSecret(Constants.CONSUMER_SECRET);

		// Set the scope. In general, we want to limit the scope as much as
		// possible. For this example, we just ask for access to all feeds.
		oauthParameters.setScope(Constants.SCOPE);

		// This sets the callback URL. This is where we want the user to be
		// sent after they have granted us access. Sometimes, developers
		// generate different URLs based on the environment. You should set
		// this value to "http://localhost:8888/step2" if you are running
		// the development server locally.
		oauthParameters.setOAuthCallback(req.getScheme() + "://" + req.getServerName() + "/UpgradeToken");

		GoogleOAuthHelper oauthHelper = new GoogleOAuthHelper(new OAuthHmacSha1Signer());

		try {
			// Remember that your request token is still unauthorized. We
			// need to first get a unique token for the user to promote.
			oauthHelper.getUnauthorizedRequestToken(oauthParameters);

			// Generate the authorization URL
			String approvalPageUrl = oauthHelper.createUserAuthorizationUrl(oauthParameters);

			// Store the token secret in the session. We use this later after
			// the user grants access. Note that this method isn't foolproof
			//  or even close. This assumes the user won't sign out of their
			// browser or the sessions are swept between the time the user
			// is redirected and the callback is invoked.
			req.getSession().setAttribute("oauthTokenSecret",
					oauthParameters.getOAuthTokenSecret());

			resp.sendRedirect(approvalPageUrl);
		} catch (OAuthException e) {
			resp.getWriter().print("OAuth Error");
			// We probably want to do something about this error
		}
	}
}
