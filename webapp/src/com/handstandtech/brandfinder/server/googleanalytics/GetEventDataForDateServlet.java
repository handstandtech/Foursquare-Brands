package com.handstandtech.brandfinder.server.googleanalytics;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVWriter;

import com.google.visualization.datasource.datatable.DataTable;
import com.handstandtech.brandfinder.server.dao.DAO;
import com.handstandtech.brandfinder.server.dao.impl.CachingDAOImpl;
import com.handstandtech.brandfinder.shared.model.DailyFollowEventCount;
import com.handstandtech.brandfinder.shared.util.ModelUtils;
import com.handstandtech.googleanalytics.ReportRunner;
import com.handstandtech.googleanalytics.model.SingleEventSummaryAnalyticsResult;

public class GetEventDataForDateServlet extends HttpServlet {

	/**
	 * Default Serialization UID
	 */
	private static final long serialVersionUID = 1L;

	protected final Logger log = LoggerFactory.getLogger(getClass().getName());

	/**
	 * Handle a GET Request and serve the appropriate {@link DataTable}
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		String dateString = request.getParameter("date");// "2010-12-19";

		try {
			SimpleDateFormat dateFormat = ModelUtils.getDateFormat();

			Date date = dateFormat.parse(dateString);

			DAO dao = new CachingDAOImpl();

			ReportRunner runner = new ReportRunner();
			List<SingleEventSummaryAnalyticsResult> analytics = runner
					.getAnalytics(dateString);

			//Creating a map so we can easily add in followed and unfollowed numbers
			Map<String, DailyFollowEventCount> dailyCountMap = new HashMap<String, DailyFollowEventCount>();

			for (SingleEventSummaryAnalyticsResult result : analytics) {
				String id = result.getEventLabel();

				DailyFollowEventCount dailyCount = dailyCountMap.get(id);

				if (dailyCount == null) {
					dailyCount = new DailyFollowEventCount();
					dailyCount.setId(ModelUtils.generateId(id, date));
					dailyCount.setDate(date);
					dailyCount.setFoursquareId(id);
				}

				String totalEvents = result.getTotalEvents();
				String uniqueEvents = result.getUniqueEvents();

				Long total = Long.parseLong(totalEvents);
				Long unique = Long.parseLong(uniqueEvents);

				String action = result.getEventAction();
				if (action.equalsIgnoreCase("followed")) {
					dailyCount.setTotalFollowCount(total);
					dailyCount.setUniqueFollowCount(unique);
				} else if (action.equalsIgnoreCase("unfollowed")) {
					dailyCount.setTotalUnFollowCount(total);
					dailyCount.setUniqueUnFollowCount(unique);
				}

				dailyCountMap.put(id, dailyCount);

			}

			Collection<DailyFollowEventCount> dailyCounts = dailyCountMap
					.values();
			dao.updateDailyFollowEventCounts(dailyCounts);
			// Write the Results to the Output
			response.getWriter().println(writeCSV(dailyCounts));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.getWriter().println("Error: " + e.getMessage());
		}
	}

	public String writeCSV(Collection<DailyFollowEventCount> dailyCounts) {
		StringWriter mega = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(mega, CSVWriter.DEFAULT_SEPARATOR,
				CSVWriter.NO_QUOTE_CHARACTER);
		String[] columns = new String[] { "id", "totalFollow", "uniqueFollow",
				"totalUnFollow", "uniqueUnFollow" };
		csvWriter.writeNext(columns);
		for (DailyFollowEventCount currCount : dailyCounts) {
			List<String> toWrite = new LinkedList<String>();
			toWrite.add(currCount.getFoursquareId());
			toWrite.add(currCount.getTotalFollowCount().toString());
			toWrite.add(currCount.getUniqueFollowCount().toString());
			toWrite.add(currCount.getTotalUnFollowCount().toString());
			toWrite.add(currCount.getUniqueUnFollowCount().toString());

			String[] values = new String[toWrite.size()];
			toWrite.toArray(values);

			csvWriter.writeNext(values);
		}
		return mega.toString();
	}
}
