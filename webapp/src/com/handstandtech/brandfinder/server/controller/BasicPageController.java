package com.handstandtech.brandfinder.server.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.handstandtech.brandfinder.server.constants.Pages;

@SuppressWarnings("serial")
public class BasicPageController extends BaseController {

	public static final Logger log = LoggerFactory
			.getLogger(BasicPageController.class);

	/**
	 * Handle a GET Request
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {
		String destination = null;
		String uri = request.getRequestURI();
		if (uri.startsWith("/about")) {
			destination = Pages.ABOUT;
		} else if (uri.startsWith("/terms")) {
			destination = Pages.TERMS;
		} else {
			log.error("Unknown Request: " + uri);
		}
		forwardRequest(request, response, destination);
	}
}
