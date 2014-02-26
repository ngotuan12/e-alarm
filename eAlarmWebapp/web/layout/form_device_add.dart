import 'package:polymer/polymer.dart';
import 'package:google_maps/google_maps.dart';
import '../src/util.dart';
import 'dart:convert';
import 'dart:html';
@CustomTag('form-device-add')
class FormDeviceAdd extends PolymerElement
{
	ButtonElement btnCancel;
	ButtonElement btnSearch;
	ButtonElement btnSave;
	TextInputElement txtAddress;
	TextInputElement txtFullAddress;
	TextInputElement txtLat;
	TextInputElement txtLng;
	TextInputElement txtCode;
	TextInputElement txtName;
	SelectElement selArea;
	SelectElement selStatus;
	
	DivElement divProperties;
	List<Map> listAreas;
	bool error=false;
	String errorString="";
	GMap map;
	Marker marker =  new Marker();
	Map currentPosition = {"lat":"","lng":""};
 	List<MapTypeStyle> mapStyles;
	bool get applyAuthorStyles => true;
	FormDeviceAdd.created() : super.created();
	List<Map> listProperties;
	enteredView() 
	{
		//enter view
		super.enteredView();
		//Combo area
		selArea=this.shadowRoot.querySelector("#area");
		selStatus=this.shadowRoot.querySelector("#status");
		btnCancel=this.shadowRoot.querySelector("#btnCancel");
		btnSave=this.shadowRoot.querySelector("#btnSave");
		btnSearch=this.shadowRoot.querySelector("#btnSearch");
		txtFullAddress=this.shadowRoot.querySelector("#fullAddress");
		txtAddress=this.shadowRoot.querySelector("#address");
		txtLat=this.shadowRoot.querySelector("#Lat");
		txtLng=this.shadowRoot.querySelector("#Lng");
		txtCode=this.shadowRoot.querySelector("#code");
		txtName =this.shadowRoot.querySelector("#name");
		//divProperties
		divProperties = this.shadowRoot.querySelector("#list-properties");
		//event
		btnCancel.onClick.listen(onExit);
		btnSearch.onClick.listen(onShowMap);
		btnSave.onClick.listen(onSave);
		selArea.onChange.listen(onSelectedArea);
		txtAddress.onInput.listen(onTextInputAdress);
		txtAddress.onChange.listen(onShowMap);
		//form load
		Map request = new Map();
		request["Method"] = "form_device_add_load";
		Responder responder = new Responder();
		//success
		responder.onSuccess.listen((Map response)
		{
			//init area
			List<Map> listArea = response["area_list"];
			initArea(listArea);
			//init google map
			initGoogleMap();
			//init properties
			listProperties = response["properties_list"];
			initProperties(listProperties);
//			print(response);
		});
		responder.onError.listen((Map error)
		{
			Util.showNotifyError(error["message"]);
		});
		AppClient.sendMessage(request, AlarmServiceName.DeviceManagementService, AlarmServiceMethod.POST,responder);
//		init();
	}
	/*
	 * @author TuanNA
	 * @since 26/02/2014
	 * @version 1.0
	 * @company: 
	 * @param: 
	 */
	void initArea(List<Map> listArea)
	{
		selArea.children.clear();
		//contructor option
		OptionElement op=new OptionElement();
		op.value="-1";
		op.text="Chọn địa bàn";
		selArea.children.add(op);
		//
		for(int i=0;i<listArea.length;i++)
		{
			Map element= listArea[i];
			String name=element['name'];
			//option
			OptionElement op = new OptionElement();
			op.text='==>'+name;
			op.value = JSON.encode(element);
			//add option
			selArea.children.add(op);
		}
	}
	/*
	 * @author TuanNA
	 * @since 26/02/2014
	 * @version 1.0
	 * @company: 
	 * @param: 
	*/	
	void initProperties(List<Map> listProperties)
	{
		divProperties.children.clear();
		//
		DivElement left = new DivElement();
		left.className = "span6";
		DivElement right = new DivElement();
		right.className = "span6";
		//
		divProperties.children.add(left);
		divProperties.children.add(right);
		//
		for(int i=0;i<listProperties.length;i++)
		{
			LabelElement label = new LabelElement();
			
			//class name
			label.className = "checkbox";
			DivElement div = new DivElement();
			div.className = "checker";
			label.children.add(div);
			SpanElement span =  new SpanElement();
			span.id = "span-check";
			div.children.add(span);
			label.appendText(listProperties[i]["name"]);
			//apend
//			label.appendHtml("<div class=\"checker\"> "+
//			"<span id=\"span-check\"class=\"\"> "+
//			"</span>"+
//			"</div>"+
//			listProperties[i]["name"]);
			if(i%2==0)
				left.children.add(label);
			else 
				right.children.add(label);
			
			span.onClick.listen(onPropertyClick);
			listProperties[i]["span"] = span;
		}
	}
	/*
	 * @author TuanNA
	 * @since 26/02/2014
	 * @version 1.0
	 * @company: 
	 * @param: 
	*/
	void onPropertyClick(Event event)
	{
		SpanElement span = event.currentTarget;
		if(span.classes.contains("checked"))
			span.classes.remove("checked");
		else
			span.classes.add("checked");
	}
	/*
	 * @author TuanNA
	 * @since 26/02/2014
	 * @version 1.0
	 * @company: 
	 * @param: 
	*/
	void onSelectedArea(Event event)
	{
		txtAddress.disabled=(selArea.selectedIndex>0)?false:true;
		updateFullAddress();
	}
	void onTextInputAdress(Event event)
	{
		updateFullAddress();
	}
	/*
	 * @author TuanNA
	 * @since 26/02/2014
	 * @version 1.0
	 * @company: 
	 * @param: 
	*/
	void updateFullAddress()
	{
		if(selArea.selectedIndex>0)
		{
			OptionElement opt = selArea.children.elementAt(selArea.selectedIndex);
			//data
			Map area = JSON.decode(opt.value);
			if(area !=null)
			txtFullAddress.value=txtAddress.value+","+area["full_name"];
  		}
	}
	/*
	 * @author TuanNA
	 * @since 26/02/2014
	 * @version 1.0
	 * @company: 
	 * @param: 
	*/
	void initGoogleMap()
	{
		//map styles
		mapStyles = 
		[
			{
				"featureType": "poi",
				//"elementType": "bus",
				"stylers": 
				[
					{ "visibility": "off" }
				]
			},
			{
				"featureType": "transit",
				//"elementType": "bus",
				"stylers": 
				[
					{ "visibility": "off" }
				]
			} 
		];
		//map options
		MapOptions mapOptions = new MapOptions()
							 ..keyboardShortcuts=true
							 ..zoom = 4
							 ..center = new LatLng(14.058324,108.27719)
							 ..mapTypeId = MapTypeId.ROADMAP
							 ..styles = mapStyles;
		map = new GMap(this.shadowRoot.querySelector("#show-map"), mapOptions);
		//marker
//		Marker marker = new Marker
//		(
//			new MarkerOptions()
//			..position = new LatLng(14.058324,108.27719)
//			..map = map
//			..title = 'Viet Nam'
//		);  
	}
	/*
	 * @author:diennd
	 */
	void onExit(Event e)
	{
		dispatchEvent(new CustomEvent("goback",detail:""));
	}
	/*
 	* @author: diennd
	* @since: 11/2/2014
	* @version: 1.0
	*/
	void onSave(Event e)
	{
		if(!isValidate())
		{
			return;
		}
		window.confirm("");
	}
	/*
	 * @author:diennd
	 * @since :14/2/2014
	 * @version:"1.0
	 */
	bool isValidate()
	{
		if(txtCode.value.trim() =="")
		{
			Util.showNotifyError("Trường mã phòng máy không được để trống");
			txtCode.focus();
			return false;
		}
		if(txtName.value.trim() =="")
		{
			Util.showNotifyError("Trường tên phòng máy không được để trống");
			txtName.focus();
			return false;
		}
		if(selArea.selectedIndex==0)
		{
			Util.showNotifyError("Chưa chọn địa bàn");
			selArea.focus();
			return false;  
		}
		//check MAC
		//check lat lng
		if(currentPosition["lat"]==""||currentPosition["lng"]=="")
		{
			Util.showNotifyError("Địa chỉ không hợp lệ");
			txtAddress.focus();
			return false;
		}
		return true;
	}
	/*
	* @author: diennd
	* @since: 11/2/2014
	* @version: 1.0
	* @modify TuanNA
	*/
	void onShowMap(Event e)
	{
		Geocoder geocoder = new Geocoder();
		GeocoderRequest request = new GeocoderRequest()
					..address = txtFullAddress.value.trim();
		geocoder.geocode(request, (List<GeocoderResult> results, GeocoderStatus status) {
			if (status == GeocoderStatus.OK)
			{
				map.center = new LatLng(results[0].geometry.location.lat, results[0].geometry.location.lng);
				map.zoom = 15;
				marker.map = map;
				marker.position = new LatLng(results[0].geometry.location.lat, results[0].geometry.location.lng);
				marker.title = txtAddress.value;
				currentPosition = {"lat":"","lng":""};
				currentPosition["lat"] = results[0].geometry.location.lat.toString();
				currentPosition["lng"] = results[0].geometry.location.lng.toString();
			}
			else
			{
				Util.showNotifyError("Không tìm thấy địa chỉ hợp lệ");
			}
		});
	}

}
