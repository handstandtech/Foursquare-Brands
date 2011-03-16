package com.handstandtech.brandfinder.server;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.visualization.datasource.datatable.DataTable;
import com.handstandtech.brandfinder.server.util.SessionHelper;
import com.handstandtech.brandfinder.shared.model.User;
import com.handstandtech.foursquare.v2.FoursquareAPIv2;
import com.handstandtech.foursquare.v2.exception.FoursquareNot200Exception;
import com.handstandtech.foursquare.v2.impl.CachingFoursquareAPIv2Impl;
import com.handstandtech.foursquare.v2.server.model.FoursquareMeta;

/**
 * The server side implementation of the RPC service.
 */
public class UnFollowServlet extends HttpServlet {

	/**
	 * Default Serialization UID
	 */
	private static final long serialVersionUID = 1L;

	protected final Logger log = LoggerFactory.getLogger(getClass().getName());

	/**
	 * Handle a GET Request and serve the appropriate {@link DataTable}
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		doUnFollow(request, response);
	}

	private void doUnFollow(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		FoursquareMeta meta = new FoursquareMeta();
		String id = request.getParameter("id");
		HttpSession session = request.getSession();
		User currentUser = SessionHelper.getCurrentUser(session);
		FoursquareAPIv2 helper = new CachingFoursquareAPIv2Impl(
				currentUser.getToken());

		try {
			helper.unFriendRequest(id);
		} catch (FoursquareNot200Exception e) {
			meta = e.getMeta();
		}


		String metaJson = new Gson().toJson(meta);
		log.info("Meta: "+ metaJson);
		
		response.setContentType("application/json");
		response.getWriter().write(metaJson);
	}
}
