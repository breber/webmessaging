package com.brianreber.messaging;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brianreber.messaging.server.DataStore;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

/**
 * Get a Token to use to open a Channel with the server
 * 
 * @author breber
 */
public class GetChannelToken extends HttpServlet {
	private static final long serialVersionUID = 6596185185860834928L;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String userId = DataStore.getUserId();

		if (userId != null) {
			ChannelService channelService = ChannelServiceFactory.getChannelService();

			String token = channelService.createChannel(userId);

			resp.setContentType("text/plain");
			resp.getWriter().write(token);
		}
	}
}
