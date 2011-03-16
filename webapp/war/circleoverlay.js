/* 
 * Circle overlay that you must instantiate after your map has already been created.
 *
 * Arguments:
 *	canvas --- the canvas element that we are going to use to draw the circle
 *	map -- the Google Maps V3 map instnace you have created
 *	point -- the center point of the circle
 *	radius -- the radius of the circle
 */
function CircleOverlay(canvas, map, point, radius)
{
	google.maps.OverlayView.call(this);

	this._map = map;
	this._point = point;
	this._radius = radius;
	this._canvas = canvas;

	// Fit bounds of a circle that is drawn into the map
	var d2r = Math.PI / 180;
	this.circleLatLngs = new Array();
	var circleLat = radius * 0.014483;  // Convert statute miles into degrees latitude
	var circleLng = circleLat / Math.cos(point.lat() * d2r);
	var numPoints = 100;

	this.latlngbounds = new google.maps.LatLngBounds( );

	// 2PI = 360 degrees, +1 so that the end points meet
	for (var i = 0; i < numPoints + 1; i++) { 
		var theta = Math.PI * (i / (numPoints / 2)); 
		var vertexLat = point.lat() + (circleLat * Math.sin(theta)); 
		var vertexLng = parseFloat(point.lng()) + parseFloat((circleLng * Math.cos(theta)));
		var vertextLatLng = new google.maps.LatLng(vertexLat, vertexLng);
		this.circleLatLngs.push(vertextLatLng);
		this.latlngbounds.extend(vertextLatLng);
	}
	map.fitBounds(this.latlngbounds);

	this.setMap(map);
}

// Inherit from the maps Overlay view so that we can access its members
CircleOverlay.prototype = new google.maps.OverlayView();


/*
 * Not Maps specific this just appends the canvas to the maps as an overlay layer
 */
CircleOverlay.prototype.onAdd = function()
{
	var panes = this.getPanes();

	if (!this._canvasAppended)
	{
		this._canvas.style.left = 0;
		this._canvas.style.top = 0;
		panes.overlayLayer.appendChild(this._canvas);
		this._canvasAppended = true;
	}
}
 
// Remove the canvas overlay from the map pane
CircleOverlay.prototype.remove = function()
{
	if (this._canvas) 
	{
		this.set_map(null);
		this._canvas.parentNode.removeChild(this._canvas);
		this._canvas = null;
		this._ctx = null;
	}
}
 
// Redraw based on the current projection and zoom level...
CircleOverlay.prototype.draw = function(firstTime)
{
	if (this._ctx)
		return;

	var projection = this.getProjection();
	var ctx = this._ctx = this._canvas.getContext("2d");
	ctx.beginPath();
	pixelPoint = projection.fromLatLngToDivPixel(this.circleLatLngs[0]);
	ctx.moveTo(pixelPoint.x, pixelPoint.y);

	for (i = 1; i < this.circleLatLngs.length; i++) {
		pixelPoint = projection.fromLatLngToDivPixel(this.circleLatLngs[i]);
		ctx.lineTo(pixelPoint.x, pixelPoint.y);
	}
	ctx.closePath();
	ctx.fillStyle = "rgba(0,0,100, .5)";
	ctx.fill();
}
