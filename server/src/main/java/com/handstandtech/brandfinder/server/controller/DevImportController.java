package com.handstandtech.brandfinder.server.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.handstandtech.brandfinder.server.dao.impl.CachingDAOImpl;
import com.handstandtech.brandfinder.server.importbackup.FoursquareUserImporter;
import com.handstandtech.server.controller.BaseController;

@SuppressWarnings("serial")
@Singleton
public class DevImportController extends BaseController {

	public static final Logger log = LoggerFactory.getLogger(DevImportController.class);

	private static CachingDAOImpl dao = new CachingDAOImpl();

	/**
	 * Handle a GET Request
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String requestUrl = request.getRequestURL().toString();
		if (!requestUrl.contains("foursquarebrands.com") && !requestUrl.contains("4sqbrands.appspot.com")) {
			String base = request.getSession().getServletContext().getRealPath("/WEB-INF/seed-data");
			log.info("Importing From: " + base);
			try {

				// FoursquareUser
				dao.updateFoursquareUsers(FoursquareUserImporter.load(base));
				
				response.sendRedirect("/");
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			} finally {
//				MemcacheScopeManager.setDirty(BaseDAOImpl.getScope(TraqmateShareUser.class));
//				MemcacheScopeManager.setDirty(BaseDAOImpl.getScope(TraqmateShareTrack.class));
//				MemcacheScopeManager.setDirty(BaseDAOImpl.getScope(TraqmateSharePhoto.class));
			}
		} else {
			log.error("DEV Import is NOT allowed in production");
			try {
				response.sendRedirect("/util");
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}
}
