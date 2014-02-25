import 'package:polymer/polymer.dart';
import 'package:google_maps/google_maps.dart';
import '../src/util.dart';
import 'dart:convert';
import 'dart:html';
@CustomTag('form-device-add')
class FormDeviceAdd extends PolymerElement
{
  
  ButtonElement btnCancel;
  ButtonElement btnSearch;
  ButtonElement btnSave;
  TextInputElement txtAddress;
  TextInputElement txtFullAddress;
  TextInputElement txtLat;
  TextInputElement txtLng;
  TextInputElement txtCode;
  TextInputElement txtName;
  SelectElement selArea;
  SelectElement selStatus;
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
	bool get applyAuthorStyles => true;
	FormDeviceAdd.created() : super.created();
	enteredView() 
	{
		super.enteredView();
		selArea=this.shadowRoot.querySelector("#area");
		selStatus=this.shadowRoot.querySelector("#status");
		btnCancel=this.shadowRoot.querySelector("#btnCancel");
		btnSave=this.shadowRoot.querySelector("#btnSave");
		btnSearch=this.shadowRoot.querySelector("#btnSearch");
		txtFullAddress=this.shadowRoot.querySelector("#fullAddress");
		txtAddress=this.shadowRoot.querySelector("#address");
		txtLat=this.shadowRoot.querySelector("#Lat");
		txtLng=this.shadowRoot.querySelector("#Lng");
		txtCode=this.shadowRoot.querySelector("#code");
		txtName =this.shadowRoot.querySelector("#name");
		//event
		btnCancel.onClick.listen(onExit);
		btnSearch.onClick.listen(onShowMap);
		btnSave.onClick.listen(onSave);
		selArea.onChange.listen(onChangeFullName);
		txtAddress.onInput.listen(onChangeFullName);
		//Load
		init();
	}
	
