import 'package:polymer/polymer.dart';
import 'dart:html';
import '../src/util.dart';
@CustomTag('form-user-none')
class FormUserNone extends PolymerElement
{
  UListElement ulPagination;
  InputElement inCurrent_page;
  InputElement inShow_per_page;
  SpanElement totalUsers;
  ButtonElement btnSearch;
  ButtonElement btnAdd;
  AnchorElement ancAdd;
  Element tblUser;
  List<Map> listSearchUsers;
  InputElement txtNameSearch = new InputElement(type: "text");
  InputElement txtEmailSearch = new InputElement(type: "text");
  SelectElement selStatusSearch = new SelectElement();
  
  int currentSelectedPageIndex = 0;
  int show_per_page;
  int stt=0;
  @observable List listUsers=toObservable([]);
  @observable List CurrentUsers=toObservable([]);
  @observable List users=toObservable([]);
  List<LIElement> pages = [];  
  bool get applyAuthorStyles => true;
  FormUserNone.created() : super.created();
  enteredView() 
  {
    super.enteredView();
    txtNameSearch = this.shadowRoot.querySelector("#txtNameSearch");
    txtNameSearch.onKeyUp.listen((event)=>handlerOnchageSearch());
    
    txtEmailSearch = this.shadowRoot.querySelector("#txtEmailSearch");
    txtEmailSearch.onKeyUp.listen((event)=>handlerOnchageSearch());
    
    selStatusSearch = this.shadowRoot.querySelector("#selStatusSearch");
    selStatusSearch.onChange.listen((event)=>handlerOnchageSearch());
    
    totalUsers=this.shadowRoot.querySelector("#totalUsers");
    btnSearch=this.shadowRoot.querySelector("#search");
    ancAdd=this.shadowRoot.querySelector("#add");
    tblUser = this.shadowRoot.querySelector("#ListUsers");
    btnAdd=this.shadowRoot.querySelector("#btnAdd");
    //event
    btnAdd.onClick.listen(onAdd);
    //Load
    init();
  }
  
  
  void handlerOnchageSearch(){
		String inputName = txtNameSearch.value;
		String inputEmail = txtEmailSearch.value;
		String selStatus = selStatusSearch.value;
		stt = 0;
		listSearchUsers= new List<Map>();
		for(int i=0;i<CurrentUsers.length;i++)
		{
			Map user = CurrentUsers[i];
			String nameofuser = user["FullName"];
			String emailofuser = user["Email"];
			String statusofuser = user["Status"];
			if(nameofuser.toLowerCase().contains(inputName.toLowerCase())&&emailofuser.toLowerCase().contains(inputEmail.toLowerCase())&&statusofuser == selStatus){
				listSearchUsers.add(CurrentUsers[i]);
			}
		}		
		showRecordSearch(listSearchUsers);
	}
 /*
  * @author:diennd
  * @since:19/2/2014
  * @company:ex-artisan
  * @version:1.0
  */
  void init()
  {
    //get data
    Map request = new Map();
    request["Method"] = "onGetAllUser";
    //Listen
    Responder responder = new Responder();
    //success
    responder.onSuccess.listen((Map response)
    {
      listUsers=response['ListUser'];
      CurrentUsers=listUsers;
      totalUsers.text="Tổng số: "+ CurrentUsers.length.toString();
      Pagination();
    });
    //error
    responder.onError.listen((Map error)
    {
      Util.showNotifyError(error["message"]);
    }
    );
    //send to server
    AppClient.sendMessage(request, AlarmServiceName.UserManagementService, AlarmServiceMethod.POST,responder);    
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
      int number_pages=(CurrentUsers.length/show_per_page).ceil();
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
        users=toObservable([]);
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
        users.clear();
        if(selectedIndex*show_per_page+show_per_page<=CurrentUsers.length)
          users.addAll(CurrentUsers.sublist(selectedIndex*show_per_page,selectedIndex*show_per_page+show_per_page));
        else
          users.addAll(CurrentUsers.sublist(selectedIndex*show_per_page,selectedIndex*show_per_page+(selectedIndex*show_per_page-CurrentUsers.length).abs()));
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
    tblUser.children.clear();
    if(CurrentUsers!=null)
          {
            for(int i=0;i<users.length;i++)
            {
              Map user=users[i];
              //row
              TableRowElement row = new TableRowElement();
              row.classes.add("selectable");
              //column
              TableCellElement colId=new TableCellElement();
              colId.classes.add("center important");
              colId.appendHtml((++stt).toString());
                                  
              TableCellElement colFullName=new TableCellElement();
              colFullName.classes.add("important center");
              colFullName.appendHtml((user['FullName']!=null)?user['FullName']:"");
              
              TableCellElement colUserName=new TableCellElement();
              colUserName.classes.add("important center");
              colUserName.appendHtml((user['UserName']!=null)?user['UserName']:"");
                                  
              TableCellElement colSex=new TableCellElement();
              colSex.classes.add("important center");
              if(user['Sex']!=null)
               colSex.appendHtml((user['Sex'].toString()=='1')?"Nam":"Nữ");
                                  
              TableCellElement colEmail=new TableCellElement();
              colEmail.classes.add("important center");
              colEmail.appendHtml((user['Email']!=null)?user['Email']:"");
              
              TableCellElement colPhone=new TableCellElement();
              colPhone.classes.add("important center");
              colPhone.appendHtml((user['Phone']!=null)?user['Phone']:"");
                                  
              TableCellElement colStatus=new TableCellElement();
              colStatus.classes.add("important center");
              colStatus.appendHtml((user['Status']!=null)?user['Status']:"");
                                    
              TableCellElement colAction=new TableCellElement();
              colAction.classes.add("important center");
                                    
              ButtonElement btnEdit=new ButtonElement();
              btnEdit.className="btn-action glyphicons pencil btn-success";
              btnEdit.appendHtml("<i></i>");
              btnEdit.onClick.listen((event)=>onEditUser(user));
                                    
              ButtonElement btnDelete=new ButtonElement();
              btnDelete.className="btn-action glyphicons remove_2 btn-danger";
              btnDelete.appendHtml("<i></i>");
              btnDelete.onClick.listen((event)=>onDeleteUser(user,i));
                                          
              colAction.children.add(btnEdit);
              colAction.children.add(btnDelete);
                                    
               //add column
               row.children.add(colId);
               row.children.add(colFullName);
               row.children.add(colUserName);
               row.children.add(colSex);           
               row.children.add(colEmail);
               row.children.add(colPhone);
               row.children.add(colStatus);    
               row.children.add(colAction);
               //add row
               tblUser.children.add(row);
            }
          }
  } 
  
