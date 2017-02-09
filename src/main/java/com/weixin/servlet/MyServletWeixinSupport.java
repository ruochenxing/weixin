package com.weixin.servlet;

import com.github.sd4324530.fastweixin.message.Article;
import com.github.sd4324530.fastweixin.message.BaseMsg;
import com.github.sd4324530.fastweixin.message.NewsMsg;
import com.github.sd4324530.fastweixin.message.TextMsg;
import com.github.sd4324530.fastweixin.message.req.BaseEvent;
import com.github.sd4324530.fastweixin.message.req.BaseReqMsg;
import com.github.sd4324530.fastweixin.message.req.ImageReqMsg;
import com.github.sd4324530.fastweixin.message.req.LinkReqMsg;
import com.github.sd4324530.fastweixin.message.req.LocationReqMsg;
import com.github.sd4324530.fastweixin.message.req.TextReqMsg;
import com.github.sd4324530.fastweixin.message.req.VoiceReqMsg;
import com.github.sd4324530.fastweixin.servlet.WeixinSupport;
import com.weixin.robot.TulingApiProcess;
import com.weixin.util.ArticleTool;
import com.weixin.util.GlobalConstants;
import com.weixin.util.SmtpHelper;

//用户自行实现的微信消息收发处理器
public class MyServletWeixinSupport extends WeixinSupport {
	private static final String WELCOME_RESP_MESSAGE = "欢迎关注网盘搜索公众号！我是网盘搜索机器人，回复'help'查看功能。你也可以找我聊天哦，我什么都懂一点。";
	private static final String ERROR_RESP_MESSAGE = "哦，抱歉，我这边信号差，不知道你说什么，请重述一遍！";
	private static final String CANNOT_REPLY_IMAGE = "哦抱歉，我看不懂图片";

	@Override
	protected String getToken() {
		return GlobalConstants.TOKEN;
	}

	// 使用安全模式时设置：APPID
	// 不再强制重写，有加密需要时自行重写该方法
	@Override
	protected String getAppId() {
		return GlobalConstants.APPID;
	}

	// 使用安全模式时设置：密钥
	// 不再强制重写，有加密需要时自行重写该方法
	// 不加密时不要有返回值
	@Override
	protected String getAESKey() {
		return null;
	}

	@Override
	protected BaseMsg handleTextMsg(TextReqMsg msg) {
		System.out.println("text msg:" + msg);
		String content = msg.getContent();
		BaseMsg result = beforeHandler(msg);
		if (result != null) {
			return result;
		}
		String respContent = TulingApiProcess.getTulingResult(content);// 图灵机器人自动回复
		if (respContent == null || respContent.trim().length() <= 0) {
			respContent = ERROR_RESP_MESSAGE;
		}
		SmtpHelper.sendInExecutor(GlobalConstants.HOST_EMAIL, "公众号新消息通知",
				"msg:" + content + "\t\n reply:" + respContent);
		return new TextMsg(respContent);
	}

	private BaseMsg beforeHandler(TextReqMsg msg) {
		String content = msg.getContent();
		if (content == null || content.trim().length() == 0) {
			return null;
		}
		if (content.startsWith("1024")) {
			NewsMsg newsMsg = new NewsMsg();
			Article a = ArticleTool.getOne(msg.getFromUserName());
			if (a == null) {
				return new TextMsg("请求达到上限，请明天再来~");
			}
			newsMsg.add(a);
			return newsMsg;
		}
		if (content.startsWith("搜索")) {
			String key = content = content.replaceFirst("搜索(\\+|：|:)?", "");
			System.out.println("搜索:" + key);
			return new TextMsg(ArticleTool.replySearch(key));
		}
		return null;
	}

	@Override
	protected BaseMsg handleImageMsg(ImageReqMsg msg) {
		System.out.println("image msg:" + msg);
		return new TextMsg(CANNOT_REPLY_IMAGE);
	}

	@Override
	protected BaseMsg handleVoiceMsg(VoiceReqMsg msg) {
		System.out.println("voice msg:" + msg);
		return super.handleVoiceMsg(msg);
	}

	@Override
	protected BaseMsg handleLocationMsg(LocationReqMsg msg) {
		System.out.println("location msg:" + msg);
		return super.handleLocationMsg(msg);
	}

	@Override
	protected BaseMsg handleLinkMsg(LinkReqMsg msg) {
		System.out.println("link msg:" + msg);
		return super.handleLinkMsg(msg);
	}

	@Override
	protected BaseMsg handleSubscribe(BaseEvent event) {
		System.out.println("subscribe event:");
		SmtpHelper.sendInExecutor(GlobalConstants.HOST_EMAIL, "公众号新增粉丝通知", "fromUserName:" + event.getFromUserName());
		return new TextMsg(WELCOME_RESP_MESSAGE);
	}

	@Override
	protected BaseMsg handleUnsubscribe(BaseEvent event) {
		System.out.println("unsubscribe event:");
		SmtpHelper.sendInExecutor(GlobalConstants.HOST_EMAIL, "公众号掉粉通知", "fromUserName:" + event.getFromUserName());
		return super.handleUnsubscribe(event);
	}

	@Override
	protected BaseMsg handleDefaultMsg(BaseReqMsg msg) {
		System.out.println("response default msg:");
		return new TextMsg(ERROR_RESP_MESSAGE);
	}
}