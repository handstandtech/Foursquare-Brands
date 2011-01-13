package com.handstandtech.brandfinder.server;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.helper.DAOBase;
import com.handstandtech.brandfinder.shared.model.BrandDiscovered;
import com.handstandtech.brandfinder.shared.model.DailyFollowEventCount;
import com.handstandtech.brandfinder.shared.model.DailyFollowerCount;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;

public class DAO extends DAOBase {
	static {
		ObjectifyService.register(FoursquareUser.class);
		ObjectifyService.register(DailyFollowEventCount.class);
		ObjectifyService.register(DailyFollowerCount.class);
		ObjectifyService.register(BrandDiscovered.class);
	}

	/** Your DAO can have your own useful methods */
	public FoursquareUser getFoursquareUser(String id) {
		return ofy().find(FoursquareUser.class, id);
	}

	public Key<FoursquareUser> updateFoursquareUser(FoursquareUser user) {
		return ofy().put(user);
	}

	public List<FoursquareUser> getUsers() {
		return ofy().query(FoursquareUser.class).filter("type", "user").list();
	}

	public List<FoursquareUser> getBrands() {
		return ofy().query(FoursquareUser.class).filter("type", "brand").list();
	}

	public List<FoursquareUser> getCelebrities() {
		return ofy().query(FoursquareUser.class).filter("type", "celebrity").list();
	}

	public List<FoursquareUser> getAllFoursquareUserObjects() {
		return ofy().query(FoursquareUser.class).list();
	}

	public void deleteFoursquareUser(FoursquareUser user) {
		ofy().delete(user);
	}

	public List<DailyFollowEventCount> getDailyFollowEventsForBrand(String foursquareId, Date start, Date stop) {
		Query<DailyFollowEventCount> query = ofy().query(DailyFollowEventCount.class);
		query.filter("foursquareId", foursquareId);
		if (start != null) {
			query.filter("date >=", start);
		}
		
		if (stop != null) {
			query.filter("date <=", stop);
		}
		return query.list();

	}
	
	public List<DailyFollowerCount> getDailyFollowCountsForBrand(String foursquareId, Date start, Date stop) {
		Query<DailyFollowerCount> query = ofy().query(DailyFollowerCount.class);
		query.filter("foursquareId", foursquareId);
		if (start != null) {
			query.filter("date >=", start);
		}
		
		if (stop != null) {
			query.filter("date <=", stop);
		}
		return query.list();

	}
	
	public void updateCollection(Collection<?> collection) {
		ofy().put(collection);
	}
	
	public void deleteCollection(Collection<?> collection) {
		ofy().delete(collection);
	}

	public List<DailyFollowEventCount> getAllDailyFollowEventCount() {
		return ofy().query(DailyFollowEventCount.class).list();
	}

	public void updateDailyFollowerCount(DailyFollowerCount item) {
		ofy().put(item);
	}
	
	public void updateBrandDiscovered(BrandDiscovered item) {
		ofy().put(item);
	}

	public List<BrandDiscovered> getBrandDiscoveredSince(Date since) {
		Query<BrandDiscovered> query = ofy().query(BrandDiscovered.class);
		if (since != null) {
			query.filter("date >=", since);
		}

		return query.list();
	}

}