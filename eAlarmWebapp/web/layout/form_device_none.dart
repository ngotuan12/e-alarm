import 'package:polymer/polymer.dart';
import 'form_device.dart';
import 'dart:html';
import 'dart:convert';
import '../src/util.dart';
import 'dart:math' as Math;
@CustomTag('form-device-none')
class FormDeviceNone extends PolymerElement
{
	UListElement ulPagination;
	InputElement inCurrent_page;
	InputElement inShow_per_page;
	SelectElement slAreas;
	ButtonElement btnSearch;
	AnchorElement ancAdd;
	int currentSelectedPageIndex = 0;
	int show_per_page;
	// This lets the Bootstrap CSS "bleed through" into the Shadow DOM
	// of this element.
	bool get applyAuthorStyles => true;
	@observable List areas=[{"code":"HN","name":"Hà Nội","full_name":"Hà Nội,Việt Nam","type":"2"},{"code":"HCM","name":"TP HCM","full_name":"TP Hồ Chí Minh,Việt Nam","type":"2"},{"code":"DN","name":"Đà Nẵng","full_name":"Đà Nẵng,Việt Nam","type":"2"},{"code":"HP","name":"Hải Phòng","full_name":"Hải Phòng,Việt Nam","type":"2"},{"code":"PT","name":"Phú Thọ","full_name":"Phú Thọ,Việt Nam","type":"2"}];
	@observable static List allDevice=toObservable([]);
	@observable static List listDevices=toObservable([]);
	@observable List CurrentDevices=toObservable([]);
	@observable List devices=toObservable([]);
	List<LIElement> pages = [];
	FormDeviceNone.created() : super.created();
	enteredView() 
	{
		super.enteredView();
		btnSearch=this.shadowRoot.querySelector("#search");
		ancAdd=this.shadowRoot.querySelector("#add");
		//action
		ancAdd.onClick.listen((Event)=>FormDevice.getCurrentAction="ADD");
		btnSearch.onClick.listen(Search);
		
		init();
	}
	/*
	 * @author:diennd
	 * @date:20/1/2014
	 * @company:Ex-artisan
	 * @version:1.0
	 */
	void init()
	{
	  //clear all device
	  allDevice=toObservable([]);
	  listDevices=toObservable([]);
		//get data
			Map request = new Map();
			request["Method"] = "onGetAllDevicesWithPro";
			//Listen
			Responder responder = new Responder();
			//success
			responder.onSuccess.listen((Map response)
			{
		  listDevices=response['all_devices_byarea_info'];
			if(listDevices.length!=null)
			{
			  for(int i=0;i<listDevices.length;i++)
			  {
			    
			    String temp="";
			    if(listDevices[i]['list'] !=null)
			    {
			       List<Map> propertyDevices = listDevices[i]['list'];
			       int count=0;
			       for(int j=0;j<propertyDevices.length;j++)
			       {
			         if(count<3)
			         {
			           count++;
			         temp+=propertyDevices[j]['code'].toString()+",";
			         }
			       }
			    }
			    allDevice.add({"id":listDevices[i]['id'],"address":listDevices[i]['address'],"area_code":listDevices[i]['area_code'],"lat":listDevices[i]['lat'],
			                   "lng":listDevices[i]['lng'],"status":listDevices[i]['status'],"property":temp});
			  }
			  CurrentDevices=allDevice;
			}
			Pagination();
			});
			//error
			responder.onError.listen((Map error)
			{
			Util.showNotifyError(error["message"]);
			}
			);
			//send to server
			AppClient.sendMessage(request, AlarmServiceName.DeviceService, AlarmServiceMethod.POST,responder);
	}
	/*
	 * @author:diennd
	 * @date:20/1/2014
	 * @company:Ex-artisan
	 * @version:1.0
	 */
	void Pagination()
	{		
		ulPagination=this.shadowRoot.querySelector("#pagination");
		inCurrent_page=this.shadowRoot.querySelector("#current_page");
		inShow_per_page=this.shadowRoot.querySelector("#show_per_page");
		//remove all pages
		ulPagination.children.clear();
		pages.clear();
		//number records in a page
		show_per_page=10;
		int number_pages=(CurrentDevices.length/show_per_page).ceil();
		if(number_pages>0)
		{
			LIElement liPagePrevious=new LIElement();
			liPagePrevious.id="PagePrevious";
			liPagePrevious.classes.add("disabled");
			liPagePrevious.onClick.listen(previous);
			AnchorElement aPageStart=new AnchorElement();
			aPageStart.href="#";
			aPageStart.text="<<";
			liPagePrevious.children.add(aPageStart);
			ulPagination.children.add(liPagePrevious);
			//init page
			if(number_pages>=1)
			{
				for(int i=0;i<number_pages;i++)
				{
					LIElement liPage=new LIElement();
					liPage.id = i.toString();
					AnchorElement aPage=new AnchorElement();
					aPage.href="#";
					aPage.text=(i+1).toString();
					liPage.children.add(aPage);
					ulPagination.children.add(liPage);
					liPage.onClick.listen(go_to_page);
					pages.add(liPage);
				}
			}
			LIElement liPageNext=new LIElement();
			liPageNext.id="PageNext";
			liPageNext.onClick.listen(next);
			AnchorElement aPageEnd=new AnchorElement();
			aPageEnd.href="#";
			aPageEnd.text=">>";
			liPageNext.children.add(aPageEnd);
			ulPagination.children.add(liPageNext);
			//set selected page 1
			setSelectedPageIndex(0);
		}
		else
		{
			devices=toObservable([]);
		}
	}
	
