<%@ tag isELIgnored="false" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="foursquarebrands" tagdir="/WEB-INF/tags/foursquarebrands"%>
<body>
	<foursquarebrands:header/>
	<div class="nav-wrapper">
		<foursquarebrands:nav/>
	</div>
	<div id="content" class="rounded wrapper">
		<jsp:doBody />
	</div>
	<foursquarebrands:footer/>
	<foursquarebrands:googleanalytics/>
	<foursquarebrands:uservoice/>
</body>