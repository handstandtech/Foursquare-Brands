package com.handstandtech.brandfinder.server;

import java.util.LinkedList;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.helper.DAOBase;
import com.handstandtech.brandfinder.shared.model.Analytic;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;

public class DAO extends DAOBase {
	static {
		ObjectifyService.register(FoursquareUser.class);
		ObjectifyService.register(Analytic.class);
	}

	/** Your DAO can have your own useful methods */
	public FoursquareUser getFoursquareUser(String id) {
		FoursquareUser found = ofy().find(FoursquareUser.class, id);
		return found;
	}
	
	public Key<FoursquareUser> updateFoursquareUser(FoursquareUser user) {
		return ofy().put(user);
	}
	
	public List<FoursquareUser> getUsers() {
		Query<FoursquareUser> query = ofy().query(FoursquareUser.class);
		query.filter("type", "user");
		
		List<FoursquareUser> list = new LinkedList<FoursquareUser>();
		for (FoursquareUser item : query) {
			list.add(item);
		}
		return list;
	}
	
	public List<FoursquareUser> getBrands() {
		Query<FoursquareUser> query = ofy().query(FoursquareUser.class);
		query.filter("type", "brand");
		
		List<FoursquareUser> list = new LinkedList<FoursquareUser>();
		for (FoursquareUser item : query) {
			list.add(item);
		}
		return list;
	}
	
	public List<FoursquareUser> getCelebrities() {
		Query<FoursquareUser> query = ofy().query(FoursquareUser.class);
		query.filter("type", "celebrity");
		
		List<FoursquareUser> list = new LinkedList<FoursquareUser>();
		for (FoursquareUser item : query) {
			list.add(item);
		}
		return list;
	}
	
	public List<FoursquareUser> getAllFoursquareUserObjects() {
		Query<FoursquareUser> query = ofy().query(FoursquareUser.class);
		
		List<FoursquareUser> list = new LinkedList<FoursquareUser>();
		for (FoursquareUser item : query) {
			list.add(item);
		}
		return list;
	}
	
	public Key<Analytic> updateAnalytic(Analytic analytic) {
		return ofy().put(analytic);
	}

	public void deleteFoursquareUser(FoursquareUser user) {
		ofy().delete(user);
	}
	
}