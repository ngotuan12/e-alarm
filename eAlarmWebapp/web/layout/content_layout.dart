library eAlarm;
import 'package:polymer/polymer.dart';
import 'dart:html';
@CustomTag('content-layout')
class ContentLayout extends PolymerElement
{
	ContentLayout.created() : super.created() 
	{
	}
	
	// This lets the Bootstrap CSS "bleed through" into the Shadow DOM
  	// of this element.
	bool get applyAuthorStyles => true;

}