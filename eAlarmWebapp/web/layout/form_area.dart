import 'package:polymer/polymer.dart';
import 'dart:html';
@CustomTag('form-area')
class FormArea extends PolymerElement
{
  @observable String currentAction;
  @observable Map currentArea;
  // of this element.
  bool get applyAuthorStyles => true;
  FormArea.created() : super.created();
  enteredView() 
  {
    currentAction="NONE";
    super.enteredView();
  }
  /*
   * @author : diennd
   * @since :17/2/2014
   * @version :1.0
   */
  void onAdd(CustomEvent event)
  {
	currentAction="ADD";
  }
  /*
   * @author:diennd
   * @since :17/2/2014
   * @version :1.0
   */
  void onGoBack(CustomEvent event)
  {
    currentAction="NONE";
  }
  /*
   * @author:diennd
   * @since:17/2/2014
   * @company:ex-artisan
   * @version:1.0
   */
  void onEdit(CustomEvent event)
  {
    currentAction="EDIT";
    currentArea=toObservable(event.detail);
  }
}