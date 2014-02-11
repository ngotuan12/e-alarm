import 'package:polymer/polymer.dart';
import 'dart:html';
import 'dart:convert';
@CustomTag('form-device-edit')
class FormDeviceEdit extends PolymerElement
{
	bool get applyAuthorStyles => true;
	@published Map device;
	FormDeviceEdit.created() : super.created();
	enteredView() 
	{
		super.enteredView();
	}
}
