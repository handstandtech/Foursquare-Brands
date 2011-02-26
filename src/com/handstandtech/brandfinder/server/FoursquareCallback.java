package com.handstandtech.brandfinder.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import com.handstandtech.brandfinder.server.util.SessionHelper;
import com.handstandtech.brandfinder.shared.model.User;
import com.handstandtech.foursquare.server.FoursquareConstants;
import com.handstandtech.foursquare.server.FoursquareHelper;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;
import com.handstandtech.server.rest.RESTClientImpl;
import com.handstandtech.server.rest.RESTUtil;
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

	protected final Logger log = Logger.getLogger(getClass().getName());

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
		String fullUrl = RESTUtil.createParamString(baseUrl, params);

		RESTClientImpl client = new RESTClientImpl();
		RESTResult result = client.request(RequestMethod.GET, fullUrl, null);

		System.out.println(result);

		try {
			JSONObject jsonObj = new JSONObject(result.getResponseBody());
			String accessToken = jsonObj.getString("access_token");
			
			FoursquareHelper helper = new FoursquareHelper(accessToken);
			FoursquareUser foursquareUser = helper.getUserInfo("self");
			DAO dao = new DAO();

			User user = new User();
			user.setToken(accessToken);
			user.setId(foursquareUser.getId());
			user.setFoursquareUser(foursquareUser);
			dao.updateUser(user);
			SessionHelper.setCurrentUser(session, user);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		redirectToFoursquareApp(session, response);
	}

	private void redirectToFoursquareApp(HttpSession session, HttpServletResponse response) {
		try {
			String continueUrl = SessionHelper.getContinueUrl(session);
			if(continueUrl==null){
				continueUrl="/";
			}
			response.sendRedirect(continueUrl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
