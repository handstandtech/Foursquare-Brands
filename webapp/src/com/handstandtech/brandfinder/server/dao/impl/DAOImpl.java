package com.handstandtech.brandfinder.server.dao.impl;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.helper.DAOBase;
import com.handstandtech.brandfinder.server.dao.DAO;
import com.handstandtech.brandfinder.shared.model.BrandDiscovered;
import com.handstandtech.brandfinder.shared.model.DailyFollowEventCount;
import com.handstandtech.brandfinder.shared.model.DailyFollowerCount;
import com.handstandtech.brandfinder.shared.model.User;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;

public abstract class DAOImpl extends DAOBase implements DAO {

	private static final Logger log = LoggerFactory.getLogger(DAO.class);

	static {
		ObjectifyService.register(FoursquareUser.class);
		ObjectifyService.register(DailyFollowEventCount.class);
		ObjectifyService.register(DailyFollowerCount.class);
		ObjectifyService.register(BrandDiscovered.class);
		ObjectifyService.register(User.class);
	}

	/**
	 * @see com.handstandtech.brandfinder.server.dao.DAO#findFoursquareUser(java.lang.String)
	 */
	@Override
	public final FoursquareUser findFoursquareUser(String id) {
		return ofy().find(FoursquareUser.class, id);
	}

	/**
	 * @see com.handstandtech.brandfinder.server.dao.DAO#updateFoursquareUser(com.handstandtech.foursquare.shared.model.v2.FoursquareUser)
	 */
	@Override
	public Key<FoursquareUser> updateFoursquareUser(FoursquareUser user) {
		Key<FoursquareUser> foursquareUserKey = ofy().put(user);
		return foursquareUserKey;
	}

	/**
	 * @see com.handstandtech.brandfinder.server.dao.DAO#getRecentlyActiveUsers(java.lang.Integer)
	 */
	@Override
	public final List<User> getRecentlyActiveUsers(Integer limit) {
		Query<User> usersQuery = ofy().query(User.class).order("-lastLogin");
		if (limit != null) {
			usersQuery.limit(limit);
		}
		return usersQuery.list();
	}

	/**
	 * @see com.handstandtech.brandfinder.server.dao.DAO#getBrands()
	 */
	@Override
	public List<FoursquareUser> getBrands() {
		Query<FoursquareUser> brandQuery = createBrandQuery();
		return brandQuery.list();
	}

	private Query<FoursquareUser> createBrandQuery() {
		return ofy().query(FoursquareUser.class).filter("type", "brand")
				.order("-followers.count");
	}

	private Query<FoursquareUser> createCelebQuery() {
		return ofy().query(FoursquareUser.class).filter("type", "celebrity")
				.order("-followers.count");
	}

	/**
	 * @see com.handstandtech.brandfinder.server.dao.DAO#getCelebrities()
	 */
	@Override
	public List<FoursquareUser> getCelebrities() {
		Query<FoursquareUser> celebQuery = createCelebQuery();
		return celebQuery.list();
	}

	/**
	 * @see com.handstandtech.brandfinder.server.dao.DAO#getAllFoursquareUserObjects()
	 */
	@Override
	public List<FoursquareUser> getAllFoursquareUserObjects() {
		return ofy().query(FoursquareUser.class).list();
	}

	/**
	 * @see com.handstandtech.brandfinder.server.dao.DAO#getDailyFollowEventsForBrand(java.lang.String,
	 *      java.util.Date, java.util.Date)
	 */
	@Override
	public List<DailyFollowEventCount> getDailyFollowEventsForBrand(
			String foursquareId, Date start, Date stop) {
		Query<DailyFollowEventCount> query = ofy().query(
				DailyFollowEventCount.class);
		query.filter("foursquareId", foursquareId);
		if (start != null) {
			query.filter("date >=", start);
		}

		if (stop != null) {
			query.filter("date <=", stop);
		}
		return query.list();

	}

