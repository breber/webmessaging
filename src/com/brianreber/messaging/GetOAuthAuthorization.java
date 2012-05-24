package com.brianreber.messaging;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.brianreber.messaging.server.AuthenticatedUser;
import com.brianreber.messaging.server.AuthenticatedUserService;
import com.brianreber.messaging.shared.Constants;
import com.google.android.c2dm.server.PMF;

/**
 * @author breber
 *
 */
public class GetOAuthAuthorization extends HttpServlet {
	private static final long serialVersionUID = 6596185185860834928L;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			JSONObject obj = new JSONObject();
			AuthenticatedUser user = AuthenticatedUserService.getCurrentUser();

			if (user != null) {
				obj.put(Constants.HTML_AUTH_KEY, user.getAuthToken());
				obj.put(Constants.HTML_AUTH_SECRET_KEY, user.getAuthTokenSecret());
				obj.put(Constants.HTML_EMAIL_KEY, user.getEmailAddress());
			} else {
				obj.put(Constants.HTML_AUTH_KEY, JSONObject.NULL);
				obj.put(Constants.HTML_AUTH_SECRET_KEY, JSONObject.NULL);
				obj.put(Constants.HTML_EMAIL_KEY, JSONObject.NULL);
			}

			resp.getWriter().write(obj.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}

	}
}
