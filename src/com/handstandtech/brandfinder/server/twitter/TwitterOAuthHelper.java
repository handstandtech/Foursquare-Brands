//package com.handstandtech.brandfinder.server.twitter;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.lang.reflect.Type;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import oauth.signpost.OAuthConsumer;
//
//import org.apache.log4j.Logger;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonDeserializationContext;
//import com.google.gson.JsonDeserializer;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonParseException;
//import com.google.gson.reflect.TypeToken;
//import com.handstandtech.shared.model.Activity;
//import com.handstandtech.shared.rpc.APICallResult;
//import com.handstandtech.twitter.shared.SearchResult;
//import com.handstandtech.twitter.shared.Tweet;
//import com.handstandtech.twitter.shared.TwitterUser;
//
///**
// * Talks to Twitter.
// * 
// * @author Sam Edwards
// * @version 2010.09.09 - Rewritten because this file was deleted.
// */
//public class TwitterOAuthHelper {
//	// Wed Jan 21 02:00:49 +0000 2009
//	protected static final String TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss Z yyyy";
//	private static Logger log = Logger.getLogger(TwitterOAuthHelper.class);
//
//	public TwitterOAuthHelper() {
//	}
//
//	public static APICallResult callAPIUrlWithoutOAuth(String endpoint) {
//		return callAPIUrl(null, endpoint, null);
//	}
//
//	public static APICallResult callAPIUrl(OAuthConsumer consumer, String endpoint) {
//		return callAPIUrl(consumer, endpoint, null);
//	}
//
//	/**
//	 * Call the URL with the OAuthConsumer and return the response string.
//	 * 
//	 * @param consumer
//	 * @param endpoint
//	 * @return
//	 * @throws Exception
//	 */
//	public static APICallResult callAPIUrl(OAuthConsumer consumer, String endpoint, String requestMethod) {
//		log.debug("Calling URL: " + endpoint);
//		APICallResult callResult = new APICallResult();
//		callResult.setUrl(endpoint);
//
//		StringBuffer jsonBuffer = new StringBuffer();
//		HttpURLConnection newRequest = null;
//		URL url;
//		BufferedReader in = null;
//		InputStreamReader isr = null;
//		try {
//			url = new URL(endpoint);
//			newRequest = (HttpURLConnection) url.openConnection();
//			int ONE_SECOND = 1000;
//			newRequest.setConnectTimeout(20 * ONE_SECOND);
//			newRequest.setReadTimeout(20 * ONE_SECOND);
//			if (requestMethod != null) {
//				newRequest.setRequestMethod(requestMethod);
//			}
//
//			if (consumer != null) {
//				consumer.sign(newRequest);
//			}
//			newRequest.connect();
//			isr = new InputStreamReader(newRequest.getInputStream(), "UTF-8");
//			in = new BufferedReader(isr);
//			String inputLine;
//
//			while ((inputLine = in.readLine()) != null) {
//				log.trace(inputLine);
//				jsonBuffer.append(inputLine);
//			}
//
//			try {
//				callResult.setResponseCode(newRequest.getResponseCode());
//			} catch (IOException e) {
//				// DO NOTHING
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			try {
//				in.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			newRequest.disconnect();
//		}
//
//		callResult.setBody(jsonBuffer.toString());
//		return callResult;
//	}
//
//	public TwitterUser getCurrentUserInfo(OAuthConsumer consumer) {
//		String url = "http://twitter.com/account/verify_credentials.json";
//		APICallResult result = callAPIUrl(consumer, url);
//		Gson gson = getGsonWithTwitterDateFormatting();
//		return gson.fromJson(result.getBody(), TwitterUser.class);
//	}
//
//	private Gson getGsonWithTwitterDateFormatting() {
//		Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
//			public Date deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2)
//					throws JsonParseException {
//				String date = arg0.getAsString();
//				SimpleDateFormat df = new SimpleDateFormat(TWITTER_DATE_FORMAT);
//
//				try {
//					return df.parse(date);
//				} catch (ParseException e) {
//					throw new JsonParseException("Cannot parse date: " + e.getMessage(), e);
//				}
//			}
//
//		}).create();
//		return gson;
//	}
//
//	public static List<SearchResult> doSearch(OAuthConsumer consumer, String query) {
//		String url = "http://search.twitter.com/search.json?q=" + query;
//		APICallResult result = callAPIUrl(consumer, url);
//		Gson gson = new Gson();
//		System.out.println("Search Response: " + result.getBody());
//		System.err.println("UNIMPLEMENTED");
//		return new ArrayList<SearchResult>();
//	}
//
//	public static void postTweet(OAuthConsumer consumer, String message) {
//		String url = "http://twitter.com/statuses/update.json?status=" + message + " #beerdiots";
//		APICallResult result = callAPIUrl(consumer, url, "POST");
//	}
//
//	public List<Activity> getTweets(OAuthConsumer consumer, String uid) {
//
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("screen_name", uid);
//		params.put("count", "20");
//		params.put("trim_user", "true");
//
//		String userFeedBaseUrl = "http://api.twitter.com/1/statuses/user_timeline.json";
//		String url = userFeedBaseUrl + getQueryString(params);
//
//		APICallResult result = callAPIUrl(consumer, url);
//
//		Gson gson = getGsonWithTwitterDateFormatting();
//
//		Type collectionType = new TypeToken<Collection<Tweet>>() {
//		}.getType();
//		List<Activity> tweets = gson.fromJson(result.getBody(), collectionType);
//
//		return tweets;
//
//	}
//
//	private String getQueryString(Map<String, String> params) {
//		StringBuffer url = new StringBuffer();
//		url.append("?");
//		for (String param : params.keySet()) {
//			url.append(param + "=" + params.get(param) + "&");
//		}
//		url = url.delete(url.length() - 1, url.length());
//		return url.toString();
//	}
//
//}
