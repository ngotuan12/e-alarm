import 'package:polymer/polymer.dart';
import 'dart:html';
import 'dart:convert';
import 'package:intl/intl.dart';
import '../src/util.dart';
@CustomTag('form-report-issue-by-area')
class FormReportIssueByArea extends PolymerElement
{
  
	SelectElement selectorArea;
	SelectElement selectorDevice;
	ButtonElement btnGetReport;
	ButtonElement btnViewReport;
	IFrameElement iframeReport;
	DateInputElement dtFromDate;
	DateInputElement dtToDate;
	List<Map> listArea;	
	List listDevices;
	FormReportIssueByArea.created() : super.created();
	bool get applyAuthorStyles => true;
	
	enteredView() 
	{
		super.enteredView();
		DateTime now = new DateTime.now();
		selectorArea = this.shadowRoot.querySelector("#selectorArea");
		selectorDevice = this.shadowRoot.querySelector("#selectorDevice");
		iframeReport = this.shadowRoot.querySelector("#iframeReport");
		btnGetReport = this.shadowRoot.querySelector("#btnGetReport");
		btnViewReport = this.shadowRoot.querySelector("#btnViewReport");
		//from date
		dtFromDate = this.shadowRoot.querySelector("#dtFromDate");
		DateTime fromDate = new DateTime.utc(now.year, now.month, 1);
		dtFromDate.valueAsDate = fromDate;
		//to date
		dtToDate = this.shadowRoot.querySelector("#dtToDate");
		DateTime toDate = new DateTime.utc(now.year, now.month, now.day);
		dtToDate.valueAsDate = toDate;
		//event
		btnGetReport.onClick.listen(getReport);
		btnViewReport.onClick.listen(getReport);
		selectorArea.onChange.listen((__){
			if(selectorArea.selectedIndex==0)
			{
				//device
				selectorDevice.children.clear();
				OptionElement op=new OptionElement();
				op.value='-1';
				op.text='Tất cả';
				selectorDevice.children.add(op);
    			if(listDevices !=null)
    				for(int i=0;i<listDevices.length;i++)
    				{
    					Map device=listDevices[i];
    					OptionElement op=new OptionElement();
    					op.value=JSON.encode(device);
    					op.text=device['name']+"/"+device['code'];
    					selectorDevice.children.add(op);
    				}
			}
    		else
    		{
    			OptionElement op;
    			op=selectorArea.children.elementAt(selectorArea.selectedIndex);
    			Map area=JSON.decode(op.value);
    			List devices = listDevices.where((element){
    				if(element["area_id"]==area["id"])
    					return true;
    				return false;
    			}).toList();
				//device
				selectorDevice.children.clear();
				op=new OptionElement();
				op.value='-1';
				op.text='Tất cả';
				selectorDevice.children.add(op);
  				if(devices !=null)
  				for(int i=0;i<devices.length;i++)
  				{
  					Map device=devices[i];
  					OptionElement op=new OptionElement();
  					op.value=JSON.encode(device);
  					op.text=device['name']+"/"+device['code'];
  					selectorDevice.children.add(op);
  				}
    		}
		});
		//load device
		init();
	}
	/**
	 * 
	 */
	void init()
	{
		//device
		selectorDevice.children.clear();
		OptionElement op=new OptionElement();
		op.value='-1';
		op.text='Tất cả';
		selectorDevice.children.add(op);
		//area
		selectorArea.children.clear();
		op=new OptionElement();
		op.value='-1';
		op.text='Tất cả';
		selectorArea.children.add(op);
		//load data
		Responder responder = new Responder();
		Map request = new Map();
		request["Method"] = "form_report_issue_load";
		responder.onSuccess.listen((Map response)
		{
			listDevices=response['devices'];
			if(listDevices !=null)
				for(int i=0;i<listDevices.length;i++)
				{
					Map device=listDevices[i];
					OptionElement op=new OptionElement();
					op.value=JSON.encode(device);
					op.text=device['name']+"/"+device['code'];
					selectorDevice.children.add(op);
				}
			listArea = response['areas'];
			if(listArea !=null)
				for(int i=0;i<listArea.length;i++)
				{
					Map area=listArea[i];
					OptionElement op=new OptionElement();
					op.value=JSON.encode(area);
					op.text=area['name'];
					selectorArea.children.add(op);
				}
		});
		//error
		responder.onError.listen((Map error)
		{
			Util.showNotifyError(error["message"]);
		});
		AppClient.sendMessage(request, AlarmServiceName.ReportService, AlarmServiceMethod.POST,responder);
	}
	/**
	 * 
	 */
	void getReport(Event e)
	{
		String strType = "";
		if(e.currentTarget==btnGetReport)
			strType = "DownLoad";
		else if(e.currentTarget==btnViewReport)
			strType = "View";
		Responder responder = new Responder();
		Map request = new Map();
		request["Method"] = "IssueReportByArea";
		//device_id
		if(selectorDevice.selectedIndex==0)
			request["device_id"] = "ALL";
		else
		{
			OptionElement op=new OptionElement();
			op=selectorDevice.children.elementAt(selectorDevice.selectedIndex);
			Map device=JSON.decode(op.value);
			request["device_id"] = device['id'].toString();
		}
		//area_id
		if(selectorArea.selectedIndex==0)
			request["area_id"] = "ALL";
		else
		{
			OptionElement op=new OptionElement();
			op=selectorArea.children.elementAt(selectorArea.selectedIndex);
			Map area=JSON.decode(op.value);
			request["area_id"] = area['id'].toString();
		}
		//from date
		DateFormat formatter = new DateFormat('dd/MM/yyyy');
		String strFromDate = formatter.format(dtFromDate.valueAsDate);
		request["from_date"] = strFromDate;
		//to date
		String strToDate = formatter.format(dtToDate.valueAsDate);
		request["to_date"] = strToDate;
		responder.onSuccess.listen((Map response)
		{
			if(strType=="DownLoad")
				window.open(AppClient.url+"report/"+response["FileOut"], "_self");
			else if(strType=="View")
			{
				String link =  AppClient.url+"report/"+response["FileOut"];
	    		iframeReport.hidden = false;
	    		iframeReport.src = "https://docs.google.com/viewer?embedded=true&url=" + link;
			}
		});
		//error
		responder.onError.listen((Map error)
		{
		Util.showNotifyError(error["message"]);
		});
		AppClient.sendMessage(request, AlarmServiceName.ReportService, AlarmServiceMethod.POST,responder);
	}
}