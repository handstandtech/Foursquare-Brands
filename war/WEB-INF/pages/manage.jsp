<%@ page isELIgnored="false" language="java"
	contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="pf" tagdir="/WEB-INF/tags/pf"%>
<%@page import="com.handstandtech.server.SessionConstants"%>
<%@page import="java.text.DecimalFormat"%>
<%@ page import="javax.jdo.PersistenceManager"%>
<%@ page import="com.handstandtech.server.db.PMF"%>
<%@ page import="com.handstandtech.server.SessionConstants"%>
<%@ page import="com.handstandtech.server.RequestConstants"%>
<%@ page import="oauth.signpost.OAuthConsumer"%>
<%@ page import="oauth.signpost.basic.DefaultOAuthConsumer"%>
<%@ page
	import="com.handstandtech.foursquare.shared.model.v2.FoursquareUser"%>
<%@ page
	import="com.handstandtech.foursquare.server.FoursquareConstants"%>
<%@ page import="com.handstandtech.foursquare.server.FoursquareHelper"%>
<%@ page import="com.handstandtech.brandfinder.server.ParseCSV"%>
<%@ page import="com.handstandtech.brandfinder.server.DAO"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.HashMap"%>

<%
	OAuthConsumer consumer = (OAuthConsumer)session.getAttribute(FoursquareConstants.CONSUMER_CONSTANT);
	
	DAO dao = new DAO();
	List<FoursquareUser> brands = dao.getBrands();
	
	//If there are currently no brands
	/*if(brands.size()==0){
		//Initialize
		ParseCSV parser = new ParseCSV();
		String path = session.getServletContext().getRealPath("WEB-INF/backup/Brands.csv");
		List<FoursquareUser> initList = parser.parseBrandsCSV(path);
		for(FoursquareUser initBrand : initList) {
			dao.updateFoursquareUser(initBrand);
		}

		//Set the new list to be the "list we found"
		brands=initList;
	}*/
	
	Map<String, FoursquareUser> idToBrandMap = new HashMap<String, FoursquareUser>();
	for (FoursquareUser b : brands) {
		idToBrandMap.put(b.getId().toString(), b);
	}

	String oauth_token = consumer.getTokenSecret();
	FoursquareHelper helper = new FoursquareHelper(oauth_token);
	
	List<FoursquareUser> friends = helper.getFriends("self");
	
	//See who is followed
	System.out.println("See who is followed");
	List<FoursquareUser> followed = new ArrayList<FoursquareUser>();
	for (FoursquareUser friend : friends) {
		String key = friend.getId().toString();
		
		//See if we can find the user in our current brand list
		FoursquareUser brand = idToBrandMap.get(key);
		if (brand != null) {
			//We found the brand list
			followed.add(brand);
		} else {
			if (friend.getRelationship()!=null && friend.getRelationship().equals("followingThem")) {
				//We didn't find the brand in our list, but it's a brand.  Add it!
				FoursquareUser newBrand = helper.getUserInfo(friend.getId());
				dao.updateFoursquareUser(newBrand);
				
				//Add in the newly discovered brand!
				followed.add(newBrand);
				idToBrandMap.put(newBrand.getId(), newBrand);
			}
		}
	}
	
	//Determine who is unfollowed
	System.out.println("See who is unfollowed");
	List<FoursquareUser> unfollowed = new ArrayList<FoursquareUser>();

		Map<String, FoursquareUser> followedMap = new HashMap<String, FoursquareUser>();
		for (FoursquareUser f : followed) {
			followedMap.put(f.getId().toString(), f);
		}

		for (FoursquareUser brand : idToBrandMap.values()) {
			if(brand==null){
				System.out.println("hi");
			}else {
				String brandKey = brand.getId();
	
				FoursquareUser b = followedMap.get(brandKey);
				if (b == null) {
					unfollowed.add(idToBrandMap.get(brandKey));
				}
			}
		}

	System.out.println("brands: " + brands.size());
	System.out.println("followed: " + followed.size());
	System.out.println("unfollowed: " + unfollowed.size());

	request.setAttribute("followed", followed);
	request.setAttribute("unfollowed", unfollowed);
	request.setAttribute("numFollowed", followed.size());
	int total = followed.size() + unfollowed.size();
	request.setAttribute("total", total);
	DecimalFormat priceFormatter = new DecimalFormat("#0.0");
	Double percent = (double)followed.size()/(double)brands.size();
	String percentage = priceFormatter.format(percent*100);
	request.setAttribute("percentage", percentage);
