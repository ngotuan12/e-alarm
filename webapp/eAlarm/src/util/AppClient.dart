part of EASUtil;

class AppClient
{
	static final String url = "http://54.243.244.187:7777/";
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
	static void sendMessage(String strData,String strServiceName,String strMethod,Responder response)
	{
		try
		{
			String strURL = url + strServiceName;
			request = new HttpRequest();
			//listen
			request.onReadyStateChange.listen(
			(_)
			{
				if(request.readyState == HttpRequest.DONE &&(request.status == 200))
				{
					print(request.status);
					String strResponse = request.responseText;
					Map mapResponse = JSON.decode(strResponse);
					response.success(mapResponse);
				}
			});
			request.onError.listen(
			(_)
			{
				response.error("Can't connect to server");
			});
			//Prepare 
			request.open(strMethod, strURL, async: true);
			//send request
			request.send(strData);
		}
		catch(e)
		{
			print(e);
		}
	}
	
}