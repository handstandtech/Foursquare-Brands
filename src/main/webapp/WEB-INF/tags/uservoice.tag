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