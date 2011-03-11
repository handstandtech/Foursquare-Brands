<%@ page isELIgnored="false" trimDirectiveWhitespaces="true" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="foursquarebrands" tagdir="/WEB-INF/tags/foursquarebrands"%>
<%@ page import="com.handstandtech.server.SessionConstants"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="javax.jdo.PersistenceManager"%>
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

	String uri = request.getRequestURI();
	String[] tokens = uri.split("/");
	
	String userId = null;
	if(tokens.length==3){
		userId = tokens[2];
		request.setAttribute("userId", userId);
	}
	
	if(userId!=null){
		FoursquareUser brand = dao.getFoursquareUser(userId);
		request.setAttribute("user", brand);
	}

	SimpleDateFormat dateFormat = ModelUtils.getDateFormat();
	
	Date start = null;
	Date end = null;
	
	List<DailyFollowEventCount> counts = dao.getDailyFollowEventsForBrand(userId, start, end);
	request.setAttribute("counts", counts);

	List<DailyFollowerCount> followerCounts = dao.getDailyFollowCountsForBrand(userId, start, end);
	request.setAttribute("followerCounts", followerCounts);
	
	SimpleDateFormat yearFormat=new SimpleDateFormat("yyyy");
	SimpleDateFormat monthFormat=new SimpleDateFormat("MM");
	SimpleDateFormat dayFormat=new SimpleDateFormat("dd");
	
	StringBuffer chart1Rows = new StringBuffer();
	for(DailyFollowEventCount count : counts) {
		Long followCount = count.getTotalFollowCount();
		Long unfollowCount = count.getTotalUnFollowCount();
		
		Date date = count.getDate();
		String year = yearFormat.format(date);
		String month = monthFormat.format(date);
		month = new Integer(Integer.parseInt(month)-1).toString();
		String day = dayFormat.format(date);
		chart1Rows.append("[new Date("+year+", " + month + ", "+day+"), "+followCount+", null, null, "+unfollowCount+", null, null],");
	}
	request.setAttribute("chart1Rows", chart1Rows.toString());
	
	StringBuffer chart2Rows = new StringBuffer();
	Long minFollowerCount = Long.MAX_VALUE;
	for(DailyFollowerCount dfc : followerCounts) {
		Long count = dfc.getCount();
		if(count!=0){
			if(count<minFollowerCount){ 
				minFollowerCount=count;
			}
		}
		
		Date date = dfc.getDate();
		String year = yearFormat.format(date);
		String month = monthFormat.format(date);
		month = new Integer(Integer.parseInt(month)-1).toString();
		String day = dayFormat.format(date);
		chart2Rows.append("[new Date("+year+", " + month + ", "+day+"), "+count+", null, null],");
	}
	request.setAttribute("chart2Rows", chart2Rows.toString());
	request.setAttribute("minFollowerCount", minFollowerCount);
%>
<c:set var="twitter" value="${user.contact.twitter}" scope="request"/>
<c:if test="${twitter==''}">
	<c:set var="twitter" value="${null}" scope="request"/>
</c:if>
<c:set var="facebook" value="${user.contact.facebook}" scope="request"/>
<c:if test="${facebook==''}">
	<c:set var="facebook" value="${null}" scope="request"/>
