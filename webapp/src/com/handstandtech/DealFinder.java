package com.handstandtech;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.handstandtech.foursquare.v2.impl.FoursquareAPIv2Impl;
import com.handstandtech.server.rest.RESTUtil;
import com.handstandtech.server.rest.impl.RESTClientJavaNetImpl;
import com.handstandtech.shared.model.rest.RESTResult;
import com.handstandtech.shared.model.rest.RequestMethod;
import com.handstandtech.shared.util.math.GreatCircleDistanceCalculator;
import com.handstandtech.shared.util.math.LatLon;

public class DealFinder {
	
	private static final Logger log = LoggerFactory.getLogger(DealFinder.class);

	public static void main(String args[]) throws UnsupportedEncodingException,
			FileNotFoundException {
		PrintWriter specialsOut = new PrintWriter("war/specials.js");
		PrintWriter pointsOut = new PrintWriter("war/points.js");
		List<String> requestURIs = new ArrayList<String>();
		// requestURIs.add(createRequest1URI());

		double diameterInKm = .5;
		double radiusInKm = diameterInKm / 2;
		double columnCount = 12;
		double rowCount = 12;
		LatLon center = new LatLon(38.990437, -77.0224);
		List<LatLon> points = new ArrayList<LatLon>();

		LatLon topLeft = findTopLeft(center, columnCount, rowCount, radiusInKm);

		// Get Points
		points.addAll(createPointsInGrid(topLeft, columnCount, rowCount,
				radiusInKm));

		// Get Offset
		// Move to right
		LatLon newTopLeft = GreatCircleDistanceCalculator.createEndPoint(
				topLeft, 90.0, radiusInKm);
		// Move down
		newTopLeft = GreatCircleDistanceCalculator.createEndPoint(newTopLeft,
				180.0, radiusInKm);

		// Get Rest of Points
		points.addAll(createPointsInGrid(newTopLeft, columnCount - 1,
				rowCount - 1, radiusInKm));

		specialsOut.println("var specials = [");
		pointsOut.println("var points = [");
		for (LatLon point : points) {
			pointsOut.println("{latitude: " + point.getLatitude()
					+ ", longitude: " + point.getLongitude() + "},");
			if (requestURIs.size() < 5) {
				requestURIs.add(createSpecialsURI(point));
			} else {
				callMultiForURIs(requestURIs, specialsOut);
			}
		}

		// If there are any left, handle them.
		if (requestURIs.size() > 0) {
			callMultiForURIs(requestURIs, specialsOut);
		}

		specialsOut.println("];");
		pointsOut.println("];");
		pointsOut.close();
		specialsOut.close();

		if (points.size() > 2) {
			String googleMapsUrl = "http://maps.google.com/maps?saddr="
					+ latLonPair(points.get(0)) + "&daddr="
					+ latLonPair(points.get(1));
			for (int i = 2; i < points.size(); i++) {
				googleMapsUrl = googleMapsUrl + "+to:"
						+ latLonPair(points.get(i));
			}
			log.info(googleMapsUrl);
		}

	}

	private static LatLon findTopLeft(LatLon center, double columnCount,
			double rowCount, double radiusInKm) {
		LatLon topLeft = GreatCircleDistanceCalculator.createEndPoint(center,
				0.0, radiusInKm * (rowCount - 1));
		topLeft = GreatCircleDistanceCalculator.createEndPoint(topLeft, 270.0,
				radiusInKm * (columnCount - 1));
		return topLeft;
	}

	private static List<LatLon> createPointsInGrid(LatLon topLeft,
			double columnCount, double rowCount, double radiusInKm) {
		List<LatLon> points = new ArrayList<LatLon>();

		LatLon currPoint = topLeft;
		for (int i = 0; i < rowCount; i++) {
			LatLon newPoint = currPoint;
			for (int j = 0; j < columnCount; j++) {
				points.add(newPoint);
				newPoint = GreatCircleDistanceCalculator.createEndPoint(
						newPoint, 90.0, radiusInKm * 2);
			}
			currPoint = GreatCircleDistanceCalculator.createEndPoint(currPoint,
					180.0, radiusInKm * 2);
		}

		return points;
	}

	public static String latLonPair(LatLon latlon) {
		return latlon.getLatitude() + "," + latlon.getLongitude();
	}

	public static double THREE_SIXTY = 360.0;

	private static List<LatLon> createPointsInCircle(LatLon start,
			double individualSearchRadius, double distanceInKm, double numPoints) {
		List<LatLon> points = new ArrayList<LatLon>();
		double increment = (THREE_SIXTY / numPoints);
		double max = THREE_SIXTY + (increment / 2);
		double begin = increment / 2;

		for (double heading = begin; heading < max; heading = heading
				+ increment) {
			LatLon endPoint = GreatCircleDistanceCalculator.createEndPoint(
					start, heading, distanceInKm);
			points.add(endPoint);
		}
		return points;
	}

	private static String createSpecialsURI(LatLon latlon)
			throws UnsupportedEncodingException {
		Map<String, String> request1Params = new HashMap<String, String>();
		String request1Endpoint = "/specials/search";
		request1Params.put(
				"ll",
				RESTUtil.encode(latlon.getLatitude() + ","
						+ latlon.getLongitude()));
		request1Params.put("limit", RESTUtil.encode("50"));
		return RESTUtil.createFullUrl(request1Endpoint, request1Params);
	}

	public static void callMultiForURIs(List<String> requestURIs,
			PrintWriter specialsOut) throws UnsupportedEncodingException {
		String requestsString = FoursquareAPIv2Impl.createCSVLine(requestURIs);
		Map<String, String> params = new HashMap<String, String>();

		params.put("oauth_token",
				"4FOC2MCOPFUEY211YUJJ3ZEMFEJAMVQACPZJNSGVDN5MJCF3");
		params.put("requests", RESTUtil.encode(requestsString));
		String endpoint = "https://api.foursquare.com/v2/multi";

		String url = RESTUtil.createFullUrl(endpoint, params);

		RESTClientJavaNetImpl client = new RESTClientJavaNetImpl();
		RESTResult result = client.request(RequestMethod.GET, url);

		try {
			JSONObject obj = new JSONObject(result.getResponseBody());
			JSONArray responses = obj.getJSONObject("response").getJSONArray(
					"responses");
			for (int i = 0; i < responses.length(); i++) {
				JSONObject firstResponse = responses.getJSONObject(i);
				JSONObject response = firstResponse.getJSONObject("response");
				JSONObject specials = response.getJSONObject("specials");
				double specialsCount = specials.getDouble("count");
				JSONArray items = specials.getJSONArray("items");
				if (specialsCount > 0) {
					for (int j = 0; j < specialsCount; j++) {
						JSONObject item = items.getJSONObject(j);
						log.info(item.toString());
						if (specialsOut != null) {
							specialsOut.println(item.toString() + ",");
						}
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		requestURIs.clear();
	}

}
