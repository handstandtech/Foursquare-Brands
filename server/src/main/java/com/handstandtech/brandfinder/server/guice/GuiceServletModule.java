package com.handstandtech.brandfinder.server.guice;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.ObjectifyFactory;
import com.handstandtech.brandfinder.server.action.FollowServlet;
import com.handstandtech.brandfinder.server.action.LogoutServlet;
import com.handstandtech.brandfinder.server.action.UnFollowServlet;
import com.handstandtech.brandfinder.server.controller.DevImportController;
import com.handstandtech.brandfinder.server.controller.RootController;
import com.handstandtech.brandfinder.server.cron.DailyCronServlet;
import com.handstandtech.brandfinder.server.cron.HourlyCronServlet;
import com.handstandtech.brandfinder.server.cron.TweetServlet;
import com.handstandtech.brandfinder.server.filter.UrlRewriteSingletonFilter;
import com.handstandtech.brandfinder.server.filters.LoggedInFilter;
import com.handstandtech.brandfinder.server.foursquare.FoursquareCallback;
import com.handstandtech.brandfinder.server.foursquare.MyFoursquareOAuth2Login;
import com.handstandtech.brandfinder.server.googleanalytics.GetEventDataForDateServlet;
import com.handstandtech.brandfinder.server.servlet.RemoteApiSingletonServlet;
import com.handstandtech.brandfinder.server.tasks.FollowerCountTaskServlet;

/**
 * @author Sam Edwards
 */
public class GuiceServletModule extends ServletModule {

	@Override
	public void configureServlets() {

		// Model object managers
		bind(ObjectifyFactory.class).in(Singleton.class);

		// // Filters
		notAssets().through(LoggedInFilter.class);
		notAssets().through(UrlRewriteSingletonFilter.class);
		
		ts("/").with(RootController.class);
		
		ts("/util/devimport").with(DevImportController.class);
		
		ts("/foursquare/callback").with(FoursquareCallback.class);
		ts("/login").with(MyFoursquareOAuth2Login.class);
		ts("/logout").with(LogoutServlet.class);
		ts("/follow").with(FollowServlet.class);
		ts("/unfollow").with(UnFollowServlet.class);

		ts("/cron/hourly").with(HourlyCronServlet.class);
		ts("/cron/daily").with(DailyCronServlet.class);

		ts("/tasks/tweet").with(TweetServlet.class);
		ts("/tasks/get-google-analytics")
				.with(GetEventDataForDateServlet.class);
		ts("/tasks/store-follower-count").with(FollowerCountTaskServlet.class);

		ts("/remote_api").with(RemoteApiSingletonServlet.class);

	}

	protected FilterKeyBindingBuilder notAssets() {
		return filterRegex("^(?!/assets/)(?!/images/)(?!/appstats/)(?!/favicon.ico)(.*)$");
	}

	protected ServletKeyBindingBuilder ts(String string) {
		return serveRegex(string + "/?$");
	}
}
