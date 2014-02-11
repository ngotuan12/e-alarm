import 'package:polymer/polymer.dart';
import 'dart:html';
import '../src/util.dart';
@CustomTag('form-gateway-list')
class FormGatewayList extends PolymerElement
{
  // This lets the Bootstrap CSS "bleed through" into the Shadow DOM
  // of this element.
  bool get applyAuthorStyles => true;
	FormGatewayList.created() : super.created();
  int GatewayID=0;
	ButtonElement btnAdd=new ButtonElement();
  Element tblGateway;
	@observable String currentAction = "NONE";
  enteredView() 
  {
    super.enteredView();
	btnAdd=this.shadowRoot.querySelector("#btnAdd");
	btnAdd.onClick.listen((event)=>onAddGateway());
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
          	if(gateway["status"].toString()=="1")
          	colStatus.appendHtml("Enable");
          	else
            	colStatus.appendHtml("Disble");
          
          	TableCellElement colAction=new TableCellElement();
			colAction.classes.add("center");
			ButtonElement btnEdit=new ButtonElement();
			btnEdit.className="btn-action glyphicons pencil btn-success";
			btnEdit.appendHtml("<i></i>");
			btnEdit.onClick.listen((event)=>onEditGateway(gateway));
			
			ButtonElement btnDelete=new ButtonElement();
			btnDelete.className="btn-action glyphicons remove_2 btn-danger";
			btnDelete.appendHtml("<i></i>");
			btnDelete.onClick.listen((event)=>onDeleteGateway(gateway["id"].toString()));
			
			colAction.children.add(btnEdit);
			colAction.children.add(btnDelete);
			
			
          //add column
          	row.children.add(colId);
          	row.children.add(colMac);
          	row.children.add(colConnServer);
          	row.children.add(colStatus);
          	row.children.add(colAction);
			
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
  
	void onAddGateway()
	{
		dispatchEvent(new CustomEvent("add",detail:"1"));
	}
  	void onEditGateway(Map ID)
  	{
		dispatchEvent(new CustomEvent("edit",detail:ID));
  	}
	
	void onDeleteGateway(String ID)
	{
		dispatchEvent(new CustomEvent("edit",detail:ID));
	}
}