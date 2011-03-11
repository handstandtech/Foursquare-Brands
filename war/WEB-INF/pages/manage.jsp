<%@ page isELIgnored="false" trimDirectiveWhitespaces="true" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="foursquarebrands" tagdir="/WEB-INF/tags/foursquarebrands"%>
<%@ page import="com.handstandtech.server.SessionConstants"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="javax.jdo.PersistenceManager"%>
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
<%@ page import="org.slf4j.Logger"%>
<%@ page import="org.slf4j.LoggerFactory"%>
<%@ page import="com.handstandtech.brandfinder.server.Manager" %>
<%
	Logger log = LoggerFactory.getLogger(this.getClass());
	User currentUser = SessionHelper.getCurrentUser(session);
	
	log.info("Get The Current User's Friends");
	Collection<FoursquareUser> friends = Manager.getCurrentUsersFriends(currentUser);
	log.info(currentUser.getFoursquareUser().getName() + " has "+ friends.size() + " Friends Total.");
	
	
	Map<String, FoursquareUser> userMap = null;
	String userType = null;
	String pageTitle = null;
	String uriString = request.getRequestURI();
	if(uriString.startsWith("/manage/brands")){
		userType="brands";
		//Get The Current Brands
		userMap = Manager.getBrandMap(session);	
		pageTitle="Brands";
	} else {
		//Get The Current Celebrities
		userType="celebs";
		userMap = Manager.getCelebMap(session);
		pageTitle="Celebrities";
	}
	log.info("User Type -> " + userType);
	log.info("Title -> " + pageTitle);
	request.setAttribute("userType", userType);
	request.setAttribute("title", pageTitle);
	
	log.info("See if they know any new users, if so, add them.");
	Collection<FoursquareUser> newUsersFound = Manager.findNewUsers(userType, currentUser, friends);
	/*for(FoursquareUser newUserFound : newUsersFound){
		userMap.put(newUserFound.getId(), newUserFound);
	}*/
	
	log.info("Prepare followed and notFollowed lists for request.");
	Manager.prepareFollowedAndNotFollowedLists(currentUser, friends, userMap, request);
	
	request.setAttribute("userMap", userMap);
%>
<c:set var="numFollowed" value="${fn:length(followed)}" scope="request"/>
<c:set var="numNotFollowed" value="${fn:length(notFollowed)}" scope="request"/>
<c:set var="total" value="${numFollowed + numNotFollowed}" scope="request"/>
<foursquarebrands:html>
	<foursquarebrands:head>
		<link type="text/css" rel="stylesheet" href="/assets/js/fancybox/jquery.fancybox-1.3.4.css" />
		<script type="text/javascript" src="/assets/js/fancybox/jquery.fancybox-1.3.4.pack.js"></script>
		<script type="text/javascript" src="/assets/js/foursquarebrands.js"></script>
	</foursquarebrands:head>
	<foursquarebrands:body>
		<div style="overflow: hidden; margin-left: 5px;">
			<h2 class="following-stats">Hey ${currentUser.foursquareUser.firstName}! You're	Following <span class="percentage">${numFollowed}</span>&nbsp;of&nbsp;<span>${total}</span>&nbsp;<span>${userType}</span>&nbsp;on Foursquare.</h2>
			<p>Follow and UnFollow ${userType} with 1-click.  Use the links to Follow or UnFollow ${userType} with 1-click.  Send us Questions/Comments using the Feedback Tab on the left.</p>
		</div>
		<h3 class="section-header">Following:</h3>
		<%--foursquarebrands:skyscraper-ad/--%>
		<div class="section following">
			<c:forEach var="id" items="${followed}">
				<c:set var="id" value="${id}" scope="request"/>
				<foursquarebrands:user-info/>
			</c:forEach>
		</div>
		<hr />
		<h3 class="section-header">Not Following:</h3>
		<%--foursquarebrands:skyscraper-ad/--%>
		<div class="section not-following">
			<c:forEach var="id" items="${notFollowed}">
				<c:set var="id" value="${id}" scope="request"/>
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
			<div id="error-occurred" class="error-dialog">
				<h3>An Error Occurred while using the Foursquare API</h3>
				<p><strong>Error Type: </strong><span class="errorType"></span></p>
				<br/>
				<p><strong>Error Detail: </strong><span class="errorDetail"></span></p>
				<br/>
				<p style="display:none;"><strong>Error Code: </strong><span class="code"></span></p>
			</div>
		</div>
		
	</foursquarebrands:body>
</foursquarebrands:html>