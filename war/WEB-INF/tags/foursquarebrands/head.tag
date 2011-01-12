<%@ tag isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ attribute name="title"%>
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

<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta name="author" content="Handstand Technologies, LLC" />
<meta name="copyright" content="&copy; 2010 Handstand Technologies, LLC" />
<meta name="robots" content="all" />
<meta name="Description"
	content="Find and Manage The Brands You Follow on Foursquare." />
<meta name="Keywords"
	content="Foursquare, Brands, Finder, Brand" />
<c:choose>
	<c:when test="${title!=null}">
		<title>Foursquare Brands - ${title}</title>
	</c:when>
	<c:otherwise>
		<title>Foursquare Brands - Find and Manage The Brands You Follow on Foursquare.</title>
	</c:otherwise>
</c:choose>


<!-- jQuery -->
<script type="text/javascript" src="/assets/js/jquery/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="/assets/js/jquery-ui/jquery-ui-1.8.4.min.js"></script>

<!-- Style -->
<link type="text/css" rel="stylesheet" href="/assets/css/style.css" />

<!-- Open Graph Markup -->
<meta property="og:title" content="Foursquare Brands"/>
<meta property="og:type" content="website"/>
<meta property="og:url" content="http://www.foursquarebrands.com"/>
<meta property="og:image" content="http://www.foursquarebrands.com/assets/images/foursquare-brands-logo.png"/>
<meta property="og:site_name" content="Foursquare Brands"/>
<meta property="fb:admins" content="169406172091"/>
<meta property="og:description" content="Find and Manage The Brands You Follow on Foursquare."/>

<jsp:doBody />
	<!-- Begin Google Analytics -->
	<script type="text/javascript">
	
	  var _gaq = _gaq || [];

	  <c:if test="${production==true}">
	  _gaq.push(['_setAccount', 'UA-12000077-5']);
	  _gaq.push(['_setDomainName', 'none']);
	  _gaq.push(['_setAllowLinker', true]);
	  _gaq.push(['_trackPageview']);

	  (function() {
	    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
	    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
	    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
	  })();

	  </c:if>
	</script>
	<!-- End Google Analytics -->
	
</head>
