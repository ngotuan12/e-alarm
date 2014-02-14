import 'package:polymer/polymer.dart';
import 'dart:html';
@CustomTag('form-device')
class FormDevice extends PolymerElement
{
	@observable String currentAction;
	@observable Map currentDevice;
	static String getCurrentAction="NONE";
	FormDevice.created() : super.created();
	enteredView() 
	{
		currentAction=getCurrentAction;
		super.enteredView();
	}
	void onEdit(CustomEvent event)
	{
		currentDevice = toObservable(event.detail);
		currentAction = "EDIT";
	}
	void onGoback(CustomEvent event)
    {
      //currentDevice = toObservable(event.detail);
      currentAction = "NONE";
    }
}
