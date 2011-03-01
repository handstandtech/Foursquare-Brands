function handleFoursquareError(response){
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
	// Error Occurred
	else if (response.errorDetail != null && response.errorDetail == "You're already friends with that user.") {
		var infoDiv = $('.user-info[userid=' + id + ']');
		var photoSrc = $(infoDiv).find('img.photo').attr('src');
		var brandName = $(infoDiv).find('.brand-name').html();
		$('#already-following span.brand-name').html(brandName);
		$('#already-following img.photo').attr('src', photoSrc);
		$.fancybox({
			'padding' : 0,
			'href' : '#already-following',
			'transitionIn' : 'elastic',
			'transitionOut' : 'elastic',
			'centerOnScroll' : 'true'
		});
		$('#already-following span.timer').html('5');
		setTimeout(function() {
			$('#already-following span.timer').html('4');
			setTimeout(function() {
				$('#already-following span.timer').html('3');
				setTimeout(function() {
					$('#already-following span.timer').html('2');
					setTimeout(function() {
						$('#already-following span.timer').html('1');
						setTimeout(function() {
							$('#already-following span.timer').html('0');
							location.reload();
						}, 1000);
					}, 1000);
				}, 1000);
			}, 1000);
		}, 1000);
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
function unfollow(user) {
	var id = user.attr('userid');

	_gaq.push([ '_trackEvent', 'brands', 'unfollowed', id ]);

	$.ajax({
		type : 'POST',
		url : "/unfollow?id=" + id,
		data : {},
		success : function(response) {
			// No Error
			if (response.errorType == null) {
				user.find('img.loading').remove();
				user.hide('slow', function() {
					$('.not-following').append(user);
					var theimage = $(this).find('img:first');
					theimage.removeClass('half-opacity');
					user.show('slow');
					updateCount();
				});
			}
			else {
				//Handle Foursquare API Error
				handleFoursquareError(response);
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
function follow(user) {
	var id = $(user).attr('userid');

	_gaq.push([ '_trackEvent', 'brands', 'followed', id ]);

	$.ajax({
		type : 'POST',
		url : "/follow?id=" + id,
		data : {},
		success : function(response) {
			// No Error
			if (response.errorType == null) {
				user.find('img.loading').remove();
				user.hide('slow', function() {
					$('.following').append(user);
					var theimage = $(this).find('img:first');
					theimage.removeClass('half-opacity');
					user.show('slow');
					updateCount();
				});
			}
			else {
				//Handle Foursquare API Error
				handleFoursquareError(response);
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

$(document).ready(function() {
	
	$('a.follow-unfollow-link').click(function() {
		var user = $(this).parent();
		user.find('.foursquare-link').hide();
		user.find('.follow-unfollow-link').hide();
		user.append('<img class="loading" src="/assets/images/loading.gif"/>');
		var theimage = user.find('img:first');
		theimage.addClass('half-opacity');
		if (user.parent().hasClass('following')) {
			unfollow(user);
		} else {
			follow(user);
		}
	});

	$('.user-info').hover(function() {
		var theimage = $(this).find('img:first');
		theimage.addClass('half-opacity');

		$(this).addClass('left-right-border');

		var imagePositionLeft = theimage.position().left;
		var imagePositionTop = theimage.position().top;

		var followunfollowlink = $(this).find('.follow-unfollow-link');
		var foursquarelink = $(this).find('.foursquare-link');
		foursquarelink.show();
		followunfollowlink.show();

		if ($(this).parent().hasClass(
				'following')) {
			followunfollowlink.find('span').text('UnFollow');
		} else {
			followunfollowlink.find('span').text('Follow');
		}

		var IMG_SIZE = 50;

		var fsLinkWidth = foursquarelink.width() + 20; // +20 for 10px padding
		var fsLinkHeight = foursquarelink.height();

		var linkPositionLeft = imagePositionLeft + ((IMG_SIZE - fsLinkWidth) / 2);
		var linkPositionTop = imagePositionTop - (fsLinkHeight + 12);// + the border, etc
		foursquarelink.css('left', linkPositionLeft).css('top',	linkPositionTop);

		var followLinkWidth = followunfollowlink.width() + 22; // +20 for 10px padding
		var followLeft = imagePositionLeft + ((IMG_SIZE - followLinkWidth) / 2);
		var followTop = imagePositionTop + (IMG_SIZE);// -5

		followunfollowlink.css('left', followLeft).css('top', followTop);
		return false;
	},
	function() {
		var theimage = $(this).find('img:first');
		theimage.removeClass('half-opacity');

		$(this).removeClass('left-right-border');

		$(this).find('.foursquare-link').hide();
		$(this).find('.follow-unfollow-link').hide();
		return false;
	})
});