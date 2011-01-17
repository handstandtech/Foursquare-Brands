package com.handstandtech.brandfinder.server.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.handstandtech.brandfinder.server.DAO;
import com.handstandtech.brandfinder.server.ParseCSV;
import com.handstandtech.brandfinder.shared.model.BrandDiscovered;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;

public class PageLoadUtils {

	public static Map<String, FoursquareUser> getFollowing(
			List<FoursquareUser> friends,
			Map<String, FoursquareUser> idToBrandMap) {
		Map<String, FoursquareUser> followingList = new HashMap<String, FoursquareUser>();

		for (FoursquareUser friend : friends) {
			String relationship = friend.getRelationship();
			if (relationship!=null && relationship.equals("followingThem")) {
				FoursquareUser currValue = idToBrandMap.get(friend.getId());
				followingList.put(friend.getId(), currValue);
			}
		}
		return followingList;
	}
	
	public static Map<String, BrandDiscovered> createMap(
			List<BrandDiscovered> brands) {
		Map<String, BrandDiscovered> map = new HashMap<String, BrandDiscovered>();
		for (BrandDiscovered b : brands) {
			map.put(b.getBrandId(), b);
		}
		return map;
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
	
	public static Comparator<FoursquareUser> getBrandCompare(final Map<String, BrandDiscovered> discovered) {
		return new Comparator<FoursquareUser>() {
			public int compare(FoursquareUser o1, FoursquareUser o2) {
				BrandDiscovered d1= discovered.get(o1.getId());
				BrandDiscovered d2= discovered.get(o2.getId());
				
				if(d1!=null && d2!=null){
					return d2.getDate().compareTo(d1.getDate());
				} else if(d1==null && d2!=null) {
					return -1;
				} else if(d1!=null && d2==null) {
					return +1;
				} else {
					Long l1 = Long.parseLong(o1.getId());
					Long l2 = Long.parseLong(o2.getId());
					return l1.compareTo(l2);
				}
			}
		};
	}
}
