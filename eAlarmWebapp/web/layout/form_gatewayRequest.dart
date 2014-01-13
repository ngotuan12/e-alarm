import 'package:polymer/polymer.dart';
import 'dart:convert';
import '../src/util.dart';
@CustomTag('form_gatewayRequest')
class FormGatewayRequest extends PolymerElement
{
  FormGatewayRequest.created() : super.created();
  
  bool get applyAuthorStyles => true;
  
  enteredView() 
  {
      super.enteredView();
    Responder responder = new Responder();
    Map request = new Map();
    request["Method"] = "AddGateway";
    responder.onSuccess.listen((Map response)
    {
      
    });
    //error
    responder.onError.listen((String strError)
    {
      print(strError);
    });
    //
    AppClient.sendMessage(request, AlarmServiceName.GatewayService, AlarmServiceMethod.POST,responder);
  }
}