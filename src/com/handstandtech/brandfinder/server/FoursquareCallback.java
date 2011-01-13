package com.handstandtech.brandfinder.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import com.handstandtech.brandfinder.server.util.SessionHelper;
import com.handstandtech.foursquare.server.FoursquareConstants;
import com.handstandtech.foursquare.server.FoursquareHelper;
import com.handstandtech.foursquare.server.oauth.FoursquareLogin;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;

/**
 * An OAuth callback handler.
 * 
 * @author Sam Edwards
 */
public class FoursquareCallback extends HttpServlet {

	public static final String PATH = "/foursquare/callback";

	protected final Logger log = Logger.getLogger(getClass().getName());

	/**
	 * Exchange an OAuth request token for an access token, and store the latter
	 * in cookies.
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		HttpSession session = request.getSession();
		OAuthConsumer sessionConsumer = (OAuthConsumer) session.getAttribute(FoursquareConstants.CONSUMER_CONSTANT);

		// If we already have a consumer, no need to login again... redirect
		// back home
		if (sessionConsumer != null) {
			redirectToFoursquareApp(response);
		}

		try {
			String oauth_verifier = request.getParameter("oauth_verifier");
			String token = (String) session.getAttribute(FoursquareConstants.TOKEN_CONSTANT);
			String tokenSecret = (String) session.getAttribute(FoursquareConstants.TOKEN_SECRET_CONSTANT);
			OAuthConsumer consumer = FoursquareLogin.getFoursquareOAuthConsumer(request);// the
																							// same
																							// constructor
																							// as
																							// above...
			consumer.setTokenWithSecret(token, tokenSecret);
			OAuthProvider provider = FoursquareLogin.getFoursquareOAuthProvider();// the
																					// same
																					// constructor
																					// as
																					// above...

			System.out.println("oauth_verifier: " + oauth_verifier);
			System.out.println("token: " + token);
			System.out.println("tokenSecret: " + tokenSecret);
			provider.setOAuth10a(true);
			provider.retrieveAccessToken(consumer, oauth_verifier);
			System.out.println("--------------------");
			System.out.println("Cosumer Key: "+consumer.getConsumerKey());
			System.out.println("Cosumer Secret: "+consumer.getConsumerSecret());
			System.out.println("Token: "+consumer.getToken());
			System.out.println("Token Secret: "+consumer.getTokenSecret());
			session.setAttribute(FoursquareConstants.CONSUMER_CONSTANT, consumer);
			
			FoursquareHelper helper = new FoursquareHelper(consumer.getTokenSecret());
			FoursquareUser user = helper.getUserInfo("self");
			DAO dao = new DAO();
			dao.updateFoursquareUser(user);
			SessionHelper.setCurrentUser(session, user);
			redirectToFoursquareApp(response);
		} catch (OAuthMessageSignerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthNotAuthorizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void redirectToFoursquareApp(HttpServletResponse response) {
		try {
			response.sendRedirect("/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static final long serialVersionUID = 1L;

}
