import 'package:polymer/polymer.dart';
import 'dart:html';
import 'dart:async';
import 'dart:convert';
import '../src/util.dart';
@CustomTag('form-report-device-by-area')
class FormReportDeviceByArea extends PolymerElement
{
  
  SelectElement selectorCity;
  SelectElement selectorDistrict;
  ButtonElement btnGetReport;
  ButtonElement btnViewReport;
  IFrameElement iframeReport;
  List<Map> listArea;	

  FormReportDeviceByArea.created() : super.created();
  bool get applyAuthorStyles => true;
	
    enteredView() 
  {
		super.enteredView();
		
		selectorCity = this.shadowRoot.querySelector("#selectorCity");
		selectorDistrict = this.shadowRoot.querySelector("#selectorDistrict");
		iframeReport = this.shadowRoot.querySelector("#iframeReport");
		btnGetReport = this.shadowRoot.querySelector("#btnGetReport");
		btnViewReport = this.shadowRoot.querySelector("#btnViewReport");
		
		btnGetReport.onClick.listen(getReport);
		btnViewReport.onClick.listen(viewReport);
		
		selectorCity.onChange.listen(onCityChange);
		
		getCity();
		
		
  }
	
	void onCityChange(Event e){
	
	}
	
	void getCity(){
	Map request = new Map();
	request["Method"] = "GetAllAreaActive";
	//Listen
	Responder responder = new Responder();
	//success
	responder.onSuccess.listen((Map response)
	{
	//Util.showNotifyInformation(response.toString());
	List<Map> temp=response['ListAreaActive'];
	listArea = filterByType(temp, ["2"]);
	//init Option Select
	for(int i=0;i<listArea.length;i++)
	{
	//option
	Map element= listArea[i];
	String name=element['Name'];
	OptionElement op = new Element.option();
	op.text=name;
	op.value = JSON.encode(element);
	//add are
	selectorCity.children.add(op);
	}
	
	});
	//error
	responder.onError.listen((Map error)
	{
	Util.showNotifyError(error["message"]);
	}
	);
	//send to server
	AppClient.sendMessage(request, AlarmServiceName.AreaService, AlarmServiceMethod.POST,responder);
	}
	
	void getDistrict(){
	
	}
	
	List<Map> filterByType(List<Map> locations,List<String> listType)
	{
		return locations.where((location){
			for(int i=0;i<listType.length;i++)
			{
				if(listType[i]==location["Type"])
				{
					return true;
				}
			}
			return false;
		}).toList();
	}
	
	void getReport(Event e){
	Responder responder = new Responder();
	Map request = new Map();
	request["Method"] = "DeviceReportByArea";
	responder.onSuccess.listen((Map response)
	{
	window.open(AppClient.url+"report/"+response["FileOut"], "_self");
	});
	//error
	responder.onError.listen((Map error)
	{
	Util.showNotifyError(error["message"]);
	});
	AppClient.sendMessage(request, AlarmServiceName.ReportService, AlarmServiceMethod.POST,responder);
	}
	
	void viewReport(Event e){
	iframeReport.hidden = true;
	Responder responder = new Responder();
	Map request = new Map();
	request["Method"] = "DeviceReportByArea";
	responder.onSuccess.listen((Map response)
	{
	String link =  AppClient.url+"report/"+response["FileOut"];
	iframeReport.hidden = false;
	iframeReport.src = "https://docs.google.com/viewer?embedded=true&url=" + link;
	});
	//error
	responder.onError.listen((Map error)
	{
	Util.showNotifyError(error["message"]);
	});
	AppClient.sendMessage(request, AlarmServiceName.ReportService, AlarmServiceMethod.POST,responder);
	}
}