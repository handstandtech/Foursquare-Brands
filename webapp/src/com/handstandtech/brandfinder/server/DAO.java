package com.handstandtech.brandfinder.server;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.googlecode.objectify.Key;
import com.handstandtech.brandfinder.shared.model.BrandDiscovered;
import com.handstandtech.brandfinder.shared.model.DailyFollowEventCount;
import com.handstandtech.brandfinder.shared.model.DailyFollowerCount;
import com.handstandtech.brandfinder.shared.model.User;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;

public interface DAO {

	/** Your DAO can have your own useful methods */
	public abstract FoursquareUser findFoursquareUser(String id);

	public abstract Key<FoursquareUser> updateFoursquareUser(FoursquareUser user);

	public abstract List<User> getRecentlyActiveUsers(Integer limit);

	public abstract List<FoursquareUser> getBrands();

	public abstract List<FoursquareUser> getCelebrities();

	public abstract HashSet<String> getCelebIds();

	public abstract HashSet<String> getBrandIds();

	public abstract List<FoursquareUser> getCelebrities(Integer offset,
			Integer limit);

	public abstract List<FoursquareUser> getBrands(Integer offset, Integer limit);

	public abstract List<FoursquareUser> getAllFoursquareUserObjects();

	public abstract List<DailyFollowEventCount> getDailyFollowEventsForBrand(
			String foursquareId, Date start, Date stop);

	public abstract List<DailyFollowerCount> getDailyFollowCountsForBrand(
			String foursquareId, Date start, Date stop);

	public abstract List<DailyFollowEventCount> getAllDailyFollowEventCount();

	public abstract void updateDailyFollowerCount(DailyFollowerCount item);

	public abstract Key<BrandDiscovered> updateBrandDiscovered(
			BrandDiscovered item);

	public abstract List<BrandDiscovered> getBrandDiscoveredSince(Date since);

	public abstract Key<User> updateUser(User user);

	public abstract User findUser(String id);

	public abstract void updateDailyFollowEventCounts(
			Collection<DailyFollowEventCount> dailyCounts);

	public abstract List<BrandDiscovered> getNewestBrands(Integer limit);

	public abstract BrandDiscovered getBrandDiscovered(String foursquareId);
	
	public abstract List<BrandDiscovered> getNewestCelebrities(Integer limit);

	public abstract List<FoursquareUser> getFoursquareUsersForIds(
			Collection<String> foursquareUserIds);
}