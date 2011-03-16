<%@ tag isELIgnored="false" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="foursquarebrands" tagdir="/WEB-INF/tags/foursquarebrands"%>
<c:choose>
	<c:when test="${friendsMap[user.id]!=null}">
		<div>
			<div class="follow-button following" userid="${user.id}">
				<a href="javascript:void(0);" class="rounded-button">
					<span>UnFollow</span>
				</a>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<div>
			<div class="follow-button not-following"  userid="${user.id}">
				<a href="javascript:void(0);" class="rounded-button">
					<span>Follow</span>
				</a>
			</div>
		</div>
	</c:otherwise>
</c:choose>