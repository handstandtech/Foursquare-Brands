package com.handstandtech.brandfinder.server.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public abstract class BaseController extends HttpServlet {

	protected static final Logger log = LoggerFactory
			.getLogger(BaseController.class);

	protected void forwardRequest(HttpServletRequest request,
			HttpServletResponse response, String destination) {
		RequestDispatcher dispatcher = getServletContext()
				.getRequestDispatcher(destination);
		try {
			dispatcher.forward(request, response);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void sendRedirect(HttpServletResponse response, String url) {
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			log.error("Error trying to send redirect to " + url);
		}
	}

}
