package com.zgsc.jmmsr.utils;

import java.util.Map;

public class SerializableMap implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private Map<String, String> geoMap;
	private Map<String, String> nickMap;
	private int myLon, myLat;

	public Map<String, String> getGeoMap() {
		return geoMap;
	}

	public void setGeoMap(Map<String, String> geoMap) {
		this.geoMap = geoMap;
	}

	public Map<String, String> getNickMap() {
		return nickMap;
	}

	public void setNickMap(Map<String, String> nickMap) {
		this.nickMap = nickMap;
	}	
	
	public int getMyLon() {
		return myLon;
	}

	public void setMyLon(int myLon) {
		this.myLon = myLon;
	}

	public int getMyLat() {
		return myLat;
	}

	public void setMyLat(int myLat) {
		this.myLat = myLat;
	}
}