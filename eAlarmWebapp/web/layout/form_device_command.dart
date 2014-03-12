import 'package:polymer/polymer.dart';
import 'dart:html';
import 'dart:convert';
import '../src/util.dart';
@CustomTag('form-device-command')
class FormGatewaycommand extends PolymerElement
{
	FormGatewaycommand.created() : super.created();
	UListElement ulPagination;
	InputElement inCurrent_page;
	InputElement inShow_per_page;
	OListElement olListDevices;
	//TextAreaElement txtRequest;
	TextAreaElement txtRespone;
	ButtonElement btnTestAlive;
	ButtonElement btnCalib;
	ButtonElement btnAlarmOff;
	ButtonElement btnSMS;
	ButtonElement btnSendRequest;
	SpanElement txtID;
	SpanElement totalDevices;
	bool get applyAuthorStyles => true;
	int GatewayID=0;
	Element tblDevice;
	int currentSelectedPageIndex = 0;
	int show_per_page;
	int stt=0;
	@observable List listDevices=toObservable([]);
	@observable List CurrentDevices=toObservable([]);
	@observable List devices=toObservable([]);
	List<LIElement> pages = [];
	enteredView() 
	{
		super.enteredView();
		try
		{
			olListDevices = this.shadowRoot.querySelector("#listDevice");
			totalDevices=this.shadowRoot.querySelector("#totalAreas");
			//txtRequest=this.shadowRoot.querySelector("#txtRequest");
			txtRespone=this.shadowRoot.querySelector("#txtRespone");
			btnSendRequest=this.shadowRoot.querySelector("#btnSendRequest");
			btnAlarmOff=this.shadowRoot.querySelector("#btnAlarmOff");;
			btnCalib=this.shadowRoot.querySelector("#btnCalib");;
			btnSMS=this.shadowRoot.querySelector("#btnSMS");;
			btnTestAlive=this.shadowRoot.querySelector("#btnTestAlive");;
			txtID=this.shadowRoot.querySelector("#txtID");
			//event
			btnAlarmOff.onClick.listen(onAlarmOff);
			btnCalib.onClick.listen(onCalib);
			btnSMS.onClick.listen(onSMS);
			btnTestAlive.onClick.listen(onAlive);
//			btnSendRequest.onClick.listen(
//			(event) => SendCommand());
			tblDevice = this.shadowRoot.querySelector("#ListDevices");
			Responder responder = new Responder();
			Map request = new Map();
			request["Method"] = "onGetAllDevices";
			responder.onSuccess.listen((Map response)
			{
				listDevices=response['all_devices_info'];
				CurrentDevices=listDevices;
				Pagination();
				totalDevices.text="Tổng số : "+ CurrentDevices.length.toString();
			});
			//error
			responder.onError.listen((Map error)
			{
			 Util.showNotifyError(error["message"]);
			});
			AppClient.sendMessage(request, AlarmServiceName.DeviceService, AlarmServiceMethod.POST,responder);
			initWebsocket();
		
		}
		catch(err)
		{
			Util.showNotifyError(err.toString());
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
		if(Conn_Ser!="null")
		{
			//txtRequest.disabled=false;
			txtRespone.disabled=false;
			//btnSendRequest.disabled=false;
			//txtRequest.value="";
			txtRespone.value="";
			GatewayID=obj;
			txtID.text="Gateway Mac Address is " + Mac_Add;
		}
		else
		{
			//btnSendRequest.disabled=true;
			//txtRequest.disabled=true;
			txtRespone.disabled=true;
			txtID.text="";
		}
	}
	void SendCommand()
	{
		//txtRequest.text=GatewayID.toString();
		Responder responder = new Responder();
		Map request = new Map();
		request["Method"] = "AddGateway";
		request["Gateway_ID"]=GatewayID;
	//	request["Request"]=txtRequest.value;
		responder.onSuccess.listen((Map response)
		{
			txtRespone.value=response['Respone'];
		});
		//error
		responder.onError.listen((Map error)
		{
			Util.showNotifyError(error["message"]);
		});
		AppClient.sendMessage(request, AlarmServiceName.GatewayService, AlarmServiceMethod.POST,responder);
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
				row.onClick.listen(onAddClass);
			}
			//add column
			row.children.add(colId);
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
