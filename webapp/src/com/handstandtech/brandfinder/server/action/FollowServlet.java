package com.handstandtech.brandfinder.server.action;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.handstandtech.brandfinder.server.dao.DAO;
import com.handstandtech.brandfinder.server.dao.impl.CachingDAOImpl;
import com.handstandtech.brandfinder.server.util.SessionHelper;
import com.handstandtech.brandfinder.shared.model.User;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;
import com.handstandtech.foursquare.v2.FoursquareAPIv2;
import com.handstandtech.foursquare.v2.exception.FoursquareNot200Exception;
import com.handstandtech.foursquare.v2.impl.CachingFoursquareAPIv2Impl;
import com.handstandtech.foursquare.v2.server.model.FoursquareMeta;

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

	private void doFollow(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		FoursquareMeta meta = new FoursquareMeta();
		HttpSession session = request.getSession();
		User currentUser = SessionHelper.getCurrentUser(session);
		if (currentUser != null) {
			FoursquareAPIv2 helper = new CachingFoursquareAPIv2Impl(
					currentUser.getToken());

			String id = request.getParameter("id");
			try {
				FoursquareUser user = helper.friendRequest(id);
				if (user != null) {
					log.info("Successfully Followed: " + user.getName());
					DAO dao = new CachingDAOImpl();
					dao.updateFoursquareUser(user);
				}
			} catch (FoursquareNot200Exception e) {
				meta = e.getMeta();
			}
		} else {
			log.warn("No current user.");
			meta.setErrorType("Not Logged into FoursquareBrands.com");
			meta.setErrorDetail("Please Login to FoursquareBrands.com to continue.");
		}
		
		String metaJson = new Gson().toJson(meta);
		log.info(metaJson);

		response.setContentType("application/json");
		response.getWriter().write(metaJson);
	}
}