  void showRecordSearch(List a)
    {
      tblUser.children.clear();
      if(CurrentUsers!=null)
            {
              for(int i=0;i<a.length;i++)
              {
                Map user=a[i];
                //row
                TableRowElement row = new TableRowElement();
                row.classes.add("selectable");
                //column
                TableCellElement colId=new TableCellElement();
                colId.classes.add("important center");
                colId.appendHtml((++stt).toString());
                                    
                TableCellElement colFullName=new TableCellElement();
                colFullName.classes.add("important center");
                colFullName.appendHtml((user['FullName']!=null)?user['FullName']:"");
                
                TableCellElement colUserName=new TableCellElement();
                colUserName.classes.add("important center");
                colUserName.appendHtml((user['UserName']!=null)?user['UserName']:"");
                                    
                TableCellElement colSex=new TableCellElement();
                colSex.classes.add("important center");
                if(user['Sex']!=null)
                 colSex.appendHtml((user['Sex'].toString()=='1')?"Nam":"Nữ");
                                    
                TableCellElement colEmail=new TableCellElement();
                colEmail.classes.add("important center");
                colEmail.appendHtml((user['Email']!=null)?user['Email']:"");
                
                TableCellElement colPhone=new TableCellElement();
                colPhone.classes.add("important center");
                colPhone.appendHtml((user['Phone']!=null)?user['Phone']:"");
                                    
                TableCellElement colStatus=new TableCellElement();
                colStatus.classes.add("important center");
                colStatus.appendHtml((user['Status']!=null)?user['Status']:"");
                                      
                TableCellElement colAction=new TableCellElement();
                colAction.classes.add("important center");
                                      
                ButtonElement btnEdit=new ButtonElement();
                btnEdit.className="btn-action glyphicons pencil btn-success";
                btnEdit.appendHtml("<i></i>");
                btnEdit.onClick.listen((event)=>onEditUser(user));
                                      
                ButtonElement btnDelete=new ButtonElement();
                btnDelete.className="btn-action glyphicons remove_2 btn-danger";
                btnDelete.appendHtml("<i></i>");
                btnDelete.onClick.listen((event)=>onDeleteUser(user,i));
                                            
                colAction.children.add(btnEdit);
                colAction.children.add(btnDelete);
                                      
                 //add column
                 row.children.add(colId);
                 row.children.add(colFullName);
                 row.children.add(colUserName);
                 row.children.add(colSex);           
                 row.children.add(colEmail);
                 row.children.add(colPhone);
                 row.children.add(colStatus);    
                 row.children.add(colAction);
                 //add row
                 tblUser.children.add(row);
              }
            }
    } 
 /*
  * @author:diennd
  * @since:19/2/2014
  * @company:ex-artisan
  * @version:1.0
  */
  void onAdd(Event e)
  {
    dispatchEvent(new CustomEvent("add", detail:''));
  }
 /*
  * @author:diennd
  * @since:19/2/2014
  * @company:ex-artisan
  * @version:1.0
  */
  void onEditUser(Map element)
  {
    dispatchEvent(new CustomEvent('edit', detail: element));
  }
 /*
  * @author:diennd
  * @since:19/2/2014
  * @company:ex-artisan
  * @version:1.0
  */
  void onDeleteUser(Map u,int order)
  {
  	bool confirm = window.confirm("Bạn có muốn xóa người dùng "+u["FullName"]+" không ?");
    if(confirm){
    	deleteUser(u["ID"].toString(),order);
    }else{
      print("no delete"+u["name"]);
    }
  }
  void deleteUser(String id,int order)
  {
  	try
  	{
        Responder responder = new Responder();
        Map request = new Map();
        request["Method"] = "onDeleteUser";
        request["id"]=id.toString();
         
        responder.onSuccess.listen((Map response){
         		Util.showNotifySuccess("Xóa thành công");
         		tblUser.children.removeAt(order);
        });
        //error
        responder.onError.listen((Map error)
        {
          Util.showNotifyError(error["message"]);
    		});
        AppClient.sendMessage(request, AlarmServiceName.UserManagementService, AlarmServiceMethod.POST,responder);
        }
        catch(err)
        {
          Util.showNotifyError(err.toString());
        }
  	}
}