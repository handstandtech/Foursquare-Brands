<%@ page isELIgnored="false" language="java"
	contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="foursquarebrands" tagdir="/WEB-INF/tags/foursquarebrands"%>
<%@page import="com.handstandtech.server.SessionConstants"%>
<%@page import="java.text.DecimalFormat"%>
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
<%@ page import="com.handstandtech.brandfinder.server.util.PageLoadUtils"%>
<%@ page import="com.handstandtech.brandfinder.server.util.SessionHelper"%>
<%@ page import="com.handstandtech.brandfinder.shared.model.BrandDiscovered"%>
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
<%@ page import="java.io.InputStreamReader"%>
<%
	OAuthConsumer consumer = (OAuthConsumer) session.getAttribute(FoursquareConstants.CONSUMER_CONSTANT);
	
	FoursquareUser currentUser = SessionHelper.getCurrentUser(session);
	
	DAO dao = new DAO();
	List<FoursquareUser> brands = dao.getBrands();
	
	/*Collections.sort(brands, new Comparator<FoursquareUser>() {
		public int compare(FoursquareUser o1, FoursquareUser o2) {
			return o1.getFirstName().compareTo(o2.getFirstName());
		}
	});*/
	
	// If there are currently no brands
	if (brands.size() == 0) {
		System.out.println("Currently... Do Nothing");
		PageLoadUtils.initializeBrandListFromCSVFile(session);
	
		// re-retreive brands from the database
		brands = dao.getBrands();
	}
	
	Map<String, FoursquareUser> idToBrandMap = PageLoadUtils.createIdToBrandMap(brands);
	
	FoursquareHelper helper = new FoursquareHelper(consumer);
	List<FoursquareUser> friends = helper.getFriends("self");
	
	// See who is followed
	System.out.println("See who is followed");
	Map<String, FoursquareUser> mapOfFollowedBrands = PageLoadUtils.getFollowing(friends,idToBrandMap);
	
	// See if there are any that weren't in the database
	for (String followedBrandKey : mapOfFollowedBrands.keySet()) {
		FoursquareUser brand = idToBrandMap.get(followedBrandKey);
		if (brand == null) {
			// FOUND SOMETHING NEW, SEE IF IT'S A BRAND
			brand = mapOfFollowedBrands.get(followedBrandKey);
			brand = helper.getUserInfo(followedBrandKey);
			if(brand.getType()!=null){
				if(brand.getType().equals("brand")){
					//IT IS A BRAND
					dao.updateFoursquareUser(brand);
			
					// Add Analytic about Discovered Brand
					BrandDiscovered brandDiscovered = new BrandDiscovered();
					brandDiscovered.setDate(new Date());
					brandDiscovered.setBrandId(brand.getId());
					brandDiscovered.setUserId(currentUser.getId());
					dao.updateBrandDiscovered(brandDiscovered);
					
					idToBrandMap.put(brand.getId(), brand);
				}
			}
		}
	}
	
	List<FoursquareUser> followed = new ArrayList<FoursquareUser>();
	List<FoursquareUser> notFollowed = new ArrayList<FoursquareUser>();
	
	for (String brandKey : idToBrandMap.keySet()) {
		FoursquareUser brand = mapOfFollowedBrands.get(brandKey);
		boolean isFollowed = false;
		if (brand != null) {
			isFollowed = true;
		}
		
		FoursquareUser brandEntry = idToBrandMap.get(brandKey);
		if(isFollowed) {
			// FOLLOWED
			followed.add(brandEntry);
		}	else {
			// NOT FOLLOWED
			notFollowed.add(brandEntry);
			System.out.println("NOT FOLLOWING: " + brandEntry.getFirstName());
			
		}
	}
	
	/*Long ONE_SECOND = 1000L;
	Long ONE_MINUTE = 60 * ONE_SECOND;
	Long ONE_HOUR = 60 * ONE_MINUTE;
	Long ONE_DAY = 24 * ONE_HOUR;*/
	List<BrandDiscovered> discoveredBrands = dao.getBrandDiscoveredSince(null);
	final Map<String, BrandDiscovered> discovered = PageLoadUtils.createMap(discoveredBrands);
	
	try {
		Collections.sort(followed, PageLoadUtils.getBrandCompare(discovered));
		Collections.sort(notFollowed, PageLoadUtils.getBrandCompare(discovered));
	}catch(Exception e){
		//DO nothing...
		e.printStackTrace();
	}
	
	System.out.println("brands: " + brands.size());
	System.out.println("followed: " + followed.size());
	System.out.println("notFollowed: " + notFollowed.size());
	
	request.setAttribute("followed", followed);
	request.setAttribute("numFollowed", followed.size());
	request.setAttribute("notFollowed", notFollowed);
	int total = followed.size() + notFollowed.size();
	System.out.println("Total Brands in Database: " + brands.size());
	System.out.println("Total Brands (Sum): " + total);
	request.setAttribute("total", total);
