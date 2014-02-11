import 'package:polymer/polymer.dart';
import 'dart:html';
@CustomTag('form-gateway')
class FormGateway extends PolymerElement
{
  // This lets the Bootstrap CSS "bleed through" into the Shadow DOM
  // of this element.
  bool get applyAuthorStyles => true;
  FormGateway.created() : super.created();
	
	@observable String GetcurrentAction;
	@observable Map currentGateway;
	@observable String currentAction = "NONE";
	
  enteredView() 
  {
    super.enteredView();
  }
	
	void onEdit(CustomEvent event)
	{
		currentGateway = toObservable(event.detail);
		currentAction = "EDIT";
	}
	
	void onAdd(CustomEvent event)
	{
		currentGateway = null;
		currentAction = "EDIT";
	}
	
	void onGoBack(CustomEvent event)
	{
		currentAction = "NONE";
	}
}