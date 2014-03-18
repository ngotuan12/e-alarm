import 'package:polymer/polymer.dart';
import 'dart:html';
import 'dart:convert';
import 'dart:async';
import 'dart:math';
import '../src/util.dart';
@CustomTag('form-device-property-none')
class FormArea extends PolymerElement
{
	UListElement ulWidgetHead;
	UListElement ulPagination;
	InputElement inCurrent_page;
	InputElement inShow_per_page;
	DivElement divWidgetContent;
	DivElement divChart;
	OListElement olListDevices;
	SpanElement txtID;
	SpanElement totalDevices;
	bool get applyAuthorStyles => true;
	int GatewayID=0;
	Element tblDevice;
	int currentSelectedPageIndex = 0;
	int show_per_page;
	int stt=0;
	Timer timer;
	@observable List listDevices=toObservable([]);
	@observable List CurrentDevices=toObservable([]);
	@observable List devices=toObservable([]);
	 List<Map> DeviceProperties=toObservable([]);
	List<LIElement> pages = [];
	FormArea.created() : super.created();
	enteredView() 
	{
		super.enteredView();
		try
		{
			divChart=this.shadowRoot.querySelector("#chart");
			divWidgetContent=this.shadowRoot.querySelector("#widgetContent");
			ulWidgetHead=this.shadowRoot.querySelector("#widgetHead");
			olListDevices = this.shadowRoot.querySelector("#listDevice");
			totalDevices=this.shadowRoot.querySelector("#totalAreas");
			txtID=this.shadowRoot.querySelector("#txtID");
			tblDevice = this.shadowRoot.querySelector("#ListDevices");
			Responder responder = new Responder();
			Map request = new Map();
			request["Method"] = "form_device_none_load";
			responder.onSuccess.listen((Map response)
			{
				listDevices=response['device_list'];
				CurrentDevices=listDevices;
				Pagination();
				//set selected device
				if(listDevices!=null&&listDevices.length>0)
					setSelectedDevice(listDevices[0]);
			});
			//error
			responder.onError.listen((Map error)
			{
			 Util.showNotifyError(error["message"]);
			});
			AppClient.sendMessage(request, AlarmServiceName.DeviceManagementService, AlarmServiceMethod.POST,responder);
			initWebsocket();
			//timer
			//timer =new Timer.periodic(new Duration(seconds:2), callback);
		}
		catch(err)
		{
			Util.showNotifyError(err.toString());
		}
	}
	/*
	 * 
	 */
	void callback(_)
	{
	//	AlarmServiceChart.updateChart(50);
		window.alert("ok");
	}
	/*
	 * 
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
		responder.onSuccess.listen((Map response)
		{
			DeviceProperties = response["properties"];
			initDeviceProperty();
		});
		responder.onError.listen((Map error){
			Util.showNotifyError(error["message"]);
		});
		//send request
		AppClient.sendMessage(request, AlarmServiceName.DeviceManagementService, AlarmServiceMethod.POST, responder);
	}
	/*
	 * @author:diennd
	 * @since:12/3/2014
	 * @company:ex-artisan
	 * @version:1.0
	 */
	void initDeviceProperty()
	{
		//clear all widget
		ulWidgetHead.children.clear();
		divWidgetContent.children.clear();
		if(DeviceProperties !=null && DeviceProperties.length>0)
		{
			for(int i=0;i<DeviceProperties.length;i++)
			{
				Map DeviceProperty=DeviceProperties[i];
				//widget head
				LIElement liWidgetHead=new LIElement();
				liWidgetHead.appendHtml("<a class=\"glyphicons share_alt\" href=\"#\" data-toggle=\"tab\"><i></i>"+DeviceProperty['name']+"</a>");
				ulWidgetHead.children.add(liWidgetHead);
				//widget content
				DivElement divContent=new DivElement();
				divContent.style.width="100%";
				divContent.style.height="250px";
				divContent.className="tab-pane";
				//chart
				AlarmServiceChart.load().then((_)
				{
				AlarmServiceChart gauge = new AlarmServiceChart(divContent,DeviceProperty);
				});
				window.onResize.listen((__){
					AlarmServiceChart.load().then((_)
				{
					// Create a Guage after the library has been loaded.
					AlarmServiceChart gauge = new AlarmServiceChart(divContent,DeviceProperty);
				});
				});
				divWidgetContent.children.add(divContent);
				//on click
				liWidgetHead.onClick.listen(OnChangeProperty);
				liWidgetHead.onClick.listen((Event)=>liWidgetHead.classes.add("active"));
				liWidgetHead.onClick.listen((Event)=>divContent.classes.add("active"));
			}
			//active widget
			ulWidgetHead.children[0].classes.add("active");
			divWidgetContent.children[0].classes.add("active");
		}
	}
	/*
	 * 
	 */
	void OnChangeProperty(Event event)
	{
		for(int i=0;i<ulWidgetHead.children.length;i++)
		{
			//remove class active in widget head
			LIElement li=ulWidgetHead.children[i];
			if(li.className.contains("active"))
				li.classes.remove("active");
			//remove class active in widget content
			DivElement div=divWidgetContent.children[i];
			if(div.className.contains("active"))
				div.classes.remove("active");
		}
	}
	/*
	 * @author:diennd
	 * @since:12/3/2014
	 * @company:ex-artisan
	 * @version:1.0
	 */
	void onCalib(Event e)
	{
		Map command = {"type":"request","cmd":"read_calib","body":{"S_ID":1}} ;
		Map request = new Map();
		request["handle"] = "send_command";
		request["body"] = command;
		request["device_id"] = 1;
		AppClient.sendWebsocketMessage(request);
	}
	/*
	 * @author:diennd
	 * @since:12/3/2014
	 * @company:ex-artisan
	 * @version:1.0
	 */
	void onAlarmOff(Event e)
	{
		
		window.alert("onAlarmOff");
	}
	/*
	 * @author:diennd
	 * @since:12/3/2014
	 * @company:ex-artisan
	 * @version:1.0
	 */
	void onAlive(Event e)
	{
		window.alert("onAlive");
	}
	/*
	 * @author:diennd
	 * @since:12/3/2014
	 * @company:ex-artisan
	 * @version:1.0
	 */
	void onSMS(Event e)
	{
		window.alert("onSMS");
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
					case "send_command_response":
//						Map properties = response["infors"];
//						onUpdateProperties(properties);
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
	void SendGatewayRequest(Object obj, String Mac_Add,String Conn_Ser)
	{
		
	}
	void SendCommand()
	{
		
	}
	void addCell(TableRowElement row,List <Map> cellDevcies,{bool isAction:true,String type:null,String align:null})
	{
		for(int i=0;i<cellDevcies.length;i++)
		{
			Map cellDevice= cellDevcies[i];
			TableCellElement colId=new TableCellElement();
			colId.classes.add("trtd");
			if(align!=null)
				colId.classes.add(align);
			if(type!=null)
				colId.classes.add(type);
			colId.appendHtml((cellDevice !=null)?cellDevice["name"]+"/"+cellDevice["code"]:"");
			//event row
			if(cellDevice !=null)
			{
				colId.onClick.listen((event)=>SendGatewayRequest(cellDevice["id"],cellDevice["mac_add"].toString(),cellDevice["connected_server"].toString()));
				colId.onClick.listen(onAddClass);
				colId.onClick.listen((Event)=> setSelectedDevice(cellDevice));
				row.onClick.listen(onAddClass);
			}
			//add column
			row.children.add(colId);
			//default row
			row.children[0].classes.add("onclick");
		}
	}
	/*
	 *@author:diennd
	 * @since 7/3/2014
	 * @company:ex-artisan
	 * @version :1.0 
	 */
	void onAddClass(Event event)
	{	
		for(int i=0;i<tblDevice.children.length;i++)
		{
			TableRowElement col=tblDevice.children[i];
			for(int j=0;j<col.children.length;j++)
			{
				TableCellElement cell=col.children[j];
				if(cell.className.contains("onclick"))
					cell.classes.remove("onclick");
			}
		}
		TableCellElement colId=event.target;
		colId.classes.add("onclick");
	}
	/*
	 * @author:diennd
	 * @since:17/2/2014
	 * @company:ex-artisan
	 * @version:1.0
	 */
	void Pagination()
	{
		//clear table
		tblDevice.children.clear();
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
	/*
	 * @author:diennd
	 * @since:17/2/2014
	 * @company:ex-artisan
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
	 * @author:diennd
	 * @since:17/2/2104
	 * @company:ex-artisan
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
	 * @author:diennd
	 * @since:17/2/2014
	 * @company:ex-artisan
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
	/*
	 * @author:diennd
	 * @since:17/2/1014
	 * @version:1.0
	 * @company:ex-artisan
	 */
	void go_to_page(Event event)
	{
		LIElement target = event.currentTarget;
		setSelectedPageIndex(int.parse(target.id));
	}
	/*
	 * @author:diennd
	 * @since:17/2/1014
	 * @version:1.0
	 * @company:ex-artisan
	 */
	void showRecord()
	{
		tblDevice.children.clear();
		if(CurrentDevices!=null)
		{
			for(int i=0;i<devices.length;i+=5)
			{
				Map device=devices[i];
				//row
				TableRowElement row = new TableRowElement();
				//add cell
				List<Map> cellDevices=[];
				cellDevices.add((i<devices.length)?devices[i]:null);
				cellDevices.add((i+1<devices.length)?devices[i+1]:null);
				cellDevices.add((i+2<devices.length)?devices[i+2]:null);
				cellDevices.add((i+3<devices.length)?devices[i+3]:null);
				cellDevices.add((i+4<devices.length)?devices[i+4]:null);
				addCell(row,cellDevices,align: "center",type: "important");
				//add row
				tblDevice.children.add(row);
			}
		}
	} 
}