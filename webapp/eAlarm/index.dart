library eAlarm;

import 'dart:html';
import 'dart:convert';
import 'package:google_maps/google_maps.dart';
import 'dart:async';
import 'src/util.dart';

SelectElement slCity;
SelectElement District;
List<Map> listArea;
InfoWindow popupWindow;

	/*
	 * @author ducdienpt
	 * @since:12/12/2013
	 */
	void main()
	{
		slCity=querySelector("#city-location");
		District=querySelector("#district-location");
		slCity.onChange.listen(onChooseCity);
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
		//Google map
		final mapOptions = new MapOptions()
		..zoom = 5
		..center = new LatLng(14.058324, 108.277199)
		..mapTypeId = MapTypeId.ROADMAP;
		final map = new GMap(querySelector("#map-canvas"), mapOptions);
		//get data
		Map request = new Map();
		request["Method"] = "GetActiveByParent";
		request["ID"]="1";
		String strJSON = JSON.encode(request);
		//Listen
		Responder responder = new Responder();
		//success
		responder.onSuccess.listen((Map response)
		{
			print(response);
			List<Map> temp=response['ListArea'];
			listArea = filterByType(temp, ["1","2","3"]);
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
		AppClient.sendMessage(strJSON, AlarmServiceName.AreaService, AlarmServiceMethod.POST,responder);
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
		//data
		Map area = JSON.decode(opt.value);
		if(area["Type"]=="2")
		{
			showLocation(area);
		}
		else if(area["Type"]=="3")
		{
			final mapOptions = new MapOptions()
			..zoom = 5
			..center = new LatLng(area["Lat"], area["Lng"])
			..mapTypeId = MapTypeId.ROADMAP;
			final map = new GMap(querySelector("#map-canvas"), mapOptions);
			//show area
			showArea(listArea, map);
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
		//remove marker
		//change center
		final mapOptions = new MapOptions()
		..zoom = 12
		..center = new LatLng(location["Lat"], location["Lng"])
		..mapTypeId = MapTypeId.ROADMAP;
		final map = new GMap(querySelector("#map-canvas"), mapOptions);
		//get devices
		List<Map> listDevice = new List<Map>();
		Map device = new Map();
		device["Lat"] = 21.0386761;
		device["Lng"] = 105.8877494;
		device["code"] = "ATM001";
		listDevice.add(device);
		//show device
		showDevices(listDevice,map);
	}
	/*
	 * @TuanNA
	 * @since 13/12/2013
	 * @company: ex-artisan
	 * @Version: 1.0
	 */
	void showDevices(List<Map> devices,final GMap map)
	{
		for(int i =0;i<devices.length;i++)
		{
			Map device = devices[i];
			//new marker
			Marker marker = new Marker
			(
				new MarkerOptions()
				..position = new LatLng(device["Lat"], device["Lng"])
				..map = map
				..title = device["code"]
				..animation = Animation.DROP
				..icon='images/ATMs/marker_red.png'
			);
			//add listener
			marker.on.click.add((e) 
			{
				showMarkerInfor(marker,map);
			});
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
			if(area["Type"]=="3")
			{
				continue;
			}
			//marker
			Marker marker = new Marker
			(
				new MarkerOptions()
				..position = new LatLng(area["Lat"], area["Lng"])
				..map = map
				..title = area["Name"]
			);
			marker.on.click.add((e) 
			{
				slCity.selectedIndex = i;
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
	void showMarkerInfor(Marker marker,final GMap map)
	{
		//show popup
		popupWindow.open(map, marker);
		//show chart
		AlarmServiceChart.load().then((_) 
		{
		int sliderValue() => int.parse('8');
		// Create a Guage after the library has been loaded.
		final DivElement visualization1 = querySelector('#content_right');
		AlarmServiceChart gauge = new AlarmServiceChart(visualization1, "Slider", 4,{ 'title': 'Thông số'});
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
		content_right.style.height="230px";
		content_right.style.width="460px";
		//left
		DivElement content_left=new DivElement();
		content_left.id="content_left";
		content_left.style.float="left";
		content_left.style.color="#000";
		content_left.style.fontFamily="Arial";
		content_left.style.height="120px";
		content_left.style.width="460px";
 		content_left.innerHtml='''
						<p class='infoWindow'>ATM 58:Nguyễn Phong Sắc</p>
						<table border='0' class="table">
						<tr>
						<th class='infoWindow1'>Thông tin</th>
						<th class='infoWindow1'>Trạng Thái</th>
						</tr>
						<tr>
						<td class='infoWindow1'>Cửa vào/ra</td>
						<td class='infoWindow1'><img class="icon" src='images/ATMs/marker_red.png' alt='red'/></td>
						</tr>
						<tr>
						<td class='infoWindow1'>Độ ẩm</td>
						<td class='infoWindow1'><img class="icon" src='images/ATMs/marker_blue.png' alt='red'/></td>
						 </tr>
						</table>
						''';
		//content
		DivElement content = new DivElement();
		content.id="solo";
		content.style.width="460px";
		content.style.height="350px";
 		content.children.add(content_right);
		content.children.add(content_left);
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
//		return locations.where((location) => location["Type"]==strType).toList();
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
