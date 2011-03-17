package com.handstandtech.brandfinder.server.twitter;

import java.io.UnsupportedEncodingException;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.handstandtech.brandfinder.server.dao.DAO;
import com.handstandtech.brandfinder.server.dao.impl.CachingDAOImpl;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser.Contact;
import com.handstandtech.twitter.server.TwitterHelper;

public class FoursquareBrandsTwitter {
	private static Logger log = LoggerFactory
			.getLogger(FoursquareBrandsTwitter.class);

	private static String FOURSQUAREBRANDS_CONSUMER_KEY = "2EBADuJpc9I0wMLpWn1Pjw";
	private static String FOURSQUAREBRANDS_CONSUMER_SECRET = "5pwA6Pwlb07vuTwtV8GO3GfpHNAT8iqwQ3yFWi8bPE";

	public static String FOURSQUAREBRANDS_TOKEN = "244235295-Svxzxnyghp2uycUyXvQA15lPffuEkn0i17MAyFQ";
	public static String FOURSQUAREBRANDS_TOKEN_SECRET = "Ho4hxNSjRRbGTQsrsfnnCqvEgTxELNLcmbXGvZvzM";

	public static void sendOutTweetOfNewBrands(String brandId)
			throws UnsupportedEncodingException {

		DAO dao = new CachingDAOImpl();
		FoursquareUser brand = dao.findFoursquareUser(brandId);
		String type = brand.getType();
		if (type.equals("brand")) {

			String url = "http://FoursquareBrands.com/brands" + "/" + brandId;

			String status = "[New Brand Found] - " + brand.getName()
					+ getTwitterHandle(brand) + " - " + url + " #Foursquare";

			// Make sure it's 140 or less
			if (status.length() >= 140) {
				status = status.substring(0, 137);
				status = status + "...";
			}

			TwitterHelper twitter = new TwitterHelper(getPreloadedConsumer());
			twitter.printConsumerInfo();
			log.info("Updating Status: " + status);
			twitter.updateStatus(status);
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

	public static OAuthConsumer getPreloadedConsumer() {
		OAuthConsumer consumer = new DefaultOAuthConsumer(
				FOURSQUAREBRANDS_CONSUMER_KEY, FOURSQUAREBRANDS_CONSUMER_SECRET);
		consumer.setTokenWithSecret(FOURSQUAREBRANDS_TOKEN,
				FOURSQUAREBRANDS_TOKEN_SECRET);
		return consumer;
	}
}
