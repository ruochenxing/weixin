package com.coreService.filter.impl;

import com.coreService.dao.UserDao;
import com.coreService.entity.HandlerResult;
import com.coreService.entity.OneTalk;
import com.coreService.entity.Statement;
import com.coreService.entity.User;
import com.coreService.entity.message.resp.WeixinMessage;
import com.coreService.filter.BaseFilter;
import com.coreService.util.TalkManager;

public class WeatherFilter implements BaseFilter {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(WeatherFilter.class);
	public static final WeatherFilter ME = new WeatherFilter();

	@Override
	public String replyByWho(WeixinMessage requestMessage, OneTalk talk) {
		// 如果这句话跟上一句有关
		if (talk.getCheck() == 1) {
			// 获取用户的最后一句话
			Statement last = talk.getLastUserStatement();
			if ("天气".equals(last.getContent())) {
				return "me";
			}
		}
		// 内容与上一句无关
		else {
			String content = requestMessage.getContent();
			if (content.equals("天气")) {
				return "me";
			}
		}
		return "tuling";
	}

	@Override
	public HandlerResult process(WeixinMessage requestMessage) {
		HandlerResult handlerResult = new HandlerResult();
		String userId = requestMessage.getFromUserName();
		handlerResult.setUserId(userId);
		OneTalk talk = TalkManager.getOneTalkByUser(userId);
		if (talk == null) {
			talk = new OneTalk(userId);
			// 去数据库中查找对应的user
			User user = new UserDao().queryUserById(userId);
			if (user != null) {
				talk.setUser(user);
			} else {
				if (new UserDao().addUser(talk.getUser())) {
					logger.info("save user success:" + userId);
				}
			}
		}
		String content = requestMessage.getContent();
		handlerResult.setUserContent(content);
		String who = replyByWho(requestMessage, talk);
		if ("me".equals(who)) {
			if (talk.getCheck() == 1) {// 如果请求的这句话和上一句有关
				Statement last = talk.getLastUserStatement();
				// 用户的上一句话是天气，那么用户的这一次内容就是城市
				if ("天气".equals(last.getContent())) {
					talk.setCheck(0);
					handlerResult.setType("1");
					handlerResult.setContent(content + "天气");
					who = "tuling";
				}
			} else {
				if (content.equals("天气")) {
					// 由程序处理
					if (talk.getUser().getAddress() == null || talk.getUser().getAddress().trim().length() == 0) {
						handlerResult.setType("2");
						handlerResult.setContent("请问你要查询哪个城市的天气？");
						talk.setCheck(1);// 下次的回答和上一句有关系
						who = "me";
					} else {
						// 交给图灵处理
						handlerResult.setType("1");
						handlerResult.setContent(talk.getUser().getAddress() + "天气");
						talk.setCheck(0);
						who = "tuling";
					}
				}
			}
		}
		talk.addUserStatement(content, 0, who);
		TalkManager.addTalk(userId, talk);
		return handlerResult;
	}
}