</c:if>
<foursquarebrands:html>
	<c:set var="title" value="${user.name}" scope="request"/>
	<foursquarebrands:head>
		<script type='text/javascript' src='https://www.google.com/jsapi'></script>
	    <script type='text/javascript'>
	      google.load('visualization', '1', {'packages':['annotatedtimeline']});
	      google.setOnLoadCallback(drawCharts);

	      function drawCharts() {
	    	  <c:if test="${counts!=null && fn:length(counts)>0}">drawChart();</c:if>
	    	  <c:if test="${followerCounts!=null && fn:length(followerCounts)>0}">drawChart2();</c:if>
	      }
			
	      function drawChart() {
	        var data = new google.visualization.DataTable();
	        data.addColumn('date', 'Date');
	        data.addColumn('number', 'Follow Count');
	        data.addColumn('string', 'title1');
	        data.addColumn('string', 'text1');
	        data.addColumn('number', 'UnFollow Count');
	        data.addColumn('string', 'title2');
	        data.addColumn('string', 'text2');
	        data.addRows([${chart1Rows}]);
	
	        var chart = new google.visualization.AnnotatedTimeLine(document.getElementById('chart_div'));
	        chart.draw(data, {displayAnnotations: true, min: ${minFollowerCount}});
	        
	      }
	      
	      function drawChart2() {
		        var data = new google.visualization.DataTable();
		        data.addColumn('date', 'Date');
		        data.addColumn('number', 'Follower Count');
		        data.addColumn('string', 'title1');
		        data.addColumn('string', 'text1');
		        data.addRows([${chart2Rows}]);
		        var chart = new google.visualization.AnnotatedTimeLine(document.getElementById('chart_div2'));
		        chart.draw(data, {displayAnnotations: true});
		      }
	    </script>
	</foursquarebrands:head>
	<foursquarebrands:body>
		<c:choose>
			<c:when test="${user!=null}">
				<div style="overflow:hidden;margin-bottom:10px;">
					<div style="float:left;">
						<h1>${user.name} (${user.followers.count} Followers)</h1>
						<br/>
						<h2>${user.homeCity}</h2>
						<br/>
						<p>Last Update: <span class="timestamp" time="${user.lastUpdate.time}"></span></p>
					</div>
					<div class="profiles">
						<div>
							<c:if test="${twitter!=null}">
								<div class="twitter">
									<a href="http://twitter.com/${twitter}" target="_blank"><img class="user-image" src="http://img.tweetimag.es/i/${twitter}_o" style="border:1px solid #333333;"/></a>
									<br/>
									<br/>
									<a href="http://twitter.com/${twitter}" target="_blank">
										<img src="/assets/images/social-network-icons/twitter_32.png"/>
									</a>
								</div>
							</c:if>	
							<div class="foursquare">
								<a href="http://foursquare.com/user/${user.id}" target="_blank"><img class="user-image" src="${user.photo}" style="border:1px solid #333333;"/></a>
								<br/>
								<br/>
								<a href="http://foursquare.com/user/${user.id}" target="_blank">
									<img src="/assets/images/social-network-icons/foursquare_32.png"/>
								</a>
							</div>
							<c:if test="${facebook!=null}">
								<div class="facebook">
									<a href="http://facebook.com/profile.php?id=${facebook}" target="_blank"><img class="user-image" src="https://graph.facebook.com/${user.contact.facebook}/picture?type=large" style="border:1px solid #333333;"/></a>
									<br/>
									<br/>
									<a href="http://facebook.com/profile.php?id=${facebook}" target="_blank">
										<img src="/assets/images/social-network-icons/facebook_32.png"/>
									</a>
								</div>
							</c:if>
						</div>
					</div>
				</div>
					<c:if test="${followerCounts!=null && fn:length(followerCounts)>0}">
						<br/>
						<div class="chart-div">
							<h2>Number of Followers over Time</h2>
							<br/>
							<%--table border="1">
								<tr>
									<th>Date</th>
									<th>Follower Count</th>
								</tr>
								<c:forEach var="snapshot" items="${followerCounts}">
									<tr>
										<td><fmt:formatDate pattern="yyyy-MM-dd" value="${snapshot.date}" /></td>
										<td>${snapshot.count}</td>
									</tr>
								</c:forEach>
							</table--%>
							<div id='chart_div2' style='width: 1000px; height: 400px;'></div>
						</div>
						<br/>
						<hr/>
					</c:if>
					<c:if test="${counts!=null && fn:length(counts)>0}">
						<br/>
						<div class="chart-div">
							<h2>Number of Follows and UnFollows from FoursquareBrands.com</h2>
							<br/>
							<%--table border="1">
								<tr>
									<th>Date</th>
									<th>Unique Follow</th>
									<th>Total Follow</th>
									<th>Unique UnFollow</th>
									<th>Total UnFollow</th>
								</tr>
								<c:forEach var="eventCount" items="${counts}">
									<tr>
										<td><fmt:formatDate pattern="yyyy-MM-dd" value="${eventCount.date}" /></td>
										<td>${eventCount.uniqueFollowCount}</td>
										<td>${eventCount.totalFollowCount}</td>
										<td>${eventCount.uniqueUnFollowCount}</td>
										<td>${eventCount.totalUnFollowCount}</td>
									</tr>
								</c:forEach>
							</table--%>
							<div id='chart_div' style='width: 1000px; height: 400px;'></div>
							<br/>
						</div>
					</c:if>
				<br/>	
			</c:when>
		</c:choose>
	</foursquarebrands:body>
</foursquarebrands:html>
