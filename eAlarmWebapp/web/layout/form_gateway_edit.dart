import 'package:polymer/polymer.dart';
import 'dart:html';
import '../src/util.dart';
@CustomTag('form-gateway-edit')
class FormGatewayEdit extends PolymerElement
{
  // This lets the Bootstrap CSS "bleed through" into the Shadow DOM
  // of this element.
  	bool get applyAuthorStyles => true;
	FormGatewayEdit.created() : super.created();
  	@published Map Gateway;
		
	InputElement txtID=new InputElement(type:"text");	
	InputElement txtMacAdd=new InputElement(type:"text");
	InputElement txtConnSer=new InputElement(type:"text");
	InputElement txtType=new InputElement(type:"text");
	SelectElement txtStatus=new SelectElement();
	InputElement txtIPAdd=new InputElement(type:"text");
	ButtonElement btnSave=new ButtonElement();
	ButtonElement btnCancel=new ButtonElement();
	
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
		txtID=this.shadowRoot.querySelector("#GatewayID");
		txtMacAdd=this.shadowRoot.querySelector("#MacAdd");
		txtConnSer=this.shadowRoot.querySelector("#ConServer");
		txtType=this.shadowRoot.querySelector("#Type");
		txtStatus=this.shadowRoot.querySelector("#Status");
		txtIPAdd=this.shadowRoot.querySelector("#IPAdd");
		txtID.disabled=true;
		if(Gateway!=null)
		{
			txtID.value=Gateway["id"].toString();
			txtMacAdd.value=Gateway["mac_add"].toString();
			txtConnSer.value=Gateway["connected_server"].toString();
			txtType.value=Gateway["type"].toString();
			if(Gateway["status"].toString()=="0")
			{
			txtStatus.selectedIndex=0;
			}
			else
			txtStatus.selectedIndex=1;
			txtIPAdd.value=Gateway["ip_add"].toString();
		}
	}
	
	void onSaveData()
	{
		try
	    {
	      	Responder responder = new Responder();
	      	Map request = new Map();
			if(Gateway!=null)
	      		request["Method"] = "UpdateGatewayDetail";
			else
				request["Method"] = "AddGatewayDetail";
			request["Gateway_ID"]=txtID.value;
			request["mac_add"]=txtMacAdd.value;
			request["type"]=txtType.value;
			if(txtStatus.selectedIndex==0)
				request["status"]="0";
			else
				request["status"]="1";
			request["connected_server"]=txtConnSer.value;
			request["ip_add"]=txtIPAdd.value;
			if(request["mac_add"]=="" ||request["type"]==""||request["connected_server"]==""||request["ip_add"]==""||request["status"]=="")
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
		      	AppClient.sendMessage(request, AlarmServiceName.GatewayService, AlarmServiceMethod.POST,responder);
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