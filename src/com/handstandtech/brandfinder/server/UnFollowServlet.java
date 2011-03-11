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

import com.google.visualization.datasource.datatable.DataTable;
import com.handstandtech.brandfinder.server.util.SessionHelper;
import com.handstandtech.brandfinder.shared.model.User;
import com.handstandtech.foursquare.server.FoursquareHelper;
import com.handstandtech.shared.model.rest.RESTResult;

/**
 * The server side implementation of the RPC service.
 */
public class UnFollowServlet extends HttpServlet {

	/**
	 * Default Serialization UID
	 */
	private static final long serialVersionUID = 1L;
	
	protected final Logger log = LoggerFactory.getLogger(getClass().getName());

	/**
	 * Handle a GET Request and serve the appropriate {@link DataTable}
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		doUnFollow(request, response);
	}


//	/**
//	 * Handle a GET Request and serve the appropriate {@link DataTable}
//	 */
//	@Override
//	protected void doGet(HttpServletRequest request,
//			HttpServletResponse response) throws IOException {
//		doUnFollow(request, response);
//	}
	
	private void doUnFollow(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String id = request.getParameter("id");
		HttpSession session = request.getSession();
		User currentUser = SessionHelper.getCurrentUser(session);
		FoursquareHelper helper = new FoursquareHelper(currentUser.getToken());
		
		RESTResult result = helper.unFriendRequest(id);
		try {
			JSONObject jsonResultObj = new JSONObject(result.getResponseBody());
			JSONObject meta = jsonResultObj.getJSONObject("meta");
			int responseCode = meta.getInt("code");
			if (responseCode == 200) {
				log.info("follow was successful");
			} else {
				log.info("follow was NOT successful");

				// Possible Errors
				// {"meta":{"code":403,"errorType":"rate_limit_exceeded","errorDetail":"Quota exceeded"},"response":{}}
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
