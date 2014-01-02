import 'package:polymer/polymer.dart';
import 'dart:convert';
import '../src/util.dart';
@CustomTag('form-area')
class FormArea extends PolymerElement
{
	FormArea.created() : super.created();
	
	bool get applyAuthorStyles => true;
	
	enteredView() 
	{
  		super.enteredView();
		Responder responder = new Responder();
		Map request = new Map();
		request["Method"] = "GetAllAreaActive";
		responder.onSuccess.listen((Map response)
		{
			
		});
		//error
		responder.onError.listen((String strError)
		{
			print(strError);
		});
		//
		AppClient.sendMessage(request, AlarmServiceName.AreaService, AlarmServiceMethod.POST,responder);
	}
}