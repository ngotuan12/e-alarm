import 'package:polymer/polymer.dart';
import 'dart:html';
import '../src/util.dart';
@CustomTag('form-devicepropertives-list')
class FormDevicePropertives extends PolymerElement
{
  // This lets the Bootstrap CSS "bleed through" into the Shadow DOM
  // of this element.
  bool get applyAuthorStyles => true;
  FormDevicePropertives.created() : super.created();
  //String deviceproID;
  Element tblDevicePro;
  @observable String currentAction = "NONE";
  enteredView() 
  {
    super.enteredView();
    try
    {
      
      tblDevicePro = this.shadowRoot.querySelector("#ListDevicesPro");
      Responder responder = new Responder();
      Map request = new Map();
      request["Method"] = "onGetAllDevicePro";
      
      responder.onSuccess.listen((Map response)
          {
        List<Map> listDevicePro=response['all_devices_pro'];
        for(int i=0;i<listDevicePro.length;i++)
        {
          Map depro = listDevicePro[i];
          //row
          TableRowElement row = new TableRowElement();
          row.classes.add("selectable");
          
          //column
          TableCellElement colId=new TableCellElement();
          colId.classes.add("center");
          colId.appendHtml(depro["id"].toString());
          
          TableCellElement colName=new TableCellElement();
          colName.classes.add("center");
          colName.appendHtml(depro["name"].toString());
          
          TableCellElement colCode=new TableCellElement();
          colCode.classes.add("center");
          colCode.appendHtml(depro["code"].toString());
          
          TableCellElement colDescription=new TableCellElement();
          colDescription.classes.add("center");
          colDescription.appendHtml(depro["description"].toString());
          
          TableCellElement colType=new TableCellElement();
          colType.classes.add("center");
          colType.appendHtml(depro["type"].toString());
          
          TableCellElement colMin=new TableCellElement();
          colMin.classes.add("center");
          colMin.appendHtml(depro["min"].toString());
          
          TableCellElement colMax=new TableCellElement();
          colMax.classes.add("center");
          colMax.appendHtml(depro["max"].toString());
          
          TableCellElement colAction=new TableCellElement();
          colAction.classes.add("center");
          ButtonElement btnEdit=new ButtonElement();
          btnEdit.className="btn-action glyphicons pencil btn-success";
          btnEdit.appendHtml("<i></i>");
          btnEdit.onClick.listen((event)=>onEditGateway(depro));
                
          ButtonElement btnDelete=new ButtonElement();
          btnDelete.className="btn-action glyphicons remove_2 btn-danger";
          btnDelete.appendHtml("<i></i>");
          btnDelete.onClick.listen((event)=>onDeleteGateway(depro["id"].toString()));
                
          colAction.children.add(btnEdit);
          colAction.children.add(btnDelete);
          //add column
          row.children.add(colId);
          row.children.add(colName);
          row.children.add(colCode);
          row.children.add(colDescription);
          row.children.add(colType);
          row.children.add(colMin);
          row.children.add(colMax);
          row.children.add(colAction);
          
          String deviceproID= depro["id"].toString();
          //add row
          tblDevicePro.children.add(row);
        }
          });
      //error
      responder.onError.listen((Map error)
          {
        Util.showNotifyError(error["message"]);
          });
      AppClient.sendMessage(request, AlarmServiceName.DeviceService, AlarmServiceMethod.POST,responder);
      
    }
    catch(err)
    {
      Util.showNotifyError(err.toString());
    }
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