%>
<pf:html>
<pf:head>
	<script type="text/javascript"
		src="http://code.jquery.com/jquery-1.4.4.min.js"></script>
	<script type="text/javascript"
		src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.4/jquery-ui.min.js"></script>
	<script type="text/javascript">
	$(document)
			.ready(
					function() {

						function unfollow(user) {
							var id = user.attr('userid');

							_gaq.push([ '_trackEvent', 'brands', 'unfollowed',
									id ]);

							$.ajax({
								type : 'POST',
								url : "/unfollow?id=" + id,
								data : {
									name : "John",
									time : "2pm"
								},
								success : function(response) {
									user.find('img.loading').remove();
									user.hide('slow', function() {
										$('.not-following').append(user);
										var theimage = $(this)
												.find('img:first');
										theimage.removeClass('half-opacity');
										user.show('slow');
										updateCount();
									});
								},
								error : function(request) {
									alert('failed request');
								}
							});
						}

						function follow(user) {
							var id = $(user).attr('userid');

							_gaq
									.push([ '_trackEvent', 'brands',
											'followed', id ]);

							//alert('follow: '+ id);
							$.ajax({
								type : 'POST',
								url : "/follow?id=" + id,
								data : {
									name : "John",
									time : "2pm"
								},
								success : function(response) {
									//alert('success');
									user.find('img.loading').remove();
									user.hide('slow', function() {
										$('.following').append(user);
										var theimage = $(this)
												.find('img:first');
										theimage.removeClass('half-opacity');
										user.show('slow');
										updateCount();
									});
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
													.append('<img class="loading" src="/images/loading.gif"/>');
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
</pf:head>
<pf:body>
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
		<div class="user-info" name='${user.firstName}' userid='${user.id}'>
		<a href="http://foursquare.com/user/${user.id}" target="_blank"> <img
			src='${user.photo}' /> </a> <a
			href="http://foursquare.com/user/${user.id}"
			class="foursquare-link hovercard" style="display: none;"
			target="_blank"> <c:choose>
			<c:when test="${user.lastName!=null}">
				<span>${user.firstName} ${user.lastName}</span>
			</c:when>
			<c:otherwise>
				<span>${user.firstName}</span>
			</c:otherwise>
		</c:choose> </a> <a class="follow-unfollow-link hovercard" href="javascript:void(0)"
			style="display: none;"> <span></span> </a></div>
	</c:forEach></div>
	<hr />
	<h3 class="section-header">Not Following:</h3>
	<div class="section not-following"><c:forEach var="user"
		items="${unfollowed}">
		<div class="user-info" name='${user.firstName}' userid='${user.id}'>
		<a href="http://foursquare.com/user/${user.id}" target="_blank">
		<img src='${user.photo}' />
		</a>
		 <a
			href="http://foursquare.com/user/${user.id}"
			class="foursquare-link hovercard" style="display: none;"
			target="_blank"> <c:choose>
			<c:when test="${user.lastName!=null}">
				<span>${user.firstName} ${user.lastName}</span>
			</c:when>
			<c:otherwise>
				<span>${user.firstName}</span>
			</c:otherwise>
		</c:choose> </a> <a class="follow-unfollow-link hovercard" href="javascript:void(0)"
			style="display: none;"> <span></span> </a></div>
	</c:forEach></div>
</pf:body>
</pf:html>