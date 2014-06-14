/**
 * 
 */

var initLat = 52.52193;
var initLon = 13.41321;
var initZoom = 14;


MyMap = new function() {

	var myMarker = null;
	var parentMapHandler = null;
	
	this.initialize = function(canvas, lat, lng, zoom, mapHandler) {
		parentMapHandler = mapHandler;
		var latlng = new google.maps.LatLng(lat, lng);
		var myOptions = {
			zoom : zoom,
			center : latlng,
			disableDefaultUI: true,

			mapTypeId : google.maps.MapTypeId.ROADMAP
		};

		MyMap.map = new google.maps.Map(document.getElementById(canvas), myOptions);
		
		//zoom
		google.maps.event.addListener(MyMap.map, "zoom_changed", function() { 
	        var newZoom = MyMap.map.getZoom();
			$("#hidenZoom").val(newZoom);
			parentMapHandler.updateMapAndImage();
		});
	};

	this.addCenterMarker = function(lat, lng) {
		myMarker = this.addMarker(lat, lng, true);
		//add drag listener
		google.maps.event.addListener(myMarker, 'dragend', function() {
			var latlng = myMarker.getPosition();
			MyMap.map.panTo(latlng);
			
			$("#hidenLat").val(latlng.lat());
			$("#hidenLng").val(latlng.lng());
			parentMapHandler.updateMapAndImage();
		});
		
		return myMarker;
	};

	
	this.addMarker = function(lat, lng, draggable) {
		var coordinates = new google.maps.LatLng(lat, lng);
		var marker = new google.maps.Marker({
			position : coordinates,
			map : MyMap.map,
			draggable : draggable
		});
		return marker;
	};


	this.centerMarker = function(latlng){
		if(latlng == null && myMarker != null){
			latlng = myMarker.getPosition();
		}
		MyMap.map.setCenter(latlng);
	};
	
};



// //////////////////////////////////////////
var MapHandler = function() {

	var messages = {
		'msgGeocodeAddressNotFound': 'Adresse nicht erkannt. Fehler: '	
	};
	
	var centeredMarker = null;
	var mapIdStr = "#myMapWrapper";
	var mapId = "myMapWrapper";

	this.initMap = function() {

		MyMap.initialize(mapId, initLat, initLon, initZoom, this);
		centeredMarker = MyMap.addCenterMarker(initLat, initLon);

		// write (or write back) for exchange
		window.setTimeout(function() {
			$("#hidenLat").val(initLat);
			$("#hidenLng").val(initLon);
			$("#hidenZoom").val(initZoom);
		}, 1000);
	};

	this.actionCenterOnMarker = function () {
		MyMap.centerMarker();
	};

	this.updateMapAndImage = function () {
		MyMap.centerMarker();

		//call ajax request with timeout, after non-ajax request
		window.setTimeout(function() {
			rerenderOutputImg();
//			console.log("actionCenterOnMarker(), end");
		}, 1000);
	};
	
	this.searchAddress = function() {
		var addressStr = $("#addressInput").val();
		if(addressStr == null || addressStr.length<1)	return false;
		
		var geocoder = new google.maps.Geocoder();
		geocoder.geocode(
			{	'address' : addressStr	}, 
			function(results, status) {
				if (status == google.maps.GeocoderStatus.OK) {
					MyMap.map.setCenter(results[0].geometry.location);
					centeredMarker.setPosition(results[0].geometry.location);
					$("#hidenLat").val(results[0].geometry.location.lat());
					$("#hidenLng").val(results[0].geometry.location.lng());
				} else {
					alert(messages.msgGeocodeAddressNotFound + status);
				}
			}
		);
	};
	
	this.getTest = function(){
		return 123456;
	};

};

// ///////////////////////////////////////////////////////////////////

var mapHandler = new MapHandler();

window.onload = function () {
	initilaizeHandler();
	
	// init searchAdress key-enter-event handler
	$("#addressInput").keyup(function(event){
	    if(event.keyCode == 13){
	    	mapHandler.searchAddress();
	    }
	});
};

function initilaizeHandler() {
	
	// read initValues, if set from outer
	var tmpZoom = $("#hidenZoom").val();
	try {
		tmpZoom = parseInt(tmpZoom);
		if(tmpZoom>0){
			initZoom = tmpZoom;
		}
	} catch (e) {}

	
	var tmpLat = $("#hidenLat").val();
	try {
		tmpLat = parseFloat(tmpLat);
		if(tmpLat && tmpLat != NaN){
			initLat = tmpLat;
		}
	} catch (e) {}

	var tmpLng = $("#hidenLng").val();
	try {
		tmpLng = parseFloat(tmpLng);
		if(tmpLng && tmpLng != NaN){
			initLon = tmpLng;
		}
	} catch (e) {}

	
	mapHandler.initMap();
}



// /////////////////////////////////////////////

