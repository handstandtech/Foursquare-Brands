<%@ tag isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<body>
	<div id="wrapper">
			<header id="header">
				<a href="/" style="float:left;">
					<img id="logo" src="/assets/images/foursquare-brands-logo.png">
					<h2 style="display:none;">Foursquare Brands</h2>
				</a>
				<div class="right-side">
					<div style="float:left;">
						<!-- AddThis -->
						<div class="addthis_toolbox addthis_default_style ">
							<a class="addthis_button_facebook_like" fb:like:layout="button_count"></a>
							<a class="addthis_button_tweet"></a>
							<a class="addthis_counter addthis_pill_style"></a>
						</div>
						<script type="text/javascript">var addthis_config = {"data_track_clickback":true};</script>
						<script type="text/javascript" src="http://s7.addthis.com/js/250/addthis_widget.js#username=handstandtech"></script>
					</div>
					<c:if test="${loggedIn==true}">
						<div style="float:left;">
							<a href="/foursquare/logout">
								<h2 style="margin-right:20px">Logout</h2>
							</a>
						</div>
					</c:if>
				</div>
			</header>
			<div id="content" class="rounded">
				<jsp:doBody />
			</div>
			<br/>
			<footer id="footer">
				<p>Copyright (c) 2010 <a href="http://handstandtech.com" title="Handstand Technologies">Handstand Technologies, LLC</a></p>
			</footer>
		</div>
		
		<!-- Begin User Voice -->
		<script type="text/javascript">
			var uservoiceOptions = {
			  /* required */
			  key: 'foursquarebrands',
			  host: 'foursquarebrands.uservoice.com', 
			  forum: '96055',
			  showTab: true,  
			  /* optional */
			  alignment: 'left',
			  background_color:'#1E7A8C', 
			  text_color: 'white',
			  hover_color: '#06C',
			  lang: 'en'
			};
			
			function _loadUserVoice() {
			  var s = document.createElement('script');
			  s.setAttribute('type', 'text/javascript');
			  s.setAttribute('src', ("https:" == document.location.protocol ? "https://" : "http://") + "cdn.uservoice.com/javascripts/widgets/tab.js");
			  document.getElementsByTagName('head')[0].appendChild(s);
			}
			_loadSuper = window.onload;
			window.onload = (typeof window.onload != 'function') ? _loadUserVoice : function() { _loadSuper(); _loadUserVoice(); };
		</script>
		<!-- End User Voice -->
</body>