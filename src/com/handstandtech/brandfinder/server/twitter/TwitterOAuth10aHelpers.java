package com.handstandtech.brandfinder.server.twitter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.handstandtech.foursquare.oauth.OAuthAuthenticator;
import com.handstandtech.server.rest.RESTUtil;
import com.handstandtech.server.rest.impl.RESTClientJavaNetImpl;
import com.handstandtech.shared.model.rest.RESTResult;
import com.handstandtech.shared.model.rest.RequestMethod;

public class TwitterOAuth10aHelpers {
	private static Logger log = LoggerFactory.getLogger(TwitterOAuth10aHelpers.class
			.getCanonicalName());

	private static String FOURSQUAREBRANDS_CONSUMER_KEY = "2EBADuJpc9I0wMLpWn1Pjw";
	private static String FOURSQUAREBRANDS_CONSUMER_SECRET = "5pwA6Pwlb07vuTwtV8GO3GfpHNAT8iqwQ3yFWi8bPE";

	public static String FOURSQUAREBRANDS_TOKEN = "244235295-Svxzxnyghp2uycUyXvQA15lPffuEkn0i17MAyFQ";
	public static String FOURSQUAREBRANDS_TOKEN_SECRET = "Ho4hxNSjRRbGTQsrsfnnCqvEgTxELNLcmbXGvZvzM";

	public static OAuthProvider getTwitterOAuthProvider() {
		String baseUrl = "https://api.twitter.com/oauth/";
		OAuthProvider provider = new DefaultOAuthProvider(baseUrl
				+ "request_token", baseUrl + "access_token", baseUrl
				+ "authorize");
		return provider;
	}

	@SuppressWarnings("unused")
	public static void main(String args[]) throws OAuthMessageSignerException,
			OAuthNotAuthorizedException, OAuthExpectationFailedException,
			OAuthCommunicationException {
		// @HandstandTech for FoursquareBrands.com

		OAuthProvider provider = TwitterOAuth10aHelpers
				.getTwitterOAuthProvider();
		OAuthConsumer consumer = getConsumer();

		// Get Tokens and AUTH URL
		// getAccessTokenAndAuthUrl(consumer, provider);

		consumer.setTokenWithSecret(FOURSQUAREBRANDS_TOKEN,
				FOURSQUAREBRANDS_TOKEN_SECRET);
		

//		// GET A REAL TOKENS
//		String oauth_verifier = "Z8A6fbKcGyHC07nhEg2SEmJLcQWVMfjuCSR3SK2F4M";
//		provider.retrieveAccessToken(consumer, oauth_verifier);

		getCurrentUser(consumer);

		printConsumerInfo(consumer);

	}

	private static void getAccessTokenAndAuthUrl(OAuthConsumer consumer,
			OAuthProvider provider) {
		String authUrl = null;
		try {
			authUrl = provider.retrieveRequestToken(consumer, "");
		} catch (OAuthMessageSignerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthNotAuthorizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.info("Auth URL: " + authUrl);
	}

	private static void getCurrentUser(OAuthConsumer consumer) {
		String baseUrl = "https://api.twitter.com/1/account/verify_credentials.json";
		RESTClientJavaNetImpl client = new RESTClientJavaNetImpl();
		RESTResult result = client.request(RequestMethod.GET, baseUrl,
				new OAuthAuthenticator(consumer));

		log.info(result.toString());
	}

	public static void updateStatus(OAuthConsumer consumer, String status) {
		String baseUrl = "http://api.twitter.com/1/statuses/update.json";
		Map<String, String> params = new HashMap<String, String>();
		try {
			params.put("status", URLEncoder.encode(status, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String url = RESTUtil.createFullUrl(baseUrl, params);

		RESTClientJavaNetImpl client = new RESTClientJavaNetImpl();
		RESTResult result = client.request(RequestMethod.POST, url,
				new OAuthAuthenticator(consumer));

		log.info(result.toString());
	}

	public static void printConsumerInfo(OAuthConsumer consumer) {
		log.info("Token: " + consumer.getToken());
		log.info("Token Secret: " + consumer.getTokenSecret());
		log.info("Consumer Key: " + consumer.getConsumerKey());
		log.info("Consumer Secret: " + consumer.getConsumerSecret());
	}

	public static OAuthConsumer getConsumer() {
		OAuthConsumer consumer = new DefaultOAuthConsumer(
				FOURSQUAREBRANDS_CONSUMER_KEY, FOURSQUAREBRANDS_CONSUMER_SECRET);
		return consumer;
	}
}