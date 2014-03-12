import 'package:polymer/polymer.dart';
import 'dart:html';
import 'dart:async';
import 'dart:convert';
import '../src/util.dart';
import 'dart:js';
@CustomTag('form-report-issue-by-area')
class FormReportIssueByArea extends PolymerElement
{
  

	SelectElement selectorDevice;
	ButtonElement btnGetReport;
	ButtonElement btnViewReport;
	IFrameElement iframeReport;
	var datepicker;	
	List<Map> listArea;	
	@observable List listDevices=toObservable([]);
	@observable List CurrentDevices=toObservable([]);
	@observable List devices=toObservable([]);
  FormReportIssueByArea.created() : super.created();
  bool get applyAuthorStyles => true;
	
	enteredView() 
	{
		super.enteredView();
		
		selectorDevice = this.shadowRoot.querySelector("#selectorDevice");
		iframeReport = this.shadowRoot.querySelector("#iframeReport");
		datepicker = this.shadowRoot.querySelector("#inputDatePicker");
		btnGetReport = this.shadowRoot.querySelector("#btnGetReport");
		btnViewReport = this.shadowRoot.querySelector("#btnViewReport");
		//event
		btnGetReport.onClick.listen(getReport);
		btnViewReport.onClick.listen(viewReport);
		final element = shadowRoot.querySelector("#inputDatePicker");
		context.callMethod(r'$', [element]).callMethod('datepicker');
		//load device
		init();
	}
	/*
	 * 
	 */
	void init()
	{
		selectorDevice.children.clear();
		OptionElement op=new OptionElement();
		op.value='-1';
		op.text='Tất cả';
		selectorDevice.children.add(op);
		
		Responder responder = new Responder();
		Map request = new Map();
		request["Method"] = "onGetAllDevices";
		responder.onSuccess.listen((Map response)
		{
			listDevices=response['all_devices_info'];
			if(listDevices !=null)
				for(int i=0;i<listDevices.length;i++)
				{
					Map device=listDevices[i];
					OptionElement op=new OptionElement();
					op.value=JSON.encode(device);
					op.text=device['name']+"/"+device['code'];
					selectorDevice.children.add(op);
				}
		});
		
		//error
		responder.onError.listen((Map error)
		{
		Util.showNotifyError(error["message"]);
		});
		AppClient.sendMessage(request, AlarmServiceName.DeviceService, AlarmServiceMethod.POST,responder);
	}
	List<Map> filterByType(List<Map> locations,List<String> listType)
	{
		return locations.where((location){
			for(int i=0;i<listType.length;i++)
			{
				if(listType[i]==location["level"])
				{
					return true;
				}
			}
			return false;
		}).toList();
	}
	
	void getReport(Event e)
	{
		Responder responder = new Responder();
		Map request = new Map();
		request["Method"] = "IssueReportByArea";
		if(selectorDevice.selectedIndex==0)
			request["device_id"] = "ALL";
		else
		{
			OptionElement op=new OptionElement();
			op=selectorDevice.children.elementAt(selectorDevice.selectedIndex);
			Map device=JSON.decode(op.value);
			request["device_id"] = device['id'];
		}
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
	
	void viewReport(Event e)
	{
		iframeReport.hidden = true;
		Responder responder = new Responder();
		Map request = new Map();
		request["Method"] = "IssueReportByArea";
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