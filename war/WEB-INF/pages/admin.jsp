<%@ page isELIgnored="false" language="java"
	contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="pf" tagdir="/WEB-INF/tags/pf"%>
<%@page import="com.handstandtech.server.SessionConstants"%>
<%@page import="java.text.DecimalFormat"%>
<%@ page import="javax.jdo.PersistenceManager"%>
<%@ page import="com.handstandtech.server.db.PMF"%>
<%@ page import="com.handstandtech.server.SessionConstants"%>
<%@ page import="com.handstandtech.server.RequestConstants"%>
<%@ page import="oauth.signpost.OAuthConsumer"%>
<%@ page import="oauth.signpost.basic.DefaultOAuthConsumer"%>
<%@ page
	import="com.handstandtech.foursquare.shared.model.v2.FoursquareUser"%>
<%@ page
	import="com.handstandtech.foursquare.server.FoursquareConstants"%>
<%@ page import="com.handstandtech.foursquare.server.FoursquareHelper"%>
<%@ page import="com.handstandtech.brandfinder.server.ParseCSV"%>
<%@ page import="com.handstandtech.brandfinder.server.DAO"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Collections"%>
<%@ page import="java.util.Comparator"%>

<%
	OAuthConsumer consumer = (OAuthConsumer)session.getAttribute(FoursquareConstants.CONSUMER_CONSTANT);
	
	DAO dao = new DAO();
	List<FoursquareUser> brands = dao.getBrands();
	Collections.sort(brands, new Comparator<FoursquareUser>(){
		public int compare(FoursquareUser one, FoursquareUser two) {
			return two.getFriends().getCount().compareTo(one.getFriends().getCount());
		}
	});
	
	request.setAttribute("brands", brands);
	
%>
<pf:html>
<pf:head>
	<script type="text/javascript"
		src="http://code.jquery.com/jquery-1.4.4.min.js"></script>
	<script type="text/javascript"
		src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.4/jquery-ui.min.js"></script>
</pf:head>
<pf:body>
	<table border="1">
	<c:forEach var="brand"
		items="${brands}">
		<tr style="border-bottom:1px solid black;">
			<td><img src="${brand.photo}" width="50" height="50"/></td>
			<td><c:choose>
			<c:when test="${brand.lastName!=null}">
				<span>  ${brand.firstName} ${brand.lastName}  </span>
			</c:when>
			<c:otherwise>
				<span>${brand.firstName}</span>
			</c:otherwise>
		</c:choose> </td>
			<td>${brand.friends.count}</td>
		</tr>
	</c:forEach>
	</table>
</pf:body>
</pf:html>