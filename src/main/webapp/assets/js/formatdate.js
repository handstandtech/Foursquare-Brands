function formatTimestamps(){
	$("span.timestamp").each(function() {
		var timeStr = $(this).attr('time');
		var time = parseInt(timeStr);
		var date = new Date(time);
		$(this).html(formatDate(date));
	});
}

function formatDate(d) {
	var d_names = new Array("Sunday", "Monday", "Tuesday",
	"Wednesday", "Thursday", "Friday", "Saturday");

	var m_names = new Array("January", "February", "March", 
	"April", "May", "June", "July", "August", "September", 
	"October", "November", "December");

	var curr_day = d.getDay();
	var curr_date = d.getDate();
	var sup = "";
	if (curr_date == 1 || curr_date == 21 || curr_date ==31)
	   {
	   sup = "st";
	   }
	else if (curr_date == 2 || curr_date == 22)
	   {
	   sup = "nd";
	   }
	else if (curr_date == 3 || curr_date == 23)
	   {
	   sup = "rd";
	   }
	else
	   {
	   sup = "th";
	   }
	var curr_month = d.getMonth();
	var curr_year = d.getFullYear();

	var a_p="";
	var curr_hour = d.getHours();

	if (curr_hour < 12)
	   {
	   a_p = "AM";
	   }
	else
	   {
	   a_p = "PM";
	   }
	if (curr_hour == 0)
	   {
	   curr_hour = 12;
	   }
	if (curr_hour > 12)
	   {
	   curr_hour = curr_hour - 12;
	   }

	var curr_min = d.getMinutes();
	
	
	if(curr_min<10) {
		curr_min="0"+curr_min;
	}
	
	
	var dateStr = d_names[curr_day] + " " + m_names[curr_month] + " "+ curr_date + ""
	+ sup + ", "+  curr_year + " at "+curr_hour + ":" + curr_min + " " + a_p;
	
	return dateStr;
}