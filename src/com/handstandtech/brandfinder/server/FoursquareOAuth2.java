package com.handstandtech.brandfinder.server;

import java.util.HashMap;
import java.util.Map;

import com.handstandtech.server.rest.RESTClientImpl;
import com.handstandtech.server.rest.RESTUtil;
import com.handstandtech.shared.model.rest.RESTResult;
import com.handstandtech.shared.model.rest.RequestMethod;

public class FoursquareOAuth2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		OAuthConsumer consumer = new DefaultOAuthConsumer(FoursquareConstants.LOCALHOST8888_CONSUMER_KEY,
//				FoursquareConstants.LOCALHOST8888_CONSUMER_SECRET);
//		consumer.setTokenWithSecret("AKUTT1GGBMLDUCVR02RWS54CBVBYBHWVHNEGNE3BBZMFGGVH",
//				"WHIGPSP1P5EGKDG32M1RSXFKRDVSDV2W2A12LYQIQTTWDKDK");

		Map<String, String> params = new HashMap<String, String>();
		params.put("oauth_token", "WHIGPSP1P5EGKDG32M1RSXFKRDVSDV2W2A12LYQIQTTWDKDK");
		params.put("limit", "500");
		String url = RESTUtil.createParamString("https://api.foursquare.com/v2/users/self/checkins", params);

		RESTClientImpl client = new RESTClientImpl();
		RESTResult result = client.request(RequestMethod.GET, url, null);
		System.out.println(result.toString());
	}

}
