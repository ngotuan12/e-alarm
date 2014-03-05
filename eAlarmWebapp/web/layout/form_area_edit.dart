import 'package:polymer/polymer.dart';
import 'dart:html';
import 'dart:convert';
import '../src/util.dart';
import 'package:google_maps/google_maps.dart';
@CustomTag('form-area-edit')
class FormAreaEdit extends PolymerElement
{
  ButtonElement btnCancel;
  ButtonElement btnExit;
  ButtonElement btnSave;
  TextInputElement txtAddress;
  TextInputElement txtFullAddress;
  String Lat="";
  String Lng="";
  TextInputElement txtCode;
  //SelectElement selArea;
  SelectElement selStatus;
  //SelectElement selType;
  List<Map> listAreas;
  bool error=false;
  String errorString="";
  List<MapTypeStyle> styles = 
        [
             {
              "featureType": "poi",
              //"elementType": "bus",
              "stylers": [
                { "visibility": "off" }
              ]
            },
          {
              "featureType": "transit",
              //"elementType": "bus",
              "stylers": [
              { "visibility": "off" }
              ]
            } 
    ];
  // of this element.
  @published Map area;
  bool get applyAuthorStyles => true;
  FormAreaEdit.created() : super.created();
  enteredView() 
  {
    super.enteredView();
    //selArea=this.shadowRoot.querySelector("#area");
    selStatus=this.shadowRoot.querySelector("#status");
    //selType=this.shadowRoot.querySelector("#type");
    btnCancel=this.shadowRoot.querySelector("#btnCancel");
    btnExit=this.shadowRoot.querySelector("#btnExit");
    btnSave=this.shadowRoot.querySelector("#btnSave");
    txtFullAddress=this.shadowRoot.querySelector("#fullAddress");
    txtAddress=this.shadowRoot.querySelector("#address");
    txtCode=this.shadowRoot.querySelector("#code");
    //event
    btnExit.onClick.listen(onExit);
    btnCancel.onClick.listen(onExit);
    btnSave.onClick.listen(onSave);
    //selArea.onChange.listen(onChangeFullName);
    txtAddress.onInput.listen(onChangeFullName);
    txtAddress.onChange.listen(onShowMap);
    //Load
    init();
  }
  /*
   * @author:diennd
   * @since :14/2/2014
   * @version:"1.0
   */
  void checkData()
  {
    error=false;
    errorString="";
    //check lat lng
    if(Lat==""||Lng==""||Lat==null||Lng==null)
    {
      error=true;
      errorString+="Couldn't find location"+", ";
    }
    if(txtCode.value.trim() =="")
    {
      error=true;
      errorString+="Code can't empty"+", ";
      txtCode.focus();
    }
    if(listAreas !=null)
    {
      for(int i=0;i<listAreas.length;i++)
      {
        Map areaCurrent=listAreas[i];
        if(areaCurrent['code'].toString().toUpperCase()==txtCode.value.trim().toUpperCase() && areaCurrent['id'].toString().toUpperCase()!=area["id"].toString().toUpperCase())
        {
          error=true;
          errorString+="Code is already exists";
        }
      }
    }
  }
  /*
   * @author: diennd
   * @since: 11/2/2014
   * @version: 1.0
   */
  void onSave(Event e)
  {
    checkData();
    if(!error)
    {
//      //window.alert(selStatus.selectedIndex.toString()+"-"+txtAddress.value+"-"+txtCode.value+"-"+txtFullAddress.value+"-"+txtLat.value+"-"+txtLng.value+"-");
      Responder responder = new Responder();
      Map request = new Map();
      request["Method"] = "UpdateArea";
      request["ID"]=area["id"];
      request["FullName"]=txtFullAddress.value.trim();
      request["Code"]=txtCode.value.trim().toUpperCase();
      request["Name"]=txtAddress.value.trim();
      //option
        //OptionElement op =selArea.children.elementAt(selArea.selectedIndex);
        //Map element = JSON.decode(op.value);
      request["ParentID"]='1';
      request["Status"]=(selStatus.selectedIndex==0)?"1":"0";       
      request["Lang"]=double.parse(Lng);
      request["Lat"]=double.parse(Lat);
      request["Type"]='2';
      responder.onSuccess.listen((Map response)
      {
        Util.showNotifySuccess("Edit record sucess");
        dispatchEvent(new CustomEvent("goback", detail:""));
        
      });
      //error
      responder.onError.listen((Map error)
      {
        Util.showNotifyError(error["message"]);
      });
      AppClient.sendMessage(request, AlarmServiceName.AreaService, AlarmServiceMethod.POST,responder);
    }
    else
    {
      Util.showNotifyError(errorString);
    }
  }
  /*
   * @author: diennd
   * @since: 11/2/2014
   * @version: 1.0
   */
  //Google map show
  void init()
  {
   //Load data
    txtAddress.value=area['name'];
    Lat=area['lat'].toString();
    Lng=area['lng'].toString();;
    txtFullAddress.value=area['full_name'];
    txtCode.value=area['code'];
    //declare option
    OptionElement opGoodStatus=new OptionElement(data: "Kích hoạt", value:"1" , selected:true);
    OptionElement opErorrStatus=new OptionElement(data: "Không kích hoạt", value:"0" , selected:false);
    //check status
    if(area['status']=="0")
      opErorrStatus.selected=true;
    //add option in select tag
    selStatus.children.add(opGoodStatus);
    selStatus.children.add(opErorrStatus);
    //check type
//    for(int i=0;i<5;i++)
//    {
//      OptionElement op=new OptionElement();
//      op.value=(i+1).toString();
//      op.text=(i+1).toString();
//      op.selected=(area["type"].toString()==(i+1).toString())?true:false;
//      selType.children.add(op);
//    }
  //Google map
    codeAddress();
  //areas
    ShowAreas();
  }
  /*
   * @author:diennd
   * @since:17/2/2014
   * @version: 1.0
   */
  void codeAddress() 
  {
    if(area !=null)
    {
      Geocoder geocoder = new Geocoder();
      final request = new GeocoderRequest()
      ..address =area['full_name'];
          final mapOptions = new MapOptions()
                ..keyboardShortcuts=true
                ..zoom = 14
                ..center = new LatLng(area['lat'],area['lng'])
                ..mapTypeId = MapTypeId.ROADMAP
                ..styles = styles;
          final map = new GMap(this.shadowRoot.querySelector("#show-map"), mapOptions);
          //marker
          Marker marker = new Marker
          (
            new MarkerOptions()
                ..position = new LatLng(area['lat'],area['lng'])
                ..map = map
                ..title = area['name']
          );          
    }
  }
  /*
   * @author:diennd
   * @since:17/2/2014
   * @version:1.0
   */
  //Show all areas
  void ShowAreas()
  {
    //refresh data
    //selArea.children.clear();
    //contructor option
//    OptionElement op=new OptionElement();
//    op.value="-1";
//    op.text="Chọn địa bàn";
//    selArea.children.add(op);
    //get data
    Map request = new Map();
    request["Method"] = "GetAllAreaActive";
    //Listen
    Responder responder = new Responder();
    //success
    responder.onSuccess.listen((Map response)
    {
      listAreas=response['ListAreaActive'];
      if(listAreas!=null)
      {
        //filter by type
        List<Map> areaProvinces=filterByLevel(listAreas, ["1"]);
        //add into tag select
        if(areaProvinces !=null)
        {
          for(int i=0;i<areaProvinces.length;i++)
          {
            //option
            Map element= areaProvinces[i];
            String name=element['name'];
            OptionElement op = new Element.option();
            op.text='==>'+name;
            op.value = JSON.encode(element);
            if(area['parent_id']==element['id'])
              op.selected=true;
            //add option
//            selArea.children.add(op);
            //filter by ParentID provice
            List<Map> areaProvices=filterByParentID(listAreas, [element['id']]);
            if(areaProvices !=null)
            {
              for(int i=0;i<areaProvices.length;i++)
              {
                //option
                Map element= areaProvices[i];
                String name=element['name'];
                OptionElement op = new Element.option();
                op.text="==> ==>"+name;
                op.value = JSON.encode(element);
                if(area['ParentID']==element['id'])
                  op.selected=true;                
                //add are
                //selArea.children.add(op);
                //filter by ParentID district
                List<Map> areaDistricts=filterByParentID(listAreas, [element['id']]);
                if(areaDistricts !=null)
                {
                  for(int i=0;i<areaDistricts.length;i++)
                  {
                    //option
                    Map element= areaDistricts[i];
                    String name=element['name'];
                    OptionElement op = new Element.option();
                    op.text="==> ==> ==>"+name;
                    op.value = JSON.encode(element);
                    if(area['ParentID']==element['id'])
                      op.selected=true;                    
                    //add are
                   // selArea.children.add(op);
                   }
                 }
               }
             }
           }
          }
        }
      });
      //error
      responder.onError.listen((Map error)
       {
         Util.showNotifyError(error["message"]);
       }
      );
      //send to server
      AppClient.sendMessage(request, AlarmServiceName.AreaService, AlarmServiceMethod.POST,responder);
  }
  /*
   * @author:diennd
   * @since:17/2/2014
   * @version:1.0
   */
  void onChangeFullName(Event e)
    {
//      Lat="";
//      Lng="";
//      OptionElement opt = selArea.children.elementAt(selArea.selectedIndex);
//          Map area = JSON.decode(opt.value);
//          if(area !=null)
            txtFullAddress.value="Việt Nam";
      if(txtAddress.value=="")
      {
        txtFullAddress.value="";
      }
      if(e.target==txtAddress)
      {
        String temp=txtFullAddress.value;
        txtFullAddress.value="";
        txtFullAddress.value=txtAddress.value+','+temp;
      }
    }
  /*
   * @author:diennd
   * @since: 17/2
   * @version :1.0
   */
  void onExit(Event e)
  {
    dispatchEvent(new CustomEvent("goback", detail:""));
  }
  /*
   * @author:diennd
   * @since:17/2/2014
   * @version:1.0
   */
  void onShowMap(Event e)
    {
      if(txtFullAddress.value.trim() !="")
      {
        Geocoder geocoder = new Geocoder();
        final request = new GeocoderRequest()
        ..address =txtFullAddress.value.trim();
        geocoder.geocode(request, (List<GeocoderResult> results, GeocoderStatus status) {
        if (status == GeocoderStatus.OK)
        {
          //print("Lat: "+results[0].geometry.location.lat.toString()+"Lng: "+results[0].geometry.location.lng.toString());
          final mapOptions = new MapOptions()
                ..keyboardShortcuts=true
                ..zoom = 14
                ..center = new LatLng(results[0].geometry.location.lat, results[0].geometry.location.lng)
                ..mapTypeId = MapTypeId.ROADMAP
                ..styles = styles;
          final map = new GMap(this.shadowRoot.querySelector("#show-map"), mapOptions);
          //marker
          Marker marker = new Marker
          (
            new MarkerOptions()
            ..position = new LatLng(results[0].geometry.location.lat, results[0].geometry.location.lng)
            ..map = map
            ..title = 'Viet Nam'
           ); 
          //get lat,lng
          Lat=results[0].geometry.location.lat.toString();
          Lng=results[0].geometry.location.lng.toString();
         } 
         else
         {
            window.alert('Geocode was not successful for the following reason: ${status}');
         }
         });
      }
      else
      {
        window.alert('Không tìm thấy !!!');
      }
    }
  /*
   * @author:diennd
   * @since:17/2/2014
   * @version:1.0
   */
   filterByParentID(List<Map> locations,List<String> listType)
   {
     return locations.where((location)
     {
       for(int i=0;i<listType.length;i++)
       {
         if(listType[i]==location["parent_id"])
         {
           return true;
         }
       }
       return false;
     }).toList();
   }
   /*
    * @author:diennd
    * @since:17/2/2014
    * @version:1.0
    */
   List<Map> filterByLevel(List<Map> locations,List<String> listLevel)
   {
     return locations.where((location)
     {
       for(int i=0;i<listLevel.length;i++)
       {
         if(listLevel[i]==location["level"])
         {
           return true;
         }
       }
       return false;
     }).toList();
   }  
 }