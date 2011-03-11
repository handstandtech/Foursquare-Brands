<%@ page isELIgnored="false" trimDirectiveWhitespaces="true"
	contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
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
	import="com.handstandtech.foursquare.shared.model.v2.FoursquareUser"%>
<%@ page
	import="com.handstandtech.foursquare.server.FoursquareConstants"%>
<%@ page import="com.handstandtech.foursquare.server.FoursquareHelper"%>
<%@ page import="com.handstandtech.brandfinder.shared.model.User"%>
<%@ page import="com.handstandtech.brandfinder.server.ParseCSV"%>
<%@ page import="com.handstandtech.brandfinder.server.DAO"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Collections"%>
<%@ page import="java.util.Comparator"%>
<%@ page import="org.slf4j.Logger"%>
<%@ page import="org.slf4j.LoggerFactory"%>

<%
	Logger log = LoggerFactory.getLogger(this.getClass());
	DAO dao = new DAO();

	String limitString = request.getParameter("limit");

	Integer limit = null;
	if (limitString != null) {
		if(limitString.equals("0")){
			limit=null;
		}else {
			try {
				limit = Integer.parseInt(limitString);
			} catch (Exception e) {
				log.warn(e.getMessage(), e);
			}
		}
	} else {
		limit = 50;
	}

	List<User> users = dao.getRecentlyActiveUsers(limit);
	request.setAttribute("users", users);
%>
<pf:html>
<pf:head>
	<script type="text/javascript"
		src="http://code.jquery.com/jquery-1.4.4.min.js"></script>
	<script type="text/javascript"
		src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.4/jquery-ui.min.js"></script>
</pf:head>
<pf:body>
	<table border="1">
		<tr>
			<th>Photo</th>
			<th>Name</th>
			<th>Location</th>
			<th>Friend Count</th>
			<th>Follower Count</th>
			<th>Badge Count</th>
			<th>Last Login</th>
		</tr>
		<c:forEach var="user" items="${users}">
			<c:set var="foursquareUser" value="${user.foursquareUser}"
				scope="request" />
			<tr style="border-bottom: 1px solid black;">
				<td><a href="http://foursquare.com/user/${user.id}"
					target="_blank"> <img src="${foursquareUser.photo}" width="50"
					height="50" /> </a></td>
				<td><span>${foursquareUser.name}</span></td>
				<td>${foursquareUser.homeCity}</td>
				<td>${foursquareUser.friends.count}</td>
				<td>${foursquareUser.friends.count}</td>
				<td>${foursquareUser.badges.count}</td>
				<td><fmt:formatDate type="both" value="${user.lastLogin}"
					timeZone="America/New_York" /></td>
			</tr>
		</c:forEach>
	</table>
</pf:body>
</pf:html>