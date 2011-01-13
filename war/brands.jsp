<%@ page isELIgnored="false" language="java"
	contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="foursquarebrands"
	tagdir="/WEB-INF/tags/foursquarebrands"%>
<%@page import="com.handstandtech.server.SessionConstants"%>
<%@page import="java.text.DecimalFormat"%>
<%@ page import="javax.jdo.PersistenceManager"%>
<%@ page import="com.handstandtech.server.db.PMF"%>
<%@ page import="com.handstandtech.server.SessionConstants"%>
<%@ page import="com.handstandtech.server.RequestConstants"%>
<%@ page import="oauth.signpost.OAuthConsumer"%>
<%@ page import="oauth.signpost.basic.DefaultOAuthConsumer"%>
<%@ page import="com.handstandtech.foursquare.shared.model.v2.FoursquareUser"%>
<%@ page import="com.handstandtech.foursquare.server.FoursquareConstants"%>
<%@ page import="com.handstandtech.foursquare.server.FoursquareHelper"%>
<%@ page import="com.handstandtech.brandfinder.server.tasks.FollowerCountTaskServlet"%>
<%@ page import="com.handstandtech.brandfinder.server.ParseCSV"%>
<%@ page import="com.handstandtech.brandfinder.server.DAO"%>
<%@ page import="com.handstandtech.brandfinder.server.util.PageLoadUtils"%>
<%@ page import="com.handstandtech.brandfinder.server.util.SessionHelper"%>
<%@ page import="com.handstandtech.brandfinder.shared.model.DailyFollowEventCount"%>
<%@ page import="com.handstandtech.brandfinder.shared.model.DailyFollowerCount"%>
<%@ page import="com.handstandtech.brandfinder.shared.util.ModelUtils"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>
<%@ page import="com.handstandtech.shared.model.rest.RESTResult"%>
<%@ page import="com.handstandtech.foursquare.server.FoursquareUtils"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Collections"%>
<%@ page import="java.util.Comparator"%>
<%@ page import="java.io.InputStream"%>
<%@ page import="java.io.BufferedReader"%>
<%@ page import="java.io.InputStreamReader"%>
<%
	DAO dao = new DAO();
	
	String brandId = request.getParameter("id");
	request.setAttribute("brandId", brandId);
	
	if(brandId!=null){
		FoursquareUser brand = dao.getFoursquareUser(brandId);
		if(brand.getType().equals("brand")) {
			request.setAttribute("brand", brand);
		}
	}else {
		List<FoursquareUser> brands = dao.getBrands();
		Collections.sort(brands, new Comparator<FoursquareUser>(){
			public int compare(FoursquareUser one, FoursquareUser two) {
				return two.getFriends().getCount().compareTo(one.getFriends().getCount());
			}
		});
		request.setAttribute("brands", brands);
	}

	SimpleDateFormat dateFormat = ModelUtils.getDateFormat();
	
	Date start = null;
	Date end = null;
	
	List<DailyFollowEventCount> counts = dao.getDailyFollowEventsForBrand(brandId, start, end);
	request.setAttribute("counts", counts);

	List<DailyFollowerCount> followerCounts = dao.getDailyFollowCountsForBrand(brandId, start, end);
	request.setAttribute("followerCounts", followerCounts);
