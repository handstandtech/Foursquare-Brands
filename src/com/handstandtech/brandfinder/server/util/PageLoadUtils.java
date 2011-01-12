package com.handstandtech.brandfinder.server.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import oauth.signpost.OAuthConsumer;

import com.handstandtech.brandfinder.server.DAO;
import com.handstandtech.brandfinder.server.ParseCSV;
import com.handstandtech.brandfinder.shared.model.Analytic;
import com.handstandtech.brandfinder.shared.model.Event;
import com.handstandtech.foursquare.server.FoursquareConstants;
import com.handstandtech.foursquare.server.FoursquareHelper;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;

public class PageLoadUtils {

	public static Map<String, FoursquareUser> getFollowing(
			List<FoursquareUser> friends,
			Map<String, FoursquareUser> idToBrandMap) {
		Map<String, FoursquareUser> followingList = new HashMap<String, FoursquareUser>();

		for (FoursquareUser friend : friends) {
			if (friend.getRelationship().equals("followingThem")) {
				FoursquareUser currValue = idToBrandMap.get(friend.getId());
				followingList.put(friend.getId(), currValue);
			}
		}
		return followingList;
	}

	public static Map<String, FoursquareUser> createIdToBrandMap(
			List<FoursquareUser> brands) {
		Map<String, FoursquareUser> idToBrandMap = new HashMap<String, FoursquareUser>();
		for (FoursquareUser b : brands) {
			idToBrandMap.put(b.getId(), b);
		}
		return idToBrandMap;
	}

	/**
	 * Read in Brands from CSV File and put them in the Database
	 * 
	 * @param session
	 */
	public static void initializeBrandListFromCSVFile(HttpSession session) {
		DAO dao = new DAO();
		ParseCSV parser = new ParseCSV();
		String path = session.getServletContext().getRealPath(
				"WEB-INF/backup/Brands.csv");
		List<FoursquareUser> initList = null;
		try {
			initList = parser.parseBrandsCSV(path);
			for (FoursquareUser initBrand : initList) {
				dao.updateFoursquareUser(initBrand);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
