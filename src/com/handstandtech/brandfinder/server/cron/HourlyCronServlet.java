package com.handstandtech.brandfinder.server.cron;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.visualization.datasource.datatable.DataTable;
import com.handstandtech.brandfinder.server.CachingDAOImpl;
import com.handstandtech.brandfinder.server.DAO;
import com.handstandtech.brandfinder.server.tasks.FollowerCountTaskServlet;
import com.handstandtech.brandfinder.shared.model.BrandDiscovered;
import com.handstandtech.brandfinder.shared.model.User;
import com.handstandtech.brandfinder.shared.util.ModelUtils;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;
import com.handstandtech.foursquare.v2.FoursquareAPIv2;
import com.handstandtech.foursquare.v2.exception.FoursquareNot200Exception;
import com.handstandtech.foursquare.v2.impl.CachingFoursquareAPIv2Impl;

/**
 * Hourly Cron Servlet
 */
public class HourlyCronServlet extends HttpServlet {

	/**
	 * Default Serialization UID
	 */
	private static final long serialVersionUID = 1L;

	protected final Logger log = LoggerFactory.getLogger(getClass().getName());

	private static Long ONE_SECOND = 1000L;
	private static Long ONE_MINUTE = 60 * ONE_SECOND;
	private static Long ONE_HOUR = 60 * ONE_MINUTE;
	private static Long ONE_DAY = 24 * ONE_HOUR;

	/**
	 * Handle a GET Request and serve the appropriate {@link DataTable}
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Date date = new Date();

		// Update Analytics from Yesterday
		updateGoogleAnalyticsForYesterday(date);

		// Find Which New Brands were Added and Follow Them
		findNewBrandsAndFollow(date);
	}

	private void updateGoogleAnalyticsForYesterday(Date date) {
		Queue queue = QueueFactory.getDefaultQueue();
		TaskOptions googleAnalyticsTask = TaskOptions.Builder.withDefaults();
		googleAnalyticsTask.url("/tasks/get-google-analytics");
		googleAnalyticsTask.method(Method.GET);
		Date yesterday = new Date(date.getTime() - ONE_DAY);
		googleAnalyticsTask.param("date",
				ModelUtils.getDateFormat().format(yesterday));
		queue.add(googleAnalyticsTask);
	}

	private void findNewBrandsAndFollow(Date date)
			throws UnsupportedEncodingException {
		DAO dao = new CachingDAOImpl();

		// See if there are any new brands in the last day
		List<BrandDiscovered> brandsDiscovered = dao
				.getBrandDiscoveredSince(new Date(date.getTime() - ONE_HOUR));
		StringBuffer sb = new StringBuffer();
		for (BrandDiscovered b : brandsDiscovered) {
			String brandIdStr = b.getBrandId();
			FoursquareUser brandFound = dao.findFoursquareUser(brandIdStr);
			String discoveredByStr = b.getUserId();
			User byUser = dao.findUser(discoveredByStr);

			StringBuilder brandUrl = new StringBuilder();
			brandUrl.append("http://foursquarebrands.com/");
			if ("brand".equals(brandFound.getType())) {
				brandUrl.append("brands");
			} else {
				brandUrl.append("celebs");
			}
			brandUrl.append("/" + brandIdStr);

			sb.append("<li>[<strong>" + brandFound.getType()
					+ "</strong>] <a href='" + brandUrl + "'><h1>"
					+ brandFound.getName() + "</h1></a>");
			sb.append("<br/>");

			sb.append("Discovered By <a href='http://foursquare.com/user/"
					+ discoveredByStr + "'>"
					+ byUser.getFoursquareUser().getName() + "</a>");
			sb.append("<br/>at " + b.getDate() + "</li>\n");

			// If a brand, follow it.
			if (brandFound.getType().equals("brand")) {
				FoursquareAPIv2 handstandTechHelper = new CachingFoursquareAPIv2Impl(
						FollowerCountTaskServlet.OAUTH_TOKEN_FOURSQUAREBRANDS);
				try {
					handstandTechHelper.friendRequest(brandIdStr);
				} catch (FoursquareNot200Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		}

		if (sb.length() > 0) {
			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props, null);

			try {
				Message msg = new MimeMessage(session);
				msg.setFrom(new InternetAddress("apps@handstandtech.com",
						"FoursquareBrands.com"));

				List<String> adminEmails = new ArrayList<String>();
				adminEmails.add("sam@handstandtech.com");
				// adminEmails.add("kristen.sheriff@gmail.com");
				for (String adminEmail : adminEmails) {
					msg.addRecipient(Message.RecipientType.TO,
							new InternetAddress(adminEmail,
									"Foursquare Brands Administrator"));
				}
				msg.setSubject(brandsDiscovered.size()
						+ " New Foursquare Brands Found!");
				StringBuffer msgBody = new StringBuffer();
				msgBody.append("<h1>New Foursquare Brands Found:</h1>\n");
				msgBody.append("<ul>\n");
				msgBody.append(sb.toString());
				msgBody.append("</ul>\n");
				log.info(msgBody.toString());

				msg.setContent(msgBody.toString(), "text/html");
				Transport.send(msg);
			} catch (AddressException e) {
				// ...
			} catch (MessagingException e) {
				// ...
			}
		} else {
			log.info("No New Brands Found");
		}
	}
}
