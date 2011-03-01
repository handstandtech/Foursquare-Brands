package com.handstandtech.brandfinder.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.googlecode.objectify.Key;
import com.handstandtech.brandfinder.server.twitter.FoursquareBrandsTwitter;
import com.handstandtech.brandfinder.server.util.PageLoadUtils;
import com.handstandtech.brandfinder.shared.model.BrandDiscovered;
import com.handstandtech.brandfinder.shared.model.User;
import com.handstandtech.foursquare.server.FoursquareHelper;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;

public class Manager {

	private static Logger log = Logger.getLogger(Manager.class.getName());

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

	public static Collection<FoursquareUser> findNewUsers(String userType,
			User currentUser, Collection<FoursquareUser> friends) {
		DAO dao = new DAO();

		Collection<Key<FoursquareUser>> existingKeys = new HashSet<Key<FoursquareUser>>();
		existingKeys.addAll(dao.createBrandQuery().listKeys());
		existingKeys.addAll(dao.createCelebQuery().listKeys());

		HashSet<String> ids = new HashSet<String>();
		for (Key<FoursquareUser> key : existingKeys) {
			String id = key.getName();
			ids.add(id);
		}

		Collection<String> newUsersFound = new ArrayList<String>();
		for (FoursquareUser friend : friends) {
			String friendId = friend.getId();
			boolean foundFriend = ids.contains(friendId);
			if (foundFriend == false) {
				// Lets make sure we are following them
				String relationship = friend.getRelationship();
				if (relationship.equals("followingThem")) {
					log.log(Level.INFO, "New User We're Following: " + friendId
							+ " -> " + friend.getName());
					newUsersFound.add(friendId);
				}
			}
		}

		FoursquareHelper helper = new FoursquareHelper(currentUser.getToken());
		Collection<FoursquareUser> newUsers = helper
				.getUserInfosForIds(newUsersFound);

		Collection<FoursquareUser> newUsersOfType = new ArrayList<FoursquareUser>();
		for (FoursquareUser user : newUsers) {
			// Go Through the new Users
			log.log(Level.INFO,
					"Adding NEW USER to Database: " + user.getName());
			dao.updateFoursquareUser(user);

			// Add Analytic about Discovered Brand
			BrandDiscovered brandDiscovered = new BrandDiscovered();
			brandDiscovered.setBrandId(user.getId());
			brandDiscovered.setUserId(currentUser.getId());
			dao.updateBrandDiscovered(brandDiscovered);
			FoursquareBrandsTwitter.queueTweet(user.getId());
		}

		return newUsersOfType;
	}

	public static void prepareFollowedAndNotFollowedLists(User currentUser,
			Collection<FoursquareUser> friends,
			Map<String, FoursquareUser> allBrandsOrCelebsMap,
			HttpServletRequest request) {
		
		HashSet<String> followingIds = new HashSet<String>();
		for(FoursquareUser f : friends){
			followingIds.add(f.getId());
		}
		
		Collection<String> followed = new HashSet<String>();
		Collection<String> notFollowed = new HashSet<String>();

		//Having trouble seeing who is currently followed!
		for (String userKey : allBrandsOrCelebsMap.keySet()) {
			boolean isFollowed = followingIds.contains(userKey);

			FoursquareUser userEntry = allBrandsOrCelebsMap.get(userKey);
			if (isFollowed) {
				// FOLLOWED
				followed.add(userEntry.getId());
			} else {
				// NOT FOLLOWED
				notFollowed.add(userEntry.getId());
			}
		}

		// List<BrandDiscovered> discoveredBrands = dao
		// .getBrandDiscoveredSince(null);
		// final Map<String, BrandDiscovered> discovered = PageLoadUtils
		// .createMap(discoveredBrands);
		//
		// try {
		// Collections.sort(followed,
		// PageLoadUtils.getBrandCompare(discovered));
		// Collections.sort(notFollowed,
		// PageLoadUtils.getBrandCompare(discovered));
		// } catch (Exception e) {
		// // DO nothing...
		// e.printStackTrace();
		// }

		request.setAttribute("followed", followed);
		request.setAttribute("notFollowed", notFollowed);
	}

	public static Map<String, FoursquareUser> getAllUsersMap() {
		Map<String, FoursquareUser> map = new HashMap<String, FoursquareUser>();
		DAO dao = new DAO();
		Collection<FoursquareUser> users = new ArrayList<FoursquareUser>();
		users.addAll(dao.getBrands());
		users.addAll(dao.getCelebrities());
		for (FoursquareUser u : users) {
			map.put(u.getId(), u);
		}
		return map;
	}
}
