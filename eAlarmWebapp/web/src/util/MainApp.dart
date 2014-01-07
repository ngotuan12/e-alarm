part of EASUtil;
abstract class MainApp extends PolymerElement
{
	MainApp.created() : super.created(); 
	@observable bool showCircleLoader;
	@observable List<Map> listNotify;
	@observable bool isLogin;
}