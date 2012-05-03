package com.handstandtech.brandfinder.server.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.inject.Singleton;
import com.handstandtech.brandfinder.server.dao.DAO;
import com.handstandtech.brandfinder.server.dao.impl.CachingDAOImpl;
import com.handstandtech.brandfinder.server.util.ContentTypes;
import com.handstandtech.brandfinder.server.util.SessionHelper;
import com.handstandtech.brandfinder.shared.model.User;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;
import com.handstandtech.foursquare.v2.FoursquareAPIv2;
import com.handstandtech.foursquare.v2.exception.FoursquareNot200Exception;
import com.handstandtech.foursquare.v2.impl.CachingFoursquareAPIv2Impl;
import com.handstandtech.foursquare.v2.server.model.FoursquareMeta;
import com.handstandtech.memcache.CF;

/**
 * The server side implementation of the RPC service.
 */
@Singleton
public class FollowServlet extends HttpServlet {

	/**
	 * Default Serialization UID
	 */
	private static final long serialVersionUID = 1L;

	private static final String FOURSQUARE_BRANDS_TO_UPDATE = "FoursquareBrandsToUpdate";
	// private static final String FOURSQUARE_BRANDS_TO_UPDATE_TIME =
	// "FoursquareBrandsToUpdateTime";

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

					// Map<String, FoursquareUser> usersToUpdate =
					// getUsersToUpdate();
					//
					// usersToUpdate.put(user.getId(), user);
					//
					// Date timeToRunUpdate = (Date) CF
					// .get(FOURSQUARE_BRANDS_TO_UPDATE_TIME);
					// Date now = new Date();
					// if (timeToRunUpdate == null) {
					// Long time = now.getTime()
					// + TimesInMilliseconds.ONE_SECOND * 30;
					// timeToRunUpdate = new Date(time);
					// CF.put(FOURSQUARE_BRANDS_TO_UPDATE_TIME,
					// timeToRunUpdate);
					// }
					//
					// log.info("Seeing if it's time, comparing...\n" + now
					// + ">=" + timeToRunUpdate);
					// if (now.getTime() >= timeToRunUpdate.getTime()) {
					// CF.put(FOURSQUARE_BRANDS_TO_UPDATE, null);
					// CF.put(FOURSQUARE_BRANDS_TO_UPDATE_TIME, null);
					log.info("[Running Update on Brands!]");
					DAO dao = new CachingDAOImpl();
					// dao.updateFoursquareUsers(usersToUpdate.values());
					dao.updateFoursquareUser(user);
					// } else {
					// log.info("[Not time to update the list!]");
					// }

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

		response.setContentType(ContentTypes.APPLICATION_JSON_UTF8);
		response.getWriter().write(metaJson);
	}

	private Map<String, FoursquareUser> getUsersToUpdate() {
		Map<String, FoursquareUser> usersToUpdate = (Map<String, FoursquareUser>) CF
				.get(FOURSQUARE_BRANDS_TO_UPDATE);
		if (usersToUpdate == null) {
			usersToUpdate = new HashMap<String, FoursquareUser>();
		}
		return usersToUpdate;
	}
}
