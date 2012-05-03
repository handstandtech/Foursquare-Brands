<%@ page isELIgnored="false" trimDirectiveWhitespaces="true" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="foursquarebrands" tagdir="/WEB-INF/tags"%>
<foursquarebrands:html>
<foursquarebrands:head/>
<foursquarebrands:body>
	<h1>Cache Stats  <span style="float:right;">[<a href="?flush=true">FLUSH</a>]</span></h1>
	<p>Hits: ${stats.cacheHits}</p>
	<p>Misses: ${stats.cacheMisses}</p>
	<p>Object Count: ${stats.objectCount}</p>
</foursquarebrands:body>
</foursquarebrands:html>