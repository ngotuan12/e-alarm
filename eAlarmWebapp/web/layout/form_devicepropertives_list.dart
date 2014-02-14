import 'package:polymer/polymer.dart';
import 'dart:html';
import '../src/util.dart';
@CustomTag('form-devicepropertives-list')
class FormDevicePropertives extends PolymerElement
{
  // This lets the Bootstrap CSS "bleed through" into the Shadow DOM
  // of this element.
  bool get applyAuthorStyles => true;
  FormDevicePropertives.created() : super.created();
  //String deviceproID;
  Element tblDevicePro;
  
  UListElement ulPagination;
    InputElement inCurrent_page;
    InputElement inShow_per_page;
    SelectElement slAreas;
    ButtonElement btnSearch;
    AnchorElement ancAdd;
    Element tblDevices;
    int currentSelectedPageIndex = 0;
    int show_per_page;
    int stt=0;
  @observable List CurrentDevices=toObservable([]);
  @observable static List allDevice=toObservable([]);
    @observable static List listDevices=toObservable([]);
    @observable List devices=toObservable([]);
    ButtonElement btnAdd=new ButtonElement();
    List<LIElement> pages = [];
  @observable String currentAction = "NONE";
  enteredView() 
  {
    super.enteredView();
    btnAdd=this.shadowRoot.querySelector("#btnAdd");
    btnAdd.onClick.listen((event)=>onAddGateway());
        tblDevices = this.shadowRoot.querySelector("#ListDevicesPro");
        //action
        //ancAdd.onClick.listen((Event)=>FormDevicePropertives.getCurrentAction="ADD");
       // btnSearch.onClick.listen(Search);
        init();
  }
  
  void onAddGateway()
    {
      dispatchEvent(new CustomEvent("add",detail:"1"));
    }
  void onEditGateway(Map ID)
    {
     dispatchEvent(new CustomEvent("edit",detail:ID));
    }
    void onDeleteGateway(String ID)
    {
     dispatchEvent(new CustomEvent("edit",detail:ID));
    }
  void showRecord()
    {
      tblDevices.children.clear();
      if(CurrentDevices!=null)
            {
              for(int i=0;i<devices.length;i++)
              {
                Map depro=devices[i];
        //row
        TableRowElement row = new TableRowElement();
        row.classes.add("selectable");
        
        //column
        TableCellElement colId=new TableCellElement();
        colId.classes.add("center");
        colId.appendHtml(depro["id"].toString());
        
        TableCellElement colName=new TableCellElement();
        colName.classes.add("center");
        colName.appendHtml(depro["name"].toString());
        
        TableCellElement colCode=new TableCellElement();
        colCode.classes.add("center");
        colCode.appendHtml(depro["code"].toString());
        
        TableCellElement colDescription=new TableCellElement();
        colDescription.classes.add("center");
        colDescription.appendHtml(depro["description"].toString());
        
        TableCellElement colType=new TableCellElement();
        colType.classes.add("center");
        colType.appendHtml(depro["type"].toString());
        
        TableCellElement colMin=new TableCellElement();
        colMin.classes.add("center");
        colMin.appendHtml(depro["min"].toString());
        
        TableCellElement colMax=new TableCellElement();
        colMax.classes.add("center");
        colMax.appendHtml(depro["max"].toString());
        
        TableCellElement colAction=new TableCellElement();
        colAction.classes.add("center");
        ButtonElement btnEdit=new ButtonElement();
        btnEdit.className="btn-action glyphicons pencil btn-success";
        btnEdit.appendHtml("<i></i>");
        btnEdit.onClick.listen((event)=>onEditGateway(depro));
              
        ButtonElement btnDelete=new ButtonElement();
        btnDelete.className="btn-action glyphicons remove_2 btn-danger";
        btnDelete.appendHtml("<i></i>");
        btnDelete.onClick.listen((event)=>onDeleteGateway(depro["id"].toString()));
              
        colAction.children.add(btnEdit);
        colAction.children.add(btnDelete);
        //add column
        row.children.add(colId);
        row.children.add(colName);
        row.children.add(colCode);
        row.children.add(colDescription);
        row.children.add(colType);
        row.children.add(colMin);
        row.children.add(colMax);
        row.children.add(colAction);
        
        String deviceproID= depro["id"].toString();
        //add row
        tblDevices.children.add(row);
              }
            }
    }
    void init()
    {
      //clear all device
      listDevices=toObservable([]);
      //get data
      Map request = new Map();
      request["Method"] = "onGetAllDevicePro";
      //Listen
      Responder responder = new Responder();
      //success
      responder.onSuccess.listen((Map response)
      {
        listDevices=response['all_devices_pro'];
        CurrentDevices=listDevices;
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
  
}