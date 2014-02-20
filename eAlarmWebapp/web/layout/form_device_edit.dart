import 'package:polymer/polymer.dart';
import 'dart:html';
import 'package:google_maps/google_maps.dart';
import 'dart:convert';
import '../src/util.dart';
@CustomTag('form-device-edit')
class FormDeviceEdit extends PolymerElement
{
  InputElement txtAddress;
  InputElement txtLat;
  InputElement txtLng;
  InputElement txtCode;
  InputElement txtFullAddress;
  SelectElement selStatus;
  ButtonElement btnSearch;
  ButtonElement btnSave;
  SelectElement selArea;
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
	@published Map device;
	FormDeviceEdit.created() : super.created();
	ButtonElement btnExit=new ButtonElement();
	ButtonElement btnCancel=new ButtonElement();
	enteredView() 
	{
		super.enteredView();
		selArea=this.shadowRoot.querySelector("#area");
		btnExit=this.shadowRoot.querySelector("#btnexit");
		btnCancel=this.shadowRoot.querySelector("#btncancel");
		btnSave=this.shadowRoot.querySelector("#btnSave");
		btnExit.onClick.listen(onExit);
		btnCancel.onClick.listen(onExit);
		txtAddress=this.shadowRoot.querySelector("#address");
		txtLat=this.shadowRoot.querySelector("#lat");
		txtLng=this.shadowRoot.querySelector("#lng");
		txtFullAddress=this.shadowRoot.querySelector("#fullAddress");
		txtCode=this.shadowRoot.querySelector("#code");
		selStatus=this.shadowRoot.querySelector("#status");
		btnSearch=this.shadowRoot.querySelector("#search");
		//Event on-click
		btnSearch.onClick.listen(onShowMap);
		selArea.onChange.listen(onChangeFullName);
		txtAddress.onInput.listen(onChangeFullName);
		btnSave.onClick.listen(onSave);
		//Load
		init();
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
	void onExit(Event e)
	{
	  dispatchEvent(new CustomEvent("goback",detail:""));
	}
	/*
	 * @author:diennd
	 * @since: 14/2/2014
	 * @version:1.0
	 */
    void checkData()
    {
      error=false;
      errorString="";
      //check lat lng
      if(txtLat.value.trim()==""||txtLng.value.trim()=="")
      {
        error=true;
        errorString+="Lat,lng can't empty"+", ";
      }
      if(txtCode.value.trim() =="")
      {
        error=true;
        errorString+="Code ATM can't empty"+", ";
      }
    }
	/*
   * @author: diennd
   * @since: 11/2/2014
   * @version: 1.0
   */
    void onChangeFullName(Event e)
    {
      
      txtLat.value="";
      txtLng.value="";
      OptionElement opt = selArea.children.elementAt(selArea.selectedIndex);
      if(e.target==selArea)
      {
        txtAddress.value="";
        txtAddress.disabled=(selArea.selectedIndex>0)?false:true;
        if(selArea.selectedIndex>0)
        {
         //data
          Map area = JSON.decode(opt.value);
          if(area !=null)
          txtFullAddress.value=area["FullName"];
        }
      }
      if(e.target==txtAddress)
      {
        String temp="";
        if(selArea.selectedIndex>0)
        {
          Map area = JSON.decode(opt.value);
          temp=area["FullName"];
        }
        txtFullAddress.value="";
        txtFullAddress.value=txtAddress.value+','+temp;
        btnSearch.disabled=(txtAddress.value.trim()!="")?false:true;
      }
    }
	void getLatLngByAddress(Event e)
	{
        Geocoder geocoder = new Geocoder();
        final request = new GeocoderRequest()
          ..address = 'ha noi,viet nam';
        print('ok');
        geocoder.geocode(request, (List<GeocoderResult> results, GeocoderStatus status) {
          if (status == GeocoderStatus.OK) {
            print(results[0].geometry.location.lat);
          } else {
            window.alert('Geocode was not successful for the following reason: ${status}');
          }
        });
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
      //show data
      txtAddress.value=device['address'];
      txtLat.value=device['lat'].toString();
      txtLng.value=device['lng'].toString();;
      txtFullAddress.value=device['address'];
      txtCode.value=device['code'];
      //declare option
      OptionElement opGoodStatus=new OptionElement(data: "Tốt", value:"1" , selected:true);
      OptionElement opErorrStatus=new OptionElement(data: "Hỏng", value:"0" , selected:false);
      //check status
      if(device['status']=="2")
        opErorrStatus.selected=true;
      //add option in select tag
      selStatus.children.add(opGoodStatus);
      selStatus.children.add(opErorrStatus);
      //show data
        ShowAreas();
      //Google map
      final mapOptions = new MapOptions()
      ..keyboardShortcuts=true
      ..zoom = 14
      ..center = new LatLng(device['lat'],device['lng'])
      ..mapTypeId = MapTypeId.ROADMAP
      ..styles = styles;
      final map = new GMap(this.shadowRoot.querySelector("#show-map"), mapOptions);
      //marker
       Marker marker = new Marker
       (
       new MarkerOptions()
       ..position = new LatLng(device['lat'],device['lng'])
       ..map = map
       ..title = device['address']
       );
  }
  /*
     * @author ducdienpt
     * @Since 18/12/2013
     * @Version 1.0
     */
    //Show all areas
    void ShowAreas()
    {
      //get data
        Map request = new Map();
        request["Method"] = "GetAllAreaActive";
        //Listen
        Responder responder = new Responder();
        //success
        responder.onSuccess.listen((Map response)
        {
          listAreas=response['ListAreaActive'];
                //filter by type
                List<Map> areaProvinces=filterByType(listAreas, ["2"]);
                //add into tag select
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
                  for(int i=0;i<areaDistricts.length;i++)
                  {
                    //option
                    Map element= areaDistricts[i];
                    String name=element['Name'];
                    OptionElement op = new Element.option();
                    op.text=name;
                    op.value = JSON.encode(element);
                    //check selected
                    if(element['ID']==device['area_id'])
                      op.selected=true;
                    //add are
                    selArea.children.add(op);
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
        /*
           * @Author ducdienpt
           * @Since 18/12/2013
           * @Version 1.0
           */
          void showAddress(Event e)
          {
          //  querySelector("#ErrorLocation").text="";
            if(selArea.selectedIndex==0)
            {
              txtFullAddress.value="";
              txtAddress.disabled=true;
              btnSearch.disabled=true;
            }
            else
            {
                //refresh text
                txtFullAddress.value="";
                //selected
                OptionElement opt = selArea.children.elementAt(selArea.selectedIndex);
                //data
                Map area = JSON.decode(opt.value);
                txtFullAddress.value=area["FullName"];
                txtAddress.disabled=false;
                if(txtAddress.disabled ==false && txtAddress.value !="")
                {
                  String temp=txtFullAddress.value;
                  temp=txtAddress.value+','+temp;
                  txtFullAddress.value=temp;
                  btnSearch.disabled=false;
                }
            }
          }
}
