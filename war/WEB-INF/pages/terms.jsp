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
	<h1>Terms</h1>
	<br/>
	<p>We honor your privacy as a user.  When you sign in with Foursquare, we only request a list of your friends in order to show who you are/are not following.  Only the information about users which can be publicly "followed" (Brands and Celebrities) are stored to be displayed on this site.<br/><br/>For questions and comments, please contact us on <a href="http://twitter.com/4sqbrands">Twitter</a> or by <a href="mailto:foursquarebrands@handstandtech.com">Email</a>.</p> 

	
</foursquarebrands:body>
</foursquarebrands:html>