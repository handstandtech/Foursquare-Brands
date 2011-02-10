package com.handstandtech.brandfinder.server.tasks;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.visualization.datasource.datatable.DataTable;
import com.handstandtech.brandfinder.server.DAO;
import com.handstandtech.brandfinder.shared.model.DailyFollowerCount;
import com.handstandtech.brandfinder.shared.util.ModelUtils;
import com.handstandtech.foursquare.server.FoursquareHelper;
import com.handstandtech.foursquare.server.FoursquareUtils;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;
import com.handstandtech.shared.model.rest.RESTResult;

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
		String multi = request.getParameter("ids");
		String timeStr = request.getParameter("time");
		Long time = Long.parseLong(timeStr);

		FoursquareHelper helper = new FoursquareHelper(
				OAUTH_TOKEN_HANDSTANDTECH);
		RESTResult result = helper.getMultiResult(multi);

		int count = 0;
		try {
			JSONObject obj = new JSONObject(result.getResponseBody());
			JSONArray responses = obj.getJSONObject("response").getJSONArray(
					"responses");
			for (int i = 0; i < responses.length(); i++) {
				JSONObject firstResponse = responses.getJSONObject(i);
				JSONObject jsonResponse = firstResponse
						.getJSONObject("response");
				JSONObject userJson = jsonResponse.getJSONObject("user");
				log.log(Level.INFO, "Storing Brand: " + userJson);
				log.log(Level.INFO,
						"Storing " + (i + 1) + " of " + responses.length());
				FoursquareUser brand = FoursquareUtils
						.getFoursquareUserFromJson(userJson.toString());
				storeBrand(brand, time, response);
				count++;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.log(Level.INFO, "Stored " + count + " Brands Total.");
	}

	private void storeBrand(FoursquareUser brand, Long time,
			HttpServletResponse response) {
		DAO dao = new DAO();
		if (brand != null) {
			String brandName = brand.getFirstName();
			if (brand.getLastName() != null) {
				brandName = brandName + brand.getLastName();
			}

			Long count = brand.getFriends().getCount();

			Date date = new Date(time);

			DailyFollowerCount followerCount = new DailyFollowerCount();
			followerCount.setFoursquareId(brand.getId());
			followerCount.setCount(count);
			followerCount.setDate(date);
			followerCount.setId(ModelUtils.generateId(brand.getId(), date));
			dao.updateDailyFollowerCount(followerCount);

			dao.updateFoursquareUser(brand);
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
}
