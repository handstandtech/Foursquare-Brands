<%@ page isELIgnored="false" trimDirectiveWhitespaces="true" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="foursquarebrands" tagdir="/WEB-INF/tags/foursquarebrands"%>
<%@ page import="com.handstandtech.server.SessionConstants"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="com.handstandtech.server.SessionConstants"%>
<%@ page import="com.handstandtech.server.RequestConstants"%>
<%@ page import="oauth.signpost.OAuthConsumer"%>
<%@ page import="oauth.signpost.basic.DefaultOAuthConsumer"%>
<%@ page import="com.handstandtech.foursquare.shared.model.v2.FoursquareUser"%>
<%@ page import="com.handstandtech.foursquare.server.FoursquareConstants"%>
<%@ page import="com.handstandtech.brandfinder.server.tasks.FollowerCountTaskServlet"%>
<%@ page import="com.handstandtech.brandfinder.server.ParseCSV"%>
<%@ page import="com.handstandtech.brandfinder.server.DAO"%>
<%@ page import="com.handstandtech.brandfinder.server.CachingDAOImpl"%>
<%@ page import="com.handstandtech.brandfinder.shared.model.User"%>
<%@ page import="com.handstandtech.brandfinder.server.util.PageLoadUtils"%>
<%@ page import="com.handstandtech.brandfinder.server.util.SessionHelper"%>
<%@ page import="com.handstandtech.brandfinder.shared.model.BrandDiscovered"%>
<%@ page import="com.handstandtech.brandfinder.server.twitter.FoursquareBrandsTwitter"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>
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

	log.info("Prepare followed and notFollowed lists for request.");
	Manager.prepareFollowedAndNotFollowedLists(currentUser, friends, userMap, request);
	
	request.setAttribute("userMap", userMap);
%>

<c:set var="numFollowed" value="${fn:length(followed)}" scope="request"/>
<c:set var="numNotFollowed" value="${fn:length(notFollowed)}" scope="request"/>
<c:set var="total" value="${numFollowed + numNotFollowed}" scope="request"/>
<foursquarebrands:html>
	<foursquarebrands:head>
		<script type="text/javascript" src="/assets/js/foursquarebrands.js"></script>
		<script type="text/javascript">
			function handleUnFollowSuccess(id) {
				var user = $("div.user-info[userid="+id+"]");
				user.find('img.loading').remove();
				user.hide('slow', function() {
					$('.not-following').append(user);
					var theimage = $(this).find('img:first');
					theimage.removeClass('half-opacity');
					user.show('slow');
					updateCount();
				});
			}
	
			function handleFollowSuccess(id) {
				var user = $("div.user-info[userid="+id+"]");
				user.find('img.loading').remove();
				user.hide('slow', function() {
					$('.following').append(user);
					var theimage = $(this).find('img:first');
					theimage.removeClass('half-opacity');
					user.show('slow');
					updateCount();
				});
			}
			
			$(document).ready(function() {
				$('a.follow-unfollow-link').click(function() {
					var user = $(this).parent();
					user.find('.foursquare-link').hide();
					user.find('.follow-unfollow-link').hide();
					user.append('<img class="loading" src="/assets/images/loading.gif"/>');
					var theimage = user.find('img:first');
					theimage.addClass('half-opacity');
					if (user.parent().hasClass('following')) {
						var id = $(user).attr('userid');
						unfollow(id);
					} else {
						var id = $(user).attr('userid');
						follow(id);
					}
				});
	
				$('.user-info').hover(function() {
					var theimage = $(this).find('img:first');
					theimage.addClass('half-opacity');
	
					$(this).addClass('left-right-border');
	
					var imagePositionLeft = theimage.position().left;
					var imagePositionTop = theimage.position().top;
	
					var followunfollowlink = $(this).find('.follow-unfollow-link');
					var foursquarelink = $(this).find('.foursquare-link');
					foursquarelink.show();
					followunfollowlink.show();
	
					if ($(this).parent().hasClass(
							'following')) {
						followunfollowlink.find('span').text('UnFollow');
					} else {
						followunfollowlink.find('span').text('Follow');
					}
	
					var IMG_SIZE = 50;
	
					var fsLinkWidth = foursquarelink.width() + 20; // +20 for 10px padding
					var fsLinkHeight = foursquarelink.height();
	
					var linkPositionLeft = imagePositionLeft + ((IMG_SIZE - fsLinkWidth) / 2);
					var linkPositionTop = imagePositionTop - (fsLinkHeight + 12);// + the border, etc
					foursquarelink.css('left', linkPositionLeft).css('top',	linkPositionTop);
	
					var followLinkWidth = followunfollowlink.width() + 22; // +20 for 10px padding
					var followLeft = imagePositionLeft + ((IMG_SIZE - followLinkWidth) / 2);
					var followTop = imagePositionTop + (IMG_SIZE);// -5
	
					followunfollowlink.css('left', followLeft).css('top', followTop);
					return false;
				},
				function() {
					var theimage = $(this).find('img:first');
					theimage.removeClass('half-opacity');
	
					$(this).removeClass('left-right-border');
	
					$(this).find('.foursquare-link').hide();
					$(this).find('.follow-unfollow-link').hide();
					return false;
				})
			});
		</script>
	</foursquarebrands:head>
	<foursquarebrands:body>
		<div style="overflow: hidden; margin-left: 5px;">
			<h2 class="following-stats">Hey ${currentUser.foursquareUser.firstName}! You're	Following <span class="percentage">${numFollowed}</span>&nbsp;of&nbsp;<span>${total}</span>&nbsp;<span>${userType}</span>&nbsp;on Foursquare.</h2>
			<p>Follow and UnFollow ${userType} with 1-click.  Use the links to Follow or UnFollow ${userType} with 1-click.  Send us Questions/Comments using the Feedback Tab on the left.</p>
		</div>
		<h3 class="section-header">Following:</h3>
		<div class="section following">
			<c:forEach var="id" items="${followed}">
				<c:set var="id" value="${id}" scope="request"/>
				<foursquarebrands:user-info/>
			</c:forEach>
		</div>
		<hr />
		<h3 class="section-header">Not Following:</h3>
		<div class="section not-following">
			<c:forEach var="id" items="${notFollowed}">
				<c:set var="id" value="${id}" scope="request"/>
				<foursquarebrands:user-info/>
			</c:forEach>
		</div>
		<foursquarebrands:error-dialogs/>
	</foursquarebrands:body>
</foursquarebrands:html>