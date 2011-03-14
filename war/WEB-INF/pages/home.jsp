<%@ page isELIgnored="false" trimDirectiveWhitespaces="true" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="foursquarebrands" tagdir="/WEB-INF/tags/foursquarebrands"%>
<%@ page import="com.handstandtech.server.SessionConstants"%>
<%@ page import="com.handstandtech.brandfinder.server.Manager" %>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="javax.jdo.PersistenceManager"%>
<%@ page import="com.handstandtech.server.SessionConstants"%>
<%@ page import="com.handstandtech.server.RequestConstants"%>
<%@ page import="com.handstandtech.brandfinder.shared.model.User"%>
<%@ page
	import="com.handstandtech.brandfinder.server.tasks.FollowerCountTaskServlet"%>
<%@ page import="com.handstandtech.brandfinder.server.ParseCSV"%>
<%@ page import="com.handstandtech.brandfinder.server.DAO"%>
<%@ page import="com.handstandtech.brandfinder.server.CachingDAOImpl"%>
<%@ page
	import="com.handstandtech.brandfinder.server.util.PageLoadUtils"%>
<%@ page
	import="com.handstandtech.brandfinder.server.util.SessionHelper"%>
<%@ page
	import="com.handstandtech.brandfinder.shared.model.DailyFollowEventCount"%>
<%@ page
	import="com.handstandtech.brandfinder.shared.model.DailyFollowerCount"%>
