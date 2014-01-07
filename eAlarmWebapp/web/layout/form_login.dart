
import 'package:polymer/polymer.dart';
import '../src/util.dart';

@CustomTag('form-login')
class FormLogin extends PolymerElement
{
	FormLogin.created() : super.created();
	// This lets the Bootstrap CSS "bleed through" into the Shadow DOM
  	// of this element.
	bool get applyAuthorStyles => true;
	enteredView() 
	{
		super.enteredView();
		SessionValue.main.showCircleLoader = false;
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
		Util.showNotifySuccess("Login successful.");
		SessionValue.main.isLogin = true;
	}
	
}