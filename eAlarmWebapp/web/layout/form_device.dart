import 'package:polymer/polymer.dart';

@CustomTag('form-device')
class FormDevice extends PolymerElement
{
	// This lets the Bootstrap CSS "bleed through" into the Shadow DOM
	// of this element.
	bool get applyAuthorStyles => false;
	FormDevice.created() : super.created();
	enteredView() 
	{
		super.enteredView();
	}
}