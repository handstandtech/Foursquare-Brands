package com.handstandtech.brandfinder.server.tasks;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.visualization.datasource.datatable.DataTable;
import com.handstandtech.brandfinder.server.DAO;
import com.handstandtech.brandfinder.shared.model.DailyFollowerCount;
import com.handstandtech.brandfinder.shared.util.ModelUtils;
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

			Long count = brand.getFriends().getCount();
			
			Date date = new Date(time);

			DailyFollowerCount followerCount = new DailyFollowerCount();
			followerCount.setFoursquareId(id);
			followerCount.setCount(count);
			followerCount.setDate(date);
			followerCount.setId(ModelUtils.generateId(id, date));
			dao.updateDailyFollowerCount(followerCount);
			
			dao.updateFoursquareUser(brand);
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
}
