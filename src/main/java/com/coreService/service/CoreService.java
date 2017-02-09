package com.coreService.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.coreService.entity.HandlerResult;
import com.coreService.entity.OneTalk;
import com.coreService.entity.autoReply.MessageResponse;
import com.coreService.entity.message.resp.Article;
import com.coreService.entity.message.resp.WeixinMessage;
import com.coreService.filter.BaseFilter;
import com.coreService.util.FilterManager;
import com.coreService.util.MessageUtil;
import com.coreService.util.TalkManager;
import com.coreService.util.XmlTool;
import com.weixin.robot.TulingApiProcess;

public class CoreService {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CoreService.class);
	private static final String WELCOME_RESP_MESSAGE = "欢迎关注那位先生的公众号！我是萌萌哒小助手！你可以找我聊天哦，我什么都懂一点。";
	private static final String ERROR_RESP_MESSAGE = "哦，抱歉，我这边信号差，不知道你说什么，请重述一遍！";
	private static final String DEFAULT_RESP_MESSAGE = "抱歉我不知道你想说什么？";
	private static final String CANNOT_REPLY_IMAGE = "哦抱歉，我看不懂图片";

	/**
	 * 由程序来处理请求
	 */
	public static HandlerResult userHandler(WeixinMessage requestMessage) {
		HandlerResult handlerResult = null;
		List<BaseFilter> filters = FilterManager.getFilters();
		if (filters != null && filters.size() > 0) {
			for (BaseFilter f : filters) {
				handlerResult = f.process(requestMessage);
				if ("1".equals(handlerResult.getType()) || "2".equals(handlerResult.getType())) {
					return handlerResult;
				}
			}
		}
		return handlerResult;
	}

	/**
	 * 处理微信发来的请求
	 * 
	 * @param request
	 * @return
	 */
	public static String processRequest(HttpServletRequest request) {
		try {
			// 默认返回的文本消息内容
			String respContent = DEFAULT_RESP_MESSAGE;
			WeixinMessage requestMessage = (WeixinMessage) XmlTool.parseXmlToObj(MessageUtil.getXmlFromRequest(request),
					WeixinMessage.class);
			// 发送方帐号（open_id）
			String fromUserName = requestMessage.getFromUserName();
			// 公众帐号
			String toUserName = requestMessage.getToUserName();
			// 消息类型
			String msgType = requestMessage.getMsgType();
			// 消息内容
			String content = requestMessage.getContent();
			if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
				// 先让程序来处理请求
				HandlerResult handlerResult = userHandler(requestMessage);
				// 如果程序处理有结果
				if (handlerResult != null) {
					String type = handlerResult.getType();
					content = handlerResult.getContent();
					// 让图灵机器人处理
					if (type.equals("2")) {
						respContent = handlerResult.getContent();
						OneTalk talk = TalkManager.getOneTalkByUser(fromUserName);
						if (talk == null) {
							talk = new OneTalk(fromUserName);
						}
						talk.addMeStatement(respContent, 0, null);
						return MessageResponse.getTextMessage(fromUserName, toUserName, respContent);
					}
				}
			}
			// 如果程序处理不了，再交由图灵机器人处理
			// 文本消息
			if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
				logger.info("get a text message1 from " + fromUserName);
				if (content != null && content.trim().length() > 0) {
					respContent = TulingApiProcess.getTulingResult(content);// 图灵机器人自动回复
					if (respContent == null || respContent.trim().length() <= 0) {
						logger.debug("tuling reply content is null");
						respContent = ERROR_RESP_MESSAGE;
					}
				}
			}
			// 事件推送 除了订阅事件，其它的都用不上
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
				String eventType = requestMessage.getEvent();// 事件类型
				logger.info("get a event message" + ",eventType is " + eventType);
				if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {// 订阅
					respContent = WELCOME_RESP_MESSAGE;
				} else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {// 取消订阅
				} else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {// 自定义菜单点击事件
					String eventKey = requestMessage.getEventKey();// 事件KEY值，与创建自定义菜单时指定的KEY值对应
					return MenuClickService.getClickResponse(eventKey, fromUserName, toUserName);
				}
			}
			// 开启微信声音识别测试
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VOICE)) {
				logger.info("get a voice ");
				String recvMessage = requestMessage.getRecognition();
				if (recvMessage != null) {
					respContent = TulingApiProcess.getTulingResult(recvMessage);
					if (respContent == null || respContent.trim().length() <= 0) {
						respContent = ERROR_RESP_MESSAGE;
					}
				} else {
					logger.info("Recognition is null");
					respContent = ERROR_RESP_MESSAGE;
				}
			} else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {
				logger.info("get a image message");
				respContent = CANNOT_REPLY_IMAGE;
				// String result= MessageResponse.getNewsMessage(fromUserName ,
				// toUserName ,getArticles());
			} else {
				logger.info("get a unknow message " + msgType);
				respContent = ERROR_RESP_MESSAGE;
			}

			OneTalk talk = TalkManager.getOneTalkByUser(fromUserName);
			if (talk == null) {
				talk = new OneTalk(fromUserName);
			}
			talk.addMeStatement(respContent, 0, null);
			return MessageResponse.getTextMessage(fromUserName, toUserName, respContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<Article> getArticles() {
		Article article = new Article();
		article.setTitle("JVM系列八：类加载器");
		article.setDescription(
				"类的加载指的是将类的.class文件中的二进制数据读入到内存中，将其放在运行时数据区的方法区内，然后在堆区创建一个这个类的java.lang.Class对象，用来封装类在方法区类的对象。也就是说，类加载的最终产品是位于堆区中的Class对象，Class对象封装了类在方法区内的数据结构，并且向Java程序员提供了访问方法区内的数据结构的接口");
		article.setPicUrl("https://static.oschina.net/uploads/img/201507/24205938_tmw5.png");
		article.setUrl("http://my.oschina.net/aptx4869/blog/483557");
		List<Article> list = new ArrayList<Article>();
		list.add(article);
		return list;
	}
}
/*
 * image <xml> <ToUserName><![CDATA[gh_848ea2168552]]></ToUserName>
 * <FromUserName><![CDATA[oXsYZxOPvvCBasJws6Sp68JeuSn8]]></FromUserName>
 * <CreateTime>1438182860</CreateTime> <MsgType><![CDATA[image]]></MsgType>
 * <PicUrl><![CDATA[http://mmbiz.qpic.cn/mmbiz/
 * AFdRX27czriaF2R90oPyfvSm7syTdm3mmGsROW5rMj43M2KAI4IyibjafWyu6JRq7VYEtZIT6lXWVIuh2zyTpUXg
 * /0]]></PicUrl> <MsgId>6176948349574946375</MsgId>
 * <MediaId><![CDATA[6pfw6ga-YWgAihK82ujwtlYrWwF-
 * mIp2PlgvFVpcpoX710i_olVSUVWM8Pp5VILs]]></MediaId> </xml> location <xml>
 * <ToUserName><![CDATA[gh_848ea2168552]]></ToUserName>
 * <FromUserName><![CDATA[oXsYZxOPvvCBasJws6Sp68JeuSn8]]></FromUserName>
 * <CreateTime>1438183119</CreateTime> <MsgType><![CDATA[location]]></MsgType>
 * <Location_X>22.528531</Location_X> <Location_Y>113.929641</Location_Y> 地图缩放大小
 * <Scale>15</Scale> 地理位置信息 <Label><![CDATA[??????????(??????????)]]></Label>
 * <MsgId>6176949461971476101</MsgId> </xml>
 * 
 * voice <xml> <ToUserName><![CDATA[gh_848ea2168552]]></ToUserName>
 * <FromUserName><![CDATA[oXsYZxOPvvCBasJws6Sp68JeuSn8]]></FromUserName>
 * <CreateTime>1438183218</CreateTime> <MsgType><![CDATA[voice]]></MsgType>
 * 语音消息媒体id，可以调用多媒体文件下载接口拉取数据。 <MediaId><![CDATA[
 * 0sCtAPg78kYigYoiW1BLPPNttGImWlvjIR9Eb7cP4JRW3e_JF32MWWEggj2ZUTG9]]></MediaId>
 * <Format><![CDATA[amr]]></Format> <MsgId>6176949887173238424</MsgId>
 * Recognition为语音识别结果，使用UTF8编码。 <Recognition><![CDATA[]]></Recognition> </xml>
 * emoji <xml> <ToUserName><![CDATA[gh_848ea2168552]]></ToUserName>
 * <FromUserName><![CDATA[oXsYZxOPvvCBasJws6Sp68JeuSn8]]></FromUserName>
 * <CreateTime>1438484121</CreateTime> <MsgType><![CDATA[text]]></MsgType>
 * <Content><![CDATA[/::)]]></Content> <MsgId>6178242255717536400</MsgId> </xml>
 */