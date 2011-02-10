package com.handstandtech.brandfinder.server.cron;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.visualization.datasource.datatable.DataTable;
import com.handstandtech.brandfinder.server.twitter.FoursquareBrandsTwitter;
import com.handstandtech.foursquare.server.FoursquareHelper;

/**
 * The server side implementation of the RPC service.
 */
public class TweetServlet extends HttpServlet {

	/**
	 * Default Serialization UID
	 */
	private static final long serialVersionUID = 1L;

	protected final Logger log = Logger.getLogger(getClass().getName());

	/**
	 * Handle a GET Request and serve the appropriate {@link DataTable}
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String brandId = request.getParameter("id");
		
		// Send out tweet about it!
		if (brandId != null && !brandId.isEmpty()) {
			//Make sure we're not on localhost so that we don't tweet during testing
			if (FoursquareHelper.isProduction(request.getRequestURL().toString()) == true) {
				FoursquareBrandsTwitter.sendOutTweetOfNewBrands(brandId);
			}
		}
	}
}
