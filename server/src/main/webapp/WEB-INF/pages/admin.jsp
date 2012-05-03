<%@ page isELIgnored="false" trimDirectiveWhitespaces="true"
	contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="foursquarebrands" tagdir="/WEB-INF/tags"%>
<foursquarebrands:html>
<foursquarebrands:head>
	<script type="text/javascript"
		src="http://code.jquery.com/jquery-1.4.4.min.js"></script>
	<script type="text/javascript"
		src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.4/jquery-ui.min.js"></script>
</foursquarebrands:head>
<foursquarebrands:body>
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
</foursquarebrands:body>
</foursquarebrands:html>