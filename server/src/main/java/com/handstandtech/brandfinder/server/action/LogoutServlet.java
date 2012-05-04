package com.handstandtech.brandfinder.server.action;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.handstandtech.brandfinder.server.foursquare.CachingFoursquareAPIv2Impl;
import com.handstandtech.brandfinder.server.util.SessionHelper;
import com.handstandtech.brandfinder.shared.model.User;
import com.handstandtech.foursquare.v2.FoursquareAPIv2;

@Singleton
public class LogoutServlet extends HttpServlet {

	/**
	 * Default Serialization UID
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory
			.getLogger(LogoutServlet.class);

	/**
	 * Handle a GET Request and serve the appropriate {@link DataTable}
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		HttpSession session = request.getSession();
		User currentUser = SessionHelper.getCurrentUser(session);
		if (currentUser != null) {
			log.info("Logging Out: " + currentUser.getId());
			FoursquareAPIv2 foursquare = new CachingFoursquareAPIv2Impl(
					currentUser.getToken());
			foursquare.logout();
		}
		String continueUrl = SessionHelper.getContinueUrl(session);
		SessionHelper.setCurrentUser(session, null);
		SessionHelper.setContinueUrl(session, null);
		response.sendRedirect(continueUrl);
	}
}
