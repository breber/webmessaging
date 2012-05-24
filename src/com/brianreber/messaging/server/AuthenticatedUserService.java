package com.brianreber.messaging.server;


public class AuthenticatedUserService {
	private static DataStore data = new DataStore();

	public static AuthenticatedUser getCurrentUser() {
		return data.getAuthenticatedUser();
	}
}