%>
<foursquarebrands:html>
	<foursquarebrands:head>
		<script type='text/javascript' src='https://www.google.com/jsapi'></script>
	    <script type='text/javascript'>
	      google.load('visualization', '1', {'packages':['annotatedtimeline']});
	      google.setOnLoadCallback(drawChart);
	      function drawChart() {
	        var data = new google.visualization.DataTable();
	        data.addColumn('date', 'Date');
	        data.addColumn('number', 'Follow Count');
	        data.addColumn('string', 'title1');
	        data.addColumn('string', 'text1');
	        data.addColumn('number', 'UnFollow Count');
	        data.addColumn('string', 'title2');
	        data.addColumn('string', 'text2');
	        data.addRows([
	        	<c:forEach var="eventCount" items="${counts}">
	        		[new Date(<fmt:formatDate pattern="yyyy, MM, dd"	value="${eventCount.date}" />), ${eventCount.totalFollowCount}, undefined, undefined, ${eventCount.totalUnFollowCount}, undefined, undefined],
	        	</c:forEach>
	        ]);
	
	        var chart = new google.visualization.AnnotatedTimeLine(document.getElementById('chart_div'));
	        chart.draw(data, {displayAnnotations: true});
	        
	        drawChart2();
	      }
	      
	      function drawChart2() {
		        var data = new google.visualization.DataTable();
		        data.addColumn('date', 'Date');
		        data.addColumn('number', 'Follower Count');
		        data.addColumn('string', 'title1');
		        data.addColumn('string', 'text1');
		        data.addRows([
		        	<c:forEach var="snapshot" items="${followerCounts}">
		        		[new Date(<fmt:formatDate pattern="yyyy, MM, dd" value="${snapshot.date}" />), ${snapshot.count}, undefined, undefined],
		        	</c:forEach>
		        ]);
		
		        var chart = new google.visualization.AnnotatedTimeLine(document.getElementById('chart_div2'));
		        chart.draw(data, {displayAnnotations: true});
		      }
	    </script>
	</foursquarebrands:head>
	<foursquarebrands:body>
		<c:choose>
			<c:when test="${brand!=null}">
				<c:choose>
					<c:when test="${brand.lastName!=null}">
						<c:set var="brandName" value="${brand.firstName} ${brand.lastName}" />
					</c:when>
					<c:otherwise>
						<c:set var="brandName" value="${brand.firstName}" />
					</c:otherwise>
				</c:choose>
				<h3><a href="/brands.jsp">Back to ALL Brands</a></h3>
				<br/>
				<h1>${brandName}</h1>
				<br/>
				<img src="${brand.photo}"/>
				<br/>
				<br/>	
				<h2>Follow Events (Google Analytics)</h2>
				<table border="1">
					<tr>
						<th>Date</th>
						<th>Unique Follow</th>
						<th>Total Follow</th>
						<th>Unique UnFollow</th>
						<th>Total UnFollow</th>
					</tr>
					<c:forEach var="eventCount" items="${counts}">
						<tr>
							<td>${eventCount.date}</td>
							<td>${eventCount.uniqueFollowCount}</td>
							<td>${eventCount.totalFollowCount}</td>
							<td>${eventCount.uniqueUnFollowCount}</td>
							<td>${eventCount.totalUnFollowCount}</td>
						</tr>
					</c:forEach>
				</table>
				
				<br/>
				<br/>
				<div id='chart_div' style='width: 700px; height: 240px;'></div>
				<br/>
				<hr/>
				<br/>
				<h2>Daily Snapshot of # of Followers</h2>
				<table border="1">
					<tr>
						<th>Date</th>
						<th>Follower Count</th>
					</tr>
					<c:forEach var="snapshot" items="${followerCounts}">
						<tr>
							<td>${snapshot.date}</td>
							<td>${snapshot.count}</td>
						</tr>
					</c:forEach>
				</table>
				
				<br/>
				<br/>
				<div id='chart_div2' style='width: 700px; height: 240px;'></div>
			</c:when>
			<c:otherwise>
				<%-- See if we have a full list of brands --%>
				<c:choose>
					<%-- We have a full list of brands --%>
					<c:when test="${brands!=null}">
						<table border="1">
							<tr>
								<th>Photo</th>
								<th>Name</th>
								<th>Friend Count</th>
								<th>Analytics Link</th>
							</tr>
							<c:forEach var="brand" items="${brands}">
								<tr style="border-bottom:1px solid black;">
									<td><img src="${brand.photo}" width="50" height="50"/></td>
									<td>
										<c:choose>
											<c:when test="${brand.lastName!=null}">
												<span>${brand.firstName} ${brand.lastName}</span>
											</c:when>
											<c:otherwise>
												<span>${brand.firstName}</span>
											</c:otherwise>
										</c:choose>
									</td>
									<td>${brand.friends.count}</td>
									<td><a href="/brands.jsp?id=${brand.id}">Analytics</a></td>
								</tr>
							</c:forEach>
						</table>
					</c:when>
					<%-- We have no brand or brands, means we couldn't find anything --%>
					<c:otherwise>
						<h1>Could Not Find Brand: ${brandId}</h1>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
	</foursquarebrands:body>
</foursquarebrands:html>
