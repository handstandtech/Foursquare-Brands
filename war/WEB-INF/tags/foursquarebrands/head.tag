<%@ tag isELIgnored="false" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="foursquarebrands" tagdir="/WEB-INF/tags/foursquarebrands"%>
<%@ tag import="com.google.appengine.api.utils.SystemProperty"%>

<%
	//See if we are in production mode
	String baseUrl = request.getRequestURL().toString();
	try {
		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
			request.setAttribute("production", true);
		}
	} catch (Exception e) {
		// Do nothing
	}
%>
<head>
	<c:choose>
		<c:when test="${title!=null}">
			<title>Foursquare Brands - ${title}</title>
		</c:when>
		<c:otherwise>
			<title>Foursquare Brands - Find and Manage The Brands You Follow on Foursquare.</title>
		</c:otherwise>
	</c:choose>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
	<meta name="author" content="Handstand Technologies, LLC" />
	<meta name="copyright" content="&copy; 2010 Handstand Technologies, LLC" />
	<meta name="robots" content="all" />
	<meta name="Description" content="Find and Manage The Brands You Follow on Foursquare." />
	<meta name="Keywords" content="Foursquare, Brands, Finder, Brand" />
	
	<!-- Facebook Properties --> 
	<meta property="fb:admins" content="169406172091"> 
	<meta property="fb:app_id" content="169406172091"/> 
	<meta property="og:site_name" content="Foursquare Brands"/> 
	<meta property="og:title" content="Foursquare Brands"/> 
	<meta property="og:url" content="http://www.foursquarebrands.com"/> 
	<meta property="og:image" content="http://www.foursquarebrands.com/assets/images/foursquare-brands-logo.png"/> 
	<meta property="og:description" content="Find and Manage The Brands You Follow on Foursquare."/> 
	
	<!-- jQuery -->
	<script type="text/javascript" src="/assets/js/jquery/jquery-1.5.1.min.js"></script>
	<script type="text/javascript" src="/assets/js/jquery-ui/jquery-ui-1.8.4.min.js"></script>
	
	<!-- Superfish -->
	<link rel="stylesheet" type="text/css" href="/assets/js/superfish-1.4.8/css/superfish.css" media="screen">
	<script type="text/javascript" src="/assets/js/superfish-1.4.8/js/hoverIntent.js"></script>
	<script type="text/javascript" src="/assets/js/superfish-1.4.8/js/superfish.js"></script>
	<script type="text/javascript">
		// initialize superfish
		$(document).ready(function(){
			$('ul.sf-menu').superfish();
		});
	</script>
	
	<!-- Style -->
	<link type="text/css" rel="stylesheet" href="/assets/css/style.css" />
	
	<jsp:doBody />
</head>
