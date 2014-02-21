import 'package:polymer/polymer.dart';
import 'dart:html';
@CustomTag("form-user-edit")
class FormUserEdit extends PolymerElement
{
  @published Map user;
  // of this element.
  bool get applyAuthorStyles => true;
  FormUserEdit.created() : super.created();
  enteredView()
  {
    super.enteredView();
    //window.alert(user.toString());
  }
}