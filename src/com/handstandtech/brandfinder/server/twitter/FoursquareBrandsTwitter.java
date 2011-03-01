package com.handstandtech.brandfinder.server.twitter;

import java.io.UnsupportedEncodingException;

import oauth.signpost.OAuthConsumer;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;
import com.handstandtech.brandfinder.server.DAO;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser.Contact;

public class FoursquareBrandsTwitter {
	private static Long ONE_SECOND = 1000L;
	private static Long ONE_MINUTE = 60 * ONE_SECOND;
	private static Long ONE_HOUR = 60 * ONE_MINUTE;
	private static Long ONE_DAY = 24 * ONE_HOUR;

	public static void sendOutTweetOfNewBrands(String brandId)
			throws UnsupportedEncodingException {

		DAO dao = new DAO();

		StringBuffer sb = new StringBuffer();
		FoursquareUser brand = dao.getFoursquareUser(brandId);
		String type = brand.getType();
		if (type.equals("brand")) {

			String url = "http://FoursquareBrands.com/brands"+"/" + brandId;

			String status = "[New Brand Found] - " + brand.getName()
					+ getTwitterHandle(brand) + " - " + url + " #Foursquare";

			// Make sure it's 140 or less
			if (status.length() >= 140) {
				status = status.substring(0, 137);
				status = status + "...";
			}

			OAuthConsumer consumer = TwitterOAuth10aHelpers.getConsumer();
			consumer.setTokenWithSecret(
					TwitterOAuth10aHelpers.FOURSQUAREBRANDS_TOKEN,
					TwitterOAuth10aHelpers.FOURSQUAREBRANDS_TOKEN_SECRET);
			TwitterOAuth10aHelpers.printConsumerInfo(consumer);
			TwitterOAuth10aHelpers.updateStatus(consumer, status);
		}
	}

	private static String getTwitterHandle(FoursquareUser brand) {
		Contact contact = brand.getContact();
		if (contact != null) {
			String twitter = contact.getTwitter();
			if (twitter != null && !twitter.isEmpty()) {
				return " @" + twitter;
			}
		}
		return "";
	}

	public static void queueTweet(String brandId) {
		Queue queue = QueueFactory.getDefaultQueue();
		TaskOptions followerCountTask = TaskOptions.Builder.withDefaults();
		followerCountTask.url("/tasks/tweet");
		followerCountTask.method(Method.GET);
		followerCountTask.param("id", brandId);
		queue.add(followerCountTask);
	}
}
