import 'package:polymer/polymer.dart';
import 'dart:html';
import 'package:google_maps/google_maps.dart';
import 'package:google_maps/google_maps_geometry.dart';
import 'dart:convert';
@CustomTag('form-device-edit')
class FormDeviceEdit extends PolymerElement
{
  InputElement address;
  InputElement lat;
  InputElement lng;
  InputElement code;
  InputElement fullAddress;
  SelectElement status;
  ButtonElement search;
  List<MapTypeStyle> styles = 
    [
         {
          "featureType": "poi",
          //"elementType": "bus",
          "stylers": [
            { "visibility": "off" }
          ]
        },
      {
          "featureType": "transit",
          //"elementType": "bus",
          "stylers": [
          { "visibility": "off" }
          ]
        } 
    ];
	bool get applyAuthorStyles => true;
	@published Map device;
	FormDeviceEdit.created() : super.created();
	ButtonElement btnExit=new ButtonElement();
	enteredView() 
	{
		super.enteredView();
		btnExit=this.shadowRoot.querySelector("#btnexit");
		btnExit.onClick.listen(onExit);
		address=this.shadowRoot.querySelector("#address");
		lat=this.shadowRoot.querySelector("#lat");
		lng=this.shadowRoot.querySelector("#lng");
		fullAddress=this.shadowRoot.querySelector("#fullAddress");
		code=this.shadowRoot.querySelector("#code");
		status=this.shadowRoot.querySelector("#status");
		search=this.shadowRoot.querySelector("#search");
		//Event on-click
		search.onClick.listen(getLatLngByAddress);
		//Load
		init();
	}
	void onExit(Event e)
	{
	  dispatchEvent(new CustomEvent("goback",detail:""));
	}
	void getLatLngByAddress(Event)
	{
	  //get lat,lng from address
	  
	}
	/*
	 * @author: diennd
	 * @since: 11/2/2014
	 * @version: 1.0
	 */
  //Google map show
  void init()
  {
      //show data
      address.value=device['address'];
      lat.value=device['lat'].toString();
      lng.value=device['lng'].toString();;
      fullAddress.value=device['address'];
      code.value=device['code'];
      //declare option
      OptionElement opGoodStatus=new OptionElement(data: "Tốt", value:"1" , selected:true);
      OptionElement opErorrStatus=new OptionElement(data: "Hỏng", value:"0" , selected:true);
      //check status
      if(device['status']=="0")
        opGoodStatus.selected=true;
      //add option in select tag
        status.children.add(opGoodStatus);
        status.children.add(opErorrStatus);
      //Google map
      final mapOptions = new MapOptions()
      ..keyboardShortcuts=true
      ..zoom = 14
      ..center = new LatLng(device['lat'],device['lng'])
      ..mapTypeId = MapTypeId.ROADMAP
      ..styles = styles;
      final map = new GMap(this.shadowRoot.querySelector("#show-map"), mapOptions);
      //marker
       Marker marker = new Marker
       (
       new MarkerOptions()
       ..position = new LatLng(device['lat'],device['lng'])
       ..map = map
       ..title = device['address']
       );
  }
}
