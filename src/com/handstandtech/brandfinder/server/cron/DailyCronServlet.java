package com.handstandtech.brandfinder.server.cron;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;
import com.google.visualization.datasource.datatable.DataTable;
import com.handstandtech.brandfinder.server.DAO;
import com.handstandtech.brandfinder.shared.model.Analytic;
import com.handstandtech.brandfinder.shared.model.Event;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;

/**
 * The server side implementation of the RPC service.
 */
public class DailyCronServlet extends HttpServlet {

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
		Date date = new Date();

		// See if there are any new brands in the last day
		// FoursquareHelper handstandTechHelper = new
		// FoursquareHelper(FollowerCountTaskServlet.OAUTH_TOKEN_HANDSTANDTECH);
		// handstandTechHelper.friendRequest(brand.getId());

		String action = "follower-count-" + date;

		DAO dao = new DAO();
		List<FoursquareUser> brands = dao.getBrands();
		for (FoursquareUser brand : brands) {
			Queue queue = QueueFactory.getDefaultQueue();

			TaskOptions taskOptions = TaskOptions.Builder.withDefaults();
			taskOptions.url("/tasks/store-follower-count");
			taskOptions.method(Method.POST);
			taskOptions.param("id", brand.getId());
			taskOptions.param("action", action);
			taskOptions.param("time", Long.toString(date.getTime()));
			queue.add(taskOptions);
		}
	}
}
