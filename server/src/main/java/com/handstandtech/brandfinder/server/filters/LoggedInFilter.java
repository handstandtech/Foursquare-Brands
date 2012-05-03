package com.handstandtech.brandfinder.server.filters;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.utils.SystemProperty;
import com.google.inject.Singleton;
import com.handstandtech.brandfinder.server.util.SessionConstants;
import com.handstandtech.brandfinder.server.util.SessionHelper;
import com.handstandtech.brandfinder.shared.model.User;

@Singleton
public class LoggedInFilter implements Filter {

	private static Logger log = LoggerFactory.getLogger(LoggedInFilter.class);

	private static HashSet<String> secureLocations = new HashSet<String>();

	private static HashSet<String> bookmarkableUris = new HashSet<String>();

	static {
		bookmarkableUris.add("/celebs");
		bookmarkableUris.add("/brands");

		secureLocations.add("/manage");
	}

	/** Creates new SessionFilter */
	public LoggedInFilter() {
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		log.debug("Instance created of " + getClass().getName());
	}

	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws java.io.IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		HttpSession session = request.getSession();
		String requestUri = request.getRequestURI();
		User currentUser = SessionHelper.getCurrentUser(session);
		log.info("[CHAIN] " + chain.toString());

		if (currentUser != null) {
			log.info("Current User is "
					+ currentUser.getFoursquareUser().getName() + " ["
					+ currentUser.getId() + "]");
		} else {
			log.info("No Current User");
		}

		log.info("[URI] " + requestUri);
		storeRequestUri(request, requestUri);
		isProduction(request, response, session);

		// if (isURISecure(requestUri)) {
		// if (currentUser == null) {
		// log.warn("No Current User, sending the to the homepage.");
		// response.sendRedirect("/");
		// return;
		// }
		// }

		if (isBookmarkableURI(requestUri)) {
			log.info("URI was bookmarked. = " + requestUri);
			SessionHelper.setContinueUrl(session, requestUri);
		}

		chain.doFilter(request, response);
	}

	private boolean isBookmarkableURI(String requestUri) {
		boolean isBookmarkable = false;
		if (requestUri.equals("/")) {
			isBookmarkable = true;
		} else {
			for (String curr : bookmarkableUris) {
				if (requestUri.startsWith(curr)) {
					isBookmarkable = true;
				}
			}
		}
		return isBookmarkable;
	}

	// private boolean isURISecure(String requestUri) {
	// boolean isSecure = false;
	// for (String exception : secureLocations) {
	// if (requestUri.startsWith(exception)) {
	// log.info("This URI is Secure");
	// isSecure = true;
	// }
	// }
	// return isSecure;
	// }

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