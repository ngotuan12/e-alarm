import 'package:polymer/polymer.dart';
import 'dart:html';
import '../src/util.dart';
@CustomTag('form-area-none')
class FormAreaNone extends PolymerElement
{
  UListElement ulPagination;
  InputElement inCurrent_page;
  InputElement inShow_per_page;
  SpanElement totalAreas;
  ButtonElement btnSearch;
  ButtonElement btnAdd;
  AnchorElement ancAdd;
  Element tblAreas;
  int currentSelectedPageIndex = 0;
  int show_per_page;
  int stt=0;
  @observable List listAreas=toObservable([]);
  @observable List CurrentAreas=toObservable([]);
  @observable List areas=toObservable([]);
  List<LIElement> pages = [];
  // of this element.
  bool get applyAuthorStyles => true;
  FormAreaNone.created() : super.created();
  enteredView() 
  {
    super.enteredView();
    totalAreas=this.shadowRoot.querySelector("#totalAreas");
    btnSearch=this.shadowRoot.querySelector("#search");
    ancAdd=this.shadowRoot.querySelector("#add");
    tblAreas = this.shadowRoot.querySelector("#ListAreas");
    btnAdd=this.shadowRoot.querySelector("#btnAdd");
    //action
    btnAdd.onClick.listen(onAdd);
    //Load
    init();
  }
  /*
   * @author : diennd
   * @since :17/2/2014
   * @version :1.0
   */
  void onAdd(Event e)
  {
    dispatchEvent(new CustomEvent("add", detail:"" ));
  }
  /*
   * @author:diennd
   * @since:17/2/2014
   * @version:1.0
   */
  void init()
    {
      //get data
      Map request = new Map();
      request["Method"] = "GetAllArea";
      //Listen
      Responder responder = new Responder();
      //success
      responder.onSuccess.listen((Map response)
      {
        listAreas=response['ListArea'];
        if(listAreas!=null)
        {
          CurrentAreas=listAreas;
          totalAreas.text="Tổng số : "+ CurrentAreas.length.toString();
          Pagination();
        }
      });
      //error
      responder.onError.listen((Map error)
      {
        Util.showNotifyError(error["message"]);
      }
      );
      //send to server
      AppClient.sendMessage(request, AlarmServiceName.AreaService, AlarmServiceMethod.POST,responder);
    }
  /*
   * @author:diennd
   * @since:17/2/2014
   * @company:ex-artisan
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
      int number_pages=(CurrentAreas.length/show_per_page).ceil();
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
        areas=toObservable([]);
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
        areas.clear();
        if(selectedIndex*show_per_page+show_per_page<=CurrentAreas.length)
          areas.addAll(CurrentAreas.sublist(selectedIndex*show_per_page,selectedIndex*show_per_page+show_per_page));
        else
          areas.addAll(CurrentAreas.sublist(selectedIndex*show_per_page,selectedIndex*show_per_page+(selectedIndex*show_per_page-CurrentAreas.length).abs()));
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
    tblAreas.children.clear();
    if(CurrentAreas!=null)
          {
            for(int i=0;i<areas.length;i++)
            {
              Map area=areas[i];
              //row
              TableRowElement row = new TableRowElement();
              row.classes.add("selectable");
              //column
              TableCellElement colId=new TableCellElement();
              colId.classes.add("center");
              colId.appendHtml((++stt).toString());
                                  
              TableCellElement colName=new TableCellElement();
              colName.classes.add("important");
              colName.appendHtml(area['name']);
              
              TableCellElement colAddress=new TableCellElement();
              colAddress.classes.add("important");
              colAddress.appendHtml((area['full_name']!=null)?area['full_name']:"");
                                  
              TableCellElement colCode=new TableCellElement();
              colCode.classes.add("important center");  
              colCode.appendHtml(area["code"].toString());
                                  
              TableCellElement colNameParent=new TableCellElement();
              colNameParent.classes.add("important center");  
              colNameParent.appendHtml(area["parent_name"].toString());
              
              TableCellElement colLevel=new TableCellElement();
              colLevel.classes.add("important center");
              colLevel.appendHtml(area["level"].toString());
                                  
              TableCellElement colType=new TableCellElement();
              colType.classes.add("important center");
              colType.appendHtml(area["type"].toString());
                                  
              TableCellElement colStatus=new TableCellElement();
              colStatus.classes.add("important center");
              if(area["status"]=="1")
              	colStatus.appendHtml("Kích hoạt");
              else
            	colStatus.appendHtml("Không kích hoạt");
                                    
              TableCellElement colAction=new TableCellElement();
              colAction.classes.add("center");
                                    
              ButtonElement btnEdit=new ButtonElement();
              btnEdit.className="btn-action glyphicons pencil btn-success";
              btnEdit.appendHtml("<i></i>");
              btnEdit.onClick.listen((event)=>onEditArea(area));
                                    
              ButtonElement btnDelete=new ButtonElement();
              btnDelete.className="btn-action glyphicons remove_2 btn-danger";
              btnDelete.appendHtml("<i></i>");
              btnDelete.onClick.listen((event)=>onDeleteArea(area["id"].toString()));
                                          
              colAction.children.add(btnEdit);
              colAction.children.add(btnDelete);
                                    
               //add column
               row.children.add(colId);
               row.children.add(colName);
               row.children.add(colCode);
               row.children.add(colNameParent);
               row.children.add(colAddress);
//               row.children.add(colLevel);
//               row.children.add(colType);
               row.children.add(colStatus);
               row.children.add(colAction);
               //add row
               tblAreas.children.add(row);
            }
          }
  } 
  /*
   * @author:diennd
   * @since:17/2/2014
   * @company:ex-artisan
   * @version:1.0
   */
  void onEditArea(Map area)
  {
    //validate
    if(area==null)
    {
      Util.showNotifyWarning("Can't find area!");
      return;
    }
    //dispatch event
    dispatchEvent(new CustomEvent("edit",detail:area));
  }
  /*
   * @author:diennd
   * @since:17/2/2014
   * @company:ex-artisan
   * @version:1.0
   */
  void onDeleteArea(String id)
  {
  bool result= window.confirm("Có chắc muốn xóa bản ghi ?");
  if(result)
  {
    //get data
    Map request = new Map();
    request["Method"] = "DeleteArea";
    request["ID"]=id;
    //Listen
    Responder responder = new Responder();
    //success
    responder.onSuccess.listen((Map response)
    {
      Util.showNotifySuccess("Đã xóa bản ghi");
      init();
    });
    //error
    responder.onError.listen((Map error)
    {
      Util.showNotifyError(error["message"]);
    }
    );
    //send to server
    AppClient.sendMessage(request, AlarmServiceName.AreaService, AlarmServiceMethod.POST,responder);
    
  }
  }
}