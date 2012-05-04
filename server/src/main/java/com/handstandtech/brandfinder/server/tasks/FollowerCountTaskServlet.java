package com.handstandtech.brandfinder.server.tasks;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.handstandtech.brandfinder.server.dao.DAO;
import com.handstandtech.brandfinder.server.dao.impl.CachingDAOImpl;
import com.handstandtech.brandfinder.server.foursquare.CachingFoursquareAPIv2Impl;
import com.handstandtech.brandfinder.shared.model.DailyFollowerCount;
import com.handstandtech.brandfinder.shared.util.ModelUtils;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;
import com.handstandtech.foursquare.v2.FoursquareAPIv2;
import com.handstandtech.foursquare.v2.util.FoursquareUtils;
import com.handstandtech.restclient.shared.model.RESTResult;

/**
 * The server side implementation of the RPC service.
 */
@Singleton
public class FollowerCountTaskServlet extends HttpServlet {

	/**
	 * Default Serialization UID
	 */
	private static final long serialVersionUID = 1L;

	public static final String OAUTH_TOKEN_FOURSQUAREBRANDS = "2QXKWLFL2VYE2GSEUUPMYC1SKPWT0TJ5ITG4N5O0D2DUS0WZ";

	protected final Logger log = LoggerFactory.getLogger(getClass().getName());

	/**
	 * Handle a GET Request and serve the appropriate {@link DataTable}
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String multi = request.getParameter("ids");
		String timeStr = request.getParameter("time");
		Long time = Long.parseLong(timeStr);

		FoursquareAPIv2 helper = new CachingFoursquareAPIv2Impl(
				OAUTH_TOKEN_FOURSQUAREBRANDS);
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
				log.info("Storing Brand: " + userJson);
				log.info("Storing " + (i + 1) + " of " + responses.length());
				FoursquareUser user = FoursquareUtils
						.getFoursquareUserFromJson(userJson.toString());
				storeBrand(user, time, response);
				count++;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.info("Stored " + count + " Brands Total.");
	}

	private void storeBrand(FoursquareUser brand, Long time,
			HttpServletResponse response) {
		DAO dao = new CachingDAOImpl();
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
