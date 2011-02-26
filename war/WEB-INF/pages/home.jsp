<%@ page isELIgnored="false" language="java"
	contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="foursquarebrands" tagdir="/WEB-INF/tags/foursquarebrands"%>
<%@ page import="javax.jdo.PersistenceManager"%>
<%@page import="java.util.Collections"%>
<%@ page import="com.handstandtech.server.util.CF"%>
<%@ page import="com.handstandtech.server.SessionConstants"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="oauth.signpost.OAuthConsumer"%>
<%@ page import="oauth.signpost.basic.DefaultOAuthConsumer"%>
<%@ page
	import="com.handstandtech.foursquare.shared.model.FoursquareUser"%>
<%@ page
	import="com.handstandtech.foursquare.server.FoursquareConstants"%>
<%@ page import="com.handstandtech.foursquare.server.FoursquareHelper"%>
<foursquarebrands:html>
<foursquarebrands:head>
</foursquarebrands:head>
<foursquarebrands:body>
	<br/>
	<h1 class="align-center">Manage the Brands and Celebrities you Follow on Foursquare</h1>
	<br/>
	<br/>
	<br/>
	<div class="feature-checklist">
		<ul>
			<li>
				<h3><img src="/assets/images/check-button.png"/>Most Up to Date List of Brands and Celebrities</h3>
			</li>
			<li>
				<h3><img src="/assets/images/check-button.png"/>Easiest Way to Manage Who You are Following</h3>
			</li>
			<li>
				<h3><img src="/assets/images/check-button.png"/>Follow or UnFollow with 1 Click</h3>
			</li>
		</ul>
		<hr style="margin:0px 15px;"/>
		<br/>
		<c:choose>
			<c:when test="${currentUser==null}">
			<p class="align-center bold">Sign In with Foursquare to Get Started!</p>
			<p class="login-button"> 
				<a href="/login">
					<img src="/assets/images/signinwith-foursquare.png"/> 
				</a>
			</p>
			</c:when>
			<c:otherwise>
			<h2 class="align-center">Hey ${currentUser.foursquareUser.name}, You're Logged in!</h2>
			<br/>
			<h2 class="align-center">Start Managing <a href="/manage/brands">Brands</a> or <a href="/manage/celebs">Celebrities</a>.</h2>
			</c:otherwise>
		</c:choose>
		<br/>
	</div>
	<br/>
	<br/>
	
</foursquarebrands:body>
</foursquarebrands:html>