<%@ page isELIgnored="false" language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:choose>
	<%-- When Logged In --%>
	<c:when test="${foursquare_consumer!=null}">
		<jsp:include page="/WEB-INF/pages/manage.jsp"/>
	</c:when>
	<%-- When NOT Logged In --%>
	<c:otherwise>
		<jsp:include page="/WEB-INF/pages/home.jsp"/>
	</c:otherwise>
</c:choose>