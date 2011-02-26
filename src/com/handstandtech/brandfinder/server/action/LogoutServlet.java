package com.handstandtech.brandfinder.server.action;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.visualization.datasource.datatable.DataTable;
import com.handstandtech.brandfinder.server.util.SessionHelper;

public class LogoutServlet extends HttpServlet {

	/**
	 * Default Serialization UID
	 */
	private static final long serialVersionUID = 1L;

	protected final Logger log = Logger.getLogger(getClass().getName());

	/**
	 * Handle a GET Request and serve the appropriate {@link DataTable}
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		SessionHelper.setCurrentUser(session, null);
		SessionHelper.setContinueUrl(session, null);
		response.sendRedirect("/");
	}
}
