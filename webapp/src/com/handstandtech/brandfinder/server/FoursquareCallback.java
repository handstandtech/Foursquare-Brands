package com.handstandtech.brandfinder.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.handstandtech.brandfinder.server.util.SessionHelper;
import com.handstandtech.brandfinder.shared.model.User;
import com.handstandtech.foursquare.server.FoursquareConstants;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;
import com.handstandtech.foursquare.v2.FoursquareAPIv2;
import com.handstandtech.foursquare.v2.exception.FoursquareNot200Exception;
import com.handstandtech.foursquare.v2.impl.CachingFoursquareAPIv2Impl;
import com.handstandtech.server.rest.RESTClient;
import com.handstandtech.server.rest.RESTUtil;
import com.handstandtech.server.rest.auth.NullAuthenticator;
import com.handstandtech.server.rest.impl.RESTClientAppEngineURLFetchImpl;
import com.handstandtech.shared.model.rest.RESTResult;
import com.handstandtech.shared.model.rest.RequestMethod;

/**
 * An OAuth callback handler.
 * 
 * @author Sam Edwards
 */
public class FoursquareCallback extends HttpServlet {

	/**
	 * Default Serialization UID
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory
			.getLogger(FoursquareCallback.class);

	/**
	 * Exchange an OAuth request token for an access token, and store the latter
	 * in cookies.
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		HttpSession session = request.getSession();

		String code = request.getParameter("code");
		String baseUrl = "https://foursquare.com/oauth2/access_token";
		Map<String, String> params = new HashMap<String, String>();
		String clientId = FoursquareConstants.getClientId(request);
		params.put("client_id", clientId);
		String clientSecret = FoursquareConstants.getClientSecret(request);
		params.put("client_secret", clientSecret);
		params.put("grant_type", "authorization_code");
		params.put("redirect_uri", RESTUtil.getBaseUrl(request)
				+ FoursquareConstants.FOURSQUARE_CALLBACK);
		params.put("code", code);
		String fullUrl = RESTUtil.createFullUrl(baseUrl, params);

		RESTClient client = new RESTClientAppEngineURLFetchImpl();
		RESTResult result = client.request(RequestMethod.GET, fullUrl);

		try {
			JSONObject jsonObj = new JSONObject(result.getResponseBody());
			String accessToken = jsonObj.getString("access_token");

			log.info("access_token -> " + accessToken);

			FoursquareAPIv2 helper = new CachingFoursquareAPIv2Impl(accessToken);
			FoursquareUser foursquareUser = null;
			try {
				foursquareUser = helper.getUserInfo("self");
				DAO dao = new CachingDAOImpl();

				User user = new User();
				user.setToken(accessToken);
				user.setId(foursquareUser.getId());
				user.setFoursquareUser(foursquareUser);
				dao.updateUser(user);
				SessionHelper.setCurrentUser(session, user);
			} catch (FoursquareNot200Exception e) {
				log.error(e.getMessage(), e);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		redirectToFoursquareApp(session, response);
	}

	private void redirectToFoursquareApp(HttpSession session,
			HttpServletResponse response) {
		try {
			String continueUrl = SessionHelper.getContinueUrl(session);
			if (continueUrl == null) {
				continueUrl = "/";
			}
			response.sendRedirect(continueUrl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
