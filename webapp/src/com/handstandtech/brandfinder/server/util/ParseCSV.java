package com.handstandtech.brandfinder.server.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oauth.signpost.OAuthConsumer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

import com.google.gson.Gson;
import com.handstandtech.foursquare.oauth.OAuthAuthenticator;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser.Contact;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser.Followers;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser.Friends;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser.Mayorships;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser.Todos;
import com.handstandtech.server.rest.impl.RESTClientJavaNetImpl;
import com.handstandtech.shared.model.rest.RESTResult;
import com.handstandtech.shared.model.rest.RequestMethod;

public class ParseCSV {

	private static final Logger log = LoggerFactory.getLogger(ParseCSV.class);

	public ParseCSV() {

	}

	public List<FoursquareUser> parseBrandsCSV(String path) throws Exception {
		List<FoursquareUser> users = new ArrayList<FoursquareUser>();

		InputStream is = new FileInputStream(path);
		InputStreamReader isReader = new InputStreamReader(is);
		CSVReader reader = new CSVReader(isReader);

		String[] line = reader.readNext();

		// Skip first line
		line = reader.readNext();

		while (line != null) {
			// todos.count,photo,firstName,lastUpdate,lastName,contact.facebook,contact.phone,homeCity,type,friends.count
			// ,key,gender,mayorships.count,contact.email,contact.twitter
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			FoursquareUser user = new FoursquareUser();

			int i = 0;
			Long todosCount = Long.parseLong(line[i++]);
			String photo = line[i++];
			String firstName = line[i++];
			Long followersCount = Long.parseLong(line[i++]);
			String lastName = line[i++];
			Date lastUpdate = sdf.parse(line[i++]);
			String contactFacebook = line[i++];
			String contactPhone = line[i++];
			String homeCity = line[i++];
			String type = line[i++];
			Long friendsCount = Long.parseLong(line[i++]);
			String key = line[i++];
			String gender = line[i++];
			Long mayorshipsCount = Long.parseLong(line[i++]);
			String contactEmail = line[i++];
			String contactTwitter = line[i++];

			Todos todos = new Todos();
			todos.setCount(todosCount);
			user.setTodos(todos);
			user.setPhoto(photo);
			user.setFirstName(firstName);
			user.setLastUpdate(lastUpdate);
			user.setLastName(lastName);
			user.setHomeCity(homeCity);
			user.setType(type);
			user.setId(key);
			user.setGender(gender);

			Mayorships mayorships = new Mayorships();
			mayorships.setCount(mayorshipsCount);

			Friends friends = new Friends();
			friends.setCount(friendsCount);
			user.setFriends(friends);

			Followers followers = new Followers();
			followers.setCount(followersCount);
			user.setFollowers(followers);

			Contact contact = new Contact();
			contact.setFacebook(contactFacebook);
			contact.setPhone(contactPhone);
			contact.setTwitter(contactTwitter);
			contact.setTwitter(contactEmail);
			user.setContact(contact);

			users.add(user);
			line = reader.readNext();
		}

		return users;
	}

	public List<FoursquareUser> getFollowed(List<FoursquareUser> friends,
			Map<String, FoursquareUser> brands) {
		List<FoursquareUser> followed = new ArrayList<FoursquareUser>();
		for (FoursquareUser friend : friends) {
			String key = friend.getId().toString();
			FoursquareUser brand = brands.get(key);
			if (brand != null) {
				followed.add(brand);
			}
		}
		return followed;
	}

	public List<FoursquareUser> getUnfollowed(List<FoursquareUser> followed,
			Map<String, FoursquareUser> brands) {
		List<FoursquareUser> unfollowed = new ArrayList<FoursquareUser>();

		Map<String, FoursquareUser> followedMap = new HashMap<String, FoursquareUser>();
		for (FoursquareUser f : followed) {
			followedMap.put(f.getId().toString(), f);
		}

		for (FoursquareUser brand : brands.values()) {
			String brandKey = brand.getId().toString();

			FoursquareUser b = followedMap.get(brandKey);
			if (b == null) {
				unfollowed.add(brands.get(brandKey));
			}
		}
		return unfollowed;
	}

	public List<FoursquareUser> getCurrentFriends(OAuthConsumer consumer) {
		List<FoursquareUser> users = new ArrayList<FoursquareUser>();

		if (consumer != null) {
			RESTClientJavaNetImpl client = new RESTClientJavaNetImpl();

			OAuthAuthenticator auth = new OAuthAuthenticator(consumer);
			RESTResult result = client.requestWithBody(RequestMethod.GET,
					"https://api.foursquare.com/v1/friends.json", auth, null);
			try {
				System.out.print(result.toString());
				JSONObject jsonObj;

				jsonObj = new JSONObject(result.getResponseBody());

				JSONArray friends = jsonObj.getJSONArray("friends");
				for (int i = 0; i < friends.length(); i++) {
					JSONObject userObj = friends.getJSONObject(i);
					String json = userObj.toString();
					Gson gson = new Gson();
					FoursquareUser user = gson.fromJson(json,
							FoursquareUser.class);
					users.add(user);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			log.info("Consumer was NULL");
		}

		return users;
	}
}
