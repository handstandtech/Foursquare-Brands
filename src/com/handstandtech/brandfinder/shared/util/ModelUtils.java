package com.handstandtech.brandfinder.shared.util;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;
import java.text.SimpleDateFormat;

public class ModelUtils {

	public static Map<String, FoursquareUser> createIdToUserMap(
			List<FoursquareUser> brands) {
		Map<String, FoursquareUser> map = new HashMap<String, FoursquareUser>();
		for (FoursquareUser user : brands) {
			map.put(user.getId(), user);
		}
		return map;
	}

	public static SimpleDateFormat getDateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd");
	}

	public static String generateId(String id, Date date) {
		return getDateFormat().format(date) + " | " + id;
	}

}
