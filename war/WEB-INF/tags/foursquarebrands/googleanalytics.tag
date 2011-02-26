<%@ tag isELIgnored="false" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="foursquarebrands" tagdir="/WEB-INF/tags/foursquarebrands"%>
<!-- Begin Google Analytics -->
<script type="text/javascript">
	var _gaq = _gaq || [];
</script>
<c:if test="${production==true}">
	<script type="text/javascript">
	  _gaq.push(['_setAccount', 'UA-12000077-5']);
	  _gaq.push(['_setDomainName', 'none']);
	  _gaq.push(['_setAllowLinker', true]);
	  _gaq.push(['_trackPageview']);
	
	  (function() {
	    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
	    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
	    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
	  })();
	</script>
</c:if>
<!-- End Google Analytics -->