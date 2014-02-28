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
  SelectElement selType=new SelectElement();
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
    selType=this.shadowRoot.querySelector("#selType");
    txtMin=this.shadowRoot.querySelector("#txtMin");
    txtMax=this.shadowRoot.querySelector("#txtMax");
    txtMinWarning=this.shadowRoot.querySelector("#txtMinWar");
    txtMaxWarning=this.shadowRoot.querySelector("#txtMaxWar");
    selType.onChange.listen((event)=>onChangeType());
//    txtDeviceProID.disabled=true;
    if(DeviceCon!=null)
    {
//      txtDeviceProID.value=DeviceCon["id"].toString();
      txtName.value =DeviceCon["name"].toString();
      txtCode.value=DeviceCon["code"].toString();
      txtDescription.value=DeviceCon["description"].toString();
      
      if(DeviceCon["type"].toString()=="1")
      {
      	selType.selectedIndex=0;
      }
      else{
      	selType.selectedIndex=1;
      	txtMin.disabled = true;
      	txtMax.disabled = true;
        txtMinWarning.disabled = true;
        txtMaxWarning.disabled = true;
      }
      
      txtMin.value=DeviceCon["min"].toString();
      txtMax.value=DeviceCon["max"].toString();
      txtMinWarning.value = DeviceCon["min_alarm"].toString();
      txtMaxWarning.value = DeviceCon["max_alarm"].toString();
    }
  }
  void onChangeType()
  {
  	if(selType.selectedIndex==0){
  		txtMin.disabled = false;
      txtMax.disabled = false;
      txtMinWarning.disabled = false;
      txtMaxWarning.disabled = false;
      if(DeviceCon==null)
      {
        txtMin.value="";
        txtMax.value="";
        txtMinWarning.value = "";
        txtMaxWarning.value = "";
      }
  	}else if(selType.selectedIndex==1){
  		txtMin.disabled = true;
      txtMax.disabled = true;
      txtMinWarning.disabled = true;
      txtMaxWarning.disabled = true;
      if(DeviceCon==null)
      {
      	txtMin.value="0";
        txtMax.value="1";
        txtMinWarning.value = "0";
        txtMaxWarning.value = "1000";
      }
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
       request["min_Alarm"] = txtMinWarning.value;
       request["max_Alarm"] = txtMaxWarning.value;
       request["name"]=txtName.value;
       request["code"]=txtCode.value;
       request["description"]=txtDescription.value;       
       if(selType.selectedIndex==0)
       {
      	 request["type"]= 1;
       }
       else if(selType.selectedIndex==1){
      	 request["type"]= 2;
       }
       request["symbol"]=txtCode.value;
       request["min"]=txtMin.value;
       request["max"]=txtMax.value;
       
       if(request["min_alarm"]=="" ||request["max_alarm"]=="" ||request["name"]=="" ||request["code"]==""||request["description"]==""||request["type"]==""||request["min"]==""||request["max"]=="")
       {
        Util.showNotifyError("Data input is  invalid");
       }
       else
       {
             responder.onSuccess.listen((Map response)
        {
         dispatchEvent(new CustomEvent("goback",detail:""));
         if(DeviceCon!=null){
        	 Util.showNotifySuccess("Thay đổi chi tiết thuộc tính thiết bị thành công");
                }else{
                	insertbydevice();
                	Util.showNotifySuccess("Thêm mới chi tiết thuộc tính thiết bị thành công");
                }
         
             });
             //error
             responder.onError.listen((Map error)
        {
            	  Util.showNotifyError(error.toString());
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
  void insertbydevice(){
  	try
     	{
           Responder responder1 = new Responder();
           Map request = new Map();
					request["Method"] = "onFixDeviceInfo";
					responder1.onSuccess.listen((Map response)
           {
            	 Util.showNotifySuccess("Thêm mới vào thông tin thiết bị thành công");
           });
           //error
					responder1.onError.listen((Map error)
            {
                   Util.showNotifyError(error["message"]);
            });
            AppClient.sendMessage(request, AlarmServiceName.DeviceService, AlarmServiceMethod.POST,responder1);          
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