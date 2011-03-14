<%@ page isELIgnored="false" trimDirectiveWhitespaces="true" contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="traqmate" tagdir="/WEB-INF/tags/traqmate"%>
<%@ page import="com.handstandtech.memcache.CF"%>
<%@ page import="org.slf4j.Logger"%>
<%@ page import="org.slf4j.LoggerFactory"%>

<%
	Logger log = LoggerFactory.getLogger(this.getClass());

	String flush = request.getParameter("flush");
	if(flush!=null && flush.toLowerCase().equals("true")) {
		log.info("Clearing Cache");
		CF.clear();
		log.info("Redirecting back to normal cache page.");
		response.sendRedirect(request.getRequestURI());
	}

	request.setAttribute("stats", CF.getStats());
	//request.setAttribute("memcachekeys", CF.getKeys());

%>

<html>
<head>
</head>
<body>
<h1>Cache Stats  <span style="float:right;">[<a href="?flush=true">FLUSH</a>]</span></h1>
<p>Hits: ${stats.cacheHits}</p>
<p>Misses: ${stats.cacheMisses}</p>
<p>Object Count: ${stats.objectCount}</p>

<br/>

<h2>Keys:</h2>
<ul>
	<c:forEach var="key" items="${memcachekeys}">
		<li>${key}</li>
	</c:forEach>
</ul>
</body>
</html>