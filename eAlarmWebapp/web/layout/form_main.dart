
import 'package:polymer/polymer.dart';
import 'dart:html';
import 'dart:convert';
import 'package:google_maps/google_maps.dart';
import 'dart:async';
import '../src/util.dart';

@CustomTag('form-main')
class FormMain extends PolymerElement
{
	FormMain.created() : super.created();
	// This lets the Bootstrap CSS "bleed through" into the Shadow DOM
  	// of this element.
	bool get applyAuthorStyles => true;
	Timer mytimer;
	SelectElement slCity;
	SelectElement slStatus;
	OListElement olDevicesGood;
	OListElement olDevicesError;
	LIElement liError;
	LIElement liNormal;
	List<Map> listArea;
	InfoWindow popupWindow;
	DivElement divgoodATM;
	DivElement diverrorATM;
	DivElement divMapCanvas;
	SpanElement spClick;
	DivElement divRight;
	List<Map> listProvince = [];
	List<Map> AllDevices=[];
	GMap map;
	int count=0;
	List<Map> properties;
	Map currentDevice;
	List<MapTypeStyle> styles =
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
	List<Map> markers = []; 
	/*
	 * @author ducdienpt
	 * @since:12/12/2013
	 * @company: ex-artisan
	 * @version :1.0
	 */
	enteredView()
	{
		super.enteredView();
		slCity=this.shadowRoot.querySelector("#city-location");
		slStatus=this.shadowRoot.querySelector("#selStatus");
		olDevicesGood=this.shadowRoot.querySelector("#ol-devicesGood");
		olDevicesError=this.shadowRoot.querySelector("#ol-devicesError");
		spClick=this.shadowRoot.querySelector("#spClick");
		divRight=this.shadowRoot.querySelector("#divRight");
		divMapCanvas=this.shadowRoot.querySelector("#map-canvas");
		//Event
		slCity.onChange.listen(onChooseCity);
		slStatus.onChange.listen(onChooseStatus);
		//popup window
		popupWindow = createPopupInfor();
		//init
		init();
	}
	/*
	 * @DienND
	 * @since 13/12/2013
	 * @company: ex-artisan
	 * @version: 1.0
	 */
	void init()
	{
		//remove all option in slCity
		slCity.children.clear();
		//add option in to slcity
		OptionElement op=new OptionElement();
		op.value="-1";
		op.text="Tỉnh/Thành Phố";
		slCity.children.add(op);
		//initWebsocket
		initWebsocket();
		//init google map
		initGoogleMap();
		//get data
		Map request = new Map();
		request["Method"] = "onFormMainLoad";
		//Listen
		Responder responder = new Responder();
		//success
		responder.onSuccess.listen((Map response)
		{
		    //get device
		    Map devRequest = new Map();
			devRequest['Method'] = 'onGetDevicesByAreaCodeStatus';
			devRequest['area_code'] = 'VN';
			devRequest['status'] = 0;
			
			Responder devResponder = new Responder();
			devResponder.onSuccess.listen((Map res){
//			print(res.toString());
			 AllDevices = res['all_devices_byarea_info'];
			});
			devResponder.onError.listen((Map error)
			{
			Util.showNotifyError(error["message"]);
		});
			//AppClient.sendMessage(devRequest, AlarmServiceName.DeviceService, AlarmServiceMethod.POST,devResponder);
			AllDevices=response['device_list'];
			List<Map> areas=response['area_list'];
			listArea = filterByType(areas, ["2"]);
			//init Option Select
			for(int i=0;i<listArea.length;i++)
			{
				//option
				Map element= listArea[i];
				String name=element['name'];
				OptionElement op = new Element.option();
				op.text=name;
				op.value = JSON.encode(element);
				//add are
				slCity.children.add(op);
			}
			//init area on map
			showArea(listArea);
			//init list device by province
			showAreaList(listArea);
		});
		//error
		responder.onError.listen((Map error)
		{
			Util.showNotifyError(error["message"]);
		}
		);
		//send to server
		AppClient.sendMessage(request, AlarmServiceName.DeviceService, AlarmServiceMethod.POST,responder);
		//default value
		slStatus.selectedIndex = 0;
	}
	/*
	 * 
	 */
	void initGoogleMap()
	{
		MapOptions mapOptions = new MapOptions()
			..keyboardShortcuts=true
			..zoom = 5
			..center = new LatLng(14.058324, 108.277199)
			..mapTypeId = MapTypeId.ROADMAP
			..styles = styles;
		map = new GMap(this.shadowRoot.querySelector("#map-canvas"), mapOptions);
	}
	/**
	 * 
	 */
	void initWebsocket()
	{
		AppClient.connectWebsocket();
		
		AppClient.websocket.onMessage.listen((MessageEvent event){
			print(event.data);
			Map response = JSON.decode(event.data);
			switch(response["handle"])
			{
				case "update_device_properties":
					updateProperties(response["infors"]);
					break;
				default:
					break;
			}
		});
	}
	/**
	 * 
	 */
	void updateProperties(Map infors)
	{
		String deviceStatus = "1";
		for(int i = 0; i < infors.keys.length;i++)
		{
			String code = infors.keys.elementAt(i);
			num value = infors[infors.keys.elementAt(i)];
			print(infors.keys.elementAt(i)+infors[infors.keys.elementAt(i)].toString());
			for(int j=0;j < properties.length;j++)
			{
				Map element = properties[j];
				if(element["code"] == code)
				{
					element["value"] = value;
					//check status
					String strStatus = "1";
					if(element["value"]>=element["max_alarm"]||element["value"]<=element["min_alarm"])
					{
						strStatus = "0";
						deviceStatus = "2";
					}
					element["status"] = strStatus;
					break;
				}
			}
		}
		currentDevice["status"] = deviceStatus;
		popupWindow.content = createContent(currentDevice, properties);
	}
	/**
	 * 
	 */
	void showAreaList(List<Map> listProvince)
	{
		DivElement divProvinces = this.shadowRoot.querySelector("#list-province");
		divProvinces.children.clear();
		for(int i=0;i<listProvince.length;i++)
		{
			Map area = listProvince[i];
			//status
			OptionElement selectedStatus = slStatus.children.elementAt(slStatus.selectedIndex);
			String strStatus = null;
			if(selectedStatus.value !="-1")
				strStatus = selectedStatus.value;
			List<Map> devices=getListDevices(area_code:area["area_code"],status:strStatus);
			//new item
			DivElement province = new DivElement();
			province.style.width="100%";
			province.className = "widget widget-activity margin-none";
			province.attributes["data-toggle"]= "collapse-widget";
			province.attributes["data-collapse-closed"]= "true";
			//item head <button type="button" class="btn btn-success hidden-phone">Success</button>
			DivElement provinceHead = new DivElement();
			provinceHead.className = "widget-head";
			provinceHead.appendHtml("<span id=\"spClick\" class=\"collapse-toggle\" style=\"float: left;\"></span>");
			provinceHead.appendHtml("<h4 class=\"heading\">"+listProvince[i]["name"]+"</h4><span class=\"\" style=\"background: #484848;color: #ffffff;border-radius: 3px 3px 3px 3px;text-shadow: none;font-weight: 700;font-size: 10px;padding: 1px 8px;-moz-border-radius: 3px 3px 3px 3px;-webkit-border-radius: 3px 3px 3px 3px;top: 12px;right: 15px;margin-top:10px;line-height: normal;border: 1px solid #484848;box-shadow: 0 0 0 1px #282828 inset;float:right;\">"+devices.length.toString()+"</span>");
			//Add listener
			provinceHead.querySelector("#spClick").onClick.listen(onProvinceToogle);
//			provinceHead.querySelector("#spClick").onClick.listen(onChangeCity);
//			provinceHead.querySelector("#spClick").onClick.listen(onChooseCity);
			//item body
			DivElement provinceBody = new DivElement();
			provinceBody.id = "province-body";
			provinceBody.className = "widget-body collapse";
			provinceBody.style.height = "0px";
			provinceBody.innerHtml = "<div class=\"tab-content\">"
									+"<div class=\"tab-pane\" id=\"tab-pane\">"
									+"<ul class=\"list\" id=\"list\">"
									+"</ul>"
									+"</div>"
									+"</div>";
			Element el=provinceBody.querySelector("#list");
			showDeviceList(el,area, devices);
			//add children
			province.children.add(provinceHead);
			province.children.add(provinceBody);
			divProvinces.children.add(province);
		}
	}
	/*
	 * @author:diennd
	 * @since:26/2/2014
	 * @company:ex-artisan
	 * @version:1.0
	 */
	void onChangeCity(Event event)
	{
		slCity.selectedIndex=2;
	}
	/*
	 * @author:diennd
	 * @since:26/2/2014
	 * @company:ex-artisan
	 * @version:1.0
	 */
	void onProvinceToogle(Event event)
	{
		Element target = event.currentTarget;
		Element headProvince = target.parent;
		Element divProvince = headProvince.parent;
		Element bodyProvince = divProvince.children[1];
		Element tabPane = divProvince.querySelector(".tab-pane");
		if(divProvince.attributes["data-collapse-closed"]=="true")
		{
			divProvince.attributes["data-collapse-closed"]="false";
			bodyProvince.className = "widget-body in collapse";
			bodyProvince.style.height = "auto";
			tabPane.classes.add("active");
		}
		else
		{
			divProvince.attributes["data-collapse-closed"]="true";
			bodyProvince.className = "widget-body collapse";
			bodyProvince.style.height = "0px";
			tabPane.classes.remove("active");
		}
	}
	/*
	 * 
	 */
	void showDeviceList(Element elList,Map area,List<Map> listDevice)
	{
		for(int i=0;i<listDevice.length;i++)
		{
			LIElement item = new LIElement();
			item.className = "double";
			item.attributes["device"] = JSON.encode(listDevice[i]);
			item.attributes["area"] = JSON.encode(area);
			SpanElement spItem=new SpanElement();
			//spItem.className="ellipsis";
			spItem.style.textOverflow="ellipsis";
			spItem.style.whiteSpace="nowrap";
			spItem.style.maxWidth="75%";
			SpanElement spItem1=new SpanElement();
			spItem1.className="glyphicons activity-icon envelope";
			ImageElement img=new ImageElement();
			img.style.width='13px';
			img.style.height='13px';
			//check status
			if(listDevice[i]['status']=="1")
				img.src='style/icons/BlueV.png';
			else if(listDevice[i]['status']=="0")
				img.src='style/icons/GrayX.png';
				else
					img.src='style/icons/RedX.png';
			spItem.text=listDevice[i]['address'];
			spItem1.children.add(img);
			item.children.add(spItem1);
			item.children.add(spItem);
			elList.children.add(item);
			//event
			item.onClick.listen(onDeviceClick);
		}
	}
	/*
	 * 
	 */
	void onDeviceClick(Event event)
	{
		LIElement target = event.currentTarget;
		Map area = JSON.decode(target.attributes["area"]);
		Map device = JSON.decode(target.attributes["device"]);
//		if(slCity.selectedIndex==0)
//		{
			clearMarker();
			//change selected index
			for(int i= 1 ;i<slCity.children.length;i++)
			{
				OptionElement option = slCity.children.elementAt(i);
				if(target.attributes["area"]==option.value)
				{
					slCity.selectedIndex = i;
					break;
				}
			}
			if(slCity.selectedIndex==0)
			{
				Util.showNotifyError("Không tìm thấy địa bàn");
				return;
			}
			//status
			OptionElement selectedStatus = slStatus.children.elementAt(slStatus.selectedIndex);
			String strStatus = null;
			if(selectedStatus.value !="-1")
				strStatus = selectedStatus.value;
			//set map option
			map.zoom = 12;
			map.center = new LatLng(area["lat"], area["lng"]);
			//
			showDevices(getListDevices(area_code: area["area_code"], status: strStatus),isTimer: false);
			showMarkerInfor(device);
//		}
//		else
//		{
//			showMarkerInfor(device);
//		}

	}
	/*
	 * @DienND
	 * @since 13/12/2013
	 * @company: ex-artisan
	 * @Version: 1.0
	 */
	void onChooseCity(Event e)
	{
		updateMap();
	}
	/*
	 * 
	 */
	void clearMarker()
	{
		for(int i = 0;i<markers.length;i++)
		{
			Map data = markers[i];
			Marker marker = data["marker"];
			marker.map = null;
		}
		markers.clear();
	}
	/*
	 * 
	 */
	Marker addMarker(num lat,num lng,String value,{Animation animation:null,String title: null, String icon: null})
	{
		Marker marker = new Marker();
		marker.map = map;
		marker.position = new LatLng(lat, lng);
		if(animation!=null)
			marker.animation = animation;
		if(title!=null)
			marker.title = title;
		if(icon!=null)
			marker.icon = icon;
		//add to list
		markers.add({"marker":marker,"value":value});
		return marker;
	}
	/*
	 * @TuanNA
	 * @since 13/12/2013
	 * @company: ex-artisan
	 * @Version: 1.0
	 */
	void updateMap()
	{
		Map location = null;
		if(slCity.selectedIndex == 0)
		{
			showArea(listArea);
		}
		else
		{
			OptionElement selectedArea = slCity.children.elementAt(slCity.selectedIndex);
			location = JSON.decode(selectedArea.value);
			//clear marker
			clearMarker();
			//set map option
			map.zoom = 12;
			map.center = new LatLng(location["lat"], location["lng"]);
			//status
			OptionElement selectedStatus = slStatus.children.elementAt(slStatus.selectedIndex);
			String strStatus = null;
			if(selectedStatus.value !="-1")
				strStatus = selectedStatus.value;
			//get devices by area
			List<Map> devices = getListDevices(area_code: location["area_code"],status: strStatus);
			//show device
			showDevices(devices);
			//show list
			showAreaList([location]);
		}
		
	}
	List<Map> getListDevices({String area_code:null,String status:null})
	{
		List<Map> devices = AllDevices;
		if(area_code!=null)
		{
			devices =  devices.where((device)
			{
				String device_area = device["area_code"];
				if(device_area.indexOf(area_code)==0)
				{
					return true;
				}
				return false;
			}).toList();
		}
		if(status!=null)
		{
			devices = devices.where((device)
			{
				if(status == device["status"])
				{
					return true;
				}
				return false;
			}).toList();
		}
		return devices;
	}
	
