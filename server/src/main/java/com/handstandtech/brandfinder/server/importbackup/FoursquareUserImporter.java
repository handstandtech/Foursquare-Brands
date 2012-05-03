package com.handstandtech.brandfinder.server.importbackup;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser.Contact;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser.Followers;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser.Friends;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser.Mayorships;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser.Todos;

public class FoursquareUserImporter extends CSVImporter {

	private static final Logger log = LoggerFactory
			.getLogger(FoursquareUserImporter.class);

	public static List<FoursquareUser> load(String base)
			throws Exception {
		List<FoursquareUser> activities = new ArrayList<FoursquareUser>();
		File file = new File(base + "/FoursquareUser.csv");

		CSVReader csvReader = new CSVReader(new FileReader(file));

		// Skip First Line
		String[] fields = csvReader.readNext();
		String[] l;
		while ((l = csvReader.readNext()) != null) {

			// ,,,,,,,,,,,,,
			// ACTIVE,image/jpeg,136001,2011-03-14T22:32:04,136001,traqmate,False,1024,5814,1024,135242,,USER,handstandtechsquare.jpg

			FoursquareUser u = new FoursquareUser();
			for (int x = 0; x < l.length; x++) {
				String field = fields[x];
				String value = l[x];

				if (field.startsWith("todos.")) {

					Todos todos = u.getTodos();
					if (todos == null) {
						todos = new Todos();
					}

					if ("todos.count".equals(field)) {
						todos.setCount(getLong(value));
					}
					u.setTodos(todos);
				} else if ("photo".equals(field)) {
					u.setPhoto(value);
				} else if ("firstName".equals(field)) {
					u.setFirstName(value);
				} else if ("key".equals(field)) {
					u.setId(value);
				} else if ("gender".equals(field)) {
					u.setGender(value);
				} else if ("homeCity".equals(field)) {
					u.setHomeCity(value);
				} else if ("type".equals(field)) {
					u.setType(value);
				} else if ("lastName".equals(field)) {
					u.setLastName(value);
				} else if ("lastUpdate".equals(field)) {
					u.setLastUpdate(getDate(value));
				} else if (field.startsWith("followers.")) {
					Followers followers = u.getFollowers();
					if (followers == null) {
						followers = new Followers();
					}

					if ("followers.count".equals(field)) {
						followers.setCount(getLong(value));
					}
					u.setFollowers(followers);
				} else if (field.startsWith("contact.")) {
					Contact contact = u.getContact();
					if (contact == null) {
						contact = new Contact();
					}

					if ("contact.facebook".equals(field)) {
						contact.setFacebook(value);
					} else if ("contact.phone".equals(field)) {
						contact.setPhone(value);
					} else if ("contact.email".equals(field)) {
						contact.setEmail(value);
					} else if ("contact.twitter".equals(field)) {
						contact.setTwitter(value);
					}
					u.setContact(contact);

				} else if (field.startsWith("friends.")) {
					Friends friends = u.getFriends();
					if (friends == null) {
						friends = new Friends();
					}
					if ("friends.count".equals(field)) {
						friends.setCount(getLong(value));
					}
					u.setFriends(friends);
				} else if (field.startsWith("mayorships.")) {
					Mayorships mayorships = u.getMayorships();
					if (mayorships == null) {
						mayorships = new Mayorships();
					}

					if ("mayorships.count".equals(field)) {
						mayorships.setCount(getLong(value));
					}

					u.setMayorships(mayorships);
				}
			}
			activities.add(u);
		}
		csvReader.close();
		return activities;
	}
}
