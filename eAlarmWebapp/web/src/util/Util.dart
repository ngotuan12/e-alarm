part of EASUtil;

class Util 
{
	static void execJS(String javascript, [bool removeAfter = true])
	{
		Element element = new Element.tag("script");
		element.attributes["type"] = "text/javascript";
		element.text = javascript;
		document.body.nodes.add(element);
		if( removeAfter == true ) 
		{
			element.remove();
		}
	}
	
	static void startLoader()
	{
		SessionValue.main.showCircleLoader = true;
	}
	
	static void stopLoader()
	{
		SessionValue.main.showCircleLoader = false;
	}
	
	static void showNotifyError(String errorMessage)
	{
		Map newError = {"type":"notyfy_error","message":errorMessage};
		SessionValue.main.listNotify.add(newError);
		Timer timer = new Timer.periodic(new Duration(seconds: 5), (Timer timer)
		{
			timer.cancel();
			SessionValue.main.listNotify.remove(newError);
		} );
	}
	
	static void showNotifySuccess(String successMessage)
	{
		Map newError = {"type":"notyfy_success","message":successMessage};
		SessionValue.main.listNotify.add(newError);
		Timer timer = new Timer.periodic(new Duration(seconds: 5), (Timer timer)
		{
			timer.cancel();
			SessionValue.main.listNotify.remove(newError);
		} );
	}
	
	static void showNotifyAlert(String alertMessage)
	{
		Map newError = {"type":"notyfy_alert","message":alertMessage};
		SessionValue.main.listNotify.add(newError);
		Timer timer = new Timer.periodic(new Duration(seconds: 5), (Timer timer)
		{
			timer.cancel();
			SessionValue.main.listNotify.remove(newError);
		} );
	}
	
	static void showNotifyWarning(String warningMessage)
	{
		Map newError = {"type":"notyfy_warning","message":warningMessage};
		SessionValue.main.listNotify.add(newError);
		Timer timer = new Timer.periodic(new Duration(seconds: 5), (Timer timer)
		{
			timer.cancel();
			SessionValue.main.listNotify.remove(newError);
		} );
	}
	
	static void showNotifyInformation(String inforMessage)
	{
		Map newError = {"type":"notyfy_information","message":inforMessage};
		SessionValue.main.listNotify.add(newError);
		Timer timer = new Timer.periodic(new Duration(seconds: 5), (Timer timer)
		{
			timer.cancel();
			SessionValue.main.listNotify.remove(newError);
		} );
	}
	
	static void showNotifyConfirm(String confirmMessage)
	{
		Map newError = {"type":"notyfy_confirm","message":confirmMessage};
		SessionValue.main.listNotify.add(newError);
		Timer timer = new Timer.periodic(new Duration(seconds: 5), (Timer timer)
		{
			timer.cancel();
			SessionValue.main.listNotify.remove(newError);
		} );
	}
	static String nvl(dynamic obj,String nullValue)
	{
		if(obj==null)
		{
			return nullValue;
		}
		else
		{
			return obj.toString();
		}
	}
}
