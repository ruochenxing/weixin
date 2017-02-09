package com.weixin.util;

/**
 * 常量
 */
public class GlobalConstants {
	/**
	 * APPID
	 */
	public static final String APPID = "";
	/**
	 * SECRET
	 */
	public static final String SECRET = " ";
	/**
	 * 获取ACCESS_TOKEN接口
	 */
	public static final String GET_ACCESSTOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	/**
	 * ACCESS_TOKEN有效时间(单位：ms)
	 */
	public static final int EFFECTIVE_TIME = 700000;
	/**
	 * 微信接入token ，用于验证微信接口
	 */
	public static final String TOKEN = "";
	
	/**
	 * 图灵机器人APIKEY
	 * */
	public static final String TULING_APIKEY = "";

	//邮件通知邮箱
	public static final String HOST_EMAIL="1111111111@163.com";
}
