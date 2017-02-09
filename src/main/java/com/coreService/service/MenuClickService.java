package com.coreService.service;

/**
 * 描述：菜单点击事件，处理
 */
public class MenuClickService {
	/**
	 * 描述：
	 * @param eventKey
	 * @param fromUserName
	 * @param toUserName
	 * @return 接受用户点击事件，通过微信推送给用户消息，跳转页面，发送消息等
	 */
	public static String getClickResponse(String eventKey, String fromUserName, String toUserName) {
		// TODO 判断evetKey事件处理
		if (eventKey.equals("test")) {

		}
		return null;
	}

}
