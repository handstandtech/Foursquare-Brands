package com.handstandtech.brandfinder.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.google.appengine.api.utils.SystemProperty;
import com.handstandtech.brandfinder.server.util.SessionHelper;
import com.handstandtech.brandfinder.shared.model.User;
import com.handstandtech.server.SessionConstants;

public class LoggedInFilter implements Filter {

	private static Logger log = Logger.getLogger(LoggedInFilter.class);

	private FilterConfig config;
	private ArrayList<String> secureLocations;

	private static HashSet<String> nonBookmarkableUris = new HashSet<String>();

	static {
		nonBookmarkableUris.add("/foursquare/callback");
		nonBookmarkableUris.add("/login");
		nonBookmarkableUris.add("/logout");
		nonBookmarkableUris.add("/follow");
		nonBookmarkableUris.add("/unfollow");
		nonBookmarkableUris.add("/action");
		nonBookmarkableUris.add("/manage");
		nonBookmarkableUris.add("/foursquare");
	}

	/** Creates new SessionFilter */
	public LoggedInFilter() {
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		log.debug("Instance created of " + getClass().getName());
		this.config = filterConfig;
		String exceptionsString = config.getInitParameter("secure-locations");
		String exceptionsArray[] = exceptionsString.split(",");
		secureLocations = new ArrayList<String>(Arrays.asList(exceptionsArray));
	}

	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws java.io.IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		HttpSession session = request.getSession();
		String requestUri = request.getRequestURI();
		User currentUser = SessionHelper.getCurrentUser(session);

		if (currentUser != null) {
			log.info("Current User is "
					+ currentUser.getFoursquareUser().getName() + " ["
					+ currentUser.getId() + "]");
		} else {
			log.info("No Current User");
		}

		if (requestUri.startsWith("/assets/js")) {
			log.info("[JavaScript ASSET] " + requestUri);
		} else if (requestUri.startsWith("/assets/css")) {
			log.info("[CSS ASSET] " + requestUri);
		} else if (requestUri.startsWith("/assets/images")) {
			log.info("[Image ASSET] " + requestUri);
		} else if (requestUri.startsWith("/appstats")) {
			log.info("[APPSTATS] " + requestUri);
		} else {
			log.info("[URI] " + requestUri);
			storeRequestUri(request, requestUri);
			isProduction(request, response, session);

			String continueUrl = SessionHelper.getContinueUrl(session);

			if (isURISecure(requestUri)) {
				log.info("This is a secure URI");
				if (currentUser == null) {
					log.info("No Current User, sending the to the homepage.");
					response.sendRedirect("/");
				}
			}

			if (!nonBookmarkableUris.contains(requestUri)) {
				log.info("URI was bookmarked.");
				SessionHelper.setContinueUrl(session, requestUri);
			} else {
				log.info("URI cannot be bookmarked.");
			}
		}

		chain.doFilter(req, resp);
	}

	private boolean isURISecure(String requestUri) {
		boolean isSecure = false;
		for (String exception : secureLocations) {
			if (requestUri.startsWith(exception)) {
				log.info("This URI is Secure");
				isSecure = true;
			}
		}
		return isSecure;
	}

	private void storeRequestUri(HttpServletRequest request, String requestUri) {
		// Set requestURI in request
		request.setAttribute(SessionConstants.REQUEST_URI.toString(),
				requestUri);
	}

	private void isProduction(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
			request.setAttribute("production", true);
		}
	}

	public void destroy() {
		/*
		 * called before the Filter instance is removed from service by the web
		 * container
		 */
	}
}