package com.brianreber.messaging;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

/**
 * Uploads a blob into the blobstore
 * 
 * @author breber
 */
public class Upload extends HttpServlet {
	private static final long serialVersionUID = -1015578241865637081L;

	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
		List<BlobKey> blobKey = blobs.get("myFile");

		if (blobKey.size() > 0) {
			res.sendRedirect("/serve?blob-key=" + blobKey.get(0).getKeyString().getBytes() + "&android=true");
		}
	}
}
