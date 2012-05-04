package com.handstandtech.brandfinder.server.cron;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

/**
 * The server side implementation of the RPC service.
 */
@Singleton
public class TweetServlet extends HttpServlet {

	/**
	 * Default Serialization UID
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory
			.getLogger(TweetServlet.class);

	/**
	 * Handle a GET Request and serve the appropriate {@link DataTable}
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String brandId = request.getParameter("id");

		log.warn("Not sending new brand tweet for : " + brandId);
		/*
		log.info("Send out tweet about brand id: " + brandId);
		if (brandId != null && !brandId.isEmpty()) {
			log.info("Make sure we're not on localhost so that we don't tweet during testing");
			if (FoursquareAPIv2Impl.isProduction(request.getRequestURL()
					.toString()) == true) {
				FoursquareBrandsTwitter.sendOutTweetOfNewBrands(brandId);
			} else {
				log.warn("We are not in production currently.  Not going to tweet.");
			}
		}*/
	}
}