<%@ page import="com.handstandtech.brandfinder.shared.util.ModelUtils"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>
<%@ page import="com.handstandtech.shared.model.rest.RESTResult"%>
<%@ page import="com.handstandtech.foursquare.v2.util.FoursquareUtils"%>
<%@ page import="com.handstandtech.foursquare.shared.model.v2.FoursquareUser"%>
<%@ page import="com.handstandtech.brandfinder.shared.model.BrandDiscovered"%>
<%@ page import="com.googlecode.objectify.Query"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.HashSet"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Collections"%>
<%@ page import="java.util.Comparator"%>
<%@ page import="java.io.InputStream"%>
<%@ page import="java.io.BufferedReader"%>
<%@ page import="java.io.InputStreamReader"%>
<%@ page import="org.slf4j.Logger"%>
<%@ page import="org.slf4j.LoggerFactory"%>
<%

	String uriString = request.getRequestURI();
	Logger log = LoggerFactory.getLogger(this.getClass());
	User currentUser = SessionHelper.getCurrentUser(session);
	
	String pageStr = request.getParameter("page");
	Integer pageNum = 1;
	if (pageStr != null) {
		try {
			pageNum = Integer.parseInt(pageStr);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	if(currentUser!=null){
		log.info("Get The Current User's Friends");
		Collection<FoursquareUser> friends = Manager.getCurrentUsersFriends(currentUser);
		log.info(currentUser.getFoursquareUser().getName() + " has "+ friends.size() + " Friends Total.");
		
		Map<String, FoursquareUser> friendsMap = Manager.createUserMap(friends);
		request.setAttribute("friendsMap", friendsMap);
	}
	
	DAO dao = new CachingDAOImpl();

	Integer numNew = 10;
	request.setAttribute("numNew", numNew);
	
	List<BrandDiscovered> newlyDiscovered = new ArrayList<BrandDiscovered>();
	List<BrandDiscovered> newCelebs = dao.getNewestCelebrities(numNew);
	request.setAttribute("newCelebs", newCelebs);
	List<BrandDiscovered> newBrands = dao.getNewestBrands(numNew);
	request.setAttribute("newBrands", newBrands);
	newlyDiscovered.addAll(newCelebs);
	newlyDiscovered.addAll(newBrands);
	

	Map<String, FoursquareUser> foursquareUsersMap = new HashMap<String, FoursquareUser>();
	Collection<String> ids = new HashSet<String>();
	for(BrandDiscovered bd :newlyDiscovered){
		ids.add(bd.getBrandId());
	}
	
	List<FoursquareUser> usersForIds = dao.getFoursquareUsersForIds(ids);
	for(FoursquareUser u: usersForIds){
		foursquareUsersMap.put(u.getId(), u);
	}
	request.setAttribute("foursquareUsersMap", foursquareUsersMap);
%>
<foursquarebrands:html>
	<foursquarebrands:head>
		<meta name="Description" content="Find and Manage Foursquare Brands and Celebrities" />
 		<meta name="Keywords" content="Foursquare Brands,Foursquare,Brands,FoursquareBrands,Brand,Celebs,Celebrities,Celebs,Celeb" />
	
		<!-- Facebook Properties --> 
		<meta property="og:site_name" content="FoursquareBrands.com"/> 
		<meta property="og:title" content="Foursquare Brands"/> 
		<meta property="og:url" content="http://www.foursquarebrands.com"/> 
		<meta property="og:image" content="http://www.foursquarebrands.com/assets/images/foursquare-brands-logo.png"/> 
		<meta property="og:description" content="Find and Manage Foursquare Brands and Celebrities" />
	
		<script type="text/javascript" src="/assets/js/basicfollow.js"></script>
	</foursquarebrands:head>
	<foursquarebrands:body>
		<c:choose>
		  <c:when test="${currentUser==null}">
		    <h1 class="align-center">Find and Manage Foursquare Brands and Celebrities</h1>
		    <br/>
		    <br/>
		    <div class="feature-checklist">
		      <ul>
		        <li>
		          <h3><img src="/assets/images/check-button.png"/>Most Up to Date List of Brands and Celebrities</h3>
		        </li>
		        <li>
		          <h3><img src="/assets/images/check-button.png"/>Easiest Way to Manage Who You are Following</h3>
		        </li>
		        <li>
		          <h3><img src="/assets/images/check-button.png"/>Follow or UnFollow with 1 Click</h3>
		        </li>
		      </ul>
		      <hr style="margin:5px 15px 15px 15px"/>
		      <p class="align-center bold">Sign In with Foursquare to Get Started!</p>
		      <p class="login-button">
		        <a href="/login">
		          <img src="/assets/images/signinwith-foursquare.png"/> 
		        </a>
		      </p>
		    </div>
		    <br/>
		</c:when>
		<c:otherwise>
		    <h1 class="align-center">Hey ${currentUser.foursquareUser.firstName}, you are logged in!<br/>Here are the newest Brands and Celebrities!</h1>
	    	<br/>
		    <br/>
			<c:if test="${newBrands!=null}">
				<c:set var="userType" value="brands" scope="request"/>
				<h2 class="align-center">${numNew} Newest Brands</h2>
				<br />
				<c:set var="count" value="${1}" scope="request" />
				<div class="leaderboard">
					<c:forEach var="brand" items="${newBrands}">
						<c:set var="user" value="${foursquareUsersMap[brand.brandId]}" scope="request"/>
						<foursquarebrands:user-row/>
						<c:set var="count" value="${count+1}" scope="request" />
					</c:forEach>
				</div>
			</c:if>
			<br/>
		    <br/>
			<c:if test="${newCelebs!=null}">
				<c:set var="userType" value="celebs" scope="request"/>
				<h2 class="align-center">${numNew} Newest Celebrities</h2>
				<br />
				<c:set var="count" value="${1}" scope="request" />
				<div class="leaderboard">
					<c:forEach var="celeb" items="${newCelebs}">
						<c:set var="user" value="${foursquareUsersMap[celeb.brandId]}" scope="request"/>
						<foursquarebrands:user-row/>
						<c:set var="count" value="${count+1}" scope="request" />
					</c:forEach>
				</div>
			</c:if>
			<foursquarebrands:error-dialogs/>
	    </c:otherwise>
	    </c:choose>
</foursquarebrands:body>
</foursquarebrands:html>