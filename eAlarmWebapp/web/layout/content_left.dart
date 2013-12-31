library eAlarm;
import 'package:polymer/polymer.dart';
@CustomTag('menu-left')
class MenuLeft extends PolymerElement
{
	MenuLeft.created() : super.created() 
	{
		print("create");
	}
	// This lets the Bootstrap CSS "bleed through" into the Shadow DOM
  	// of this element.
	bool get applyAuthorStyles => true;

}