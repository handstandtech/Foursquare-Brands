package com.handstandtech.brandfinder.server.cron;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oauth.signpost.OAuthConsumer;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;
import com.google.visualization.datasource.datatable.DataTable;
import com.handstandtech.brandfinder.server.DAO;
import com.handstandtech.brandfinder.server.tasks.FollowerCountTaskServlet;
import com.handstandtech.brandfinder.server.twitter.TwitterOAuth10aHelpers;
import com.handstandtech.brandfinder.shared.model.BrandDiscovered;
import com.handstandtech.brandfinder.shared.util.ModelUtils;
import com.handstandtech.foursquare.server.FoursquareHelper;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;

/**
 * The server side implementation of the RPC service.
 */
public class TweetCronServlet extends HttpServlet {

	/**
	 * Default Serialization UID
	 */
	private static final long serialVersionUID = 1L;

	protected final Logger log = Logger.getLogger(getClass().getName());

	private static Long ONE_SECOND = 1000L;
	private static Long ONE_MINUTE = 60 * ONE_SECOND;
	private static Long ONE_HOUR = 60 * ONE_MINUTE;
	private static Long ONE_DAY = 24 * ONE_HOUR;

	/**
	 * Handle a GET Request and serve the appropriate {@link DataTable}
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Date date = new Date();

		// Find Which New Brands were Added and Follow Them
		sendOutTweetOfNewBrands(date);
	}

	private void sendOutTweetOfNewBrands(Date date)
			throws UnsupportedEncodingException {

		DAO dao = new DAO();

		// See if there are any new brands in the last day
		List<BrandDiscovered> brandsDiscovered = dao
				.getBrandDiscoveredSince(new Date(date.getTime() - (ONE_HOUR*12)));
		StringBuffer sb = new StringBuffer();
		sb.append(brandsDiscovered.size() + " New Brands on"
				+ " http://FoursquareBrands.com - ");
		int count = 0;
		for (BrandDiscovered b : brandsDiscovered) {
			count++;
			String brandId = b.getBrandId();
			FoursquareUser brand = dao.getFoursquareUser(brandId);
			sb.append(getBrandName(brand));
			if (count < brandsDiscovered.size()) {
				sb.append(", ");
			}
		}

		String status = sb.toString();
		
		// Make sure it's 140 or less
		if(status.length()>=140) {
			status = status.substring(0, 137);
			status = status + "...";
		}

		if (brandsDiscovered.size() > 0) {
			OAuthConsumer consumer = TwitterOAuth10aHelpers.getConsumer();
			consumer.setTokenWithSecret(
					TwitterOAuth10aHelpers.FOURSQUAREBRANDS_HANDSTANDTECH_TOKEN,
					TwitterOAuth10aHelpers.FOURSQUAREBRANDS_HANDSTANDTECH_TOKEN_SECRET);
			TwitterOAuth10aHelpers.printConsumerInfo(consumer);
			TwitterOAuth10aHelpers.updateStatus(consumer, status);
		}
	}

	private String getBrandName(FoursquareUser brand) {
		String first = brand.getFirstName();
		String last = brand.getLastName();
		if (last == null) {
			return first;
		} else {
			return first + " " + last;
		}
	}
}
