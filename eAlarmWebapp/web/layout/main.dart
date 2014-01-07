
import 'package:polymer/polymer.dart';
import 'dart:html';
//import 'dart:convert';
import '../src/util.dart';
/*
 * @author TuanNA
 * @since:30/12/2013
 * @company: ex-artisan
 * @version :1.0
 */
@CustomTag('main-application')
class MainApplication extends MainApp
{
	@observable List<Map> listNotify = toObservable([]);
	@observable String currentAction = "NONE";
	@observable bool showCircleLoader = true;
	@observable bool isLogin = false;
	bool showMenu = true;
	bool showLanguage = false;
	DivElement mainContent;
	DivElement menu;
	UListElement ulModules;
	List<Map> listModule;
	// This lets the Bootstrap CSS "bleed through" into the Shadow DOM
  	// of this element.
	bool get applyAuthorStyles => true;
	/*
	 * @author TuanNA
	 * @since:30/12/2013
	 * @company: ex-artisan
	 * @version :1.0
	 * @description:
	 */
	MainApplication.created() : super.created(); 
	enteredView() 
	{
		super.enteredView();
		
		SessionValue.main = this;
	}
	
}