	void go_to_page(Event event)
	{
		LIElement target = event.currentTarget;
		setSelectedPageIndex(int.parse(target.id));
	}
	/*
	 * @author: diennd
	 * @since: 21/1/2014
	 * @company:Ex-artisan
	 * @version:1.0
	 */
	void setSelectedPageIndex(int selectedIndex)
	{
		for(int i=0;i<pages.length;i++)
		{
			if(selectedIndex==i)
			{
				pages[i].classes.add("active");
				currentSelectedPageIndex = i;
				devices.clear();
				if(selectedIndex*show_per_page+show_per_page<=CurrentDevices.length)
					devices.addAll(CurrentDevices.sublist(selectedIndex*show_per_page,selectedIndex*show_per_page+show_per_page));
				else
					devices.addAll(CurrentDevices.sublist(selectedIndex*show_per_page,selectedIndex*show_per_page+(selectedIndex*show_per_page-CurrentDevices.length).abs()));
			}
			else
			{
				if(pages[i].classes.contains("active"))
					pages[i].classes.remove("active");
			}
		}
		//check the last page, the first page
		if(selectedIndex==0)
		{
			LIElement liPagePrevious=this.shadowRoot.querySelector("#PagePrevious");
			LIElement liPageNext=this.shadowRoot.querySelector("#PageNext");
			liPageNext.classes.remove("disabled");
			liPagePrevious.classes.add("disabled");
		}
		else if(selectedIndex==(pages.length-1))
			{
				LIElement liPagePrevious=this.shadowRoot.querySelector("#PagePrevious");
				LIElement liPageNext=this.shadowRoot.querySelector("#PageNext");
				liPagePrevious.classes.remove("disabled");
				liPageNext.classes.add("disabled");
			}
			else
			{
				LIElement liPagePrevious=this.shadowRoot.querySelector("#PagePrevious");
				LIElement liPageNext=this.shadowRoot.querySelector("#PageNext");
				if(liPagePrevious.className.contains("disabled"))
					liPagePrevious.classes.remove("disabled");
				if(liPageNext.className.contains("disabled"))
					liPageNext.classes.remove("disabled");
			}
	}
	/*
	 * @author: diennd
	 * @since: 21/1/2014
	 * @company:Ex-artisan
	 * @version:1.0
	 */
	void previous(Event e)
	{
		if(currentSelectedPageIndex>0)
		{
		 setSelectedPageIndex(currentSelectedPageIndex-1);
		}
	}
	/*
	 * @author: diennd
	 * @since: 21/1/2014
	 * @company:Ex-artisan
	 * @version:1.0
	 */
	void next(Event e)
	{
		if(currentSelectedPageIndex<pages.length-1)
		{
		 setSelectedPageIndex(currentSelectedPageIndex+1);
		}
		
	}
	/*
	 * @author: diennd
	 * @since: 21/1/2014
	 * @company:Ex-artisan
	 * @version:1.0
	 */
	List<Map> filterByAreaCode(List<Map> devices,String code)
	{
		return devices.where((devices){	
				if(code==devices["area_code"])
				{
					return true;
				}
			return false;
		}).toList();
	}
	/*
	 * @author: diennd
	 * @since: 21/1/2014
	 * @company:Ex-artisan
	 * @version:1.0
	 */
	void Search(Event e)
	{
		
		slAreas=this.shadowRoot.querySelector("#areas");
		if(slAreas.selectedIndex >0)
		{
			Map area=areas[slAreas.selectedIndex-1];
			CurrentDevices=filterByAreaCode(allDevice, area["code"]);
		}
		if(slAreas.selectedIndex ==0)
		{
		  CurrentDevices=allDevice;
		}
		Pagination();
	}
	
	void onEditDevice(Event event)
	{
		
		ButtonElement btn = event.currentTarget;
		Map device = getDeviceByID(btn.attributes["id"]);
		//validate
		if(device==null)
		{
			Util.showNotifyWarning("Can't find device!");
			return;
		}
		//dispatch event
		dispatchEvent(new CustomEvent("edit",detail:device));
	}
	
	void onDeleteDevice()
	{
		dispatchEvent(new CustomEvent("delete",detail:{"dsad":"fdsfdfd"}));
	}
	
	Map getDeviceByID(String id)
	{
		for(int i = 0;i<listDevices.length;i++)
		{
			Map device = listDevices[i];
			if(device["id"].toString()==id)
			{
				return listDevices[i];
			}
		}
		return null;
	}
}