	/*
	 * @Diennd
	 * @since 13/12/2013
	 * @company: ex-artisan
	 * @Version: 1.0
	 */
	void showDevices(List<Map> devices,{bool isTimer:true})
	{
		if(devices!=null&&devices.length>0)
		{	
			if(!isTimer)
			{
				for(int i=0;i<devices.length;i++)
				{
					showDevice(devices[i]);
				}
				return;
			}
			int count = 0;
			Timer timer = new Timer.periodic(new Duration(milliseconds: 50), (Timer timer)
			{

				showDevice(devices[count]);
				count++;
				if(count==devices.length)
				{
					timer.cancel();
				}
			});
		}
	}
	/*
	 * @author:diennd
	 * @since 26/2/2014
	 * @company:ex-artisan
	 * @version:1.0
	 */
	void showDevice(Map device)
	{
		Marker marker;
		//check status
		if(device["status"]=="1")
			marker = addMarker(device["lat"], device["lng"], JSON.encode(device), animation: Animation.DROP, title:device["code"] , icon:"style/icons/marker_blue.png" );
		else if(device["status"]=="0")
				marker = addMarker(device["lat"], device["lng"], JSON.encode(device), animation: Animation.DROP, title:device["code"] , icon:"style/icons/marker_gray.png" );
			else
				marker = addMarker(device["lat"], device["lng"], JSON.encode(device), animation: Animation.DROP, title:device["code"] , icon:"style/icons/marker_red.png" );
		//add listener
		marker.onClick.listen((e)
		{
			showMarkerInfor(device);
		});
	}
	/*
	 * @TuanNA
	 * @since 13/12/2013
	 * @company: ex-artisan
	 * @Version: 1.0
	 */
	void showArea(List<Map> areas)
	{
		//clear marker
		clearMarker();
		//set zoom
		map.zoom = 5;
		map.center = new LatLng(14.058324, 108.277199);
		//show marker
		for(int i =0;i<areas.length;i++)
		{
			//data
			Map area = areas[i];
			//get devices error
			List<Map> devices = getListDevices(area_code: area['area_code'], status: '2');
			//marker
			Marker marker;
			if(devices !=null && devices.length>0)
				marker = addMarker(area["lat"], area["lng"], JSON.encode(area),title:area["name"]);
			else
				marker = addMarker(area["lat"], area["lng"], JSON.encode(area),title:area["name"]);
			marker.onClick.listen((e)
			{
				slCity.selectedIndex = i+1;
				updateMap();
			});
		}
		showAreaList(listArea);
	}
	/*
	 * @TuanNA
	 * @since 13/12/2013
	 * @company: ex-artisan
	 * @Version: 1.0
	 */
	void showMarkerInfor(Map device)
	{
		Marker marker;
				//
		for(int i=0;i<markers.length;i++)
		{
			Map value = JSON.decode(markers[i]["value"]);
			if(value["id"]==device["id"])
			{
				marker = markers[i]["marker"];
				break;
			}
		}
		if(marker ==null)
		{
			Util.showNotifyError("Không tìm thấy địa điểm!");
			return;
		}
		Map devRequest = new Map();
		devRequest['Method'] = 'get_device_detail_by_id';
		devRequest['device_id'] = device['id'];
		
		Responder devResponder = new Responder();
		devResponder.onSuccess.listen((Map res)
		{
			print(res.toString());
			properties = res['properties'];
			popupWindow.content = createContent(device, properties);
			currentDevice = device;
			//show popup
			popupWindow.open(map, marker);
			//connect device
			Map webSocketRequest = new Map();
			webSocketRequest["handle"] = "connect_device";
			webSocketRequest["device_id"] = device['id'];
			AppClient.sendWebsocketMessage(webSocketRequest);
			//show chart
	//		AlarmServiceChart.load().then((_)
	//		{
	//			int sliderValue() => int.parse('8');
	//			// Create a Guage after the library has been loaded.
	//			final DivElement visualization1 = this.shadowRoot.querySelector('#content_right');
	//			AlarmServiceChart gauge = new AlarmServiceChart(visualization1,{ 'title': 'Biểu đồ'},device);
	//		});
		});
		devResponder.onError.listen((Map error)
		{
			Util.showNotifyError(error["message"]);
		});
	AppClient.sendMessage(devRequest, AlarmServiceName.DeviceService, AlarmServiceMethod.POST,devResponder);
		
		
		
	}
	/*
	 * @TuanNA
	 * @since 13/12/2013
	 * @company: ex-artisan
	 * @Version: 1.0
	 */
	InfoWindow createPopupInfor()
	{
		//content
		DivElement content = new DivElement();
		//infor window
		InfoWindow popup = new InfoWindow
		(
			new InfoWindowOptions()
			..content = content
		);
		//return
		return popup;
	}
	
