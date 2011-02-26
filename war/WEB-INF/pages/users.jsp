<%@ page isELIgnored="false" language="java"
	contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="foursquarebrands"
	tagdir="/WEB-INF/tags/foursquarebrands"%>
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
<%@ page import="com.handstandtech.brandfinder.shared.model.DailyFollowEventCount"%>
<%@ page import="com.handstandtech.brandfinder.shared.model.DailyFollowerCount"%>
<%@ page import="com.handstandtech.brandfinder.shared.util.ModelUtils"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>
<%@ page import="com.handstandtech.shared.model.rest.RESTResult"%>
<%@ page import="com.handstandtech.foursquare.server.FoursquareUtils"%>
<%@ page import="com.googlecode.objectify.Query"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Collections"%>
<%@ page import="java.util.Comparator"%>
<%@ page import="java.io.InputStream"%>
<%@ page import="java.io.BufferedReader"%>
<%@ page import="java.io.InputStreamReader"%>
<%
	String pageStr = request.getParameter("page");
	Integer pageNum = 1;
	try {
		pageNum = Integer.parseInt(pageStr);
	} catch (Exception e){
		//Do Nothing
	}
	
	Integer pageSize = 20;
	Integer limit=pageSize;
	Integer offset=0;
	Integer totalCount = 0;
	
	if(pageNum!=null && pageSize>0) {
		offset=(pageNum-1)*pageSize;
	}


	DAO dao = new DAO();

	String uri = request.getRequestURI();
	String[] tokens = uri.split("/");
			
	List<FoursquareUser> users = null;		
		String uriString = request.getRequestURI();
		System.out.println(uriString);
		String userType=null;
		if(uriString.startsWith("/brand")){
			//Get The Current Brands
			Query brandQuery = dao.createBrandQuery();
			totalCount = brandQuery.count();
			dao.addLimitAndOffset(brandQuery, limit, offset);
			users=brandQuery.list();
			userType="brands";
		} else {
			//Get The Current Celebrities
			
			Query celebQuery = dao.createCelebQuery();
			totalCount = celebQuery.count();
			dao.addLimitAndOffset(celebQuery, limit, offset);
			users=celebQuery.list();
			
			userType="celebs";
		}
		request.setAttribute("userType", userType);
		Collections.sort(users, new Comparator<FoursquareUser>(){
			public int compare(FoursquareUser one, FoursquareUser two) {
				return two.getFriends().getCount().compareTo(one.getFriends().getCount());
			}
		});
		request.setAttribute("users", users);

	SimpleDateFormat dateFormat = ModelUtils.getDateFormat();
	
	
	/*Integer pageSize = 10;
	Integer limit=pageSize;
	Integer offset=0;
	Integer totalCount = 0;*/
	request.setAttribute("limit", limit);
	request.setAttribute("offset", offset);
	request.setAttribute("page", pageNum);
	request.setAttribute("totalCount", totalCount);
%>
<foursquarebrands:html>
	<foursquarebrands:head>
	</foursquarebrands:head>
	<foursquarebrands:body>
		<%-- See if we have a full list of brands --%>
		<c:choose>
			<%-- We have a full list of brands --%>
			<c:when test="${users!=null}">
				<c:set var="count" value="${offset+1}" scope="request"/>
				<div class="leaderboard">
					<c:forEach var="user" items="${users}">
						<ul>
							<li class="rank">
								<h1>${count}</h1>
							</li>
							<li class="photo">
								<a href="/${userType}/${user.id}"><img src="${user.photo}" width="100" height="100"/></a>
							</li>
							<li class="name">
								<h1>${user.name}</h1>
							</li>
							<li class="twitter">
								<h3>
									<c:set var="twitter" value="${user.contact.twitter}"/>
									<c:if test="${twitter!=null && twitter!='' }">
										<a href="http://twitter.com/${twitter}" target="_blank">@${twitter}</a>
									</c:if>
								</h3>
							</li>
							<li class="follower-count">
								<h1>${user.friends.count}</h1>
							</li>
						</ul>
						<c:set var="count" value="${count+1}" scope="request"/>
					</c:forEach>
				</div>
			</c:when>
			<%-- We have no brand or brands, means we couldn't find anything --%>
			<c:otherwise>
				<h1>Could Not Find Brand: ${brandId}</h1>
			</c:otherwise>
		</c:choose>
		<br/>
		<br/>
		<div class="paging-controls">
			<div style="float:left;">
				<c:if test="${offset>0}">
					<h3>
						<a href="?page=${page-1}">Previous Page</a>
					</h3>
				</c:if>
			</div>
			<div style="float:right;">
				<c:if test="${(offset+limit)<totalCount}">
					<h3>
						<a href="?page=${page+1}">Next Page</a>
					</h3>
				</c:if>
			</div>
		</div>
	</foursquarebrands:body>
</foursquarebrands:html>
