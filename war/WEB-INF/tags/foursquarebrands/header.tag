<%@ tag isELIgnored="false" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="foursquarebrands" tagdir="/WEB-INF/tags/foursquarebrands"%>
<header id="header" class="wrapper">
	<a href="/" style="float:left;">
		<img id="logo" src="/assets/images/foursquare-brands-logo.png">
		<h1 style="display:none;">Foursquare Brands</h1>
	</a>
	<div style="float:right;">
		<foursquarebrands:leaderboard-ad/>
	</div>
</header>
