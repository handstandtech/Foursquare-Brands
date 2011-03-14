package com.handstandtech.brandfinder.server;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.googlecode.objectify.Key;
import com.handstandtech.brandfinder.shared.model.BrandDiscovered;
import com.handstandtech.brandfinder.shared.model.DailyFollowEventCount;
import com.handstandtech.brandfinder.shared.model.DailyFollowerCount;
import com.handstandtech.brandfinder.shared.model.User;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;
import com.handstandtech.memcache.CacheKeyMaker;
import com.handstandtech.memcache.MemcacheScopeManager;

public class CachingDAOImpl extends DAOImpl {

	@Override
	public List<BrandDiscovered> getNewestBrands(Integer limit) {
		String scope = getScope(BrandDiscovered.class);
		Map<String, Object> params = new HashMap<String, Object>();
		CacheKeyMaker.addParam(params, String.class, "limit", limit);
		String key = CacheKeyMaker.createKey(params);
		List<BrandDiscovered> items = (List<BrandDiscovered>) MemcacheScopeManager
				.getFromCache(scope, key);
		if (items == null) {
			items = super.getNewestBrands(limit);
			MemcacheScopeManager.setClean(scope, key, items);
		}
		return items;
	}

	@Override
	public List<BrandDiscovered> getNewestCelebrities(Integer limit) {
		String scope = getScope(BrandDiscovered.class);
		Map<String, Object> params = new HashMap<String, Object>();
		CacheKeyMaker.addParam(params, String.class, "limit", limit);
		String key = CacheKeyMaker.createKey(params);
		List<BrandDiscovered> items = (List<BrandDiscovered>) MemcacheScopeManager
				.getFromCache(scope, key);
		if (items == null) {
			items = super.getNewestCelebrities(limit);
			MemcacheScopeManager.setClean(scope, key, items);
		}
		return items;
	}

	@Override
	public List<FoursquareUser> getFoursquareUsersForIds(
			Collection<String> foursquareUserIds) {
		String scope = getScope(FoursquareUser.class);
		Map<String, Object> params = new HashMap<String, Object>();
		CacheKeyMaker.addParam(params, String.class, "foursquareUserIds", foursquareUserIds);
		String key = CacheKeyMaker.createKey(params);
		List<FoursquareUser> items = (List<FoursquareUser>) MemcacheScopeManager
				.getFromCache(scope, key);
		if (items == null) {
			items = super.getFoursquareUsersForIds(foursquareUserIds);
			MemcacheScopeManager.setClean(scope, key, items);
		}
		return items;
	}

	public CachingDAOImpl() {
		super();
	}

	/**
	 * @see DAOImpl#updateFoursquareUser(FoursquareUser)
	 */
	@Override
	public Key<FoursquareUser> updateFoursquareUser(FoursquareUser user) {
		String scope = getScope(FoursquareUser.class);
		MemcacheScopeManager.setDirty(scope);
		return super.updateFoursquareUser(user);
	}

	@Override
	public List<FoursquareUser> getBrands() {
		String scope = getScope(FoursquareUser.class);
		String key = CacheKeyMaker.createKey(null);
		List<FoursquareUser> items = (List<FoursquareUser>) MemcacheScopeManager
				.getFromCache(scope, key);
		if (items == null) {
			items = super.getBrands();
			MemcacheScopeManager.setClean(scope, key, items);
		}
		return items;
	}

	@Override
	public List<FoursquareUser> getCelebrities() {
		String scope = getScope(FoursquareUser.class);
		String key = CacheKeyMaker.createKey(null);
		List<FoursquareUser> items = (List<FoursquareUser>) MemcacheScopeManager
				.getFromCache(scope, key);
		if (items == null) {
			items = super.getCelebrities();
			MemcacheScopeManager.setClean(scope, key, items);
		}
		return items;
	}

	@Override
	public List<FoursquareUser> getAllFoursquareUserObjects() {
		String scope = getScope(FoursquareUser.class);
		String key = CacheKeyMaker.createKey(null);
		List<FoursquareUser> items = (List<FoursquareUser>) MemcacheScopeManager
				.getFromCache(scope, key);
		if (items == null) {
			items = super.getAllFoursquareUserObjects();
			MemcacheScopeManager.setClean(scope, key, items);
		}
		return items;
	}

	@Override
	public List<DailyFollowEventCount> getDailyFollowEventsForBrand(
			String foursquareId, Date start, Date stop) {
		String scope = getScope(DailyFollowEventCount.class);
		Map<String, Object> params = new HashMap<String, Object>();
		CacheKeyMaker.addParam(params, String.class, "foursquareId",
				foursquareId);
		CacheKeyMaker.addParam(params, Date.class, "start", start);
		CacheKeyMaker.addParam(params, Date.class, "stop", stop);
		String key = CacheKeyMaker.createKey(params);
		List<DailyFollowEventCount> items = (List<DailyFollowEventCount>) MemcacheScopeManager
				.getFromCache(scope, key);
		if (items == null) {
			items = super.getDailyFollowEventsForBrand(foursquareId, start,
					stop);
			MemcacheScopeManager.setClean(scope, key, items);
		}
		return items;
	}

	@Override
	public List<DailyFollowerCount> getDailyFollowCountsForBrand(
			String foursquareId, Date start, Date stop) {
		String scope = getScope(DailyFollowerCount.class);
		Map<String, Object> params = new HashMap<String, Object>();
		CacheKeyMaker.addParam(params, String.class, "foursquareId",
				foursquareId);
		CacheKeyMaker.addParam(params, Date.class, "start", start);
		CacheKeyMaker.addParam(params, Date.class, "stop", stop);
		String key = CacheKeyMaker.createKey(params);
		List<DailyFollowerCount> items = (List<DailyFollowerCount>) MemcacheScopeManager
				.getFromCache(scope, key);
		if (items == null) {
			items = super.getDailyFollowCountsForBrand(foursquareId, start,
					stop);
			MemcacheScopeManager.setClean(scope, key, items);
		}
		return items;
	}

