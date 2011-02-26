package com.handstandtech.brandfinder.server.util;

import javax.servlet.http.HttpSession;

import com.handstandtech.brandfinder.shared.model.User;
import com.handstandtech.server.SessionConstants;

public class SessionHelper {

	public static void setCurrentUser(HttpSession session, User user) {
		session.setAttribute(SessionConstants.CURRENT_USER, user);
	}
	
	public static User getCurrentUser(HttpSession session) {
		return (User)session.getAttribute(SessionConstants.CURRENT_USER);
	}

	public static void setContinueUrl(HttpSession session, String url) {
		session.setAttribute(SessionConstants.CONTINUE_URL, url);
	}
	
	public static String getContinueUrl(HttpSession session) {
		return (String)session.getAttribute(SessionConstants.CONTINUE_URL);
	}


}
