package com.coreService.util;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.coreService.dao.UserDao;
import com.coreService.entity.OneTalk;
public class TalkManager{
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TalkManager.class);
	private static final ConcurrentHashMap<String,OneTalk> talks=new ConcurrentHashMap<String,OneTalk>();
	private static final long nd = 1000*24*60*60;//一天的毫秒数
	private static final long nh = 1000*60*60;//一小时的毫秒数
	private TalkManager(){}

	public static void init(){
		logger.info("init");
	}
	//获取所有的对话
	public static Map<String,OneTalk> getAllTalk(){
		return talks;
	}
	//添加一个对话
	public static void addTalk(String userId,OneTalk talk){
		if(talks.putIfAbsent(userId,talk)==null){
			logger.info("add a new talk with "+userId);
		}
	}
	//清除过期的对话 定时管理
	public static void recycleTalk(){
		logger.info("recycle talk");
		Date now=new Date();
		for(Map.Entry<String,OneTalk> e: talks.entrySet() ){
			OneTalk talk=e.getValue();
			if((now.getTime()-talk.getCreateDate().getTime())%nd/nh>=24){
				UserDao userDao=new UserDao();
				if(userDao.queryUserById(talk.getUser().getUserId())!=null){
					userDao.updateUser(talk.getUser());
				}
				else{
					userDao.addUser(talk.getUser());
				}
				logger.info("remove talk with "+e.getKey());
				talks.remove(e.getKey());
			}
		}	
	}
	//根据userId查找对话
	public static OneTalk getOneTalkByUser(String userId){
		return talks.get(userId);
	}
	//删除对话
	public static OneTalk removeTalkByUser(String userId){
		return talks.remove(userId);
	}
	//保存对话到数据库中
}