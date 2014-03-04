part of EASUtil;

class AppClient
{
	static final String url = "http://54.243.244.187:7777/";
//	static final String url = "http://127.0.0.1:7777/AlarmServer/";
	static HttpRequest request = new HttpRequest();
	static final String serverName = "54.243.244.187";
	static final String serverIP = "54.243.244.187";
	static final int serverPort = 7777;
	static final int webSocketPort = 8080;
	static WebSocket websocket;
	static bool forceExitWebsocket = false;
	/**
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
							response.error(mapResponse);
							if(mapResponse["code"]=="EAS-SYS-002")
							{
								SessionValue.main.isLogin = false;
							}
							else if(mapResponse["code"]=="EAS-SYS-004")
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
	/**
	 * @author TuanNA
	 * @since 13/12/2013
	 * @Company Ex-Artisan
	 * @param
	 */
	static void connectWebsocket([int retrySeconds = 2])
	{
		bool reconnectScheduled = false;
		void scheduleReconnect() 
		{
			if (!reconnectScheduled) 
			{
		 		new Timer(new Duration(milliseconds: 1000 * retrySeconds), () => connectWebsocket(retrySeconds * 2));
			}
			reconnectScheduled = true;
 		}
		String url = "ws://"+AppClient.serverName+":"+AppClient.webSocketPort.toString();
		
		websocket = new WebSocket("ws:\\"+AppClient.serverName+":"+AppClient.webSocketPort.toString());
		
		websocket.onOpen.listen((e) {
			//announce to server
			Map request = new Map();
			request["handle"] = "announce";
			request["session_key"] = SessionUser.sessionKey;
			//send announce
			websocket.send(JSON.encode(request));
		});
		
		websocket.onClose.listen((e) {
			print('Websocket closed!');
			if(!forceExitWebsocket)
				scheduleReconnect();
			else
				forceExitWebsocket = false;
		});
		
		websocket.onError.listen((e) {
			print("Error connecting to ws");
			if(!forceExitWebsocket)
				scheduleReconnect();
		 });
		
	}
	/**
	 * @author TuanNA
	 * @since 04/03/2014
	 * @Company Ex-Artisan
	 * @param
	 */
	static void sendWebsocketMessage(Map request)
	{
		websocket.sendString(JSON.encode(request));
	}
	/**
	 * @author TuanNA
	 * @since 03/03/2014
	 * @Company Ex-Artisan
	 * @param
	 */
	static void forceCloseWebsocket()
	{
		forceExitWebsocket = true;
		if(websocket!=null)
		{
			websocket.close();
			websocket == null;
		}
	}
}