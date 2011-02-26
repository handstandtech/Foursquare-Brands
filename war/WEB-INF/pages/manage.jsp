<%@ page isELIgnored="false" language="java"
	contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="foursquarebrands" tagdir="/WEB-INF/tags/foursquarebrands"%>
<%@ page import="com.handstandtech.server.SessionConstants"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="javax.jdo.PersistenceManager"%>
<%@ page import="com.handstandtech.server.db.PMF"%>
<%@ page import="com.handstandtech.server.SessionConstants"%>
<%@ page import="com.handstandtech.server.RequestConstants"%>
<%@ page import="oauth.signpost.OAuthConsumer"%>
<%@ page import="oauth.signpost.basic.DefaultOAuthConsumer"%>
<%@ page import="com.handstandtech.foursquare.shared.model.v2.FoursquareUser"%>
<%@ page import="com.handstandtech.foursquare.server.FoursquareConstants"%>
<%@ page import="com.handstandtech.foursquare.server.FoursquareHelper"%>
<%@ page import="com.handstandtech.brandfinder.server.tasks.FollowerCountTaskServlet"%>
<%@ page import="com.handstandtech.brandfinder.server.ParseCSV"%>
<%@ page import="com.handstandtech.brandfinder.server.DAO"%>
<%@ page import="com.handstandtech.brandfinder.shared.model.User"%>
<%@ page import="com.handstandtech.brandfinder.server.util.PageLoadUtils"%>
<%@ page import="com.handstandtech.brandfinder.server.util.SessionHelper"%>
<%@ page import="com.handstandtech.brandfinder.shared.model.BrandDiscovered"%>
<%@ page import="com.handstandtech.brandfinder.server.twitter.FoursquareBrandsTwitter"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>
<%@ page import="com.handstandtech.shared.model.rest.RESTResult"%>
<%@ page import="com.handstandtech.foursquare.server.FoursquareUtils"%>
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
<%@ page import="com.handstandtech.brandfinder.server.Manager" %>
<%
	User currentUser = SessionHelper.getCurrentUser(session);
	
	Map<String, FoursquareUser> userMap = null;
	String userType = null;
	String uriString = request.getRequestURI();
	System.out.println(uriString);
	if(uriString.startsWith("/manage/brands")){
		userType="brands";
		//Get The Current Brands
		userMap = Manager.getBrandMap(session);	
	} else {
		//Get The Current Celebrities
		userType="celebs";
		userMap = Manager.getCelebMap(session);	
	}
	request.setAttribute("userType", userType);
	
	//Get The Current User's Friends
	Collection<FoursquareUser> friends = Manager.getCurrentUsersFriends(currentUser);
	
	// See who is followed
	Map<String, FoursquareUser> mapOfFollowedUsers = PageLoadUtils.getFollowing(friends, userMap);
	
	// See if they know any new users, if so, add them
	mapOfFollowedUsers = Manager.findNewUsers(userType, currentUser, mapOfFollowedUsers);
	
	//Prepare followed and notFollowed lists for request
	Manager.prepareFollowedAndNotFollowedLists(currentUser, mapOfFollowedUsers, userMap, request);
%>

<c:set var="numFollowed" value="${fn:length(followed)}" scope="request"/>
<c:set var="numNotFollowed" value="${fn:length(notFollowed)}" scope="request"/>
<c:set var="total" value="${numFollowed + numNotFollowed}" scope="request"/>
<foursquarebrands:html>
<foursquarebrands:head>
	<link type="text/css" rel="stylesheet"
		href="/assets/js/fancybox/jquery.fancybox-1.3.4.css" />
	<script type="text/javascript"
		src="/assets/js/fancybox/jquery.fancybox-1.3.4.pack.js"></script>
	<script type="text/javascript" src="/assets/js/foursquarebrands.js"></script>
</foursquarebrands:head>
<foursquarebrands:body>
	<div style="overflow: hidden; margin-left: 5px;">
		<!--img class="user-photo" src="${currentUser.foursquareUser.photo}" width="110" height="110" /-->
		<h2 class="following-stats">Hey ${currentUser.foursquareUser.firstName}! You're	Following <span class="percentage">${numFollowed}</span> of ${total} ${userType} on Foursquare.</h2>
		<p>Follow and UnFollow ${userType} with 1-click.  The ${userType} are ordered by when they were discovered.  The newest ${userType} are displayed in the bottom-right of each section.  Use the links to Follow or UnFollow ${userType} with 1-click.  Send us Questions/Comments using the Feedback Tab on the left.</p>
	</div>
	<h3 class="section-header">Following:</h3>
	<%--foursquarebrands:skyscraper-ad/--%>
	<div class="section following">
		<c:forEach var="user" items="${followed}">
			<c:set var="user" value="${user}" scope="request"/>
			<foursquarebrands:user-info/>
		</c:forEach>
	</div>
	<hr />
	<h3 class="section-header">Not Following:</h3>
	<%--foursquarebrands:skyscraper-ad/--%>
	<div class="section not-following">
		<c:forEach var="user" items="${notFollowed}">
			<c:set var="user" value="${user}" scope="request"/>
			<foursquarebrands:user-info/>
		</c:forEach>
	</div>
	<div id="dialogs" style="display: none;">
		<div id="already-following" class="error-dialog">
			<img class="photo" src="" />
			<h3>You are already following <span class="brand-name"></span>!</h3>
			<p>The page will refresh in <span class="timer">5</span> seconds to show the most up to date info, or you can refresh the page manually.</p>
		</div>
		<div id="rate-limit-exceeded" class="error-dialog">
			<h3>Foursquare Rate Limit Exceeded.</h3>
			<p>Sounds like you have been following a lot of people! Foursquare imposes some limits to the amount of times their API can be called. For more information see <a	href="http://developer.foursquare.com/docs/overview.html">their	documentation on rate limiting</a>. If you keep seeing this error, come	back in an hour and try again.</p>
		</div>
	</div>
	
</foursquarebrands:body>
</foursquarebrands:html>