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
	
	Map<String,Element> currentDeviceDetail = {};
	
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
			//websocket
			initWebsocket();
			//init
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
				Map device=devices[i];
				//row
				TableRowElement row = new TableRowElement();
				row.classes.add("selectable");
				row.attributes["data"] = JSON.encode(devices[i]);
				String type="";
				String status ="<img class=\"l_h_content_img\" style=\"margin:-3px 0px 0px 0px\" src=\"style/icons/marker_gray.png\" alt=\"blue\">";
				if(device["status"]=="1")
				{
					status = "<img class=\"l_h_content_img\" style=\"margin:-3px 0px 0px 0px\" src=\"style/icons/ic_green.png\" alt=\"blue\">";
					type = "important";
				}
				else if(device["status"]=="2")
				{
					status = "<img class=\"l_h_content_img\" style=\"margin:-3px 0px 0px 0px\" src=\"style/icons/ic_red.png\" alt=\"blue\">";
					type = "has-error";
				}
				type = "important";
				//STT
				addCell(row,(i+1).toString(),align: "center",type: type);
				//Address
				addCell(row, device["address"],align: "left",type: type);
				//code
				addCell(row,device["code"],align: "center",type: type);
				//name
				addCell(row,device["name"],align: "center",type: type);
				//mac_add
				addCell(row,device["mac_add"],align: "center",type: type);
				//connected server
				addCell(row,Util.nvl(device["connected_server"],"Not connect"),align: "center",type: type);
				//status
				addCell(row,status,align: "center",type: type);
				//action
				TableCellElement colAction=new TableCellElement();
				colAction.classes.add("center");
				
				ButtonElement btnEdit=new ButtonElement();
				btnEdit.className="btn-action glyphicons pencil btn-success";
				btnEdit.appendHtml("<i></i>");
				btnEdit.onClick.listen((event)=>onEditDevice(device));
				
				ButtonElement btnDelete=new ButtonElement();
				btnDelete.className="btn-action glyphicons remove_2 btn-danger";
				btnDelete.appendHtml("<i></i>");
				btnDelete.onClick.listen((event)=>onDeleteDevice(device));
				
				colAction.children.add(btnEdit);
				colAction.children.add(btnDelete);
				
				//add column
				row.children.add(colAction);
				//listener
				row.onClick.listen(onSelectedDevice);
				//add row
				tblDevices.children.add(row);
			}
		}
	}
	/**
	 * @author TuanNA
	 * @since: 03/02/2014
	 * @version: 1.0
	 */
	void addCell(TableRowElement row,String content,{bool isAction:false,String type:null,String align:null})
	{
		TableCellElement colId=new TableCellElement();
		if(align!=null)
			colId.classes.add(align);
		if(type!=null)
			colId.classes.add(type);
		colId.appendHtml(content);
		//add column
		row.children.add(colId);
	}
	/**
	 * 
	 */
	void onSelectedDevice(Event event)
	{
		if(event.target is TableCellElement)
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
			//clear detail
			currentDeviceDetail.clear();
			//properties
			initDeviceProperties(device,properties);
			//command log
//			initDeviceCommandLog();
			//configuration
//			initConfiguration();
			//default selected
			setSelectedTabIndex(0);
			//connect device id
			Map websocketRequest = new Map();
			websocketRequest["handle"] = "connect_device";
			websocketRequest["device_id"] = iDeviceID;
			AppClient.sendWebsocketMessage(websocketRequest);
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
		//name, status, address
		SpanElement span = new SpanElement();
		span.style.fontSize = "18px";
		span.id = "spanDeviceInfor";
		String strImgStatus;
		//img status
		if(device["status"]=="0")
		{
			strImgStatus = "<img class=\"l_h_content_img\" style=\"margin:-3px 0px 0px 10px\" src=\"style/icons/marker_gray.png\" alt=\"blue\">";
		}
		else if(device["status"]=="1")
		{
			strImgStatus = "<img class=\"l_h_content_img\" style=\"margin:-3px 0px 0px 10px\" src=\"style/icons/ic_green.png\" alt=\"blue\">";
		}
		else if(device["status"]=="2")
		{
			strImgStatus = "<img class=\"l_h_content_img\" style=\"margin:-3px 0px 0px 10px\" src=\"style/icons/ic_red.png\" alt=\"blue\">";
		}
		//append
		span.appendHtml(device["code"]+" - "+device["name"]+strImgStatus
				+"<br><p style=\"font-size: 12px;\">"+device["address"]
				+"<br> Connected server: <a>"+ Util.nvl(device["connected_server"],"not connect")+"</a>"
				+"<br>MAC: <a>"+device["mac_add"] +"</a>"
				+"<br>Chi tiết: "+Util.nvl(device["description"],"")
				+" </p>");
		//add children
		content.children.add(span);
		//div list properties
		DivElement divListProperties = new DivElement();
		divListProperties.className = "properties";
		//ul
		UListElement ul = new UListElement();
		ul.className = "ulListProperties";
		ul.id = "ulDeviceProperties";
		divListProperties.children.add(ul);
		//init
		for(int i=0;i<properties.length;i++)
		{
			Map property = properties[i];
			LIElement li = new LIElement();
			li.className = "liListProperties";
			li.id = property["code"];
			li.attributes["value"] = JSON.encode(property); 
			SpanElement span = new SpanElement();
			if(device["status"]=="0")
			{
//				span.className = "normal";
  				span.appendHtml(property["name"]+"<br><strong>  "+"--"+" "+property["symbol"]+"</strong>");  
			}
			else if(property["alarm_status"]=="1")
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
	 *@author TuanNA
	 * @since 03/03/2014
	 * @version 1.0 
	 */
	void onUpdateProperties(Map properties)
	{
		//update device infor
		SpanElement span = this.shadowRoot.querySelector("#spanDeviceInfor");
		//update list sensor
		UListElement ul = this.shadowRoot.querySelector("#ulDeviceProperties");
		for(int i=0;i<properties.keys.length;i++)
		{
			LIElement li = ul.querySelector("#"+properties.keys.elementAt(i));
			if(li!=null)
			{
				li.children.clear();
				Map property = JSON.decode(li.attributes["value"]);
				property["value"] = properties[properties.keys.elementAt(i)];
				String strAlarmStatus = "1";
				if(property["value"]>=property["max_alarm"]||property["value"]<=property["min_alarm"])
				{
					strAlarmStatus = "0";
				}
				property["alarm_status"] = strAlarmStatus;
				
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
    			//
    			li.attributes["value"] = JSON.encode(property);
    			//
    			li.children.add(span);
				//print(properties.keys.elementAt(i)+properties[properties.keys.elementAt(i)].toString());
			}
		}
		
//		ul.children.clear();
		//
	}
	/**
	 *@author TuanNA
	 * @since 03/03/2014
	 * @version 1.0 
	 */
	void initWebsocket()
	{
		
		AppClient.connectWebsocket();
		AppClient.websocket.onMessage.listen((MessageEvent e) {
			try
			{
				print(e.data);
				Map response = JSON.decode(e.data);
				switch(response["handle"])
				{
					case "update_device_properties":
						Map properties = response["infors"];
						onUpdateProperties(properties);
						break;
					default:
						break;
				}
			}
			catch(err)
			{
				print(err);
			}
			
		});
	}
	/**
	 * @author: TuanNA
	 * @since: 04/03/2014
	 * @version: 1.0
	 * @company
	 * @params:
	 */
	void updateDeviceProperties(Map properties)
	{
		
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
			Map request = new Map();
    		request["Method"] = "form_device_none_delete";
    		request["device_id"] = device["id"];
    		//Listen
    		Responder responder = new Responder();
    		//success
    		responder.onSuccess.listen((Map response)
    		{
				for(int i=0;i<listDevices.length;i++)
				{
					if(device["id"]==listDevices[i]["id"])
					{
						listDevices.removeAt(i);
						break;
					}
				}
				CurrentDevices=listDevices;
				totalDevice.text="Tổng số trạm: "+ CurrentDevices.length.toString();
				Pagination();
				//set selected device
				if(listDevices!=null&&listDevices.length>0)
					setSelectedDevice(listDevices[0]);
				//delete
				Util.showNotifySuccess("Xoá bản ghi thành công");
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
}
