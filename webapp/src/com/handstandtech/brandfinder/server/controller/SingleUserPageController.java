package com.handstandtech.brandfinder.server.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class SingleUserPageController extends HttpServlet {

	public static final Logger log = LoggerFactory
			.getLogger(SingleUserPageController.class);

	private static DAO dao = new CachingDAOImpl();

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
		prepareUser(request, response);
	}

	private void prepareUser(HttpServletRequest request,
			HttpServletResponse response) {
		String userId = (String) request.getAttribute("userId");
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
}
