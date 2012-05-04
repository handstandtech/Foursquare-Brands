package com.handstandtech.brandfinder.server.googleanalytics;
/* Copyright (c) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gdata.client.analytics.AnalyticsService;
import com.google.gdata.client.analytics.DataQuery;
import com.google.gdata.data.analytics.DataEntry;
import com.google.gdata.data.analytics.DataFeed;
import com.google.gdata.util.ServiceException;

/**
 * Demonstrates how to use the Google Data API's Java client library to access
 * Google Analytics data.
 * 
 * 
 */
public class GoogleAnalyticsClient {
	public static final String DATA_URL = "https://www.google.com/analytics/feeds/data";
	
	private String username;
	private String password;

	public GoogleAnalyticsClient(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public List<SingleEventSummaryAnalyticsResult> getEventData(String accountId, String startDate, String endDate) throws ServiceException, IOException {

		AnalyticsService myService = new AnalyticsService("foursquarebrands-analytics");

		// Authenticate using ClientLogin
		myService.setUserCredentials(username, password);

		// Ask Analytics to return the data sorted in descending order of visits
		// https://www.google.com/analytics/feeds/data?ids=ga:40242457&dimensions=ga:eventCategory,ga:eventAction,ga:eventLabel&metrics=ga:pageviews&sort=-ga:pageviews&start-date=2010-12-06&end-date=2010-12-20&max-results=10000
		// DataQuery sortedQuery = getBasicQuery(tableId);
		DataQuery query = new DataQuery(new URL(DATA_URL));
		query.setIds(accountId);
		query.setDimensions("ga:eventCategory,ga:eventAction,ga:eventLabel");
		query.setMetrics("ga:eventValue,ga:totalEvents,ga:uniqueEvents");
		query.setSort("-ga:totalEvents");
		query.setStartDate(startDate);
		query.setEndDate(endDate);
		query.setMaxResults(1000);
		DataFeed dataFeed = myService.getFeed(query, DataFeed.class);

		Map<String, String> columns = new HashMap<String, String>();

		columns.put("ga:eventCategory", "Event Category");
		columns.put("ga:eventLabel", "Event Label");
		columns.put("ga:eventAction", "Event Action");
		columns.put("ga:totalEvents", "Total Events");
		columns.put("ga:uniqueEvents", "Unique Events");
		columns.put("ga:eventValue", "Event Value");

		List<SingleEventSummaryAnalyticsResult> eventAnalytics = new LinkedList<SingleEventSummaryAnalyticsResult>();
		for (DataEntry entry : dataFeed.getEntries()) {

			SingleEventSummaryAnalyticsResult anResult = new SingleEventSummaryAnalyticsResult();

			String eventCategory = entry.stringValueOf("ga:eventCategory");
			String eventLabel = entry.stringValueOf("ga:eventLabel");
			String eventAction = entry.stringValueOf("ga:eventAction");
			String totalEvents = entry.stringValueOf("ga:totalEvents");
			String uniqueEvents = entry.stringValueOf("ga:uniqueEvents");
			String eventValue = entry.stringValueOf("ga:eventValue");

			anResult.setEventCategory(eventCategory);
			anResult.setEventLabel(eventLabel);
			anResult.setEventAction(eventAction);
			anResult.setTotalEvents(totalEvents);
			anResult.setUniqueEvents(uniqueEvents);
			anResult.setEventValue(eventValue);

			eventAnalytics.add(anResult);
		}
		return eventAnalytics;
	}

}
