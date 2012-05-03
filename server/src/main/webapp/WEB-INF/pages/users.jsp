<%@ page isELIgnored="false" trimDirectiveWhitespaces="true" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="foursquarebrands" tagdir="/WEB-INF/tags"%>
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
