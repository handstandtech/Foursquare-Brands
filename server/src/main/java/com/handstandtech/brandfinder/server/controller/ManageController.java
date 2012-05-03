package com.handstandtech.brandfinder.server.controller;

import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.visualization.datasource.datatable.DataTable;
import com.handstandtech.brandfinder.server.util.Manager;
import com.handstandtech.brandfinder.server.util.SessionHelper;
import com.handstandtech.brandfinder.shared.model.User;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;

@SuppressWarnings("serial")
public class ManageController extends HttpServlet {

	public static final Logger log = LoggerFactory
			.getLogger(ManageController.class);

	/**
	 * Handle a GET Request and serve the appropriate {@link DataTable}
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		User currentUser = SessionHelper.getCurrentUser(session);
		Map<String, FoursquareUser> friendsMap = null;
		if (currentUser != null) {
			friendsMap = Manager.createFriendsMap(request, currentUser);
		}

		log.info("Get The Current User's Friends and check for new ones");
		Collection<FoursquareUser> friends = friendsMap.values();
		log.info(currentUser.getFoursquareUser().getName() + " has "
				+ friends.size() + " Friends Total.");

		Map<String, FoursquareUser> userMap = null;
		String userType = (String) request.getAttribute("userType");

		if ("brands".equals(userType)) {
			// Get The Current Brands
			userMap = Manager.getBrandMap(session);
		} else if ("celebs".equals(userType)) {
			userMap = Manager.getCelebMap(session);
		} else {
			log.error("Unknown user type: " + userType);
		}

		request.setAttribute("userMap", userMap);

		log.info("Prepare followed and notFollowed lists for request.");
		Manager.prepareFollowedAndNotFollowedLists(currentUser, friends,
				userMap, request);
	}
}
