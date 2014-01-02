
import 'package:polymer/polymer.dart';
import 'dart:html';
import 'dart:convert';
import '../src/util.dart';
/*
 * @author TuanNA
 * @since:30/12/2013
 * @company: ex-artisan
 * @version :1.0
 */
@CustomTag('main-application')
class MainApplication extends PolymerElement
{
	@observable String currentAction = "";
	bool showMenu = true;
	bool showLanguage = false;
	DivElement mainContent;
	DivElement toolbar;
	DivElement menu;
	UListElement ulModules;
	List<Element> listToogle = new List();
	List<Map> listModule = [
	{"module_name":"Quản trị người dùng","module_icon":"cogwheels","module_type":"G","children":[{"module_name":"Người dùng","module_type":"M"},{"module_name":"Phòng ban","module_type":"M"}]},
	{"module_name":"Quản trị người dùng","module_icon":"stats","module_type":"G","children":[{"module_name":"Người dùng","module_type":"M"},{"module_name":"Phòng ban","module_type":"M"}]}
	];
	/*
	 * @author TuanNA
	 * @since:30/12/2013
	 * @company: ex-artisan
	 * @version :1.0
	 * @description:
	 */
	MainApplication.created() : super.created() 
	{
		mainContent = this.shadowRoot.querySelector("#main-content");
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
		//menu
		menu = this.shadowRoot.querySelector("#menu");
		//module
		ulModules = menu.querySelector("#module-content");
		//get menu data
		Map request = new Map();
	    request["Method"] = "queryMenuData";
	    //Listen
	    Responder responder = new Responder();
	    //success
	    responder.onSuccess.listen((Map response)
	    {
			listModule = response["menu_data"];
			//home
			loadModuleHome();
			//load menu
			loadMenu(ulModules,listModule);
	    });
	    //error
	    responder.onError.listen((String strError)
	    {
	      print(strError);
	    });
	    //send to server
	    AppClient.sendMessage(request, AlarmServiceName.PermissionService, AlarmServiceMethod.POST,responder);
			
	}
	
	// This lets the Bootstrap CSS "bleed through" into the Shadow DOM
  	// of this element.
	bool get applyAuthorStyles => true;
	/*
	 * @author TuanNA
	 * @since:30/12/2013
	 * @company: ex-artisan
	 * @version :1.0
	 * @description:show hide menu
	 */
	void onToogleMenu()
	{
		if(!showMenu)
		{
			mainContent.classes.add("menu-hidden");
		}
		else
		{
			mainContent.classes.remove("menu-hidden");
		}
		showMenu = !showMenu;
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
	void loadModuleHome()
	{
		LIElement module = new LIElement();
		//style
		module.classes.add("glyphicons");
		module.classes.add("google_maps");
		module.classes.add("active");
		module.appendHtml("<a href=\"#\"> <i></i><span>Bản đồ</span> </a>");
		//attr
		module.attributes["name"] = "FORM_MAIN";
		//listen
		module.onClick.listen(onModuleItemClick);
		//add
		ulModules.children.add(module);
		//
		currentAction = "FORM_MAIN";
	}
	
	UListElement createModuleGroup(Element cmpParent,String strGroupName,String strModuleIcon,int numChidren )
	{
		LIElement group = new LIElement();
		//style
		group.classes.add("hasSubmenu");
		group.classes.add("glyphicons");
		group.classes.add(strModuleIcon);
		//link
		AnchorElement link = new AnchorElement();
		link.href = "#";
		link.attributes["data-toggle"] = "collapse";
		link.appendHtml("<i></i><span>"+strGroupName+"</span>");
		group.children.add(link);
//		group.appendHtml("<a data-toggle=\"collapse\" href=\"#submenu-1\"><i></i><span>"+strGroupName+"</span></a>");
		//module group
		UListElement ulGroup = new UListElement();
		ulGroup.classes.add("collapse");
		ulGroup.attributes["name"] = strGroupName;
		group.children.add(ulGroup);
		//count
		SpanElement span = new SpanElement();
		span.classes.add("count");
		span.appendHtml(numChidren.toString());
		group.children.add(span);
		//add to parent
		cmpParent.children.add(group);
		//event
		link.onClick.listen(onModuleGroupClick);
		return ulGroup;
	}
	
	LIElement createModuleItem(String strModuleName,String strModuleAction)
	{
		LIElement mnu = new LIElement();
		mnu.attributes["name"] = strModuleAction;
		mnu.appendHtml("<a href=\"#\"><span>"+strModuleName+"</span></a");
		mnu.onClick.listen(onModuleItemClick);
		return mnu;
	}
	
	void onModuleItemClick(MouseEvent event)
	{
		Element target = event.currentTarget;
		print(target.attributes["name"]);
		currentAction = target.attributes["name"];
	}
	void toogleForm()
	{
		
	}
	void onModuleGroupClick(MouseEvent event)
	{
		Element target = event.currentTarget;
		target = target.parent;
		if(target.classes.contains("active"))
		{
			target.classes.remove("active");
			target.children[0].classes.remove("collapsed");
			target.children[1].classes.remove("in");
		}
		else
		{
			target.classes.add("active");
			target.children[0].classes.add("collapsed");
			target.children[1].classes.add("in");
		}
	}
	void loadMenu(Element cmpParent,List<Map> listMenu)
	{
		for (int iIndex = 0; iIndex < listMenu.length; iIndex++)
		{
			Map mapInfo = listMenu[iIndex];
			String strMenuName = mapInfo["module_name"];
			String strMenuType = mapInfo["module_type"];
			String strModuleIcon = mapInfo["module_icon"];
			String strModuleAction = mapInfo["module_action"];
			String strDescription = mapInfo["description"];
			Element cmp = findMenu(cmpParent, strMenuName);
			if (strMenuType == "G")
			{
				if (cmp != null && cmp is UListElement)
				{
					loadMenu(cmp, mapInfo["children"]);
				}
				else
				{
					UListElement mnu = createModuleGroup(cmpParent,strDescription,strModuleIcon,mapInfo["children"].length);
					loadMenu(mnu, mapInfo["children"]);
				}
			}
			else if (strMenuType == "M")
			{
				if (cmpParent is UListElement)
				{
					LIElement mnu = null;
					if (cmp != null && cmp is LIElement)
					{
						mnu =  cmp;
					}
					else
					{
						mnu = createModuleItem(strDescription,strModuleAction);
						cmpParent.children.add(mnu);
					}
				}
			}
		}
	}
	
	Element findMenu(Element cmpParent, String strMenuName)
	{
		if (cmpParent is UListElement)
		{
			UListElement mnuParent = cmpParent;
			for (int iMenuIndex = 0; iMenuIndex < mnuParent.children.length; iMenuIndex++)
			{
				Element cmp = mnuParent.children[iMenuIndex];
				if (strMenuName == cmp.getAttribute("name"))
				{
					return cmp;
				}
			}
		}
		else
		{
			for (int iMenuIndex = 0; iMenuIndex < cmpParent.children.length; iMenuIndex++)
			{
				Element cmp = cmpParent.children[iMenuIndex];
				if (strMenuName == cmp.getAttribute("name"))
				{
					return cmp;
				}
			}
		}
	}
}