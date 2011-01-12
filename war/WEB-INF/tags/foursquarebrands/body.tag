<%@ tag isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div id="wrapper">
			<header id="header">
				<a href="/" style="float:left;">
					<img id="logo" src="/assets/images/foursquare-brands-logo.png">
					<h2 style="display:none;">Foursquare Brands</h2>
				</a>
				<a href="/foursquare/logout" style="float:right;">
					<c:if test="${loggedIn==true}">
						<h2 style="margin-right:20px">Logout</h2>
					</c:if>
				</a>
			</header>
			<div id="content" class="rounded">
				<jsp:doBody />
			</div>
			<br/>
			<footer id="footer">
				<p>Copyright (c) 2010 <a href="http://handstandtech.com" title="Handstand Technologies">Handstand Technologies, LLC</a></p>
			</footer>
		</div>
</body>
</html>