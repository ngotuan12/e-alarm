import 'package:polymer/polymer.dart';
import 'dart:html';
import '../src/util.dart';
import 'package:crypto/crypto.dart';
@CustomTag('form-pass-change')
class FormPassChange extends PolymerElement
{
  // This lets the Bootstrap CSS "bleed through" into the Shadow DOM
  // of this element.
  bool get applyAuthorStyles => true;
  
  List<Map> listToogle = new List();
  
  FormPassChange.created() : super.created();
  
  InputElement txtinputUsername=new InputElement(type:"text"); 
  InputElement txtinputPasswordOld=new InputElement(type:"text"); 
  InputElement txtinputPasswordNew=new InputElement(type:"text"); 
  InputElement txtinputPasswordNew2=new InputElement(type:"text"); 
  
  ButtonElement btnSaveChangePass=new ButtonElement();
    
  enteredView() 
  {
    super.enteredView();    
    init();
  }
  void init()
  {
        txtinputUsername=this.shadowRoot.querySelector("#inputUsername");
        txtinputPasswordOld=this.shadowRoot.querySelector("#inputPasswordOld");
        txtinputPasswordNew=this.shadowRoot.querySelector("#inputPasswordNew");
        txtinputPasswordNew2=this.shadowRoot.querySelector("#inputPasswordNew2");
        txtinputUsername.value = SessionUser.sessionUserInfor["username"];
        btnSaveChangePass=this.shadowRoot.querySelector("#btnSaveChangePass");
        btnSaveChangePass.onClick.listen((event)=>onSavePass());
        
        
  }
  
  void clearData1()
  {
  	txtinputPasswordNew.value = "";
  	txtinputPasswordOld.value = "";
  	txtinputPasswordNew2.value = "";
  	
  }
  void onSavePass()
     {
       try
         {   
                 Responder responder = new Responder();
             Map request = new Map();
             String passnew = txtinputPasswordNew.value;
             String passold = txtinputPasswordOld.value;
           request["Method"] = "UpdatePass";
         
         String md5hashnew = CryptoUtils.bytesToHex((new MD5()..add(passnew.codeUnits)).close());
         request["newpassword"]= md5hashnew;
         String md5hashold = CryptoUtils.bytesToHex((new MD5()..add(passold.codeUnits)).close());
         request["id"] = SessionUser.sessionUserInfor["id"].toString();
         if(txtinputPasswordOld.value==""||txtinputPasswordNew.value=="")
         {
           Util.showNotifyError("Chưa nhập đủ dữ liệu");
         }else if(md5hashold!=SessionUser.sessionPassWord.toString()){
        	 String a = SessionUser.sessionUserInfor.toString();
        	 Util.showNotifyError("Mật khẩu cũ không đúng");
         }
         else if(txtinputPasswordNew.value!=txtinputPasswordNew2.value){
                 	 Util.showNotifyError("Mật khẩu mới nhập không khớp");
                  }
         else
         {
               responder.onSuccess.listen((Map response){
              	 Util.showNotifySuccess("Thay đổi mật khẩu thành công");
              	 SessionUser.sessionPassWord = md5hashnew;
                 clearData1();
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
}