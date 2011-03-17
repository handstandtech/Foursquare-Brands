<%@ page isELIgnored="false" trimDirectiveWhitespaces="true"
	contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="foursquarebrands"
	tagdir="/WEB-INF/tags"%>
<foursquarebrands:html>
<foursquarebrands:head>
	<meta name="Description"
		content="Find and Manage Foursquare Brands and Celebrities" />
	<meta name="Keywords"
		content="Foursquare Brands,Foursquare,Brands,FoursquareBrands,Brand,Celebs,Celebrities,Celebs,Celeb" />

	<!-- Facebook Properties -->
	<meta property="og:site_name" content="FoursquareBrands.com" />
	<meta property="og:title" content="Foursquare Brands" />
	<meta property="og:url" content="http://www.foursquarebrands.com" />
	<meta property="og:image"
		content="http://www.foursquarebrands.com/assets/images/foursquare-brands-logo.png" />
	<meta property="og:description"
		content="Find and Manage Foursquare Brands and Celebrities" />

	<script type="text/javascript" src="/assets/js/basicfollow.js"></script>
</foursquarebrands:head>
<foursquarebrands:body>
	<h1 class="align-center">Hey
	${currentUser.foursquareUser.firstName}, you are logged in!<br />
	Here are the newest Brands and Celebrities!</h1>
	<br />
	<br />
	<c:if test="${newBrands!=null}">
		<c:set var="userType" value="brands" scope="request" />
		<h2 class="align-center">${numNew} Newest Brands</h2>
		<br />
		<c:set var="count" value="${1}" scope="request" />
		<div class="leaderboard"><c:forEach var="brand"
			items="${newBrands}">
			<c:set var="user" value="${foursquareUsersMap[brand.brandId]}"
				scope="request" />
			<foursquarebrands:user-row />
			<c:set var="count" value="${count+1}" scope="request" />
		</c:forEach></div>
	</c:if>
	<br />
	<br />
	<c:if test="${newCelebs!=null}">
		<c:set var="userType" value="celebs" scope="request" />
		<h2 class="align-center">${numNew} Newest Celebrities</h2>
		<br />
		<c:set var="count" value="${1}" scope="request" />
		<div class="leaderboard"><c:forEach var="celeb"
			items="${newCelebs}">
			<c:set var="user" value="${foursquareUsersMap[celeb.brandId]}"
				scope="request" />
			<foursquarebrands:user-row />
			<c:set var="count" value="${count+1}" scope="request" />
		</c:forEach></div>
	</c:if>
	<foursquarebrands:error-dialogs />
</foursquarebrands:body>
</foursquarebrands:html>