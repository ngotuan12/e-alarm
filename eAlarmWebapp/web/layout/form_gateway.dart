import 'package:polymer/polymer.dart';
import 'dart:html';
import '../src/util.dart';
@CustomTag('form-gateway')
class FormGateway extends PolymerElement
{
  // This lets the Bootstrap CSS "bleed through" into the Shadow DOM
  // of this element.
  bool get applyAuthorStyles => true;
  FormGateway.created() : super.created();
  int GatewayID=0;
  Element tblGateway;
  enteredView() 
  {
    super.enteredView();
    try
    {
      
      tblGateway = this.shadowRoot.querySelector("#ListGateway");
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
          
          TableCellElement colStatus=new TableCellElement();
          colStatus.classes.add("center");
          if(gateway["connected_server"].toString()=="1")
          colStatus.appendHtml("OK");
          else
            colStatus.appendHtml("Not OK");
          
          TableCellElement colAction=new TableCellElement();
          colAction.classes.add("center");
          colAction.appendHtml("<button id='' class='btn-action glyphicons remove_2 btn-danger'><i></i></button>");
          colAction.
          //add column
          row.children.add(colId);
          row.children.add(colMac);
          row.children.add(colConnServer);
          row.children.add(colStatus);
          row.children.add(colAction);
          GatewayID=gateway["id"];
          //event click of row item
          row.onClick.listen((event)=>SendGatewayRequest(gateway["mac_add"].toString(),gateway["connected_server"].toString()));
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
  void SendGatewayRequest(String Mac_Add,String Conn_Ser)
  {
    if(Conn_Ser!="null")
    {
      
    }
    else
    {
      
    }
  }
}