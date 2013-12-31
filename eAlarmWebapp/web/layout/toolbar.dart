library eAlarm;
import 'package:polymer/polymer.dart';
@CustomTag('tool-bar')
class ToolBar extends PolymerElement
{
	ToolBar.created() : super.created() 
	{
		print("create");
	}
	// This lets the Bootstrap CSS "bleed through" into the Shadow DOM
  	// of this element.
	bool get applyAuthorStyles => true;

}