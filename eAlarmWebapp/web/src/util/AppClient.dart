part of EASUtil;

class AppClient
{
	static final String url = "http://54.243.244.187:7777/";
//	static final String url = "http://localhost:7777/AlarmServer/";
	static HttpRequest request = new HttpRequest();
	static bool isLogin = false;
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
							response.error(mapResponse["message"]);
							return;
						}
						response.success(mapResponse);
					}
					else
					{
						response.error("Can't connect to server. Pls, try again!");
					}
				}
			});
			//Prepare 
			request.open(strMethod, strURL, async: true);
			//Session
			mapData["SessionID"] = SessionUser.sessionID;
			mapData["SessionUserName"] = SessionUser.sessionUserName;
			//send request
			request.send(JSON.encode(mapData));
		}
		catch(e)
		{
			print(e);
		}
	}
	
}