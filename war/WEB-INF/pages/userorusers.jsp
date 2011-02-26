<%@ page isELIgnored="false" language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
	String uriString = request.getRequestURI();
	String tokens[] = uriString.split("/");
	System.out.println("Tokens: " + tokens.length);
	request.setAttribute("tokenCount", tokens.length);
%>

<c:choose>
	<c:when test="${tokenCount<=2}">
		<jsp:include page="/WEB-INF/pages/users.jsp"/>
	</c:when>
	<c:otherwise>
		<jsp:include page="/WEB-INF/pages/user.jsp"/>
	</c:otherwise>
</c:choose>