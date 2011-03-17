package com.handstandtech.brandfinder.server.cron;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.visualization.datasource.datatable.DataTable;
import com.handstandtech.brandfinder.server.dao.DAO;
import com.handstandtech.brandfinder.server.dao.impl.CachingDAOImpl;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;
import com.handstandtech.foursquare.v2.impl.FoursquareAPIv2Impl;
import com.handstandtech.server.rest.RESTUtil;

/**
 * Daily Cron Servlet
 */
public class DailyCronServlet extends HttpServlet {

	/**
	 * Default Serialization UID
	 */
	private static final long serialVersionUID = 1L;

	protected final Logger log = LoggerFactory.getLogger(DailyCronServlet.class);

	/**
	 * Handle a GET Request and serve the appropriate {@link DataTable}
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Date date = new Date();
		DAO dao = new CachingDAOImpl();

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
			String encodedBrandId = RESTUtil.encode(id);
			URIs.add(FoursquareAPIv2Impl.getUserURI(encodedBrandId));
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
		
		String ids = FoursquareAPIv2Impl.createCSVLine(uris);

		TaskOptions followerCountTask = TaskOptions.Builder.withDefaults();
		followerCountTask.header("Brand Ids", ids);
		followerCountTask.url("/tasks/store-follower-count");
		followerCountTask.method(Method.POST);
		followerCountTask.param("ids", RESTUtil.encode(ids));
		followerCountTask.param("time", Long.toString(date.getTime()));
		queue.add(followerCountTask);
	}
}
