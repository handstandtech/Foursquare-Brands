<%@page
	import="com.handstandtech.foursquare.v2.impl.CachingFoursquareAPIv2Impl"%>
<%@page import="com.handstandtech.foursquare.v2.FoursquareAPIv2"%>
<%@ page isELIgnored="false" trimDirectiveWhitespaces="true"
	contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="foursquarebrands"
	tagdir="/WEB-INF/tags/foursquarebrands"%>
<%@ page import="com.handstandtech.server.SessionConstants"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="javax.jdo.PersistenceManager"%>
<%@ page import="com.handstandtech.server.SessionConstants"%>
<%@ page import="com.handstandtech.server.RequestConstants"%>
<%@ page import="oauth.signpost.OAuthConsumer"%>
<%@ page import="oauth.signpost.basic.DefaultOAuthConsumer"%>
<%@ page
	import="com.handstandtech.foursquare.server.FoursquareConstants"%>
<%@ page
	import="com.handstandtech.foursquare.shared.model.v2.FoursquareUser"%>
<%@ page
	import="com.handstandtech.brandfinder.server.tasks.FollowerCountTaskServlet"%>
<%@ page import="com.handstandtech.brandfinder.server.ParseCSV"%>
<%@ page import="com.handstandtech.brandfinder.server.DAO"%>
<%@ page import="com.handstandtech.brandfinder.server.CachingDAOImpl"%>
<%@ page import="com.handstandtech.brandfinder.shared.model.User"%>
<%@ page
	import="com.handstandtech.brandfinder.server.util.PageLoadUtils"%>
<%@ page
	import="com.handstandtech.brandfinder.server.util.SessionHelper"%>
<%@ page
	import="com.handstandtech.brandfinder.shared.model.BrandDiscovered"%>
<%@ page
	import="com.handstandtech.brandfinder.server.twitter.FoursquareBrandsTwitter"%>
<%@ page import="com.handstandtech.shared.model.rest.RESTResult"%>
<%@ page import="com.handstandtech.foursquare.v2.util.FoursquareUtils"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Collections"%>
<%@ page import="java.util.Comparator"%>
<%@ page import="java.io.InputStream"%>
<%@ page import="java.io.BufferedReader"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.HashSet"%>
<%@ page import="java.io.InputStreamReader"%>
<%@ page import="com.handstandtech.brandfinder.server.Manager"%>
<%@ page import="com.googlecode.objectify.Query"%>
<%@ page import="org.slf4j.Logger"%>
<%@ page import="org.slf4j.LoggerFactory"%>
<%
	Logger log = LoggerFactory.getLogger(this.getClass());
	User currentUser = SessionHelper.getCurrentUser(session);

	//FoursquareAPIv2 helper = new CachingFoursquareAPIv2Impl(currentUser.getToken());
	//Collection<FoursquareUser> newUsers = helper.getAllFriends();
	//log.debug("Got: "+ newUsers);

	Map<String, FoursquareUser> userMap = Manager.getAllUsersMap();

	DAO dao = new CachingDAOImpl();
	List<BrandDiscovered> list = dao.getBrandDiscoveredSince(null);

	/*for (BrandDiscovered item : list) {
		if (item.getType() == null) {
			String foursquareId = item.getBrandId();
			FoursquareUser foursquareUser = userMap.get(foursquareId);
			if (foursquareUser != null) {
				item.setType(foursquareUser.getType());
				dao.updateBrandDiscovered(item);
				response.getWriter()
						.append(foursquareId + " - "
								+ foursquareUser.getName() + "("
								+ foursquareUser.getType() + ")");
			}
		}
	}*/
%>
DONE
