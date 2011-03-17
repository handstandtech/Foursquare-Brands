package com.handstandtech.brandfinder.server.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.visualization.datasource.datatable.DataTable;
import com.handstandtech.brandfinder.server.constants.Pages;
import com.handstandtech.brandfinder.server.dao.DAO;
import com.handstandtech.brandfinder.server.dao.impl.CachingDAOImpl;
import com.handstandtech.brandfinder.server.util.Manager;
import com.handstandtech.brandfinder.server.util.SessionHelper;
import com.handstandtech.brandfinder.shared.model.DailyFollowEventCount;
import com.handstandtech.brandfinder.shared.model.DailyFollowerCount;
import com.handstandtech.brandfinder.shared.model.User;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;

@SuppressWarnings("serial")
public class BrandOrCelebPageController extends BaseController {

	private static DAO dao = new CachingDAOImpl();

	private static final Integer PAGE_SIZE = 50;

	/**
	 * Handle a GET Request and serve the appropriate {@link DataTable}
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {
		String destination = null;

		HttpSession session = request.getSession();
		User currentUser = SessionHelper.getCurrentUser(session);
		if (currentUser != null) {
			Manager.createFriendsMap(request, currentUser);
		} else {
			log.error("No current user, shouldn't be here.");
		}

		// See if this is a "users" or "user" page, and prepare information
		String uriString = request.getRequestURI();
		if (uriString.split("/").length <= 2) {
			destination = Pages.USERS;
			prepareUsers(request, response);
		} else {
			destination = Pages.USER;
			prepareUser(request, response);
		}

		forwardRequest(request, response, destination);
	}

	private void prepareUser(HttpServletRequest request,
			HttpServletResponse response) {

		String userId = getUserId(request.getRequestURI());
		if (userId != null) {
			FoursquareUser brand = dao.findFoursquareUser(userId);
			request.setAttribute("user", brand);
		} else {
			log.error("This user doesn't exist! " + userId);
		}

		List<DailyFollowEventCount> counts = dao.getDailyFollowEventsForBrand(
				userId, null, null);
		StringBuffer chart1Rows = new StringBuffer();
		for (DailyFollowEventCount count : counts) {
			Long followCount = count.getTotalFollowCount();
			Long unfollowCount = count.getTotalUnFollowCount();
			chart1Rows.append("[new Date(" + formattedDate(count.getDate())
					+ "), " + followCount + ", null, null, " + unfollowCount
					+ ", null, null],");
		}

		List<DailyFollowerCount> followerCounts = dao
				.getDailyFollowCountsForBrand(userId, null, null);
		StringBuffer chart2Rows = new StringBuffer();
		Long minFollowerCount = Long.MAX_VALUE;
		for (DailyFollowerCount dfc : followerCounts) {
			Long count = dfc.getCount();
			if (count != 0) {
				if (count < minFollowerCount) {
					minFollowerCount = count;
				}
			}
			chart2Rows.append("[new Date(" + formattedDate(dfc.getDate())
					+ "), " + count + ", null, null],");
		}

		request.setAttribute("chart1Rows", chart1Rows.toString());
		request.setAttribute("chart2Rows", chart2Rows.toString());
		request.setAttribute("minFollowerCount", minFollowerCount);
		request.setAttribute("counts", counts);
		request.setAttribute("followerCounts", followerCounts);
	}

	private String formattedDate(Date date) {
		SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
		SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
		SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
		String year = yearFormat.format(date);
		String month = monthFormat.format(date);
		month = new Integer(Integer.parseInt(month) - 1).toString();
		String day = dayFormat.format(date);
		return year + ", " + month + ", " + day;
	}

	private String getUserId(String uri) {

		String[] tokens = uri.split("/");
		String userId = null;
		if (tokens.length == 3) {
			userId = tokens[2];
		}
		return userId;
	}

	private void prepareUsers(HttpServletRequest request,
			HttpServletResponse response) {

		Integer pageNum = getPageNumber(request);
		Integer totalCount = 0;
		Integer offset = calculateOffset(pageNum, PAGE_SIZE);

		List<FoursquareUser> users = null;
		String title = null;
		String userType = null;
		String uriString = request.getRequestURI();
		if (uriString.startsWith("/brand")) {
			// Get The Current Brands
			totalCount = dao.getBrandIds().size();
			users = dao.getBrands(offset, PAGE_SIZE);
			userType = "brands";
			title = "Brands";
		} else {
			// Get The Current Celebrities
			totalCount = dao.getCelebIds().size();
			users = dao.getCelebrities(offset, PAGE_SIZE);
			userType = "celebs";
			title = "Celebrities";
		}

		request.setAttribute("title", title);
		request.setAttribute("userType", userType);
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
