
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
	SelectElement slDistrict;
	OListElement olDevicesGood;
	OListElement olDevicesError;
	LIElement error;
	LIElement normal;
	List<Map> listArea;
	InfoWindow popupWindow;
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
		slDistrict=this.shadowRoot.querySelector("#district-location");
		olDevicesGood=this.shadowRoot.querySelector("#ol-devicesGood");
		olDevicesError=this.shadowRoot.querySelector("#ol-devicesError");
		normal=this.shadowRoot.querySelector("#normal");
		error=this.shadowRoot.querySelector("#error");
		normal.onClick.listen(changeColorNormal);
		error.onClick.listen(changeColorError);
		//Event
		slCity.onChange.listen(onChooseCity);
		slDistrict.onChange.listen(onChooseDistric);
		//popup window
		popupWindow = createPopupInfor();
		//init
		init();
		//sys data
//		var oneSecond = new Duration(seconds:60);
//		mytimer = new Timer.periodic(oneSecond, updateData);
		//default background for status atm
		error.style.background='#d71c00';
		normal.style.background='#64625f';
	}
	/*
	 * @DienND
	 * @since 27/12/2013
	 * @company ex-artisan
	 * @version 1.0
	 */
	void changeColorNormal(Event e)
	{
		normal.style.background='#008000';
		error.style.background='#64625f';
	}
	/*
	 * @DienND
	 * @since 27/12/2013
	 * @company ex-artisan
	 * @version 1.0
	 */
	void changeColorError(Event e)
	{
		error.style.background='#d71c00';
		normal.style.background='#64625f';
	}
	/*
	 * @DienND
	 * @since 27/12/2013
	 * @company ex-artisan
	 * @version 1.0
	 */
	void updateData(Timer _)
	{
		//remove all li in ul-Devices
		olDevicesGood.children.clear();
		olDevicesError.children.clear();
		//selected
		OptionElement opt = slDistrict.children.elementAt(slDistrict.selectedIndex);
		//check selectedIndex
		if(slDistrict.selectedIndex==0)
		{
			//remove all option in slDistric
			slDistrict.children.clear();
			//add default
			OptionElement op=new OptionElement();
			op.value="-1";
			op.text="Quận/Huyện";
			slDistrict.children.add(op);
			OptionElement opt = slCity.children.elementAt(slCity.selectedIndex);
			if(slCity.selectedIndex!=0)
			{
				Map area = JSON.decode(opt.value);
				if(area["Type"].toString()=="2")
				{
					showLocation(area);
				}
			}
		}
		else
		{
			//data
			Map area = JSON.decode(opt.value);
			if(area["Type"]=="3")
			{
				//show device
				//change center
				final mapOptions = new MapOptions()
				..zoom = 14
				..center = new LatLng(area["Lat"], area["Lng"])
				..mapTypeId = MapTypeId.ROADMAP
				..styles = styles;
				final map = new GMap(this.shadowRoot.querySelector("#map-canvas"), mapOptions);
				getAllDeviceByAreaId(area, map);
			}
		}
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
		//Google map
		final mapOptions = new MapOptions()
		..keyboardShortcuts=true
		..zoom = 5
		..center = new LatLng(14.058324, 108.277199)
		..mapTypeId = MapTypeId.ROADMAP
		..styles = styles;
		final map = new GMap(this.shadowRoot.querySelector("#map-canvas"), mapOptions);
		//get data
		Map request = new Map();
		request["Method"] = "GetAllAreaActive";
		//Listen
		Responder responder = new Responder();
		//success
		responder.onSuccess.listen((Map response)
		{
			List<Map> temp=response['ListAreaActive'];
			listArea = filterByType(temp, ["2"]);
			//init Option Select
			for(int i=0;i<listArea.length;i++)
			{
				//option
				Map element= listArea[i];
				String name=element['Name'];
				OptionElement op = new Element.option();
				op.text=name;
				op.value = JSON.encode(element);
				//add are
				slCity.children.add(op);
			}
			//Show area on map
			showArea(listArea, map);
		});
		//error
		responder.onError.listen((String strError)
		{
			print(strError);
		}
		);
		//send to server
		AppClient.sendMessage(request, AlarmServiceName.AreaService, AlarmServiceMethod.POST,responder);
	}
	/*
	 * @DienND
	 * @since 13/12/2013
	 * @company: ex-artisan
	 * @Version: 1.0
	 */
	void onChooseCity(Event e)
	{
		//selected
		OptionElement opt = slCity.children.elementAt(slCity.selectedIndex);
		//remove all option in slDistric
		slDistrict.children.clear();
		//add default
		OptionElement op=new OptionElement();
		op.value="-1";
		op.text="Quận/Huyện";
		slDistrict.children.add(op);
		//remove all li in ul-Devices
		olDevicesGood.children.clear();
		olDevicesError.children.clear();
		//check selectedIndex
		if(slCity.selectedIndex==0)
		{
		init();
		}
		else
		{
			//data
			Map area = JSON.decode(opt.value);
			if(area["Type"]=="2")
			{
				showLocation(area);
			}
		}
	}
	/*
	 * @DienND
	 * @since 13/12/2013
	 * @company: ex-artisan
	 * @Version: 1.0
	 */
	void onChooseDistric(Event e)
	{
		//remove all li in ul-Devices
		olDevicesGood.children.clear();
		olDevicesError.children.clear();
		//selected
		OptionElement opt = slDistrict.children.elementAt(slDistrict.selectedIndex);
		//check selectedIndex
		if(slDistrict.selectedIndex==0)
		{
			//remove all option in slDistric
			slDistrict.children.clear();
			//add default
			OptionElement op=new OptionElement();
			op.value="-1";
			op.text="Quận/Huyện";
			slDistrict.children.add(op);
			OptionElement opt = slCity.children.elementAt(slCity.selectedIndex);
			Map area = JSON.decode(opt.value);
			if(area["Type"]=="2")
			{
				showLocation(area);
			}
		}
		else
		{
			//data
			Map area = JSON.decode(opt.value);
			if(area["Type"]=="3")
			{
				
				//show device
				//change center
				final mapOptions = new MapOptions()
				..zoom = 14
				..center = new LatLng(area["Lat"], area["Lng"])
				..mapTypeId = MapTypeId.ROADMAP
				..styles = styles;
				final map = new GMap(this.shadowRoot.querySelector("#map-canvas"), mapOptions);
				getAllDeviceByAreaId(area, map);
			}
		}
	}
	/*
	 * @TuanNA
	 * @since 13/12/2013
	 * @company: ex-artisan
	 * @Version: 1.0
	 */
	void showLocation(Map location)
	{
		//change center
		final mapOptions = new MapOptions()
		..zoom = 14
		..center = new LatLng(location["Lat"], location["Lng"])
		..mapTypeId = MapTypeId.ROADMAP
		..styles = styles;
		final map = new GMap(this.shadowRoot.querySelector("#map-canvas"), mapOptions);
		//show area
		getAllAreaById(location,map);
	}
	/*
	 * @ducdienpt
	 * @since 25/12/2013
	 * @company :ex-artisan
	 * @version :1.0
	 */
	void getAllDevice(final GMap map,Map area)
	{
	//get data
		Map request = new Map();
		request["Method"] = "onGetAllDevicesWithPro";
		//Listen
		Responder responder = new Responder();
		//success
		responder.onSuccess.listen((Map response)
		{
		List<Map> temp=response['all_devices_byarea_info'];
		//filter order areaId
		List<Map> listDevices = filterByAreaCode(temp, [area['Code']]);
		//Show devices on map
		if(listDevices !=null)
		showDevices(listDevices, map);
		});
		//error
		responder.onError.listen((String strError)
		{
		print(strError);
		}
		);
		//send to server
		AppClient.sendMessage(request, AlarmServiceName.DeviceService, AlarmServiceMethod.POST,responder);
	}
	/*
	 * @ducdienpt
	 * @Since 15/12/2013
	 * @company :ex-artisan
	 * @Version 1.0
	 */
	void getAllDeviceByAreaId(Map area,final GMap map)
	{
		//get data
		Map request = new Map();
		request["Method"] = "onGetAllDevicesByAreaID";
		request["area_id"]=area["ID"];
		//Listen
		Responder responder = new Responder();
		//success
		responder.onSuccess.listen((Map response)
		{
		List<Map> listDevices=response['all_devices_byarea_info'];
		//Show devices on map
		if(listDevices !=null)
			showDevices(listDevices, map);
		});
		//error
		responder.onError.listen((String strError)
		{
		print(strError);
		}
		);
		//send to server
		AppClient.sendMessage(request, AlarmServiceName.DeviceService, AlarmServiceMethod.POST,responder);

	}
	/*
	 * @ducdienpt
	 * @since 18/12/2013
	 * @company:ex-artisan
	 * @version : 1.0
	 */
	void getAllAreaById(Map area,final GMap map){
		//get data
		Map request = new Map();
		request["Method"] = "GetActiveByParent";
		request["ID"]=area["ID"];
		//Listen
		Responder responder = new Responder();
		//success
		responder.onSuccess.listen((Map response)
		{
		List<Map> listAreas=response['ListArea'];
		//Show areas on Select
		if(listAreas !=null)
		{
			for(int i=0;i<listAreas.length;i++)
			{
				//option
				Map element= listAreas[i];
				String name=element['Name'];
				OptionElement op = new Element.option();
				op.text=name;
				op.value = JSON.encode(element);
				//add are
				slDistrict.children.add(op);
			}
		}
		//show device
		//getAllDeviceByAreaId(area, map);
		getAllDevice(map, area);
		});
		
		//error
		responder.onError.listen((String strError)
		{
		print(strError);
		}
		);
		//send to server
		AppClient.sendMessage(request, AlarmServiceName.AreaService, AlarmServiceMethod.POST,responder);
	}
	/*
	 * @Diennd
	 * @since 13/12/2013
	 * @company: ex-artisan
	 * @Version: 1.0
	 */
	void showDevices(List<Map> devices,final GMap map)
	{
			for(int i =0;i<devices.length;i++)
			{
				Map device = devices[i];
				//add into ul_devices
				LIElement liDevice=new LIElement();
				liDevice.style.marginTop="5px";
				liDevice.style.marginLeft="5px";
				liDevice.style.borderBottom="1px solid #616161";
					ImageElement img=new ImageElement();
						img.style.width='16px';
						img.style.height='16px';
						img.style.marginRight='10px';
					AnchorElement aDevice=new AnchorElement();
					aDevice.style.textDecoration="none";
					aDevice.style.color="#fff";
					aDevice.href="#";
					aDevice.onMouseOver.listen((event)=>aDevice.style.color='gray');
					aDevice.onMouseLeave.listen((event)=>aDevice.style.color='#fff');
					liDevice.onMouseOver.listen((event)=>aDevice.style.color='gray');
					liDevice.onMouseLeave.listen((event)=>aDevice.style.color='#fff');
					//split data
					
					List parts = device['address'].split(',');
					String temp="";
					if(slDistrict.selectedIndex>0){
						for(int i=0;i<parts.length-2;i++)
						{
							temp+=parts[i]+",";
						}
					}
					else
					{
						for(int i=0;i<parts.length-1;i++)
						{
						temp+=parts[i]+",";
						}
					}
					aDevice.text=temp.substring(0,temp.length-1);
				//check status
				if(device["status"]=="1")
				{
					img.src='images/ATMs/BlueV.png';
					//aDevice.children.add(img);
					liDevice.children.add(img);
					liDevice.children.add(aDevice);
					olDevicesGood.children.add(liDevice);
					//new marker
					Marker marker = new Marker
					(
						new MarkerOptions()
						..position = new LatLng(device["lat"], device["lng"])
						..map = map
						..title = device["code"]
						..animation = Animation.DROP
						..icon='images/ATMs/marker_blue.png'
					);
					//add listener
					liDevice.onClick.listen((event) =>showMarkerInfor(device,marker,map));
					marker.onClick.listen((e) 
					{
						showMarkerInfor(device,marker,map);
					});
				}
				else
				{
					img.src='images/ATMs/RedX.png';
					//aDevice.children.add(img);
					liDevice.children.add(img);
					liDevice.children.add(aDevice);
					olDevicesError.children.add(liDevice);
					//new marker
					Marker marker = new Marker
					(
						new MarkerOptions()
						..position = new LatLng(device["lat"], device["lng"])
						..map = map
						..title = device["code"]
						..animation = Animation.DROP
						..icon='images/ATMs/marker_red.png'
					);
					//add listener
					liDevice.onClick.listen((event) =>showMarkerInfor(device,marker,map));
					marker.onClick.listen((e) 
					{
						showMarkerInfor(device,marker,map);
					});
				}
			}
	}
	/*
	 * @TuanNA
	 * @since 13/12/2013
	 * @company: ex-artisan
	 * @Version: 1.0
	 */
	void showArea(List<Map> areas,final GMap map)
	{
		for(int i =0;i<areas.length;i++)
		{
			
			//data
			Map area = areas[i];
			//marker
			Marker marker = new Marker
			(
				new MarkerOptions()
				..position = new LatLng(area["Lat"], area["Lng"])
				..map = map
				..title = area["Name"]
			);
			marker.onClick.listen((e) 
			{
				slCity.selectedIndex = i+1;
				showLocation(areas[i]);
			});
		}
	}
	/*
	 * @TuanNA
	 * @since 13/12/2013
	 * @company: ex-artisan
	 * @Version: 1.0
	 */
	void showMarkerInfor(Map device,Marker marker,final GMap map)
	{
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
								<p class="l_h_content_1"><img class="l_h_content_img" src="images/ATMs/ic_green.png" alt="blue"></p>
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
								<p class="l_h_content_1"><img class="l_h_content_img" src="images/ATMs/ic_red.png" alt="red"></p>
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
								<p class="l_h_content_1"><img class="l_h_content_img" src="images/ATMs/ic_green.png" alt="blue"></p>
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
							<p class="l_h_content_1"><img class="l_h_content_img" src="images/ATMs/ic_green.png" alt="blue"></p>
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
							<p class="l_h_content_1"><img class="l_h_content_img" src="images/ATMs/ic_green.png" alt="bule"></p>
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
							<p class="l_h_content_1"><img class="l_h_content_img" src="images/ATMs/ic_red.png" alt="red"></p>
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
				if(listType[i]==location["Type"])
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
	
}