%>
<foursquarebrands:html>
<foursquarebrands:head>
	<link type="text/css" rel="stylesheet"
		href="/assets/js/fancybox/jquery.fancybox-1.3.4.css" />
	<script type="text/javascript"
		src="/assets/js/fancybox/jquery.fancybox-1.3.4.pack.js"></script>
	<script type="text/javascript">
	$(document)
			.ready(
					function() {

						$("a.foursquare-photo-link").fancybox({
							'width' : '90%',
							'height' : '90%',
							'autoScale' : false,
							'transitionIn' : 'none',
							'transitionOut' : 'none',
							'type' : 'iframe',
							'centerOnScroll' : 'true'
						});

						/**
						 * When a Brand is UnFollowed
						 */
						function unfollow(user) {
							var id = user.attr('userid');

							_gaq.push([ '_trackEvent', 'brands', 'unfollowed',
									id ]);

							$
									.ajax({
										type : 'POST',
										url : "/unfollow?id=" + id,
										data : {
											name : "John",
											time : "2pm"
										},
										success : function(response) {
											//No Error
											if (response.error == null) {
												user.find('img.loading')
														.remove();
												user
														.hide(
																'slow',
																function() {
																	$(
																			'.not-following')
																			.append(
																					user);
																	var theimage = $(
																			this)
																			.find(
																					'img:first');
																	theimage
																			.removeClass('half-opacity');
																	user
																			.show('slow');
																	updateCount();
																});
											}
											//Error Occurred
											else {
												alert(response.error);
												location.reload();
											}
										},
										error : function(request) {
											alert("An error has occurred, we're going to refresh the page to try and fix it.  If this continues to happen, let us know @HandstandTech.  Thanks!");
											location.reload();
										}
									});
						}

						/**
						 * When a Brand is Followed
						 */
						function follow(user) {
							var id = $(user).attr('userid');

							_gaq
									.push([ '_trackEvent', 'brands',
											'followed', id ]);

							//alert('follow: '+ id);
							$
									.ajax({
										type : 'POST',
										url : "/follow?id=" + id,
										data : {
											name : "John",
											time : "2pm"
										},
										success : function(response) {
											//No Error
											if (response.errorType == null) {

												user.find('img.loading')
														.remove();
												user
														.hide(
																'slow',
																function() {
																	$(
																			'.following')
																			.append(
																					user);
																	var theimage = $(
																			this)
																			.find(
																					'img:first');
																	theimage
																			.removeClass('half-opacity');
																	user
																			.show('slow');
																	updateCount();
																});
											}
											//Rate Limit Exceeded
											else if (response.errorType == 'rate_limit_exceeded') {
												//Rate Limit
												$
														.fancybox({
															'padding' : 0,
															'href' : '#rate-limit-exceeded',
															'transitionIn' : 'elastic',
															'transitionOut' : 'elastic',
															'centerOnScroll' : 'true'
														});
											}
											//Error Occurred
											else if (response.errorType == "other") {
												var infoDiv = $('.user-info[userid='
														+ id + ']');
												var photoSrc = $(infoDiv).find(
														'img.photo')
														.attr('src');
												var brandName = $(infoDiv)
														.find('.brand-name')
														.html();
												$(
														'#already-following span.brand-name')
														.html(brandName);
												$(
														'#already-following img.photo')
														.attr('src', photoSrc);
												$
														.fancybox({
															'padding' : 0,
															'href' : '#already-following',
															'transitionIn' : 'elastic',
															'transitionOut' : 'elastic',
															'centerOnScroll' : 'true'
														});
												$(
														'#already-following span.timer')
														.html('5');
												setTimeout(
														function() {
															$(
																	'#already-following span.timer')
																	.html('4');
															setTimeout(
																	function() {
																		$(
																				'#already-following span.timer')
																				.html(
																						'3');
																		setTimeout(
																				function() {
																					$(
																							'#already-following span.timer')
																							.html(
																									'2');
																					setTimeout(
																							function() {
																								$(
																										'#already-following span.timer')
																										.html(
																												'1');
																								setTimeout(
																										function() {
																											$(
																													'#already-following span.timer')
																													.html(
																															'0');
																											location
																													.reload();
																										},
																										1000);
																							},
																							1000);
																				},
																				1000);
																	}, 1000);
														}, 1000);
											}
										},
										error : function(request) {
											alert('failed request');
										}
									});
						}

						function updateCount() {
							var followingCount = $(".following .user-info").length;
							$('.following-stats span.percentage').text(
									followingCount);
						}

						$('a.follow-unfollow-link')
								.click(
										function() {
											var user = $(this).parent();
											user.find('.foursquare-link')
													.hide();
											user.find('.follow-unfollow-link')
													.hide();
											user
													.append('<img class="loading" src="/assets/images/loading.gif"/>');
											var theimage = user
													.find('img:first');
											theimage.addClass('half-opacity');
											if (user.parent().hasClass(
													'following')) {
												unfollow(user);
											} else {
												follow(user);
											}
										});

						$('.user-info')
								.hover(
										function() {
											var theimage = $(this).find(
													'img:first');
											theimage.addClass('half-opacity');

											$(this).addClass(
													'left-right-border');

											var imagePositionLeft = theimage
													.position().left;
											var imagePositionTop = theimage
													.position().top;

											var followunfollowlink = $(this)
													.find(
															'.follow-unfollow-link');
											var foursquarelink = $(this).find(
													'.foursquare-link');
											foursquarelink.show();
											followunfollowlink.show();

											if ($(this).parent().hasClass(
													'following')) {
												followunfollowlink.find('span')
														.text('UnFollow');
											} else {
												followunfollowlink.find('span')
														.text('Follow');
											}

											var IMG_SIZE = 50;

											var fsLinkWidth = foursquarelink
													.width() + 20; //+20 for 10px padding
											var fsLinkHeight = foursquarelink
													.height();

											var linkPositionLeft = imagePositionLeft
													+ ((IMG_SIZE - fsLinkWidth) / 2);
											var linkPositionTop = imagePositionTop
													- (fsLinkHeight + 12);//+ the border, etc
											foursquarelink.css('left',
													linkPositionLeft).css(
													'top', linkPositionTop);

											var followLinkWidth = followunfollowlink
													.width() + 22; //+20 for 10px padding
											var followLeft = imagePositionLeft
													+ ((IMG_SIZE - followLinkWidth) / 2);
											var followTop = imagePositionTop
													+ (IMG_SIZE);//-5

											followunfollowlink.css('left',
													followLeft).css('top',
													followTop);

											return false;
										},
										function() {
											var theimage = $(this).find(
													'img:first');
											theimage
													.removeClass('half-opacity');

											$(this).removeClass(
													'left-right-border');

											$(this).find('.foursquare-link')
													.hide();
											$(this).find(
													'.follow-unfollow-link')
													.hide();
											return false;
										})
					});
