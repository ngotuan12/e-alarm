import 'package:polymer/polymer.dart';
import 'dart:html';
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
			//action
			btnAdd.onClick.listen(onAddDevice);
			DivElement divChart = this.shadowRoot.querySelector("#device-properties-chart");
			AlarmServiceChart chart = new AlarmServiceChart(divChart, {'title': 'Biểu đồ'}, {"id":1,"code":"DSPM001","area_id":13,"area_code":"HN","address":"177 Bùi Thị Xuân, P. Bùi Thị Xuân, Quận Hai Bà Trưng, Hà Nội","lat":21.012076,"lng":105.849817,"status":"1","list":[{"id":1,"device_id":1,"device_pro_id":1,"value":0,"code":"Vac","name":"Điện áp xoay chiều","description":"là đi ện áp xoay chiều của lưới điện ( dải đo từ 0 đến 400)","type":"1","min":0,"max":400},{"id":2,"device_id":1,"device_pro_id":2,"value":0,"code":"Vp","name":"Điện áp dò ra vỏ máy","description":"Là điện áp dò ra vỏ máy ( dải đo từ 0 đến 400)","type":"1","min":0,"max":400},{"id":3,"device_id":1,"device_pro_id":3,"value":0,"code":"Vdc1","name":"Điện áp ăc quy tại điểm 1","description":"Là điện áp ăc quy tại điểm 1( dải đo từ 0 đến 100.0)","type":"1","min":0,"max":100},{"id":4,"device_id":1,"device_pro_id":4,"value":0,"code":"Vdc2","name":"Điện áp ắc quy tại điểm 2","description":"Là điện áp ắc quy tại điểm 2(dải đo từ 0 đến 100.0)","type":"1","min":0,"max":100},{"id":82,"device_id":1,"device_pro_id":10,"value":0,"code":"S1","name":"Công tắc 1","description":"Là trạng thái của công tắc tại điểm 1","type":"2","min":0,"max":1},{"id":83,"device_id":1,"device_pro_id":11,"value":0,"code":"S2","name":"Công tắc 2","description":"Là trạng thái của công tắc tại điểm 2","type":"2","min":0,"max":1},{"id":84,"device_id":1,"device_pro_id":12,"value":0,"code":"S3","name":"Công tắc 3","description":"Là trạng thái của công tắc tại điểm 3","type":"2","min":0,"max":1},{"id":94,"device_id":1,"device_pro_id":13,"value":0,"code":"S4","name":"Công tắc 4","description":"Là trạng thái của công tắc tại điểm 4","type":"2","min":0,"max":1},{"id":99,"device_id":1,"device_pro_id":7,"value":0,"code":"Idc","name":"Dòng điện DC của ắc quy","description":"Là dòng điện DC của ắc quy ( dải đo từ 0 đến 50.0)","type":"1","min":0,"max":50},{"id":100,"device_id":1,"device_pro_id":8,"value":2,"code":"Temp1","name":"Nhiệt độ tại điểm T1","description":"Là nhiệt độ đo tại điểm T1","type":"1","min":0,"max":300},{"id":101,"device_id":1,"device_pro_id":9,"value":0,"code":"Temp2","name":"Nhiệt độ tại điểm T2","description":"Là nhiệt độ đo tại điểm T2","type":"1","min":0,"max":300},{"id":102,"device_id":1,"device_pro_id":14,"value":0,"code":"X_Axis","name":"Góc nghiêng trục X","description":"Giá trị  đo góc nghiêng theo trục X ( dải đo -90 đến 90)","type":"1","min":-90,"max":90},{"id":103,"device_id":1,"device_pro_id":15,"value":0,"code":"Y_Axis","name":"Góc nghiêng trục Y","description":"Giá trị  đo góc nghiêng theo trục Y ( dải đo -90 đến 90)","type":"1","min":-90,"max":90},{"id":104,"device_id":1,"device_pro_id":16,"value":0,"code":"AccX","name":"Độ rung lắc theo trục X ","description":"Là giá trị  đo GYROSCOPE  (đo độ rung lắc)  theo trục X ( dải đo 0 đến 16 )","type":"1","min":0,"max":16},{"id":105,"device_id":1,"device_pro_id":17,"value":0,"code":"AccY","name":"Độ rung lắc theo trục Y","description":"Là giá trị  đo GYROSCOPE  (đo độ rung lắc)  theo trục Y ( dải đo 0 đến 16 )","type":"1","min":0,"max":16},{"id":106,"device_id":1,"device_pro_id":19,"value":0,"code":"Smoke","name":"Giá trị  ADC đo khói và khí ga","description":"Là giá trị  ADC đo khói và khí ga ( giải đo từ 0 đên 4096)","type":"1","min":0,"max":4096},{"id":210,"device_id":1,"device_pro_id":20,"value":78,"code":"Humi","name":"Độ ẩm của khoang máy","description":"Độ ẩm của khoang máy","type":"1","min":0,"max":100}]});
			init();
		}
		catch(err)
		{
			Util.showNotifyError(err.toString());
		}
	}
	/*
	 * author: diennd
	 */
	void onAddDevice(Event e)
	{
	  dispatchEvent(new CustomEvent("add",detail:""));
	}
	/*
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
              btnDelete.onClick.listen((event)=>onDeleteDevice(Device["id"].toString()));
                                          
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
               //add row
               tblDevices.children.add(row);
            }
          }
	}
	/*
	 * @author :diennd
	 * @since :12/2/2014
	 * @version:1.0
	 */
	void onDeleteDevice(String idDevice)
	{
	  
	}
	/*
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
	/*
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
	  request["Method"] = "onGetAllDevicesWithPro";
	  //Listen
	  Responder responder = new Responder();
		//success
		responder.onSuccess.listen((Map response)
		{
		  listDevices=response['all_devices_byarea_info'];
		  CurrentDevices=listDevices;
		  totalDevice.text="Tổng số trạm: "+ CurrentDevices.length.toString();
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
	/*
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

/*
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