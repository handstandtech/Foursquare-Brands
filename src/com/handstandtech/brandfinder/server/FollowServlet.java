package com.handstandtech.brandfinder.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oauth.signpost.OAuthConsumer;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.visualization.datasource.datatable.DataTable;
import com.handstandtech.foursquare.server.FoursquareConstants;
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

	protected final Logger log = Logger.getLogger(getClass().getName());

	/**
	 * Handle a GET Request and serve the appropriate {@link DataTable}
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String id = request.getParameter("id");
		HttpSession session = request.getSession();
		OAuthConsumer sessionConsumer = (OAuthConsumer) session
				.getAttribute(FoursquareConstants.CONSUMER_CONSTANT);
		FoursquareHelper helper = new FoursquareHelper(
				sessionConsumer.getTokenSecret());

		RESTResult result = helper.friendRequest(id);
		try {
			JSONObject jsonResultObj = new JSONObject(result.getResponseBody());
			JSONObject meta = jsonResultObj.getJSONObject("meta");
			int responseCode = meta.getInt("code");
			if (responseCode == 200) {
				log.info("follow was successful");
				FoursquareUser user = FoursquareUtils
						.getFoursquareUserFromResult(result);
				DAO dao = new DAO();
				dao.updateFoursquareUser(user);
			} else {
				log.info("follow was NOT successful");
				
				//Possible Errors
				//{"meta":{"code":403,"errorType":"rate_limit_exceeded","errorDetail":"Quota exceeded"},"response":{}}
				//
				
				response.setContentType("application/json");
				response.getWriter().append(meta.toString());
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
