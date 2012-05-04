package com.handstandtech.brandfinder.server.constants;

import com.handstandtech.server.auth.OAuthConsumerInfoManager.OAuthConsumerInfoSet;
import com.handstandtech.server.auth.ServerConstants;

public class FoursquareServerConstants extends ServerConstants {

	public static FoursquareServerConstants INSTANCE = new FoursquareServerConstants();

	public static final String LOGIN_REDIRECT_PAGE = "/";
	public static final String SELF = "self";
	private static final String FOURSQUARE_CALLBACK_URI = "/foursquare/callback";

	private FoursquareServerConstants() {
		log.info("Initializing Foursquare OAuth Constants");
		oauthManager.addConsumerInfoSet(getLocalhostConsumerInfo());
		oauthManager.addConsumerInfoSet(getFoursquareBrandsConsumerInfo());
		oauthManager.addConsumerInfoSet(getSocialUsageConsumerInfo());
	}

	private static OAuthConsumerInfoSet getLocalhostConsumerInfo() {
		OAuthConsumerInfoSet localhost = new OAuthConsumerInfoSet();
		localhost.setName("Foursquare - Localhost:8888");
		localhost.setCallbackUri(FOURSQUARE_CALLBACK_URI);
		localhost
				.setConsumerKey("FOQAUJ3EWKDHT2IOCO1QSODM32XM0ALGSWX4N3OBNWAJHLIQ");
		localhost
				.setConsumerSecret("10PNO0M0IIDRBUJHKCZ2BRBUX0EVDPBGLN125NX1UWAI41XK");
		localhost.setUrlPrefix("localhost");
		return localhost;
	}

	private static OAuthConsumerInfoSet getSocialUsageConsumerInfo() {
		OAuthConsumerInfoSet localhost = new OAuthConsumerInfoSet();
		localhost.setName("Foursquare - SocialUsage.com");
		localhost.setUrlPrefix("socialusage");
		localhost.setCallbackUri(FOURSQUARE_CALLBACK_URI);
		localhost
				.setConsumerKey("QAW25ZPXDSDCI0JZHH0H51UAZULE0MXRDDZTP5NWB50QHRHS");
		localhost
				.setConsumerSecret("JWO4EBWPPF2I41A1ANO2J0E4AJSSUYOVEXSRJV51QMWWLDFM");

		return localhost;
	}

	private static OAuthConsumerInfoSet getFoursquareBrandsConsumerInfo() {
		OAuthConsumerInfoSet localhost = new OAuthConsumerInfoSet();
		localhost.setName("Foursquare - FoursquareBrands.com");
		localhost.setUrlPrefix("foursquarebrands");
		localhost.setCallbackUri(FOURSQUARE_CALLBACK_URI);
		localhost
				.setConsumerKey("CHPKVTXPDOCIYHK02LRXTXMWI1JJMQARZVXGNGTTZAOD5UMO");
		localhost
				.setConsumerSecret("2OC1GI3CVYV35GSCADZSTG5NJYIW0F325WE2HPTZ2X2P0NA5");

		return localhost;
	}

}
