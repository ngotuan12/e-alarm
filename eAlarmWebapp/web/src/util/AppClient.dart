part of EASUtil;

class AppClient
{
	static final String url = "http://54.243.244.187:7777/";
//	static final String url = "http://127.0.0.1:7777/AlarmServer/";
	static HttpRequest request = new HttpRequest();
	/*
	 * @author TuanNA
	 * @since 13/12/2013
	 * @Company Ex-Artisan
	 * @param
	 * 		strData: Data of request
	 * 		strServiceName: service name
	 * 		strMethod: Method (POST,GET,DELETE)
	 * 		onSuccess: Function
	 * 		onFaile:Function
	 */
	static void sendMessage(Map mapData,String strServiceName,String strMethod,Responder response)
	{
		try
		{
			String strURL = url + strServiceName;
			request = new HttpRequest();
//			request.setRequestHeader("autKey", autKey);
//			request.g
			//start loader
			Util.startLoader();
			//listen
			request.onReadyStateChange.listen(
			(_)
			{
				if(request.readyState == HttpRequest.DONE)
				{
					Util.stopLoader();
					if(request.status == 200)
					{
						String strResponse = request.responseText;
						Map mapResponse = JSON.decode(strResponse);
						if(mapResponse["handle"]=="on_error")
						{
							response.error(mapResponse);
							if(mapResponse["code"]=="EAS-SYS-002")
							{
								SessionValue.main.isLogin = false;
							}
							return;
						}
						response.success(mapResponse);
					}
					else
					{
						response.error({"message":"Can't connect to server. Pls, try again!"});
					}
				}
			});
			//Prepare 
			request.open(strMethod, strURL, async: true);
			request.setRequestHeader("Authorization", SessionUser.sessionKey);
			//Session
			mapData["SessionKey"] = SessionUser.sessionKey;
			mapData["SessionUserName"] = SessionUser.sessionUserName;
			//send request
			request.send(JSON.encode(mapData));
		}
		catch(e)
		{
			print(e);
			Util.stopLoader();
			response.error({"message":"Can't connect to server. Pls, try again!"});
		}
	}
	
}