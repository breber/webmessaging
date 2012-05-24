package com.brianreber.messaging;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

/**
 * A servlet that creates a blobstore url
 * 
 * @author breber
 */
public class GetBlobUrl extends HttpServlet {
	private static final long serialVersionUID = -9039863446465469805L;

	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		res.getWriter().append(blobstoreService.createUploadUrl("/upload"));
	}
}
