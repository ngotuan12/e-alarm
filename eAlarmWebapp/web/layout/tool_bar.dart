import 'package:polymer/polymer.dart';
import 'dart:html';
import '../src/util.dart';
@CustomTag('tool-bar')
class ToolBar extends PolymerElement
{
	List<Element> listToogle = new List();
	DivElement toolbar;
	SpanElement spanToogleToolbar;
	ToolBar.created() : super.created(); 
	// This lets the Bootstrap CSS "bleed through" into the Shadow DOM
  	// of this element.
	bool get applyAuthorStyles => true;
	@observable String userName = SessionUser.sessionUserName;
	@observable String email = Util.nvl(SessionUser.sessionUserInfor["email"],"");
	/*
	 * @author TuanNA
	 * @since:03/01/2014
	 * @company: ex-artisan
	 * @version :1.0
	 */
	enteredView() 
	{
		//toolbar
		toolbar = this.shadowRoot.querySelector("#tool-bar");
		//language
		LIElement liLanguage = toolbar.querySelector("#lang_nav");
		liLanguage.onClick.listen(onToogleNav);
		//help
		LIElement liHelp = toolbar.querySelector("#help_nav");
		liHelp.onClick.listen(onToogleNav);
		//acount
		LIElement liAccount = toolbar.querySelector("#account_nav");
		liAccount.onClick.listen(onToogleNav);
		
		listToogle.add(liLanguage);
		listToogle.add(liHelp);
		listToogle.add(liAccount);
		//toogle toolbar
		spanToogleToolbar =  toolbar.querySelector(".toggle-navbar");
		spanToogleToolbar.onClick.listen(onToogleToolbar);
	}
	/*
	 * @author TuanNA
	 * @since:30/12/2013
	 * @company: ex-artisan
	 * @version :1.0
	 * @description:show hide navigation
	 */
	void onToogleNav(MouseEvent event)
	{
		for(int i=0;i<listToogle.length;i++)
		{
			if(event.currentTarget!=listToogle[i])
				listToogle[i].classes.remove("open");
			else
			{
				if(listToogle[i].classes.contains("open"))
					listToogle[i].classes.remove("open");
				else
					listToogle[i].classes.add("open");
			}
		}
	}
	/*
	 * @author TuanNA
	 * @since:30/12/2013
	 * @company: ex-artisan
	 * @version :1.0
	 * @description:show hide toolbar
	 */
	void onToogleToolbar(MouseEvent event)
	{
		if(toolbar.classes.contains("navbar-hidden"))
		{
			toolbar.classes.remove("navbar-hidden");
		}
		else
		{
			toolbar.classes.add("navbar-hidden");
		}
	}
	
	/*
	 * @author TuanNA
	 * @since:30/12/2013
	 * @company: ex-artisan
	 * @version :1.0
	 * @description:show hide menu
	 */
	void onToogleMenu(Event event)
	{
		ButtonElement target = event.currentTarget;
		if(parent.classes.contains("menu-hidden"))
		{
			parent.classes.remove("menu-hidden");
			target.style.left = "228px";
			target.style.borderLeft = "1px solid #272727";
		}
		else
		{
			parent.classes.add("menu-hidden");
			target.style.left = "0px";
			target.style.borderLeft = "none";
		}
	}
}