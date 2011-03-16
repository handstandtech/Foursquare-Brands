<div id="dialogs" style="display: none;">
	<div id="already-following" class="error-dialog">
		<img class="photo" />
		<h3>You are already <span class="verb"></span> <span class="brand-name"></span>!</h3>
		<p>The page will refresh in <span class="timer">5</span> seconds to show the most up to date info, or you can refresh the page manually.</p>
	</div>
	<div id="rate-limit-exceeded" class="error-dialog">
		<h3>Foursquare Rate Limit Exceeded.</h3>
		<p>Sounds like you have been following a lot of people! Foursquare imposes some limits to the amount of times their API can be called. For more information see <a	href="http://developer.foursquare.com/docs/overview.html">their	documentation on rate limiting</a>. If you keep seeing this error, come	back in an hour and try again.</p>
	</div>
	<div id="error-occurred" class="error-dialog">
		<h3>An Error Occurred while using the Foursquare API</h3>
		<p><strong>Error Type: </strong><span class="errorType"></span></p>
		<br/>
		<p><strong>Error Detail: </strong><span class="errorDetail"></span></p>
		<br/>
		<p style="display:none;"><strong>Error Code: </strong><span class="code"></span></p>
	</div>
</div>