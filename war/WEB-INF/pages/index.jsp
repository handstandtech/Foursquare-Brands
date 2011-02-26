<%@ page isELIgnored="false" language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<jsp:include page="/WEB-INF/pages/home.jsp"/>

<%--c:choose>
	<c:when test="${currentUser!=null}">
		<jsp:include page="/WEB-INF/pages/manage.jsp"/>
	</c:when>
	<c:otherwise>
		<jsp:include page="/WEB-INF/pages/home.jsp"/>
	</c:otherwise>
</c:choose--%>