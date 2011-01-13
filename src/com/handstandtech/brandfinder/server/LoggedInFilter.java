package com.handstandtech.brandfinder.server;

import java.util.ArrayList;
import java.util.Arrays;

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

import com.handstandtech.foursquare.server.FoursquareConstants;

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

		log.debug("Request URL: " + requestUrl);
		log.debug("Request URI: " + requestUri);
		
		if (requestUrl.contains("/manage")) {
			response.sendRedirect("/");
			return;
		}

		//Don't allow requests to the appengine version
		/*if (requestUrl.contains("4sqbrands.appspot.com")) {
			String redirectUrl = "http://www.foursquarebrands.com" + requestUri;
			log.debug("redirecting...");
			response.sendRedirect(redirectUrl);
			return;
		}*/

		Object consumer = session.getAttribute(FoursquareConstants.CONSUMER_CONSTANT);

		boolean loggedIn=false;
		if (consumer != null) {
			loggedIn=true;
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

		log.debug(requestUri + " - is secure? " + isSecure);

		if (isSecure == true) {

			/*
			 * use the ServletContext.log method to log filter messages
			 */
			log.debug("doFilter called in: " + config.getFilterName() + " on "
					+ (new java.util.Date()));

			// log the session ID
			log.debug("session ID: " + session.getId());

			// Find out whether the logged-in session attribute is set
			if (consumer == null) {
				log.debug("Redirecting to LOGIN");
				response.sendRedirect("/");
				return;
			}
		}
		
		chain.doFilter(req, resp);
	}

	public void destroy() {
		/*
		 * called before the Filter instance is removed from service by the web
		 * container
		 */
	}
}