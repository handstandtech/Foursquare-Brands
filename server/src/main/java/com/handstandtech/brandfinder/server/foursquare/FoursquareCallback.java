package com.handstandtech.brandfinder.server.foursquare;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.handstandtech.brandfinder.server.dao.DAO;
import com.handstandtech.brandfinder.server.dao.impl.CachingDAOImpl;
import com.handstandtech.brandfinder.server.util.SessionHelper;
import com.handstandtech.brandfinder.shared.model.User;
import com.handstandtech.foursquare.oauth.FoursquareOAuth2Callback;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;
import com.handstandtech.foursquare.v2.FoursquareAPIv2;
import com.handstandtech.foursquare.v2.exception.FoursquareNot200Exception;
import com.handstandtech.foursquare.v2.impl.CachingFoursquareAPIv2Impl;

/**
 * An OAuth callback handler.
 * 
 * @author Sam Edwards
 */
@SuppressWarnings("serial")
@Singleton
public class FoursquareCallback extends FoursquareOAuth2Callback {

	private static final String SELF = "self";

	protected final Logger log = LoggerFactory.getLogger(getClass().getName());

	private static DAO dao = new CachingDAOImpl();
	
	@Override
	protected void handleAccessTokenFound(String accessToken) {
		FoursquareAPIv2 helper = new CachingFoursquareAPIv2Impl(accessToken);
		FoursquareUser foursquareUser = null;
		try {
			foursquareUser = helper.getUserInfo(SELF);
			User user = new User();
			user.setToken(accessToken);
			user.setId(foursquareUser.getId());
			user.setFoursquareUser(foursquareUser);
			dao.updateUser(user);

			SessionHelper.setCurrentUser(request.getSession(), user);
		} catch (FoursquareNot200Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	protected void redirectToFoursquareApp() {
		try {
			String continueUrl = SessionHelper.getContinueUrl(request.getSession());
			if (continueUrl == null) {
				continueUrl = "/";
			}
			response.sendRedirect(continueUrl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
