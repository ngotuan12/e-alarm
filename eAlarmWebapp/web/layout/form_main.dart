
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
		slStatus.selectedIndex = 2;
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
	/*
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
		if(slCity.selectedIndex==0)
		{
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
			//
			showMarkerInfor(device);
		}
		else
			showMarkerInfor(device);
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
		//show popup
		popupWindow.open(map, marker);
		//show chart
		AlarmServiceChart.load().then((_)
		{
			int sliderValue() => int.parse('8');
			// Create a Guage after the library has been loaded.
			final DivElement visualization1 = this.shadowRoot.querySelector('#content_right');
			AlarmServiceChart gauge = new AlarmServiceChart(visualization1,{ 'title': 'Biểu đồ'},device);
		});
	}
	/*
	 * @TuanNA
	 * @since 13/12/2013
	 * @company: ex-artisan
	 * @Version: 1.0
	 */
	InfoWindow createPopupInfor()
	{
		//right
		DivElement content_right=new DivElement();
		content_right.id="content_right";
		content_right.style.height="200px";
		content_right.style.width="650px";
		//left
		DivElement content_left=new DivElement();
		content_left.id="content_left";
		content_left.style.color="#000";
		content_left.style.fontFamily="Arial";
		content_left.style.height="200px";
		content_left.style.width="650px";
			//left_header
			DivElement left_header=new DivElement();
			left_header.id="left_header";
			left_header.style.color="#000";
			left_header.style.fontFamily="Arial";
			left_header.style.height="30%";
			left_header.style.width="100%";
				// left
				DivElement left_header_left=new DivElement();
				left_header_left.id="left_header";
				left_header_left.style.color="#000";
				left_header_left.style.fontFamily="Arial";
				left_header_left.style.height="95%";
				left_header_left.style.width="25%";
				left_header_left.style.float="left";
					//create tag select
					SelectElement slProperty=new SelectElement();
					slProperty.id="properties";
					slProperty.style.width="100%";
						//create option
						OptionElement opProperty=new OptionElement();
						opProperty.text="Thuộc tính";
						opProperty.value="-1";
						opProperty.style.width="100%";
						//add option
						slProperty.children.add(opProperty);
					//add tag select
					left_header_left.children.add(slProperty);
				// center
				DivElement left_header_content=new DivElement();
				left_header_content.id="left_header";
				left_header_content.style.color="#000";
				left_header_content.style.fontFamily="Arial";
				left_header_content.style.height="100%";
				left_header_content.style.width="50%";
				left_header_content.style.fontSize="12";
				left_header_content.style.float="left";
					//add text
				left_header_content.innerHtml='''
						<p class="l_h_content" id='title'></p>
					''';
				// right
				DivElement left_header_right=new DivElement();
				left_header_right.id="left_header";
				left_header_right.style.color="#000";
				left_header_right.style.fontFamily="Arial";
				left_header_right.style.height="100%";
				left_header_right.style.width="25%";
				left_header_right.style.float="left";
					//create tag select
					SelectElement slATM=new SelectElement();
					slATM.id="atm";
					slATM.style.width="100%";
					//create option
					OptionElement opATM=new OptionElement();
					opATM.text="ATM liên quan";
					opATM.value="-1";
					opATM.style.width="100%";
					//add option
					slATM.children.add(opATM);
					//add tag select
					left_header_right.children.add(slATM);
				//add left,center,right
				left_header.children.add(left_header_left);
				left_header.children.add(left_header_content);
				left_header.children.add(left_header_right);
			//left_content
			DivElement left_content=new DivElement();
			left_content.id="left_header";
			left_content.style.color="#000";
			left_content.style.fontFamily="Arial";
			left_content.style.height="70%";
			left_content.style.width="100%";

				//create div left,right in div left_content
				DivElement left_content_left=new DivElement();
				left_content_left.id="left_content_left";
				left_content_left.style.color="#000";
				left_content_left.style.fontFamily="Arial";
				left_content_left.style.border="1px solid #616161";
				left_content_left.style.height="95%";
				left_content_left.style.width="48%";
				left_content_left.style.float="left";
					//create head,content
					DivElement divStatusDeviceHeader=new DivElement();
					divStatusDeviceHeader.id="divStatusDevice";
					divStatusDeviceHeader.style.color="#000";
					divStatusDeviceHeader.style.fontFamily="Arial";
					divStatusDeviceHeader.style.borderBottom="1px solid #616161";
					divStatusDeviceHeader.style.height="25%";
					divStatusDeviceHeader.style.width="100%";
					divStatusDeviceHeader.style.background="#F5F5F5";
						//add text
					divStatusDeviceHeader.innerHtml='''
						<p class="l_h_content">Trạng thái thiết bị</p>
						''';
					//divStatusDevice.style.borderBottom="1px solid #616161";
					DivElement divStatusDeviceContent=new DivElement();
					divStatusDeviceContent.id="divStatuSensor";
					divStatusDeviceContent.style.color="#000";
					divStatusDeviceContent.style.fontFamily="Arial";
					divStatusDeviceContent.style.height="73%";
					divStatusDeviceContent.style.width="100%";
					//add text
					divStatusDeviceContent.innerHtml='''
						<p class="l_h_status" id="aaaaaaa">Điện áp ắc quy(V):tốt</p>
						<p class="l_h_status">Nhiệt độ:31C</p>
						<p class="l_h_status">Độ ẩm:80%</p>
						<p class="l_h_status">Thiết bị trên bo mạch:Bình thường</p>
						<p class="l_h_status">Thiết bị trên Ethernet:Bình thường</p>
						''';
					//add div
					left_content_left.children.add(divStatusDeviceHeader);
					left_content_left.children.add(divStatusDeviceContent);
				//create div left,right in div left_content
				DivElement left_content_right=new DivElement();
				left_content_right.id="left_content_right";
				left_content_right.style.color="#000";
				left_content_right.style.fontFamily="Arial";
				left_content_right.style.border="1px solid #616161";
				left_content_right.style.height="95%";
				left_content_right.style.width="48%";
				left_content_right.style.float="right";
					//create head,content
					DivElement divStatuSensorHeader=new DivElement();
					divStatuSensorHeader.id="divStatusDevice";
					divStatuSensorHeader.style.color="#000";
					divStatuSensorHeader.style.fontFamily="Arial";
					divStatuSensorHeader.style.borderBottom="1px solid #616161";
					divStatuSensorHeader.style.height="25%";
					divStatuSensorHeader.style.width="100%";
					divStatuSensorHeader.style.background="#F5F5F5";
						//add text
						divStatuSensorHeader.innerHtml='''
						<p class="l_h_content">Trạng thái cảm biến</p>
						''';
					//divStatusDevice.style.borderBottom="1px solid #616161";
					DivElement divStatuSensorContent=new DivElement();
					divStatuSensorContent.id="divStatuSensor";
					divStatuSensorContent.style.color="#000";
					divStatuSensorContent.style.fontFamily="Arial";
					divStatuSensorContent.style.height="73%";
					divStatuSensorContent.style.width="100%";
						//create div
						DivElement divStatuSensorContentup=new DivElement();
						divStatuSensorContentup.id="divStatuSensorContentup";
						divStatuSensorContentup.style.color="#000";
						divStatuSensorContentup.style.fontFamily="Arial";
						divStatuSensorContentup.style.height="50%";
						divStatuSensorContentup.style.width="100%";
							DivElement divStatuSensorContentup1=new DivElement();
							divStatuSensorContentup1.id="divStatuSensorContentup1";
							divStatuSensorContentup1.style.color="#000";
							divStatuSensorContentup1.style.fontFamily="Arial";
							divStatuSensorContentup1.style.height="100%";
							divStatuSensorContentup1.style.width="34%";
							divStatuSensorContentup1.style.float="left";
								//text
							divStatuSensorContentup1.innerHtml='''
								<p class="l_h_content_1"><img class="l_h_content_img" src="style/icons/ic_green.png" alt="blue"></p>
								<p class="l_h_content_1">Cửa ngoài</p>
								''';
								//create div
							DivElement divStatuSensorContentup2=new DivElement();
							divStatuSensorContentup2.id="divStatuSensorContentup2";
							divStatuSensorContentup2.style.color="#000";
							divStatuSensorContentup2.style.fontFamily="Arial";
							divStatuSensorContentup2.style.height="100%";
							divStatuSensorContentup2.style.width="33%";
							divStatuSensorContentup2.style.float="left";
								//text
							divStatuSensorContentup2.innerHtml='''
								<p class="l_h_content_1"><img class="l_h_content_img" src="style/icons/ic_red.png" alt="red"></p>
								<p class="l_h_content_1">Nhiệt độ</p>
								''';
							DivElement divStatuSensorContentup3=new DivElement();
							divStatuSensorContentup3.id="divStatuSensorContentup3";
							divStatuSensorContentup3.style.color="#000";
							divStatuSensorContentup3.style.fontFamily="Arial";
							divStatuSensorContentup3.style.height="100%";
							divStatuSensorContentup3.style.width="33%";
							divStatuSensorContentup3.style.float="left";
								//text
							divStatuSensorContentup3.innerHtml='''
								<p class="l_h_content_1"><img class="l_h_content_img" src="style/icons/ic_green.png" alt="blue"></p>
								<p class="l_h_content_1">Cửa trên</p>
								''';
							//add
							divStatuSensorContentup.children.add(divStatuSensorContentup1);
							divStatuSensorContentup.children.add(divStatuSensorContentup2);
							divStatuSensorContentup.children.add(divStatuSensorContentup3);
						DivElement divStatuSensorContentdown=new DivElement();
						divStatuSensorContentdown.id="divStatuSensorContentdown";
						divStatuSensorContentdown.style.color="#000";
						divStatuSensorContentdown.style.fontFamily="Arial";
						divStatuSensorContentdown.style.height="50%";
						divStatuSensorContentdown.style.width="100%";
							//create div
							DivElement divStatuSensorContentdown1=new DivElement();
							divStatuSensorContentdown1.id="divStatuSensorContentup1";
							divStatuSensorContentdown1.style.color="#000";
							divStatuSensorContentdown1.style.fontFamily="Arial";
							divStatuSensorContentdown1.style.height="100%";
							divStatuSensorContentdown1.style.width="34%";
							divStatuSensorContentdown1.style.float="left";
							//text
							divStatuSensorContentdown1.innerHtml='''
							<p class="l_h_content_1"><img class="l_h_content_img" src="style/icons/ic_green.png" alt="blue"></p>
							<p class="l_h_content_1">Cửa ngoài</p>
							''';
							//create div
							DivElement divStatuSensorContentdown2=new DivElement();
							divStatuSensorContentdown2.id="divStatuSensorContentup2";
							divStatuSensorContentdown2.style.color="#000";
							divStatuSensorContentdown2.style.fontFamily="Arial";
							divStatuSensorContentdown2.style.height="100%";
							divStatuSensorContentdown2.style.width="33%";
							divStatuSensorContentdown2.style.float="left";
							//text
							divStatuSensorContentdown2.innerHtml='''
							<p class="l_h_content_1"><img class="l_h_content_img" src="style/icons/ic_green.png" alt="bule"></p>
							<p class="l_h_content_1">Cửa kẹt</p>
							''';
							DivElement divStatuSensorContentdown3=new DivElement();
							divStatuSensorContentdown3.id="divStatuSensorContentup3";
							divStatuSensorContentdown3.style.color="#000";
							divStatuSensorContentdown3.style.fontFamily="Arial";
							divStatuSensorContentdown3.style.height="100%";
							divStatuSensorContentdown3.style.width="33%";
							divStatuSensorContentdown3.style.float="left";
							//text
							divStatuSensorContentdown3.innerHtml='''
							<p class="l_h_content_1"><img class="l_h_content_img" src="style/icons/ic_red.png" alt="red"></p>
							<p class="l_h_content_1">Tiếp đất</p>
							''';
							//add
							divStatuSensorContentdown.children.add(divStatuSensorContentdown1);
							divStatuSensorContentdown.children.add(divStatuSensorContentdown2);
							divStatuSensorContentdown.children.add(divStatuSensorContentdown3);
						//add div
						divStatuSensorContent.children.add(divStatuSensorContentup);
						divStatuSensorContent.children.add(divStatuSensorContentdown);
					//add div
					left_content_right.children.add(divStatuSensorHeader);
					left_content_right.children.add(divStatuSensorContent);
				//add left right in div left_content
				left_content.children.add(left_content_left);
				left_content.children.add(left_content_right);
			//content_left add
			content_left.children.add(left_header);
			content_left.children.add(left_content);
		//content
		DivElement content = new DivElement();
		content.id="solo";
		content.style.width="650px";
		content.style.height="400px";
		content.children.add(content_left);
		content.children.add(content_right);
		//infor window
		InfoWindow popup = new InfoWindow
		(
			new InfoWindowOptions()
			..content = content
		);
		//return
		return popup;
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