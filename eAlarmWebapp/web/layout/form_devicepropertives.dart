import 'package:polymer/polymer.dart';
import 'dart:html';
@CustomTag('form-devicepropertives')
class FormDevicePropertives extends PolymerElement
{
  // This lets the Bootstrap CSS "bleed through" into the Shadow DOM
  // of this element.
  bool get applyAuthorStyles => true;
  FormDevicePropertives.created() : super.created();
  
  @observable String GetcurrentAction;
  @observable Map currentDevicePro;
  @observable String currentAction = "NONE";
  
  enteredView() 
  {
    super.enteredView();
  }
  
  void onEdit(CustomEvent event)
  {
    currentDevicePro = toObservable(event.detail);
    currentAction = "EDIT";
  }
  
  void onGoBack(CustomEvent event)
    {
      //currentDevicePro = toObservable(event.detail);
      currentAction = "NONE";
    }
  void onAdd(CustomEvent event)
    {
    currentDevicePro = null;
      currentAction = "EDIT";
    }
}