<%@ tag isELIgnored="false" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="foursquarebrands" tagdir="/WEB-INF/tags" %>
<c:set var="user" value="${userMap[id]}"/>
<c:if test="${user.id!=null}">
    <div class="user-info" name='${user.firstName}' userid='${user.id}'>
        <a href="/${userType}/${user.id}" class="foursquare-photo-link">
            <c:choose>
                <c:when test="${fn:startsWith(user.photo, 'http')}">
                    <img class="photo" src='${user.photo}'/>
                </c:when>
                <c:otherwise>
                    <img class="photo" src='${user.photo.prefix}64x64${user.photo.suffix}'/>
                </c:otherwise>
            </c:choose>
        </a>
        <a href="http://foursquare.com/user/${user.id}" class="foursquare-link hovercard" style="display: none;"
           target="_blank">
            <span class="brand-name">${user.name}</span>
        </a>
        <a class="follow-unfollow-link hovercard" href="javascript:void(0)" style="display: none;">
            <span></span>
        </a>
    </div>
</c:if>