	@Override
	public List<DailyFollowEventCount> getAllDailyFollowEventCount() {
		String scope = getScope(DailyFollowEventCount.class);
		String key = CacheKeyMaker.createKey(null);
		List<DailyFollowEventCount> items = (List<DailyFollowEventCount>) MemcacheScopeManager
				.getFromCache(scope, key);
		if (items == null) {
			items = super.getAllDailyFollowEventCount();
			MemcacheScopeManager.setClean(scope, key, items);
		}
		return items;
	}

	@Override
	public List<BrandDiscovered> getBrandDiscoveredSince(Date since) {
		String scope = getScope(BrandDiscovered.class);
		Map<String, Object> params = new HashMap<String, Object>();
		CacheKeyMaker.addParam(params, Date.class, "since", since);
		String key = CacheKeyMaker.createKey(params);
		List<BrandDiscovered> items = (List<BrandDiscovered>) MemcacheScopeManager
				.getFromCache(scope, key);
		if (items == null) {
			items = super.getBrandDiscoveredSince(since);
			MemcacheScopeManager.setClean(scope, key, items);
		}
		return items;
	}

	@Override
	public Integer getBrandCount() {
		String scope = getScope(FoursquareUser.class);
		String key = CacheKeyMaker.createKey(null);
		Integer items = (Integer) MemcacheScopeManager.getFromCache(scope, key);
		if (items == null) {
			items = super.getBrandCount();
			MemcacheScopeManager.setClean(scope, key, items);
		}
		return items;
	}

	@Override
	public Integer getCelebCount() {
		String scope = getScope(FoursquareUser.class);
		String key = CacheKeyMaker.createKey(null);
		Integer items = (Integer) MemcacheScopeManager.getFromCache(scope, key);
		if (items == null) {
			items = super.getBrandCount();
			MemcacheScopeManager.setClean(scope, key, items);
		}
		return items;
	}

	@Override
	public HashSet<String> getCelebIds() {
		String scope = getScope(FoursquareUser.class);
		String key = CacheKeyMaker.createKey(null);
		HashSet<String> items = (HashSet<String>) MemcacheScopeManager
				.getFromCache(scope, key);
		if (items == null) {
			items = super.getCelebIds();
			MemcacheScopeManager.setClean(scope, key, items);
		}
		return items;
	}

	@Override
	public HashSet<String> getBrandIds() {
		String scope = getScope(FoursquareUser.class);
		String key = CacheKeyMaker.createKey(null);
		HashSet<String> items = (HashSet<String>) MemcacheScopeManager
				.getFromCache(scope, key);
		if (items == null) {
			items = super.getBrandIds();
			MemcacheScopeManager.setClean(scope, key, items);
		}
		return items;
	}

	@Override
	public void updateDailyFollowerCount(DailyFollowerCount item) {
		String scope = getScope(DailyFollowerCount.class);
		MemcacheScopeManager.setDirty(scope);
		super.updateDailyFollowerCount(item);
	}

	/**
	 * @see DAOImpl#updateBrandDiscovered(BrandDiscovered)
	 */
	@Override
	public Key<BrandDiscovered> updateBrandDiscovered(BrandDiscovered item) {
		String scope = getScope(BrandDiscovered.class);
		MemcacheScopeManager.setDirty(scope);
		return super.updateBrandDiscovered(item);
	}

	@Override
	public void updateDailyFollowEventCounts(
			Collection<DailyFollowEventCount> dailyCounts) {
		String scope = getScope(DailyFollowEventCount.class);
		MemcacheScopeManager.setDirty(scope);
		super.updateDailyFollowEventCounts(dailyCounts);
	}

	/**
	 * @see DAOImpl#updateUser(User)
	 */
	@Override
	public Key<User> updateUser(User user) {
		String scope = getScope(User.class);
		MemcacheScopeManager.setDirty(scope);
		return super.updateUser(user);
	}

	@Override
	public List<FoursquareUser> getCelebrities(Integer offset, Integer limit) {
		String scope = getScope(FoursquareUser.class);
		Map<String, Object> params = new HashMap<String, Object>();
		CacheKeyMaker.addParam(params, Integer.class, "offset", offset);
		CacheKeyMaker.addParam(params, Integer.class, "limit", limit);
		String key = CacheKeyMaker.createKey(params);
		List<FoursquareUser> items = (List<FoursquareUser>) MemcacheScopeManager
				.getFromCache(scope, key);
		if (items == null) {
			items = super.getCelebrities(offset, limit);
			MemcacheScopeManager.setClean(scope, key, items);
		}
		return items;
	}

	@Override
	public List<FoursquareUser> getBrands(Integer offset, Integer limit) {
		String scope = getScope(FoursquareUser.class);
		Map<String, Object> params = new HashMap<String, Object>();
		CacheKeyMaker.addParam(params, Integer.class, "offset", offset);
		CacheKeyMaker.addParam(params, Integer.class, "limit", limit);
		String key = CacheKeyMaker.createKey(params);
		List<FoursquareUser> items = (List<FoursquareUser>) MemcacheScopeManager
				.getFromCache(scope, key);
		if (items == null) {
			items = super.getBrands(offset, limit);
			MemcacheScopeManager.setClean(scope, key, items);
		}
		return items;
	}

	private String getScope(Class<?> clazz) {
		return "DAO-" + clazz.getSimpleName();
	}
}
