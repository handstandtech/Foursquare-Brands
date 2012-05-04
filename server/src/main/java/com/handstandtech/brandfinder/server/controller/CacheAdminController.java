package com.handstandtech.brandfinder.server.controller;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.handstandtech.memcache.CF;

@SuppressWarnings("serial")
public class CacheAdminController extends HttpServlet {

	public static final Logger log = LoggerFactory
			.getLogger(CacheAdminController.class);

	/**
	 * Handle a GET Request and serve the appropriate {@link DataTable}
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String flush = request.getParameter("flush");
		if (flush != null && flush.toLowerCase().equals("true")) {
			log.info("Clearing Cache");
			CF.clear();
			log.info("Redirecting back to normal cache page.");
			String uri = request.getRequestURI();
			try {
				response.sendRedirect(uri);
			} catch (IOException e) {
				log.error("Error trying to send redirect to " + uri);
			}
		}
		request.setAttribute("stats", CF.getStats());
	}

}
