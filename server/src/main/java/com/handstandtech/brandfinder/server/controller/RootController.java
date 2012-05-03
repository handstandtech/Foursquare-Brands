package com.handstandtech.brandfinder.server.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.visualization.datasource.datatable.DataTable;
import com.handstandtech.brandfinder.server.constants.Pages;
import com.handstandtech.brandfinder.server.dao.DAO;
import com.handstandtech.brandfinder.server.dao.impl.CachingDAOImpl;
import com.handstandtech.brandfinder.server.util.Manager;
import com.handstandtech.brandfinder.server.util.SessionHelper;
import com.handstandtech.brandfinder.shared.model.BrandDiscovered;
import com.handstandtech.brandfinder.shared.model.User;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;

@SuppressWarnings("serial")
@Singleton
public class RootController extends HttpServlet {

	public static final Logger log = LoggerFactory
			.getLogger(RootController.class);

	private static DAO dao = new CachingDAOImpl();

	private static Integer NUM_NEW = 10;

	/**
	 * Handle a GET Request and serve the appropriate {@link DataTable}
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String destination = null;
		HttpSession session = request.getSession();
		User currentUser = SessionHelper.getCurrentUser(session);
		if (currentUser != null) {
			destination = Pages.NEWBRANDS;
			Manager.createFriendsMap(request, currentUser);
		} else {
			destination = Pages.HOME;
		}

		List<BrandDiscovered> newCelebs = dao.getNewestCelebrities(NUM_NEW);
		List<BrandDiscovered> newBrands = dao.getNewestBrands(NUM_NEW);

		request.setAttribute("newCelebs", newCelebs);
		request.setAttribute("newBrands", newBrands);
		request.setAttribute("numNew", NUM_NEW);

		List<BrandDiscovered> newlyDiscovered = new ArrayList<BrandDiscovered>();
		newlyDiscovered.addAll(newCelebs);
		newlyDiscovered.addAll(newBrands);

		request.setAttribute("foursquareUsersMap",
				prepareFoursquareMapForBrandsDiscovered(newlyDiscovered));

		forwardRequest(request, response, destination);
	}

	private Object prepareFoursquareMapForBrandsDiscovered(
			List<BrandDiscovered> newlyDiscovered) {
		Collection<String> ids = new HashSet<String>();
		for (BrandDiscovered bd : newlyDiscovered) {
			ids.add(bd.getBrandId());
		}

		Map<String, FoursquareUser> foursquareUsersMap = new HashMap<String, FoursquareUser>();
		List<FoursquareUser> usersForIds = dao.getFoursquareUsersForIds(ids);
		if (usersForIds != null && !usersForIds.isEmpty()) {
			for (FoursquareUser u : usersForIds) {
				foursquareUsersMap.put(u.getId(), u);
			}
		}
		return foursquareUsersMap;
	}

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
}
