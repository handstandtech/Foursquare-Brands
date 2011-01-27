///*
// * Copyright 2007 Netflix, Inc.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.socialusage.server.servlet.callback;
//
//import java.io.IOException;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//
//import oauth.signpost.OAuthConsumer;
//import oauth.signpost.OAuthProvider;
//
//import org.apache.log4j.Logger;
//
//import com.google.inject.Singleton;
//import com.handstandtech.twitter.server.TwitterConstants;
//import com.socialusage.server.model.SocialUsageUser;
//import com.socialusage.server.model.SocialUsageUser.TwitterInfo;
//import com.socialusage.server.util.SocialUsageSessionHelper;
//import com.socialusage.server.util.SocialUsageURL;
//
///**
// * A trivial consumer of the 'friends_timeline' service at Twitter.
// * 
// * @author Sam Edwards
// */
//@Singleton
//public class TwitterOAuth10aLogin extends HttpServlet {
//
//	private static Logger log = Logger.getLogger(TwitterOAuth10aLogin.class);
//	private static final long serialVersionUID = 1L;
//
//	@Override
//	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//		HttpSession session = request.getSession();
//
//		OAuthConsumer sessionConsumer = null;
//		SocialUsageUser user = SocialUsageSessionHelper.getCurrentUser(session);
//
//		if (user != null) {
//			TwitterInfo twitter = user.getTwitter();
//			if (twitter == null) {
//				try {
//					OAuthConsumer consumer = TwitterOAuth10aHelpers.getTwitterOAuthConsumer(request);
//					OAuthProvider provider = TwitterOAuth10aHelpers.getTwitterOAuthProvider();
//					String authUrl = provider.retrieveRequestToken(consumer, "");
//
//					log.debug("Token: " + consumer.getToken());
//					log.debug("Token Secret: " + consumer.getTokenSecret());
//
//					session.setAttribute(TwitterConstants.TOKEN_CONSTANT, consumer.getToken());
//					session.setAttribute(TwitterConstants.TOKEN_SECRET_CONSTANT, consumer.getTokenSecret());
//
//					// Send to FourSquare for Authorization
//					response.sendRedirect(authUrl);
//
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//
//			} else {
//				// Already Logged In, Redirect to App
//				response.sendRedirect(SocialUsageURL.HOME_URL);
//			}
//		}
//
//	}
//}
