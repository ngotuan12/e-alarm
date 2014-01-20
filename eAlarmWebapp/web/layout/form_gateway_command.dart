import 'package:polymer/polymer.dart';
import 'dart:html';
import '../src/util.dart';
@CustomTag('form-gateway-command')
class FormGatewayCommand extends PolymerElement
{
  FormGatewayCommand.created() : super.created();
  OListElement olListGateway;
  TextAreaElement txtRequest;
  TextAreaElement txtRespone;
  ButtonElement btnSendRequest;
  HeadingElement txtID;
  bool get applyAuthorStyles => true;
  int GatewayID=0;
  enteredView() 
  {
      super.enteredView();
      try
      {
        olListGateway = this.shadowRoot.querySelector("#listGateway");
        txtRequest=this.shadowRoot.querySelector("#txtRequest");
        txtRespone=this.shadowRoot.querySelector("#txtRespone");
        btnSendRequest=this.shadowRoot.querySelector("#btnSendRequest");
        txtID=this.shadowRoot.querySelector("#txtID");
        btnSendRequest.onClick.listen(
            (event) => SendCommand());
        Responder responder = new Responder();
        Map request = new Map();
        request["Method"] = "GetListGateway";
        responder.onSuccess.listen((Map response)
        {
          List<Map> listGateway=response['ListGateway'];
          for(int i=0;i<listGateway.length;i++)
          {
            Map gateway = listGateway[i];
            //add children
            LIElement liDevice=new LIElement();
            liDevice.style.marginTop="5px";
            liDevice.style.marginLeft="5px";
            liDevice.style.borderBottom="1px solid #616161";
            
            //<a>
            AnchorElement aDevice=new AnchorElement();
            aDevice.style.textDecoration="none";
            aDevice.style.color="#fff";
            aDevice.href="#";
            aDevice.text = gateway["mac_add"];
            
            aDevice.onMouseOver.listen((event)=>aDevice.style.color='gray');
            aDevice.onMouseLeave.listen((event)=>aDevice.style.color='#fff');
            liDevice.onMouseOver.listen((event)=>aDevice.style.color='gray');
            liDevice.onMouseLeave.listen((event)=>aDevice.style.color='#fff');
            //
            liDevice.children.add(aDevice);
            olListGateway.children.add(liDevice);
            GatewayID=gateway["id"];
            liDevice.onClick.listen((event)=>SendGatewayRequest(gateway["mac_add"].toString()));
          }
        });
        //error
        responder.onError.listen((Map error)
        {
          Util.showNotifyError(error["message"]);
        });
        AppClient.sendMessage(request, AlarmServiceName.GatewayService, AlarmServiceMethod.POST,responder);
        
      }
      catch(err)
      {
        Util.showNotifyError(err.toString());
      }
  }
  void SendGatewayRequest(String ID)
  {
    txtRequest.value="";
    txtRespone.value="";
    txtID.text="Gateway MacAddress is " + ID;
  }
  void SendCommand()
  {
    //txtRequest.text=GatewayID.toString();
    Responder responder = new Responder();
    Map request = new Map();
    request["Method"] = "AddGateway";
    request["Gateway_ID"]=GatewayID;
    request["Request"]=txtRequest.value;
    responder.onSuccess.listen((Map response)
    {
      txtRespone.value=response['Respone'];
    });
    //error
    responder.onError.listen((Map error)
    {
      Util.showNotifyError(error["message"]);
    });
    AppClient.sendMessage(request, AlarmServiceName.GatewayService, AlarmServiceMethod.POST,responder);
  }
}