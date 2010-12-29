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

import au.com.bytecode.opencsv.CSVReader;

import com.google.gson.Gson;
import com.handstandtech.foursquare.server.oauth.OAuthAuthenticator;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser.Contact;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser.Friends;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser.Mayorships;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser.Todos;
import com.handstandtech.server.rest.Authenticator;
import com.handstandtech.server.rest.BasicAuthenticator;
import com.handstandtech.server.rest.RESTClientImpl;
import com.handstandtech.server.rest.RESTUtil;
import com.handstandtech.shared.model.rest.RESTResult;
import com.handstandtech.shared.model.rest.RequestMethod;

public class ParseCSV {

	public ParseCSV() {

	}

//	public static void main(String args[]) throws Exception {
//		ParseCSV parser = new ParseCSV();
//		List<FoursquareUser> users = parser.parseBrandsCSV("/Users/ssaammee/Documents/eclipseworkspaces/handstandtechnologies/brandfinder/war/WEB-INF/backup/Brands.csv");
//		System.out.println("users: " + users);
//
////		BasicAuthenticator auth = new BasicAuthenticator("ssaammee@gmail.com", "wakefield");
////		parser.findFriendByName("instyle", auth);
//
//		// OAuthConsumer consumer = new
//		// DefaultOAuthConsumer(FoursquareConstants.LOCALHOST8888_CONSUMER_KEY,
//		// FoursquareConstants.LOCALHOST8888_CONSUMER_SECRET);
//		// consumer.setTokenWithSecret("AKUTT1GGBMLDUCVR02RWS54CBVBYBHWVHNEGNE3BBZMFGGVH",
//		// "WHIGPSP1P5EGKDG32M1RSXFKRDVSDV2W2A12LYQIQTTWDKDK");
//		// Map<String, FoursquareUser> brands = parser
//		// .read("/Users/ssaammee/Documents/eclipseworkspaces/handstandtechnologies/brandfinder/war/uidToJSON.txt");
//		// List<FoursquareUser> friends = parser.getCurrentFriends(consumer);
//		// List<FoursquareUser> followed = parser.getFollowed(friends, brands);
//		// List<FoursquareUser> unfollowed = parser.getUnfollowed(followed,
//		// brands);
//		//
//		// System.out.println("brands: " + brands.size());
//		// System.out.println("followed: " + followed.size());
//		// System.out.println("unfollowed: " + unfollowed.size());
//
//		
//	}

//	public List<FoursquareUser> parseBrandsCSV(String path) throws Exception {
//		List<FoursquareUser> users = new ArrayList<FoursquareUser>();
//
//		InputStream is = new FileInputStream(path);
//		InputStreamReader isReader = new InputStreamReader(is);
//		CSVReader reader = new CSVReader(isReader);
//
//		String[] line = reader.readNext();
//		
//		//Skip first line
//		line = reader.readNext();
//		
//		while (line != null) {
//			// todos.count,photo,firstName,lastUpdate,lastName,contact.facebook,contact.phone,homeCity,type,friends.count
//			// ,key,gender,mayorships.count,contact.email,contact.twitter
//			FoursquareUser user = new FoursquareUser();
//
//			Long todosCount = Long.parseLong(line[0]);
//			String photo = line[1];
//			String firstName = line[2];
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//			Date lastUpdate = sdf.parse(line[3]);
//			String lastName = line[4];
//			String contactFacebook = line[5];
//			String contactPhone = line[6];
//			String homeCity = line[7];
//			String type = line[8];
//			Long friendsCount = Long.parseLong(line[9]);
//			String key = line[10];
//			String gender = line[11];
//			Long mayorshipsCount = Long.parseLong(line[12]);
//			String contactEmail = line[13];
//			String contactTwitter = line[14];
//
//			Todos todos = new Todos();
//			todos.setCount(todosCount);
//			user.setTodos(todos);
//			user.setPhoto(photo);
//			user.setFirstName(firstName);
//			user.setLastUpdate(lastUpdate);
//			user.setLastName(lastName);
//			user.setHomeCity(homeCity);
//			user.setType(type);
//			user.setId(key);
//			user.setGender(gender);
//
//			Mayorships mayorships = new Mayorships();
//			mayorships.setCount(mayorshipsCount);
//
//			Friends friends = new Friends();
//			friends.setCount(friendsCount);
//			user.setFriends(friends);
//
//			Contact contact = new Contact();
//			contact.setFacebook(contactFacebook);
//			contact.setPhone(contactPhone);
//			contact.setTwitter(contactTwitter);
//			contact.setTwitter(contactEmail);
//			user.setContact(contact);
//
//			users.add(user);
//			line = reader.readNext();
//		}
//
//		return users;
//	}

	public List<FoursquareUser> getFollowed(List<FoursquareUser> friends, Map<String, FoursquareUser> brands) {
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

	public List<FoursquareUser> getUnfollowed(List<FoursquareUser> followed, Map<String, FoursquareUser> brands) {
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
			RESTClientImpl client = new RESTClientImpl();

			OAuthAuthenticator auth = new OAuthAuthenticator(consumer);
			RESTResult result = client.requestWithBody(RequestMethod.GET, "https://api.foursquare.com/v1/friends.json",
					auth, null);
			try {
				System.out.print(result.toString());
				JSONObject jsonObj;

				jsonObj = new JSONObject(result.getResponseBody());

				JSONArray friends = jsonObj.getJSONArray("friends");
				for (int i = 0; i < friends.length(); i++) {
					JSONObject userObj = friends.getJSONObject(i);
					String json = userObj.toString();
					Gson gson = new Gson();
					FoursquareUser user = gson.fromJson(json, FoursquareUser.class);
					users.add(user);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("Consumer was NULL");
		}

		return users;
	}

	public Map<String, FoursquareUser> read(String path) throws Exception {
		Map<String, FoursquareUser> users = new HashMap<String, FoursquareUser>();

		InputStream is = new FileInputStream(path);
		InputStreamReader isReader = new InputStreamReader(is);
		CSVReader reader = new CSVReader(isReader, '\n');

		String[] line = reader.readNext();
		while (line != null) {
			String lineData = line[0];
			String[] split = lineData.split(",", 2);

			String uid = split[0];
			String json = split[1];

			Gson gson = new Gson();
			FoursquareUser foursquareUser = gson.fromJson(json, FoursquareUser.class);
			users.put(foursquareUser.getId().toString(), foursquareUser);

			line = reader.readNext();
		}

		return users;
	}

	// private void go() throws Exception {
	// InputStream is = new
	// FileInputStream("/Users/ssaammee/Desktop/foursquare/uids.txt");
	// InputStreamReader isReader = new InputStreamReader(is);
	// CSVReader reader = new CSVReader(isReader);
	//
	// String[] line = reader.readNext();
	// while (line != null) {
	// String uid = line[0];
	// // System.out.println(uid);
	// RESTResult result = findFriendByName(uid);
	// line = reader.readNext();
	// }
	// }

	private RESTResult findFriendByName(String uid, Authenticator auth) {

		RESTClientImpl client = new RESTClientImpl();

		Map<String, String> params = new HashMap<String, String>();
		params.put("q", uid);
		String urlString = RESTUtil.createParamString("https://api.foursquare.com/v1/findfriends/byname.json", params);
		RESTResult result = client.requestWithBody(RequestMethod.GET, urlString, auth, null);

		System.out.println(result.toString());

		System.out.print("\n");
		return result;
	}
}
