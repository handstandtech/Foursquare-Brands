//package com.handstandtech.brandfinder.server.twitter;
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
//import oauth.signpost.basic.DefaultOAuthConsumer;
//import oauth.signpost.exception.OAuthCommunicationException;
//import oauth.signpost.exception.OAuthExpectationFailedException;
//import oauth.signpost.exception.OAuthMessageSignerException;
//import oauth.signpost.exception.OAuthNotAuthorizedException;
//import oauth.signpost.http.HttpParameters;
//
//import org.apache.log4j.Logger;
//
//import com.handstandtech.brandfinder.server.DAO;
//import com.handstandtech.server.SessionConstants;
//
///**
// * An OAuth callback handler.
// * 
// * @author Sam Edwards
// */
//public class TwitterOAuth10aCallback extends HttpServlet {
//	
//	private static Logger log = Logger.getLogger(TwitterOAuth10aCallback.class);
//
//	private static final long serialVersionUID = 1L;
//
//	public static final String PATH = "/twitter/callback";
//
//	
//	private static DAO dao = new DAO();
//
//	/**
//	 * Exchange an OAuth request token for an access token, and store the latter
//	 * in cookies.
//	 */
//	@Override
//	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//		HttpSession session = request.getSession();
//
//		OAuthConsumer sessionConsumer = (OAuthConsumer) session.getAttribute(TwitterConstants.CONSUMER_CONSTANT);
//
//		// If we already have a consumer, no need to login again... redirect
//		// back home
//		if (sessionConsumer != null) {
//			redirectToTwitterApp(response);
//		}
//
//		try {
//			String oauth_verifier = request.getParameter("oauth_verifier");
//			String token = (String) session.getAttribute(TwitterConstants.TOKEN_CONSTANT);
//			String tokenSecret = (String) session.getAttribute(TwitterConstants.TOKEN_SECRET_CONSTANT);
//			
//			DefaultOAuthConsumer consumer = TwitterOAuth10aHelpers.getTwitterOAuthConsumer(request);// the
//			consumer.setTokenWithSecret(token, tokenSecret);
//			
//			OAuthProvider provider = TwitterOAuth10aHelpers.getTwitterOAuthProvider();// the
//			provider.setOAuth10a(true);
//			provider.retrieveAccessToken(consumer, oauth_verifier);
//
//
//			// Update Twitter User Info
//			TwitterHelper twitterHelper = new TwitterHelper(consumer);
//			TwitterUser twitterUser = twitterHelper.getCurrentUserInfo();
//			dao.updateTwitterUser(twitterUser);
//			
//			//---------------------
//			String consumerKey = consumer.getConsumerKey();
//			String consumerSecret = consumer.getConsumerSecret();
//			token = consumer.getToken();
//			tokenSecret = consumer.getTokenSecret();
//			HttpParameters requestParams = consumer.getRequestParameters();
//
//			log.debug("CONSUMER_KEY: " + consumerKey);
//			log.debug("CONSUMER_SECRET: " + consumerSecret);
//			log.debug("TOKEN: " + token);
//			log.debug("TOKEN_SECRET: " + tokenSecret);
//			log.debug("REQUEST_PARAMS: " + requestParams.size());
//			//---------------------
//			
//			
//			session.setAttribute(SessionConstants.CURRENT_TWITTER_USER, twitterUser);
//			session.setAttribute(TwitterConstants.CONSUMER_CONSTANT, consumer);
//
//			// Persist the Consumer with the User Object
//
//			// --------------------
//			// Print out the Consumer
//			SocialUsageUser user = SocialUsageSessionHelper.getCurrentUser(session);
//			log.debug("Currently Logged In User Key: " + user);
//			
//			if (user != null) {
//				log.debug("Current User: " + user.getEmail());
//				
//				TwitterInfo twitter= new TwitterInfo();
//				twitter.setConsumer(consumer);
//				twitter.setName(twitterUser.getScreen_name());
//				twitter.setUid(twitterUser.getId().toString());
//				user.setTwitter(twitter);
//				dao.updateUser(user);
//			} else {
//				System.err.println("Current User was NULL");
//			}
//			// -----------------
//
//			redirectToTwitterApp(response);
//		} catch (OAuthMessageSignerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (OAuthExpectationFailedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (OAuthCommunicationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (OAuthNotAuthorizedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	private void redirectToTwitterApp(HttpServletResponse response) {
//		try {
//			response.sendRedirect(SocialUsageURL.ACCOUNT_URL);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//}
