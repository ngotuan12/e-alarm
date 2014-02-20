
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
@CustomTag('main-menu')
class MainMenu extends PolymerElement
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
	SpanElement profile;
	List<Map> listModule;
	Element btnEditUser;
	/*
	 * @author TuanNA
	 * @since:30/12/2013
	 * @company: ex-artisan
	 * @version :1.0
	 * @description:
	 */
	MainMenu.created() : super.created(); 
	enteredView() 
	{
		super.enteredView();
		//
		mainContent = this.shadowRoot.querySelector("#main-content");
		//menu
		menu = this.shadowRoot.querySelector("#menu");
		//profile
		profile = menu.querySelector("#profile");
		//module
		ulModules = menu.querySelector("#module-content");
		//user infor
		
		initUserInfor();
		
		btnEditUser = this.shadowRoot.querySelector("#btnEditUser");
    btnEditUser.onClick.listen((event)=>loadEditUser());
		//main menu
		initMainMenu();
		
	}
	
	void loadEditUser()
	{  
	  currentAction = "FORM_EDIT_ACOUNT";
	}
	
	void initMainMenu()
	{
		
		//get menu data
		Map request = new Map();
	    request["Method"] = "loadSystemData";
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
	    responder.onError.listen((Map error)
	    {
			Util.showNotifyError(error["message"]);
	    });
	    //send to server
	    AppClient.sendMessage(request, AlarmServiceName.PermissionService, AlarmServiceMethod.POST,responder);
	}
	void initUserInfor()
	{
		AnchorElement userName = menu.querySelector("#lblUserName");
		userName.text = SessionUser.sessionUserInfor["fullname"];
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