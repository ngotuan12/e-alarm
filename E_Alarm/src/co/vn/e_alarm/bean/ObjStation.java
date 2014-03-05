package co.vn.e_alarm.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class ObjStation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public  int id;
	public String code;
	public int area_id;
	public String area_code;
	public String address;
	public double lat;
	public double lng;
	public int status;
	public ArrayList<ObjProperties> listPropertiesStation;
	public String macAddress;
	public String serverConnect;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getArea_id() {
		return area_id;
	}
	public void setArea_id(int area_id) {
		this.area_id = area_id;
	}
	public String getArea_code() {
		return area_code;
	}
	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	public ArrayList<ObjProperties> getListPropertiesStation() {
		return listPropertiesStation;
	}
	public void setListPropertiesStation(ArrayList<ObjProperties> listPropertiesStation) {
		this.listPropertiesStation = listPropertiesStation;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public String getServerConnect() {
		return serverConnect;
	}
	public void setServerConnect(String serverConnect) {
		this.serverConnect = serverConnect;
	}
	
	
	
	
}
