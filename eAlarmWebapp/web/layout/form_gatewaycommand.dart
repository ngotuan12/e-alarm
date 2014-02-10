import 'package:polymer/polymer.dart';
import 'dart:html';
import '../src/util.dart';
@CustomTag('form-gatewaycommand')
class FormGatewaycommand extends PolymerElement
{
  FormGatewaycommand.created() : super.created();
  OListElement olListGateway;
  TextAreaElement txtRequest;
  TextAreaElement txtRespone;
  ButtonElement btnSendRequest;
  SpanElement txtID;
  bool get applyAuthorStyles => true;
  int GatewayID=0;
  Element tblGateway;
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
        tblGateway = this.shadowRoot.querySelector("#ListDevices");
        Responder responder = new Responder();
        Map request = new Map();
        request["Method"] = "GetListGateway";
       
        responder.onSuccess.listen((Map response)
        {
          List<Map> listGateway=response['ListGateway'];
          for(int i=0;i<listGateway.length;i++)
          {
            Map gateway = listGateway[i];
            //row
            TableRowElement row = new TableRowElement();
            row.classes.add("selectable");
            //column
            TableCellElement colId=new TableCellElement();
            colId.classes.add("center");
            colId.appendHtml((i+1).toString());
            TableCellElement colMac=new TableCellElement();
            colMac.classes.add("center");
            colMac.appendHtml(gateway["mac_add"].toString());
            TableCellElement colConnServer=new TableCellElement();
            colConnServer.classes.add("center");
            colConnServer.appendHtml(gateway["connected_server"].toString());
            //add column
            row.children.add(colId);
            row.children.add(colMac);
            row.children.add(colConnServer);
            
            //event click of row item
            row.onClick.listen((event)=>SendGatewayRequest(gateway["id"],gateway["mac_add"].toString(),gateway["connected_server"].toString()));
            //add row
            tblGateway.children.add(row);
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
  void SendGatewayRequest(Object obj, String Mac_Add,String Conn_Ser)
  {
    if(Conn_Ser!="null")
    {
      txtRequest.disabled=false;
      txtRespone.disabled=false;
      btnSendRequest.disabled=false;
      txtRequest.value="";
      txtRespone.value="";
      GatewayID=obj;
      txtID.text="Gateway Mac Address is " + Mac_Add;
    }
    else
    {
      btnSendRequest.disabled=true;
      txtRequest.disabled=true;
      txtRespone.disabled=true;
      txtID.text="";
    }
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
