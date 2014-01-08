part of EASUtil;

//class Responder extends EventSource
//{
//	factory Responder(String title){
//	
//	}
//	static const EventStreamProvider<CustomEvent> readyResponse = const EventStreamProvider<CustomEvent>('onresponseok');
//	Stream<Event> get onResponse => readyResponse.forTarget(this);
//	
//	static const EventStreamProvider<CustomEvent> requestError = const EventStreamProvider<CustomEvent>('onresponseerror');
//	Stream<Event> get onError => requestError.forTarget(this);
//}
class Responder 
{
	StreamController _errorController = new StreamController();
	StreamController _successController = new StreamController();
	Stream _onError, _success;

	Responder() 
	{
		_onError = _errorController.stream.asBroadcastStream();
		_success = _successController.stream.asBroadcastStream();
	}

	Stream<Map> get onError => _onError;
	Stream<Map> get onSuccess => _success;
	
	void error(Map response) => _errorController.add(response);
	void success(Map response) => _successController.add(response);
}