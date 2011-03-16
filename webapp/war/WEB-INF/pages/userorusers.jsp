<%@ page isELIgnored="false" trimDirectiveWhitespaces="true" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="org.slf4j.Logger"%>
<%@ page import="org.slf4j.LoggerFactory"%>
<%
	String uriString = request.getRequestURI();
	Logger log = LoggerFactory.getLogger(this.getClass());
	String tokens[] = uriString.split("/");
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