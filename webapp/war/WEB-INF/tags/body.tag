<%@ tag isELIgnored="false" language="java"%>
<%@ taglib prefix="foursquarebrands" tagdir="/WEB-INF/tags"%>
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