	/*
	 * @author:diennd
	 */
  void onExit(Event e)
	{
	  dispatchEvent(new CustomEvent("goback",detail:""));
	}
  /*
   * @author: diennd
   * @since: 11/2/2014
   * @version: 1.0
   */
  void onSave(Event e)
  {
    if(!isValidate())
    {
    	return;
    }
    if(!error)
    {
      window.alert(selStatus.selectedIndex.toString()+"-"+txtAddress.value+"-"+txtCode.value+"-"+txtFullAddress.value+"-"+txtLat.value+"-"+txtLng.value+"-");
      Responder responder = new Responder();
      Map request = new Map();
      request["Method"] = "onCreateNewDevices";
      request["code"]="DSPM111";
      request["area_id"]="13";
      request["area_code"]="HN";
      request["address"]="881 Đại Cồ Việt, Quận Hai Bà Trưng, Hà Nội";       
      request["lat"]="1312";
      request["lng"]="234";
      request["status"]="1";
      responder.onSuccess.listen((Map response)
      {
        window.alert('suscess');
      });
      //error
      responder.onError.listen((Map error)
      {
        Util.showNotifyError(error["message"]);
      });
      AppClient.sendMessage(request, AlarmServiceName.DeviceService, AlarmServiceMethod.POST,responder);
    }
    else
    {
      Util.showNotifyError(errorString);
    }
  }
  /*
   * @author:diennd
   * @since :14/2/2014
   * @version:"1.0
   */
  bool isValidate()
  {
	  if(txtCode.value.trim() =="")
      {
      	Util.showNotifyError("Trường mã phòng máy không được để trống");
        txtCode.focus();
        return false;
      }
	  if(txtName.value.trim() =="")
	   {
	   	Util.showNotifyError("Trường tên phòng máy không được để trống");
	   	txtName.focus();
	     return false;
	   }
	  if(selArea.selectedIndex==0)
	  {
		  Util.showNotifyError("Chưa chọn địa bàn");
		  selArea.focus();
          return false;  
	  }
    //check lat lng
    if(txtLat.value.trim()==""||txtLng.value.trim()=="")
    {
    	Util.showNotifyError("Không tìm thấy địa chỉ");
    	txtAddress.focus();
      return false;
    }
    
  }
  /*
   * @author: diennd
   * @since: 11/2/2014
   * @version: 1.0
   */
  void onChangeFullName(Event e)
  {
    OptionElement opt = selArea.children.elementAt(selArea.selectedIndex);
    if(selArea.selectedIndex>0)
    {
      //data
        Map area = JSON.decode(opt.value);
        if(area !=null)
          txtFullAddress.value=area["FullName"];
    }
    if(e.target==selArea)
    {
      txtAddress.value="";
      txtAddress.disabled=(selArea.selectedIndex>0)?false:true;
    }
    if(e.target==txtAddress)
    {
      btnSearch.disabled=(txtAddress.value.trim() !="")?false:true;
      String temp=txtFullAddress.value;
      txtFullAddress.value="";
      txtFullAddress.value=txtAddress.value+','+temp;
    }
  }
  /*
   * @author: diennd
   * @since: 11/2/2014
   * @version: 1.0
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
        txtLat.value=results[0].geometry.location.lat.toString();
        txtLng.value=results[0].geometry.location.lng.toString();
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
   * @author: diennd
   * @since: 11/2/2014
   * @version: 1.0
   */
    //Google map show
    void init()
    {
        //Google map
        codeAddress();
        //areas
        ShowAreas();
    }
    void codeAddress() {
        Geocoder geocoder = new Geocoder();
        final request = new GeocoderRequest()
              ..address = 'ha noi,viet nam';
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
        } 
        else
        {
            window.alert('Geocode was not successful for the following reason: ${status}');
        }
        });
      }
    /*
         * @author ducdienpt
         * @Since 18/12/2013
         * @Version 1.0
         */
        //Show all areas
        void ShowAreas()
        {
          //refresh data
            selArea.children.clear();
            //contructor option
            OptionElement op=new OptionElement();
            op.value="-1";
            op.text="Chọn địa bàn";
            selArea.children.add(op);
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
                    List<Map> areaProvinces=filterByType(listAreas, ["2"]);
                    //add into tag select
                    if(areaProvinces !=null)
                    {
                      for(int i=0;i<areaProvinces.length;i++)
                      {
                      //option
                        Map element= areaProvinces[i];
                        String name=element['Name'];
                        OptionElement op = new Element.option();
                        op.text='==>'+name;
                        op.value = JSON.encode(element);
                        //add option
                        //selArea.children.add(op);
                        //filter by ParentID
                        List<Map> areaDistricts=filterByParentID(listAreas, [element['ID']]);
                        if(areaDistricts !=null)
                        {
                          for(int i=0;i<areaDistricts.length;i++)
                          {
                            //option
                            Map element= areaDistricts[i];
                            String name=element['Name'];
                            OptionElement op = new Element.option();
                            op.text=name;
                            op.value = JSON.encode(element);
                            //add are
                            selArea.children.add(op);
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
               * @ducdienpt
               * @since 18/12/2013
               * @company: ex-artisan
               * @version :1.0
               */
              filterByParentID(List<Map> locations,List<String> listType)
              {
                return locations.where((location){
                  for(int i=0;i<listType.length;i++)
                  {
                    if(listType[i]==location["ParentID"])
                    {
                      return true;
                    }
                  }
                  return false;
                }).toList();
              }
              /*
                 * @ducdienpt
                 * @since 13/12/2013
                 * @company: ex-artisan
                 * @Version: 1.0
                 */
                List<Map> filterByType(List<Map> locations,List<String> listType)
                {
                  return locations.where((location){
                    for(int i=0;i<listType.length;i++)
                    {
                      if(listType[i]==location["Type"])
                      {
                        return true;
                      }
                    }
                    return false;
                  }).toList();
                }
}
