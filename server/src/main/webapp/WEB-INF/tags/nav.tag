<%@ tag isELIgnored="false" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="foursquarebrands" tagdir="/WEB-INF/tags"%>
<div id="nav" class="wrapper">
  <ul class="sf-menu">
    <li class="current">
      <a href="/" class="first-level">Home</a>
    </li>
    <li>
      <a href="/brands" class="first-level">Brands</a>
    </li>
    <li>
      <a href="/celebs" class="first-level">Celebrities</a>
    </li>
    <!--li>
      <a class="first-level">Manage</a>
	  <ul>
	    <li>
	      <a href="/manage/brands">Brands</a>
	    </li>
	    <li>
          <a href="/manage/celebs">Celebrities</a>
        </li>
      </ul>
    </li-->
  </ul>	
  <div style="float:right;">	
    <c:choose>
      <c:when test="${requestUri=='/'}">
        <!-- AddThis -->
        <script type="text/javascript">var addthis_config = {"data_track_clickback":true};</script>
        <script type="text/javascript" src="http://s7.addthis.com/js/250/addthis_widget.js#username=handstandtech"></script>
        <div class="addthis_toolbox addthis_default_style ">
          <a class="addthis_button_facebook_like" fb:like:layout="button_count"></a>
          <a class="addthis_button_tweet"></a>
          <a class="addthis_counter addthis_pill_style"></a>
        </div>
        <!--End AddThis -->
      </c:when>
      <c:otherwise>
        <ul class="sf-menu" style="position:relative;">
          <li>
            <p>
              <a href="http://twitter.com/4sqbrands" target="_blank" class="first-level">Foursquare Brands on Twitter</a>
            </p>
          </li>
        </ul>
      </c:otherwise>
    </c:choose>
    <div class="login-logout">
      <c:choose>
        <c:when test="${currentUser!=null}">
          <img style="border:1px solid black;margin:4px;float:left;" src="${currentUser.foursquareUser.photo}" width="32" height="32" />
          <ul class="sf-menu" style="position:relative;">
            <li>
              <p class="login-button">
                <a href="/logout" class="first-level">Logout</a>
              </p>
            </li>
          </ul>
        </c:when>
        <c:otherwise>
          <a href="/login">
            <img src="/assets/images/signinwith-foursquare.png" style="margin:0px;margin-top:8px;"> 
          </a>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</div>