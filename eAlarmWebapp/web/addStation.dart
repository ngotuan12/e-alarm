library eAlarm;

import 'dart:html';
import 'dart:convert';
import 'package:google_maps/google_maps.dart';
import 'dart:async';
import 'src/util.dart';
ButtonElement btnSave;
ButtonElement bntSearch;
InputElement inAdress;
InputElement inLong;
InputElement inLat;
SelectElement selStatus;
SelectElement seArea;
InputElement inTemperature;
InputElement inHumidity;
InputElement inFullAddress;
InputElement inCode;
CheckboxInputElement chkTemperature;
CheckboxInputElement chkHumidity;
CheckboxInputElement chkVibration;
CheckboxInputElement chkBrightness;
List<Map> listArea;
bool Error=false;
	/*
	 * @author ducdienpt
	 * @since:12/12/2013
	 */
	void main()
	{
		//Get id from addStation.html
		btnSave=querySelector("#Save2");
		bntSearch=querySelector("#search");
		inFullAddress=querySelector("#fullAddress");
		inAdress=querySelector("#address");
		inLong=querySelector("#Long");
		inLat=querySelector("#Lat");
		selStatus=querySelector("#status");
		seArea=querySelector("#area");
		inTemperature=querySelector("#temperature");
		inHumidity=querySelector("#humidity");
		inCode=querySelector("#code");
		
		chkTemperature=querySelector("#temperature");
		chkHumidity=querySelector("#humidity");
		chkVibration=querySelector("#vibration");
		chkBrightness=querySelector("#brightness");
		
		//Event
		btnSave.onClick.listen(checkData);
		seArea.onChange.listen(showAddress);
		inAdress.onChange.listen(showAddress);
		//Load
		init();
		ShowAreas();
	}
	/*
	 * @Author ducdienpt
	 * @since 18/12/2013
	 * @version 1.0
	 */
	//Google map show
	void init()
	{
	final mapOptions = new MapOptions()
	..keyboardShortcuts=true
	..zoom = 5
	..center = new LatLng(16.058324, 105.277199)
	..mapTypeId = MapTypeId.ROADMAP;
	final map = new GMap(querySelector("#show-map"), mapOptions);
	}
	/*
	 * @Author ducdienpt
	 * @since 18/12/2013
	 * @Version 1.0
	 */
	//Check data from addStattion before Send to server
	void checkData(Event e)
	{
		Error=false;
		//lat log not null
		if(inLong.value == "" || inLat.value == "")
		{
			querySelector("#ErrorLocation").text="Chưa có tọa độ";
			Error=true;
		}
		if(Error==false)
		{
			//get data
			Map request = new Map();
			request["Method"] = "onCreateNewDevices";
			request["code"]=inCode.value;
			//get data from option chosen
			OptionElement opt = seArea.children.elementAt(seArea.selectedIndex);
			//data
			Map area = JSON.decode(opt.value);
			request["area_id"]=area['ID'];
			request["area_code"]=area['Code'];
			request["address"]=inFullAddress.value;
			request["lat"]=double.parse(inLat.value);
			request["lng"]=double.parse(inLong.value);
			OptionElement status=selStatus.children.elementAt(selStatus.selectedIndex);
			request["status"]=status.value;
			String strJSON = JSON.encode(request);
			print(strJSON);
			//Listen
			Responder responder = new Responder();
			//success
			responder.onSuccess.listen((Map response)
			{
				querySelector("#").text="Thêm mới trạm thành công ";
			});
			//error
			responder.onError.listen((String strError)
			{
			print(strError);
			}
			);
			//send to server
			//AppClient.sendMessage(strJSON, AlarmServiceName.DeviceService, AlarmServiceMethod.POST,responder);
			querySelector("#suscess").text="Thêm mới trạm thành công ";
			querySelector("#suscess").style.color="#8EC657";
			seArea.selectedIndex=0;
			inFullAddress.value="";
			inLat.value="";inLong.value="";inCode.value="";inAdress.value="";
			selStatus.selectedIndex=0;
			
		}
	}
	/*
	 * @author ducdienpt
	 * @Since 18/12/2013
	 * @Version 1.0
	 */
	//Show all areas
	void ShowAreas()
	{
		//get data
		Map request = new Map();
		request["Method"] = "GetAllAreaActive";
		//Listen
		Responder responder = new Responder();
		//success
		responder.onSuccess.listen((Map response)
		{
			listArea=response['ListAreaActive'];
			//filter by type
			List<Map> areaProvinces=filterByType(listArea, ["2"]);
			//add into tag select
			for(int i=0;i<areaProvinces.length;i++)
			{
			//option
				Map element= areaProvinces[i];
				String name=element['Name'];
				OptionElement op = new Element.option();
				op.text='==>'+name;
				op.value = JSON.encode(element);
				//add are
				seArea.children.add(op);
				//filter by ParentID
				List<Map> areaDistricts=filterByParentID(listArea, [element['ID']]);
				for(int i=0;i<areaDistricts.length;i++)
				{
					//option
					Map element= areaDistricts[i];
					String name=element['Name'];
					OptionElement op = new Element.option();
					op.text='==> ==>'+name;
					op.value = JSON.encode(element);
					//add are
					seArea.children.add(op);
				}
			}
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
	 * @Author ducdienpt
	 * @Since 18/12/2013
	 * @Version 1.0
	 */
	void showAddress(Event e)
	{
		querySelector("#ErrorLocation").text="";
		if(seArea.selectedIndex==0)
		{
			inFullAddress.value="";
			inAdress.value="";
			inAdress.disabled=true;
			bntSearch.disabled=true;
		}
		else
		{
				//refresh text
				inFullAddress.value="";
				//selected
				OptionElement opt = seArea.children.elementAt(seArea.selectedIndex);
				//data
				Map area = JSON.decode(opt.value);
				inFullAddress.value=area["FullName"];
				inAdress.disabled=false;
				if(inAdress.disabled ==false && inAdress.value !="")
				{
					String temp=inFullAddress.value;
					temp=inAdress.value+','+temp;
					inFullAddress.value=temp;
					bntSearch.disabled=false;
				}
		}
	}
	/*
	 * @ducdienpt
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
	 * @ducdienpt
	 * @since 18/12/2013
	 * @company: ex-artisan
	 * @version :1.0
	 */
	filterByParentID(List<Map> locations,List<String> listType)
	{
		return locations.where((location){
			for(int i=0;i<listType.length;i++)
			{
				if(listType[i]==location["ParentID"])
				{
					return true;
				}
			}
			return false;
		}).toList();
	}
	