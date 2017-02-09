package com.coreService.filter;

import com.coreService.entity.HandlerResult;
import com.coreService.entity.OneTalk;
import com.coreService.entity.message.resp.WeixinMessage;

public interface BaseFilter {
	public String replyByWho(WeixinMessage requestMessage, OneTalk talk);

	public HandlerResult process(WeixinMessage requestMessage);
}