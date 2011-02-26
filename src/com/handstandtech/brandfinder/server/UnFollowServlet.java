package com.handstandtech.brandfinder.server;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.visualization.datasource.datatable.DataTable;
import com.handstandtech.brandfinder.server.util.SessionHelper;
import com.handstandtech.brandfinder.shared.model.User;
import com.handstandtech.foursquare.server.FoursquareHelper;

/**
 * The server side implementation of the RPC service.
 */
public class UnFollowServlet extends HttpServlet {

	/**
	 * Default Serialization UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Handle a GET Request and serve the appropriate {@link DataTable}
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String id = request.getParameter("id");
		HttpSession session = request.getSession();
		User currentUser = SessionHelper.getCurrentUser(session);
		FoursquareHelper helper = new FoursquareHelper(currentUser.getToken());
		helper.unFriendRequest(id);
	}
}
