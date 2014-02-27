import 'package:polymer/polymer.dart';
import 'dart:html';
import '../src/util.dart';
@CustomTag('form-devicepropertives-edit')
class FormDevicesProEdit extends PolymerElement
{
  // This lets the Bootstrap CSS "bleed through" into the Shadow DOM
  // of this element.
    bool get applyAuthorStyles => true;
    FormDevicesProEdit.created() : super.created();
    @published Map DeviceCon;
    
//  InputElement txtDeviceProID=new InputElement(type:"text"); 
  InputElement txtCode=new InputElement(type:"text");
  InputElement txtDescription=new InputElement(type:"text");
  InputElement txtType=new InputElement(type:"text");
  InputElement txtMin=new InputElement(type:"text");
  InputElement txtMax=new InputElement(type:"text");
  InputElement txtMinWarning=new InputElement(type:"text");
  InputElement txtMaxWarning=new InputElement(type:"text");
  ButtonElement btnSave=new ButtonElement();
  ButtonElement btnCancel=new ButtonElement();
  InputElement txtName=new InputElement(type:"text");
  
  enteredView() 
  {
    super.enteredView();
    GetElement();
  }
  
  void GetElement()
  {
    btnSave=this.shadowRoot.querySelector("#btnSave");
    btnCancel=this.shadowRoot.querySelector("#btnCancel");
    btnSave.onClick.listen((event)=>onSaveData());
    btnCancel.onClick.listen((event)=>onCancel());
//    txtDeviceProID=this.shadowRoot.querySelector("#txtDeviceProID");
    txtName=this.shadowRoot.querySelector("#txtName");
    txtCode=this.shadowRoot.querySelector("#txtCode");
    txtDescription=this.shadowRoot.querySelector("#txtDescription");
    txtType=this.shadowRoot.querySelector("#txtType");
    txtMin=this.shadowRoot.querySelector("#txtMin");
    txtMax=this.shadowRoot.querySelector("#txtMax");
    txtMinWarning=this.shadowRoot.querySelector("#txtMinWar");
    txtMaxWarning=this.shadowRoot.querySelector("#txtMaxWar");
//    txtDeviceProID.disabled=true;
    if(DeviceCon!=null)
    {
//      txtDeviceProID.value=DeviceCon["id"].toString();
      txtName.value =DeviceCon["name"].toString();
      txtCode.value=DeviceCon["code"].toString();
      txtDescription.value=DeviceCon["description"].toString();
      txtType.value=DeviceCon["type"].toString();
      txtMin.value=DeviceCon["min"].toString();
      txtMax.value=DeviceCon["max"].toString();
      txtMinWarning.value = DeviceCon["min_alarm"].toString();
      txtMaxWarning.value = DeviceCon["max_alarm"].toString();
    }
  }
  
  void onSaveData()
  {
    try
         {
            Responder responder = new Responder();
            Map request = new Map();
       if(DeviceCon!=null){
             request["Method"] = "onEditDevicePro";
             request["ID"]=DeviceCon["id"].toString();
       }else{
        request["Method"] = "onAddDevicePro";
       }
       request["min_alarm"] = txtMinWarning.value.toString();
       request["max_alarm"] = txtMaxWarning.value.toString();
       request["name"]=txtName.value.toString();
       request["code"]=txtCode.value.toString();
       request["description"]=txtDescription.value.toString();       
       request["type"]=txtType.value.toString();
       request["min"]=txtMin.value.toString();
       request["max"]=txtMax.value.toString();
       
       if(request["min_alarm"]=="" ||request["max_alarm"]=="" ||request["name"]=="" ||request["code"]==""||request["description"]==""||request["type"]==""||request["min"]==""||request["max"]=="")
       {
        Util.showNotifyError("Data input is  invalid");
       }
       else
       {
             responder.onSuccess.listen((Map response)
        {
         dispatchEvent(new CustomEvent("goback",detail:""));
             });
             //error
             responder.onError.listen((Map error)
        {
               Util.showNotifyError(error["message"]);
        });
             AppClient.sendMessage(request, AlarmServiceName.DeviceService, AlarmServiceMethod.POST,responder);
       }
         }
         catch(err)
         {
            Util.showNotifyError(err.toString());
         }
  }
  
  void onCancel()
  {
    dispatchEvent(new CustomEvent("goback",detail:""));
  }
}