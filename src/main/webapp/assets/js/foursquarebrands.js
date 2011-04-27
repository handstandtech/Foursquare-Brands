/******************************** COUNTDOWN ****************************** */
/**
 * Sets the HTML for the given element to the number of the countdown. Counts
 * down in seconds.
 * 
 * @param countdown_number -
 *            Number of Seconds to Count.
 * @param elem
 */
function countdownToReload(countdown_number, elem) {
    if(countdown_number > 0) {
    	setTimeout(function() {
    		countdown_number--;
    		$(elem).html(countdown_number);
        	countdownToReload(countdown_number, elem);
        }, 1000);
    }else {
    	// Countdown Over, Refresh
    	location.reload(true);
    }
}


function handleFoursquareError(response, id){
	// Rate Limit Exceeded
	if (response.errorType == 'rate_limit_exceeded') {
		// Rate Limit
		$.fancybox({
			'padding' : 0,
			'href' : '#rate-limit-exceeded',
			'transitionIn' : 'elastic',
			'transitionOut' : 'elastic',
			'centerOnScroll' : 'true'
		});
	}
	// Error Occurred, Already Followed
	else if (response.errorDetail != null && response.errorDetail == "You're already friends with that user.") {
		var infoDiv = $('.user-info[userid=' + id + ']');
		var photoSrc = $(infoDiv).find('img.photo').attr('src');
		var brandName = $(infoDiv).find('.brand-name').html();
		$('#already-following span.brand-name').html(brandName);
		$('#already-following span.verb').html("Following");
		$('#already-following img.photo').attr('src', photoSrc);
		$.fancybox({
			'padding' : 0,
			'href' : '#already-following',
			'transitionIn' : 'elastic',
			'transitionOut' : 'elastic',
			'centerOnScroll' : 'true'
		});
		
		var countdownElem = $('#already-following span.timer');
		countdownToReload(5, countdownElem);
	} 
	// Already UnFollowed
	else if (response.errorDetail != null && response.errorDetail == "No relationship between you and this user.") {
		var infoDiv = $('.user-info[userid=' + id + ']');
		var photoSrc = $(infoDiv).find('img.photo').attr('src');
		var brandName = $(infoDiv).find('.brand-name').html();
		$('#already-following span.brand-name').html(brandName);
		$('#already-following span.verb').html("Not Following");
		$('#already-following img.photo').attr('src', photoSrc);
		$.fancybox({
			'padding' : 0,
			'href' : '#already-following',
			'transitionIn' : 'elastic',
			'transitionOut' : 'elastic',
			'centerOnScroll' : 'true'
		});

		var countdownElem = $('#already-following span.timer');
		countdownToReload(5, countdownElem);
	} 
	else if(response.errorType!=null){
		//Other error...
		$('#error-occurred span.errorType').html(response.errorType);
		$('#error-occurred span.errorDetail').html(response.errorDetail);
		$('#error-occurred span.code').html(response.code);
		$.fancybox({
			'padding' : 0,
			'href' : '#error-occurred',
			'transitionIn' : 'elastic',
			'transitionOut' : 'elastic',
			'centerOnScroll' : 'true'
		});
	}
}


/**


 * When a Brand is UnFollowed
 */
function unfollow(id) {

	_gaq.push([ '_trackEvent', 'brands', 'unfollowed', id ]);

	$.ajax({
		type : 'POST',
		url : "/unfollow?id=" + id,
		data : {},
		success : function(response) {
			// No Error
			if (response.errorType == null) {
				handleUnFollowSuccess(id)
			}
			else {
				//Handle Foursquare API Error
				handleFoursquareError(response, id);
			}
		},
		error : function(request) {
			alert("An error has occurred, we're going to refresh the page to try and fix it.  If this continues to happen, let us know @HandstandTech.  Thanks!");
			location.reload();
		}
	});
}

/**
 * When a Brand is Followed
 */
function follow(id) {
	_gaq.push([ '_trackEvent', 'brands', 'followed', id ]);

	$.ajax({
		type : 'POST',
		url : "/follow?id=" + id,
		data : {},
		success : function(response) {
			// No Error
			if (response.errorType == null) {
				handleFollowSuccess(id);
			}
			else {
				//Handle Foursquare API Error
				handleFoursquareError(response, id);
			}
		}, 
		error : function(request) {
			alert("An error has occurred, we're going to refresh the page to try and fix it.  If this continues to happen, let us know @HandstandTech.  Thanks!");
			location.reload();
		}
	});
}

/**
 * Update the Count of who is being followed vs. not followed
 */
function updateCount() {
	var followingCount = $(".following .user-info").length;
	$('.following-stats span.percentage').text(followingCount);
}