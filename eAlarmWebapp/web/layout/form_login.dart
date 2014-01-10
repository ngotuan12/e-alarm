
import 'package:polymer/polymer.dart';
import '../src/util.dart';
import 'package:crypto/crypto.dart';
import 'dart:html';
@CustomTag('form-login')
class FormLogin extends PolymerElement
{
	InputElement txtUserName;
	InputElement txtPassWord;
	FormLogin.created() : super.created();
	// This lets the Bootstrap CSS "bleed through" into the Shadow DOM
  	// of this element.
	bool get applyAuthorStyles => true;
	enteredView() 
	{
		super.enteredView();
		SessionValue.main.showCircleLoader = false;
		txtUserName = this.shadowRoot.querySelector("#txtUserName");
		txtPassWord = this.shadowRoot.querySelector("#txtPassWord");
		txtUserName.focus();
		txtUserName.onKeyPress.listen((KeyboardEvent event){
		
			if(event.keyCode == KeyCode.ENTER)
			{
				login();
			}
		});
		txtPassWord.onKeyPress.listen((KeyboardEvent event){
		
			if(event.keyCode == KeyCode.ENTER)
			{
				login();
			}
		});
//		SessionValue.main.isLogin = true;
	}
	/*
	 * @author TuanNA
	 * @since:07/01/2014
	 * @company: ex-artisan
	 * @version :1.0
	 * @description:
	 */
	login()
	{
		if(!validateForm())
		{
			return;
		}
//		print("username: "+txtUserName.value);
//		print("password: "+txtPassWord.value);
		String text = txtPassWord.value;
		String strUserName = txtUserName.value;
		String md5hash1 = CryptoUtils.bytesToHex((new MD5()..add(text.codeUnits)).close());
		//REQUEST
		Map request = new Map();
		request["Method"] = "login";
		request["UserName"] = strUserName;
		request["PassWord"] = md5hash1;
		//responder
		Responder responder = new Responder();
		//success
		responder.onSuccess.listen((Map response)
		{
			Util.showNotifySuccess("Login successful.");
			SessionValue.main.isLogin = true;
			SessionUser.sessionUserName = strUserName;
			SessionUser.sessionKey = response["sessionKey"];
			SessionUser.sessionUserInfor = response["userInfor"];
		});
		//error
		responder.onError.listen((Map response)
		{
			Util.showNotifyError(response["message"]);
		});
		//send to server
		AppClient.sendMessage(request, AlarmServiceName.PermissionService, AlarmServiceMethod.POST,responder);
	}
	
	bool validateForm()
	{
		return true;
	}
	
}