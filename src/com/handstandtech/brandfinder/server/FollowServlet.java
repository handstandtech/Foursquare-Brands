package com.handstandtech.brandfinder.server;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oauth.signpost.OAuthConsumer;

import com.handstandtech.foursquare.server.FoursquareConstants;
import com.handstandtech.foursquare.server.FoursquareHelper;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;

/**
 * The server side implementation of the RPC service.
 */
public class FollowServlet extends HttpServlet {

	/**
	 * Default Serialization UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Handle a GET Request and serve the appropriate {@link DataTable}
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String id = request.getParameter("id");
		HttpSession session = request.getSession();
		OAuthConsumer sessionConsumer = (OAuthConsumer) session.getAttribute(FoursquareConstants.CONSUMER_CONSTANT);
		FoursquareHelper helper = new FoursquareHelper(sessionConsumer.getTokenSecret());
		FoursquareUser user = helper.friendRequest(id);
		DAO dao = new DAO();
		dao.updateFoursquareUser(user);
	}
}
