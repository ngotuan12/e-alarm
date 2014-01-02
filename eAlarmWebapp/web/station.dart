library eAlarm;

import 'dart:html';
import 'dart:convert';
import 'package:google_maps/google_maps.dart';
import 'dart:async';
import 'src/util.dart';
List<Map> listDevices;
TableElement tbDevices;
/*
* @author ducdienpt
* @since:19/12/2013
* @company: ex-artisan
* @version :1.0
*/
	void main()
	{
		
	}
	/*
	 * @author: ducdienpt
	 * @since: 19/12/2013
	 * @company: ex-artisan
	 * @version: 1.0
	 */
	//get all onGetAllDevices
	void init()
	{
	//get data
	Map request = new Map();
	request["Method"] = "onGetAllDevices";
	//Listen
	Responder responder = new Responder();
	//success
	responder.onSuccess.listen((Map response)
	{
	List<Map> listDevices=response['all_devices_info'];
	//init Option Select
	if(listDevices !=null)
	{
		for(int i=0;i<listDevices.length;i++)
		{
			//view device
			
		}
	}
	});
	//error
	responder.onError.listen((String strError)
	{
	print(strError);
	}
	);
	//send to server
	AppClient.sendMessage(request, AlarmServiceName.DeviceService, AlarmServiceMethod.POST,responder);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	