package com.zgsc.jmmsr.domain;

/**
 * 记录用户数据，以在地图上显示出来。
 * @author Wangzhenyue
 * 
 */

public class UserMap implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String nick;// 昵称
	private String username;// umd5
	private String age;// 年龄
	private String headerurl;// 头像
	private String jiedao;// 在哪街道
	private String lasttime;// 最后登录时间
	private String sex;// 性别
	private String city;// 城市
	private String geo;

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getHeaderurl() {
		return headerurl;
	}

	public void setHeaderurl(String headerurl) {
		this.headerurl = headerurl;
	}

	public String getJiedao() {
		return jiedao;
	}

	public void setJiedao(String jiedao) {
		this.jiedao = jiedao;
	}

	public String getLasttime() {
		return lasttime;
	}

	public void setLasttime(String lasttime) {
		this.lasttime = lasttime;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getGeo() {
		return geo;
	}

	public void setGeo(String geo) {
		this.geo = geo;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
