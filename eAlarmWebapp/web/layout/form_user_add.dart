import 'package:polymer/polymer.dart';
import 'dart:html';
@CustomTag('form-user-add')
class FormUserAdd extends PolymerElement{
  ButtonElement btnCancel;
  ButtonElement btnSignIn;
  ButtonElement btnSave;
  TextInputElement txtEmail;
  TextInputElement txtPhone;
  bool get applyAuthorStyles => true;
  FormUserAdd.created() : super.created();
  enteredView() 
  {
    super.enteredView();
    
    btnSave=this.shadowRoot.querySelector("#btnSave");
    btnCancel=this.shadowRoot.querySelector("#btnCancel");
    btnSignIn=this.shadowRoot.querySelector("#btnSignIn");
    txtPhone=this.shadowRoot.querySelector("#txtPhone");
    txtEmail=this.shadowRoot.querySelector("#txtEmail");
    //event
    btnCancel.onClick.listen(onExit);
    btnSave.onClick.listen(onSave);
    txtPhone.onKeyDown.listen(onCheck);
  }
  /*
   * @author:diennd
   * @since:20/2/2014
   * @company:ex-artisan
   * @version:1.0
   */
  void onCheck(Event e)
 {
   bool isok = window.confirm('ok');
   if(isok)
     window.alert("ok");
   else
     window.alert("cancel");
  }
  /*
   * @author:diennd
   * @since:20/2/2014
   * @company:ex-artisan
   * @version:1.0
   */
  void onExit(Event e)
  {
    dispatchEvent(new CustomEvent("goback",detail:""));
  }
  /*
   * @author:diennd
   * @since :20/2/2014
   * @company:ex-artisan
   * @version:1.0
   * 
   */
  void onSave(Event e)
  {
    window.alert(isEmail(txtEmail.value.trim()).toString());
    window.alert(isEmail(txtPhone.value.trim()).toString());
  }
  /*
   * @author:diennd
   * @since :20/2/2014
   * @company:ex-artisan
   * @version:1.0
   * 
   */
  bool isEmail(String s) {

    String regex = r'^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$';

    RegExp regExp = new RegExp(regex);

    return regExp.hasMatch(s);
  }
}