	/**
	 * Create content for info window
	 * @author anhNvt
	 * @param device the data of device
	 * @return content the content for info window
	 */
	dynamic createContent(Map device, List dev){
			//divHeader
			DivElement divHeader=new DivElement();
			divHeader.id="left_header";
			divHeader.style.color="#000";
			divHeader.style.fontFamily="Arial";
			//divHeader.style.width="340px";
			divHeader.style.paddingTop="10px";
			divHeader.style.paddingLeft="10px";
			divHeader.style.paddingBottom="10px";
			//add text head
				SpanElement spCode=new SpanElement();
				spCode.style.color="#fff";
				spCode.style.fontFamily="Open Sans', sans-serif";
				spCode.style.fontSize="18px";
				spCode.text=device['code'];
				
				BRElement br=new BRElement();
				
				SpanElement spAddress=new SpanElement();
				spAddress.style.color="#fff";
				spAddress.style.fontFamily="Open Sans', sans-serif";
				spAddress.style.fontSize="14px";
				spAddress.text= device['address'];
			//left_head add span element
				divHeader.children.add(spCode);
				divHeader.children.add(br);
				divHeader.children.add(spAddress);
			//
				DivElement divScroll=new DivElement();
				divScroll.style.maxHeight="200px";
				divScroll.className="scrollbar";
				divScroll.id="scroll";
				
			//divContent
			DivElement divContent=new DivElement();
			divContent.id="divHeader";
			divContent.style.color="#000";
			divContent.style.fontFamily="Arial";
			divContent.style.width="100%";
					//header
					DivElement divContentHeader=new DivElement();
					divContentHeader.style.width="100%";
					divContentHeader.style.float="left";
					divContentHeader.innerHtml='<p class="l_h_header">Thông tin cảm biến</p>';
					//content
					DivElement divContentContent=new DivElement();
					divContentContent.id="divStatuSensor";
					divContentContent.style.color="#000";
					divContentContent.style.fontFamily="Arial";
					divContentContent.style.height="73%";
					divContentContent.style.width="100%";
					//infor
					//divFooter
			DivElement divFooter=new DivElement();
			divFooter.style.width="100%";
			divFooter.style.fontFamily="Arial";
			divFooter.style.float="left";
			//
			Element p=new Element.p();
			p.className="l_h_header";
			p.text="===============================================";
			divFooter.children.add(p);
			//
			Element pAlert=new Element.p();
			pAlert.className="l_h_header";
			pAlert.text="Cảnh báo";
			divFooter.children.add(pAlert);
					if(dev !=null)
					{
						for(int i=0;i<dev.length;i++)
						{
							DivElement divContentContentleft=new DivElement();
							divContentContentleft.style.float="left";
							divContentContentleft.style.width="50%";
							divContentContentleft.innerHtml='<p class="l_h_status">' + dev[i]['name'].toString();
							//add text
							DivElement divContentContentRight=new DivElement();
							divContentContentRight.style.width="50%";
							divContentContentRight.style.float="left";
							//check status_alarm
							if(dev[i]['alarm_status']=="0")
							{
								divContentContentRight.innerHtml='<p class="l_h_status_error">' + dev[i]['value'].toString()+' '+dev[i]['symbol'];
								//show alert
								Element pInfor=new Element.p();
								p.style.width="100%";
								pInfor.className="l_h_alert";
								if(dev[i]['value']<dev[i]['min_alarm'])
									pInfor.text=dev[i]['name'].toString()+" thấp !!!";
								else if(dev[i]['value']>=dev[i]['max_alarm'])
										pInfor.text=dev[i]['name'].toString()+" cao !!!";
								divFooter.children.add(pInfor);
							}
							else
							{
								divContentContentRight.innerHtml='<p class="l_h_status_good">' + dev[i]['value'].toString()+' '+dev[i]['symbol'];
							}
							divContentContent.children.add(divContentContentleft);
							divContentContent.children.add(divContentContentRight);
						}
					}
					//add div
					divContent.children.add(divContentHeader);
					divContent.children.add(divContentContent);
					divScroll.children.add(divContent);
			//clear
			DivElement divClear=new DivElement();
			divClear.className="Clear";
		//content
		DivElement divPopup = new DivElement();
		divPopup.style.width="350px";
		divPopup.style.border="1px solid #a5a5a5";
		//change color
		switch(device['status'])
		{
			case '0' ://disconnect
				divHeader.style.background = '#c0c0c0';
				divHeader.style.backgroundRepeat="repeat-x";
				divHeader.style.borderBottom="1px solid #a5a5a5";
				divPopup.style.border="1px solid #a5a5a5";
				break;
			case '1'://good
				divHeader.style.background = '#9dce6e';
				divHeader.style.backgroundRepeat="repeat-x";
				divHeader.style.borderBottom="1px solid #689c35";
				divPopup.style.border="1px solid #689c35";
				break;
			case '2'://error
				divHeader.style.background = '#e80d15';
				divHeader.style.backgroundRepeat="repeat-x";
				divHeader.style.borderBottom="1px solid #910207";
				divPopup.style.border="1px solid #910207";
				break;
			default:
				divHeader.style.background = '#9dce6e';
				divHeader.style.backgroundRepeat="repeat-x";
				divHeader.style.borderBottom="1px solid #689c35";
				divPopup.style.border="1px solid #689c35";
				break;
		}
		divPopup.children.add(divHeader);
		divPopup.children.add(divScroll);
		divPopup.children.add(divFooter);
		divPopup.children.add(divClear);
	return divPopup;
	}
	
