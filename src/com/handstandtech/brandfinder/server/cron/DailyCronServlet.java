package com.handstandtech.brandfinder.server.cron;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
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
import com.handstandtech.foursquare.server.FoursquareHelper;
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

	private static Long ONE_SECOND = 1000L;
	private static Long ONE_MINUTE = 60 * ONE_SECOND;
	private static Long ONE_HOUR = 60 * ONE_MINUTE;
	private static Long ONE_DAY = 24 * ONE_HOUR;

	/**
	 * Handle a GET Request and serve the appropriate {@link DataTable}
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Date date = new Date();
		DAO dao = new DAO();

		List<FoursquareUser> users = new ArrayList<FoursquareUser>();
		users.addAll(dao.getBrands());
		users.addAll(dao.getCelebrities());

		Collection<String> allIds = new ArrayList<String>();
		for (FoursquareUser user : users) {
			allIds.add(user.getId());
		}

		int count = 0;
		List<String> URIs = new ArrayList<String>();
		for (String id : allIds) {
			String encodedBrandId = FoursquareHelper.encode(id);
			URIs.add(FoursquareHelper.getUserURI(encodedBrandId));
			count++;
			if (count == 5) {
				queueTask(URIs, date);
				count = 0;
				URIs.clear();
			} 
		}

		if (URIs.size() > 0) {
			queueTask(URIs, date);
		}
	}

	private void queueTask(List<String> uris, Date date)
			throws UnsupportedEncodingException {

		Queue queue = QueueFactory.getDefaultQueue();
		
		String ids = FoursquareHelper.createCSVLine(uris);

		TaskOptions followerCountTask = TaskOptions.Builder.withDefaults();
		followerCountTask.header("Brand Ids", ids);
		followerCountTask.url("/tasks/store-follower-count");
		followerCountTask.method(Method.POST);
		followerCountTask.param("ids", FoursquareHelper.encode(ids));
		followerCountTask.param("time", Long.toString(date.getTime()));
		queue.add(followerCountTask);
	}
}
