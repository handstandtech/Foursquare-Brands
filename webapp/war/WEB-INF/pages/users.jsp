<%@ page isELIgnored="false" trimDirectiveWhitespaces="true"
	contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="foursquarebrands"
	tagdir="/WEB-INF/tags/foursquarebrands"%>
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
<%@ page import="com.googlecode.objectify.Query"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Map"%>
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
		log.info("Get The Current User's Friends and check for new ones");
		Collection<FoursquareUser> friends = Manager.getCurrentUsersFriends(currentUser);
		log.info(currentUser.getFoursquareUser().getName() + " has "+ friends.size() + " Friends Total.");
			
		Map<String, FoursquareUser> friendsMap = Manager.createUserMap(friends);
		request.setAttribute("friendsMap", friendsMap);
	}

	Integer pageSize = 50;
	Integer limit = pageSize;
	Integer offset = 0;
	Integer totalCount = 0;

	if (pageNum != null && pageSize > 0) {
		offset = (pageNum - 1) * pageSize;
	}

	log.info("pageSize -> " + pageSize);
	log.info("limit -> " + limit);
	log.info("offset -> " + offset);
	log.info("totalCount" + totalCount);

	DAO dao = new CachingDAOImpl();

	String uri = request.getRequestURI();
	String[] tokens = uri.split("/");

	List<FoursquareUser> users = null;
	String pageTitle = null;
	String userType = null;
	if (uriString.startsWith("/brand")) {
		//Get The Current Brands
		totalCount = dao.getBrandIds().size();
		users = dao.getBrands(offset, limit);
		userType = "brands";
		pageTitle = "Brands";
	} else {
		//Get The Current Celebrities
		totalCount = dao.getCelebIds().size();
		users = dao.getCelebrities(offset, limit);

		userType = "celebs";
		pageTitle = "Celebrities";
	}

	log.info("title -> " + pageTitle +" -- userType -> " + userType+" -- users -> " + users);

	request.setAttribute("title", pageTitle);
	request.setAttribute("userType", userType);
	request.setAttribute("users", users);

	SimpleDateFormat dateFormat = ModelUtils.getDateFormat();

	log.info("limit -> " + limit + " -- offset -> " + offset+" -- page -> " + pageNum+" -- totalCount -> " + totalCount);

	request.setAttribute("limit", limit);
	request.setAttribute("offset", offset);
	request.setAttribute("page", pageNum);
	request.setAttribute("totalCount", totalCount);
%>
<foursquarebrands:html>
<foursquarebrands:head>
	<script type="text/javascript" src="/assets/js/basicfollow.js"></script>
</foursquarebrands:head>
<foursquarebrands:body>
	<c:set var="toNum" value="${offset+limit}" scope="request" />
	<c:if test="${toNum>totalCount}">
		<c:set var="toNum" value="${totalCount}" scope="request" />
	</c:if>
	<%-- See if we have a full list of brands --%>
	<c:choose>
		<c:when test="${offset>toNum}">
			<h1 class="align-center">Quit messing with the parameters!<br />
			<br />
			It's not going to work if you do that!</h1>
		</c:when>
		<%-- We have a full list of brands --%>
		<c:when test="${users!=null}">
			<h2 class="align-center">Top ${userType} by Follower Count,	showing ${offset+1}-${toNum} out of ${totalCount}</h2>
			<br />
			<c:set var="count" value="${offset+1}" scope="request" />
			<div class="leaderboard">
				<c:forEach var="user" items="${users}">
					<c:set var="user" value="${user}" scope="request"/>
					<foursquarebrands:user-row/>
					<c:set var="count" value="${count+1}" scope="request" />
				</c:forEach>
			</div>
			<br />
			<br />
			<div class="paging-controls">
				<div style="float: left;">
					<c:if test="${offset>0}">
						<h2><a href="?page=${page-1}">Previous Page</a></h2>
					</c:if>
				</div>
				<div style="float: right;">
					<c:if test="${(offset+limit)<totalCount}">
						<h2><a href="?page=${page+1}">Next Page</a></h2>
					</c:if>
				</div>
			</div>
		</c:when>
	</c:choose>
	<foursquarebrands:error-dialogs/>
</foursquarebrands:body>
</foursquarebrands:html>
