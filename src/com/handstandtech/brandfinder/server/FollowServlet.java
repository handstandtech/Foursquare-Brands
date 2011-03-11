package com.handstandtech.brandfinder.server;

import java.io.IOException;

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
import com.handstandtech.foursquare.server.FoursquareHelper;
import com.handstandtech.foursquare.server.FoursquareUtils;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;
import com.handstandtech.shared.model.rest.RESTResult;

/**
 * The server side implementation of the RPC service.
 */
public class FollowServlet extends HttpServlet {

	/**
	 * Default Serialization UID
	 */
	private static final long serialVersionUID = 1L;

	protected final Logger log = LoggerFactory.getLogger(getClass().getName());

	/**
	 * Handle a Follow Request
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		doFollow(request, response);
	}

//	/**
//	 * Handles a Follow Request
//	 */
//	@Override
//	protected void doGet(HttpServletRequest request,
//			HttpServletResponse response) throws IOException {
//		doFollow(request, response);
//	}

	private void doFollow(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		JSONObject meta = new JSONObject();
		HttpSession session = request.getSession();
		User currentUser = SessionHelper.getCurrentUser(session);
		if (currentUser != null) {
			FoursquareHelper helper = new FoursquareHelper(
					currentUser.getToken());

			String id = request.getParameter("id");
			RESTResult result = helper.friendRequest(id);
			try {
				JSONObject jsonResultObj = new JSONObject(
						result.getResponseBody());
				meta = jsonResultObj.getJSONObject("meta");
				int responseCode = meta.getInt("code");
				if (responseCode == 200) {
					FoursquareUser user = FoursquareUtils
							.getFoursquareUserFromResult(result);
					if (user != null) {
						log.info("Successfully Followed: " + user.getName());
					}

					DAO dao = new DAO();
					dao.updateFoursquareUser(user);
				} else {
					log.warn("Follow Unsuccessful, Response Code: "
							+ responseCode);
					log.warn("Meta: " + meta.toString());
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
			log.warn("No current user.");
			try {
				meta.put("errorType", "Not Logged into FoursquareBrands.com");
				meta.put("errorDetail",
						"Please Login to FoursquareBrands.com to continue.");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		log.info(meta.toString());
		
		response.setContentType("application/json");
		response.getWriter().append(meta.toString());
	}
}