</script>
</foursquarebrands:head>
<foursquarebrands:body>
	<div style="overflow: hidden; margin-left: 5px;"><img
		class="user-photo" src="${currentUser.photo}" width="110" height="110" />
	<h2 class="following-stats">Hey ${currentUser.firstName}! You're
	Following <span class="percentage">${numFollowed}</span> of ${total}
	brands on Foursquare.</h2>
	<p>Manage the brands you're following and not following below. Use
	the links to Follow or UnFollow them as you please by clicking the
	button that appears after hovering over their logo. Clicking the name
	of the brand will open up their page on Foursquare.</p>
	</div>
	<h3 class="section-header">Following:</h3>
	<div class="section following"><c:forEach var="user"
		items="${followed}">
		<c:if test="${user.id!=null}">
			<div class="user-info" name='${user.firstName}' userid='${user.id}'>
			<a href="http://foursquare.com/user/${user.id}"
				class="foursquare-photo-link" target="_blank"> <img
				class="photo" src='${user.photo}' /> </a> <c:choose>
				<c:when test="${user.lastName!=null}">
					<c:set var="brandName" value="${user.firstName} ${user.lastName}" />
				</c:when>
				<c:otherwise>
					<c:set var="brandName" value="${user.firstName}" />
				</c:otherwise>
			</c:choose> <a href="http://foursquare.com/user/${user.id}"
				class="foursquare-link hovercard" style="display: none;"> <span
				class='brand-name'>${brandName}</span> </a> <a
				class="follow-unfollow-link hovercard" href="javascript:void(0)"
				style="display: none;"> <span></span> </a></div>
		</c:if>
	</c:forEach></div>
	<hr />
	<h3 class="section-header">Not Following:</h3>
	<div class="section not-following"><c:forEach var="user"
		items="${notFollowed}">
		<div class="user-info" name='${user.firstName}' userid='${user.id}'>
		<c:choose>
			<c:when test="${user.lastName!=null}">
				<c:set var="brandName" value="${user.firstName} ${user.lastName}" />
			</c:when>
			<c:otherwise>
				<c:set var="brandName" value="${user.firstName}" />
			</c:otherwise>
		</c:choose> <a href="http://foursquare.com/user/${user.id}"
			class="foursquare-photo-link" target="_blank"> <img class="photo"
			src='${user.photo}' /> </a> <a
			href="http://foursquare.com/user/${user.id}"
			class="foursquare-link hovercard" style="display: none;"
			target="_blank"> <span class="brand-name">${brandName}</span> </a> <a
			class="follow-unfollow-link hovercard" href="javascript:void(0)"
			style="display: none;"> <span></span> </a></div>
	</c:forEach></div>
	<div id="dialogs" style="display: none;">
	<div id="already-following" class="error-dialog"
		style="overflow: hidden;"><img
		style="float: right; margin: 10px; border: 1px solid #000000;"
		class="photo" src="" />
	<h3>You are already following <span class="brand-name"></span>!</h3>
	<p>The page will refresh in <span class="timer">5</span> seconds to
	show the most up to date info, or you can refresh the page manually.</p>
	</div>
	<div id="rate-limit-exceeded" class="error-dialog"
		style="overflow: hidden;">
	<h3>Foursquare Rate Limit Exceeded.</h3>
	<p>Sounds like you have been following a lot of people! Foursquare
	imposes some limits to the amount of times their API can be called. For
	more information see <a
		href="http://developer.foursquare.com/docs/overview.html">their
	documentation on rate limiting</a>. If you keep seeing this error, come
	back in an hour and try again.</p>
	</div>
	</div>
	
</foursquarebrands:body>
</foursquarebrands:html>