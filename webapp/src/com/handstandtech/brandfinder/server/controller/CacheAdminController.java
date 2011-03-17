package com.handstandtech.brandfinder.server.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.visualization.datasource.datatable.DataTable;
import com.handstandtech.brandfinder.server.constants.Pages;
import com.handstandtech.memcache.CF;

@SuppressWarnings("serial")
public class CacheAdminController extends BaseController {

	public static final Logger log = LoggerFactory
			.getLogger(CacheAdminController.class);

	/**
	 * Handle a GET Request and serve the appropriate {@link DataTable}
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {
		String flush = request.getParameter("flush");
		if (flush != null && flush.toLowerCase().equals("true")) {
			log.info("Clearing Cache");
			CF.clear();
			log.info("Redirecting back to normal cache page.");
			sendRedirect(response, request.getRequestURI());
		}
		request.setAttribute("stats", CF.getStats());

		forwardRequest(request, response, Pages.CACHE);
	}

	
}
