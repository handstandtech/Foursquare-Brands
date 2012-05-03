function handleUnFollowSuccess(id) {
	var button = $("div.follow-button[userid="+id+"]");
	button.removeClass('following');
	button.addClass('not-following');
	button.find("a").html("Follow");
}

function handleFollowSuccess(id) {
	var button = $("div.follow-button[userid="+id+"]");
	button.removeClass('not-following');
	button.addClass('following');
	button.find("a").html("UnFollow");
}

$(document).ready(function() {
	$("div.follow-button a").click(function() {
		var button = $(this).parent();
		var id = button.attr('userid');
		if(button.hasClass('following')){
			//alert("UnFollow "+ id);
			unfollow(id);
		}else {
			//alert("Follow "+ id);
			follow(id);
		}
	});
});