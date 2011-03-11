package com.handstandtech.brandfinder.server;

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
import com.handstandtech.foursquare.server.oauth.OAuthAuthenticator;
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

	// public static void main(String args[]) throws Exception {
	// ParseCSV parser = new ParseCSV();
	// List<FoursquareUser> users =
	// parser.parseBrandsCSV("/Users/ssaammee/Documents/eclipseworkspaces/handstandtechnologies/brandfinder/war/WEB-INF/backup/Brands.csv");
	// log.info("users: " + users);
	//
	// // BasicAuthenticator auth = new BasicAuthenticator("ssaammee@gmail.com",
	// "wakefield");
	// // parser.findFriendByName("instyle", auth);
	//
	// // OAuthConsumer consumer = new
	// // DefaultOAuthConsumer(FoursquareConstants.LOCALHOST8888_CONSUMER_KEY,
	// // FoursquareConstants.LOCALHOST8888_CONSUMER_SECRET);
	// //
	// consumer.setTokenWithSecret("AKUTT1GGBMLDUCVR02RWS54CBVBYBHWVHNEGNE3BBZMFGGVH",
	// // "WHIGPSP1P5EGKDG32M1RSXFKRDVSDV2W2A12LYQIQTTWDKDK");
	// // Map<String, FoursquareUser> brands = parser
	// //
	// .read("/Users/ssaammee/Documents/eclipseworkspaces/handstandtechnologies/brandfinder/war/uidToJSON.txt");
	// // List<FoursquareUser> friends = parser.getCurrentFriends(consumer);
	// // List<FoursquareUser> followed = parser.getFollowed(friends, brands);
	// // List<FoursquareUser> unfollowed = parser.getUnfollowed(followed,
	// // brands);
	// //
	// // log.info("brands: " + brands.size());
	// // log.info("followed: " + followed.size());
	// // log.info("unfollowed: " + unfollowed.size());
	//
	//
	// }

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

			int i=0;
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

//	public Map<String, FoursquareUser> read(String path) throws Exception {
//		Map<String, FoursquareUser> users = new HashMap<String, FoursquareUser>();
//
//		InputStream is = new FileInputStream(path);
//		InputStreamReader isReader = new InputStreamReader(is);
//		CSVReader reader = new CSVReader(isReader, '\n');
//
//		String[] line = reader.readNext();
//		while (line != null) {
//			String lineData = line[0];
//			String[] split = lineData.split(",", 2);
//
//			String uid = split[0];
//			String json = split[1];
//
//			Gson gson = new Gson();
//			FoursquareUser foursquareUser = gson.fromJson(json,
//					FoursquareUser.class);
//			users.put(foursquareUser.getId().toString(), foursquareUser);
//
//			line = reader.readNext();
//		}
//
//		return users;
//	}

	// private void go() throws Exception {
	// InputStream is = new
	// FileInputStream("/Users/ssaammee/Desktop/foursquare/uids.txt");
	// InputStreamReader isReader = new InputStreamReader(is);
	// CSVReader reader = new CSVReader(isReader);
	//
	// String[] line = reader.readNext();
	// while (line != null) {
	// String uid = line[0];
	// // log.info(uid);
	// RESTResult result = findFriendByName(uid);
	// line = reader.readNext();
	// }
	// }

//	private RESTResult findFriendByName(String uid, Authenticator auth) {
//
//		RESTClientImpl client = new RESTClientImpl();
//
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("q", uid);
//		String urlString = RESTUtil
//				.createParamString(
//						"https://api.foursquare.com/v1/findfriends/byname.json",
//						params);
//		RESTResult result = client.requestWithBody(RequestMethod.GET,
//				urlString, auth, null);
//
//		log.info(result.toString());
//
//		System.out.print("\n");
//		return result;
//	}
}
