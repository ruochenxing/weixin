package com.coreService.filter.impl;

import com.coreService.dao.UserDao;
import com.coreService.entity.HandlerResult;
import com.coreService.entity.OneTalk;
import com.coreService.entity.User;
import com.coreService.entity.message.resp.WeixinMessage;
import com.coreService.filter.BaseFilter;
import com.coreService.util.EmojiUtil;
import com.coreService.util.TalkManager;

public class EmojiFilter implements BaseFilter {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(EmojiFilter.class);
	public static final EmojiFilter ME = new EmojiFilter();

	@Override
	public String replyByWho(WeixinMessage requestMessage, OneTalk talk) {
		return null;
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
		if (EmojiUtil.removeEmoji(content).trim().length() == 0) {
			handlerResult.setType("2");
			handlerResult.setContent(EmojiUtil.reply(content));
			talk.addUserStatement(content, 0, "me");
			TalkManager.addTalk(userId, talk);
		}
		return handlerResult;
	}
}