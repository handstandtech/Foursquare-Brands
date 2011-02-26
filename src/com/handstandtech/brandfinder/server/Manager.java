package com.handstandtech.brandfinder.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.handstandtech.brandfinder.server.twitter.FoursquareBrandsTwitter;
import com.handstandtech.brandfinder.server.util.PageLoadUtils;
import com.handstandtech.brandfinder.shared.model.BrandDiscovered;
import com.handstandtech.brandfinder.shared.model.User;
import com.handstandtech.foursquare.server.FoursquareHelper;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;

public class Manager {
	public static Map<String, FoursquareUser> getBrandMap(HttpSession session) {
		DAO dao = new DAO();
		List<FoursquareUser> brands = dao.getBrands();

		// [INITIALIZATION CASE] -If there are currently no brands
		if (brands.size() == 0) {
			System.out.println("Currently... Do Nothing");
			PageLoadUtils.initializeBrandListFromCSVFile(session);

			// re-retreive brands from the database
			brands = dao.getBrands();
		}

		return createUserMap(brands);
	}

	public static Map<String, FoursquareUser> getCelebMap(HttpSession session) {
		DAO dao = new DAO();
		List<FoursquareUser> users = dao.getCelebrities();

		// [INITIALIZATION CASE] -If there are currently no brands
		if (users.size() == 0) {
			System.out.println("Currently... Do Nothing");
			PageLoadUtils.initializeBrandListFromCSVFile(session);

			// re-retreive brands from the database
			users = dao.getBrands();
		}

		return createUserMap(users);
	}

	public static Map<String, FoursquareUser> createUserMap(
			List<FoursquareUser> users) {
		Map<String, FoursquareUser> userMap = new HashMap<String, FoursquareUser>();
		for (FoursquareUser user : users) {
			userMap.put(user.getId(), user);
		}
		return userMap;
	}

	public static Collection<FoursquareUser> getCurrentUsersFriends(
			User currentUser) {
		FoursquareHelper helper = new FoursquareHelper(currentUser.getToken());
		Collection<FoursquareUser> friends = new HashSet<FoursquareUser>();
		Boolean keepGoing = true;
		Integer offset = 0;
		String userId = "self";
		List<FoursquareUser> friendResponseList = helper.getFriends(userId,
				offset);
		while (keepGoing) {
			System.out.println("Adding: " + friendResponseList.size());
			friends.addAll(friendResponseList);
			if (friendResponseList.size() >= 500) {
				keepGoing = true;
				offset = offset + 500;
				friendResponseList = helper.getFriends(userId, offset);
			} else {
				keepGoing = false;
			}
		}
		return friends;
	}

	public static Map<String, FoursquareUser> findNewUsers(String userType,
			User currentUser, Map<String, FoursquareUser> mapOfFollowedUsers) {
		DAO dao = new DAO();

		Map<String, FoursquareUser> allUsersMap = Manager.getAllUsersMap();

		FoursquareHelper helper = new FoursquareHelper(currentUser.getToken());
		// See if there are any that weren't in the database
		for (String followedUserId : mapOfFollowedUsers.keySet()) {
			FoursquareUser user = allUsersMap.get(followedUserId);
			if (user == null) {
				// FOUND SOMETHING NEW, SEE IF IT'S A BRAND
				// user = mapOfFollowedUsers.get(followedUserId);
				// if (user == null) {
				user = helper.getUserInfo(followedUserId);

				// IT IS NEW
				dao.updateFoursquareUser(user);

				// Add Analytic about Discovered Brand
				BrandDiscovered brandDiscovered = new BrandDiscovered();
				brandDiscovered.setDate(new Date());
				brandDiscovered.setBrandId(user.getId());
				brandDiscovered.setUserId(currentUser.getId());
				dao.updateBrandDiscovered(brandDiscovered);
				// FoursquareBrandsTwitter.queueTweet(user.getId());
				allUsersMap.put(user.getId(), user);
				if (userType.substring(0, 4).equals(
						user.getType().substring(0, 4))) {
					mapOfFollowedUsers.put(user.getId(), user);
				}
			}
		}
		return mapOfFollowedUsers;
	}

	public static void prepareFollowedAndNotFollowedLists(User currentUser,
			Map<String, FoursquareUser> mapOfFollowedUsers,
			Map<String, FoursquareUser> userMap, HttpServletRequest request) {
		DAO dao = new DAO();
		List<FoursquareUser> followed = new ArrayList<FoursquareUser>();
		List<FoursquareUser> notFollowed = new ArrayList<FoursquareUser>();

		for (String brandKey : userMap.keySet()) {
			FoursquareUser brand = mapOfFollowedUsers.get(brandKey);
			boolean isFollowed = false;
			if (brand != null) {
				isFollowed = true;
			}

			FoursquareUser brandEntry = userMap.get(brandKey);
			if (isFollowed) {
				// FOLLOWED
				followed.add(brandEntry);
			} else {
				// NOT FOLLOWED
				notFollowed.add(brandEntry);
			}
		}

		List<BrandDiscovered> discoveredBrands = dao
				.getBrandDiscoveredSince(null);
		final Map<String, BrandDiscovered> discovered = PageLoadUtils
				.createMap(discoveredBrands);

		try {
			Collections.sort(followed,
					PageLoadUtils.getBrandCompare(discovered));
			Collections.sort(notFollowed,
					PageLoadUtils.getBrandCompare(discovered));
		} catch (Exception e) {
			// DO nothing...
			e.printStackTrace();
		}

		request.setAttribute("followed", followed);
		request.setAttribute("notFollowed", notFollowed);
	}

	public static Map<String, FoursquareUser> getAllUsersMap() {
		Map<String, FoursquareUser> map = new HashMap<String, FoursquareUser>();
		DAO dao = new DAO();
		Collection<FoursquareUser> users = dao.getAllFoursquareUserObjects();
		for (FoursquareUser u : users) {
			map.put(u.getId(), u);
		}
		return map;
	}
}
