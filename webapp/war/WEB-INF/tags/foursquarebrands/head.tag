<%@ tag isELIgnored="false" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<head>
  <c:choose>
    <c:when test="${title!=null}">
      <title>Foursquare Brands - ${title}</title>
    </c:when>
    <c:otherwise>
      <title>Foursquare Brands - Find and Manage Foursquare Brands and Celebrities.</title>
    </c:otherwise>
  </c:choose>
  <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
  <meta name="author" content="Handstand Technologies, LLC" />
  <meta name="copyright" content="&copy; 2010 Handstand Technologies, LLC" />
  <meta name="robots" content="all" />
  
  <!-- Facebook Properties --> 
  <meta property="fb:admins" content="169406172091"> 
  <meta property="fb:app_id" content="169406172091"/> 
  
  <!-- jQuery -->
  <script type="text/javascript" src="/assets/js/jquery/jquery-1.5.1.min.js"></script>
  <script type="text/javascript" src="/assets/js/jquery-ui/jquery-ui-1.8.4.min.js"></script>
  
  <!-- Format Date -->
  <script type="text/javascript" src="/assets/js/formatdate.js"></script>
  
  <!-- Superfish -->
  <link rel="stylesheet" type="text/css" href="/assets/js/superfish-1.4.8/css/superfish.css" media="screen">
  <script type="text/javascript" src="/assets/js/superfish-1.4.8/js/hoverIntent.js"></script>
  <script type="text/javascript" src="/assets/js/superfish-1.4.8/js/superfish.js"></script>
  <script type="text/javascript">
    // initialize superfish
    $(document).ready(function(){
      $('ul.sf-menu').superfish();
      formatTimestamps();
    });
  </script>
  
	<link type="text/css" rel="stylesheet" href="/assets/js/fancybox/jquery.fancybox-1.3.4.css" />
	<script type="text/javascript" src="/assets/js/fancybox/jquery.fancybox-1.3.4.pack.js"></script>
	<script type="text/javascript" src="/assets/js/foursquarebrands.js"></script>
		
  
  
  <!-- Style -->
  <link type="text/css" rel="stylesheet" href="/assets/css/style.css" />
  <jsp:doBody />
</head>
