<%@ page isELIgnored="false" language="java"
	contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pf" tagdir="/WEB-INF/tags/pf"%>
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
<%
	OAuthConsumer sessionConsumer = (OAuthConsumer) session.getAttribute(FoursquareConstants.CONSUMER_CONSTANT);
	if (sessionConsumer != null) {
		response.sendRedirect("/manage");
	}
%>
<pf:html>
<pf:head>
	<script type="text/javascript" src="/js/jquery-1.4.4.min.js"
		id="jquery"></script>
</pf:head>
<pf:body>
	<br/>
	<h1 class="align-center">Find and Manage The Brands You Follow on Foursquare</h1>
	<br/>
	<br/>
	<br/>
	<div class="feature-checklist">
		<ul>
			<li>
				<h3><img src="/images/check-button.png"/>Most Up to Date List of Brands on Foursquare</h3>
			</li>
			<li>
				<h3><img src="/images/check-button.png"/>Easiest Way to Manage Who You are Following</h3>
			</li>
			<li>
				<h3><img src="/images/check-button.png"/>Follow or UnFollow Brands with 1 Click</h3>
			</li>
		</ul>
		<hr style="margin:0px 15px;"/>
		<br/>
	<p class="align-center bold">Sign In with Foursquare to Get Started!</p>
	<p class="login-button"> 
		<a href="/foursquare/login">
			<img src="/images/signinwith-foursquare.png"/> 
		</a>
	</p>
	<br/>
	</div>
	<br/>
	<br/>
	<div class="lower-section">
	<a class="created-by" href="http://handstandtech.com"><img src="/images/createdby-handstandtech.png"/></a>
	<p style="">This application is not created by or affiliated with <a href="http://foursquare.com">Foursquare</a>, but rather your friends at <a href="http://handstandtech.com">Handstand Technologies</a>.  
	Feel free to ask questions and comments by hitting us up on <a href="http://twitter.com/HandstandTech"><img src="/images/twitter-small-logo.png" style=""/></a>.
	</p>
	</div>
	
</pf:body>
</pf:html>