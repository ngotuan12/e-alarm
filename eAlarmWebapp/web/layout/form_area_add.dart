import 'package:polymer/polymer.dart';
import 'dart:html';
import 'dart:core';
import '../src/util.dart';
import 'package:google_maps/google_maps.dart';
@CustomTag('form-area-add')
class FormAreaAdd extends PolymerElement
{
	ButtonElement btnCancel;
	ButtonElement btnExit;
	ButtonElement btnSave;
	TextInputElement txtAddress;
	TextInputElement txtFullAddress;
	String Lat;
	String Lng;
	TextInputElement txtCode;
	//SelectElement selArea;
	SelectElement selStatus;
	//SelectElement selType;
	List<Map> listAreas;
	bool error=false;
	String errorString="";
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
	// of this element.
	bool get applyAuthorStyles => true;
	FormAreaAdd.created() : super.created();
	enteredView() 
	{
	super.enteredView();
	selStatus=this.shadowRoot.querySelector("#status");
	btnCancel=this.shadowRoot.querySelector("#btnCancel");
	btnExit=this.shadowRoot.querySelector("#btnExit");
	btnSave=this.shadowRoot.querySelector("#btnSave");
	txtFullAddress=this.shadowRoot.querySelector("#fullAddress");
	txtAddress=this.shadowRoot.querySelector("#address");
	txtCode=this.shadowRoot.querySelector("#code");
	//event
	btnExit.onClick.listen(onExit);
	btnCancel.onClick.listen(onExit);
	btnSave.onClick.listen(onSave);
	txtAddress.onInput.listen(onChangeFullName);
	txtAddress.onChange.listen(onShowMap);
	//Load
	init();
	}
	/*
	 * @author: diennd
	 * @since: 11/2/2014
	 * @version: 1.0
	 */
	void onSave(Event e)
		{
		checkData();
		if(!error)
		{
			//window.alert(selStatus.selectedIndex.toString()+"-"+txtAddress.value+"-"+txtCode.value+"-"+txtFullAddress.value+"-"+txtLat.value+"-"+txtLng.value+"-");
			Responder responder = new Responder();
			Map request = new Map();
			request["Method"] = "AddArea";
			request["Code"]=txtCode.value.trim().toUpperCase();
			request["Name"]=txtAddress.value.trim();
			request["FullName"]=txtFullAddress.value.trim();
			//option
			request["ParentID"]='1';
			request["Status"]=(selStatus.selectedIndex==0)?"1":"0"; 
			request["Lang"]=double.parse(Lng);
			request["Lat"]=double.parse(Lat);
			request["Type"]='2';
			responder.onSuccess.listen((Map response)
			{
				Util.showNotifySuccess("Add new record sucess");
				dispatchEvent(new CustomEvent("goback", detail:""));
			});
			//error
			responder.onError.listen((Map error)
			{
				Util.showNotifyError(error["message"]);
			});
			AppClient.sendMessage(request, AlarmServiceName.AreaService, AlarmServiceMethod.POST,responder);
		}
		else
		{
			Util.showNotifyError(errorString);
		}
	}
	/*
	 * @author:diennd
	 * @since :14/2/2014
	 * @version:"1.0
	 */
	void checkData()
	{
		error=false;
		errorString="";
		//check lat lng
		if(Lat==""||Lng==""||Lat==null||Lng==null)
		{
			error=true;
			errorString+="Lat,lng can't empty"+", ";
		}
		if(txtCode.value.trim() =="")
		{
			error=true;
			errorString+="Code can't empty"+", ";
			txtCode.focus();
		}
		if(listAreas !=null)
		{
			for(int i=0;i<listAreas.length;i++)
		{
		Map area=listAreas[i];
		if(area['code'].toString().toUpperCase()==txtCode.value.trim().toUpperCase())
		{
			error=true;
			errorString+="Code is already exists";
		}
		}
		}
	}
	/*
	 * @author:diennd
	 * @since:17/2/2014
	 * @version:1.0
	 */
	void onChangeFullName(Event e)
	{
		Lat="";
		Lng="";
		txtFullAddress.value='Việt Nam';
		if(txtAddress.value=="")
		{
			txtFullAddress.value="";
		}
		if(e.target==txtAddress)
		{
			String temp=txtFullAddress.value;
			txtFullAddress.value="";
			txtFullAddress.value=txtAddress.value+','+temp;
		}
	}
	/*
	 * @author:diennd
	 * @since:17/2/2014
	 * @version:1.0
	 */
	void onShowMap(Event e)
	{
		if(txtFullAddress.value.trim() !="")
		{
		Geocoder geocoder = new Geocoder();
		final request = new GeocoderRequest()
		..address =txtFullAddress.value.trim();
		geocoder.geocode(request, (List<GeocoderResult> results, GeocoderStatus status) {
		if (status == GeocoderStatus.OK)
		{
			//print("Lat: "+results[0].geometry.location.lat.toString()+"Lng: "+results[0].geometry.location.lng.toString());
			final mapOptions = new MapOptions()
			..keyboardShortcuts=true
			..zoom = 16
			..center = new LatLng(results[0].geometry.location.lat, results[0].geometry.location.lng)
			..mapTypeId = MapTypeId.ROADMAP
			..styles = styles;
			final map = new GMap(this.shadowRoot.querySelector("#show-map"), mapOptions);
			//marker
			Marker marker = new Marker
			(
			new MarkerOptions()
			..position = new LatLng(results[0].geometry.location.lat, results[0].geometry.location.lng)
			..map = map
			..title = txtAddress.value
			 ); 
			//get lat,lng
			Lat=results[0].geometry.location.lat.toString();
			Lng=results[0].geometry.location.lng.toString();
		} 
		else
		{
			window.alert('Geocode was not successful for the following reason: ${status}');
		}
		});
		}
		else
		{
			window.alert('Không tìm thấy !!!');
		}
	}
	/*
	 * @author:diennd
	 * @since: 17/2
	 * @version :1.0
	 */
	void onExit(Event e)
	{
	dispatchEvent(new CustomEvent("goback", detail:""));
	}
	/*
	 * @author: diennd
	 * @since: 11/2/2014
	 * @version: 1.0
	 */
	//Google map show
	void init()
	{
	//Google map
	codeAddress();
	//get areas
	getAreas();
	}
	/*
	 * @author:diennd
	 * @since:17/2/2014
	 * @version: 1.0
	 */
	void codeAddress() 
	{
		Geocoder geocoder = new Geocoder();
		final request = new GeocoderRequest()
		..address = 'Hà Nội,Việt Nam';
		geocoder.geocode(request, (List<GeocoderResult> results, GeocoderStatus status) 
		{
			if (status == GeocoderStatus.OK)
			{
				//print("Lat: "+results[0].geometry.location.lat.toString()+"Lng: "+results[0].geometry.location.lng.toString());
				final mapOptions = new MapOptions()
				..keyboardShortcuts=true
				..zoom = 14
				..center = new LatLng(results[0].geometry.location.lat, results[0].geometry.location.lng)
				..mapTypeId = MapTypeId.ROADMAP
				..styles = styles;
				final map = new GMap(this.shadowRoot.querySelector("#show-map"), mapOptions);
				//marker
				Marker marker = new Marker
				(
					new MarkerOptions()
					..position = new LatLng(results[0].geometry.location.lat, results[0].geometry.location.lng)
					..map = map
					..title = 'Việt Nam'
				);
			} 
			else
			{
			window.alert('Geocode was not successful for the following reason: ${status}');
			}
		});
	}
	 /*
	* @author:diennd
	* @since:17/2/2014
	* @version:1.0
	*/
	filterByParentID(List<Map> locations,List<String> listType)
	{
		return locations.where((location)
		{
			for(int i=0;i<listType.length;i++)
			{
				if(listType[i]==location["parent_id"])
				{
					return true;
				}
			}
			return false;
		}).toList();
	}
	/*
	 * @author:diennd
	 * @since:17/2/2014
	 * @version:1.0
	 */
	List<Map> filterByLevel(List<Map> locations,List<String> listLevel)
	{
		return locations.where((location)
		{
			for(int i=0;i<listLevel.length;i++)
			{
				if(listLevel[i]==location["level"])
				{
					return true;
				}
			}
			return false;
		}).toList();
	}
	/*
 * @author:diennd
 * @since:17/2/2014
 * @version:1.0
 */
	//Show all areas
	void getAreas()
	{
		//get data
		Map request = new Map();
		request["Method"] = "GetAllAreaActive";
		//Listen
		Responder responder = new Responder();
		//success
		responder.onSuccess.listen((Map response)
		{
			listAreas=response['ListAreaActive'];
		});
		//error
		responder.onError.listen((Map error)
		 {
		 Util.showNotifyError(error["message"]);
		 }
		);
		//send to server
		AppClient.sendMessage(request, AlarmServiceName.AreaService, AlarmServiceMethod.POST,responder);
	}
}