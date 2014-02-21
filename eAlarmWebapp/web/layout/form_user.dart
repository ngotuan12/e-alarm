import 'package:polymer/polymer.dart';
import 'dart:html';
@CustomTag('form-user')
class FormUser extends PolymerElement
{
  @observable String currentAction;
  @observable Map currentUser;
  bool get applyAuthorStyles => true;
  FormUser.created() : super.created();
  enteredView() 
  {
    super.enteredView();
    currentAction="NONE";
  }
  void onAdd(CustomEvent event)
  {
    currentAction="ADD";
  }
  void onEdit(CustomEvent event)
  {
    currentAction="EDIT";
    currentUser=event.detail;
  }
  void onGoBack(CustomEvent event)
  {
    currentAction="NONE";
  }
}