package com.handstandtech.brandfinder.server.tasks;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oauth.signpost.OAuthConsumer;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;
import com.google.visualization.datasource.datatable.DataTable;
import com.handstandtech.brandfinder.server.DAO;
import com.handstandtech.brandfinder.shared.model.Analytic;
import com.handstandtech.brandfinder.shared.model.Event;
import com.handstandtech.foursquare.server.FoursquareHelper;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;

/**
 * The server side implementation of the RPC service.
 */
public class FollowerCountTaskServlet extends HttpServlet {

	/**
	 * Default Serialization UID
	 */
	private static final long serialVersionUID = 1L;

	public static final String OAUTH_TOKEN_HANDSTANDTECH = "2QXKWLFL2VYE2GSEUUPMYC1SKPWT0TJ5ITG4N5O0D2DUS0WZ";

	protected final Logger log = Logger.getLogger(getClass().getName());

	/**
	 * Handle a GET Request and serve the appropriate {@link DataTable}
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String id = request.getParameter("id");
		String action = request.getParameter("action");
		String timeStr = request.getParameter("time");
		Long time = Long.parseLong(timeStr);

		DAO dao = new DAO();
		FoursquareHelper helper = new FoursquareHelper(
				OAUTH_TOKEN_HANDSTANDTECH);
		FoursquareUser brand = helper.getUserInfo(id);
		if (brand != null) {
			String brandName = brand.getFirstName();
			if (brand.getLastName() != null) {
				brandName = brandName + brand.getLastName();
			}

			Long followerCount = brand.getFriends().getCount();

			Analytic analytic = new Analytic();
			analytic.setEvent(Event.DAILY_FOLLOWER_SNAPSHOT);
			analytic.setLabel(id);
			analytic.setAction(action);
			analytic.setValue(followerCount);
			analytic.setDate(new Date(time));
			dao.updateAnalytic(analytic);
			
			dao.updateFoursquareUser(brand);
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
}