	/**
	 * @see com.handstandtech.brandfinder.server.dao.DAO#getDailyFollowCountsForBrand(java.lang.String,
	 *      java.util.Date, java.util.Date)
	 */
	@Override
	public List<DailyFollowerCount> getDailyFollowCountsForBrand(
			String foursquareId, Date start, Date stop) {
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

	/**
	 * @see com.handstandtech.brandfinder.server.dao.DAO#getAllDailyFollowEventCount()
	 */
	@Override
	public List<DailyFollowEventCount> getAllDailyFollowEventCount() {
		return ofy().query(DailyFollowEventCount.class).list();
	}

	/**
	 * @see com.handstandtech.brandfinder.server.dao.DAO#updateDailyFollowerCount(com.handstandtech.brandfinder.shared.model.DailyFollowerCount)
	 */
	@Override
	public void updateDailyFollowerCount(DailyFollowerCount item) {
		ofy().put(item);
	}

	/**
	 * @see com.handstandtech.brandfinder.server.dao.DAO#updateBrandDiscovered(com.handstandtech.brandfinder.shared.model.BrandDiscovered)
	 */
	@Override
	public Key<BrandDiscovered> updateBrandDiscovered(BrandDiscovered item) {
		// log.info("Using a transaction to update the discovered user - "
		// + item.getBrandId() + " " + item.getType());
//		Objectify ofy = ObjectifyService.beginTransaction();
		Key<BrandDiscovered> key = ofy().put(item);
//		ofy.getTxn().commit();
		return key;
	}

	/**
	 * @see com.handstandtech.brandfinder.server.dao.DAO#getBrandDiscovered(String)
	 */
	@Override
	public BrandDiscovered getBrandDiscovered(String foursquareId) {
		Query<BrandDiscovered> query = ofy().query(BrandDiscovered.class).filter(
				"brandId", foursquareId);
		BrandDiscovered brandDiscovered = query.get();
		return brandDiscovered;
	}

	/**
	 * @see com.handstandtech.brandfinder.server.dao.DAO#getBrandDiscoveredSince(java.util.Date)
	 */
	@Override
	public List<BrandDiscovered> getBrandDiscoveredSince(Date since) {
		Query<BrandDiscovered> query = ofy().query(BrandDiscovered.class);
		if (since != null) {
			query.filter("date >=", since);
		}
		List<BrandDiscovered> list = query.list();
		return list;
	}

	/**
	 * @see com.handstandtech.brandfinder.server.dao.DAO#updateUser(com.handstandtech.brandfinder.shared.model.User)
	 */
	@Override
	public Key<User> updateUser(User user) {
		return ofy().put(user);
	}

	/**
	 * @see com.handstandtech.brandfinder.server.dao.DAO#findUser(java.lang.String)
	 */
	@Override
	public final User findUser(String id) {
		return ofy().find(User.class, id);
	}

	@Override
	public HashSet<String> getCelebIds() {
		return getKeyHashset(createCelebQuery().listKeys());
	}

	@Override
	public HashSet<String> getBrandIds() {
		return getKeyHashset(createBrandQuery().listKeys());
	}

	private HashSet<String> getKeyHashset(List<Key<FoursquareUser>> keys) {
		HashSet<String> ids = new HashSet<String>();
		for (Key<FoursquareUser> key : keys) {
			String id = key.getName();
			ids.add(id);
		}
		return ids;
	}

	@Override
	public void updateDailyFollowEventCounts(
			Collection<DailyFollowEventCount> dailyCounts) {
		ofy().put(dailyCounts);
	}

	@Override
	public List<FoursquareUser> getCelebrities(Integer offset, Integer limit) {
		Query<FoursquareUser> query = createCelebQuery();
		if (offset != null) {
			query.offset(offset);
		}

		if (limit != null) {
			query.limit(limit);
		}
		return query.list();
	}

	@Override
	public List<FoursquareUser> getBrands(Integer offset, Integer limit) {
		Query<FoursquareUser> query = createBrandQuery();
		if (offset != null) {
			query.offset(offset);
		}

		if (limit != null) {
			query.limit(limit);
		}
		return query.list();
	}

	/**
	 * @see DAO#getNewestBrands(Integer)
	 */
	@Override
	public List<BrandDiscovered> getNewestBrands(Integer limit) {
		Query<BrandDiscovered> query = ofy().query(BrandDiscovered.class)
				.filter("type", "brand").order("-date");
		if (limit != null) {
			query.limit(limit);
		}
		return query.list();
	}

	/**
	 * @see DAO#getNewestCelebrities(Integer)
	 */
	@Override
	public List<BrandDiscovered> getNewestCelebrities(Integer limit) {
		Query<BrandDiscovered> query = ofy().query(BrandDiscovered.class)
				.filter("type", "celebrity").order("-date");
		if (limit != null) {
			query.limit(limit);
		}
		return query.list();
	}

	@Override
	public List<FoursquareUser> getFoursquareUsersForIds(
			Collection<String> foursquareUserIds) {
		Query<FoursquareUser> query = ofy().query(FoursquareUser.class);
		if (foursquareUserIds != null && !foursquareUserIds.isEmpty()) {
			query.filter("id in", foursquareUserIds);
			return query.list();
		} else {
			log.error("List of User Ids cannot be empty or null!");
			return null;
		}
	}
}