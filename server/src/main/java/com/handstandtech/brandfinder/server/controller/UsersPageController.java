package com.handstandtech.brandfinder.server.controller;

import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.visualization.datasource.datatable.DataTable;
import com.handstandtech.brandfinder.server.dao.DAO;
import com.handstandtech.brandfinder.server.dao.impl.CachingDAOImpl;
import com.handstandtech.brandfinder.server.util.Manager;
import com.handstandtech.brandfinder.server.util.SessionHelper;
import com.handstandtech.brandfinder.shared.model.User;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;

@SuppressWarnings("serial")
public class UsersPageController extends HttpServlet {

	private static DAO dao = new CachingDAOImpl();

	public static final Logger log = LoggerFactory
			.getLogger(SingleUserPageController.class);

	private static final Integer PAGE_SIZE = 50;

	/**
	 * Handle a GET Request and serve the appropriate {@link DataTable}
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		User currentUser = SessionHelper.getCurrentUser(session);
		if (currentUser != null) {
			Manager.createFriendsMap(request, currentUser);
		}
		prepareUsers(request, response);
	}

	private void prepareUsers(HttpServletRequest request,
			HttpServletResponse response) {
		Integer pageNum = getPageNumber(request);
		Integer totalCount = 0;
		Integer offset = calculateOffset(pageNum, PAGE_SIZE);

		List<FoursquareUser> users = null;
		String uriString = request.getRequestURI();
		if (uriString.startsWith("/brand")) {
			// Get The Current Brands
			totalCount = dao.getBrandsCount();
			users = dao.getBrands(offset, PAGE_SIZE);
			
		} else {
			// Get The Current Celebrities
			totalCount = dao.getCelebrities().size();
			users = dao.getCelebrities(offset, PAGE_SIZE);
		}
		request.setAttribute("users", users);
		request.setAttribute("limit", PAGE_SIZE);
		request.setAttribute("offset", offset);
		request.setAttribute("page", pageNum);
		request.setAttribute("totalCount", totalCount);
	}

	private Integer calculateOffset(Integer pageNum, Integer pageSize) {
		Integer offset = 0;
		if (pageNum != null && pageSize > 0) {
			offset = (pageNum - 1) * pageSize;
		}
		return offset;
	}

	private Integer getPageNumber(HttpServletRequest request) {
		Integer pageNum = 1;
		String pageStr = request.getParameter("page");
		if (pageStr != null) {
			try {
				pageNum = Integer.parseInt(pageStr);
			} catch (Exception e) {
				// Do nothing we have a default value
			}
		}
		return pageNum;
	}
}
