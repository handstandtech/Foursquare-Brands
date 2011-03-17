package com.handstandtech.brandfinder.server.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.visualization.datasource.datatable.DataTable;
import com.handstandtech.brandfinder.server.constants.Pages;
import com.handstandtech.brandfinder.server.dao.DAO;
import com.handstandtech.brandfinder.server.dao.impl.CachingDAOImpl;
import com.handstandtech.brandfinder.shared.model.User;

@SuppressWarnings("serial")
public class AdminController extends BaseController {
	private static DAO dao = new CachingDAOImpl();

	/**
	 * Handle a GET Request and serve the appropriate {@link DataTable}
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {
		String limitString = request.getParameter("limit");

		Integer limit = null;
		if (limitString != null) {
			if (limitString.equals("0")) {
				limit = null;
			} else {
				try {
					limit = Integer.parseInt(limitString);
				} catch (Exception e) {
					log.warn(e.getMessage(), e);
				}
			}
		} else {
			limit = 50;
		}

		List<User> users = dao.getRecentlyActiveUsers(limit);
		request.setAttribute("users", users);

		forwardRequest(request, response, Pages.ADMIN);
	}
}