	/*
	 * @TuanNA
	 * @since 13/12/2013
	 * @company: ex-artisan
	 * @Version: 1.0
	 */
	List<Map> filterByType(List<Map> locations,List<String> listType)
	{
		return locations.where((location){
			for(int i=0;i<listType.length;i++)
			{
				if(listType[i]==location["type"])
				{
					return true;
				}
			}
			return false;
		}).toList();
	}
	/*
	 * @author: ducdienpt
	 * @since: 19/12/2013
	 * @company: ex-artisan
	 * @vesion :1.0
	 */
	List<Map> filterByAreaCode(List<Map> locations,List<String> listAreaId)
	{
		return locations.where((location){
		for(int i=0;i<listAreaId.length;i++)
		{
		if(listAreaId[i]==location["area_code"])
		{
		return true;
		}
		}
		return false;
		}).toList();
	}
	/*
	 * @author:diennd
	 * @since:26/2/2014
	 * @company:ex-artisan
	 * @version:1.0
	 */
	void onChooseStatus(Event e)
	{
		updateMap();
	}
	List<Map> filterDeviceByAreaCode(List<Map> devices,String area_code)
	{
		return devices.where((device){
			String device_area = device["area_code"];
			if(device_area.indexOf(area_code)==0)
			{
				return true;
			}
			return false;
		}).toList();
	}
}