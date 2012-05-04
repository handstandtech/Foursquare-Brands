package com.handstandtech.brandfinder.server.googleanalytics;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVWriter;

import com.google.gdata.util.ServiceException;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;

public class ReportRunner {
	private static Logger log = LoggerFactory.getLogger(ReportRunner.class);

	private void processAnalytics(Map<String, FoursquareUser> users,
			List<SingleEventSummaryAnalyticsResult> analytics) {
		if (analytics != null) {

			StringWriter mega = new StringWriter();
			CSVWriter csvWriter = new CSVWriter(mega,
					CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);
			String[] columns = new String[] { "id", "name", "category",
					"action", "label", "totalEvents", "uniqueEvents",
					"eventValue" };
			csvWriter.writeNext(columns);
			for (SingleEventSummaryAnalyticsResult result : analytics) {
				FoursquareUser currentBrand = users.get(result.getEventLabel());
				if (currentBrand != null
						&& currentBrand.getType().equals("brand")) {
					String lastName = currentBrand.getLastName();

					List<String> toWrite = new LinkedList<String>();
					toWrite.add(currentBrand.getId());
					String name = currentBrand.getFirstName();
					if (lastName != null && !lastName.isEmpty()) {
						name = name + " " + lastName;
					}
					toWrite.add(name);
					toWrite.add(result.getEventCategory());
					toWrite.add(result.getEventAction());
					toWrite.add(result.getEventLabel());
					toWrite.add(result.getTotalEvents());
					toWrite.add(result.getUniqueEvents());
					toWrite.add(result.getEventValue());

					String[] values = new String[toWrite.size()];
					toWrite.toArray(values);

					csvWriter.writeNext(values);
				}
			}
			log.info(mega.toString());
		}

	}

	public List<SingleEventSummaryAnalyticsResult> sortAnalytics(
			List<SingleEventSummaryAnalyticsResult> analytics) {
		Collections.sort(analytics,
				new Comparator<SingleEventSummaryAnalyticsResult>() {

					@Override
					public int compare(SingleEventSummaryAnalyticsResult o1,
							SingleEventSummaryAnalyticsResult o2) {
						Long total1 = Long.parseLong(o1.getTotalEvents());
						Long total2 = Long.parseLong(o2.getTotalEvents());
						return total2.compareTo(total1);
					}
				});
		return analytics;
	}

	public List<SingleEventSummaryAnalyticsResult> getAnalytics(
			String dateStr) {
		List<SingleEventSummaryAnalyticsResult> analytics = new LinkedList<SingleEventSummaryAnalyticsResult>();

		GoogleAnalyticsClient client = new GoogleAnalyticsClient("ssaammee",
				"b3adv3nturous");
		String foursquareBrandsId = "ga:40242457";
		try {
			analytics = client.getEventData(foursquareBrandsId, dateStr,
					dateStr);
		} catch (ServiceException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return analytics;
	}

}
