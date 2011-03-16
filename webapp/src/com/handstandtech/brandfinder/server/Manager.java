package com.handstandtech.brandfinder.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.handstandtech.brandfinder.server.twitter.FoursquareBrandsTwitter;
import com.handstandtech.brandfinder.server.util.PageLoadUtils;
import com.handstandtech.brandfinder.shared.model.BrandDiscovered;
import com.handstandtech.brandfinder.shared.model.User;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;
import com.handstandtech.foursquare.v2.FoursquareAPIv2;
import com.handstandtech.foursquare.v2.impl.CachingFoursquareAPIv2Impl;

public class Manager {

	private static Logger log = LoggerFactory.getLogger(Manager.class);

	public static Map<String, FoursquareUser> getBrandMap(HttpSession session) {
		DAO dao = new CachingDAOImpl();
		List<FoursquareUser> brands = dao.getBrands();

		// [INITIALIZATION CASE] -If there are currently no brands
		if (brands.size() == 0) {
			log.warn("Initialization Case, getting brands from CSV file.");
			PageLoadUtils.initializeBrandListFromCSVFile(session);

			// re-retreive brands from the database
			brands = dao.getBrands();
		}

		return createUserMap(brands);
	}

	public static Map<String, FoursquareUser> getCelebMap(HttpSession session) {
		DAO dao = new CachingDAOImpl();
		List<FoursquareUser> users = dao.getCelebrities();

		// [INITIALIZATION CASE] -If there are currently no brands
		if (users.size() == 0) {
			log.warn("Initialization Case, getting brands from CSV file.");
			PageLoadUtils.initializeBrandListFromCSVFile(session);

			// re-retreive brands from the database
			users = dao.getBrands();
		}

		return createUserMap(users);
	}

	public static Map<String, FoursquareUser> createUserMap(
			Collection<FoursquareUser> users) {
		Map<String, FoursquareUser> userMap = new HashMap<String, FoursquareUser>();
		for (FoursquareUser user : users) {
			userMap.put(user.getId(), user);
		}
		return userMap;
	}

	public static Collection<FoursquareUser> getCurrentUsersFriends(
			User currentUser) {
		Collection<FoursquareUser> friends = new HashSet<FoursquareUser>();
		if (currentUser != null) {
			FoursquareAPIv2 helper = new CachingFoursquareAPIv2Impl(
					currentUser.getToken());
			friends = helper.getAllFriends();
		} else {
			log.error("User is NULL");
		}

		log.info("See if they know any new users, if so, add them.");
		Manager.findNewUsers(currentUser, friends);

		return friends;
	}

	private static Collection<FoursquareUser> findNewUsers(User currentUser,
			Collection<FoursquareUser> friends) {
		DAO dao = new CachingDAOImpl();

		HashSet<String> ids = new HashSet<String>();
		ids.addAll(dao.getCelebIds());
		ids.addAll(dao.getBrandIds());

		Collection<String> newUsersFound = new ArrayList<String>();
		for (FoursquareUser friend : friends) {
			String friendId = friend.getId();
			boolean foundFriend = ids.contains(friendId);
			if (foundFriend == false) {
				// Lets make sure we are following them
				String relationship = friend.getRelationship();
				if (relationship.equals("followingThem")) {
					log.info("New User We're Following: " + friendId + " -> "
							+ friend.getName());
					newUsersFound.add(friendId);
				}
			}
		}

		FoursquareAPIv2 helper = new CachingFoursquareAPIv2Impl(
				currentUser.getToken());
		Collection<FoursquareUser> newUsers = helper
				.getUserInfosForIds(newUsersFound);

		Collection<FoursquareUser> newUsersOfType = new ArrayList<FoursquareUser>();
		for (FoursquareUser user : newUsers) {
			String newUserId = user.getId();
			BrandDiscovered brandDiscovered = dao.getBrandDiscovered(newUserId);
			if (brandDiscovered == null) {
				// Go Through the new Users
				log.info("Adding NEW USER to Database: " + user.getName());
				dao.updateFoursquareUser(user);

				// Add Analytic about Discovered Brand
				brandDiscovered = new BrandDiscovered();
				brandDiscovered.setBrandId(newUserId);
				brandDiscovered.setType(user.getType());
				brandDiscovered.setUserId(currentUser.getId());
				dao.updateBrandDiscovered(brandDiscovered);
				FoursquareBrandsTwitter.queueTweet(user.getId());
			} else {
				log.warn("Someone already added the brand first! -> "
						+ brandDiscovered.getUserId() + " -- ["
						+ brandDiscovered.getId() + "]");
			}
		}

		return newUsersOfType;
	}

	public static void prepareFollowedAndNotFollowedLists(User currentUser,
			Collection<FoursquareUser> friends,
			Map<String, FoursquareUser> allBrandsOrCelebsMap,
			HttpServletRequest request) {

		HashSet<String> followingIds = new HashSet<String>();
		for (FoursquareUser f : friends) {
			followingIds.add(f.getId());
		}

		Collection<String> followed = new HashSet<String>();
		Collection<String> notFollowed = new HashSet<String>();

		// Having trouble seeing who is currently followed!
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

		request.setAttribute("followed", followed);
		request.setAttribute("notFollowed", notFollowed);
	}

	public static Map<String, FoursquareUser> getAllUsersMap() {
		Map<String, FoursquareUser> map = new HashMap<String, FoursquareUser>();
		DAO dao = new CachingDAOImpl();
		Collection<FoursquareUser> users = new ArrayList<FoursquareUser>();
		users.addAll(dao.getBrands());
		users.addAll(dao.getCelebrities());
		for (FoursquareUser u : users) {
			map.put(u.getId(), u);
		}
		return map;
	}
}
