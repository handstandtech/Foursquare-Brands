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

import com.handstandtech.brandfinder.server.util.SessionHelper;
import com.handstandtech.brandfinder.shared.model.User;

public class LoggedInFilter implements Filter {

	private static Logger log = Logger.getLogger(LoggedInFilter.class);

	private FilterConfig config;
	private ArrayList<String> secureLocations;

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
		// Start Logged In Filter
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String requestUri = request.getRequestURI();
		String requestUrl = request.getRequestURL().toString();
		HttpSession session = request.getSession();

		HashSet<String> uris = new HashSet<String>();
		uris.add("/foursquare/callback");
		uris.add("/login");
		uris.add("/logout");
		if (!uris.contains(requestUri)) {
			SessionHelper.setContinueUrl(session, requestUri);
		}

		log.debug("Request URL: " + requestUrl);
		log.debug("Request URI: " + requestUri);

		User currentUser = SessionHelper.getCurrentUser(session);

		boolean loggedIn = false;
		if (currentUser != null) {
			loggedIn = true;
		}
		request.setAttribute("loggedIn", loggedIn);

		boolean isSecure = false;
		for (String exception : secureLocations) {
			if (requestUri.startsWith(exception)) {
				log.debug("The Secure URI -- " + requestUri + " -- matches -- "
						+ exception);
				isSecure = true;
			}
		}

		if (isSecure && !loggedIn) {
			response.sendRedirect("/login");
		}

		log.debug(requestUri + " - is secure? " + isSecure);

		chain.doFilter(req, resp);
	}

	public void destroy() {
		/*
		 * called before the Filter instance is removed from service by the web
		 * container
		 */
	}
}