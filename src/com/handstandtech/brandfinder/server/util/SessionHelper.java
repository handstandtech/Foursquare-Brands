package com.handstandtech.brandfinder.server.util;

import javax.servlet.http.HttpSession;

import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;
import com.handstandtech.server.SessionConstants;

public class SessionHelper {

	public static void setCurrentUser(HttpSession session, FoursquareUser user) {
		session.setAttribute(SessionConstants.CURRENT_USER, user);
	}
	
	public static FoursquareUser getCurrentUser(HttpSession session) {
		return (FoursquareUser)session.getAttribute(SessionConstants.CURRENT_USER);
	}


}
