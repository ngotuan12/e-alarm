import 'package:polymer/polymer.dart';
import 'dart:html';
import '../src/util.dart';
@CustomTag('form-edit-account-detail')
class FormEditAccountDetail extends PolymerElement
{
  // This lets the Bootstrap CSS "bleed through" into the Shadow DOM
  // of this element.
  bool get applyAuthorStyles => true;
  
  List<Map> listToogle = new List();
  
  FormEditAccountDetail.created() : super.created();
  
  InputElement txtFullName=new InputElement(type:"text");
  InputElement txtusername=new InputElement(type:"text"); 
  InputElement txtdatepicker=new InputElement(type:"text"); 
  
//  InputElement txtinputUsername=new InputElement(type:"text"); 
//  InputElement txtinputPasswordOld=new InputElement(type:"text"); 
//  InputElement txtinputPasswordNew=new InputElement(type:"text"); 
//  InputElement txtinputPasswordNew2=new InputElement(type:"text"); 
  
  ButtonElement btnSaveChangeDetail=new ButtonElement();
//  ButtonElement btnSaveChangePass=new ButtonElement();
  
  SelectElement selectGender=new SelectElement();
  
  
//  LIElement liAccountDetails = new LIElement();
//  DivElement divAccountDetails = new DivElement();
//  LIElement liAccountSettings = new LIElement();
//  DivElement divAccountSettings = new DivElement();
  //DivElement widgethead = new DivElement();
  
  enteredView() 
  {
    super.enteredView();    
    init();
  }
  void init()
  {
  	//widgethead = this.shadowRoot.querySelector("#account-nav");
//        liAccountDetails = widgethead.querySelector("#account-details");
//        liAccountDetails.onClick.listen(onAccountnav);
//    divAccountDetails = this.shadowRoot.querySelector("#account-body-details");
//                    
//     liAccountSettings = widgethead.querySelector("#account-settings");
//     liAccountSettings.onClick.listen(onAccountnav);
//     divAccountSettings = this.shadowRoot.querySelector("#account-body-settings");
                    
//      listToogle.add({"header":liAccountDetails,"list":divAccountDetails});
//      listToogle.add({"header":liAccountSettings,"list":divAccountSettings});
        
        txtFullName = this.shadowRoot.querySelector("#txtfullname");
        txtusername = this.shadowRoot.querySelector("#txtusername");
        txtdatepicker = this.shadowRoot.querySelector("#datepicker");
        selectGender=this.shadowRoot.querySelector("#selectGender");
        txtFullName.value = SessionUser.sessionUserInfor["fullname"];
        txtusername.value = SessionUser.sessionUserInfor["username"];
        txtdatepicker.value = SessionUser.sessionUserInfor["create_date"];
        if(SessionUser.sessionUserInfor["sex"].toString()=="0")
        {
          selectGender.selectedIndex=1;
        }
        else{
          selectGender.selectedIndex=0;
        }
        
        btnSaveChangeDetail = this.shadowRoot.querySelector("#btnSaveChangeUDetail");
        btnSaveChangeDetail.onClick.listen((event)=>onSaveData());
        
        
//        txtinputUsername=this.shadowRoot.querySelector("#inputUsername");
//        txtinputPasswordOld=this.shadowRoot.querySelector("#inputPasswordOld");
//        txtinputPasswordNew=this.shadowRoot.querySelector("#inputPasswordNew");
//        txtinputPasswordNew2=this.shadowRoot.querySelector("#inputPasswordNew2");
//        txtinputUsername.value = SessionUser.sessionUserInfor["username"];
//        btnSaveChangePass=this.shadowRoot.querySelector("#btnSaveChangePass");
//        btnSaveChangePass.onClick.listen((event)=>onSavePass());
        
        
  }
  
  void onAccountnav(MouseEvent event)
  {
    for(int i=0;i<listToogle.length;i++)
    {
      Map selectedToogle = listToogle[i];
      if(event.currentTarget!=selectedToogle["header"])
      {
        selectedToogle["header"].classes.remove("active");
        selectedToogle["list"].classes.remove("active");
      }
      else
      {
          selectedToogle["header"].classes.add("active");
          selectedToogle["list"].classes.add("active");
      }
    }
  }
  
  void onSaveData()
    {
      try
        {
            Responder responder = new Responder();
            Map request = new Map();
           String a = txtFullName.value;
          request["Method"] = "UpdateDetail";
        request["fullname"]=txtFullName.value;
        if(selectGender.selectedIndex==0)
          request["gender"]="1";
        else
          request["gender"]="0";
        request["id"] = SessionUser.sessionUserInfor["id"].toString();
        if(request["fullname"]=="" ||request["gender"]=="")
        {
          Util.showNotifyError("Dữ liệu không đúng");
        }
        else
        {
              responder.onSuccess.listen((Map response){
              	Util.showNotifySuccess("Thay đổi chi tiết tài khoản thành công");
              	dispatchEvent(new CustomEvent("goback",detail: ""));
              });
              //error
              responder.onError.listen((Map error)
          {
                Util.showNotifyError(error["message"]);
                
                
          });
              AppClient.sendMessage(request, AlarmServiceName.UserService, AlarmServiceMethod.POST,responder);
        }
        }
        catch(err)
        {
            Util.showNotifyError(err.toString());
        }
    }
//  void clearData1()
//  {
//  	txtinputPasswordNew.value = "";
//  	txtinputPasswordOld.value = "";
//  	txtinputPasswordNew2.value = "";
//  	
//  }
//  void onSavePass()
//     {
//       try
//         {   
//                 Responder responder = new Responder();
//             Map request = new Map();
//             String passnew = txtinputPasswordNew.value;
//             String passold = txtinputPasswordOld.value;
//           request["Method"] = "UpdatePass";
//         
//         String md5hashnew = CryptoUtils.bytesToHex((new MD5()..add(passnew.codeUnits)).close());
//         request["newpassword"]= md5hashnew;
//         String md5hashold = CryptoUtils.bytesToHex((new MD5()..add(passold.codeUnits)).close());
//         request["id"] = SessionUser.sessionUserInfor["id"].toString();
//         if(txtinputPasswordOld.value==""||txtinputPasswordNew.value=="")
//         {
//           Util.showNotifyError("Chưa nhập đủ dữ liệu");
//         }else if(md5hashold!=SessionUser.sessionPassWord.toString()){
//        	 String a = SessionUser.sessionUserInfor.toString();
//        	 Util.showNotifyError("Mật khẩu cũ không đúng");
//         }
//         else if(txtinputPasswordNew.value!=txtinputPasswordNew2.value){
//                 	 Util.showNotifyError("Mật khẩu mới nhập không khớp");
//                  }
//         else
//         {
//               responder.onSuccess.listen((Map response){
//              	 Util.showNotifySuccess("Thay đổi mật khẩu thành công");
//              	 SessionUser.sessionPassWord = md5hashnew;
//                 clearData1();
//               });
//               
//               //error
//               responder.onError.listen((Map error)
//           {
//                 Util.showNotifyError(error["message"]);
//           });
//               AppClient.sendMessage(request, AlarmServiceName.UserService, AlarmServiceMethod.POST,responder);
//         }
//         }
//         catch(err)
//         {
//             Util.showNotifyError(err.toString());
//         }
//     }
}