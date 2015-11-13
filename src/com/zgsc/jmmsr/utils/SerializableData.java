package com.zgsc.jmmsr.utils;

import java.util.List;

import com.zgsc.jmmsr.domain.UserMap;

public class SerializableData implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private List<UserMap> zainaList;// 在哪数据集合
	private int myLon, myLat; // 自己的经纬度

	public List<UserMap> getZainaList() {
		return zainaList;
	}

	public void setZainaList(List<UserMap> zainaList) {
		this.zainaList = zainaList;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
