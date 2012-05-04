package com.handstandtech.brandfinder.server.foursquare;

import java.util.HashSet;

import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;
import com.handstandtech.foursquare.v2.FoursquareAPIv2;
import com.handstandtech.foursquare.v2.FoursquareAPIv2Impl;
import com.handstandtech.foursquare.v2.FoursquareNot200Exception;
import com.handstandtech.memcache.MemcacheScopeManager;
import com.handstandtech.shared.model.TimesInMilliseconds;

public class CachingFoursquareAPIv2Impl extends FoursquareAPIv2Impl {

	private static final String FRIENDS = "friends";

	public CachingFoursquareAPIv2Impl() {
		super();
	}

	public CachingFoursquareAPIv2Impl(String token) {
		super(token);
	}

	@Override
	public HashSet<FoursquareUser> getAllFriends() {
		String scope = getScope(FoursquareUser.class);
		String key = FRIENDS;
		HashSet<FoursquareUser> items = (HashSet<FoursquareUser>) MemcacheScopeManager
				.getFromCache(scope, key);
		if (items == null) {
			items = super.getAllFriends();
			Long tenMins = TimesInMilliseconds.ONE_MINUTE * 10;
			MemcacheScopeManager.setCleanWithExpiration(scope, key, items,
					tenMins);
		}
		return items;
	}

	private String getScope(Class<?> clazz) {
		return clazz.getSimpleName() + "-" + oauth_token;
	}

	/**
	 * @see FoursquareAPIv2#unFriendRequest(String)
	 */
	@Override
	public void unFriendRequest(String id) throws FoursquareNot200Exception {
		String scope = getScope(FoursquareUser.class);
		String key = FRIENDS;
		

		// Do the unfriending
		try {
			super.unFriendRequest(id);

			// If unfriending was successful, keep going
			HashSet<FoursquareUser> items = (HashSet<FoursquareUser>) MemcacheScopeManager
					.getFromCache(scope, key);
			if (items != null) {
				HashSet<FoursquareUser> newList = new HashSet<FoursquareUser>();
				for (FoursquareUser u : items) {
					if (!u.getId().equals(id)) {
						newList.add(u);
					}
				}
				MemcacheScopeManager.setClean(scope, key, newList);
			}
		} catch (FoursquareNot200Exception e) {
			log.warn("Handling not 200 by setting cache [" + scope
					+ "] as dirty.");
			MemcacheScopeManager.setDirty(scope);
			throw e;
		}
	}

	/**
	 * @see FoursquareAPIv2Impl#friendRequest(String)
	 */
	@Override
	public FoursquareUser friendRequest(String id)
			throws FoursquareNot200Exception {
		String scope = getScope(FoursquareUser.class);
		String key = FRIENDS;
		FoursquareUser newFriend = null;
		try {
			newFriend = super.friendRequest(id);
		} catch (FoursquareNot200Exception e) {
			log.warn("Handling not 200 by setting cache [" + scope
					+ "] as dirty.");
			MemcacheScopeManager.setDirty(scope);
			throw e;
		}

		// Get Current Friend List from Memcache
		HashSet<FoursquareUser> items = (HashSet<FoursquareUser>) MemcacheScopeManager
				.getFromCache(scope, key);
		if (items != null) {
			log.info("Friends were found, add to the Memcached list so we don't have to re-retrieve");
			items.add(newFriend);
			MemcacheScopeManager.setClean(scope, key, items);
		}
		return newFriend;
	}

	@Override
	public void logout() {
		log.info("User is logging out, clearing all scopes!");
		String scope = getScope(FoursquareUser.class);
		MemcacheScopeManager.setDirty(scope);
	}

}
