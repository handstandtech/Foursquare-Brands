package com.handstandtech.brandfinder.server.foursquare;

import javax.servlet.http.HttpServletRequest;

import com.google.inject.Singleton;
import com.handstandtech.brandfinder.server.constants.FoursquareServerConstants;
import com.handstandtech.foursquare.oauth2.FoursquareOAuth2Login;
import com.handstandtech.restclient.server.util.RESTUtil;

@SuppressWarnings("serial")
@Singleton
public class MyFoursquareOAuth2Login extends FoursquareOAuth2Login {
	protected String getClientId(HttpServletRequest request) {
		String requestUrl = request.getRequestURL().toString();
		return FoursquareServerConstants.INSTANCE.getClientId(requestUrl);
	}

	protected String getCallbackUrl(HttpServletRequest request) {
		return RESTUtil.getBaseUrl(request)
				+ FoursquareServerConstants.INSTANCE.getCallbackUri(request);
	}
}
