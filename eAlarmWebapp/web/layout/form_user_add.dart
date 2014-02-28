import 'package:polymer/polymer.dart';
import 'dart:html';
import '../src/util.dart';
import 'package:crypto/crypto.dart';
@CustomTag('form-user-add')
class FormUserAdd extends PolymerElement{
	ButtonElement btnCancel;
    ButtonElement btnSignIn;
    ButtonElement btnSave;

    
    InputElement txtaddress=new InputElement(type:"text");
    InputElement txtEmail=new InputElement(type:"text");
    InputElement txtPhone=new InputElement(type:"text");
    InputElement txtUsername=new InputElement(type:"text");
  	InputElement txtPass=new InputElement(type:"text");
  	InputElement txtPass2=new InputElement(type:"text");
    InputElement txtfullname=new InputElement(type:"text");
    DateInputElement txtbirthday=new DateInputElement();
    SelectElement selstatus=new SelectElement();
    SelectElement selgender=new SelectElement();
    
    
  bool get applyAuthorStyles => true;
  FormUserAdd.created() : super.created();
  enteredView() 
  {
    super.enteredView();
    
    btnSave=this.shadowRoot.querySelector("#btnSave");
        btnCancel=this.shadowRoot.querySelector("#btnCancel");
        btnSignIn=this.shadowRoot.querySelector("#btnSignIn");
        
        
        txtaddress=this.shadowRoot.querySelector("#txtaddress");
        txtUsername=this.shadowRoot.querySelector("#txtUsername");
    		txtPass=this.shadowRoot.querySelector("#txtPass");
    		txtPass2=this.shadowRoot.querySelector("#txtPass2");
        txtfullname=this.shadowRoot.querySelector("#txtfullname");
        txtbirthday=this.shadowRoot.querySelector("#txtbirthday");
        selstatus=this.shadowRoot.querySelector("#selstatus");
        selgender=this.shadowRoot.querySelector("#selgender");
        
        txtPhone=this.shadowRoot.querySelector("#txtPhone");
        txtEmail=this.shadowRoot.querySelector("#txtEmail");
        
        
        btnCancel.onClick.listen(onExit);
        btnSave.onClick.listen(onSave);
  }
  
  void onCheck(Event e)
 {
   bool isok = window.confirm('ok');
   if(isok)
     window.alert("ok");
   else
     window.alert("cancel");
  }

  void onExit(Event e)
  {
    dispatchEvent(new CustomEvent("goback",detail:""));
  }

  void onSave(Event e)
  {
  	if(isvalidate(txtEmail.value, txtPhone.value)){
      	try
        {
        Responder responder = new Responder();
       Map request = new Map();
        request["Method"] = "onAddUser";
         String pass = txtPass.value;
         String md5hashnew = CryptoUtils.bytesToHex((new MD5()..add(pass.codeUnits)).close());
         request["password"] = md5hashnew;
         request["username"] = txtUsername.value;
          request["email"] = txtEmail.value;
          request["phone"]= txtPhone.value;
          request["fullname"] = txtfullname.value;
          request["birth_day"]= txtbirthday.value;
          request["address"]= txtaddress.value;
          if(selstatus.selectedIndex==0){
             request["status"]="0";
           }else{
             request["status"]="1";
           }
           if(selgender.selectedIndex==0){
             request["sex"]="0";
            }else{
             request["sex"]="1";
            }
            responder.onSuccess.listen((Map response){
               Util.showNotifySuccess("Thêm người dùng thành công");
               dispatchEvent(new CustomEvent("goback",detail:""));
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
  
  bool isvalidate(String email, String phone) {
    			
      		String regexphone=r'^\d{0,9}$';
          String regexmail = r'^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$';

          RegExp regExpMail = new RegExp(regexmail);
          RegExp regExpPhone = new RegExp(regexphone);

          if(regExpMail.hasMatch(email)&&regExpPhone.hasMatch(phone)){
          	return true;
          }else if(!regExpMail.hasMatch(email)){
          	Util.showNotifyError("Địa chỉ email không đúng");
           // print("Email:"+regExpMail.hasMatch(email).toString()+"======="+" Phone"+regExpPhone.hasMatch(phone).toString());
          	return false;
          }else if(!regExpPhone.hasMatch(phone)){
          	Util.showNotifyError("Số phone không đúng ");
           // print("Email:"+regExpMail.hasMatch(email).toString()+"======="+" Phone"+regExpPhone.hasMatch(phone).toString());
          	return false;
          }
        else if(txtPass.value==""||txtPass2.value=="")
        {
        	Util.showNotifyError("Thiếu dữ liệu mật khẩu");
        	return false;
        }else if(txtPass.value!=txtPass2.value)
        {
        	Util.showNotifyError("Mật khẩu không khớp");
        	return false;
        }
  		}
}