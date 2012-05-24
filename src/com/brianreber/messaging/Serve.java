package com.brianreber.messaging;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

/**
 * Serves a blob from the blobstore
 * 
 * @author breber
 */
public class Serve extends HttpServlet {
	private static final long serialVersionUID = -6971245642794526026L;

	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		String blobParam = req.getParameter("blob-key");
		String isForAndroid = req.getParameter("android");

		if (isForAndroid != null) {
			res.getOutputStream().write(blobParam.getBytes());
		} else if (blobParam != null) {
			BlobKey blobKey = new BlobKey(blobParam);
			blobstoreService.serve(blobKey, res);
		}
	}
}