import 'package:polymer/polymer.dart';
import 'dart:html';
import 'dart:convert';
import '../src/util.dart';
@CustomTag('form-device-none')
class FormDeviceNone extends PolymerElement
{
	UListElement ulPagination;
	InputElement inCurrent_page;
	InputElement inShow_per_page;
	SpanElement totalDevice;
	ButtonElement btnSearch;
	ButtonElement btnAdd;
	AnchorElement ancAdd;
	Element tblDevices;
	int currentSelectedPageIndex = 0;
	int show_per_page;
	int stt=0;
	// This lets the Bootstrap CSS "bleed through" into the Shadow DOM
	// of this element.
	bool get applyAuthorStyles => true;
	@observable List listDevices=toObservable([]);
	@observable List CurrentDevices=toObservable([]);
	@observable List devices=toObservable([]);
	List<LIElement> pages = [];
	FormDeviceNone.created() : super.created();
	List<Map> listElementDevice = [];
	DivElement tabPane;
	List<Map> tabs = [];
	enteredView() 
	{
		try
		{
			super.enteredView();
			totalDevice=this.shadowRoot.querySelector("#totalDevice");
			btnSearch=this.shadowRoot.querySelector("#search");
			ancAdd=this.shadowRoot.querySelector("#add");
			tblDevices = this.shadowRoot.querySelector("#ListDevices");
			btnAdd=this.shadowRoot.querySelector("#btnAdd");
			tabPane=this.shadowRoot.querySelector("#device-detail");
			//action
			btnAdd.onClick.listen(onAddDevice);
			init();
		}
		catch(err)
		{
			Util.showNotifyError(err.toString());
		}
	}
	/**
	 * author: diennd
	 */
	void onAddDevice(Event e)
	{
	  dispatchEvent(new CustomEvent("add",detail:""));
	}
	/**
   * author: diennd
   */
	void showRecord()
	{
	  tblDevices.children.clear();
	  if(CurrentDevices!=null)
          {
            for(int i=0;i<devices.length;i++)
            {
              Map Device=devices[i];
              //row
              TableRowElement row = new TableRowElement();
              row.classes.add("selectable");
              row.attributes["data"] = JSON.encode(devices[i]);
              //column
              TableCellElement colId=new TableCellElement();
              colId.classes.add("center");
              colId.appendHtml((++stt).toString());
                                  
              TableCellElement colAddress=new TableCellElement();
              colAddress.classes.add("important");
              colAddress.appendHtml(Device['address']);
                                  
              TableCellElement colCode=new TableCellElement();
              colCode.classes.add("important center");  
              colCode.appendHtml(Device["code"].toString());
                                  
              TableCellElement colLat=new TableCellElement();
              colLat.classes.add("important center");
              colLat.appendHtml(Device["lat"].toString());
                                  
              TableCellElement colLng=new TableCellElement();
              colLng.classes.add("important center");
              colLng.appendHtml(Device["lng"].toString());
                                  
              TableCellElement colStatus=new TableCellElement();
              colStatus.classes.add("important center");
              colStatus.appendHtml(Device["status"].toString());
                                    
              TableCellElement colAction=new TableCellElement();
              colAction.classes.add("center");
                                    
              ButtonElement btnEdit=new ButtonElement();
              btnEdit.className="btn-action glyphicons pencil btn-success";
              btnEdit.appendHtml("<i></i>");
              btnEdit.onClick.listen((event)=>onEditDevice(Device));
                                    
              ButtonElement btnDelete=new ButtonElement();
              btnDelete.className="btn-action glyphicons remove_2 btn-danger";
              btnDelete.appendHtml("<i></i>");
              btnDelete.onClick.listen((event)=>onDeleteDevice(Device));
                                          
              colAction.children.add(btnEdit);
              colAction.children.add(btnDelete);
                                    
               //add column
               row.children.add(colId);
               row.children.add(colAddress);
               row.children.add(colCode);
               row.children.add(colLat);
               row.children.add(colLng);
               row.children.add(colStatus);
               row.children.add(colAction);
               //listener
               row.onClick.listen(onSelectedDevice);
               //add row
               tblDevices.children.add(row);
            }
          }
	}
	void onSelectedDevice(Event event)
	{
		if(event.target ==btnSearch)
		{
			return;	
		}
		else if(event.currentTarget is TableRowElement)
		{
			Element selected = event.currentTarget;
    		setSelectedDevice(JSON.decode(selected.attributes["data"]));
		}
	}
	/**
	 * @author: TuanNA
	 * @since 28/02/2014
	 * @version 1.0
	 */
	void setSelectedDevice(Map device)
	{
		int iDeviceID = device["id"];
		//request
		Map request = new Map();
		request["Method"] = "get_device_detail";
		request["device_id"] = iDeviceID; 
		
		//responder
		Responder responder = new Responder();
		responder.onSuccess.listen((Map response){
			clearTab();
			List<Map> properties = response["properties"];
			//properties
			initDeviceProperties(device,properties);
			//
			initDeviceCommandLog();
			//
			initConfiguration();
			setSelectedTabIndex(0);
		});
		responder.onError.listen((Map error){
			Util.showNotifyError(error["message"]);
		});
		//send request
		AppClient.sendMessage(request, AlarmServiceName.DeviceManagementService, AlarmServiceMethod.POST, responder);
	}
	/**
	 * @author tuanna
	 * @since 28/02/2014
	 */
	void initDeviceCommandLog()
	{
		DivElement content = new DivElement();
		addTab("Lịch sử tác động", content);
	}
	/**
	 * @author tuanna
	 * @since 28/02/2014
	 */
	void initConfiguration()
	{
		DivElement content = new DivElement();
		addTab("Cấu hình", content);
	}
	/**
	 * @author tuanna
	 */
	void initDeviceProperties(Map device,List<Map> properties)
	{
		DivElement content = new DivElement();
		//name and address
		SpanElement span = new SpanElement();
		span.style.fontSize = "18px";
		span.appendHtml(device["code"]+" - "+device["name"]+"<img class=\"l_h_content_img\" style=\"margin:-3px 0px 0px 10px\" src=\"style/icons/ic_red.png\" alt=\"blue\">"
				+"<br><p style=\"font-size: 12px;\">"+device["address"]
				+"<br> Connected server: "+ Util.nvl(device["connected_server"],"not connect")
				+"<br>MAC: "+device["mac_add"]+" </p>");
		//add children
		content.children.add(span);
		//properties
		//div
		DivElement divListProperties = new DivElement();
		divListProperties.className = "properties";
		//ul
		UListElement ul = new UListElement();
		ul.className = "ulListProperties";
		divListProperties.children.add(ul);
		//init
		for(int i=0;i<properties.length;i++)
		{
			Map property = properties[i];
			LIElement li = new LIElement();
			li.className = "liListProperties";
			SpanElement span = new SpanElement();
			
			if(property["alarm_status"]=="1")
			{
				span.className = "normal";
				span.appendHtml(property["name"]+"<br><strong>  "+property["value"].toString()+" "+property["symbol"]+"</strong>");
			}
			else
			{
				span.className = "error";
				span.appendHtml(property["name"]+"<br><strong>  "+property["value"].toString()+" "+property["symbol"]+"</strong>");
			}
			li.children.add(span);
			ul.children.add(li);
		}
		//clear fix
		DivElement divClear = new DivElement();
		divClear.className ="Clear";
		divListProperties.children.add(divClear);
		//add list
		content.children.add(divListProperties);
		//chart
//		DivElement divChart = new DivElement();
//		divChart.style.height ="300px";
//		divChart.style.width = "100%";
//		divChart.style.margin = "auto";
//		//add chart
//		content.children.add(divChart);
//		AlarmServiceChart.load().then((_)
//		{
//			// Create a Guage after the library has been loaded.
//			AlarmServiceChart gauge = new AlarmServiceChart(divChart,{ 'title': 'Biểu đồ'},device);
//		});
//		window.onResize.listen((__){
//			AlarmServiceChart.load().then((_)
//    		{
//    			// Create a Guage after the library has been loaded.
//    			AlarmServiceChart gauge = new AlarmServiceChart(divChart,{ 'title': 'Biểu đồ'},device);
//    		});
//		});
		//
		addTab("Thông số", content);
	}
	/**
	 * @author TuanNA
	 * @since 28/02/2014
	 * @version 1.0
	 */
	void addTab(String tabName,DivElement content)
	{
		//header
		UListElement listHeader = tabPane.querySelector("#device-detail-header");
		LIElement header = new LIElement();
		header.appendHtml("<a style=\"cursor: pointer;\" data-toggle=\"tab\"class=\"glyphicons star\"><i></i>"+tabName+"</a>");
		//listener
		header.onClick.listen(onTabChange);
		listHeader.children.add(header);
		//content
		DivElement listContent = tabPane.querySelector("#device-detail-content");
		content.classes.add("tab-pane");
		listContent.children.add(content);
		//
		tabs.add({"header":header,"content":content});
	}
	/**
	 * 
	 */
	void clearTab()
	{
		UListElement listHeader = tabPane.querySelector("#device-detail-header");
		listHeader.children.clear();
		DivElement listContent = tabPane.querySelector("#device-detail-content");
		listContent.children.clear();
		tabs.clear();
	}
	/**
	 * @author: TuanNA
	 * @since: 29/02/2014
	 * @version: 1.0
	 */
	void onTabChange(Event event)
	{
		LIElement target = event.currentTarget;
		for(int i =0;i<tabs.length;i++)
		{
			LIElement header = tabs[i]["header"];
			if(target==header)
			{
				setSelectedTabIndex(i);
				break;
			}
		}
	}
	/**
	 * @author: TuanNA
	 * @since: 28/02/2014
	 * @version: 1.0
	 */
	void setSelectedTabIndex(int selectedIndex)
	{
		for(int i =0;i<tabs.length;i++)
		{
			LIElement header = tabs[i]["header"];
			DivElement content = tabs[i]["content"];
			if(i==selectedIndex)
			{
				header.className = "active";
				content.classes.add("active");
			}
			else 
			{
				if(header.classes.contains("active"))
				{
					header.className = "";
				}
				if(content.classes.contains("active"))
				{
					content.classes.remove("active");
				}
			}
		}
	}
	/**
	 * 
	 */
	void initDeviceConfiguration()
	{
		
	}
	/**
	 * @author :diennd
	 * @since :12/2/2014
	 * @version:1.0
	 */
	void onDeleteDevice(Map device)
	{
		bool isComfirm = window.confirm("Bạn thực sự muốn xoá "+device["name"]+"-"+device["code"]+ "?");
		if(isComfirm)
		{
			//delete
			print("delete");
		}
	}
	/**
     * @author :diennd
     * @since :12/2/2014
     * @version:1.0
     */
    void onEditDevice(Map device)
    {
          //validate
          if(device==null)
          {
            Util.showNotifyWarning("Can't find device!");
            return;
          }
          //dispatch event
          dispatchEvent(new CustomEvent("edit",detail:device));
    }
	/**
	 * @author:diennd
	 * @date:20/1/2014
	 * @company:Ex-artisan
	 * @version:1.0
	 */
	void init()
	{
	  //clear all device
	  listDevices=toObservable([]);
		//get data
		Map request = new Map();
	  request["Method"] = "form_device_none_load";
	  //Listen
	  Responder responder = new Responder();
		//success
		responder.onSuccess.listen((Map response)
		{
			listDevices=response['device_list'];
			CurrentDevices=listDevices;
			totalDevice.text="Tổng số trạm: "+ CurrentDevices.length.toString();
			Pagination();
			//set selected device
			if(listDevices!=null&&listDevices.length>0)
				setSelectedDevice(listDevices[0]);
		});
		//error
		responder.onError.listen((Map error)
		{
			Util.showNotifyError(error["message"]);
		}
		);
		//send to server
		AppClient.sendMessage(request, AlarmServiceName.DeviceManagementService, AlarmServiceMethod.POST,responder);
	}
	
	/**
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
		show_per_page=5;
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
	/**
	 * @author: diennd
	 * @since: 21/1/2014
	 * @company:Ex-artisan
	 * @version:1.0
	 */
	void setSelectedPageIndex(int selectedIndex)
	{
	  stt=show_per_page*selectedIndex;
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
		showRecord();
	}
	/**
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
	/**
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
	/**
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
	/**
	 * @author: diennd
	 * @since: 21/1/2014
	 * @company:Ex-artisan
	 * @version:1.0
	 */
//	void Search(Event e)
//	{
//		if(slAreas.selectedIndex >0)
//		{
//			Map area=listAreas[slAreas.selectedIndex-1];
//			CurrentDevices=filterByAreaCode(listDevices, area["code"]);
//		}
//		if(slAreas.selectedIndex ==0)
//		{
//		  CurrentDevices=listDevices;
//		}
//		Pagination();
//	}
	
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

	/**
   * @diennd
   * @since 12/2/2014
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