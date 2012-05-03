<%@ tag isELIgnored="false" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="foursquarebrands" tagdir="/WEB-INF/tags"%>
<ul>
	<li class="rank">
		<h2>${count}</h2>
	</li>
	<li class="photo">
		<a href="/${userType}/${user.id}">
			<img src="${user.photo}" width="60" height="60" />
		</a>
	</li>
	<li class="name">
		<a href="/${userType}/${user.id}" style="text-decoration:none;color:#000000;">
			<h2>${user.name}</h2>
		</a>
	</li>
	
	<li class="twitter">
		<h4>
			<c:set var="twitter" value="${user.contact.twitter}" />
			<c:if test="${twitter!=null && twitter!='' }">
				<a href="http://twitter.com/${twitter}" target="_blank">@${twitter}</a>
			</c:if>
		</h4>
	</li>
	<c:if test="${currentUser!=null}">
		<li class="the-button">
			<c:set var="user" value="${user}" scope="request"/>
			<foursquarebrands:followunfollowbutton/>
		</li>
	</c:if>
	<li class="follower-count">
		<h2>${user.followers.count}</h2>
	</li>
</ul>